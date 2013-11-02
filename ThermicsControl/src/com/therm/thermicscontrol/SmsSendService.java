package com.therm.thermicscontrol;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;



public class SmsSendService extends Service {

NotificationManager nm;

  final String LOG_TAG = "myLogsSmsSend";
  
  Queue<SMSCommand> sms_to_send=new LinkedList<SMSCommand>();
  
  ExecutorService es;

  public void onCreate() {
    super.onCreate();
    Log.d(LOG_TAG, "SmsSendService onCreate");
    es = Executors.newFixedThreadPool(1);
    nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
  }

  public void onDestroy() {
    super.onDestroy();
    Log.d(LOG_TAG, "SmsSendService onDestroy");
  }

  public void addSMSCommand(SMSCommand command)
  {
	  sms_to_send.add(command);
  }
  
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(LOG_TAG, "SmsSendService onStartCommand");

    //sms_to_send.add(sms);
    //int task = intent.getIntExtra(MainActivity.PARAM_TASK, 0);

    //MyRun mr = new MyRun(startId, time, task);
    //es.execute(mr);
    
    return super.onStartCommand(intent, flags, startId);
  }

  public IBinder onBind(Intent arg0) {
    return null;
  }

  class MyRun implements Runnable {

    int time;
    int startId;
    int task;

    public MyRun(int startId, int time, int task) {
      this.time = time;
      this.startId = startId;
      this.task = task;
      Log.d(LOG_TAG, "MyRun#" + startId + " create");
    }

    public void run() {
    	//sendBroadcast(intent)
//      Intent intent = new Intent(MainActivity.BROADCAST_ACTION);
//      Log.d(LOG_TAG, "MyRun#" + startId + " start, time = " + time);
//      try {
//        // сообщаем об старте задачи
//        intent.putExtra(MainActivity.PARAM_TASK, task);
//        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_START);
//        sendBroadcast(intent);
//
//        // начинаем выполнение задачи
//        TimeUnit.SECONDS.sleep(time);
//
//        // сообщаем об окончании задачи
//        intent.putExtra(MainActivity.PARAM_STATUS, MainActivity.STATUS_FINISH);
//        intent.putExtra(MainActivity.PARAM_RESULT, time * 100);
//        sendBroadcast(intent);
//
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      stop();
    }

    void stop() {
      Log.d(LOG_TAG, "MyRun#" + startId + " end, stopSelfResult("
          + startId + ") = " + stopSelfResult(startId));
    }
  }
}