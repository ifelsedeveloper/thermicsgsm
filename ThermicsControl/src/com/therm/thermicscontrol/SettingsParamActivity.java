package com.therm.thermicscontrol;

//import java.util.Locale;

import com.therm.thermicscontrol.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.content.SharedPreferences;
import android.widget.TextView;

public class SettingsParamActivity extends BaseActivity {

	public static final String TAG_events="event_tag_settings_param";
	public CSettingsPref settings=null;
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
	ImageView automatic1;
	ImageView automatic2;
	ImageView automatic3;
	ImageView imageViewStateTimer;
	
	LinearLayout linearLayoutDayTemp;
	LinearLayout linearLayoutNightTemp;
	
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
		//number_tmp_sensor = settings.getNumberSensorReleWarm();
		
		//create for work with shared preference
		settings=new CSettingsPref(getSharedPreferences(MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS));
		CSettingsPref.phone_number = settings.getNumberSIM();
		settingsDev= new CSettingsDev(settings,getApplicationContext());
		automatic1 = (ImageView)findViewById(R.id.imageViewAutomatic1);
	    automatic2 = (ImageView)findViewById(R.id.imageViewAutomatic2);
	    automatic3 = (ImageView)findViewById(R.id.imageViewAutomatic3);
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
	    	  int n_rele = intent.getIntExtra(TimerActionService.ATTRIBUTE_NRELE, 0); 
	    	  boolean value = intent.getBooleanExtra(TimerActionService.ATTRIBUTE_VKL, false); 
	    	  settings.setIsTimerRunning(n_rele,value);
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
	        setReleState();
	        if(intent.hasExtra(BaseActivity.prefIsRele1))
	        {
		        isRele1 = intent.getBooleanExtra(BaseActivity.prefIsRele1, settings.getIsRele1());
		        settings.setIsRele1(isRele1);
		        setCheckRele(isRele1,isRele2,isRele3);
		        
	        }
	        
	        if(intent.hasExtra(BaseActivity.prefIsRele2))
	        {
		        isRele2 = intent.getBooleanExtra(BaseActivity.prefIsRele2, settings.getIsRele2());
		        settings.setIsRele2(isRele2);
		        setCheckRele(isRele1,isRele2,isRele3);
		        
	        }
	        
	        if(intent.hasExtra(BaseActivity.prefIsRele3))
	        {
		        isRele3 = intent.getBooleanExtra(BaseActivity.prefIsRele3, settings.getIsRele3());
		        settings.setIsRele3(isRele3);
		        setCheckRele(isRele1,isRele2,isRele3);
		        
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
	    
	    
	    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	        @Override
	        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            Log.e("Alert","Lets See if it Works settings param !!!");
	            Toast.makeText(getApplicationContext(), "Error settings param", Toast.LENGTH_LONG).show();
	        }
	    });
	    
	    setVisibilityScrollRegulator();
	    
	    setTimerState();
	    
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
		}
		else
		{
			linearLayoutDayTemp.setVisibility(View.VISIBLE);
			linearLayoutNightTemp.setVisibility(View.VISIBLE);
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
		//load parameters from preference
		//tmp_water=settings.getTmpWater();
		//tmp_air=settings.getTmpAir();
		numberRele = settings.getNumberReleWarm();
		numberSensorTMPReleWarm=settings.getNumberSensorReleWarm();		
		tmp_rele_warm=settings.getTmpReleWarm(numberSensorTMPReleWarm-1);
		tmp_rele_warm_night=settings.getTmpReleWarmNight(numberSensorTMPReleWarm-1);
		value_seek_rele = tmp_rele_warm;
		value_seek_rele_night = tmp_rele_warm_night;
		isRele1=settings.getIsRele1();
		isRele2=settings.getIsRele2();
		isRele3=settings.getIsRele3();
		isUpr=settings.getIsUpr();
		
		//isMicrophone=settings.getIsMicrophone();
		//Log.i(TAG_events,"isRele1="+ Boolean.toString(isRele1));
		//Log.i(TAG_events,"isRele2="+ Boolean.toString(isRele2));
		//set parameters to view
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
		
		
		
		CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele1);
		checkbox.setChecked(isRele1);
		checkbox.setOnCheckedChangeListener(listener);
		
		checkbox=(CheckBox) findViewById(R.id.checkBoxRele2);
		checkbox.setChecked(isRele2);
		checkbox.setOnCheckedChangeListener(listener);
		
		checkbox=(CheckBox) findViewById(R.id.checkBoxRele3);
		checkbox.setChecked(isRele3);
		checkbox.setOnCheckedChangeListener(listener);
		
		checkbox=(CheckBox) findViewById(R.id.checkBoxUpr);
		checkbox.setChecked(isUpr);
//		checkbox=(CheckBox) findViewById(R.id.checkBoxMicrophone);
//		checkbox.setChecked(isMicrophone);
		
		
		setReleState();
	    
	    
	}
	
	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		 
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			setCheckRele(isRele1, isRele2, isRele3);
		}
		};
	
	public void setReleState()
	{
		setEnableReleN1(true);
		setEnableReleN2(true);
		setEnableReleN3(true);
		
		setEnableReleN1(!settings.getIsSetAutoRele1Control());
	    setEnableReleN3(!settings.getIsAutoPowerOnAlarm());
		
		if(numberSensorTMPReleWarm==0)
			setEnableReleN2(true);
		else
		{
			switch(numberRele)
			{
				case 1:
					setEnableReleN1(false);
				break;
				case 2:
					setEnableReleN2(false);
					break;
				case 3:
					setEnableReleN3(false);
					break;
			}
			
		}
	}
	
	public void setCheckRele(boolean isRele1, boolean isRele2, boolean isRele3)
	{
		CheckBox checkbox;
		
		if(isEnableRele1) 
		{
			checkbox =(CheckBox) findViewById(R.id.checkBoxRele1);
			checkbox.setChecked(isRele1);
		}
		
		if(isEnableRele2) 
		{
			checkbox =(CheckBox) findViewById(R.id.checkBoxRele2);
			checkbox.setChecked(isRele2);
		}
		
		if(isEnableRele3) 
		{
			checkbox =(CheckBox) findViewById(R.id.checkBoxRele3);
			checkbox.setChecked(isRele3);
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
	
	public void setSeekBar()
	{
		if(numberSensorTMPReleWarm != settings.getNumberSensorReleWarm() || (value_seek_rele!=tmp_rele_warm) || numberRele != settings.getNumberReleWarm())
			viewOkCancel.setVisibility(View.VISIBLE );
		else
			viewOkCancel.setVisibility(View.GONE);
		
		if(numberSensorTMPReleWarm==0)
		{
			linearLayoutDayTemp.setVisibility(View.GONE);
			linearLayoutNightTemp.setVisibility(View.GONE);
		}
		else
		{
			linearLayoutDayTemp.setVisibility(View.VISIBLE);
			linearLayoutNightTemp.setVisibility(View.VISIBLE);
			value_seek_rele =settings.getTmpReleWarm(numberSensorTMPReleWarm-1);
			value_seek_rele_night =settings.getTmpReleWarmNight(numberSensorTMPReleWarm-1);
			sbWeightRele.setProgress(value_seek_rele-min_tmp);
			sbWeightReleNight.setProgress(value_seek_rele_night-min_tmp);
		}
		
	}
	
	public void OnClickOkButton(View v)
	{
		saveParam2();
	}
	public void OnClickCancelButton(View v)
	{
		numberSensorTMPReleWarm=settings.getNumberSensorReleWarm();
		numberRele = settings.getNumberReleWarm();
		editTextTmpReleWarm.setText(String.format("%d", tmp_rele_warm));	
		editNTmpSensorRele.setText(String.format("%d", numberSensorTMPReleWarm));
		editNRele.setText(String.format("%d", numberRele));
		sbWeightRele.setProgress(tmp_rele_warm-min_tmp);

		//viewOkCancel.setVisibility(View.INVISIBLE);
		viewOkCancel.setVisibility(View.GONE);
		SetFunctionSensorRele();
		setSeekBar();
		setReleState();
	}
	
	boolean isEnableRele1 = false;
	public void setEnableReleN1(boolean value)
	{
		isEnableRele1 = value;
		CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele1);
		if(value)
		{
			//enable
			checkbox.setChecked(settings.getIsRele1());
			checkbox.setClickable(true);
			automatic1.setVisibility(View.GONE);
		}
		else
		{
			//disable		
			checkbox.setChecked(false);
			checkbox.setClickable(false);
			automatic1.setVisibility(View.VISIBLE);
		}
	}
	
	boolean isEnableRele2 = false;
	public void setEnableReleN2(boolean value)
	{
		isEnableRele2 = value;
		CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele2);
		if(value)
		{
			//enable
			checkbox.setChecked(settings.getIsRele2());
			checkbox.setClickable(true);
			automatic2.setVisibility(View.GONE);
		}
		else
		{
			//disable		
			checkbox.setChecked(false);
			checkbox.setClickable(false);
			automatic2.setVisibility(View.VISIBLE);
		}
	}
	
	boolean isEnableRele3 = false;
	public void setEnableReleN3(boolean value)
	{
		isEnableRele3 = value;
		CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele3);
		if(value)
		{
			//enable
			checkbox.setChecked(settings.getIsRele3());
			checkbox.setClickable(true);
			automatic3.setVisibility(View.GONE);
		}
		else
		{
			//disable		
			checkbox.setChecked(false);
			checkbox.setClickable(false);
			automatic3.setVisibility(View.VISIBLE);
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
		boolean isAnyway = true;
		settingsDev.clearQueueCommands();
		if(settingsDev.isSimNumberValid())
		{
			settingsDev.AddSetNumberSensorReleWarmCommand(numberSensorTMPReleWarm,numberRele, false);
			if(numberSensorTMPReleWarm!=0)
			settingsDev.AddSetTmpReleCommand(numberSensorTMPReleWarm-1,value_seek_rele,value_seek_rele_night,numberRele, isAnyway);
			
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle("Установить параметры реле нагревателя?");
				//b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
				b.setPositiveButton("Установить", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	settings.setNumberSensorReleWarm(numberSensorTMPReleWarm);
			    		settings.setNumberReleWarm(numberRele);
			        	//viewOkCancel.setVisibility(View.INVISIBLE);
			        	viewOkCancel.setVisibility(View.GONE);
			        	if(numberSensorTMPReleWarm!=0)
			    		{
			        		tmp_rele_warm = value_seek_rele;
			        		tmp_rele_warm_night = value_seek_rele_night;
			        		settings.setTmpReleWarm(numberSensorTMPReleWarm-1,tmp_rele_warm);
			        		settings.setTmpReleWarmNight(numberSensorTMPReleWarm-1,tmp_rele_warm_night);
			        	}
			        	
			    		
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Настройка термостата");
			    		settingsDev.sendCommands();		
			    		setReleState();
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
	

	
	public boolean isRele1=true;
	public void onClickCheckBoxRele1(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxLight");
		isRele1=(!isRele1);
		final String titleProgress;
		final String questionDialog;
		final String actionDialog;
		if(isRele1)
		{
			titleProgress = "Включение реле №1";
			questionDialog = "Включить реле №1?";
			actionDialog = "Включить";
		}
		else
		{
			titleProgress = "Отключение реле №1";
			questionDialog = "Отключить реле №1?";
			actionDialog = "Отключить";
		}
		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		if(settingsDev.isSimNumberValid())
		{
			settingsDev.AddSetReleNCommand(1, isRele1, true);
			
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			    		
			    		//set parameters to preference 
			    		//settings.setIsRele1(isRele1);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        	isRele1=(!isRele1);
			        	CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele1);
			        	checkbox.setChecked(isRele1);
			        }});
				b.show();
			}
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
	
	public boolean isRele2=true;
	public void onClickCheckBoxRele2(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxBoiler");
		isRele2=(!isRele2);
		final String titleProgress;
		final String questionDialog;
		final String actionDialog;
		if(isRele2)
		{
			titleProgress = "Включение реле №2";
			questionDialog = "Включить реле №2?";
			actionDialog = "Включить";
		}
		else
		{
			titleProgress = "Отключение реле №2";
			questionDialog = "Отключить реле №2?";
			actionDialog = "Отключить";
		}
		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		settingsDev.AddSetReleNCommand(2, isRele2, true);
		if(settingsDev.isSimNumberValid())
		{
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			    		
			    		//set parameters to preference 
			    		//settings.setIsRele2(isRele2);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        	isRele2=(!isRele2);
			        	CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele2);
			        	checkbox.setChecked(isRele2);
			        }});
				b.show();
			}
		}
	}
	
	public boolean isRele3=false;
	public void onClickCheckBoxRele3(View v)
	{
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickCheckBoxAlarm");
		isRele3=(!isRele3);
		final String titleProgress;
		final String questionDialog;
		final String actionDialog;
		if(isRele3)
		{
			titleProgress = "Включение реле №3";
			questionDialog = "Включить реле №3?";
			actionDialog = "Включить";
		}
		else
		{
			titleProgress = "Отключение реле №3";
			questionDialog = "Отключить реле №3?";
			actionDialog = "Отключить";
		}
		//set parameters to device
		//add commands to queue
		settingsDev.clearQueueCommands();
		if(settingsDev.isSimNumberValid())
		{
			settingsDev.AddSetReleNCommand(3, isRele3, true);
			
			if(settingsDev.sms_to_send.size() > 0)
			{
				//save parameters
				final AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setTitle(questionDialog);
				b.setPositiveButton(actionDialog, new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			    		
			    		//set parameters to preference 
			    		//settings.setIsRele3(isRele3);
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,titleProgress);
			    		settingsDev.sendCommands();		
			    		pd.show();
			        }
			      });
				b.setNegativeButton("Отмена", new OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) {
			        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			        	isRele3=(!isRele3);
			        	CheckBox checkbox=(CheckBox) findViewById(R.id.checkBoxRele3);
			        	checkbox.setChecked(isRele3);
			        }});
				b.show();
			}
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
			    		Toast.makeText(getApplicationContext(), "Ожидайте входящего вызова", Toast.LENGTH_LONG);
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
		if(isRele3)
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
}
