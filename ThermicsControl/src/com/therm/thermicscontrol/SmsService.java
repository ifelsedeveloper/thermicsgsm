package com.therm.thermicscontrol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.therm.thermicscontrol.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;


import android.content.SharedPreferences;

import android.content.Intent;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;


import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;


public class SmsService extends Service {
	
	NotificationManager nm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String sms_body = intent.getExtras().getString("sms_body");
		String sms_from = intent.getExtras().getString("incoming_number");
		if(sms_from!=null)
		{
			CSettingsPref settings=new CSettingsPref(this.getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, BaseActivity.MODE_MULTI_PROCESS));
			if(SMSMonitor.checkSMSKsytal(sms_from,settings))
			{
				Log.i("database",sms_body);
				nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				saveSms(sms_body,sms_from);
			}

		}
		else
		{
			Log.i("database",sms_body);
			nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			saveSms(sms_body,sms_from);
		}
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	        @Override
	        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            Log.e("Alert","Lets See if it Works SmsService !!!");
	            Toast.makeText(getApplicationContext(), "Error SmsService", Toast.LENGTH_LONG).show();
	        }
	    });
		
		return START_STICKY_COMPATIBILITY;
	}
	
	private void saveSms(String sms_body,String sms_from) {
		try{
		//
			SharedPreferences pref = getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS);
			CSettingsPref settings = new CSettingsPref(pref);
			//parse sms
			//Toast.makeText(getApplicationContext(), sms_body,Toast.LENGTH_LONG).show();
			boolean isReportSystem = IsReportSystem(sms_body);
		    parseSMS(sms_body,settings);
		    
		    //Toast.makeText(getApplicationContext(), Integer.toString(res3.length), Toast.LENGTH_LONG).show();
		    //database
			Date now = new Date();
			long now_long = now.getTime();
			Log.i("recived_sms",sms_body);
			//substitute sms title
			String title="systema KSYTAL";
			int ntitle=sms_body.indexOf(title);
			if(ntitle>-1)
			{
				String new_title=settings.getTextSMSTitle();
				String new_sms=new_title+sms_body.substring(ntitle+title.length());
				sms_body = new String(new_sms);
			}
			//insert text alarm
			String strTrevoga="Trevoga!";
			int nTrevoga=-1;
			nTrevoga = sms_body.indexOf(strTrevoga);
			if(nTrevoga>-1)
			{
				String first = sms_body.substring( 0,nTrevoga+strTrevoga.length());
				String second = sms_body.substring( nTrevoga+strTrevoga.length() );
				sms_body=first+" "+settings.getTextSMSAlarm()+" "+second;
				if(sms_body.contains("Trevoga"))
				sms_body=sms_body.replace("Trevoga", "Тревога");
				if(sms_body.contains("Narushena zona"))
				sms_body=sms_body.replace("Narushena zona", "Нарушена зона");
			}
			String strControlSystemSnjat="Control systemy snjat.Po SMS ot";
			if(sms_body.contains(strControlSystemSnjat))
				sms_body=sms_body.replace(strControlSystemSnjat, "Контроль системы снят по SMS от");
			
			String strControlSystem="Systema na controle.Po SMS ot";
			if(sms_body.contains(strControlSystem))
				sms_body=sms_body.replace(strControlSystem, "Система на контроле по SMS от");
			
			String strControlSnjat="Control snjat";
			if(sms_body.contains(strControlSnjat))
				sms_body=sms_body.replace(strControlSnjat, "Контроль снят");
			
			String strNaprjagenieNorma="Naprjagenie norma";
			if(sms_body.contains(strNaprjagenieNorma))
				sms_body=sms_body.replace(strNaprjagenieNorma, "Напряжение норма");
			
			for(int i=0;i<6;i++)
			{
				String strTN=String.format("T%d=", i+1);
				if(sms_body.contains(strTN))
				{
					String TNTitle = settings.getFunctionTmpSensor(i);
					sms_body=sms_body.replace(strTN, String.format("%d. "+ TNTitle+ " = ", i+1));
				}
			}
			Intent intent = new Intent(MessageSystemActivity.BROADCAST_ACTION_RCVSMS);
			String strRELE="RELE";
			if(sms_body.contains(strRELE))
			{
				int n = sms_body.indexOf("RELE=");
				String [] res3 = sms_body.split("\n");
				int n_rele_str = -1;
				for(int i = 0;i < res3.length;i++)
				{
					if(res3[i].contains("RELE="))
					{
						n_rele_str = i;
						break;
					}
				}
				String zone_value = res3[n_rele_str+1];
				String rele_value = res3[n_rele_str];
				String active_zone ="\n"+ "Зоны контроля: "+zone_value;
				sms_body=sms_body.replace(zone_value,active_zone);
				String releTitle = sms_body.substring(n, n+9);
				String reles_value1 = "1. " + settings.getFunctionRele1() +" = "+ (settings.getIsRele1() ? "вкл" : "откл");
				String reles_value2 = "2. " + settings.getFunctionRele2() +" = "+ (settings.getIsRele2() ? "вкл" : "откл");
				String reles_value3 = "3. " + settings.getFunctionRele3() +" = "+ (settings.getIsRele3() ? "вкл" : "откл");
				boolean upr_flag = false;
				if(rele_value.length()>8 && (rele_value.charAt(8) == '0' || rele_value.charAt(8) == '1'))
					upr_flag = true;
				else
					upr_flag = false;
				if(upr_flag)
				{
					String upr_value = "4. " + settings.getFunctionUpr() +" = "+ (settings.getIsUpr() ? "вкл" : "откл");
					sms_body=sms_body.replace(releTitle, "Состояния реле:\n"+reles_value1+'\n'+reles_value2+'\n'+ reles_value3+'\n'+upr_value );
				}
				else
				{
					sms_body=sms_body.replace(releTitle, "Состояния реле:\n"+reles_value1+'\n'+reles_value2+'\n'+ reles_value3 );
				}
			}
			
			String strTEMPR1="Temp.R1";
			if(sms_body.contains(strTEMPR1))
				sms_body=sms_body.replace(strTEMPR1,"Заданная температура для реле №1");
			
			String strTEMPR2="Temp.R2";
			if(sms_body.contains(strTEMPR2))
				sms_body=sms_body.replace(strTEMPR2,"Заданная температура для реле №2");
			
			String strTEMPR3="Temp.R3";
			if(sms_body.contains(strTEMPR3))
				sms_body=sms_body.replace(strTEMPR3,"Заданная температура для реле №3");
			
			String strTEMPR="Temp.R";
			if(sms_body.contains(strTEMPR))
				sms_body=sms_body.replace(strTEMPR,"Заданная температура для реле №2");
			
			String strVkluchenoRele="Vklucheno rele N1";
			if(sms_body.contains(strVkluchenoRele))
			{
				sms_body=sms_body.replace(strVkluchenoRele,settings.getFunctionRele1() + " вкл");
				settings.setIsRele1(true);
				intent.putExtra(BaseActivity.prefIsRele1, true);
			}
			
			strVkluchenoRele="Vklucheno rele N2";
			if(sms_body.contains(strVkluchenoRele))
			{
				sms_body=sms_body.replace(strVkluchenoRele,settings.getFunctionRele2() + " вкл");
				settings.setIsRele2(true);
				intent.putExtra(BaseActivity.prefIsRele2, true);
			}
	
			strVkluchenoRele="Vklucheno rele N3";
			if(sms_body.contains(strVkluchenoRele))
			{
				sms_body=sms_body.replace(strVkluchenoRele,settings.getFunctionRele3() + " вкл");
				settings.setIsRele3(true);
				intent.putExtra(BaseActivity.prefIsRele3, true);
			}
	
			
			String strOtkluchenoRele="Otklucheno rele N1";
			if(sms_body.contains(strOtkluchenoRele))
			{
				sms_body=sms_body.replace(strOtkluchenoRele,settings.getFunctionRele1() + " откл");
				settings.setIsRele1(false);
				intent.putExtra(BaseActivity.prefIsRele1, false);
			}
			
			strOtkluchenoRele="Otklucheno rele N2";
			if(sms_body.contains(strOtkluchenoRele))
			{
				sms_body=sms_body.replace(strOtkluchenoRele,settings.getFunctionRele2() + " откл");
				settings.setIsRele2(false);
				intent.putExtra(BaseActivity.prefIsRele2, false);
			}
			
			strOtkluchenoRele="Otklucheno rele N3";
			if(sms_body.contains(strOtkluchenoRele))
			{
				sms_body=sms_body.replace(strOtkluchenoRele,settings.getFunctionRele3() + " откл");
				settings.setIsRele3(false);
				intent.putExtra(BaseActivity.prefIsRele3, false);
			}
			
			String strVyhodUpravlenieVkluchen="Vyhod Upravlenie vkluchen";
			if(sms_body.contains(strVyhodUpravlenieVkluchen))
			{
				sms_body=sms_body.replace(strVyhodUpravlenieVkluchen,settings.getFunctionUpr() + " вкл");
				settings.setIsUpr(true);
			}
			
			String strVyhodUpravlenieOtkluchen="Vyhod Upravlenie otkluchen";
			if(sms_body.contains(strVyhodUpravlenieOtkluchen))
			{
				sms_body=sms_body.replace(strVyhodUpravlenieOtkluchen,settings.getFunctionUpr()  + " откл");
				settings.setIsUpr(false);
			}
			
			
			
			String strSystemaNaControle="Systema na controle";
			if(sms_body.contains(strSystemaNaControle))
				sms_body=sms_body.replace(strSystemaNaControle, "Система на контроле");
			
			String strKluch="Kluch";
			if(sms_body.contains(strKluch))
				sms_body=sms_body.replace(strKluch, "Ключ");
			
			
			String TempL = "Temp.L";
			if(sms_body.contains(TempL))
			{
				String res_str = "Нижняя граница оповещения";
				int pos = sms_body.indexOf(TempL);
				String number = sms_body.substring(pos+TempL.length(),pos+TempL.length()+1);
				if(Character.isDigit(number.charAt(0)))
				{
					int num = Integer.parseInt(number);
					res_str+= "("+settings.getFunctionTmpSensor(num-1) + ") = ";
					sms_body=sms_body.replace(TempL+number+"=", res_str);
				}
				else
					sms_body=sms_body.replace(TempL, res_str);
			}
			
			String TempH = "Temp.H";
			if(sms_body.contains(TempH))
			{
				String res_str = "Верхняя граница оповещения";
				int pos = sms_body.indexOf(TempH);
				String number = sms_body.substring(pos+TempL.length(),pos+TempL.length()+1);
				if(Character.isDigit(number.charAt(0)))
				{
					int num = Integer.parseInt(number);
					res_str+= "("+settings.getFunctionTmpSensor(num-1) + ") = ";
					sms_body=sms_body.replace(TempH+number+"=", res_str);
				}
				else
					sms_body=sms_body.replace(TempH, res_str);
			}
			
			String datchik_rele="datchik rele=";
			
			if(sms_body.contains(datchik_rele))
			{
				if(sms_body.length() > sms_body.indexOf(datchik_rele)+datchik_rele.length()+2 && settings.getDevVersion() == BaseActivity.deviceAfter01112012)
				{
					String to_replace = sms_body.substring(sms_body.indexOf(datchik_rele),sms_body.indexOf(datchik_rele)+datchik_rele.length()+3);
					if(settings.getNumberSensorReleWarm() > 0)
						sms_body=sms_body.replace(to_replace,"Для реле №"+Integer.toString(settings.getNumberReleWarm())+" задан термодатчик №"+Integer.toString(settings.getNumberSensorReleWarm()));
					else
						sms_body=sms_body.replace(to_replace,"Термостат отключен");
				}
				else
				{
					String to_replace = sms_body.substring(sms_body.indexOf(datchik_rele),sms_body.indexOf(datchik_rele)+datchik_rele.length()+1);
					if(settings.getNumberSensorReleWarm() > 0)
						sms_body=sms_body.replace(to_replace,"Для реле №"+Integer.toString(settings.getNumberReleWarm())+" задан термодатчик №"+Integer.toString(settings.getNumberSensorReleWarm()));
					else
						sms_body=sms_body.replace(to_replace,"Термостат отключен");
				}
			}
			
			sms_body+= "\nSIM = "+ sms_from;
			
			DBSMS dbsms = new DBSMS(this);
			dbsms.open();
			dbsms.addRec(sms_body, now_long);
			if(isReportSystem)
			{
				settings.setLastSystemReport(sms_body);
				settings.setLastSystemReportTime(now_long);
			}
		    //write sms to base
			dbsms.close();
			
		    intent.putExtra(MessageSystemActivity.PARAM_SMS, sms_body);
		    String strDate=new SimpleDateFormat("dd.MM.yy HH:mm").format(now_long);
		    //Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_LONG).show();
	    	intent.putExtra(MessageSystemActivity.PARAM_SMSTIME, strDate );
	    	intent.putExtra(BaseActivity.prefLastSystemReport, sms_body);
	    	intent.putExtra(BaseActivity.prefLastSystemReportTime, now_long);
	    	sendBroadcast(intent);
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
			
			DBSMS dbsms = new DBSMS(this);
			dbsms.open();
			Date now = new Date();
			long now_long = now.getTime();
			dbsms.addRec(sms_body, now_long);

			SharedPreferences pref = getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS);
			CSettingsPref settings = new CSettingsPref(pref);
			settings.setLastSystemReport(sms_body);
			settings.setLastSystemReportTime(now_long);

		    //write sms to base
			dbsms.close();
			Intent intent = new Intent(MessageSystemActivity.BROADCAST_ACTION_RCVSMS);
			intent.putExtra(MessageSystemActivity.PARAM_SMS, sms_body);
		    String strDate=new SimpleDateFormat("dd.MM.yy HH:mm").format(now_long);
		    //Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_LONG).show();
	    	intent.putExtra(MessageSystemActivity.PARAM_SMSTIME, strDate );
	    	intent.putExtra(BaseActivity.prefLastSystemReport, sms_body);
	    	intent.putExtra(BaseActivity.prefLastSystemReportTime, now_long);
	    	sendBroadcast(intent);
			
		}
	    
	}
	
	private int parseIntValue(String value)
	{
		int res = 0;
		
		int isPositive = 1;
		if(value.charAt(0) == '-')
		{
			isPositive = -1;
		}
		if(value.charAt(1) == 0 && value.length() == 3) 
		{
			String str = "";
			str+=value.charAt(2);
			res = Integer.parseInt(str)* isPositive;
		}
		if(value.length() == 3 && value.charAt(1) != 0)
		{
			String str = "";
			str+=value.charAt(1);
			str+=value.charAt(2);
			res = Integer.parseInt(str)* isPositive;
		}
		
		return res;
	}
	
	private boolean IsReportSystem(String sms_body)
	{
		boolean res = true;
//		boolean valueControl = false;
//		String strControlSnjat="Control snjat";
//		String strSystemaNaControle="Systema na controle";
//		if(sms_body.contains(strControlSnjat) || sms_body.contains(strSystemaNaControle))
//			valueControl= true;
//		String strTEMPR1="T1=";
//		String strTEMPR2="T2=";
//		String strTEMPR3="T3=";
//		if(sms_body.contains(strTEMPR3) && sms_body.contains(strTEMPR2) && sms_body.contains(strTEMPR1) && valueControl)
//			res = true;
		return res;
	}
	
	private void parseSMS(String sms,CSettingsPref settings)
	{
		//increment number notifications
		//CSettingsPref.incNumNotification();
        int num_not = settings.getNumNotificationSaved() + 1;
        //Toast.makeText(getApplicationContext(), "num not = "+ Integer.toString(num_not), Toast.LENGTH_LONG).show();
        settings.setNumNotificationSaved(num_not);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
     // 3-я часть
        Intent intent = new Intent(this, MessageSystemActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        
        if (currentapiVersion >= android.os.Build.VERSION_CODES.FROYO){
            // Do something for froyo and above versions
        	//Define sound URI
        	Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        	NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        	builder.setContentIntent(pIntent)
        	            .setSmallIcon(R.drawable.ic_launcher)
        	            .setTicker("Новое сообщение от системы Кситал")
        	            .setWhen(System.currentTimeMillis())
        	            .setAutoCancel(true)
        	            .setNumber(num_not)
        	            .setContentTitle("Система Кситал")
        	            .setContentText(String.format("Непрочитанных сообщений %d", num_not));
        	builder.setSound(soundUri);
        	Notification notif = builder.build();
        	notif.defaults |= Notification.DEFAULT_VIBRATE |Notification.DEFAULT_SOUND |Notification.FLAG_AUTO_CANCEL;
        	notif.flags |= Notification.FLAG_SHOW_LIGHTS;
        	
        	
        	notif.ledARGB = 0xff0000ff;
        	notif.ledOnMS = 800;
        	notif.ledOffMS = 1000* 36000;
        	
        	//notif.number = num_not;
        	nm.notify(1, notif);
        } else{
            // do something for phones running an SDK before froyo
        	Notification notif = new Notification(R.drawable.ic_launcher, "Новое сообщение от системы Кситал", 
          	      System.currentTimeMillis());       	    
	                 	    
	         // 2-я часть
	         notif.setLatestEventInfo(this, "Система Кситал", String.format("Непрочитанных сообщений %d", num_not), pIntent);
	          	    
	         // ставим флаг, чтобы уведомление пропало после нажатия
	         notif.flags |= Notification.DEFAULT_VIBRATE |Notification.DEFAULT_SOUND |Notification.FLAG_AUTO_CANCEL;
	         notif.flags |= Notification.FLAG_SHOW_LIGHTS;
	         notif.ledARGB = 0xff00ff00;
	         notif.ledOnMS = 800;
	         notif.ledOffMS = 1000* 36000;
	         //notif.	    
	         notif.number = num_not;
	         nm.notify(1, notif);
        }
		//detect device version
        String [] linesRep = sms.split("\n");
        int k = 0;
        for(k = 0; k< linesRep.length; k++)
        {
        	if(linesRep[k].contains("Temp.R"))
        	{
        		String lineRele = linesRep[k];
        		String tempR = "Temp.R=";
                if(lineRele.contains(tempR))
                {
                	//device 1 or 2
                	if(lineRele.contains("\\"))
                		settings.setDevVersion(BaseActivity.deviceBefore01112011);
                	else
                		settings.setDevVersion(BaseActivity.deviceBefore01112012);
                }
                else
                {
                	settings.setDevVersion(BaseActivity.deviceAfter01112012);
                }
        	}
        }
        
        
        //
        
		//rele
		int n = sms.indexOf("RELE=");
		
		//rele 1
		if(n>-1)
		{
			String str_rele = sms.substring(n+5, n+9);
			//Toast.makeText(getApplicationContext(), str_rele, Toast.LENGTH_LONG).show();
			if(str_rele.charAt(0)=='1')
				settings.setIsRele1(true);
			else if(str_rele.charAt(0)=='0')
				settings.setIsRele1(false);
			
			if(str_rele.charAt(1)=='1')
				settings.setIsRele2(true);
			else if(str_rele.charAt(1)=='0')
				settings.setIsRele2(false);
			
			if(str_rele.charAt(2)=='1')
				settings.setIsRele3(true);
			else if(str_rele.charAt(2)=='0')
				settings.setIsRele3(false);
			
			if(str_rele.charAt(3)=='1')
				settings.setIsUpr(true);
			else if(str_rele.charAt(3)=='0')
				settings.setIsUpr(false);
		}
		
		n = sms.indexOf("BALANS=");
		if(n>-1)
		{
		String str_balans = sms.substring(n+7, sms.length()-1);
		settings.setUSSDSIM(str_balans);
		}
		//guardian
		boolean flagTr=false;
        boolean res = sms.contains("snjat");
        boolean res2=sms.contains("na controle");
        flagTr=res ||res2;
       		 
        if(flagTr)
    	   settings.setIsGuardian(!res);
        
        //rele control resp for command
        String vkl_rele="Vklucheno rele N";
        String otkl_rele="Otklucheno rele N";
        n = sms.indexOf(vkl_rele);
        String rele_value="-1";
        boolean flag_vkl=false;
        if(n>-1)
        {
        	flag_vkl=true;
        	rele_value = sms.substring(n+vkl_rele.length(),n+vkl_rele.length()+1);
        }
        else
        {
        	flag_vkl= false;
        	n =  sms.indexOf(otkl_rele);
        	if(n>-1)
        	{
        		rele_value = sms.substring(n+otkl_rele.length(),n+otkl_rele.length()+1);
        	}	
        }
        if(n>-1)
        {
        	int n_rele = Integer.parseInt(rele_value);
        	if(n_rele>0 && n_rele<4)
        	{
        		switch(n_rele)
        		{
        		case 1:
        			settings.setIsRele1(flag_vkl);
        			break;
        		case 2:
        			settings.setIsRele2(flag_vkl);
        			break;
        		case 3:
        			settings.setIsRele3(flag_vkl);
        			break;
        		}
        	}
        }
        ////n tmp sensor
        String datchik_rele="datchik rele=";
        n=sms.indexOf(datchik_rele);
        if(n>-1)
        {
        	int n_sensor=0;
        	int n_rele = 0;
        	String str_nsenosr1=sms.substring(n+datchik_rele.length(), n+datchik_rele.length()+1);
        	
        	
        	if(!str_nsenosr1.equalsIgnoreCase("0"))
        	{
        		n_sensor=Integer.parseInt(str_nsenosr1);
        		n_rele = 1;
        	}
        	if(sms.length() > n+datchik_rele.length()+2)
        	{
	        	String str_nsenosr2=sms.substring(n+datchik_rele.length()+1, n+datchik_rele.length()+2);
	        	String str_nsenosr3=sms.substring(n+datchik_rele.length()+2, n+datchik_rele.length()+3);
	        	if(!str_nsenosr2.equalsIgnoreCase("0"))
	        	{
	            	n_sensor=Integer.parseInt(str_nsenosr2);
	            	n_rele = 2;
	        	}
	        	if(!str_nsenosr3.equalsIgnoreCase("0"))
	        	{
	            	n_sensor=Integer.parseInt(str_nsenosr3);
	            	n_rele = 3;
	        	}
        	}
        	//Toast.makeText(getApplicationContext(), str_nsenosr1 + str_nsenosr2 + str_nsenosr3, Toast.LENGTH_LONG).show();
        	settings.setNumberSensorReleWarm(n_sensor);
        	
        	if(settings.getDevVersion() == BaseActivity.deviceAfter01112012)
        		settings.setNumberReleWarm(n_rele);
        	else
        		settings.setNumberReleWarm(2);
        }
        for(int i=1;i<10;i++)
        {
        	String prefix_str = String.format("0%d SMS=+7",i);
        	String wrong_number = String.format("0%d SMS=+7**********", i);
        	if(sms.contains(wrong_number))
        	{
        		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        		Map<String, String> m = new HashMap<String, String>();
    		    m.put(BaseActivity.ATTRIBUTE_CONTACT_NAME, "none");
    		    m.put(BaseActivity.ATTRIBUTE_PHONE, wrong_number);
    		    m.put(BaseActivity.ATTRIBUTE_POSIITION,Integer.toString(i));
    		    m.put(BaseActivity.ATTRIBUTE_AVAILABLE, "1");
    		    data.add(m);
    		    settings.setNumbersBroadcast(data);
        	}
        }
        
        //tmp sensor
//        String tmpR2="Temp.R2=";
//        n=sms.indexOf(tmpR2);
//        if(n>-1)
//        {
//        	int n_sensor=settings.getNumberSensorReleWarm();
//        	String str_tmp_rele=sms.substring(n+tmpR2.length(),n+tmpR2.length()+3);
//        	int nplus=str_tmp_rele.indexOf("+0");
//        	int nminus=-1;
//        	if(nplus>-1)
//        	{
//        		str_tmp_rele=str_tmp_rele.substring(1, 2);
//        	}
//        	else
//        	{
//        		nminus=str_tmp_rele.indexOf("-0");
//        		if(nminus >-1)
//        		{
//        			str_tmp_rele=str_tmp_rele.substring(1, 2);
//        		}
//        	}
//        	
//        	int tmp=Integer.parseInt(str_tmp_rele);
//        	if(nminus>-1) tmp=0-tmp;
//        	
//        	settings.setTmpReleWarm(n_sensor, tmp);
//        }
        

        
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {}
//        
	}
	
}
