package com.therm.thermicscontrol;

import java.util.ArrayList;
import java.util.List;

import com.therm.thermicscontrol.R;

import android.os.Bundle;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimerActivity extends BaseActivity {

	final Context context = this;
	
	public SystemConfig settings=null;
	
	TimerValue timerValue =  new TimerValue();
	
	Spinner functionTimer;
	
	TextView textViewStartTimeValue;
	TextView textViewStopTimeValue;
	
	static Dialog dialogSetDay;
	CheckBox [] checkBoxsDays = new CheckBox [7];
	TextView titleValueWeek;
	
	DBTimerDateTimeAction databaseTimers = null;
	
	int modify_id = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_timer);
		//create custom dialog set day
		dialogSetDay = new Dialog(context);
		dialogSetDay.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSetDay.setContentView(R.layout.custom_day_of_week_peeker);
		settings=SystemConfigDataSource.getActiveSystem();
		databaseTimers = new DBTimerDateTimeAction(this, DBTimerDateTimeAction.DB_DEFAULT_TABLE);
		databaseTimers.open();
		modify_id = getIntent().getIntExtra(DBTimerDateTimeAction.COLUMN_ID, -1);
		if(modify_id > 0)
		{
			Cursor cursor = databaseTimers.getRec(modify_id);
			if(cursor != null && cursor.moveToFirst())
			{
				timerValue = databaseTimers.getTimerValueFromRec(cursor);
			}
			
			if(timerValue == null) timerValue = new TimerValue();
		}

		checkBoxsDays[0] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayMon);
		checkBoxsDays[1] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayTue);
		checkBoxsDays[2] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayWen);
		checkBoxsDays[3] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayThu);
		checkBoxsDays[4] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayFri);
		checkBoxsDays[5] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySat);
		checkBoxsDays[6] = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySun);
		
		textViewStartTimeValue = (TextView) findViewById(R.id.textViewStartTimeValue);
		textViewStopTimeValue = (TextView) findViewById(R.id.textViewStopTimeValue);
		titleValueWeek = (TextView) findViewById(R.id.titleValueWeek);
		
		for(int i=0; i < 7; i++)
		{
			isDays[i] = timerValue.isDay[i];
		}
		
		RelativeLayout layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutMon);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(0);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutTue);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(1);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutWen);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(2);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutThu);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(3);
        		return;
            }
         });
		
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutFri);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(4);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutSat);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(5);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutSun);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
        		switchCheckBox(6);
        		return;
            }
         });
		
		//ok and cancel
		Button dialogButton = (Button) dialogSetDay.findViewById(R.id.dialogButtonOkWeek);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		for(int i=0; i<7; i++)
        		{
        			timerValue.isDay[i] = isDays[i];
        		}
        		titleValueWeek.setText(timerValue.getDays());
        		dialogSetDay.dismiss();
            }
         });
		
		dialogButton = (Button) dialogSetDay.findViewById(R.id.dialogButtonCancelWeek);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		setValueDaysOfWeek();
        		dialogSetDay.dismiss();
            }
         });
		
		addItemsOnSpinnerFunctionTimer();
		LoadFrom(timerValue);
	}

	boolean [] isDays =  new boolean [7];
	private void switchCheckBox(int i)
	{
		//perform action
		isDays[i] = ! isDays[i];
		checkBoxsDays[i].setChecked(isDays[i]);
	}
	
	private void switchCheckBox(int i, boolean value)
	{
		//perform action
		isDays[i] = value;
		checkBoxsDays[i].setChecked(value);
	}
	
	private void setValueDaysOfWeek()
	{
		for(int i=0; i<7; i++)
		{
			switchCheckBox(i, timerValue.isDay[i]);
		}
	}
	
	private void LoadFrom(TimerValue timerValue)
	{
		functionTimer.setSelection(timerValue.n_rele);
		textViewStartTimeValue.setText(timerValue.getStartStringTime());
		textViewStopTimeValue.setText(timerValue.getStopStringTime());
		titleValueWeek.setText(timerValue.getDays());
		setValueDaysOfWeek();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timer, menu);
		return true;
	}
	
	
	// add items into spinner dynamically
	public void addItemsOnSpinnerFunctionTimer() {
	 //spinner1.getSelectedItem()
		functionTimer = (Spinner) findViewById(R.id.valueSpinnerFunctionTimer);
		List<String> list = new ArrayList<String>();
		list.add("реле №1 - "+settings.getFunctionRele1());
		list.add("реле №2 - "+settings.getFunctionRele2());
		list.add("реле №3 - "+settings.getFunctionRele3());
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		functionTimer.setAdapter(dataAdapter);
	}
	
	public void onClickEnableTimer(View v)
	{
		timerValue.enable = !timerValue.enable;
	}
	
	OnTimeSetListener myCallBackTimerStart = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		   timerValue.start_hour = hourOfDay;
		   timerValue.start_minute = minute; 
		   textViewStartTimeValue.setText(timerValue.getStartStringTime());
	}};
	
	public void onClickConfigTimeStart(View v)
	{
		TimePickerDialog tpd = new TimePickerDialog(this, myCallBackTimerStart, timerValue.start_hour, timerValue.start_minute, true);
		tpd.show();
	}
	
	OnTimeSetListener myCallBackTimerStop = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		   timerValue.stop_hour = hourOfDay;
		   timerValue.stop_minute = minute; 
		   textViewStopTimeValue.setText(timerValue.getStopStringTime());
	}};
	
	
	public void onClickConfigTimeEnd(View v)
	{
		TimePickerDialog tpd = new TimePickerDialog(this, myCallBackTimerStop, timerValue.stop_hour, timerValue.stop_minute, true);
		tpd.show();
	}
	
	public void onClickConfigWeek(View v)
	{
		dialogSetDay.show();
	}
	
	public void OnClickOkButton(View v)
	{
		//add new timer item to database and AlarmManager
		timerValue.n_rele = (int)functionTimer.getSelectedItemId();
		if(modify_id > 0)
		{
			Cursor cursor = databaseTimers.getRec(modify_id);
			if(cursor != null && cursor.moveToFirst())
			{
				TimerValue timerValueOld = databaseTimers.getTimerValueFromRec(cursor);
				timerValueOld.CancelAllTimer(this);
			}
			databaseTimers.updateRec(timerValue, modify_id);
		}
		else
		{
			settings.setIsEnableTimer(true);
			timerValue.id_system = settings.getId();
			databaseTimers.addRec(timerValue);
		}
		
		//
		databaseTimers.close();
		timerValue.AddStartTimer(this);
		timerValue.AddStopTimer(this);
		onBackPressed();
	}
	
	public void OnClickCancelButton(View v)
	{
		onBackPressed();
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    databaseTimers.close();
	  }
	
}
