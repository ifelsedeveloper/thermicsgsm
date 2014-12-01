package com.therm.thermicscontrol;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CSettingsDev {
	SystemConfig settings_;
	String phoneNumber;
	String password;
	public Queue<SMSCommand> sms_to_send=new LinkedList<SMSCommand>();
	public int count_default_param = 3;
	Context appcontext;
	Executor ex;
    static Handler handler;
	SendCommandsRun mysend = null;
	enum numberSMSFunction
	{
		SMS_postanovka(34),
		SMS_snjatie(35),
		T_datchik_SMS(38),
		Parol(32),
		otchet(33),
		Syrena(36),
		Zagolovok(30),
		ReleN1(39),
		Upravlenie(51),
		ActiveZone(50),
		MainNumber(10),
		Password(32),
		DailyReport(33),
		NTmpSensorSMS(38),
		NTmpSensorReleWarm(37),
		USSD(48);
		numberSMSFunction(int ncommand)
		{
			this.ncommand=ncommand;
		}
		private final int ncommand;
		public int ncommand()
		{return ncommand;}
	};
	
	CSettingsDev(SystemConfig settings,final Context appcontext)
	{
		this.settings_=settings;
		
		this.appcontext =appcontext;
		ex=Executors.newFixedThreadPool(1);
		handler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	        	//Bundle b = msg.obtain().getData();
	            //if (msg.what == 0) {
	            Toast.makeText(appcontext,(String)msg.getData().getString("msgvalue"),Toast.LENGTH_LONG).show();
	            
	            //}
	     }};
	}
	
	public void setSettingsAndContext(SystemConfig settings,final Context appcontext) {
		this.settings_=settings;
		this.appcontext = appcontext;
	}
	
	Handler hdp;	
	public void setHandlerDialog(Handler hdp)
	{
		this.hdp = hdp;
	}
	
	public void sendSMS(String message) {
		if(isSimNumberValid())
		{
			phoneNumber=settings_.getNumberSIM();
			password=settings_.getPinSIM();
		    SmsManager smsManager = SmsManager.getDefault();
		    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
		    long currentTime = new Long(System.currentTimeMillis()/1000);
		    SMSRequestReportSender.lastSendTime.set(currentTime);
		}
			
	}
	
	public boolean isSimNumberValid()
	{
		boolean res = false;
		String phone_number;
		if(settings_.getIsFirstSim())
		{
			phone_number = settings_.getNumberSIM();
		}
		else
		{
			phone_number = settings_.getNumberSIM2();
		}
		
		res = isPhoneNumberValid(phone_number);
		return res;
	}
	
	public boolean isPhoneNumberValid(String phone_number)
	{
		boolean res = false;
		if(phone_number.length() == 12)
		{
			if(PhoneNumberUtils.isGlobalPhoneNumber(phone_number))
				res = true;
		}
		
		if(phone_number.length() == 11)
		{
			if(phone_number.charAt(0)=='8')
			{
				phone_number=String.format("+7%s", phone_number.substring(1,phone_number.length()));
			
				if(PhoneNumberUtils.isGlobalPhoneNumber(phone_number))
					res = true;
			}
		}
		if(!res) Toast.makeText(appcontext, "¬ведите корректный номер SIM карты", Toast.LENGTH_LONG).show();
		return res;
	}
	
	public void clearQueueCommands()
	{
		sms_to_send.clear();
	}
	
	public void VklRele(int nrele, int timeout)
	{
		phoneNumber=settings_.getNumberSIM();
		password=settings_.getPinSIM();
		String message=String.format(Locale.US,"Vkl %d %d %s",nrele, timeout, password);
		sendSMS(message);
		Toast.makeText(appcontext,String.format(Locale.US,"ќтправлена команда: %s",message),Toast.LENGTH_LONG).show();
	}
	
	public void RequestReport()
	{
		phoneNumber=settings_.getNumberSIM();
		password=settings_.getPinSIM();
		String message=String.format(Locale.US,"Kak dela? %s", password);
		sendSMS(message);
	}
	
	public void RequestBalans()
	{
		String message=String.format(Locale.US,"Balans %s", password);
		sendSMS(message);
	}
	
	public void AddRequestBalansCommand()
	{
		password=settings_.getPinSIM();
		String message=String.format(Locale.US,"Balans %s", password);
		SMSCommand command = new SMSCommand(message, 1);
		sms_to_send.add(command);
	}
	
	public void AddRequestReportCommand()
	{
		password=settings_.getPinSIM();
		String message=String.format(Locale.US,"Kak dela? %s", password);
		SMSCommand command = new SMSCommand(message, 1);
		sms_to_send.add(command);
	}
	
	public String getCommandDevSettings(int nn,int value,String password)
	{
		return String.format(Locale.US,"N%d(%d)%s", nn,value,password);
	}
	
	public String getCommandDevSettings(int nn,String value,String password)
	{
		return String.format(Locale.US,"N%d(%s)%s", nn,value,password);
	}
	public String getCommandDevSettings(int nn,boolean value,String password)
	{
		String val;
		if(value)
			val = "1";
		else
			val = "0";
		return String.format(Locale.US,"N%d(%s)%s", nn,val,password);
	}
	
	
	public void recvSMS(String sms)
	{
		if(mysend!=null)
		mysend.iSmsRecive = true;
	}
	
	public boolean getIsRecvSMS()
	{
		return mysend.iSmsRecive;
	}
	
	public void SetDefaultDevParametrsMultiSMS() 
	{
		Toast.makeText(appcontext, "”становка начальных параметров", Toast.LENGTH_LONG).show();
		phoneNumber=settings_.getNumberSIM();
		password=settings_.getPinSIM();
		
		sms_to_send = getDefaultStartDevCommands();
		
		mysend = new SendCommandsRun(sms_to_send,phoneNumber,appcontext,handler,hdp,getActualPhoneNumber());
		ex.execute(mysend);
	}
	
	
	public boolean sendCommands()
	{
		if(isSimNumberValid())
		{
			Toast.makeText(appcontext, "ќтправка команд "+settings_.getName(), Toast.LENGTH_LONG).show();
			phoneNumber=settings_.getNumberSIM();
			password=settings_.getPinSIM();
			if(sms_to_send.size()>0)
			{
				mysend = new SendCommandsRun(sms_to_send,phoneNumber,appcontext,handler,hdp,getActualPhoneNumber());
				ex.execute(mysend);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getActualPhoneNumber()
	{
		String PhoneNumber;
		if(settings_.getIsFirstSim())
			PhoneNumber = settings_.getNumberSIM();
		else
			PhoneNumber = settings_.getNumberSIM2();
		
		return PhoneNumber;
	}
	
	public Queue<SMSCommand> getDefaultStartDevCommands()
	{
		Queue<SMSCommand> result = new LinkedList<SMSCommand>();
		password=settings_.getPinSIM();
		SMSCommand command;
			
		int ncommand=0;
		int value=0;
		
		//active zone
		ncommand=numberSMSFunction.ActiveZone.ncommand();
		value=1111;
		command = new SMSCommand(getCommandDevSettings(ncommand, value, password), 0);
		result.add(command);
		
		//sms rele N1 set control
		ncommand=numberSMSFunction.ReleN1.ncommand();
		value=0;
		command = new SMSCommand(getCommandDevSettings(ncommand, value, password), 0);
		result.add(command);
		
		
		return result;
	}
	
	public void AddSetSimNumberCommand(String number_sim,boolean anyway)
	{
		
		if((!number_sim.equals(settings_.getNumberSIM())) || anyway)
		{
			password=settings_.getPinSIM();
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.MainNumber.ncommand(), number_sim, password), 0);
			sms_to_send.add(command);
			//settings_.setNumberSIM(number_sim);
			this.phoneNumber = number_sim;
		}
	}
	
	public void AddSetUpravlenieCommand(boolean value)
	{
		password=settings_.getPinSIM();
		if(value)
		{
			//enable upravlenie
			SMSCommand command = new SMSCommand(String.format(Locale.US,"Upr vkl %s", password), 1);
			sms_to_send.add(command);
		}
		else
		{
			//disable upravlenie
			SMSCommand command = new SMSCommand(String.format(Locale.US,"Upr otkl %s", password), 1);
			sms_to_send.add(command);
		}
	}
	
	public void AddSetSimPasswordCommand(String password,boolean anyway)
	{
		
		if((!password.equals(settings_.getPinSIM())) || anyway)
		{
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.Password.ncommand(), password, settings_.getPinSIM()), 0);
			sms_to_send.add(command);
			//settings_.setPinSIM(password);
			this.password=password;
		}
	}
	
	public void AddSetDailyReportCommand(boolean isDailyReport,boolean anyway)
	{
		
		if( (! ( isDailyReport == settings_.getIsDailyReport() ) ) || anyway)
		{
			password=settings_.getPinSIM();
			SMSCommand smsCommand = new SMSCommand(getCommandDevSettings(numberSMSFunction.DailyReport.ncommand(), isDailyReport, password), 0);
			sms_to_send.add(smsCommand);
			//settings_.setIsDailyReport(isDailyReport);
		}
	}
	
	
	public void AddSetNotificationSMSCommand(int upperBound,int lowerBound, int ntmpsensor,boolean anyway)
	{
		CNotificationSMS notSMS = new CNotificationSMS();
		if(ntmpsensor>0)
		notSMS = settings_.getCNotificationSMS(ntmpsensor-1);
		int oldntmpsensor = settings_.getNumberTmpSensorSMS();
		
		password=settings_.getPinSIM();
		//номер термодатчика
		if( (! ( ntmpsensor == oldntmpsensor ) ) || anyway)
		{
			SMSCommand smsCommand = new SMSCommand(getCommandDevSettings(numberSMSFunction.NTmpSensorSMS.ncommand(), Integer.toString(ntmpsensor), password), 0);
			sms_to_send.add(smsCommand);
			//settings_.setNumberTmpSensorSMS(ntmpsensor);
		}
		if(ntmpsensor>0)
		{
			//верхн€€ граница
			if( (! ( notSMS.lower_bound == lowerBound ) ) || anyway)
			{
				SMSCommand smsCommand = new SMSCommand(String.format(Locale.US,"Temp.L%d=%d %s", ntmpsensor,lowerBound,password), 1);
				sms_to_send.add(smsCommand);
			}
			
			//нижн€€ граница
			if( (! ( notSMS.upper_bound == upperBound ) ) || anyway)
			{
				SMSCommand smsCommand = new SMSCommand(String.format(Locale.US,"Temp.H%d=%d %s", ntmpsensor,upperBound,password), 1);
				sms_to_send.add(smsCommand);
			}
			notSMS.upper_bound = upperBound;
			notSMS.lower_bound = lowerBound;
		}
		//settings_.setCNotificationSMS(ntmpsensor-1, notSMS);
	}
	
	public void AddSetNumberSensorReleWarmCommand(int nSensorReleWarm,int n_rele,boolean anyway)
	{
		if(nSensorReleWarm>-1 && nSensorReleWarm<7)
		{
			if(settings_.getDevVersion() == BaseActivity.deviceAfter01112012)
			{
				if( ( nSensorReleWarm != settings_.getNumberSensorReleWarm() )  || ( n_rele != settings_.getNumberReleWarm() ) || anyway)
				{
					password=settings_.getPinSIM();
					String val = "020";
					switch(n_rele)
					{
					 case 1:
						 val = String.format(Locale.US,"%d00", nSensorReleWarm);
						 break;
					 case 2:
						 val = String.format(Locale.US,"0%d0", nSensorReleWarm);
						 break;
					 case 3:
						 val = String.format(Locale.US,"00%d", nSensorReleWarm);
						 break;
					}
					
					SMSCommand smsCommand = new SMSCommand(getCommandDevSettings(numberSMSFunction.NTmpSensorReleWarm.ncommand(), val, password), 0);
					sms_to_send.add(smsCommand);
					//settings_.setNumberSensorReleWarm(nReleWarm);
				}
			}
			else
			{
				if( ( nSensorReleWarm != settings_.getNumberSensorReleWarm() )  || ( n_rele != settings_.getNumberReleWarm() ) || anyway)
				{
					password=settings_.getPinSIM();
					String val = "1";
					val = String.format(Locale.US,"%d", nSensorReleWarm);
					SMSCommand smsCommand = new SMSCommand(getCommandDevSettings(numberSMSFunction.NTmpSensorReleWarm.ncommand(), val, password), 0);
					sms_to_send.add(smsCommand);
					//settings_.setNumberSensorReleWarm(nReleWarm);
				}
			}
		}
	}
	
	public void AddSetAoutPowerOnSyrenCommand(boolean isSyren,boolean anyway)
	{

		if( (! ( isSyren == settings_.getIsAutoPowerOnAlarm() ) ) || anyway)
		{
			password=settings_.getPinSIM();
			String val;
			if(isSyren)
				val="1";
			else
				val="0";
			SMSCommand smsCommand = new SMSCommand(getCommandDevSettings(numberSMSFunction.Syrena.ncommand(), val, password), 0);
			sms_to_send.add(smsCommand);
			//settings_.setIsAutoPowerOnAlarm(isSyren);
		}
	}
	
	
	public void AddSetGuardianCommand(boolean isGuardian,boolean anyway)
	{

		if( (! ( isGuardian == settings_.getIsGuardian() ) ) || anyway)
		{
			password= settings_.getPinSIM();
			String val;
			if(isGuardian)
				val=String.format(Locale.US,"Ust control %s", password);
			else
				val=String.format(Locale.US,"Otkl control %s", password);
			SMSCommand smsCommand = new SMSCommand(val, 1);
			sms_to_send.add(smsCommand);
		}
	}
	
	public void AddSetTmpWaterCommand(int tmpwater,boolean anyway)
	{
		if(tmpwater>-56 && tmpwater<100)
		if(tmpwater != settings_.getTmpWater() || anyway)
		{
			if((settings_.getNumberSensorReleWarm() == 2))
			{
				password=settings_.getPinSIM();
				String val = String.format(Locale.US,"Temp.R2=%d %s", tmpwater,password);
				SMSCommand smsCommand = new SMSCommand(val, 1);
				sms_to_send.add(smsCommand);
			}
		}
	}
	
	public void AddSetTmpReleCommand(int n_tmpsensor,int tmprele,int tmprele_night,int n_rele,boolean anyway)
	{
		if(tmprele>-56 && tmprele<100)
		{
			int nbutton = settings_.getNTempConfig();
			switch(settings_.getDevVersion())
			{
			case BaseActivity.deviceAfter01112012:
				if(tmprele != settings_.getTempDayConfig(n_tmpsensor,nbutton) || anyway || n_rele != settings_.getNumberReleWarm())
				{
						password=settings_.getPinSIM();
						String val = String.format(Locale.US,"Temp.R%d=%d/%d %s",n_rele, tmprele,tmprele_night,password);
						SMSCommand smsCommand = new SMSCommand(val, 1);
						sms_to_send.add(smsCommand);	
				}
				break;
				
			case BaseActivity.deviceBefore01112011:
				if(tmprele != settings_.getTempDayConfig(n_tmpsensor, nbutton) || anyway )
				{
						password=settings_.getPinSIM();
						String val = String.format(Locale.US,"Temp.R=%d %s", tmprele, password);
						SMSCommand smsCommand = new SMSCommand(val, 1);
						sms_to_send.add(smsCommand);	
				}
				break;
				
			case BaseActivity.deviceBefore01112012:
				if(tmprele != settings_.getTempDayConfig(n_tmpsensor, nbutton) || anyway )
				{
						password=settings_.getPinSIM();
						String val = String.format(Locale.US,"Temp.R=%d/%d %s", tmprele,tmprele_night,password);
						SMSCommand smsCommand = new SMSCommand(val, 1);
						sms_to_send.add(smsCommand);	
				}
				break;
			}
		}
	}
	
	public void AddSetTmpAirCommand(int tmpair,boolean anyway)
	{
		if(tmpair>-56 && tmpair<100)
		if(tmpair != settings_.getTmpAir() || anyway)
		{
			if((settings_.getNumberSensorReleWarm() == 3))
			{
				password=settings_.getPinSIM();
				String val = String.format(Locale.US,"Temp.R2=%d %s",tmpair,password);
				SMSCommand smsCommand = new SMSCommand(val, 1);
				sms_to_send.add(smsCommand);
			}
		}
	}
	
	public void AddSetUSSDCommand(String ussd, boolean anyway)
	{
		if(  (settings_.getIsFirstSim()  && !ussd.equalsIgnoreCase(settings_.getUSSDSIM())) || anyway || (!settings_.getIsFirstSim()  && !ussd.equalsIgnoreCase(settings_.getUSSDSIM2())) )
		{
			password=settings_.getPinSIM();
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.USSD.ncommand(), ussd, password), 0);
			sms_to_send.add(command);
		}
	}
	
	public void AddSwitchSimCommand( boolean isSim1)
	{
		if(settings_.getIsFirstSim() != isSim1)
		{
			password=settings_.getPinSIM();
			SMSCommand command = new SMSCommand(String.format(Locale.US,"Sim %s",password), 0);
			sms_to_send.add(command);
		}
	}
	public void AddSetReleNCommand(int nrele,boolean value, boolean anyway)
	{
		AddSetReleNCommand(nrele, value, -1, anyway);
	}
	
	public void AddSetReleNCommand(int nrele,boolean value, int time, boolean anyway)
	{
		if(nrele>0 && nrele<4)
		{
			boolean releval=settings_.getIsRele(nrele-1);
			String command_text = "";
			command_text = GetCmdRele(nrele, value, time);
			if(value != releval || anyway)
			{
				SMSCommand smsCommand = new SMSCommand(command_text, 1);
				sms_to_send.add(smsCommand);
			}
		}
	}
	
	public String GetCmdRele(int nrele, boolean value)
	{
		return GetCmdRele(nrele, value, -1);
	}
	
	public String GetCmdRele(int nrele, boolean value, int time)
	{
		if(nrele == 0) nrele++;
		password = settings_.getPinSIM();
		String command_text = "";
		
		if(time < 0)
		{
			command_text = String.format(Locale.US,"%s rele %d %s", 
																value ? "Vkl" :"Otkl",
																nrele, 
																password);
		}
		else
		{
			command_text = String.format(Locale.US,"%s rele %d %d %s", 
																value ? "Vkl" :"Otkl",
																nrele, 
																time, 
																password);
		}
		return command_text;
	}
	
	public void AddSetIsSMSPostanovkaCommand(boolean isSMSPostanovka,boolean anyway)
	{

		if( (! ( isSMSPostanovka == settings_.getIsSMSPostanovka() ) ) || anyway)
		{
			password= settings_.getPinSIM();
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.SMS_postanovka.ncommand(), isSMSPostanovka, password), 0);
			sms_to_send.add(command);
		}
	}
	
	public void AddSetIsSMSSnjatieCommand(boolean isSMSSnjatie,boolean anyway)
	{

		if( (! ( isSMSSnjatie == settings_.getIsSMSSnjatie() ) ) || anyway)
		{
			password= settings_.getPinSIM();
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.SMS_snjatie.ncommand(), isSMSSnjatie, password), 0);
			sms_to_send.add(command);
		}
	}
	
	public void AddSetIsAutoOnRele1(boolean isAutoOnRele1,boolean anyway)
	{

		if( (! ( isAutoOnRele1 == settings_.getIsSetAutoRele1Control() ) ) || anyway)
		{
			password= settings_.getPinSIM();
			SMSCommand command = new SMSCommand(getCommandDevSettings(numberSMSFunction.ReleN1.ncommand(), isAutoOnRele1, password), 0);
			sms_to_send.add(command);
		}
	}
	
	//active zone
	public void AddSetActiveZone(int [] activeZone,boolean anyway)
	{
		boolean flagSetZone=false;
		int [] oldValueActiveZone=settings_.getActiveZoneValue();
		if(!anyway)
		for(int i=0;i<4;i++)
		{
			if(activeZone[i]!=oldValueActiveZone[i])
			{
				flagSetZone = true;
				break;
			}
		}
		else
			flagSetZone = true;
		
		if(!flagSetZone) return;
		
		int ncommand=numberSMSFunction.ActiveZone.ncommand();
		String value=String.format(Locale.US,"%d%d%d%d", activeZone[0],activeZone[1],activeZone[2],activeZone[3]);
		SMSCommand command = new SMSCommand(getCommandDevSettings(ncommand, value, password), 0);
		sms_to_send.add(command);
	}
	
	public void AddSetCallMicrophoneCommand()
	{
		password = settings_.getPinSIM();
		SMSCommand command = new SMSCommand(String.format("Mikrofon %s", password), 1);
		sms_to_send.add(command);
	}
	
	public void setNumbersBroadcastCommand(ArrayList<Map<String, String>> numbers)
	{
		password = settings_.getPinSIM();
		for(Map<String, String> map : numbers)
		{
			int pos = Integer.parseInt(map.get(BaseActivity.ATTRIBUTE_POSIITION));
			setNumberBroadcastCommand(pos+10,map.get(BaseActivity.ATTRIBUTE_PHONE));
		}
	}
	
	public void setNumberBroadcastCommand(int number_position, String phone_number)
	{
		SMSCommand command = new SMSCommand(getCommandDevSettings(number_position, phone_number, password), 0);
		sms_to_send.add(command);
	}
	
	public static final int TimeExecuteFirstCommand = 90;
	public static final int TimeExecuteSecondCommand = 30;
	public static final int DelayBetweenCommandsFirstType = 25;
	
	public int getTimeNeededToSend()
	{
		int res=0;
		
		int length = sms_to_send.size();
		
		int ncmd1=0, ncmd2=0;
		
		for (SMSCommand command : sms_to_send) {
			if(command.type == 0) ncmd1++;
			else ncmd2++;
		}
		if(length>1)
		{
			if(ncmd1>1)
				res = ncmd1*TimeExecuteFirstCommand + (ncmd1-1)*DelayBetweenCommandsFirstType + ncmd2*TimeExecuteSecondCommand;
			else
				res = ncmd1*TimeExecuteFirstCommand  + ncmd2*TimeExecuteSecondCommand +10;
		}
		
		if(length == 1) res = ncmd1*200 + ncmd2*TimeExecuteSecondCommand;
		
		
		return res+20;
	}
  }


	
	 class SendCommandsRun implements Runnable {
		    
		 Queue<SMSCommand> sms_to_send=new LinkedList<SMSCommand>();
			public boolean iSmsRecive= false;
			int current_command=0;
			int nrecived_sms = 0; 
			int nsend_sms = 0; 
			Timer timer;
			TimerTask tTask;
			long interval = 1000;
			int current_sec=0;
			boolean first_sms=true;
			Context appcontext;
			String phoneNumber;
			Handler handler;
			Handler hdp;
			boolean stop_timer=false;
			String CurrentPhoneNumber; 
		    public SendCommandsRun(Queue<SMSCommand> sms_to_send,String phoneNumber,Context appcontext,Handler handler,Handler hdp,String CurrentPhoneNumber) {
		    	this.phoneNumber = phoneNumber;
		    	this.sms_to_send =sms_to_send;
		    	this.appcontext = appcontext;
		    	timer = new Timer();
		    	this.handler =handler;
		    	this.hdp=hdp;
		    	this.phoneNumber = CurrentPhoneNumber;
		    }
		    
		    public void run() {
		      
		    	sendCommands();
		    }
		    
		    
		public void sendCommands()
		{
			
			timer = new Timer();
			interval=1000;
			first_sms=true;
			current_sec=0;
			iSmsRecive=false;
			int last_type=0;
			int long_interval=200;
			//final String tag_sms_sendr="tag_sms_sendr";
			for (Iterator<SMSCommand> iterator = sms_to_send.iterator(); iterator
					.hasNext();) {
				SMSCommand current_cmd = iterator.next();
				Log.i("tag_sms_sendr",current_cmd.command);
				if(first_sms)
				{
					//send sms command
					sendSMS(current_cmd.command);
					first_sms=false;
					scheduleIncSec();
					last_type=current_cmd.type;
				}
				else
				{
					//select time interval
					int time_interval = 35;
					if(last_type == 0) time_interval = long_interval;
					//wait until sms recive or timer 
					while(!iSmsRecive && current_sec< time_interval)
					{};
					current_sec=0;
					if(iSmsRecive && last_type == 0)
					{
						while(current_sec < 25)
						{};
					}
					//send sms command
					sendSMS(current_cmd.command);
					current_sec=0;
					iSmsRecive=false;
					
				}
				last_type=current_cmd.type;
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("msgvalue","ќтправлена команда: "+current_cmd.command);
				msg.setData(b);
				handler.sendMessage(msg);
				hdp.sendEmptyMessage(0);
				interval = -1 ;
			}
			//ex.execute(null);
			int time_interval=30;
			if(last_type == 0)time_interval =long_interval;
			while(!iSmsRecive && current_sec< time_interval)
			{};
			current_sec=0;
			if(iSmsRecive && last_type == 0)
			{
				while(current_sec < 1)
				{};
			}
			else
			{
				while(current_sec < 1)
				{};
			}
			hdp.sendEmptyMessage(0);
			hdp.sendEmptyMessage(0);
			hdp.sendEmptyMessage(0);
			timer.cancel();
		}
		void scheduleIncSec() {
		    if (tTask != null) tTask.cancel();
		    if (interval > 0) {
		      tTask = new TimerTask() {
		        public void run() {
		        	current_sec++;
		        }
		      };
		      timer.schedule(tTask, 1000, interval);
		    }
		  }
		public void sendSMS(String message) {
		    SmsManager smsManager = SmsManager.getDefault();
		    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
		    long currentTime = System.currentTimeMillis()/1000;
		    SMSRequestReportSender.lastSendTime.set(currentTime);
		}
}
