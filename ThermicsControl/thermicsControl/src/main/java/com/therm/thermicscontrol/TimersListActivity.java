package com.therm.thermicscontrol;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

public class TimersListActivity extends BaseActivity {

	ListView lvData;
	DBTimerDateTimeAction db;
	TimerCursorAdapter scAdapter;
	Cursor cursor;
	public SystemConfig settings=null;
	
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_timers_list);
		try
		{
				lvData = (ListView) findViewById(R.id.listViewTimers);
				settings = SystemConfigDataSource.getActiveSystem();
				 // открываем подключение к БД
			    db = new DBTimerDateTimeAction(this,DBTimerDateTimeAction.DB_DEFAULT_TABLE);
			    db.open();
			    //db.clearBase();
			    // получаем курсор
			    cursor = db.getAllData(settings.getId());
			    startManagingCursor(cursor);
			    
			    // формируем столбцы сопоставления
			    String[] from = new String[] { DBTimerDateTimeAction.COLUMN_N_RELE,DBTimerDateTimeAction.COLUMN_ENABLE, DBTimerDateTimeAction.COLUMN_REPEAT };
			    int[] to = new int[] 		 { R.id.titleTimerDestenation,R.id.checkBoxEnableTimer, R.id.valueTimeRepeatDay};
		
			    // создааем адаптер и настраиваем список
			    scAdapter = new TimerCursorAdapter(this, settings, R.layout.row_timer,cursor, db, from, to);
			    lvData.setAdapter(scAdapter);
			    
			    
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.timers_list, menu);
		return true;
	}
	
	public void onClickAddTimer(View v)
	{
		Intent intent = new Intent(this, TimerActivity.class);
	    startActivity(intent);
	}
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
	
	@Override
	public void onBackPressed(){
		settings.setIsEnableTimer(db.getEnableTimer());
		super.onBackPressed();
	}
	
	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    settings.setIsEnableTimer(db.getEnableTimer());
	}
}
