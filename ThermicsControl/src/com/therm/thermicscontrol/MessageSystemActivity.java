package com.therm.thermicscontrol;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.therm.thermicscontrol.R;

import android.os.Bundle;
import android.util.Log;

import android.view.Menu;
import android.view.View;
import android.view.Window;

import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;


@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class MessageSystemActivity extends BaseActivity {
	
	ListView lvData;
	DBSMS db;
	SimpleCursorAdapter scAdapter;
	Cursor cursor;
	int count_messages = 0;
	
	BroadcastReceiver br;
	
	public static final String TAG_events="event_tag_message_sytem";
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_message_system);
		
		//create for work with shared preference
		Log.i(TAG_events,"start activity message sytem");
		
		//database
		
		 // открываем подключение к БД
	    db = new DBSMS(this);
	    db.open();

	    // получаем курсор
	    cursor = db.getAllData();
	    startManagingCursor(cursor);
	    
	    // формируем столбцы сопоставления
	    String[] from = new String[] { DBSMS.COLUMN_DATE, DBSMS.COLUMN_TXT };
	    int[] to = new int[] { R.id.smsDate, R.id.smsMessage };

	    // создааем адаптер и настраиваем список
	    scAdapter = new SimpleCursorAdapter(this, R.layout.sms_row, cursor, from, to);
	    lvData = (ListView) findViewById(R.id.listViewSMSessages);
	    lvData.setAdapter(scAdapter);
		
		/////
	 // создаем BroadcastReceiver
	    br = new BroadcastReceiver() {
	      // действия при получении сообщений
	      public void onReceive(Context context, Intent intent) {
	        String sms = intent.getStringExtra(PARAM_SMS);
	        String time = intent.getStringExtra(PARAM_SMSTIME);
	        Log.d(TAG_events, "onReceive sms: "+sms+" ;time = "+time);
	        // обновляем курсор
	        updateListView();
	        
	      }
	    };
	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION_RCVSMS);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(br, intFilt);
	    count_messages = cursor.getCount();
	    TextView textViewTitle = (TextView) findViewById(R.id.titleMessageSystem);
	    textViewTitle.setText(String.format("Сообщения от системы (%d)", count_messages));
	    
	    lvData.setSelection(lvData.getCount());
	    
	    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
	        @Override
	        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
	            Log.e("Alert","Lets See if it Works Message system !!!");
	            Toast.makeText(getApplicationContext(), "Error message system", Toast.LENGTH_LONG).show();
	        }
	    });
	    
	    SystemConfig settings=SystemConfigDataSource.getActiveSystem();
	    SystemConfig.clearNumNotification();
	    settings.setNumNotificationSaved(0);
	    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    nm.cancelAll();
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    // закрываем подключение при выходе
	    db.close();
	    unregisterReceiver(br);
	  }
	
	public void updateListView()
	{
		cursor.requery();
		count_messages = cursor.getCount();
		TextView textViewTitle = (TextView) findViewById(R.id.titleMessageSystem);
		textViewTitle.setText(String.format("Сообщения от системы (%d)", count_messages));
		lvData.smoothScrollToPosition(lvData.getCount());
		
	}
	
	public void onClickClearButton(View v)
	{
		Log.i(TAG_events,"onClickClearButton");
		if(count_messages>0)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Удалить сообщения?");
			b.setPositiveButton("Да", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	db.clearBase();
		    		cursor.requery();
		    		count_messages = cursor.getCount();
		    		TextView textViewTitle = (TextView) findViewById(R.id.titleMessageSystem);
		    		textViewTitle.setText(String.format("Сообщения от системы (%d)", count_messages));
		    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        }
		      });
			b.setNegativeButton("Нет", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        }});
			b.show();
		}
		
	}
	
	public void clickSaveButton(View v)
	{
		
		//save parameters
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setTitle("Сохранить сообщения в файл?");
		b.setPositiveButton("Сохранить", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	SaveMessage();
	        }
	      });
		b.setNegativeButton("Отмена", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {

	        }});
		b.show();
		
		
	}
	
	public void SaveMessage()
	{
		try {
			Date currentDate = new Date();
			Calendar cdate = new GregorianCalendar();
			cdate.setTime(currentDate);
			String filePath = String.format(Locale.US,"/sdcard/messages %d %d %d.doc",  cdate.get(Calendar.DAY_OF_MONTH),cdate.get(Calendar.MONTH)+1, cdate.get(Calendar.YEAR));
            File myFile = new File(filePath);
            myFile.createNewFile();

            Writer myOutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFile), "UTF8"));
            DBSMS ldb = new DBSMS(this);
            ldb.open();
            Cursor lcursor = ldb.getAllData();
            String message;
            String date;
            int nmessage = 1;
            
            for (lcursor.moveToFirst(); !lcursor.isAfterLast(); lcursor.moveToNext()) {
            	
        		message = lcursor.getString(this.cursor.getColumnIndex(DBSMS.COLUMN_TXT));
            	date = lcursor.getString(this.cursor.getColumnIndex(DBSMS.COLUMN_DATE));
            	myOutWriter.append(String.format("%d %s\n%s\n",nmessage, date,message));
            	nmessage++;
            }

            
            
            myOutWriter.close();
            
            Toast.makeText(getApplicationContext(),String.format("Сообщения экспортированы в файл: %s", filePath), Toast.LENGTH_LONG).show();
        } 
        catch (Exception e) 
        {
            Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
        }
	}
	
	@Override  
	public void onBackPressed() {
	      
	    //clear count notifications
	    SystemConfig settings=SystemConfigDataSource.getActiveSystem();
	    SystemConfig.clearNumNotification();
	    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    
	    nm.cancelAll();
		Intent intent = new Intent(MessageSystemActivity.BROADCAST_ACTION_CLEARUNREADEDSMS);
	    sendBroadcast(intent);
	    settings.setNumNotificationSaved(0);
	    super.onBackPressed(); 
	}
}
