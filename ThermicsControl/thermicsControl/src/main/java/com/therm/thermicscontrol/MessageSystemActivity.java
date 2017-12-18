package com.therm.thermicscontrol;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

@SuppressLint({"NewApi", "SimpleDateFormat"})
public class MessageSystemActivity extends BaseActivity implements OnItemSelectedListener {

    public static final String TAG_events = "event_tag_message_sytem";
    ListView lvData;
    DBSMS db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor;
    int count_messages = 0;
    int SelectedIndexOfSystem = 0;
    List<String> listSystems;
    List<SystemConfig> systems;
    BroadcastReceiver br;
    String filterValue = "";
    ArrayAdapter<String> dataAdapterSystems;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            //Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //Remove notification bar
            //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_message_system);

            //create for work with shared preference
            Log.i(TAG_events, "start activity message sytem");

            //database

            // открываем подключение к БД
            db = new DBSMS(this);
            db.open();
            if (cursor != null)
                stopManagingCursor(cursor);
            // получаем курсор
            cursor = db.getAllData("", "");
            startManagingCursor(cursor);

            // формируем столбцы сопоставления
            String[] from = new String[]{DBSMS.COLUMN_DATE, DBSMS.COLUMN_TXT};
            int[] to = new int[]{R.id.smsDate, R.id.smsMessage};

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
                    Log.d(TAG_events, "onReceive sms: " + sms + " ;time = " + time);
                    // обновляем курсор
                    updateListView();

                }
            };
            // создаем фильтр для BroadcastReceiver
            IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION_RCVSMS);
            // регистрируем (включаем) BroadcastReceiver
            registerReceiver(br, intFilt);
            count_messages = cursor.getCount();
            Spinner filterSpinner = (Spinner) findViewById(R.id.spinnerFilterSystem);
            listSystems = new ArrayList<String>();
            listSystems.add("Все сообщения");
            systems = SystemConfigDataSource.sharedInstanceSystemConfigDataSource().getAllSystemConfig();
            for (SystemConfig system : systems) {
                Cursor systemCursor = db.getAllData(system.getNumberSIM(), system.getNumberSIM());
                listSystems.add(system.getName() + " " + String.format("(%d)", systemCursor.getCount()));
            }
            dataAdapterSystems = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, listSystems);
            filterSpinner.setAdapter(dataAdapterSystems);

            dataAdapterSystems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


            //textViewTitle.setText(String.format("Сообщения от системы (%d)", count_messages));

            lvData.setSelection(lvData.getCount());

            SystemConfig settings = SystemConfigDataSource.getActiveSystem();
            SystemConfig.clearNumNotification();
            settings.setNumNotificationSaved(0);
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.cancelAll();
            filterSpinner.setOnItemSelectedListener(this);
            setSystemMessageCount();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            Log.d("Error", Log.getStackTraceString(e));
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        //((TextView)parent.getChildAt(0)).setTextColor(Color.rgb(249, 249, 249));
        //((TextView)parent.getChildAt(0)).setBackgroundColor(Color.rgb(0, 249, 0));
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        stopManagingCursor(cursor);
        if (pos == 0) {
            cursor = db.getAllData("", "");
        } else {
            //filter
            SelectedIndexOfSystem = pos;
            SystemConfig selectedSystem = systems.get(pos - 1);
            cursor = db.getAllData(selectedSystem.getNumberSIM(), selectedSystem.getNumberSIM2());
        }
        SelectedIndexOfSystem = pos;
        startManagingCursor(cursor);
        scAdapter.changeCursor(cursor);
        setSystemMessageCount();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
        unregisterReceiver(br);
        db.close();

    }

    public void updateListView() {
        if (!cursor.isClosed())
            cursor.requery();
        setSystemMessageCount();
    }

    private void setSystemMessageCount() {
        count_messages = cursor.getCount();
        if (SelectedIndexOfSystem == 0)
            listSystems.set(SelectedIndexOfSystem, String.format("Все сообщения (%d)", count_messages));
        else
            listSystems.set(SelectedIndexOfSystem, String.format(systems.get(SelectedIndexOfSystem - 1).getName() + " (%d)", count_messages));
        lvData.smoothScrollToPosition(lvData.getCount());
    }

    public void onClickClearButton(View v) {
        Log.i(TAG_events, "onClickClearButton");
        if (count_messages > 0) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            final AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Удалить сообщения?");
            b.setPositiveButton("Да", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    db.clearBase();
                    if (!cursor.isClosed())
                        cursor.requery();
                    setSystemMessageCount();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
            b.setNegativeButton("Нет", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            });
            b.show();
        }

    }

    public void clickSaveButton(View v) {

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

            }
        });
        b.show();


    }

    public void SaveMessage() {
        try {
            Date currentDate = new Date();
            Calendar cdate = new GregorianCalendar();
            cdate.setTime(currentDate);
            String filePath = String.format(Locale.US, "/sdcard/messages %d %d %d.doc", cdate.get(Calendar.DAY_OF_MONTH), cdate.get(Calendar.MONTH) + 1, cdate.get(Calendar.YEAR));
            File myFile = new File(filePath);
            myFile.createNewFile();

            Writer myOutWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(myFile), "UTF8"));
            DBSMS ldb = new DBSMS(this);
            ldb.open();
            Cursor lcursor = ldb.getAllData("", "");
            String message;
            String date;
            int nmessage = 1;

            for (lcursor.moveToFirst(); !lcursor.isAfterLast(); lcursor.moveToNext()) {

                message = lcursor.getString(this.cursor.getColumnIndex(DBSMS.COLUMN_TXT));
                date = lcursor.getString(this.cursor.getColumnIndex(DBSMS.COLUMN_DATE));
                myOutWriter.append(String.format("%d %s\n%s\n", nmessage, date, message));
                nmessage++;
            }


            myOutWriter.close();

            Toast.makeText(getApplicationContext(), String.format("Сообщения экспортированы в файл: %s", filePath), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        //stopManagingCursor(cursor);
        //unregisterReceiver(br);
        //db.close();
        //clear count notifications
        SystemConfig settings = SystemConfigDataSource.getActiveSystem();
        SystemConfig.clearNumNotification();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.cancelAll();
        Intent intent = new Intent(MessageSystemActivity.BROADCAST_ACTION_CLEARUNREADEDSMS);
        sendBroadcast(intent);
        settings.setNumNotificationSaved(0);
        super.onBackPressed();
    }
}
