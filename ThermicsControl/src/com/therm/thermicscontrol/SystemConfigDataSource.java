package com.therm.thermicscontrol;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SystemConfigDataSource {
	public static final String SYSTEM_SELECTION_CHANGED = "SYTEM_SELECTION_CHANGED";
	public static final String MYSYSTEM_PREFERENCES = "SystemPrefs";
	public static final String SELECTED_SYSTEM = "SELECTED_SYSTEM";
	public static final String INITIAL_LOADED_FINISHED = "INITIAL_LOADED_FINISHED";
	// Database fields
	private SQLiteDatabase database;
	private SystemConfigSQLiteHelper dbHelper;
	private String[] allColumns = { SystemConfigSQLiteHelper.COLUMN_ID,
			SystemConfigSQLiteHelper.COLUMN_NAME };

	static SystemConfigDataSource _systemConfigDataSource = null;
	static Object _syncObject = new Object();
	public static SystemConfigDataSource sharedInstanceSystemConfigDataSource()
	{	
		synchronized(_syncObject)
		{
			if(_systemConfigDataSource == null)
			{
				_systemConfigDataSource = new SystemConfigDataSource(ContextApplication.getAppContext());
				_systemConfigDataSource.InitialSetup();
				_systemConfigDataSource.open();
			}
			return _systemConfigDataSource;
		}
	}

	public SystemConfigDataSource(Context context) {
		dbHelper = new SystemConfigSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void InitialSetup()
	{
		SharedPreferences prefefrence = ContextApplication.getAppContext()
				.getSharedPreferences(MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS);
		boolean isExistSystem = prefefrence.getBoolean("INITIAL_LOADED_FINISHED", false);
		if(!isExistSystem) {
			open();
			createSystemConfig("ќбъект");
			setSelectedSystem(1);
			SharedPreferences.Editor editor = prefefrence.edit();
			editor.putBoolean(INITIAL_LOADED_FINISHED, true);
			editor.commit();
			//initital setup for timer
			
			
			close();
		}
	}
	
	public static SystemConfig getActiveSystem()
	{	
		SystemConfigDataSource dataSource = sharedInstanceSystemConfigDataSource();
		return dataSource.getSystemConfig(dataSource.idSelectedSystem());
	}
	
	public void setSelectedSystem(long database_id)
	{
		SharedPreferences.Editor editor = ContextApplication.getAppContext()
				.getSharedPreferences(MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS).edit();
		editor.putLong(SELECTED_SYSTEM, database_id);
		editor.commit();
	}
	
	public long idSelectedSystem()
	{
		SharedPreferences prefefrence = ContextApplication.getAppContext()
				.getSharedPreferences(MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS);
		return prefefrence.getLong(SELECTED_SYSTEM, 0);
	}
	
	// получить все данные из таблицы DB_TABLE
	public Cursor getAllData() {
		return database.query(SystemConfigSQLiteHelper.TABLE_SYSTEMS, null, null, null, null, null, null);
	}

	public SystemConfig createSystemConfig(String name) {
		_allConfigs = null;
		ContentValues values = new ContentValues();
		values.put(SystemConfigSQLiteHelper.COLUMN_NAME, name);
		long insertId = database.insert(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
				null, values);
		Cursor cursor = database.query(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
				allColumns, SystemConfigSQLiteHelper.COLUMN_ID + " = "
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		SystemConfig newSystemConfig = cursorToSystemConfig(cursor);
		cursor.close();
		return newSystemConfig;
	}

	public void deleteSystemConfig(SystemConfig systemConfig) {
		_allConfigs = null;
		long id = systemConfig.getId();
		System.out.println("Comment deleted with id: " + id);
		// cancel all timers and delete their tables

		database.delete(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
				SystemConfigSQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public void deleteSystemConfig(int id_system) {
		_allConfigs = null;
		database.delete(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
				SystemConfigSQLiteHelper.COLUMN_ID + " = " + id_system, null);
	}

	public void updateSystemConfigName(int id_system, String name) {
		_allConfigs = null;
		ContentValues cv = new ContentValues();
		cv.put(SystemConfigSQLiteHelper.COLUMN_NAME, name);
		database.update(SystemConfigSQLiteHelper.TABLE_SYSTEMS, cv, SystemConfigSQLiteHelper.COLUMN_ID + " = " + id_system, null);
	}


	private volatile static List<SystemConfig> _allConfigs = null;
	public List<SystemConfig> getAllSystemConfig() {
		if(_allConfigs == null){
			List<SystemConfig> systems = new ArrayList<SystemConfig>();
	
			Cursor cursor = database.query(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
					allColumns, null, null, null, null, null);
	
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				SystemConfig comment = cursorToSystemConfig(cursor);
				systems.add(comment);
				cursor.moveToNext();
			}
			// make sure to close the cursor
			cursor.close();
			_allConfigs = systems;
		}
		return _allConfigs;
	}

	public SystemConfig getSystemConfig(long id)
	{  
//		List<SystemConfig> systems = getAllSystemConfig();
//		open();
//		Cursor cursor = this.database.rawQuery(
//				"select * from " + SystemConfigSQLiteHelper.TABLE_SYSTEMS + " where " + SystemConfigSQLiteHelper.COLUMN_ID + "='" + Long.toString(id) + "'" , null);
//		cursor.moveToFirst();
//		SystemConfig res = cursorToSystemConfig(cursor);
//		close();
//		Cursor cursor = database.query(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
//				allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			SystemConfig config = cursorToSystemConfig(cursor);
//			if(config.getId() == id) {
//				Log.d("event_tag_settings_param", "System founded "+ Long.toString(id));
//				return config;
//			}
//			cursor.moveToNext();
//		}
		for (SystemConfig config : getAllSystemConfig()) {
			if(config.getId() == id) {
				Log.d("event_tag_settings_param", "System founded "+ Long.toString(id));
				return config;
			}
		}
		return null;
	}

	public SystemConfig getSystemConfig(String phoneNumber) {
		SystemConfig resSystemConfig = null;

//		Cursor cursor = database.query(SystemConfigSQLiteHelper.TABLE_SYSTEMS,
//				allColumns, null, null, null, null, null);
//
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()) {
//			SystemConfig systemConfig = cursorToSystemConfig(cursor);
//			if (compareSIMNumbers(phoneNumber,systemConfig.getNumberSIM())
//					|| compareSIMNumbers(phoneNumber,systemConfig.getNumberSIM2())) {
//				resSystemConfig = systemConfig;
//				break;
//			}
//			cursor.moveToNext();
//		}
//		// make sure to close the cursor
//		cursor.close();

		for (SystemConfig systemConfig : getAllSystemConfig()) {
			if (compareSIMNumbers(phoneNumber,systemConfig.getNumberSIM())
					|| compareSIMNumbers(phoneNumber,systemConfig.getNumberSIM2())) {
				resSystemConfig = systemConfig;
				break;
			}
		}
		
		return resSystemConfig;
	}

	public static boolean compareSIMNumbers(String number1,String number2)
	{
		boolean res = false;
		if(number1!= null && number2!=null && number1.length() > 0 && number2.length() > 0)
		{
			if((number1.charAt(0)=='8') && (number2.charAt(0)=='+'))
				number1=String.format("+7%s", number1.substring(1,number1.length()));
	
			if((number1.charAt(0)=='+') && (number2.charAt(0)=='8'))
				number2=String.format("+7%s", number2.substring(1,number2.length()));
			//Log.i("TAG_compare_numbers", number2+" = "+number1);
			if (number2.equalsIgnoreCase(number1))
			{
				res = true;
			}
		}
		return res;
	}
	
	private SystemConfig cursorToSystemConfig(Cursor cursor) {
		SystemConfig systemConfig = new SystemConfig(cursor.getLong(0));
		systemConfig.setId(cursor.getLong(0));
		systemConfig.setName(cursor.getString(1));
		return systemConfig;
	}
	
	public int getPositionFrom(long id_system)
	{
		List<SystemConfig> systems = getAllSystemConfig();
		int position = 0;
		for (position = 0; position < systems.size(); position++) {
			if(systems.get(position).getId() == id_system)
				break;
		}
		return position;
	}
	public int getSelectedPosition()
	{
		return getPositionFrom(idSelectedSystem());
	}
}
