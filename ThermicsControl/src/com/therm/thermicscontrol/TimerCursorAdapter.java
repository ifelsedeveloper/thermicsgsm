package com.therm.thermicscontrol;

import java.util.HashMap;
import java.util.Map;

import com.therm.thermicscontrol.R;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimerCursorAdapter extends SimpleCursorAdapter implements
		OnClickListener, OnLongClickListener  {

	private Context context;
	CSettingsPref settings;
	private DBTimerDateTimeAction dbTimer;
	private Cursor currentCursor;
	static Dialog dialog;
	static Integer id_ = 0;
	
	public TimerCursorAdapter(Context context, CSettingsPref settings,int layout, Cursor c, DBTimerDateTimeAction dbTimer, String[] from, int [] to) {
		super(context, layout, c, from, to);
		this.currentCursor = c;
		this.context = context;
		this.dbTimer = dbTimer;
		
		dialog = new Dialog(context);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog_change_delete);
		dialog.setTitle("Выберите действие");
		this.settings = settings;
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonChange);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		changeRequest();
        		dialog.dismiss();
            }
         });
		
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		deleteRequest();
        		dialog.dismiss();
            }
         });
		
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		cancelRequest();
        		dialog.dismiss();
            }
         });
	}

	public View getView(int pos, View inView, ViewGroup parent) {
		View v = inView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) 
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_timer, null);
		}

		this.currentCursor.moveToPosition(pos);

		TimerValue value = this.dbTimer.getTimerValueFromRec(this.currentCursor);
		
		CheckBox cBox = (CheckBox) v.findViewById(R.id.checkBoxEnableTimer);

		cBox.setTag(Integer.parseInt(this.currentCursor
				.getString(this.currentCursor
						.getColumnIndex(DBTimerDateTimeAction.COLUMN_ID))));
		
		cBox.setChecked(value.enable);
		cBox.setOnClickListener(this);
		TextView titleTimerDestenation = (TextView) v.findViewById(R.id.titleTimerDestenation);
		titleTimerDestenation.setText("Реле №"+Integer.toString(value.n_rele + 1) + " - " + settings.getFunctionRele(value.n_rele));
		
		TextView valueTimeStart = (TextView) v.findViewById(R.id.valueTimeStart);
		valueTimeStart.setText(value.getStartStringTime());
		
		TextView valueTimeStop = (TextView) v.findViewById(R.id.valueTimeStop);
		valueTimeStop.setText(value.getStopStringTime());
		
		TextView valueTimeRepeatDay = (TextView) v.findViewById(R.id.valueTimeRepeatDay);
		valueTimeRepeatDay.setText(value.getDays());
		
		LinearLayout view = (LinearLayout)  v.findViewById(R.id.layoutTimer);
		
		String _ID = this.currentCursor
				.getString(this.currentCursor
						.getColumnIndex(DBTimerDateTimeAction.COLUMN_ID));
		view.setTag(Integer.parseInt(_ID));
		
		view.setOnLongClickListener(this);

		mapTimerValues.put(_ID, value);

		return (v);
	}

	public void ClearSelections() {
		//this.dbHelper.clearSelections();
		this.currentCursor.requery();

	}

	Map <String,TimerValue> mapTimerValues =  new HashMap<String,TimerValue>();
	@Override
	public void onClick(View v) {

		CheckBox cBox = (CheckBox) v;
		Integer _idLocal = (Integer) cBox.getTag();

		ContentValues values = new ContentValues();
		values.put(DBTimerDateTimeAction.COLUMN_ENABLE, cBox.isChecked() ? 1 : 0);
		TimerValue value = mapTimerValues.get(_idLocal.toString());
		
		
		
//		boolean enableTimer = false;
//		for (TimerValue iterable_value : mapTimerValues.values()) {
//			enableTimer = enableTimer || iterable_value.enable;
//		}
//		settings.setIsEnableTimer(enableTimer);
//		Toast.makeText(context, "check box " +Integer.toString(mapTimerValues.values().size()), Toast.LENGTH_LONG).show();
		
		this.dbTimer.mDB.update(this.dbTimer.DB_TABLE, values, "_id=?",
				new String[] { Integer.toString(_idLocal) });
		
		value.enable = cBox.isChecked();
		value.CancelAllTimer(context);
		if(cBox.isChecked())
		{	
			value.AddStartTimer(context);
			value.AddStopTimer(context);
		}
		
		enableAlarms(_idLocal,cBox.isChecked());
		this.currentCursor.requery();
		notifyDataSetChanged();
		//Toast.makeText(context, _idLocal.toString(), Toast.LENGTH_LONG).show();
	}
	
	@Override
    public boolean onLongClick(View v){
		
		((TimersListActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		LinearLayout view = (LinearLayout)  v;
		Integer _idLocal = (Integer) view.getTag();
		id_ = _idLocal;
		dialog.show();
		//Toast.makeText(context, _idLocal.toString(), Toast.LENGTH_LONG).show();
		return true;
		
	}
	
	public void deleteRequest()
	{
		enableAlarms(id_,false);
		Cursor cursor = this.dbTimer.getRec(id_);
		if(cursor!=null && cursor.moveToFirst())
		{
			TimerValue value = this.dbTimer.getTimerValueFromRec(cursor);
			value.CancelAllTimer(context);
		}
		this.dbTimer.mDB.delete(this.dbTimer.DB_TABLE, DBTimerDateTimeAction.COLUMN_ID + " = " + id_, null);
		//Toast.makeText(context, id_.toString(), Toast.LENGTH_LONG).show();
		this.currentCursor.requery();
		((TimersListActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public void enableAlarms(Integer id,boolean enable)
	{
		AlarmManager amSendRequest = (AlarmManager) ((TimersListActivity) context).getSystemService(Context.ALARM_SERVICE);
		
	}
	
	public void changeRequest()
	{
		Intent intent = new Intent(context, TimerActivity.class);
		intent.putExtra(DBTimerDateTimeAction.COLUMN_ID, id_);
		((TimersListActivity) context).startActivity(intent);
		this.currentCursor.requery();
		((TimersListActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public void cancelRequest()
	{
		((TimersListActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

}