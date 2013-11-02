package com.therm.thermicscontrol;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DBRequest{
  
  public static final String DB_NAME = "mydbrequests";
  private static final int DB_VERSION = 1;
  public static final String DB_TABLE = "mytabrequests";
  public static final String DATABASE_PATH = "/data/data/com.therm.thermicscontrol/databases/";
  
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_ENABLE = "enable";
  public static final String COLUMN_HOUR = "hour";
  public static final String COLUMN_MINUTES = "minutes";
  public static final String COLUMN_REPEAT = "repeat";
  public static final String COLUMN_MON = "mon";
  public static final String COLUMN_TUE = "tue";
  public static final String COLUMN_WEN = "wen";
  public static final String COLUMN_THU = "thu";
  public static final String COLUMN_FRI = "fri";
  public static final String COLUMN_SAT = "sat";
  public static final String COLUMN_SUN = "sun";
  
  private static final String DB_CREATE = 
    "create table " + DB_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, " +
      COLUMN_ENABLE + " integer, " +
      COLUMN_HOUR + " integer, " +
      COLUMN_MINUTES + " integer, " +
      COLUMN_REPEAT + " text, " +
      COLUMN_MON + " integer, " +
      COLUMN_TUE + " integer, " +
      COLUMN_WEN + " integer, " +
      COLUMN_THU + " integer, " +
      COLUMN_FRI + " integer, " +
      COLUMN_SAT + " integer, " +
      COLUMN_SUN + " integer" +
    ");";
  
  private final Context mCtx;
  
  
  private DBHelper mDBHelper;
  public SQLiteDatabase mDB;
  
  public DBRequest(Context ctx) {
    mCtx = ctx;
  }
  
  // открыть подключение
  public void open() {
    mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
    mDB = mDBHelper.getWritableDatabase();
  }
  
  // закрыть подключение
  public void close() {
    if (mDBHelper!=null) mDBHelper.close();
  }
  
  // получить все данные из таблицы DB_TABLE
  public Cursor getAllData() {
    return mDB.query(DB_TABLE, null, null, null, null, null, null);
  }
  
  // добавить запись в DB_TABLE
  public void addRec(int enable, int hour,int minutes, String str_repeat, Long mon, Long tue, Long wen, Long thu, Long fri, Long sat, Long sun) {
    ContentValues cv = new ContentValues();
    
    cv.put(COLUMN_ENABLE, enable);
    cv.put(COLUMN_HOUR, hour);
    cv.put(COLUMN_MINUTES, minutes);
    
    cv.put(COLUMN_REPEAT, str_repeat);
    
    cv.put(COLUMN_MON, mon);
    cv.put(COLUMN_TUE, tue);
    cv.put(COLUMN_WEN, wen);
    cv.put(COLUMN_THU, thu);
    cv.put(COLUMN_FRI, fri);
    cv.put(COLUMN_SAT, sat);
    cv.put(COLUMN_SUN, sun);
    
    mDB.insert(DB_TABLE, null, cv);
  }
  
//добавить запись в DB_TABLE
 public void updateRec(int id,int enable, int hour,int minutes, String str_repeat, Long mon, Long tue, Long wen, Long thu, Long fri, Long sat, Long sun) {
   ContentValues cv = new ContentValues();
   
   cv.put(COLUMN_ENABLE, enable);
   cv.put(COLUMN_HOUR, hour);
   cv.put(COLUMN_MINUTES, minutes);
   
   cv.put(COLUMN_REPEAT, str_repeat);
   
   cv.put(COLUMN_MON, mon);
   cv.put(COLUMN_TUE, tue);
   cv.put(COLUMN_WEN, wen);
   cv.put(COLUMN_THU, thu);
   cv.put(COLUMN_FRI, fri);
   cv.put(COLUMN_SAT, sat);
   cv.put(COLUMN_SUN, sun);
   
   mDB.update(DBRequest.DB_TABLE, cv, "_id=?",
			new String[] { Integer.toString(id) });
 }
  
  
  
  // удалить запись из DB_TABLE
  public void delRec(long id) {
    mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    
  }
  
  public Cursor getRec(long id)
  {  
	  //Toast.makeText(mCtx, "my id = "+ Long.toString(id), Toast.LENGTH_LONG).show();
	  return this.mDB.rawQuery("select * from " + DB_TABLE + " where " + COLUMN_ID + "='" + Long.toString(id) + "'" , null);
  }
  
  public long lastRowNumber()
  {
	  long result = -1;
	  String query = "SELECT " + COLUMN_ID +" from " + DB_TABLE + " order by " + COLUMN_ID +" DESC limit 1";
	  Cursor c = mDB.rawQuery(query,null);
	  if (c != null && c.moveToFirst()) {
		  result = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
	  }
	  
	  return result;
  }
  
  public boolean check(long n_row,long day_of_week)
  {
	  boolean result = false;
	  long value = n_row * 7 + day_of_week +1;
	  Cursor cursor = getRec(n_row);
	  if (cursor != null && cursor.moveToFirst()) {
		  long [] idInntents = new long[7];
		  
		  	idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_MON));
			idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_TUE));
			idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_WEN));
			idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_THU));
			idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_FRI));
			idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SAT));
			idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBRequest.COLUMN_SUN));
			//Toast.makeText(mCtx, "value  = "+Long.toString(value) +"|" + Long.toString( idInntents[(int)day_of_week] ), Toast.LENGTH_LONG).show();
			if(idInntents[(int)day_of_week] == value) 
				return true;
			else 
				return false;
	  }
	  return result;
  }
  
  public void clearBase() {
	    mDB.delete(DB_TABLE, null, null);
	  }
  
  // класс по созданию и управлению БД
  private class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String name, CursorFactory factory,
        int version) {
      super(context, name, factory, version);
    }

    // создаем и заполняем БД
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DB_CREATE);
      
//      ContentValues cv = new ContentValues();
//      for (int i = 1; i < 10; i++) {
//        cv.put(COLUMN_TXT, "sometext " + i);
//        cv.put(COLUMN_DATE, R.drawable.ic_launcher);
//        db.insert(DB_TABLE, null, cv);
//      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
  }

}