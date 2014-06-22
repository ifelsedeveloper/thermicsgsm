package com.therm.thermicscontrol;

//import java.util.Locale;

import java.util.Locale;

import com.therm.thermicscontrol.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.content.SharedPreferences;
import android.widget.TextView;
import android.view.View.OnLongClickListener;


public class SettingsParamActivity extends BaseActivity {

	public static final String TAG_events="event_tag_settings_param";
	public SystemConfig settings=null;
	public CSettingsDev settingsDev=null;
	SeekBar sbWeightRele;
	SeekBar sbWeightReleNight;
	EditText editNTmpSensorRele;
	EditText editNRele;
	BroadcastReceiver br;
	BroadcastReceiver brTimer;
	View viewOkCancel;
	EditText editTextTmpReleWarm;
	EditText editTextTmpReleWarmNight;
	ImageView []automatic = new ImageView[3];
	CheckBox []checkBoxRele = new CheckBox[3];
	ImageView imageViewStateTimer;
	
	LinearLayout linearLayoutDayTemp;
	LinearLayout linearLayoutNightTemp;
	RelativeLayout RelativeLayoutReleWarm;
	RelativeLayout RelativeLayoutNTmpSensor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings_param);
		
		sbWeightRele = (SeekBar) findViewById(R.id.seekBarReleWarm);
		sbWeightReleNight = (SeekBar) findViewById(R.id.seekBarReleWarmNight);
		editNTmpSensorRele=(EditText) findViewById(R.id.editTextNTmpSensorRele);
		linearLayoutDayTemp = (LinearLayout) findViewById(R.id.layoutSeekBarReleWarm);
		linearLayoutNightTemp = (LinearLayout) findViewById(R.id.layoutSeekBarReleWarmNight);
		RelativeLayoutReleWarm = (RelativeLayout) findViewById(R.id.RelativeLayoutReleWarm);
		RelativeLayoutNTmpSensor = (RelativeLayout) findViewById(R.id.RelativeLayoutNTmpSensor);
		layoutToggleButtons = (LinearLayout)findViewById(R.id.layotSwitchButton);
		//number_tmp_sensor = settings.getNumberSensorReleWarm();
		
		loadSystemSettings();
		automatic[0] = (ImageView)findViewById(R.id.imageViewAutomatic1);
	    automatic[1] = (ImageView)findViewById(R.id.imageViewAutomatic2);
	    automatic[2] = (ImageView)findViewById(R.id.imageViewAutomatic3);
	    checkBoxRele[0] =(CheckBox) findViewById(R.id.checkBoxRele1);
	    checkBoxRele[1] =(CheckBox) findViewById(R.id.checkBoxRele2);
	    checkBoxRele[2] =(CheckBox) findViewById(R.id.checkBoxRele3);
	    imageViewStateTimer = (ImageView)findViewById(R.id.imageViewStateTimer);
	    editTextTmpReleWarm = (EditText) findViewById(R.id.editTextTmpRele);
	    editTextTmpReleWarmNight = (EditText) findViewById(R.id.editTextTmpReleNight);
	    editNRele = (EditText) findViewById(R.id.editTextNRele);
	    
		loadParam();
		
		Log.i(TAG_events, "creating activity");
//		settings.setIsTimerRunning(2,true);
//		

//		boolean rest = settings.getIsTimerRunning();
		// создаем BroadcastReceiver
	    brTimer = new BroadcastReceiver() {
	      // действия при получении сообщений
	      public void onReceive(Context context, Intent intent) {
	    	  loadSystemSettings();
	    	  setTimerState();
	      }
	    };

	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFiltTimer = new IntentFilter(TimerActionService.ATTRIBUTE_ACTION);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(brTimer, intFiltTimer);
	    
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
	        loadSystemSettings();
	        setReleState();
	        for(int i =0; i<3; i++)
	        {
		        if(intent.hasExtra(BaseActivity.prefIsRele[i]))
		        {
			        isRele[i] = intent.getBooleanExtra(BaseActivity.prefIsRele[i], settings.getIsRele(i));
			        settings.setIsRele(i,isRele[i]);
			        setCheckRele(isRele);
		        }  
	        }
	      }
	    };
	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION_RCVSMS);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(br, intFilt);
	    
	    viewOkCancel = findViewById(R.id.layoutOKCancelReleWarm);
	    //setVisibility(mSampleContent.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	    //viewOkCancel.setVisibility(View.INVISIBLE);
	    viewOkCancel.setVisibility(View.GONE);
	    //editTextTmpReleWarm.setVisibility(View.GONE);
	    sbWeightRele.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	    	@Override
	  	  public void onProgressChanged(SeekBar seekBar, int progress,
	  	      boolean fromUser) {
	  			value_seek_rele = progress + min_tmp;
	  			editTextTmpReleWarm.setText(String.format("%d", value_seek_rele));
	  			dayValueTemp = value_seek_rele;
	  			setValueForHotButton(newTempSelectedButton, dayValueTemp, nightValueTemp);
	  			mInitializedTempSensor[numberSensorTMPReleWarm] = true;
	  			newTempDayHotKeys[numberSensorTMPReleWarm][newTempSelectedButton] = value_seek_rele;
	  			//Log.i(TAG_events,String.format("%d", value_seek_rele));
	  	  }

	  	  @Override
	  	  public void onStartTrackingTouch(SeekBar seekBar) {
	  		  //viewOkCancel.setVisibility(View.INVISIBLE);
	  		  viewOkCancel.setVisibility(View.GONE);
	  	  }

	  	  @Override
	  	  public void onStopTrackingTouch(SeekBar seekBar) {  
	  		  if(value_seek_rele!=tmp_rele_warm || value_seek_rele_night!=tmp_rele_warm_night)
	  		  {

	  			viewOkCancel.setVisibility(View.VISIBLE);
	  		  }
	  	  }
	    }
	    );
	    
	    
	    sbWeightReleNight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	    	@Override
	  	  public void onProgressChanged(SeekBar seekBar, int progress,
	  	      boolean fromUser) {
	  			value_seek_rele_night = progress + min_tmp;
	  			editTextTmpReleWarmNight.setText(String.format("%d", value_seek_rele_night));
	  			nightValueTemp = value_seek_rele_night;
	  			setValueForHotButton(newTempSelectedButton, dayValueTemp, nightValueTemp);
	  			newTempNightHotKeys[numberSensorTMPReleWarm][newTempSelectedButton] = value_seek_rele_night;
	  			//Log.i(TAG_events,String.format("%d", value_seek_rele));
	  	  }

	  	  @Override
	  	  public void onStartTrackingTouch(SeekBar seekBar) {
	  		  //viewOkCancel.setVisibility(View.INVISIBLE);
	  		  viewOkCancel.setVisibility(View.GONE);
	  	  }

	  	  @Override
	  	  public void onStopTrackingTouch(SeekBar seekBar) {  
	  		  if(value_seek_rele!=tmp_rele_warm || value_seek_rele_night!=tmp_rele_warm_night)
	  		  {

	  			viewOkCancel.setVisibility(View.VISIBLE);
	  		  }
	  	  }
	    }
	    );
	    
	    setVisibilityScrollRegulator();
	    
	    setTimerState();
	    
	}

	private void loadSystemSettings() {
		//create for work with shared preference
		settings=SystemConfigDataSource.getActiveSystem();
		SystemConfig.phone_number = settings.getNumberSIM();
		settingsDev= new CSettingsDev(settings,getApplicationContext());
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    
	    setTimerState();
	}
	
	private void setTimerState()
	{
		if(settings.getIsEnableTimer())
		{
			if(settings.getIsTimerRunning())	
				imageViewStateTimer.setImageDrawable(getResources().getDrawable(R.drawable.play_timer));
			else
				imageViewStateTimer.setImageDrawable(getResources().getDrawable(R.drawable.pause_timer));
		}
	    else
	    {
	    	settings.setIsTimerRunningFalse();
	    	imageViewStateTimer.setImageDrawable(getResources().getDrawable(R.drawable.stop_timer));
	    }
	}
	
	public void setVisibilityScrollRegulator()
	{
		if(numberSensorTMPReleWarm==0)
		{
			linearLayoutDayTemp.setVisibility(View.GONE);
			linearLayoutNightTemp.setVisibility(View.GONE);
			layoutToggleButtons.setVisibility(View.GONE);
		}
		else
		{
			linearLayoutDayTemp.setVisibility(View.VISIBLE);
			linearLayoutNightTemp.setVisibility(View.VISIBLE);
			layoutToggleButtons.setVisibility(View.VISIBLE);
		}
		if(settings.getDevVersion() == 1)
		{
			linearLayoutDayTemp.setVisibility(View.VISIBLE);
			linearLayoutNightTemp.setVisibility(View.GONE);
			RelativeLayoutReleWarm.setVisibility(View.GONE);
			//RelativeLayoutNRele.setVisibility(View.GONE);
		}
		if(settings.getDevVersion() == 2)
		{
			RelativeLayoutReleWarm.setVisibility(View.GONE);
			//RelativeLayoutNTmpSensor.setVisibility(View.GONE);
		}
	}
	
	static int min_tmp=-55;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	static int value_seek_rele;
	int tmp_rele_warm;
	
	static int value_seek_rele_night;
	int tmp_rele_warm_night;
	
	//int number_tmp_sensor=1;
	String function_tmp_sensor;
	//load parameters from system and display it on activity
	public void loadParam()
	{
		LoadButtons();

		numberRele = settings.getNumberReleWarm();
		numberSensorTMPReleWarm=settings.getNumberSensorReleWarm();		
		tmp_rele_warm=settings.getTempDayConfig(numberSensorTMPReleWarm,newTempSelectedButton);
		tmp_rele_warm_night=settings.getTempNightConfig(numberSensorTMPReleWarm,newTempSelectedButton);
		value_seek_rele = tmp_rele_warm;
		value_seek_rele_night = tmp_rele_warm_night;
		for(int nrele=0; nrele<SystemConfig.numReles; nrele++)
			isRele[nrele]=settings.getIsRele(nrele);
		
		isUpr=settings.getIsUpr();
		
		TextView text  = (TextView) findViewById(R.id.editTextTmpRele);
		text.setText(Integer.toString(tmp_rele_warm));
		
		text  = (TextView) findViewById(R.id.editTextTmpReleNight);
		text.setText(Integer.toString(tmp_rele_warm_night));
		
		sbWeightRele.setProgress(tmp_rele_warm-min_tmp);
		sbWeightReleNight.setProgress(tmp_rele_warm_night-min_tmp);
		
		SetFunctionSensorRele();
		
		
		//rele and control
		text  = (TextView) findViewById(R.id.titleRele1);
		text.setText(settings.getFunctionRele1());
		
		text  = (TextView) findViewById(R.id.titleRele2);
		text.setText(settings.getFunctionRele2());
		
		text  = (TextView) findViewById(R.id.titleRele3);
		text.setText(settings.getFunctionRele3());
		
		text  = (TextView) findViewById(R.id.titleUpr);
		text.setText(settings.getFunctionUpr());
		
		editNTmpSensorRele.setText(Integer.toString(numberSensorTMPReleWarm));
		editNRele.setText(Integer.toString(numberRele));

		for(int i=0; i<isRele.length; i++)
		{
			checkBoxRele[i].setChecked(isRele[i]);
			checkBoxRele[i].setOnCheckedChangeListener(listener);
		}
		
		CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxUpr);
		checkbox.setChecked(isUpr);
		
		setReleState();
	    
	    
	}
	
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		 
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			setCheckRele(isRele);
		}
		};
	
	public void setReleState()
	{
		setEnableReleN(1,true);
		setEnableReleN(0,!settings.getIsSetAutoRele1Control());
	    setEnableReleN(2,!settings.getIsAutoPowerOnAlarm());
		
		if(numberSensorTMPReleWarm==0)
			setEnableReleN(1,true);
		else
		{
			setEnableReleN(numberRele-1,false);
		}
	}
	
	public void setCheckRele(boolean[] isRele)
	{
		for(int i=0; i < isRele.length; i++)
		if(isEnableRele[i]) 
		{
			checkBoxRele[i].setChecked(isRele[i]);
		}
	}
	
	
	public void SetFunctionSensorRele()
	{
		TextView text;
		if(numberSensorTMPReleWarm==0)
		{
			function_tmp_sensor="Датчик не задан";
			findViewById(R.id.seekBarReleWarm).setEnabled(false);
			text  = (TextView) findViewById(R.id.titleFunctionTmpSensorReleWarm);
			text.setText(function_tmp_sensor);
		}
		else
		{
			function_tmp_sensor = settings.getFunctionTmpSensor(numberSensorTMPReleWarm-1);
			function_tmp_sensor = String.format("%s, C", function_tmp_sensor);
			text  = (TextView) findViewById(R.id.titleFunctionTmpSensorReleWarm);
			text.setText(function_tmp_sensor);
			findViewById(R.id.seekBarReleWarm).setEnabled(true);
		}
		
		
	}
	LinearLayout layoutToggleButtons;
	public void setSeekBar()
	{ 
		if(numberSensorTMPReleWarm != settings.getNumberSensorReleWarm() || (value_seek_rele!=tmp_rele_warm) || numberRele != settings.getNumberReleWarm())
			viewOkCancel.setVisibility(View.VISIBLE );
		else
			viewOkCancel.setVisibility(View.GONE);
		
		
		if(numberSensorTMPReleWarm==0 && settings.getDevVersion() == 1)
		{
			linearLayoutDayTemp.setVisibility(View.GONE);
			linearLayoutNightTemp.setVisibility(View.GONE);
			layoutToggleButtons.setVisibility(View.GONE);
		}
		else
		{
			setVisibilityScrollRegulator();
			
			value_seek_rele = newTempDayHotKeys[numberSensorTMPReleWarm][newTempSelectedButton];
			value_seek_rele_night = newTempNightHotKeys[numberSensorTMPReleWarm][newTempSelectedButton];

			sbWeightRele.setProgress(value_seek_rele-min_tmp);
			sbWeightReleNight.setProgress(value_seek_rele_night-min_tmp);
			
		}
		
	}
	
	boolean SendCommandsToDevice = false;
	public void OnClickOkButton(View v)
	{
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Изменить настройки в");
		b.setPositiveButton("программе", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	SendCommandsToDevice = false;
	        	saveParam2();
	        }
	      });
		b.setNegativeButton("устройстве", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	SendCommandsToDevice = true;
	        	saveParam2();
	        }});
		b.show();
		
	}
	public void OnClickCancelButton(View v)
	{
		LoadButtons();
		SetFunctionSensorRele();
		setSeekBar();
		setReleState();
		viewOkCancel.setVisibility(View.GONE);
	}
	
	boolean isEnableRele[] = new boolean[3];
	public void setEnableReleN(int nrele, boolean value)
	{
		isEnableRele[nrele] = value;
		CheckBox checkbox = checkBoxRele[nrele];
		
		if(value)
		{
			//enable
			checkbox.setChecked(settings.getIsRele(0));
			checkbox.setClickable(true);
			automatic[nrele].setVisibility(View.GONE);
		}
		else
		{
			//disable		
			checkbox.setChecked(false);
			checkbox.setClickable(false);
			automatic[nrele].setVisibility(View.VISIBLE);
		}
	}
	
	
	public int numberSensorTMPReleWarm=2;
	static public final int maxNTmpSensor = 6;
	static public final int minNTmpSensor = 0;
	
	public void onClickIncNTmpSensorRele(View v)
	{
		if(numberSensorTMPReleWarm >= minNTmpSensor && numberSensorTMPReleWarm <maxNTmpSensor)
		{
			numberSensorTMPReleWarm++;
			editNTmpSensorRele.setText(String.format("%d", numberSensorTMPReleWarm));
			//change title and function
			SetFunctionSensorRele();
			setSeekBar();
			UpdateValueButtons();
			
			for(int i = 0; i<7;i++) mInitializedTempSensor[i] = false;
		}
	}
	
	public void onClickDecNTmpSensorRele(View v)
	{
		if(numberSensorTMPReleWarm > minNTmpSensor && numberSensorTMPReleWarm <=maxNTmpSensor)
		{
			numberSensorTMPReleWarm--;
			editNTmpSensorRele.setText(String.format("%d", numberSensorTMPReleWarm));
			//change title and function
			SetFunctionSensorRele();
			setSeekBar();
			UpdateValueButtons();
		}
	}
	
	public int numberRele=2;
	static public final int maxNRele = 3;
	static public final int minNRele = 1;
	
	public void onClickIncNRele(View v)
	{
		if(numberRele <= 0 || numberRele >= 4) numberRele = 1;
		if(numberRele >= minNRele && numberRele <maxNRele)
		{
			numberRele++;
			editNRele.setText(String.format("%d", numberRele));
			//change title and function
			SetFunctionSensorRele();
			setSeekBar();
		}
	}
	
	public void onClickDecNRele(View v)
	{
		if(numberRele <= 0 || numberRele >= 4) numberRele = 1;
		if(numberRele > minNRele && numberRele <=maxNRele)
		{
			numberRele--;
			editNRele.setText(String.format("%d", numberRele));
			//change title and function
			SetFunctionSensorRele();
			setSeekBar();
		}
	}
	
	//save parameters to system
	public void saveParam2()
	{
		EditText edittext=(EditText) findViewById(R.id.editTextNTmpSensorRele);
		numberSensorTMPReleWarm=Integer.parseInt(edittext.getText().toString());
		edittext=(EditText) findViewById(R.id.editTextNRele);
		numberRele=Integer.parseInt(edittext.getText().toString());
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		//set parameters to device
		//add commands to queue
		boolean isAnyway = false;
		settingsDev.clearQueueCommands();
		if(settingsDev.isSimNumberValid())
		{
			if(SendCommandsToDevice)
			{
				settingsDev.AddSetNumberSensorReleWarmCommand(numberSensorTMPReleWarm,numberRele, false);
				if(numberSensorTMPReleWarm!=0)
					settingsDev.AddSetTmpReleCommand(numberSensorTMPReleWarm-1,value_seek_rele,value_seek_rele_night,numberRele, isAnyway);
			}
			

				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle("Установить параметры реле нагревателя?");
				//b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
				b.setPositiveButton("Установить", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	//set hot keys state
			        	settings.setNTempConfig(newTempSelectedButton);
			        	oldTempSelectedButton = newTempSelectedButton;
			        	
			        	if(numberSensorTMPReleWarm!=0)
			    		{
			        		tmp_rele_warm = value_seek_rele;
			        		tmp_rele_warm_night = value_seek_rele_night;
			        		newTempDayHotKeys[numberSensorTMPReleWarm][newTempSelectedButton] = tmp_rele_warm;
			        		newTempNightHotKeys[numberSensorTMPReleWarm][newTempSelectedButton] = tmp_rele_warm_night;
			        		for(int nsensor = 0; nsensor < SystemConfig.numberSensors; nsensor++)
			        		{
			        			for(int nbutton = 0; nbutton < SystemConfig.numberButtons; nbutton++)
			        			{
			        				settings.setTempDayConfig(nsensor, nbutton, newTempDayHotKeys[nsensor][nbutton]);
			        				settings.setTempNightConfig(nsensor, nbutton, newTempNightHotKeys[nsensor][nbutton]);
			        				oldTempNightHotKeys[nsensor][nbutton] = newTempNightHotKeys[nsensor][nbutton];
			        				oldTempDayHotKeys[nsensor][nbutton] = newTempDayHotKeys[nsensor][nbutton];;
			        			}
			        		}
			        	}
			        	
			        	if(!SendCommandsToDevice)
						{
			        		settings.setNumberSensorReleWarm(numberSensorTMPReleWarm);
							settings.setNumberReleWarm(numberRele);
						}
			        	
						if(settingsDev.sms_to_send.size() > 0)
						{
				        	settings.setNumberSensorReleWarm(numberSensorTMPReleWarm);
				    		settings.setNumberReleWarm(numberRele);
				        	//viewOkCancel.setVisibility(View.INVISIBLE);
				        	viewOkCancel.setVisibility(View.GONE);
				    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Настройка термостата");
				    		settingsDev.sendCommands();		
				    		setReleState();
				    		pd.show();
						}
						else
						{
							viewOkCancel.setVisibility(View.GONE);
						}
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        }});
				b.show();
			}

		
		
 	}
	

	public boolean[] isRele=new boolean[3];
	public void onClickCheckBoxRele1(View v)
	{
		ClickRele(0);
	}
	
	public void onClickCheckBoxRele2(View v)
	{
		ClickRele(1);
	}
	
	public void onClickCheckBoxRele3(View v)
	{
		ClickRele(2);
	}
	
	public void ClickRele(final int nrele)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxBoiler");
		final boolean isReleN=(!isRele[nrele]);
		final String titleProgress;
		final String questionDialog;
		final String actionDialog;
		final String neutralActionDialog;
		if(isReleN)
		{
			titleProgress = String.format(Locale.ENGLISH,"Включение реле №%d", nrele+1);
			questionDialog = String.format(Locale.ENGLISH,"Включить реле №%d", nrele+1);
			actionDialog = "Включить";
			neutralActionDialog = "Вкл. 60 сек";
		}
		else
		{
			titleProgress = String.format(Locale.ENGLISH,"Отключение реле №%d", nrele+1);
			questionDialog = String.format(Locale.ENGLISH,"Отключить реле №%d", nrele+1);
			actionDialog = "Отключить";
			neutralActionDialog = "";
		}
		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		
		if(settingsDev.isSimNumberValid())
		{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	settingsDev.AddSetReleNCommand(nrele+1, isReleN, true);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				if(isReleN)
				{
					b.setNeutralButton(neutralActionDialog, new OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
				    		
				        	final Handler handler = new Handler();
				        	handler.postDelayed(new Runnable() {
				        	  @Override
				        	  public void run() {
				        		  isRele[nrele] = false;
				        		  checkBoxRele[nrele].setChecked(isRele[nrele]);
				        	  }
				        	}, 60000);
				        	
				        	settingsDev.AddSetReleNCommand(nrele+1, isReleN, 60,true);
				    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
				    		settingsDev.sendCommands();		
				    		pd.show();
				        }
				      });
				}
				
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        	checkBoxRele[nrele].setChecked(isRele[nrele]);
			        }});
				b.show();

		}
	}
	
	public void onClickFrameCheckBox1(View v)
	{
		VklRele60(0);
		//Toast.makeText(getApplicationContext(), "test 2", Toast.LENGTH_LONG).show();
	}
	
	public void onClickFrameCheckBox2(View v)
	{
		VklRele60(1);
		//Toast.makeText(getApplicationContext(), "test 2", Toast.LENGTH_LONG).show();
	}
	
	public void onClickFrameCheckBox3(View v)
	{
		VklRele60(2);
		//Toast.makeText(getApplicationContext(), "test 2", Toast.LENGTH_LONG).show();
	}
	
	private void VklRele60(final int nrele)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxBoiler");
		final String titleProgress;
		final String questionDialog;
		final String actionDialog;

		titleProgress = String.format(Locale.ENGLISH,"Включение реле №%d", nrele+1);
		questionDialog = String.format(Locale.ENGLISH,"Включить реле №%d", nrele+1);
		actionDialog = "Вкл. 60 сек";

		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		
		if(settingsDev.isSimNumberValid())
		{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	final Handler handler = new Handler();
			        	handler.postDelayed(new Runnable() {
			        	  @Override
			        	  public void run() {
			        		  isRele[nrele] = false;
			        		  checkBoxRele[nrele].setChecked(isRele[nrele]);
			        	  }
			        	}, 60000);
			        	
			        	settingsDev.AddSetReleNCommand(nrele+1, true, 60,true);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

			        }});
				b.show();

		}
	}
	
	public boolean isUpr = false;
	public void onClickCheckBoxUpr(View v)
	{
		if(settingsDev.isSimNumberValid())
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			Log.i(TAG_events,"onClickCheckBoxBoiler");
			isUpr=(!isUpr);
			final String titleProgress;
			final String questionDialog;
			final String actionDialog;
			if(isUpr)
			{
				titleProgress = "Включение управления";
				questionDialog = "Включить управление?";
				actionDialog = "Включить";
			}
			else
			{
				titleProgress = "Отключение управления";
				questionDialog = "Отключить управление?";
				actionDialog = "Отключить";
			}
			//set parameters to device
			//add commands to queue
			settingsDev.clearQueueCommands();
		
			settingsDev.AddSetUpravlenieCommand(isUpr);
		
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			    		
			    		//set parameters to preference 
			    		settings.setIsUpr(isUpr);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        	isUpr=(!isUpr);
			        	CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxUpr);
			        	checkbox.setChecked(isUpr);
			        }});
				b.show();
			}
		}
		else
		{
			CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxUpr);
			checkbox.setChecked(isUpr);
		}
	}

	public void onClickCallMicrophone(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		if(settingsDev.isSimNumberValid())
		{
			settingsDev.AddSetCallMicrophoneCommand();
			
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle("Вызвать устройство на связь?");
				b.setPositiveButton("Ok", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			    		
			    		//set parameters to preference 
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Вызов устройства");
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        }});
				b.show();
			}
		}
	}
	
	public boolean isTimer=false;
	public void onClickCheckBoxTimer(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxisTimer");
		isTimer=(!isTimer);
		final String questionDialog;
		final String actionDialog;
		if(isTimer)
		{
			questionDialog = "Включить таймер?";
			actionDialog = "Включить";
		}
		else
		{
			questionDialog = "Отключить таймер?";
			actionDialog = "Отключить";
		}
		

			//save parameters
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.setTitle(questionDialog);
			b.setPositiveButton(actionDialog, new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        		
		        }
		      });
			b.setNegativeButton("Отмена", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	isTimer=(!isTimer);
		        	CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxTimer);
		        	checkbox.setChecked(isTimer);
		        }});
			b.show();
	}
	
	public void onClickLayoutCheckBoxTimer(View v)
	{
		Intent intent = new Intent(this, TimersListActivity.class);
	    startActivity(intent);
	}
	
	boolean [] statesButtons = new boolean[4];
	Button [] valuesButtons = new Button[4];
	
	void LoadButtons()
	{
		valuesButtons[0] = (Button)findViewById(R.id.buttonSwitch1);
		valuesButtons[1] = (Button)findViewById(R.id.buttonSwitch2);
		valuesButtons[2] = (Button)findViewById(R.id.buttonSwitch3);
		valuesButtons[3] = (Button)findViewById(R.id.buttonSwitch4);
		

		valuesButtons[0].setOnLongClickListener(new OnLongClickListener() {		
				@Override
				public boolean onLongClick(View v) {
					//call change value
					ShowDialogTempPicker(0);
					return false;
				}
			});
			
		valuesButtons[1].setOnLongClickListener(new OnLongClickListener() {		
			@Override
			public boolean onLongClick(View v) {
				//call change value
				ShowDialogTempPicker(1);
				return false;
			}
		});
		
		valuesButtons[2].setOnLongClickListener(new OnLongClickListener() {		
			@Override
			public boolean onLongClick(View v) {
				//call change value
				ShowDialogTempPicker(2);
				return false;
			}
		});
		
		valuesButtons[3].setOnLongClickListener(new OnLongClickListener() {		
			@Override
			public boolean onLongClick(View v) {
				//call change value
				ShowDialogTempPicker(3);
				return false;
			}
		});
		numberSensorTMPReleWarm = settings.getNumberSensorReleWarm(); 
		oldTempSelectedButton = newTempSelectedButton = settings.getNTempConfig();
		dayValueTemp = settings.getTempDayConfig(numberSensorTMPReleWarm, newTempSelectedButton);
		nightValueTemp = settings.getTempNightConfig(numberSensorTMPReleWarm, newTempSelectedButton);
		setValueForHotButton(newTempSelectedButton, dayValueTemp, nightValueTemp);

		for(int nsensor = 0; nsensor < SystemConfig.numberSensors; nsensor++)
		{
			for(int nbutton = 0; nbutton < SystemConfig.numberButtons ; nbutton++)
			{
				oldTempDayHotKeys[nsensor][nbutton] = newTempDayHotKeys[nsensor][nbutton] = settings.getTempDayConfig(nsensor, nbutton);
				oldTempNightHotKeys[nsensor][nbutton] = newTempNightHotKeys[nsensor][nbutton] = settings.getTempNightConfig(nsensor, nbutton); 
				if(nbutton != newTempSelectedButton && nsensor == numberSensorTMPReleWarm)
				{
					setValueForHotButton(nbutton, oldTempDayHotKeys[nsensor][nbutton], oldTempNightHotKeys[nsensor][nbutton]);
					statesButtons[nbutton] = false;
				}
			}
		}
		
		statesButtons[newTempSelectedButton] = true;
		
		DisplayStatesButtons();
		UpdateValueButtons();
	}
	
	void UpdateValueButtons()
	{
		int nsensor = numberSensorTMPReleWarm;
		for(int nbutton = 0; nbutton < SystemConfig.numberButtons ; nbutton++)
		{ 
			setValueForHotButton(nbutton, newTempDayHotKeys[nsensor][nbutton], newTempNightHotKeys[nsensor][nbutton]);
			statesButtons[nbutton] = false;
		}

	}
	
	void setValueForHotButton(int n, int temp_day, int temp_night)
	{	
		if(settings.getDevVersion() == 1)
			valuesButtons[n].setText(String.format("%d", temp_day));
		else
			valuesButtons[n].setText(String.format("%d\n%d", temp_day, temp_night));
	}
	
	final Context context = this;
	TextView dialogTempNight;
	TextView dialogTempDay;
	int nightValueTemp = 0;
	int dayValueTemp = 0;
	int nConfigTempTuning = 0;
	
	int [][]newTempDayHotKeys = new int[7][4];
	int [][]newTempNightHotKeys = new int[7][4];
	int newTempSelectedButton = 0;
	
	int newLastTempDayHotKeys;
	int newLastTempNightHotKeys;
	
	int [][]oldTempDayHotKeys = new int[7][4];
	int [][]oldTempNightHotKeys = new int[7][4];
	int oldTempSelectedButton = 1;
	
	boolean [] mInitializedTempSensor = new boolean[7];
	
	static int REP_DELAY = 50;
	private final int maximumValue = 99;
	private final int minimumValue = -55;
	private boolean mAutoIncrementNight = false;
	private boolean mAutoDecrementNight = false;
	private Handler repeatUpdateHandler= new Handler();
	
	class RptUpdater implements Runnable {
	    public void run() {
	        if( mAutoIncrementNight ){
	        	IncrementNight();
	        	repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        } else if( mAutoDecrementNight ){
	        	DecrementNight();
	        	repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        } else if( mAutoIncrementDay ){
	        	IncrementDay();
	        	repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        } else if( mAutoDecrementDay ){
	        	DecrementDay();
	        	repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
	        }
	        
	    }
	}
	
	void IncrementNight()
	{
		if( nightValueTemp < maximumValue)
		{
			nightValueTemp++;
			dialogTempNight.setText(Integer.toString(nightValueTemp));
		}
	}
	
	void DecrementNight()
	{
		if(nightValueTemp > minimumValue)
		{
			nightValueTemp--;
			dialogTempNight.setText(Integer.toString(nightValueTemp));
		}
	}
	
	private boolean mAutoIncrementDay = false;
	private boolean mAutoDecrementDay = false;
	
	void IncrementDay()
	{
		if( dayValueTemp < maximumValue)
		{
			dayValueTemp++;
			dialogTempDay.setText(Integer.toString(dayValueTemp));
		}
	}
	
	void DecrementDay()
	{
		if(dayValueTemp > minimumValue)
		{
			dayValueTemp--;
			dialogTempDay.setText(Integer.toString(dayValueTemp));
		}
	}
	
	public void ShowDialogTempPicker(int n)
	{
		nConfigTempTuning = n;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		// custom dialog
		final Dialog dialog = new Dialog(context,R.style.cust_dialog);
		dialog.setTitle("Установка значений регулятора");
		dialog.setContentView(R.layout.dialog_temerature_picker);
		dialogTempNight = (TextView) dialog.findViewById(R.id.titleNightTemp);
		dialogTempDay = (TextView) dialog.findViewById(R.id.titleDayTemp);
		
		dayValueTemp = newTempDayHotKeys[numberSensorTMPReleWarm][n];
		nightValueTemp = newTempNightHotKeys[numberSensorTMPReleWarm][n];
		dialogTempNight.setText(Integer.toString(nightValueTemp));
		dialogTempDay.setText(Integer.toString(dayValueTemp));
		
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}

		});
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOkTempPick);
		
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// OK
				//save parameters		
				newTempDayHotKeys[numberSensorTMPReleWarm][nConfigTempTuning] = dayValueTemp;
				newTempNightHotKeys[numberSensorTMPReleWarm][nConfigTempTuning] = nightValueTemp;
				
				if(newTempSelectedButton == nConfigTempTuning)
				{
					//set progress bar
					sbWeightRele.setProgress(dayValueTemp-min_tmp);
					sbWeightReleNight.setProgress(nightValueTemp-min_tmp);
					
					mInitializedTempSensor[numberSensorTMPReleWarm] = true;
					newLastTempDayHotKeys = newTempDayHotKeys[numberSensorTMPReleWarm][nConfigTempTuning];
					newLastTempNightHotKeys = newTempNightHotKeys[numberSensorTMPReleWarm][nConfigTempTuning];
					if( tmp_rele_warm_night != nightValueTemp || dayValueTemp != tmp_rele_warm)
						viewOkCancel.setVisibility(View.VISIBLE);
				}

				//view ok/cancel
				boolean isShowOkCancel = false;
				for(int i=0; i<4; i++)
					if(newTempDayHotKeys[i] != oldTempDayHotKeys[i] || newTempNightHotKeys[i] != oldTempNightHotKeys[i])
					{
						isShowOkCancel = true;
						break;
					}
				
				if(isShowOkCancel)
					viewOkCancel.setVisibility(View.VISIBLE);
				
				setValueForHotButton(nConfigTempTuning, dayValueTemp, nightValueTemp);
				dialog.dismiss();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});
		
		
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancelTempPick);
		
		dialogButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Cancel
				dialog.dismiss();
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		});
		
		boolean isHideNightRegulator = (settings.getDevVersion() == 1);
		//imageViewNightSetter
		ImageView imageViewNightSetter = (ImageView) dialog.findViewById(R.id.imageViewNightSetter);
		if(isHideNightRegulator)
		{
			imageViewNightSetter.setVisibility(View.GONE);
			dialogTempNight.setVisibility(View.GONE);
		}
		else
		{
			imageViewNightSetter.setVisibility(View.VISIBLE);
			dialogTempNight.setVisibility(View.VISIBLE);
		}
		
		
		dialogButton = (Button) dialog.findViewById(R.id.buttonNightPlusTempReg);
		
		if(isHideNightRegulator)
		{
			dialogButton.setVisibility(View.GONE);
		}
		else
		{
			dialogButton.setVisibility(View.VISIBLE);
		
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					IncrementNight();
				}
			});
			
			//speed up increment
			dialogButton.setOnLongClickListener( 
		            new View.OnLongClickListener(){
		                public boolean onLongClick(View arg0) {
		                	mAutoIncrementNight = true;
		                    repeatUpdateHandler.post( new RptUpdater() );
		                    return false;
		                }
		            }
		    );   

			dialogButton.setOnTouchListener( new View.OnTouchListener() {
		        public boolean onTouch(View v, MotionEvent event) {
		            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
		                    && mAutoIncrementNight ){
		            	mAutoIncrementNight = false;
		            }
		            return false;
		        }
		    });
		}
		
		dialogButton = (Button) dialog.findViewById(R.id.buttonNightMinusTempReg);
		if(isHideNightRegulator)
		{
			dialogButton.setVisibility(View.GONE);
		}
		else
		{
			dialogButton.setVisibility(View.VISIBLE);
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// dec night
					DecrementNight();
				}
			});
			
			//speed up decrement
			dialogButton.setOnLongClickListener( 
		            new View.OnLongClickListener(){
		                public boolean onLongClick(View arg0) {
		                	mAutoDecrementNight = true;
		                    repeatUpdateHandler.post( new RptUpdater() );
		                    return false;
		                }
		            }
		    );   

			dialogButton.setOnTouchListener( new View.OnTouchListener() {
		        public boolean onTouch(View v, MotionEvent event) {
		            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
		                    && mAutoDecrementNight ){
		            	mAutoDecrementNight = false;
		            }
		            return false;
		        }
		    });
		}
			
			dialogButton = (Button) dialog.findViewById(R.id.buttonDayMinusTempReg);

			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// dec day
					DecrementDay();
				}
			});
			
			//speed up decrement
			dialogButton.setOnLongClickListener( 
		            new View.OnLongClickListener(){
		                public boolean onLongClick(View arg0) {
		                	mAutoDecrementDay = true;
		                    repeatUpdateHandler.post( new RptUpdater() );
		                    return false;
		                }
		            }
		    );   

			dialogButton.setOnTouchListener( new View.OnTouchListener() {
		        public boolean onTouch(View v, MotionEvent event) {
		            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
		                    && mAutoDecrementDay ){
		            	mAutoDecrementDay = false;
		            }
		            return false;
		        }
		    });
			
			dialogButton = (Button) dialog.findViewById(R.id.buttonDayPlusTempReg);
			
			dialogButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// inc day
					IncrementDay();
				}
			});
			
			//speed up increment
			dialogButton.setOnLongClickListener( 
		            new View.OnLongClickListener(){
		                public boolean onLongClick(View arg0) {
		                	mAutoIncrementDay = true;
		                    repeatUpdateHandler.post( new RptUpdater() );
		                    return false;
		                }
		            }
		    );   

			dialogButton.setOnTouchListener( new View.OnTouchListener() {
		        public boolean onTouch(View v, MotionEvent event) {
		            if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL) 
		                    && mAutoIncrementDay ){
		            	mAutoIncrementDay = false;
		            }
		            return false;
		        }
		    });
	 
		dialog.show();
	}

	
	public void DisplayStatesButtons()
	{
		for(int i = 0; i < 4;i++)
		{
			if(statesButtons[i])
				valuesButtons[i].setBackgroundResource(R.drawable.button_hover);
			else
				valuesButtons[i].setBackgroundResource(R.drawable.button_white);;
		}
	}
	
	public void EnableButton(int n)
	{
		setValueForHotButton(n, newTempDayHotKeys[numberSensorTMPReleWarm][n], newTempNightHotKeys[numberSensorTMPReleWarm][n]);
		newTempSelectedButton = n;
		int i;
		for(i=0;i<4;i++)
			statesButtons[i] = false;
		statesButtons[newTempSelectedButton] = true;
		
		DisplayStatesButtons();
		
		int dayTemp = newTempDayHotKeys[numberSensorTMPReleWarm][n];
		int nightTemp = newTempNightHotKeys[numberSensorTMPReleWarm][n];
		
		sbWeightRele.setProgress(dayTemp-min_tmp);
		sbWeightReleNight.setProgress(nightTemp-min_tmp);

		if( dayTemp != settings.getTempDayConfig(numberSensorTMPReleWarm,n) || nightTemp != settings.getTempNightConfig(numberSensorTMPReleWarm,n) || newTempSelectedButton != settings.getNTempConfig())
			viewOkCancel.setVisibility(View.VISIBLE);
	}
	
	public void onClickButtonSwitch1(View v)
	{
		EnableButton(0);
	}
	
	public void onClickButtonSwitch2(View v)
	{
		EnableButton(1);
	}
	
	public void onClickButtonSwitch3(View v)
	{
		EnableButton(2);
	}
	
	public void onClickButtonSwitch4(View v)
	{
		EnableButton(3);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(br);
	    unregisterReceiver(brTimer);
	  }
	
	CounterTimer timer = new CounterTimer(30000, 1000);
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
	            
	          }
	        }
	      };
	      
	      settingsDev.setHandlerDialog(hpd);
	}
	
	public void onClickButtonRele1(View v)
	{
		PowerOnRele(1);
	}
	public void onClickButtonRele2(View v)
	{
		PowerOnRele(2);
	}
	public void onClickButtonRele3(View v)
	{
		PowerOnRele(3);
	}
	
	private void PowerOnRele(final int nrele)
	{
		String title = String.format(Locale.ENGLISH,"Вкл реле №%d на 60 секунд?", nrele);
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setTitle(title);
		b.setPositiveButton("Вкл", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	    		settingsDev.VklRele(nrele, 60);
	        }
	      });
		b.setNegativeButton("Отмена", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	
	        }});
		b.show();
	}
	
	//AutoRequest
	public void onClickLayoutCheckBoxAutoRequest(View v) {
		Log.i(TAG_events,"RequestSystemActivity");
		Intent intent = new Intent(this, RequestSystemActivity.class);
	    startActivity(intent);
	}
	
	private boolean isAutoRequest = true;
	public void onClickCheckBoxAutoRequest(View v) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxisTimer");
		isTimer=(!isTimer);
		final String questionDialog;
		final String actionDialog;
		if(isTimer)
		{
			questionDialog = "Включить автозапрос?";
			actionDialog = "Включить";
		}
		else
		{
			questionDialog = "Отключить автозапрос?";
			actionDialog = "Отключить";
		}
		

			//save parameters
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.setTitle(questionDialog);
			b.setPositiveButton(actionDialog, new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        		
		        }
		      });
			b.setNegativeButton("Отмена", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	isAutoRequest=(!isAutoRequest);
		        	//CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxAutoRequest);
		        	//checkbox.setChecked(isAutoRequest);
		        }});
			b.show();
	}
	
}
