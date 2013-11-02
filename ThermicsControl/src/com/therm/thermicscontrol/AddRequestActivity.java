package com.therm.thermicscontrol;

import java.util.Calendar;
import java.util.Date;

import com.therm.thermicscontrol.R;

import android.os.Bundle;
import android.os.SystemClock;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlarmManager;

import android.app.Dialog;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;


public class AddRequestActivity extends BaseActivity {

	public static final String TAG_events="event_tag_add_request";
	public CSettingsPref settings=null;
	CheckBox checkBoxEnableRequest_;
	AlarmManager amSendRequest;
	
	public static final String ATTRIBUTE_ROWID = "attribute_rowid";
	public static final String ATTRIBUTE_DAYOFWEEK = "attribute_dayofweek";
	
	
	final Context context = this;
	
	int DIALOG_TIME = 1;
	int myHour = 14;
	int myMinute = 35;
	TextView tvTime;
	TextView tvDays;
	DBRequest dbRequest;
	
	String value_days;
	
	static long currentRowNumber = 1;
	
	boolean [] days_of_week = {true,true,true,true,true,true,true}; 
	long [] idInntents = new long [7];
	static Dialog dialogSetDay;
	CheckBox check_mon;
	CheckBox check_tue;
	CheckBox check_wen;
	CheckBox check_thu;
	CheckBox check_fri;
	CheckBox check_sat;
	CheckBox check_sun;
	
	boolean modifay = false;
	Integer id_ = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//amSendRequest.cancel(operation)
		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_add_new_request);
		
		amSendRequest = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		// открываем подключение к БД
		dbRequest = new DBRequest(this);
		dbRequest.open();
		
		
		
		try{
		checkBoxEnableRequest_ = (CheckBox) findViewById(R.id.checkBoxEnableRequest);
		
		//create for work with shared preference
		settings=new CSettingsPref(getSharedPreferences(MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS));
		
		
		//initialize
		
		for(int i=0;i<7;i++)
			idInntents[i] = 0;
		
		//create custom dialog set time
		tvTime = (TextView) findViewById(R.id.titleValueTime);
		tvDays = (TextView) findViewById(R.id.titleValueWeek);
		//
		
		
		
		//create custom dialog set day
		dialogSetDay = new Dialog(context);
		dialogSetDay.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSetDay.setContentView(R.layout.custom_day_of_week_peeker);
		
		
		//
		check_mon = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayMon);
		check_tue = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayTue);
		check_wen = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayWen);
		check_thu = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayThu);
		check_fri = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayFri);
		check_sat = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySat);
		check_sun = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySun);
		
		Intent myIntent = getIntent(); // this is just for example purpose
		if(myIntent.hasExtra(ATTRIBUTE_ROWID))
		{

			modifay = true;
			id_ = Integer.parseInt(myIntent.getStringExtra(ATTRIBUTE_ROWID));
			//Toast.makeText(getApplicationContext(), "my id = "+ Integer.toString(id_), Toast.LENGTH_LONG).show();
			Cursor cursor = dbRequest.getRec(id_);
			
			if (cursor != null && cursor.moveToFirst()) {
				myHour = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_HOUR));
				myMinute = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_MINUTES));
				
				idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_MON));
				idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_TUE));
				idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_WEN));
				idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_THU));
				idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_FRI));
				idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SAT));
				idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SUN));
				for(int i=0;i<7;i++)
					days_of_week[i] = idInntents[i]  > 0;
				
				
				checkBoxEnableRequest_.setChecked(cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_ENABLE)) > 0);
				
				currentRowNumber = id_;
			}
		}
		else
		{
			Calendar c = Calendar.getInstance(); 
			myMinute = c.get(Calendar.MINUTE);
			myHour = c.get(Calendar.HOUR_OF_DAY);
			currentRowNumber = dbRequest.lastRowNumber()+1;
			modifay = false;
		}
		setTimeStr();
		
		setValueDaysOfWeek();
		tvDays.setText(getValueDaysOfWeek());
		RelativeLayout layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutMon);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[0] = !days_of_week[0];
        		check_mon.setChecked(days_of_week[0]);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutTue);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[1] = !days_of_week[1];
        		check_tue.setChecked(days_of_week[1]);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutWen);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[2] = !days_of_week[2];
        		check_wen.setChecked(days_of_week[2]);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutThu);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[3] = !days_of_week[3];
        		check_thu.setChecked(days_of_week[3]);
        		return;
            }
         });
		
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutFri);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[4] = !days_of_week[4];
        		check_fri.setChecked(days_of_week[4]);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutSat);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[5] = !days_of_week[5];
        		check_sat.setChecked(days_of_week[5]);
        		return;
            }
         });
		
		layout = (RelativeLayout) dialogSetDay.findViewById(R.id.relativeLayoutSun);
		layout.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		days_of_week[6] = !days_of_week[6];
        		check_sun.setChecked(days_of_week[6]);
        		return;
            }
         });
		
		//ok and cancel
		Button dialogButton = (Button) dialogSetDay.findViewById(R.id.dialogButtonOkWeek);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		value_days = getValueDaysOfWeek();
        		tvDays.setText(value_days);
        		dialogSetDay.dismiss();
            }
         });
		
		dialogButton = (Button) dialogSetDay.findViewById(R.id.dialogButtonCancelWeek);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		for(int i=0;i<7;i++)
        			days_of_week[i] = idInntents[i]  > 0;
        		setValueDaysOfWeek();
        		dialogSetDay.dismiss();
            }
         });
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void setTimeStr()
	{
			String str_hour = Integer.toString(myHour);
		   if(str_hour.length()==1)
			   str_hour= "0"+str_hour;
		   
		   String str_minute = Integer.toString(myMinute);
		   if(str_minute.length()==1)
			   str_minute= "0"+str_minute;
		      
		   tvTime.setText(str_hour + ":" + str_minute);
	}
	
	boolean enable_request = true;
	public void onClickEnableRequest(View v)
	{
		enable_request=!enable_request;
		checkBoxEnableRequest_.setChecked(enable_request);
	}

	OnTimeSetListener myCallBackTime = new OnTimeSetListener() {
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	   myHour = hourOfDay;
	   myMinute = minute; 
	   setTimeStr();
	}};
	
	public void onClickConfigTime(View v)
	{
		TimePickerDialog tpd = new TimePickerDialog(this, myCallBackTime, myHour, myMinute, true);
		tpd.show();
	}
	
	String getValueDaysOfWeek()
	{
		String result = "Каждый день";
		
		CheckBox check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayMon);
		days_of_week [0] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayTue);
		days_of_week [1] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayWen);
		days_of_week [2] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayThu);
		days_of_week [3] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayFri);
		days_of_week [4] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySat);
		days_of_week [5] = check_day.isChecked();
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySun);
		days_of_week [6] = check_day.isChecked();
		
		boolean flag = true;
		for(int i=0;i<7;i++)
			flag = flag & days_of_week[i];
		
		String [] days = {"пн","вт","ср","чт","пт","сб","вс"};
		
		if(!flag)
		{
			result = "";
			int k = 0;
			for(int i=0;i<7;i++)
			{
				if(days_of_week[i])
				{
					if(k>0)
						result+=", " + days[i];
					else
						result+=days[i];
					k++;
				}
			}
		}
		
		
		flag = false;
		for(int i=0;i<7;i++)
			flag = flag | days_of_week[i];
		
		if(!flag)
			return "Никогда";
		
		return result;
	}
	
	void setValueDaysOfWeek()
	{
		CheckBox check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayMon);
		check_day.setChecked(days_of_week[0]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayTue);
		check_day.setChecked(days_of_week[1]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayWen);
		check_day.setChecked(days_of_week[2]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayThu);
		check_day.setChecked(days_of_week[3]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDayFri);
		check_day.setChecked(days_of_week[4]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySat);
		check_day.setChecked(days_of_week[5]);
		
		check_day = (CheckBox) dialogSetDay.findViewById(R.id.checkBoxDaySun);
		check_day.setChecked(days_of_week[6]);
	}
	
	public void onClickConfigWeek(View v)
	{
		dialogSetDay.show();
	}
	
	long getIdIntent(int n_day)
	{
		return currentRowNumber * 7 + n_day + 1;
	}
	
//	long getDateWeek(int n_day)
//	{
//		Calendar c = Calendar.getInstance(); 
//		int year = c.get(Calendar.YEAR);
//		int day = c.get(Calendar.)+n_day;
//		if(day>)
//		int minutes = myMinute;
//		int hours = myHour;
//		
//		
//	}
	int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {

	    Calendar c = Calendar.getInstance();
//	    c.set(Calendar.YEAR, year);
//	    c.set(Calendar.MONTH, month);
//	    c.set(Calendar.DAY_OF_MONTH, day);
	    c.set(Calendar.HOUR_OF_DAY, hour);
	    c.set(Calendar.MINUTE, minute);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);

	    return (int) (c.getTimeInMillis() / 1000L);
	}
	
	public void OnClickButtonOkAddRequestK(View v)
	{
		//save added request
		int enable =  enable_request? 1 : 0;
		
		String str_repeat = getValueDaysOfWeek();
				
		
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
		if(modifay)
			currentRowNumber = id_;
			
		
		for(int i=0;i<7;i++)
		if(days_of_week[i])
			idInntents[i] = getIdIntent(i);
		
		if(modifay)
			dbRequest.updateRec(id_,enable, myHour, myMinute, str_repeat, idInntents[0], idInntents[1], idInntents[2], idInntents[3], idInntents[4], idInntents[5], idInntents[6]);
		else
			dbRequest.addRec(enable, myHour, myMinute, str_repeat, idInntents[0], idInntents[1], idInntents[2], idInntents[3], idInntents[4], idInntents[5], idInntents[6]);
			
		if(!modifay)
		{
			currentRowNumber = dbRequest.lastRowNumber();
			for(int i=0;i<7;i++)
				if(days_of_week[i])
					idInntents[i] = getIdIntent(i);
			dbRequest.updateRec((int)currentRowNumber,enable, myHour, myMinute, str_repeat, idInntents[0], idInntents[1], idInntents[2], idInntents[3], idInntents[4], idInntents[5], idInntents[6]);
		}
		
		
		//Toast.makeText(this, "number = " + Long.toString(currentRowNumber), Toast.LENGTH_LONG).show();
		int repeat_time = 7*24*3600*1000;
		int k = 0;
		for(int i=(day_of_week + 5)%7;i<7;i++)
		{
			Intent intent = new Intent(this, SMSRequestReportSender.class);
			intent.putExtra(ATTRIBUTE_ROWID, (long)currentRowNumber);
			intent.putExtra(ATTRIBUTE_DAYOFWEEK, (long)i);
			int add_time = k*24*3600;
		    //intent.putExtra("extra", extra)
			if(idInntents[i] != 0  && days_of_week[i] == false)
			{
				intent.setAction(Long.toString(idInntents[i]));
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
				amSendRequest.cancel(pIntent);
				idInntents[i] = 0;
			}
			if(idInntents[i] != 0  && days_of_week[i] == true && enable_request)
			{
				intent.setAction(Long.toString(idInntents[i]));
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
				amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
			}
			k++;
		}
		
		for(int i=0;i<(day_of_week+5)%7;i++)
		{
			Intent intent = new Intent(this, SMSRequestReportSender.class);
			intent.putExtra(ATTRIBUTE_ROWID, (long)currentRowNumber);
			intent.putExtra(ATTRIBUTE_DAYOFWEEK, (long)i);
			int add_time = k*24*3600;
		    //intent.putExtra("extra", extra)
			if(idInntents[i] != 0  && days_of_week[i] == false)
			{
				intent.setAction(Long.toString(idInntents[i]));
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
				amSendRequest.cancel(pIntent);
				idInntents[i] = 0;
			}
			if(idInntents[i] != 0  && days_of_week[i] == true && enable_request)
			{
				intent.setAction(Long.toString(idInntents[i]));
				PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
				amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
			}
			k++;
		}
		
		
		
		if((timeToSend - calTime)>0 && (timeToSend - calTime)<100)
			SMSRequestReportSender.flag_not_check = true;
		
		onBackPressed();
		
//		Intent intent = new Intent(this, null);
//	    intent.setAction(action);
//	    intent.putExtra("extra", extra);
	}
	
	public void OnClickButtonCancelAddRequest(View v)
	{
		onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}


	protected void onDestroy() {
	    super.onDestroy();
	    dbRequest.close();
	  }
	
}
