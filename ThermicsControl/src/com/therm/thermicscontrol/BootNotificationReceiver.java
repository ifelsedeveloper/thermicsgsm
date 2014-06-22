package com.therm.thermicscontrol;


import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;
//import android.util.Log;

public class BootNotificationReceiver extends BroadcastReceiver {
	

	@Override
	public void onReceive(Context context, Intent intent) {
		StartNotificationService(context, intent);
		StartAllTimers(context,intent);
	}
	private void StartAllTimers(Context context, Intent intent)
	{
		DBTimerDateTimeAction database = new DBTimerDateTimeAction(context,DBTimerDateTimeAction.DB_DEFAULT_TABLE);
		database.open();
		Cursor cursor = database.getAllData();

		
		while(cursor!=null && cursor.moveToNext()){
			TimerValue value = database.getTimerValueFromRec(cursor);
			value.AddStartTimer(context);
			value.AddStopTimer(context);
		}
		database.close();
	}
	
	private void StartNotificationService(Context context, Intent intent)
	{
		try{
			//load notifications to manager
			Toast.makeText(context, "Start notifications", Toast.LENGTH_LONG).show();
			DBRequest dbRequest = new DBRequest(context);
			dbRequest.open();
			Cursor cursor = dbRequest.getAllData();
			long [] idInntents = new long[7];
			
			boolean enable;
			
			while(cursor!=null && cursor.moveToNext()){
				
				idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_MON));
				idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_TUE));
				idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_WEN));
				idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_THU));
				idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_FRI));
				idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SAT));
				idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SUN));
				
				enable = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_ENABLE)) == 1 ;
				
				long id = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_ID));
					
				AlarmManager amSendRequest = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				
					if(!enable)
						for(int i=0;i<7;i++)
						{
							if(idInntents[i]>0){
								Intent intentLocal = new Intent(context, SMSRequestReportSender.class);
								intent.setAction(Long.toString(idInntents[i]));
								PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentLocal, 0);
								amSendRequest.cancel(pIntent);
							}
						}
						else
						{
							//enable alarms recreate it
						 	int myHour = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_HOUR));
							int myMinute = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_MINUTES));
							int idSystem = cursor.getInt(cursor.getColumnIndex(DBRequest.COLUMN_ID_SYSTEM));
							
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
							
							Intent intentLocal = new Intent(context, SMSRequestReportSender.class);
							intentLocal.putExtra(AddRequestActivity.ATTRIBUTE_ROWID, (long)id);
							intentLocal.putExtra(AddRequestActivity.ATTRIBUTE_IDSYSTEM, (long)idSystem);
							
							for(int i=(day_of_week + 5)%7;i<7;i++)
							{
								intentLocal.putExtra(AddRequestActivity.ATTRIBUTE_DAYOFWEEK, (long)i);
								int add_time = k*24*3600;

								if(idInntents[i] != 0)
								{
									intentLocal.setAction(Long.toString(idInntents[i]));
									PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentLocal, PendingIntent.FLAG_CANCEL_CURRENT);
									
									amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
									amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
								}

								k++;
							}
							
							for(int i=0;i<(day_of_week+5)%7;i++)
							{
								intentLocal.putExtra(AddRequestActivity.ATTRIBUTE_DAYOFWEEK, (long)i);
								int add_time = k*24*3600;

								if(idInntents[i] != 0)
								{
									intentLocal.setAction(Long.toString(idInntents[i]));
									PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intentLocal, PendingIntent.FLAG_CANCEL_CURRENT);
									
									amSendRequest.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
									amSendRequest.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
								}

								k++;
							}
							
							////////
						}
					
			}
			
			/////
		}
		catch(Exception e)
		{
			Toast.makeText(context, "Error in sms monitor", Toast.LENGTH_LONG).show();
		}
	}
}
