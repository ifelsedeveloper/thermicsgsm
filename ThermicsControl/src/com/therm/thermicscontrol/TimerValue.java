package com.therm.thermicscontrol;

import java.io.Serializable;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


public class TimerValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int n_rele = 0;
	public boolean enable = true;
	public int start_hour = 0;
	public int start_minute = 0;
	public int stop_hour = 0;
	public int stop_minute = 0;
	public boolean [] isDay = new boolean[7];
	String [] days = {"пн","вт","ср","чт","пт","сб","вс"};
	
	public TimerValue()
	{
		for(int i = 0; i < 7; i++)
			isDay[i] = true;

		Calendar c = Calendar.getInstance(); 
		start_hour = c.get(Calendar.HOUR_OF_DAY);
		start_minute = c.get(Calendar.MINUTE);
		stop_hour = start_hour;
		stop_minute = start_minute;
	}
	
	public String toString()
	{
		String res = "";
		res = String.format("n_rele = %d start_hour = %d start_minute = %d", n_rele, start_hour,start_minute);
		return res;
	}
	String getDays()
	{
		String result = "Каждый день";
		
		boolean flag = true;
		for(int i=0;i<7;i++)
			flag = flag & isDay[i];

		if(!flag)
		{
			result = "";
			int k = 0;
			for(int i=0;i<7;i++)
				if(isDay[i])
				{
					if(k>0)
						result+=", " + days[i];
					else
						result+=days[i];
					k++;
				}
		}
		
		flag = false;
		for(int i=0;i<7;i++)
			flag = flag | isDay[i];
		
		if(!flag)
			return "Никогда" +
					"";
		
		return result;
	}
	
	public String getStartStringTime()
	{
		return getStringHourMinute(start_hour,start_minute);
	}
	
	public String getStopStringTime()
	{
		return getStringHourMinute(stop_hour,stop_minute);
	}
	
	String getStringHourMinute(int hour, int minute)
	{
		String str_hour = Integer.toString(hour);
		   if(str_hour.length()==1)
			   str_hour= "0"+str_hour;
		   
		String str_minute = Integer.toString(minute);
		   if(str_minute.length()==1)
			   str_minute= "0"+str_minute;
		   
		return str_hour + ":" + str_minute; 
	}
	
	int multpParameter = 5000000;
	public int getStartIdIntent(int nDay)
	{
		return (2*n_rele)*multpParameter + start_hour*60 + start_minute + nDay * 24 *60;	
	}
	
	public int getStopIdIntent(int nDay)
	{
		return (2*n_rele+1)*multpParameter + start_hour*60 + start_minute + nDay * 24 *60;	
	}
	
	public void AddStartTimer(Context ctx)
	{
		AddToAlarmMangerTimer(ctx,start_hour,start_minute, true);
		
	}
	
	public void AddStopTimer(Context ctx)
	{
		AddToAlarmMangerTimer(ctx,stop_hour,stop_minute, false);
	}
	
	private void AddToAlarmMangerTimer(Context context,int vhour, int vminute, boolean isStart)
	{
		if(enable)
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
			
			AlarmManager schedulerManger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			int [] idIntents = new int[7];
	
				
			
			
			for(int i=(day_of_week + 5)%7;i<7;i++)
			{
				if(isStart)
					idIntents[i] = getStartIdIntent(i);
				else
					idIntents[i] = getStopIdIntent(i);
				
				Intent intent = new Intent(context, TimerActionService.class);
	
				//to pass :
				intent.putExtra("TimerValue", this);  
				intent.putExtra(TimerActionService.ATTRIBUTE_COMMAND, isStart);
				int add_time = k*24*3600;
			    //intent.putExtra("extra", extra)
				if(idIntents[i] != 0  && !isDay[i])
				{
					intent.setAction(Long.toString(idIntents[i]));
					PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
					schedulerManger.cancel(pIntent);
					idIntents[i] = 0;
				}
				if(idIntents[i] != 0  && isDay[i])
				{
					intent.setAction(Long.toString(idIntents[i]));
					PendingIntent pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
					
					schedulerManger.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
					schedulerManger.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
				}
				k++;
			}
			
			for(int i=0;i<(day_of_week+5)%7;i++)
			{
				if(isStart)
					idIntents[i] = getStartIdIntent(i);
				else
					idIntents[i] = getStopIdIntent(i);
				
				Intent intent = new Intent(context, TimerActionService.class);
				intent.putExtra("TimerValue", this); 
				intent.putExtra(TimerActionService.ATTRIBUTE_COMMAND, isStart);
				int add_time = k*24*3600;
			    //intent.putExtra("extra", extra)
				if(idIntents[i] != 0  && isDay[i] == false)
				{
					intent.setAction(Long.toString(idIntents[i]));
					PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
					schedulerManger.cancel(pIntent);
					idIntents[i] = 0;
				}
				if(idIntents[i] != 0  && isDay[i] == true)
				{
					intent.setAction(Long.toString(idIntents[i]));
					PendingIntent pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
					
					schedulerManger.set(AlarmManager.RTC_WAKEUP, (timeToSend + 4)*1000, pIntent);
					schedulerManger.setRepeating(AlarmManager.RTC_WAKEUP,(timeToSend + 2 + add_time)*1000, repeat_time, pIntent);
				}
				k++;
			}
		}
		else
			CancelAllTimer(context);
	}
	
	public void CancelAllTimer(Context context)
	{
		Calendar calendar = Calendar.getInstance();
		AlarmManager schedulerManger = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
		for(int i=(day_of_week + 5)%7;i<7;i++)
		{
			Intent intent = new Intent(context, TimerActionService.class);
			intent.setAction(Long.toString(getStartIdIntent(i)));
			PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
			schedulerManger.cancel(pIntent);
		}
		for(int i=0;i<(day_of_week+5)%7;i++)
		{
			Intent intent = new Intent(context, TimerActionService.class);
			intent.setAction(Long.toString(getStartIdIntent(i)));
			PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
			schedulerManger.cancel(pIntent);
		}
		for(int i=(day_of_week + 5)%7;i<7;i++)
		{
			Intent intent = new Intent(context, TimerActionService.class);
			intent.setAction(Long.toString(getStopIdIntent(i)));
			PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
			schedulerManger.cancel(pIntent);
		}
		for(int i=0;i<(day_of_week+5)%7;i++)
		{
			Intent intent = new Intent(context, TimerActionService.class);
			intent.setAction(Long.toString(getStopIdIntent(i)));
			PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
			schedulerManger.cancel(pIntent);
		}
	}
}
