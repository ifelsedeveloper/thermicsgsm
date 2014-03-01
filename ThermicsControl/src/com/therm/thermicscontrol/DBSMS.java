package com.therm.thermicscontrol;

import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class DBSMS{
  
  private static final String DB_NAME = "mydbsms";
  private static final int DB_VERSION = 1;
  private static final String DB_TABLE = "mytabsms";
  
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_TXT = "txt";
  
  public static final String RECV_SMS = "sms";
  
  private static final String DB_CREATE = 
    "create table " + DB_TABLE + "(" +
      COLUMN_ID + " integer primary key autoincrement, " +
      COLUMN_DATE + " text, " +
      COLUMN_TXT + " text" +
    ");";
  
  private final Context mCtx;
  
  
  private DBHelper mDBHelper;
  private SQLiteDatabase mDB;
  
  public DBSMS(Context ctx) {
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
  public void addRec(String txt, long date) {
    ContentValues cv = new ContentValues();
    cv.put(COLUMN_TXT, txt);
    String strDate=new SimpleDateFormat("dd.MM.yy HH:mm").format(date);
    cv.put(COLUMN_DATE, strDate);
    mDB.insert(DB_TABLE, null, cv);
    //обработка и выдача уведомления, передача указаний службе
    
  }
  
  // удалить запись из DB_TABLE
  public void delRec(long id) {
    mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
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