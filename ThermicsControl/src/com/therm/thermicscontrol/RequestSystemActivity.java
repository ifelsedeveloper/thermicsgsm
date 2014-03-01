package com.therm.thermicscontrol;

import com.therm.thermicscontrol.R;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;

public class RequestSystemActivity extends BaseActivity {

	public static final String TAG_events="event_tag_request_system";
	public CSettingsPref settings=null;
	
	ListView lvData;
	DBRequest db;
	CursorAdapterCheckBox scAdapter;
	Cursor cursor;
	
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//Remove title bar
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	
			//Remove notification bar
			//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_request_system);
			
			//create for work with shared preference
			settings=new CSettingsPref(getSharedPreferences(MYSYSTEM_PREFERENCES, MODE_MULTI_PROCESS));
	
		try
		{
				lvData = (ListView) findViewById(R.id.listViewRequestActions);
				
				 // открываем подключение к БД
			    db = new DBRequest(this);
			    db.open();

			    // получаем курсор
			    cursor = db.getAllData();
			    startManagingCursor(cursor);
			    
			    // формируем столбцы сопоставления
			    String[] from = new String[] { DBRequest.COLUMN_ENABLE, DBRequest.COLUMN_HOUR, DBRequest.COLUMN_MINUTES, DBRequest.COLUMN_REPEAT };
			    int[] to = new int[] 		 { R.id.checkBoxEnableRequest, R.id.titleTimeHourRow, R.id.titleTimeMinutesRow, R.id.titleDayRow };
		
			    // создааем адаптер и настраиваем список
			    scAdapter = new CursorAdapterCheckBox(this, R.layout.row_request,cursor, from, to, db);
			    lvData.setAdapter(scAdapter);
			    
		}
		catch (Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void onClickAddRequest(View v)
	{
		Log.i(TAG_events,"AddRequestActivity");
		Intent intent = new Intent(this, AddRequestActivity.class);
	    startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}


	protected void onDestroy() {
	    super.onDestroy();
	    db.close();
	  }
	
}
