package com.therm.thermicscontrol;


import java.util.TooManyListenersException;

import com.therm.thermicscontrol.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ConfigSystemActivity extends BaseActivity implements TextWatcher, OnCheckedChangeListener  {

	
	public static final String TAG_events="event_tag_config_sytem";
	public CSettingsPref settings=null;
	public CSettingsDev settingsDev=null;
	
	static View viewOkCancel;
	
	BroadcastReceiver br;
	boolean run_sms_sender=false;
	//CSMSSender sender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_config_system);
		
		//create for work with shared preference
		Log.i(TAG_events,"start activity config sytem");
		
		//create for work with shared preference
		settings=new CSettingsPref(getSharedPreferences(MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS));
		settingsDev= new CSettingsDev(settings,getApplicationContext());
		getViewVaribales();
		loadParam();
		//sender=new CSMSSender(settings.getNumberSIM(), getApplicationContext());
		// создаем BroadcastReceiver
	    br = new BroadcastReceiver() {
	      // действия при получении сообщений
	      public void onReceive(Context context, Intent intent) {
	        String sms = intent.getStringExtra(PARAM_SMS);
	        String time = intent.getStringExtra(PARAM_SMSTIME);
	        Log.d(TAG_events, "onReceive sms: "+sms+" ;time = "+time);
	        //отправляем полученное сообщение нашему классу
	        //if(run_sms_sender) settingsDev.smsRecive(sms);
	        settingsDev.recvSMS(sms);
	      }
	    };
	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION_RCVSMS);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(br, intFilt);
	    
	    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	        @Override
	        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            Log.e("Alert","Lets See if it Works MainMenu !!!");
	            Toast.makeText(getApplicationContext(), "Error config system", Toast.LENGTH_LONG).show();
	        }
	    });
	    
	    setVisiblePassword();
	    isEditNumberPasswordHasFocus = false;
	    editTextNumberPassword.setOnFocusChangeListener(new OnFocusChangeListener() {
	    	@Override
	    	public void onFocusChange(View v, boolean hasFocus) {
	    	    if(hasFocus){
	    	        //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
	    	    	isEditNumberPasswordHasFocus = true;
	    	    	checkStateOkCancel();
	    	    }else {
	    	        //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
	    	    }
	    	   }
	    	});
	    
	}
	boolean isEditNumberPasswordHasFocus = false;
	
	public void setVisiblePassword()
	{
		RelativeLayout lpassword = (RelativeLayout) findViewById(R.id.relativeLayoutPassword);
		View devider = findViewById(R.id.firstDivider2afterBalans);
		if(!settings.getIsFirstSim())
	    {
	    	lpassword.setVisibility(View.GONE);
	    	devider.setVisibility(View.GONE);
	    }
		else
		{
			lpassword.setVisibility(View.VISIBLE);
			devider.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	Spinner spinnerSelectDevice;
	//data for activity
	public String simNumber;
	public String simNumber2;
	private boolean isSIM1 = true;
	private boolean LisSIM1 = true;
	public String simPassword;
	public String simUSSD;
	public boolean isDailyReport;
	public boolean isAutoAlarm;
	public boolean isAlwaysSend;
	public boolean isSMSPostanovka;
	public boolean isSMSSnjatie;
	public boolean isAutoOnRele1;
	public boolean isSMSInIncoming;
	public boolean isShowDeviceVersion = false;
	public CNotificationSMS [] notificationSMS=null;
	public int numberTmpSensorSMS=0;
	
	public String textSMSAlarmZone;
	public String titleSMSAlarmZone;
	
	////for onCreate
	//load parameters from system and display it on activity
	
	boolean loaded_complete = false;
	
	EditText editTextPhoneNumber,
	editTextPhoneNumber2,
	editTextCodeRequestBalans,
	editTextNumberPassword,
	editTextNumberSensorTMPSMS,
	editTextLowerBoundTmp,
	editTextUpperBoundTmp,
	editTextSMSHead,
	editTextSMSAarmZone;
	
	CheckBox checkBoxDaylyReport,
	checkBoxSMSPostanovka,
	checkBoxSMSSnjatie,
	checkBoxAutoPowerOnAlarm,
	checkBoxAutoPowerOnRele1Alarm,
	checkBoxIsSMSInIncoming;
	
	TextView buttonZone1,
	buttonZone2,
	buttonZone3,
	buttonZone4,
	titleCodeRequestBalans;
	
	ToggleButton ActiveSIM;
	
	public void getViewVaribales()
	{
		spinnerSelectDevice = (Spinner) findViewById(R.id.device_versions);
		viewOkCancel = findViewById(R.id.layoutApplyCancelConfigSystem);
		
		editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
		editTextPhoneNumber2 = (EditText) findViewById(R.id.editTextPhoneNumber2);
		ActiveSIM = (ToggleButton) findViewById(R.id.toggleButtonActiveSIM);
		editTextCodeRequestBalans = (EditText) findViewById(R.id.editTextCodeRequestBalans);
		editTextNumberPassword = (EditText) findViewById(R.id.editTextNumberPassword);
		
		editTextNumberSensorTMPSMS = (EditText) findViewById(R.id.editTextNumberSensorTMPSMS);
		editTextLowerBoundTmp = (EditText) findViewById(R.id.editTextLowerBoundTmp);
		editTextUpperBoundTmp = (EditText) findViewById(R.id.editTextUpperBoundTmp);
		
		editTextSMSHead = (EditText) findViewById(R.id.editTextSMSHead);
		editTextSMSAarmZone = (EditText) findViewById(R.id.editTextSMSAarmZone);
		
		checkBoxDaylyReport = (CheckBox) findViewById(R.id.checkBoxDaylyReport);
		checkBoxSMSPostanovka = (CheckBox) findViewById(R.id.checkBoxSMSPostanovka);
		checkBoxSMSSnjatie = (CheckBox) findViewById(R.id.checkBoxSMSSnjatie);
		
		checkBoxAutoPowerOnAlarm = (CheckBox) findViewById(R.id.checkBoxAutoPowerOnAlarm);
		checkBoxAutoPowerOnRele1Alarm = (CheckBox) findViewById(R.id.checkBoxAutoPowerOnRele1Alarm);
		checkBoxIsSMSInIncoming = (CheckBox) findViewById(R.id.checkSMSIsInIncoming);
		
		buttonZone1 = (TextView)findViewById(R.id.buttonZone1);
		buttonZone2 = (TextView)findViewById(R.id.buttonZone2);
		buttonZone3 = (TextView)findViewById(R.id.buttonZone3);
		buttonZone4 = (TextView)findViewById(R.id.buttonZone4);
		
		titleCodeRequestBalans = (TextView) findViewById(R.id.titleCodeRequestBalans);
		
	}
	
	public void checkStateOkCancel()
	{
		LdeviceVersion = spinnerSelectDevice.getSelectedItemPosition()+1;
		LsimNumber =  editTextPhoneNumber.getText().toString();	
		LsimNumber2 =  editTextPhoneNumber2.getText().toString();	
		LsimUSSD = editTextCodeRequestBalans.getText().toString();
		LsimPassword = editTextNumberPassword.getText().toString();
		LisSIM1 = ActiveSIM.isChecked();
		
		String LnumberTmpSensorSMSstr = editTextNumberSensorTMPSMS.getText().toString();
		boolean flagLow=false,flagUp=false;
		if((LnumberTmpSensorSMSstr.length() == 1) && 
				(LnumberTmpSensorSMSstr.charAt(0) == '0' ||
				LnumberTmpSensorSMSstr.charAt(0) == '1' ||
				LnumberTmpSensorSMSstr.charAt(0) == '2' ||
				LnumberTmpSensorSMSstr.charAt(0) == '3' ||
				LnumberTmpSensorSMSstr.charAt(0) == '4' ||
				LnumberTmpSensorSMSstr.charAt(0) == '5' ||
				LnumberTmpSensorSMSstr.charAt(0) == '6' 
				) )
		{
			boolean flag_digits = true;
			LnumberTmpSensorSMS = Integer.parseInt(LnumberTmpSensorSMSstr);
			if(LnumberTmpSensorSMS>0)
			{
				try{
					LnotificationSMS.lower_bound=Integer.parseInt(editTextLowerBoundTmp.getText().toString());
					LnotificationSMS.upper_bound=Integer.parseInt(editTextUpperBoundTmp.getText().toString());
					flagLow = (LnotificationSMS.lower_bound != notificationSMS[LnumberTmpSensorSMS-1].lower_bound);
					flagUp = (LnotificationSMS.upper_bound != notificationSMS[LnumberTmpSensorSMS-1].upper_bound);
					flag_digits= true;
				}
				catch(NumberFormatException nfe)  
				{  
					flag_digits = false;  
				} 
				
			}
			
			if(flag_digits)
			{
				LtitleSMSAlarmZone = editTextSMSHead.getText().toString();
				LtextSMSAlarmZone = editTextSMSAarmZone.getText().toString();
				
				LisDailyReport=checkBoxDaylyReport.isChecked();
				LisAutoAlarm=checkBoxAutoPowerOnAlarm.isChecked();
				LisAutoOnRele1=checkBoxAutoPowerOnRele1Alarm.isChecked();
				LisSMSPostanovka=checkBoxSMSPostanovka.isChecked();
				LisSMSSnjatie=checkBoxSMSSnjatie.isChecked();
				LisSMSInIncoming = checkBoxIsSMSInIncoming.isChecked();
				
				if(((!LsimNumber.equalsIgnoreCase(simNumber)) || (!LsimNumber2.equalsIgnoreCase(simNumber2)) ||
				  (!LsimUSSD.equalsIgnoreCase(simUSSD)) ||
				  (!LsimPassword.equalsIgnoreCase(simPassword)) ||
				  (!LtitleSMSAlarmZone.equalsIgnoreCase(titleSMSAlarmZone)) ||
				  (!LtextSMSAlarmZone.equalsIgnoreCase(textSMSAlarmZone)) ||
				  (LnumberTmpSensorSMS!=numberTmpSensorSMS) ||
				  flagUp || flagLow ||
				  (zoneValue[0] != LzoneValue[0]) ||
				  (zoneValue[1] != LzoneValue[1]) ||
				  (zoneValue[2] != LzoneValue[2]) ||
				  (zoneValue[3] != LzoneValue[3]) ||
				  (isDailyReport ^ LisDailyReport) ||
				  (isAutoAlarm ^ LisAutoAlarm) ||
				  (isSMSInIncoming ^ LisSMSInIncoming) ||
				  (isAutoOnRele1 ^ LisAutoOnRele1) ||
				  (isSMSPostanovka ^ LisSMSPostanovka) ||
				  (isSMSSnjatie ^ LisSMSSnjatie)) || isEditNumberPasswordHasFocus ||
				  (LdeviceVersion != deviceVersion) ||
				  (LisSIM1 != isSIM1) && 
				  flag_digits
						)
					viewOkCancel.setVisibility(View.VISIBLE);
				else
					viewOkCancel.setVisibility(View.GONE);
			}	
			else
				viewOkCancel.setVisibility(View.GONE);
		}
		else
			viewOkCancel.setVisibility(View.GONE);
	}
	
	public void loadParam()
	{
		deviceVersion = settings.getDevVersion();
		spinnerSelectDevice.setSelection(deviceVersion-1);
		spinnerSelectDevice.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	checkStateOkCancel();
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		        // your code here
		    }

		});
		loaded_complete = false;
		//load configuration from settings
		isSIM1 = settings.getIsFirstSim();
		simNumber=settings.getNumberSIM();
		simNumber2=settings.getNumberSIM2();
		
		if(isSIM1)
			simUSSD=settings.getUSSDSIM();
		else
			simUSSD=settings.getUSSDSIM2();
		
		simPassword=settings.getPinSIM();
		isSMSPostanovka=settings.getIsSMSPostanovka();
		isSMSSnjatie=settings.getIsSMSSnjatie();
		isAutoAlarm=settings.getIsAutoPowerOnAlarm();
		isDailyReport=settings.getIsDailyReport();
		isAlwaysSend=settings.getIsSendAnyway();
		isSMSInIncoming = settings.getIsSMSInIncoming();
		
		notificationSMS=new CNotificationSMS[6];
		int [] localZoneValue = settings.getActiveZoneValue();
		for(int i=0;i<4;i++)
		{
			zoneValue[i]=localZoneValue[i];
			LzoneValue[i]=localZoneValue[i];
		}
		isAutoOnRele1 = settings.getIsSetAutoRele1Control();
		
		int i;
		for(i=0;i<6;i++)
		{
			//notificationSMS[i]=new CNotificationSMS();
			notificationSMS[i]=settings.getCNotificationSMS(i);
		}
		numberTmpSensorSMS=settings.getNumberTmpSensorSMS();
		
		textSMSAlarmZone=settings.getTextSMSAlarm();
		titleSMSAlarmZone=settings.getTextSMSTitle();
		
		//set loaded parameters to activity
		
		viewOkCancel.setVisibility(View.GONE);
		
		editTextPhoneNumber.setText(simNumber);
		editTextPhoneNumber.addTextChangedListener(this);
		
		editTextPhoneNumber2.setText(simNumber2);
		editTextPhoneNumber2.addTextChangedListener(this);
		
		editTextCodeRequestBalans.setText(simUSSD);
		editTextCodeRequestBalans.addTextChangedListener(this);
		
		editTextNumberPassword.setText(simPassword);
		editTextNumberPassword.addTextChangedListener(this);
		editTextNumberPassword.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				isEditNumberPasswordHasFocus = true;
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				isEditNumberPasswordHasFocus = true;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				isEditNumberPasswordHasFocus = true;
			}
		});
		
		
		checkBoxDaylyReport.setChecked(isDailyReport);
		checkBoxDaylyReport.setOnCheckedChangeListener(this);
		
		checkBoxSMSPostanovka.setChecked(isSMSPostanovka);
		checkBoxSMSPostanovka.setOnCheckedChangeListener(this);
		
		checkBoxSMSSnjatie.setChecked(isSMSSnjatie);
		checkBoxSMSSnjatie.setOnCheckedChangeListener(this);
		
		checkBoxAutoPowerOnAlarm.setChecked(isAutoAlarm);
		checkBoxAutoPowerOnAlarm.setOnCheckedChangeListener(this);

		checkBoxAutoPowerOnRele1Alarm.setChecked(isAutoOnRele1);
		checkBoxAutoPowerOnRele1Alarm.setOnCheckedChangeListener(this);
		
		checkBoxIsSMSInIncoming.setChecked(isSMSInIncoming);
		checkBoxIsSMSInIncoming.setOnCheckedChangeListener(this);
		
		buttonZone1.setText(String.format("%d", zoneValue[0]));
		buttonZone1.addTextChangedListener(this);
		
		buttonZone2.setText(String.format("%d", zoneValue[1]));
		buttonZone2.addTextChangedListener(this);
		
		buttonZone3.setText(String.format("%d", zoneValue[2]));
		buttonZone3.addTextChangedListener(this);
		
		buttonZone4.setText(String.format("%d", zoneValue[3]));
		buttonZone4.addTextChangedListener(this);
		
		//loading first notification

		editTextNumberSensorTMPSMS.setText(Integer.toString(numberTmpSensorSMS));
		editTextNumberSensorTMPSMS.addTextChangedListener(this);
		
		if(numberTmpSensorSMS>0)
		{
			editTextLowerBoundTmp.setText(Integer.toString(notificationSMS[numberTmpSensorSMS-1].lower_bound));
			editTextLowerBoundTmp.addTextChangedListener(this);
			
			editTextUpperBoundTmp.setText(Integer.toString(notificationSMS[numberTmpSensorSMS-1].upper_bound));
			editTextUpperBoundTmp.addTextChangedListener(this);
		}
		else
		{
			editTextLowerBoundTmp.setText(Integer.toString(0));
			editTextLowerBoundTmp.addTextChangedListener(this);
		
			editTextUpperBoundTmp.setText(Integer.toString(0));
			editTextUpperBoundTmp.addTextChangedListener(this);
		}
		
		editTextSMSHead.setText(titleSMSAlarmZone);
		editTextSMSHead.addTextChangedListener(this);
		
		editTextSMSAarmZone.setText(textSMSAlarmZone);
		editTextSMSAarmZone.addTextChangedListener(this);
		
		ActiveSIM.setChecked(isSIM1);
		
		setRequestCodeBalanse();
		
		loaded_complete = true;
	}
	
	
	public void setRequestCodeBalanse()
	{
		if(isSIM1)
			simUSSD=settings.getUSSDSIM();
		else
			simUSSD=settings.getUSSDSIM2();
		
		if(isSIM1)
			titleCodeRequestBalans.setText(getResources().getString(R.string.sim_code_balans));
		else
			titleCodeRequestBalans.setText(getResources().getString(R.string.sim_code_balans2));
		
		editTextCodeRequestBalans.setText(simUSSD);
	}
		////
	//save parameters to system
	String LsimNumber;
	String LsimNumber2;
	String LsimUSSD;
	String LsimPassword;
	
	int deviceVersion = 3;
	int LdeviceVersion = 3;
	boolean LisDailyReport;
	boolean LisAutoAlarm;
	boolean LisAutoOnRele1;
	boolean LisSMSPostanovka;
	boolean LisSMSSnjatie;
	boolean LisSMSInIncoming;
	
	int LnumberTmpSensorSMS;
	CNotificationSMS LnotificationSMS = new CNotificationSMS();
	
	String LtitleSMSAlarmZone;
	String LtextSMSAlarmZone;
	public static boolean isOKSend = false;
	boolean isSendNewPassword = false;
	public void saveParam( )
	{
		
		//loading parameters from activity
		isOKSend = false;
		LsimNumber =  editTextPhoneNumber.getText().toString();	
		LsimNumber2 =  editTextPhoneNumber2.getText().toString();	
		LsimUSSD = editTextCodeRequestBalans.getText().toString();
		LsimPassword = editTextNumberPassword.getText().toString();
		LdeviceVersion = spinnerSelectDevice.getSelectedItemPosition()+1;
		
		LisDailyReport=checkBoxDaylyReport.isChecked();
		LisAutoAlarm=checkBoxAutoPowerOnAlarm.isChecked();
		LisAutoOnRele1=checkBoxAutoPowerOnRele1Alarm.isChecked();
		LisSMSPostanovka=checkBoxSMSPostanovka.isChecked();
		LisSMSSnjatie=checkBoxSMSSnjatie.isChecked();
		LisSMSInIncoming = checkBoxIsSMSInIncoming.isChecked();
		
		LisSIM1 = ActiveSIM.isChecked();
		
		if(settingsDev.isPhoneNumberValid(LsimNumber))
		{
			LnumberTmpSensorSMS=Integer.parseInt(editTextNumberSensorTMPSMS.getText().toString());
			if(LnumberTmpSensorSMS>0)
			{
				LnotificationSMS.lower_bound=Integer.parseInt(editTextLowerBoundTmp.getText().toString());
				LnotificationSMS.upper_bound=Integer.parseInt(editTextUpperBoundTmp.getText().toString());
			}
			
			LtitleSMSAlarmZone = editTextSMSHead.getText().toString();
			LtextSMSAlarmZone = editTextSMSAarmZone.getText().toString();
			
			//add others....
			//add commands to queue
			
			settingsDev.clearQueueCommands();
			settingsDev.password = LsimPassword;
			
			if(isEditNumberPasswordHasFocus)
			{
				//show dialog
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle("Смена пароля");
				b.setMessage("Изменить настройки в");
				b.setPositiveButton("программе", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	isSendNewPassword = false;
			        	saveParamStep2();
			        }
			      });
				b.setNegativeButton("устройстве", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	isSendNewPassword = true;
			        	saveParamStep2();
			        }});
				b.setCancelable(false);
				b.show();
			}
			else
				saveParamStep2();
		}
 	}
	
	private void saveParamStep2()
	{
		boolean isAnyway = false;
		
		if(isSendNewPassword && isEditNumberPasswordHasFocus)
			settingsDev.AddSetSimPasswordCommand(LsimPassword,true);
		
		settingsDev.AddSetUSSDCommand(LsimUSSD, isAnyway);
		settingsDev.AddSetDailyReportCommand(LisDailyReport, isAnyway);
		settingsDev.AddSetIsSMSPostanovkaCommand(LisSMSPostanovka, isAnyway);
		settingsDev.AddSetIsSMSSnjatieCommand(LisSMSSnjatie, isAnyway);
		settingsDev.AddSetActiveZone(LzoneValue, isAnyway);
		settingsDev.AddSetIsAutoOnRele1(LisAutoOnRele1, isAnyway);
		
		
		if(LnumberTmpSensorSMS>0)
			settingsDev.AddSetNotificationSMSCommand(LnotificationSMS.upper_bound, LnotificationSMS.lower_bound, LnumberTmpSensorSMS, isAnyway);
		else
			settingsDev.AddSetNotificationSMSCommand(0, 0, 0, isAnyway);
		
		settingsDev.AddSetAoutPowerOnSyrenCommand(LisAutoAlarm, isAnyway);
		
		
		if(isChangeSIMDevice) settingsDev.AddSwitchSimCommand(LisSIM1);
		
		
		
		if(settingsDev.sms_to_send.size() > 0)
		{
			//save parameters
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.setTitle("Установка параметров");
			b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
			b.setPositiveButton("Отправить", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		    		
		        	//copy from local to class variable
		        	
		        	titleSMSAlarmZone = LtitleSMSAlarmZone;
		        	textSMSAlarmZone = LtextSMSAlarmZone;
		        	
		        	simNumber2 = LsimNumber2;
		        	simNumber = LsimNumber;
		        	simUSSD = LsimUSSD;
		        	simPassword = LsimPassword;
		        	
		        	isDailyReport = LisDailyReport;
		        	isSMSPostanovka = LisSMSPostanovka;
		        	isSMSSnjatie = LisSMSSnjatie;
		        	isAutoAlarm = LisAutoAlarm;
		        	isAutoOnRele1 = LisAutoOnRele1;
		        	
		        	isSIM1 = LisSIM1;
		        	isSMSInIncoming = LisSMSInIncoming;
		        	
		        	numberTmpSensorSMS = LnumberTmpSensorSMS;
		        	if(numberTmpSensorSMS>0)
		    			notificationSMS[numberTmpSensorSMS-1] = LnotificationSMS;
		        	
		        	for(int i=0;i<4;i++)
		        		zoneValue[i] = LzoneValue[i];
		        	//
		        	
		    		
		    		settings.setTextSMSTitle(titleSMSAlarmZone);
		    		settings.setTextSMSAlarm(textSMSAlarmZone);
		        	
		    		run_sms_sender=true;
		    		//set parameters to device
		    		settings.setNumberSIM2(simNumber2);
		    		
		    		settings.setNumberSIM(simNumber);
		    		settings.setUSSDSIM(simUSSD);
		    		settings.setPinSIM(simPassword);
		    		if(settingsDev.isSimNumberValid())
		    		{
			    		settings.setIsDailyReport(isDailyReport);
			    		settings.setIsSMSPostanovka(isSMSPostanovka);
			    		settings.setIsSMSSnjatie(isSMSSnjatie);
			    		settings.setIsAutoPowerOnAlarm(isAutoAlarm);
			    		settings.setIsSetAutoRele1Control(isAutoOnRele1);
			    		settings.setActiveZoneValue(zoneValue);
			    		settings.setNumberTmpSensorSMS(numberTmpSensorSMS);
			    		settings.setIsSMSInIncoming(isSMSInIncoming);
			    		
			    		
			    		if(numberTmpSensorSMS>0)
			    			settings.setCNotificationSMS(numberTmpSensorSMS-1, notificationSMS[numberTmpSensorSMS-1]);
	
			    		//first launch
			    		settings.setIsSendAnyway(false);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Установка параметров");
			    		settingsDev.sendCommands();	
			    		
			    		viewOkCancel.setVisibility(View.GONE);
			    		isOKSend = true;
			    		pd.show();
			    		setRequestCodeBalanse();
		    		}
		    		else
		    			loadParam();
		        }
		      });
			b.setNegativeButton("Отмена", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        	loadParam();
		        	isOKSend = false;
		        	viewOkCancel.setVisibility(View.GONE);
		        }});
			b.show();
		}
		else
		{
			//save parameters
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.setTitle("Принять изменения?");
			b.setPositiveButton("Ok", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	isSIM1 = LisSIM1;
		        	isSMSInIncoming = LisSMSInIncoming;
		        	settings.setIsFirstSim(isSIM1);
		    		simNumber =  editTextPhoneNumber.getText().toString();
		    		settings.setNumberSIM(simNumber);
		    		simNumber2 =  editTextPhoneNumber2.getText().toString();
		    		settings.setNumberSIM2(simNumber2);
		    		simPassword = LsimPassword;
		    		settings.setPinSIM(simPassword);
		    		titleSMSAlarmZone = editTextSMSHead.getText().toString();
		    		settings.setTextSMSTitle(titleSMSAlarmZone);
		    		textSMSAlarmZone = editTextSMSAarmZone.getText().toString();
		    		settings.setTextSMSAlarm(textSMSAlarmZone);
		    		settings.setIsSMSInIncoming(isSMSInIncoming);
		    		viewOkCancel.setVisibility(View.GONE);
		    		settings.setPinSIM(LsimPassword); 
		    		simPassword = LsimPassword;
		    		
		    		deviceVersion = LdeviceVersion;
		    		settings.setDevVersion(deviceVersion);
		    		if(deviceVersion != 3)
		    		{
			    		settings.setNumberReleWarm(2);
		    		}
		    		setVisiblePassword();
		    		setRequestCodeBalanse();
		        }
		      });
			b.setNegativeButton("Отмена", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	ActiveSIM.setChecked(isSIM1);
		        	simPassword = settings.getPinSIM();
		        	LsimPassword = simPassword;
		        	LdeviceVersion = deviceVersion;
		        	spinnerSelectDevice.setSelection(LdeviceVersion-1);
		        	editTextNumberPassword.setText(simPassword);
		        	editTextPhoneNumber.setText(simNumber);
		        	editTextPhoneNumber2.setText(simNumber2);
		        	editTextSMSHead.setText(titleSMSAlarmZone);
		        	editTextSMSAarmZone.setText(textSMSAlarmZone);
		        	checkBoxIsSMSInIncoming.setChecked(isSMSInIncoming);
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        	viewOkCancel.setVisibility(View.GONE);
		        	setRequestCodeBalanse();
		        }});
			b.show();
		}
		isEditNumberPasswordHasFocus = false;
	}
	
	public void HideKeyboard()
	{
		try
		{
			InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);	
		}
		catch(Exception e){}
	}
	
	public void OnClickCancelButton(View v)
	{
		HideKeyboard();
		loadParam();
	}
	
	public void onClickSendButton(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickSendButton");
		HideKeyboard();
		saveParam();	
	}
	
	
	CounterTimer timer = new CounterTimer(1000, 1000);
	ProgressDialog pd = null;
	static Handler hpd =null;
	public void LoadProgressDialog(int count_sms,String title)
	{
		timer = new CounterTimer(settingsDev.getTimeNeededToSend()*1000, 1000); 
		pd = new ProgressDialog(this);
	    pd.setTitle(title);
	    timer.setTitle(title);
	    timer.setProgressDialog(pd);
	      // меняем стиль на индикатор
	      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	      // устанавливаем максимум
	      pd.setMax(count_sms);
	      // включаем анимацию ожидания
	      pd.setIndeterminate(true);
	      pd.setCancelable(false);
	      timer.start();
	      hpd = new Handler() {
	        public void handleMessage(Message msg) {
	          // выключаем анимацию ожидания
	          pd.setIndeterminate(false);
	          if (pd.getProgress() < pd.getMax()) {
	            // увеличиваем значения индикаторов
	            pd.incrementProgressBy(1);
	            pd.incrementSecondaryProgressBy(1);
	          } else {
	        	timer.cancel();
	            pd.dismiss();
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	            Toast.makeText(getApplicationContext(),"Все команды успешно отправлены",Toast.LENGTH_LONG).show();

	            if(isShowDeviceVersion)
	            {
	            	settings = new CSettingsPref(getSharedPreferences(MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS));
            		deviceVersion = settings.getDevVersion();
            		spinnerSelectDevice.setSelection(deviceVersion-1);
            		switch(deviceVersion)
            		{
            		case BaseActivity.deviceAfter01112012:
            			Toast.makeText(getApplicationContext(),"Ваша версия устройства после 01 11 2012",Toast.LENGTH_LONG).show();
            			break;
            		case BaseActivity.deviceBefore01112012:
            			Toast.makeText(getApplicationContext(),"Ваша версия устройства до 01 11 2012",Toast.LENGTH_LONG).show();
            			break;
            		case BaseActivity.deviceBefore01112011:
            			Toast.makeText(getApplicationContext(),"Ваша версия устройства до 01 11 2011",Toast.LENGTH_LONG).show();
            			break;
            		}
            		
	            }
	            //set final settings
	            if(isOKSend) settings.setIsFirstSim(isSIM1);
	            setVisiblePassword();
	            setRequestCodeBalanse();
	          }
	        }
	      };
	      
	      settingsDev.setHandlerDialog(hpd);
	}
	
	public void OnButtonDestenationRele13(View v)
	{
		HideKeyboard();
		Log.i(TAG_events,"settings rele dest");
		Intent intent = new Intent(this, FunctionReleActivity.class);
	    startActivity(intent);
	}
	
	public void OnButtonFunctionTmpSensor(View v)
	{
		HideKeyboard();
		Log.i(TAG_events,"settings tmp sensor function");
		Intent intent = new Intent(this, FunctionTmpSensorActivity.class);
	    startActivity(intent);
	}
	
	static int zoneValue[] = {1,1,1,1};
	static int LzoneValue[] = {1,1,1,1};
	public void OnButtonZone1(View v)
	{
		if(LzoneValue[0]<3) LzoneValue[0]++;
		else LzoneValue[0] = 0;
		TextView zone=(TextView)findViewById(R.id.buttonZone1);
		zone.setText(String.format("%d", LzoneValue[0]));
	}
	
	public void OnButtonZone2(View v)
	{
		if(LzoneValue[1]<3) LzoneValue[1]++;
		else LzoneValue[1] = 0;
		TextView zone=(TextView)findViewById(R.id.buttonZone2);
		zone.setText(String.format("%d", LzoneValue[1]));
	}
	
	public void OnButtonZone3(View v)
	{
		if(LzoneValue[2]<3) LzoneValue[2]++;
		else LzoneValue[2] = 0;
		TextView zone=(TextView)findViewById(R.id.buttonZone3);
		zone.setText(String.format("%d", LzoneValue[2]));
	}
	
	public void OnButtonZone4(View v)
	{
		if(LzoneValue[3]<3) LzoneValue[3]++;
		else LzoneValue[3] = 0;
		TextView zone=(TextView)findViewById(R.id.buttonZone4);
		zone.setText(String.format("%d", LzoneValue[3]));
	}
	
	public void OnClickAdditionalNumbers(View v)
	{
		HideKeyboard();
		Log.i(TAG_events,"start number broadcast cofiguration");
		Intent intent = new Intent(this, NumbersBroadcastActivity.class);
	    startActivity(intent);
	}
	
	public void OnButtonAddAutoRequest(View v)
	{
		HideKeyboard();
		Log.i(TAG_events,"RequestSystemActivity");
		Intent intent = new Intent(this, RequestSystemActivity.class);
	    startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(br);
	  }
	
	@Override  
	public void onBackPressed() {
	    super.onBackPressed();   
	    // Do extra stuff here

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		if(loaded_complete)
		{
			//view ok and cancel buttons
			checkStateOkCancel();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(loaded_complete)
		{
			//view ok and cancel buttons
			checkStateOkCancel();
		}
	}
	
	boolean isChangeSIMDevice = false;
	public void onClickToggleButtonActiveSIM(View v)
	{
		if(loaded_complete)
		{
			//view ok and cancel buttons
			checkStateOkCancel();
			//show dialog
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Переключение SIM");
			b.setMessage("Изменить настройки в");
			b.setPositiveButton("программе", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	isChangeSIMDevice = false;
		        }
		      });
			b.setNegativeButton("устройстве", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	isChangeSIMDevice = true;
		        }});
			b.show();
		}
	}
	
	public void OnbuttonRequestDevVerion(View v)
	{
		
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Определить версию устройcтва?");
		b.setPositiveButton("Да", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	Toast.makeText(getApplicationContext(),"Опред. вер. устройcтва",Toast.LENGTH_LONG).show();
	        	settingsDev.clearQueueCommands();
	        	settingsDev.AddRequestReportCommand();
	        	LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Опред. вер. устройcтва");
	        	
	    		if(settingsDev.sendCommands())
	    		{	pd.show(); isShowDeviceVersion = true;}
	        }
	      });
		b.setNegativeButton("Отмена", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	        }});
		b.show();
		Log.i(TAG_events,"request report");
	}
}
