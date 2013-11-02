package com.therm.thermicscontrol;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

public class DateTimeActionManager {
	DBTimerDateTimeAction _database;
	Context _context;
	
	
	
	public DateTimeActionManager(String database_name, Context ctx)
	{
		_context = ctx;
		_database = new DBTimerDateTimeAction(_context, database_name);
	}
	
	int modify_id = -1;
	public void StartAction(int vyear,int vmonth,int vday,int vhour,int vminute, int enable, boolean [] days_of_week,int modify_id)
	{
		
		_database.open();
		currentRowNumber = _database.lastRowNumber()+1;
//		if(modifay)
//			currentRowNumber = id_;
		
//		String str_repeat = "";
//		
//		long [] idIntents = new long[7];
//		
//		for(int i=0;i<7;i++)
//		if(days_of_week[i])
//			idIntents[i] = getIdIntent(i);
//		
//		if(modify_id > -1)
//			_database.updateRec(modify_id,enable, vhour, vminute, str_repeat, idIntents[0], idIntents[1], idIntents[2], idIntents[3], idIntents[4], idIntents[5], idIntents[6]);
//		else
//			_database.addRec(enable, vhour, vminute, str_repeat, idIntents[0], idIntents[1], idIntents[2], idIntents[3], idIntents[4], idIntents[5], idIntents[6]);
//			
//		if(modify_id == -1)
//		{
//			currentRowNumber = _database.lastRowNumber();
//			for(int i=0;i<7;i++)
//				if(days_of_week[i])
//					idIntents[i] = getIdIntent(i);
//			
//			_database.updateRec((int)currentRowNumber,enable, vhour, vminute, str_repeat, idIntents[0], idIntents[1], idIntents[2], idIntents[3], idIntents[4], idIntents[5], idIntents[6]);
//		}
		Intent intent = new Intent( _context, TimerActionService.class);
		intent.putExtra(TimerActionService.ATTRIBUTE_ROWID, (long)0);
		intent.putExtra(TimerActionService.ATTRIBUTE_DAYOFWEEK, (long)1);
		
		AlarmManager schedulerManger = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		intent.setAction(Long.toString(1055));
		PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		Calendar calendar = Calendar.getInstance();
		schedulerManger.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+5000, pIntent);
		//schedulerManger.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
		
		//SetValuesToActivityManger(idIntents, vyear, vmonth, vday, vhour, vminute,days_of_week);
		_database.close();
	}
	
	public void DeleteAction(int modify_id)
	{
		
	}
	
	public void StartAction(int vyear,int vmonth,int vday,int vhour,int vminute, int enable, boolean [] days_of_week)
	{
		StartAction(vyear, vmonth, vday, vhour, vminute, enable, days_of_week, modify_id);
	}
	
	private void SetValuesToActivityManger(long[] idIntents,int vyear,int vmonth,int vday,int vhour,int vminute, boolean [] days_of_week)
	{
		Calendar calendar = Calendar.getInstance();
		long calTime = calendar.getTimeInMillis()/1000L;
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		long timeToSend;
		timeToSend = calTime - minute*60 - hour * 3600 + vhour *3600 + vminute * 60;
		if((vhour*60 + vminute)<(hour*60+minute))
		{
			timeToSend=timeToSend + 24 * 3600;
			day_of_week++;
		}
		
		//Toast.makeText(this, "number = " + Long.toString(currentRowNumber), Toast.LENGTH_LONG).show();
		int repeat_time = 7*24*3600*1000;
		int k = 0;
		
		AlarmManager schedulerManger = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		
		for(int i=(day_of_week + 5)%7;i<7;i++)
		{
			Intent intent = new Intent(_context, TimerActionService.class);
			intent.putExtra(TimerActionService.ATTRIBUTE_ROWID, (long)currentRowNumber);
			intent.putExtra(TimerActionService.ATTRIBUTE_DAYOFWEEK, (long)i);
			int add_time = k*24*3600;
		    //intent.putExtra("extra", extra)
			if(idIntents[i] != 0  && !days_of_week[i])
			{
				intent.setAction(Long.toString(idIntents[i]));
				PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, 0);
				schedulerManger.cancel(pIntent);
				idIntents[i] = 0;
			}
			if(idIntents[i] != 0  && days_of_week[i])
			{
				intent.setAction(Long.toString(idIntents[i]));
				PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				schedulerManger.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
				schedulerManger.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
			}
			k++;
		}
		
		for(int i=0;i<(day_of_week+5)%7;i++)
		{
			Intent intent = new Intent(_context, TimerActionService.class);
			intent.putExtra(TimerActionService.ATTRIBUTE_ROWID, (long)currentRowNumber);
			intent.putExtra(TimerActionService.ATTRIBUTE_DAYOFWEEK, (long)i);
			int add_time = k*24*3600;
		    //intent.putExtra("extra", extra)
			if(idIntents[i] != 0  && days_of_week[i] == false)
			{
				intent.setAction(Long.toString(idIntents[i]));
				PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, 0);
				schedulerManger.cancel(pIntent);
				idIntents[i] = 0;
			}
			if(idIntents[i] != 0  && days_of_week[i] == true)
			{
				intent.setAction(Long.toString(idIntents[i]));
				PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				schedulerManger.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
				schedulerManger.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
			}
			k++;
		}
	}
	
	public void enableAlarms(Integer id,boolean enable)
	{
		AlarmManager amSendRequest = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
		Cursor cursor = _database.getRec(id);

		if (cursor != null && cursor.moveToFirst()) {

			Long [] idInntents = new Long [7];
			idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_MON));
			idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_TUE));
			idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_WEN));
			idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_THU));
			idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_FRI));
			idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_SAT));
			idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_SUN));
			if(!enable)
			for(int i=0;i<7;i++)
			{
				if(idInntents[i]>0){
					Intent intent = new Intent(_context, TimerActionService.class);
					intent.setAction(Long.toString(idInntents[i]));
					PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, 0);
					amSendRequest.cancel(pIntent);
				}
			}
			else
			{
//				//enable alarms recreate it
//			 	int myHour = cursor.getInt(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_HOUR));
//				int myMinute = cursor.getInt(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_MINUTES));
//				
//				Calendar calendar = Calendar.getInstance();
//
//				long calTime = calendar.getTimeInMillis()/1000L;
//				
//				int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
//
//
//				int hour = calendar.get(Calendar.HOUR_OF_DAY);
//				int minute = calendar.get(Calendar.MINUTE);
//				
//				long timeToSend;
//				timeToSend = calTime - minute*60 - hour * 3600 + myHour *3600 + myMinute * 60;
//				if((myHour*60 + myMinute)<(hour*60+minute))
//				{
//					timeToSend=timeToSend + 24 * 3600;
//					day_of_week++;
//				}
//
//				int repeat_time = 7*24*3600*1000;
//				int k = 0;
//				for(int i=(day_of_week + 5)%7;i<7;i++)
//				{
//					Intent intent = new Intent(_context, TimerActionService.class);
//					intent.putExtra(TimerActionService.ATTRIBUTE_ROWID, (long)id);
//					intent.putExtra(TimerActionService.ATTRIBUTE_DAYOFWEEK, (long)i);
//					int add_time = k*24*3600;
//
//					if(idInntents[i] != 0)
//					{
//						intent.setAction(Long.toString(idInntents[i]));
//						PendingIntent pIntent = PendingIntent.getService(_context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//						
//						amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
//						amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
//					}
//
//					k++;
//				}
//				
//				for(int i=0;i<(day_of_week+5)%7;i++)
//				{
//					Intent intent = new Intent( _context, TimerActionService.class);
//					intent.putExtra(TimerActionService.ATTRIBUTE_ROWID, (long)id);
//					intent.putExtra(TimerActionService.ATTRIBUTE_DAYOFWEEK, (long)i);
//					int add_time = k*24*3600;
//					
//					if(idInntents[i] != 0)
//					{
//						intent.setAction(Long.toString(idInntents[i]));
//						PendingIntent pIntent = PendingIntent.getService( _context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//						
//						amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
//						amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
//					}
//
//					k++;
//				}
				
				////////
			}
		}
	}
	
	long currentRowNumber = 0;
	long getIdIntent(int n_day)
	{
		return currentRowNumber * 7 + n_day + 1;
	}
}
