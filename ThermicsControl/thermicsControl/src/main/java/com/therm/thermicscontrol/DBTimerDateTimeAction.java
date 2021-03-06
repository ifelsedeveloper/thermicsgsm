package com.therm.thermicscontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class DBTimerDateTimeAction {

    public static final String DB_NAME = "dbtimerbase";
    public static final String DB_NAME_TEMPORARY = "dbtimerbasetemporary";
    public static final String DB_DEFAULT_TABLE = "DB_TIMERS";
    public static final String DATABASE_PATH = "/data/data/com.therm.thermicscontrol/databases/";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_N_RELE = "n_rele";
    public static final String COLUMN_ENABLE = "enable";
    public static final String COLUMN_START_HOUR = "start_hour";
    public static final String COLUMN_START_MINUTE = "start_minute";
    public static final String COLUMN_STOP_HOUR = "stop_hour";
    public static final String COLUMN_STOP_MINUTE = "stop_minute";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_MON = "mon";
    public static final String COLUMN_TUE = "tue";
    public static final String COLUMN_WEN = "wen";
    public static final String COLUMN_THU = "thu";
    public static final String COLUMN_FRI = "fri";
    public static final String COLUMN_SAT = "sat";
    public static final String COLUMN_SUN = "sun";
    public static final String COLUMN_ID_SYSTEM = "id_system";
    private static final int DB_VERSION = 2;
    private final Context mCtx;
    public String DB_TABLE = "DB_TIMERS";
    public SQLiteDatabase mDB;
    private String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_N_RELE + " integer, " +
                    COLUMN_ENABLE + " integer, " +
                    COLUMN_START_HOUR + " integer, " +
                    COLUMN_START_MINUTE + " integer, " +
                    COLUMN_STOP_HOUR + " integer, " +
                    COLUMN_STOP_MINUTE + " integer, " +
                    COLUMN_REPEAT + " text, " +
                    COLUMN_MON + " integer, " +
                    COLUMN_TUE + " integer, " +
                    COLUMN_WEN + " integer, " +
                    COLUMN_THU + " integer, " +
                    COLUMN_FRI + " integer, " +
                    COLUMN_SAT + " integer, " +
                    COLUMN_SUN + " integer, " +
                    COLUMN_ID_SYSTEM + " integer DEFAULT '1' NOT NULL" +
                    ");";
    private DBHelper mDBHelper;

    public DBTimerDateTimeAction(Context ctx, String databaseName) {
        mCtx = ctx;
        DB_TABLE = databaseName;
        DB_CREATE =
                "create table " + DB_TABLE + "(" +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_N_RELE + " integer, " +
                        COLUMN_ENABLE + " integer, " +
                        COLUMN_START_HOUR + " integer, " +
                        COLUMN_START_MINUTE + " integer, " +
                        COLUMN_STOP_HOUR + " integer, " +
                        COLUMN_STOP_MINUTE + " integer, " +
                        COLUMN_REPEAT + " text, " +
                        COLUMN_MON + " integer, " +
                        COLUMN_TUE + " integer, " +
                        COLUMN_WEN + " integer, " +
                        COLUMN_THU + " integer, " +
                        COLUMN_FRI + " integer, " +
                        COLUMN_SAT + " integer, " +
                        COLUMN_SUN + " integer, " +
                        COLUMN_ID_SYSTEM + " integer DEFAULT '1' NOT NULL" +
                        ");";
    }

    public DBTimerDateTimeAction(Context ctx) {
        mCtx = ctx;
        DB_CREATE =
                "create table " + DB_TABLE + "(" +
                        COLUMN_ID + " integer primary key autoincrement, " +
                        COLUMN_N_RELE + " integer, " +
                        COLUMN_ENABLE + " integer, " +
                        COLUMN_START_HOUR + " integer, " +
                        COLUMN_START_MINUTE + " integer, " +
                        COLUMN_STOP_HOUR + " integer, " +
                        COLUMN_STOP_MINUTE + " integer, " +
                        COLUMN_REPEAT + " text, " +
                        COLUMN_MON + " integer, " +
                        COLUMN_TUE + " integer, " +
                        COLUMN_WEN + " integer, " +
                        COLUMN_THU + " integer, " +
                        COLUMN_FRI + " integer, " +
                        COLUMN_SAT + " integer, " +
                        COLUMN_SUN + " integer, " +
                        COLUMN_ID_SYSTEM + " integer DEFAULT '1' NOT NULL" +
                        ");";
    }

    static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        if (!doesDatabaseExist(mCtx, DB_NAME)) {

        }
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData(long id_system) {
        return mDB.query(DB_TABLE, null, COLUMN_ID_SYSTEM + " = ?", new String[]{Long.toString(id_system)}, null, null, null);
    }

    // добавить запись в DB_TABLE
    public void addRec(TimerValue timerValue) {
        ContentValues cv = getContentValue(timerValue);
        mDB.insert(DB_TABLE, null, cv);
    }

    //добавить запись в DB_TABLE
    public void updateRec(TimerValue timerValue, int id) {
        ContentValues cv = getContentValue(timerValue);
        mDB.update(DB_TABLE, cv, "_id=?",
                new String[]{Integer.toString(id)});
    }

    private ContentValues getContentValue(TimerValue timerValue) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_N_RELE, timerValue.n_rele);
        cv.put(COLUMN_ENABLE, timerValue.enable ? 1 : 0);
        cv.put(COLUMN_START_HOUR, timerValue.start_hour);
        cv.put(COLUMN_START_MINUTE, timerValue.start_minute);

        cv.put(COLUMN_STOP_HOUR, timerValue.stop_hour);
        cv.put(COLUMN_STOP_MINUTE, timerValue.stop_minute);

        cv.put(COLUMN_REPEAT, timerValue.getDays());

        cv.put(COLUMN_MON, timerValue.isDay[0] ? 1 : 0);
        cv.put(COLUMN_TUE, timerValue.isDay[1] ? 1 : 0);
        cv.put(COLUMN_WEN, timerValue.isDay[2] ? 1 : 0);
        cv.put(COLUMN_THU, timerValue.isDay[3] ? 1 : 0);
        cv.put(COLUMN_FRI, timerValue.isDay[4] ? 1 : 0);
        cv.put(COLUMN_SAT, timerValue.isDay[5] ? 1 : 0);
        cv.put(COLUMN_SUN, timerValue.isDay[6] ? 1 : 0);
        cv.put(COLUMN_ID_SYSTEM, timerValue.id_system);
        return cv;
    }

    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);

    }

    public Cursor getRec(long id) {
        //Toast.makeText(mCtx, "my id = "+ Long.toString(id), Toast.LENGTH_LONG).show();
        return this.mDB.rawQuery("select * from " + DB_TABLE + " where " + COLUMN_ID + "='" + Long.toString(id) + "'", null);
    }

    boolean getEnableTimer() {
        boolean res = false;
        Cursor cursor = this.mDB.rawQuery("select * from " + DB_TABLE + " where " + COLUMN_ENABLE + "='" + Long.toString(1) + "'", null);
        if (cursor.moveToFirst()) res = true;
        return res;
    }

    public TimerValue getTimerValueFromRec(long id) {
        Cursor cursor = getRec(id);
        if (cursor != null) {
            TimerValue value = getTimerValueFromRec(cursor);
            return value;
        }
        return null;
    }

    public TimerValue getTimerValueFromRec(Cursor cursor) {
        if (cursor != null) {

            TimerValue value = new TimerValue();
            value.row_id = cursor.getLong(0);
            value.n_rele = cursor.getInt(1);
            value.enable = cursor.getInt(2) > 0;
            value.start_hour = cursor.getInt(3);
            value.start_minute = cursor.getInt(4);
            value.stop_hour = cursor.getInt(5);
            value.stop_minute = cursor.getInt(6);

            int start_v = 8;
            for (int i = 0; i < 7; i++) {
                value.isDay[i] = cursor.getInt(i + start_v) > 0;
            }
            int col_index = cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_ID_SYSTEM);
            if (col_index > 0)
                value.id_system = cursor.getInt(col_index);
            return value;
        }
        return null;
    }

    public long lastRowNumber() {
        long result = -1;
        String query = "SELECT " + COLUMN_ID + " from " + DB_TABLE + " order by " + COLUMN_ID + " DESC limit 1";
        Cursor c = mDB.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            result = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
        }

        return result;
    }

    public boolean check(TimerValue value) {
        boolean res = false;

        Cursor cursor = this.mDB.rawQuery("select * from " + DB_TABLE + " where "
                        + COLUMN_ENABLE + "='" + (value.enable ? "1" : "0") + "' and "
                        + COLUMN_N_RELE + "='" + Integer.toString(value.n_rele) + "' and "
                        + COLUMN_START_HOUR + "='" + Integer.toString(value.start_hour) + "' and "
                        + COLUMN_STOP_HOUR + "='" + Integer.toString(value.stop_hour) + "' and "
                        + COLUMN_START_MINUTE + "='" + Integer.toString(value.start_minute) + "' and "
                        + COLUMN_STOP_MINUTE + "='" + Integer.toString(value.stop_minute) + "' and "
                        + COLUMN_ID_SYSTEM + "='" + Long.toString(value.id_system) + "'"
                , null);
        if (cursor.moveToFirst()) res = true;
//        TimerValue last = getTimerValueFromRec(cursor);
//        Toast.makeText(ContextApplication.getAppContext(), "start mismatch  check!!!!", Toast.LENGTH_SHORT).show();
//        if(last.enable !=  value.enable) {
//            Toast.makeText(ContextApplication.getAppContext(), "enable mismatch !!!!", Toast.LENGTH_LONG).show();
//        }
//        if(last.n_rele !=  value.n_rele) {
//            Toast.makeText(ContextApplication.getAppContext(), "n_rele mismatch !!!!", Toast.LENGTH_LONG).show();
//        }
//        if(last.start_hour !=  value.start_hour) {
//            Toast.makeText(ContextApplication.getAppContext(), "start_hour mismatch !!!!", Toast.LENGTH_LONG).show();
//        }
//        if(last.stop_hour !=  value.stop_hour) {
//            Toast.makeText(ContextApplication.getAppContext(), "stop_hour mismatch !!!! " + last.stop_hour + "|" + value.stop_hour, Toast.LENGTH_LONG).show();
//        }
//        if(last.start_minute !=  value.start_minute) {
//            Toast.makeText(ContextApplication.getAppContext(), "start_minute mismatch !!!!"  + last.start_minute + "|" + value.start_minute, Toast.LENGTH_LONG).show();
//        }
//        if(last.stop_minute !=  value.stop_minute) {
//            Toast.makeText(ContextApplication.getAppContext(), "stop_minute mismatch !!!!"  + last.stop_minute + "|" + value.stop_minute, Toast.LENGTH_LONG).show();
//        }
//        if(last.id_system !=  value.id_system) {
//            Toast.makeText(ContextApplication.getAppContext(), "id_system mismatch !!!!", Toast.LENGTH_LONG).show();
//        }
//        Toast.makeText(ContextApplication.getAppContext(), "end mismatch  check!!!!", Toast.LENGTH_SHORT).show();
        return res;
    }

    public boolean check(long n_row, long day_of_week) {
        boolean result = false;
        long value = n_row * 7 + day_of_week + 1;
        Cursor cursor = getRec(n_row);
        if (cursor != null && cursor.moveToFirst()) {
            long[] idInntents = new long[7];

            idInntents[0] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_MON));
            idInntents[1] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_TUE));
            idInntents[2] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_WEN));
            idInntents[3] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_THU));
            idInntents[4] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_FRI));
            idInntents[5] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_SAT));
            idInntents[6] = cursor.getLong(cursor.getColumnIndex(DBTimerDateTimeAction.COLUMN_SUN));
            //Toast.makeText(mCtx, "value  = "+Long.toString(value) +"|" + Long.toString( idInntents[(int)day_of_week] ), Toast.LENGTH_LONG).show();
            if (idInntents[(int) day_of_week] == value)
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

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.d("DBTimer", " --- onUpgrade database from " + oldVersion
                    + " to " + newVersion + " version --- ");
            if (oldVersion == 1 && newVersion == 2) {

                db.beginTransaction();
                try {

                    db.execSQL("alter table " + DB_TABLE + " add column " + COLUMN_ID_SYSTEM + " integer DEFAULT '1' NOT NULL;");
                    // создаем временную таблицу
                    db.execSQL("create temporary table " + DB_NAME_TEMPORARY + "(" +
                            COLUMN_ID + " integer primary key autoincrement, " +
                            COLUMN_N_RELE + " integer, " +
                            COLUMN_ENABLE + " integer, " +
                            COLUMN_START_HOUR + " integer, " +
                            COLUMN_START_MINUTE + " integer, " +
                            COLUMN_STOP_HOUR + " integer, " +
                            COLUMN_STOP_MINUTE + " integer, " +
                            COLUMN_REPEAT + " text, " +
                            COLUMN_MON + " integer, " +
                            COLUMN_TUE + " integer, " +
                            COLUMN_WEN + " integer, " +
                            COLUMN_THU + " integer, " +
                            COLUMN_FRI + " integer, " +
                            COLUMN_SAT + " integer, " +
                            COLUMN_SUN + " integer, " +
                            COLUMN_ID_SYSTEM + " integer" +
                            ");");

                    db.execSQL("insert into " + DB_NAME_TEMPORARY + " select " +
                            COLUMN_ID + ", " +
                            COLUMN_N_RELE + ", " +
                            COLUMN_ENABLE + ", " +
                            COLUMN_START_HOUR + ", " +
                            COLUMN_START_MINUTE + ", " +
                            COLUMN_STOP_HOUR + ", " +
                            COLUMN_STOP_MINUTE + ", " +
                            COLUMN_REPEAT + ", " +
                            COLUMN_MON + ", " +
                            COLUMN_TUE + ", " +
                            COLUMN_WEN + ", " +
                            COLUMN_THU + ", " +
                            COLUMN_FRI + ", " +
                            COLUMN_SAT + ", " +
                            COLUMN_SUN + ", " +
                            COLUMN_ID_SYSTEM + " " +
                            "from " + DB_TABLE + ";");
                    db.execSQL("drop table " + DB_TABLE + ";");

                    db.execSQL(DB_CREATE);

                    db.execSQL("insert into " + DB_TABLE + " select " +
                            COLUMN_ID + ", " +
                            COLUMN_N_RELE + ", " +
                            COLUMN_ENABLE + ", " +
                            COLUMN_START_HOUR + ", " +
                            COLUMN_START_MINUTE + ", " +
                            COLUMN_STOP_HOUR + ", " +
                            COLUMN_STOP_MINUTE + ", " +
                            COLUMN_REPEAT + ", " +
                            COLUMN_MON + ", " +
                            COLUMN_TUE + ", " +
                            COLUMN_WEN + ", " +
                            COLUMN_THU + ", " +
                            COLUMN_FRI + ", " +
                            COLUMN_SAT + ", " +
                            COLUMN_SUN + ", " +
                            COLUMN_ID_SYSTEM + " " +
                            "from " + DB_NAME_TEMPORARY + ";");
                    db.execSQL("drop table " + DB_NAME_TEMPORARY + ";");

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();

                }
            }
        }
    }

}