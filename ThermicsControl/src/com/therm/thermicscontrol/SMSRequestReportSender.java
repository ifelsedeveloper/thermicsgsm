package com.therm.thermicscontrol;


import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.Intent;
import android.widget.Toast;
//import android.util.Log;

public class SMSRequestReportSender extends BroadcastReceiver {
	
	
	static AtomicLong lastSendTime = new AtomicLong(0);
	static boolean flag_not_check = true;
	final static String ATTRIBUTE_TIME = "attribute_time";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		try{
			Toast.makeText(ctx, "���������� ������ �� ����������", Toast.LENGTH_LONG).show();
			if(intent !=null && ctx !=null)
			{
				long n_row = intent.getLongExtra(AddRequestActivity.ATTRIBUTE_ROWID, -1);
				long day_of_week = intent.getLongExtra(AddRequestActivity.ATTRIBUTE_DAYOFWEEK, -1);
				//Toast.makeText(ctx, "���������� �� ����������: n_row = " + Long.toString(n_row) + "n_day = " + Long.toString(day_of_week), Toast.LENGTH_LONG).show();
				
				if(n_row>=0 && day_of_week>=0)
				{
					if(doesDatabaseExist(ctx,DBRequest.DATABASE_PATH + DBRequest.DB_NAME))
					{
						//check database
						// ��������� ����������� � ��
						DBRequest dbRequest = new DBRequest(ctx);
						dbRequest.open();
						//Toast.makeText(ctx, "���������", Toast.LENGTH_LONG).show();
						if(dbRequest.check(n_row,day_of_week) || flag_not_check)
						{
							//send request
							//Toast.makeText(ctx, "���������", Toast.LENGTH_LONG).show();
							SharedPreferences pref = ctx.getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS);
							CSettingsPref settings = new CSettingsPref(pref);
							CSettingsDev settingsDev = new CSettingsDev(settings,ctx);
							
							//long sendingTime = intent.getLongExtra(ATTRIBUTE_TIME, System.currentTimeMillis()/1000);
							long currentTime = System.currentTimeMillis()/1000;
							
							if(currentTime - lastSendTime.get() > 60)
							{
								//settingsDev.RequestReport();
								//Toast.makeText(ctx, "���������� �� ���������� :" + settings.getPinSIM(), Toast.LENGTH_LONG).show();
								settingsDev.RequestReport();
								//Toast.makeText(ctx, "������ ���������", Toast.LENGTH_LONG).show();
								lastSendTime.set(currentTime);
							}
							flag_not_check = false;
						}
						else
						{
							Toast.makeText(ctx, "��� ������ � ��", Toast.LENGTH_LONG).show();
							AlarmManager amSendRequest = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
							//cancel intent
							Intent lintent = new Intent(ctx, SMSRequestReportSender.class);
							lintent.setAction(Long.toString(n_row*7 + day_of_week +1));				
							PendingIntent lpIntent = PendingIntent.getBroadcast(ctx, 0, lintent, 0);
							amSendRequest.cancel(lpIntent);
						}
						
					}
					else
					{
						Toast.makeText(ctx, "��� ��", Toast.LENGTH_LONG).show();
						//cancel intent
						AlarmManager amSendRequest = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
						//cancel intent
						Intent lintent = new Intent(ctx, SMSRequestReportSender.class);
						lintent.setAction(Long.toString(n_row*7 + day_of_week +1));				
						PendingIntent lpIntent = PendingIntent.getBroadcast(ctx, 0, lintent, 0);
						amSendRequest.cancel(lpIntent);
					}
				}
				
			}
			
		}
		catch(Exception e)
		{
			Toast.makeText(ctx, "������ ��� ��������: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	private static boolean doesDatabaseExist(Context context, String dbName) {
	    File dbFile=context.getDatabasePath(dbName);
	    return dbFile.exists();
	}
	
}
