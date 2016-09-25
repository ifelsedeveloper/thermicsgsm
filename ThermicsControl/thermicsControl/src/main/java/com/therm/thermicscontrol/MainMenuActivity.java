package com.therm.thermicscontrol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.sql.Date;
import java.text.SimpleDateFormat;


public class MainMenuActivity extends BaseActivity   {

	public static final String TAG_events="event_tag_main_menu";
	public SystemConfig settings=null;
	public CSettingsDev settingsDev=null;
	public boolean clickControlAlarm =false;
	BroadcastReceiver br;
	BroadcastReceiver br_clear_unreaded_sms;
	BroadcastReceiver brSystemSelectionChanged;
	public static int current_local_num_not = 0;
	private SlidingMenu slidingMenu ;
	TextView textNameSystem;
	
	ListView listViewSystems;
	SystemConfigDataSource systemConfig_DB;
	SystemConfigSympleAdapter systemConfigSCAdapter;
	Cursor systemConfigCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Remove title bar
		try{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			//Remove notification bar
			//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_main_menu);

			// configure the SlidingMenu
			slidingMenu = new SlidingMenu(this);
			slidingMenu.setMode(SlidingMenu.LEFT);
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
			slidingMenu.setShadowDrawable(R.drawable.shadow);
			slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			slidingMenu.setFadeDegree(0.35f);
			slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
			slidingMenu.setMenu(R.layout.menu);
			slidingMenu.setSlidingEnabled(true);

			//create for work with shared preference
			settings=SystemConfigDataSource.getActiveSystem();
			settingsDev=new CSettingsDev(settings,getApplicationContext());
			textNameSystem = (TextView) findViewById(R.id.textNameSystem);
			
			setTitleLastReport(settings.getLastSystemReport(),settings.getLastSystemReportTime());
			// создаем BroadcastReceiver
			br = new BroadcastReceiver() {
				// действия при получении сообщений
				public void onReceive(Context context, Intent intent) {
					//change title message reader
					current_local_num_not++;
					setTitleMessageButton(current_local_num_not);

					String sms = intent.getStringExtra(BaseActivity.PARAM_SMS);
					String time = intent.getStringExtra(BaseActivity.PARAM_SMSTIME);
					Log.d(TAG_events, "onReceive sms: "+sms+" ;time = "+time);
					if(!clickControlAlarm) Toast.makeText(getApplicationContext(),sms,Toast.LENGTH_LONG).show();

					//отправляем полученное сообщение нашему классу
					settingsDev.recvSMS(sms);
					settings=SystemConfigDataSource.getActiveSystem();
					settingsDev=new CSettingsDev(settings,getApplicationContext());
					isGuardian = settings.getIsGuardian();
					SetCheckedGuardian(settings.getIsGuardian());
					
					String lastReport = intent.getStringExtra(BaseActivity.prefLastSystemReport);
					long date = intent.getLongExtra(BaseActivity.prefLastSystemReportTime, -1);

					setTitleLastReport(lastReport,date);
				}
			};
			// создаем фильтр для BroadcastReceiver
			IntentFilter intFilt = new IntentFilter(BaseActivity.BROADCAST_ACTION_RCVSMS);
			// регистрируем (включаем) BroadcastReceiver
			registerReceiver(br, intFilt);



			//new broadcast
			// создаем BroadcastReceiver
			br_clear_unreaded_sms = new BroadcastReceiver() {
				// действия при получении сообщений
				public void onReceive(Context context, Intent intent) {
					current_local_num_not = 0;
					SystemConfig.clearNumNotification();
					setTitleMessageButton(0);
				}
			};
			// создаем фильтр для BroadcastReceiver
			IntentFilter intFilt2 = new IntentFilter(BaseActivity.BROADCAST_ACTION_CLEARUNREADEDSMS);
			// регистрируем (включаем) BroadcastReceiver
			registerReceiver(br_clear_unreaded_sms, intFilt2);

			brSystemSelectionChanged = new BroadcastReceiver(){
				// действия при смене системы
				public void onReceive(Context context, Intent intent) {
					SystemChanged();
				}
			};

			IntentFilter intentFilter = new IntentFilter(SystemConfigDataSource.SYSTEM_SELECTION_CHANGED);
			// регистрируем (включаем) BroadcastReceiver
			registerReceiver(brSystemSelectionChanged, intentFilter);

			//load parameters for system
			loadConfigParam();  

			listViewSystems = (ListView) findViewById(R.id.listSystems);
			listViewSystems.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			listViewSystems.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
					view.setSelected(true);

				}
			});

			systemConfig_DB = SystemConfigDataSource.sharedInstanceSystemConfigDataSource();
			systemConfig_DB.open();

			// получаем курсор
			systemConfigCursor = systemConfig_DB.getAllData();
			//startManagingCursor(systemConfigCursor);

			// формируем столбцы сопоставления
			String[] from = new String[] { SystemConfigSQLiteHelper.COLUMN_NAME };
			int[] to = new int[] 		 { R.id.textViewSystemName };

			// создааем адаптер и настраиваем список
			systemConfigSCAdapter = new SystemConfigSympleAdapter(this,R.layout.system_row,systemConfigCursor, from, to, 0, listViewSystems,systemConfig_DB);


		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}

	}

	private void SystemChanged()
	{
		settings=SystemConfigDataSource.getActiveSystem();
		settingsDev=new CSettingsDev(settings,getApplicationContext());
		setTitleLastReport(settings.getLastSystemReport(),settings.getLastSystemReportTime());
		loadConfigParam();
	}

	public void onClickViewMenuGlobalx(View v)
	{
		if(slidingMenu != null){
			Log.d(TAG_events,"contact information");
			slidingMenu.showMenu(true);
		} else {
			slidingMenu = new SlidingMenu(this);
			slidingMenu.setMode(SlidingMenu.LEFT);
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
			slidingMenu.setShadowDrawable(R.drawable.shadow);
			slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			slidingMenu.setFadeDegree(0.35f);
			slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
			slidingMenu.setMenu(R.layout.menu);
			slidingMenu.setSlidingEnabled(true);
		}
	}

	public void onClickCreateSystem(View v)
	{
		systemConfigSCAdapter.addNewSystem();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void setTitleMessageButton(int num_msg)
	{
		Button textViewTitle = (Button) findViewById(R.id.buttonWithImageMessages);

		if(current_local_num_not>0)
			textViewTitle.setText(String.format("Сообщения (%d)", current_local_num_not));
		else
			textViewTitle.setText("Сообщения");
	}

	public void SetCheckedGuardian(boolean value)
	{
		Button textViewTitle = (Button) findViewById(R.id.buttonWithImageGuardian);
		if(value)
		{
			final Drawable drawableTop = getResources().getDrawable(R.drawable.secure);
			textViewTitle.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
		}
		else
		{
			final Drawable drawableTop = getResources().getDrawable(R.drawable.secure_open);
			textViewTitle.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop , null, null);
		}
	}

	public void setTitleLastReport(String lastRep, long time)
	{
		if(time>0 && lastRep!=null ){
			TextView lastReport = (TextView) findViewById(R.id.TextViewLastReport);
			lastReport.setText(lastRep);
			String dateString;

			if(lastRep.length()>0)
				dateString = new SimpleDateFormat("dd MM yyyy в HH:mm").format(new Date(time));
			else
				dateString = "--//--//---- --:--";

			TextView TitleLastReport = (TextView) findViewById(R.id.TextViewLastReportTitle);
			TitleLastReport.setText(dateString);
		}
	}


	public void onClickConfigSystem(View v)
	{
		Log.i(TAG_events,"config system");
		Intent intent = new Intent(this, ConfigSystemActivity.class);
		startActivity(intent);
	}

	public void onClickLayoutLabelThermix(View v)
	{
		Log.i(TAG_events,"contact information");
		Intent intent = new Intent(this, ContactInformationActivity.class);
		startActivity(intent);
	}

	public void onClickSettingsParam(View v)
	{
		Log.i(TAG_events,"settings param");
		Intent intent = new Intent(this, SettingsParamActivity.class);
		startActivity(intent);
	}

	public void onClickReportSystem(View v)
	{
		Log.i(TAG_events,"report system");
		Intent intent = new Intent(this, MessageSystemActivity.class);
		setTitleMessageButton(0);
		startActivity(intent);
	}

	public void onClickRequestReport(View v)
	{
		count_reques = 0;

		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Запросить отчёт?");
		b.setPositiveButton("Да", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(),"Запрос отчёта",Toast.LENGTH_LONG).show();
				settingsDev.clearQueueCommands();
				settingsDev.AddRequestReportCommand();
				LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Запрос отчёта");
				if(settingsDev.sendCommands())
					pd.show();
			}
		});
		b.setNegativeButton("Отмена", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}});
		b.show();
		Log.i(TAG_events,"request report");
	}

	public void onClickRequestBalans(View v)
	{
		count_reques = 0;
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Запросить баланс?");
		b.setPositiveButton("Да", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(),"Запрос баланса сим карты",Toast.LENGTH_LONG).show();
				settingsDev.clearQueueCommands();
				settingsDev.AddRequestBalansCommand();
				LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Запрос баланса сим карты");
				if(settingsDev.sendCommands())
					pd.show();
			}
		});
		b.setNegativeButton("Отмена", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}});
		b.show();
		Log.i(TAG_events,"request balans");
	}

	public void onClickGuardianSet(View v)
	{
		count_reques = 0;
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Log.i(TAG_events,"onClickSetStartParam");
		flagSMSPostanovka = settings.getIsSMSPostanovka();
		flagSMSSnjatie = settings.getIsSMSSnjatie();
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		String MainTitle;
		String ButtonTitle;
		final String TitleProgressBar;

		if(isGuardian)
		{
			MainTitle = "Снять с контроля?";
			ButtonTitle = "Снять";
			TitleProgressBar = "Снятие с контроля";
		}
		else
		{
			MainTitle = "Поставить на контроль?";
			ButtonTitle = "Поставить";	
			TitleProgressBar = "Постановка на контроль";
		}

		b.setTitle(MainTitle);
		b.setPositiveButton(ButtonTitle, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				clickControlAlarm = true;
				settingsDev.clearQueueCommands();
				settingsDev.AddSetGuardianCommand(!isGuardian, true);

				if(!isGuardian)
				{
					if(!flagSMSPostanovka)
						settingsDev.AddRequestReportCommand();
				}
				else
				{
					if(!flagSMSSnjatie)
						settingsDev.AddRequestReportCommand();
				}

				LoadProgressDialog(settingsDev.sms_to_send.size()+1,TitleProgressBar);
				if(settingsDev.sendCommands())
					pd.show();
			}
		});
		b.setNegativeButton("Отмена",  new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}});
		b.show();
	}

	public void onClickTextViewHelpStr2(View v)
	{
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.thermics.com"));
		startActivity(browserIntent);
	}


	public static boolean flagNeedsSendRequest=false;
	public static boolean flagSMSPostanovka;
	public static boolean flagSMSSnjatie;
	public static boolean isGuardian;
	public void loadConfigParam()
	{
		Log.i(TAG_events,"guardian set");
		textNameSystem.setText( settings.getName() );
		//change title message reader
		current_local_num_not = SystemConfig.getNumNotification();
		setTitleMessageButton(current_local_num_not);

		isGuardian=settings.getIsGuardian();
		SetCheckedGuardian(isGuardian);

		flagSMSPostanovka = settings.getIsSMSPostanovka();
		flagSMSSnjatie = settings.getIsSMSSnjatie();

		current_local_num_not = settings.getNumNotificationSaved();
		setTitleMessageButton(current_local_num_not);
	}
	String resultGuardian;
	static ProgressDialog pd = null;
	static Handler hpd =null;
	static int count_reques = 0;
	final CounterTimer timer = new CounterTimer(30000, 1000);
	public void LoadProgressDialog(int count_sms,String title)
	{
		pd = new ProgressDialog(this);
		//pd.setOwnerActivity((Activity)getApplicationContext());
		timer.setProgressDialog(pd);
		timer.setTitle(title);
		pd.setTitle(title);
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
					pd.dismiss();
					timer.cancel();
					//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);	
				}
			}
		};

		settingsDev.setHandlerDialog(hpd);
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}


	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);
		unregisterReceiver(br_clear_unreaded_sms);
		unregisterReceiver(brSystemSelectionChanged);
	}



}
