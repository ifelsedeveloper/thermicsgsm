package com.therm.thermicscontrol;



import java.util.Calendar;

import com.therm.thermicscontrol.R;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class CursorAdapterCheckBox extends SimpleCursorAdapter implements
		OnClickListener, OnLongClickListener  {

	private Context context;

	private DBRequest dbHelper;
	private Cursor currentCursor;
	static Dialog dialog;
	static Integer id_ = 0;
	
	public CursorAdapterCheckBox(Context context, int layout, Cursor c,
			String[] from, int[] to, DBRequest dbHelper) {
		super(context, layout, c, from, to);
		this.currentCursor = c;
		this.context = context;
		this.dbHelper = dbHelper;
		
		dialog = new Dialog(context);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog_change_delete);
		dialog.setTitle("Выберите действие");
		
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
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.row_request, null);
		}

		this.currentCursor.moveToPosition(pos);

		CheckBox cBox = (CheckBox) v.findViewById(R.id.checkBoxRequestRow);

		cBox.setTag(Integer.parseInt(this.currentCursor
				.getString(this.currentCursor
						.getColumnIndex(DBRequest.COLUMN_ID))));

		if (this.currentCursor.getString(this.currentCursor
				.getColumnIndex(DBRequest.COLUMN_ENABLE)) != null
				&& Integer.parseInt(this.currentCursor
						.getString(this.currentCursor
								.getColumnIndex(DBRequest.COLUMN_ENABLE))) != 0) {
			cBox.setChecked(true);
		} else {
			cBox.setChecked(false);
		}
		cBox.setOnClickListener(this);

		TextView txtTitle = (TextView) v.findViewById(R.id.titleTimeHourRow);
		String hour = this.currentCursor.getString(this.currentCursor
				.getColumnIndex(DBRequest.COLUMN_HOUR));
		if(hour.length()==1)
			hour = "0"+hour;
		
		txtTitle.setText(hour);
		
		txtTitle = (TextView) v.findViewById(R.id.titleTimeMinutesRow);
		
		String minutes = this.currentCursor.getString(this.currentCursor
				.getColumnIndex(DBRequest.COLUMN_MINUTES));
		if(minutes.length()==1)
			minutes = "0"+minutes;
		
		txtTitle.setText(minutes);
		
		txtTitle = (TextView) v.findViewById(R.id.titleDayRow);
		txtTitle.setText(this.currentCursor.getString(this.currentCursor
				.getColumnIndex(DBRequest.COLUMN_REPEAT)));
		
		LinearLayout view = (LinearLayout)  v.findViewById(R.id.layoutTimeDayRow);
		
		view.setTag(Integer.parseInt(this.currentCursor
				.getString(this.currentCursor
						.getColumnIndex(DBRequest.COLUMN_ID))));
		
		view.setOnLongClickListener(this);
		
		return (v);
	}

	public void ClearSelections() {
		//this.dbHelper.clearSelections();
		this.currentCursor.requery();

	}

	@Override
	public void onClick(View v) {

		CheckBox cBox = (CheckBox) v;
		Integer _idLocal = (Integer) cBox.getTag();

		ContentValues values = new ContentValues();
		values.put(DBRequest.COLUMN_ENABLE, cBox.isChecked() ? 1 : 0);
		if(cBox.isChecked())
		{
			
		}
		
		this.dbHelper.mDB.update(DBRequest.DB_TABLE, values, "_id=?",
				new String[] { Integer.toString(_idLocal) });
		
		enableAlarms(_idLocal,cBox.isChecked());
		//Toast.makeText(context, _idLocal.toString(), Toast.LENGTH_LONG).show();
	}
	
	@Override
    public boolean onLongClick(View v){
		
		((RequestSystemActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
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
		this.dbHelper.mDB.delete(DBRequest.DB_TABLE, DBRequest.COLUMN_ID + " = " + id_, null);
		//Toast.makeText(context, id_.toString(), Toast.LENGTH_LONG).show();
		this.currentCursor.requery();
		((RequestSystemActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public void enableAlarms(Integer id,boolean enable)
	{
		AlarmManager amSendRequest = (AlarmManager) ((RequestSystemActivity) context).getSystemService(Context.ALARM_SERVICE);
		Cursor cursor = dbHelper.getRec(id);

		if (cursor != null && cursor.moveToFirst()) {

			Long [] idInntents = new Long [7];
			idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_MON));
			idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_TUE));
			idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_WEN));
			idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_THU));
			idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_FRI));
			idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SAT));
			idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SUN));
			if(!enable)
			for(int i=0;i<7;i++)
			{
				if(idInntents[i]>0){
					Intent intent = new Intent(((RequestSystemActivity) context), SMSRequestReportSender.class);
					intent.setAction(Long.toString(idInntents[i]));
					PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
					amSendRequest.cancel(pIntent);
				}
			}
			else
			{
				//enable alarms recreate it
			 	int myHour = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_HOUR));
				int myMinute = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_MINUTES));
				
				Calendar calendar = Calendar.getInstance();

				long calTime = calendar.getTimeInMillis()/1000L;
				
				int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);


				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				
				long timeToSend;
				timeToSend = calTime - minute*60 - hour * 3600 + myHour *3600 + myMinute * 60;
				if((myHour*60 + myMinute)<(hour*60+minute))
				{
					timeToSend=timeToSend + 24 * 3600;
					day_of_week++;
				}

				int repeat_time = 7*24*3600*1000;
				int k = 0;
				for(int i=(day_of_week + 5)%7;i<7;i++)
				{
					Intent intent = new Intent(((RequestSystemActivity) context), SMSRequestReportSender.class);
					intent.putExtra(AddRequestActivity.ATTRIBUTE_ROWID, (long)id);
					intent.putExtra(AddRequestActivity.ATTRIBUTE_DAYOFWEEK, (long)i);
					int add_time = k*24*3600;
				    //intent.putExtra("extra", extra)

					if(idInntents[i] != 0)
					{
						intent.setAction(Long.toString(idInntents[i]));
						PendingIntent pIntent = PendingIntent.getBroadcast(((RequestSystemActivity) context), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
						
						amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
						amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
					}

					k++;
				}
				
				for(int i=0;i<(day_of_week+5)%7;i++)
				{
					Intent intent = new Intent(((RequestSystemActivity) context), SMSRequestReportSender.class);
					intent.putExtra(AddRequestActivity.ATTRIBUTE_ROWID, (long)id);
					intent.putExtra(AddRequestActivity.ATTRIBUTE_DAYOFWEEK, (long)i);
					int add_time = k*24*3600;
				    //intent.putExtra("extra", extra)

					if(idInntents[i] != 0)
					{
						intent.setAction(Long.toString(idInntents[i]));
						PendingIntent pIntent = PendingIntent.getBroadcast(((RequestSystemActivity) context), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
						
						amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
						amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
					}

					k++;
				}
				
				////////
			}
		}
	}
	
	public void changeRequest()
	{
		Intent intent = new Intent(context, AddRequestActivity.class);
		intent.putExtra(AddRequestActivity.ATTRIBUTE_ROWID, id_.toString());
		((RequestSystemActivity) context).startActivity(intent);
		this.currentCursor.requery();
		((RequestSystemActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}
	
	public void cancelRequest()
	{
		((RequestSystemActivity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

}