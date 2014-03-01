package com.therm.thermicscontrol;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;



public class TimerActionService extends Service {
	public static final String ATTRIBUTE_ROWID = "attribute_rowid";
	public static final String ATTRIBUTE_DAYOFWEEK = "attribute_dayofweek";
	public static final String ATTRIBUTE_COMMAND = "attribute_command";
	public static final String ATTRIBUTE_DATBASENAME = "attribute_databasename";
	public static final String ATTRIBUTE_NRELE = "attribute_n_rele";
	public static final String ATTRIBUTE_VKL = "attribute_vkl";
	public static final String ATTRIBUTE_ACTION = "attribute_timer_action";
	CSettingsDev settingsDev;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		try{
			
			if(intent !=null)
			{
				//Toast.makeText(this, "Запуск таймера ", Toast.LENGTH_LONG).show();
				SharedPreferences pref = this.getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS);
				CSettingsPref settings = new CSettingsPref(pref);
				settingsDev = new CSettingsDev(settings,this);
				
					TimerValue value = (TimerValue) intent.getSerializableExtra("TimerValue");
					if(value != null && value.enable)
					{
						//Toast.makeText(this,"TimerValue = " + value.toString(), Toast.LENGTH_LONG).show();
						boolean vkl = intent.getBooleanExtra(ATTRIBUTE_COMMAND, false); 
						String cmd = settingsDev.GetCmdRele(value.n_rele+1, vkl);
						//Toast.makeText(this, "Отправляем команду = " + cmd + " :" + DBTimerDateTimeAction.DB_DEFAULT_TABLE, Toast.LENGTH_LONG).show();

						if(DBTimerDateTimeAction.doesDatabaseExist(this, DBTimerDateTimeAction.DATABASE_PATH + DBTimerDateTimeAction.DB_NAME))
						{
							DBTimerDateTimeAction dbAction = new DBTimerDateTimeAction(this,DBTimerDateTimeAction.DB_DEFAULT_TABLE);
							dbAction.open();
							//Toast.makeText(this, "Проверка", Toast.LENGTH_LONG).show();
							
							if(dbAction.check(value))
							{
								Intent intentTimer = new Intent(ATTRIBUTE_ACTION);
								intentTimer.putExtra(ATTRIBUTE_NRELE, value.n_rele);
								intentTimer.putExtra(ATTRIBUTE_VKL, vkl);
						    	sendBroadcast(intentTimer);
								settings.setIsTimerRunning(value.n_rele,vkl);
								WorkAction(cmd);
							}
							else
								CancelAlarmActivity(intent);
						}
					}
				}
				else
					CancelAlarmActivity(intent);
			
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Ошибка при отправке: " + e.toString(), Toast.LENGTH_LONG).show();
		}
		
		return START_STICKY_COMPATIBILITY;
		
	}
	
	private void WorkAction(String cmd)
	{
		Toast.makeText(this, "Отправляем команду = " + cmd, Toast.LENGTH_LONG).show();
		settingsDev.sendSMS(cmd);
	}
	
	private void CancelAlarmActivity(Intent intent)
	{
		
		AlarmManager amSendRequest = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getService(this,0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		amSendRequest.cancel(pendingIntent);
	}
	
}