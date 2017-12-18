package com.therm.thermicscontrol;

import java.io.Serializable;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;


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
	public long id_system = 1;
	public long row_id = 0;
	
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
		res = String.format("n_rele = %d start_hour = %d start_minute = %d enable = %b id_system = %d stop_minute = %d", n_rele, start_hour, start_minute, enable, id_system, stop_minute);
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
	int multpParameter2 = 333333;
	public int getStartIdIntent(int nDay)
	{
		return (2*n_rele)*multpParameter + start_hour*60 + start_minute + nDay * 24 *60 + multpParameter2*((int)id_system-1);	
	}
	
	public int getStopIdIntent(int nDay)
	{
		return (2*n_rele+1)*multpParameter + start_hour*60 + start_minute + nDay * 24 *60 + multpParameter2*((int)id_system-1);	
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
			//Toast.makeText(context,"Add alarm 1", Toast.LENGTH_LONG).show();
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
	
				//to pass : TimerValue
                putToIntent(intent);
				intent.putExtra(TimerActionService.ATTRIBUTE_COMMAND, isStart);
				intent.putExtra(TimerActionService.ATTRIBUTE_IDSYSTEM, id_system);
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
					//Toast.makeText(context,"Add alarm 20", Toast.LENGTH_LONG).show();
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
				putToIntent(intent);
				intent.putExtra(TimerActionService.ATTRIBUTE_COMMAND, isStart);
				intent.putExtra(TimerActionService.ATTRIBUTE_IDSYSTEM, id_system);
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
					//Toast.makeText(context,"Add alarm 21", Toast.LENGTH_LONG).show();
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

    public void putToIntent(Intent intent) {
        intent.putExtra("TimerValue.n_rele", n_rele);
        intent.putExtra("TimerValue.enable", enable);
        intent.putExtra("TimerValue.start_hour", start_hour);
        intent.putExtra("TimerValue.start_minute", start_minute);
        intent.putExtra("TimerValue.stop_hour", stop_hour);
        intent.putExtra("TimerValue.stop_minute", stop_minute);
        for(int i = 0; i < 7; i++) {
            intent.putExtra("isDay" + i, isDay[i]);
        }
        intent.putExtra("TimerValue.id_system", id_system);
        intent.putExtra("TimerValue.row_id", row_id);
    }

    public static TimerValue getFromIntent(Intent intent) {
        TimerValue res = new TimerValue();
        res.n_rele = intent.getIntExtra("TimerValue.n_rele", res.n_rele);
        res.enable = intent.getBooleanExtra("TimerValue.enable", res.enable);
        res.start_hour = intent.getIntExtra("TimerValue.start_hour", res.start_hour);
        res.start_minute = intent.getIntExtra("TimerValue.start_minute", res.start_minute);
        res.stop_hour = intent.getIntExtra("TimerValue.stop_hour", res.stop_hour);
        res.stop_minute = intent.getIntExtra("TimerValue.stop_minute", res.stop_minute);
        for(int i = 0; i < 7; i++) {
            res.isDay[i] = intent.getBooleanExtra("isDay" + i, res.isDay[i]);
        }
        res.id_system = intent.getLongExtra("TimerValue.id_system", res.id_system);
        res.row_id = intent.getLongExtra("TimerValue.row_id", res.row_id);

        return res;
    }
}
