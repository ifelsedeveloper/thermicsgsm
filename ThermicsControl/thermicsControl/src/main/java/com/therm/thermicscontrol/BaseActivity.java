package com.therm.thermicscontrol;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
//import android.os.Bundle;
//import android.util.Log;
public class BaseActivity extends Activity {
	
	//main value strings
//total
public static final String MYSYSTEM_PREFERENCES = "SystemPrefs";

//main menu
public static final String prefIsGuardian = "isGuardian";

//settings param
public static final String prefTmpWater = "tmpWater";
public static final String prefTmpAir = "tmpAir";
public static final String prefNameRele1 = "nameRele1";
public static final String prefNameRele2 = "nameRele2";
public static final String prefNameRele3 = "nameRele3";
public static final String prefIsRele[] = {"isRele1","isRele2","isRele3"};
public static final String prefIsMicrophone = "isMicrophone";

//config system
public static final String prefNumberSIM = "numberSIM";
public static final String prefNumberSIM2 = "numberSIM2";
public static final String prefIsFirstSimSIM = "isFirstSimSIM";
public static final String prefPinSIM = "pinSIM";
public static final String prefIsDailyReport = "dailyReport";

//sms notification
public static int maxNumberSensorSMSNotification=6;

public static final String [] prefIsSMSNotification = {
	"isSMSNotification1", "isSMSNotification2", "isSMSNotification3",
	"isSMSNotification4", "isSMSNotification5", "isSMSNotification6"};

public static final String [] prefSMSNotificationLowerBound = {
	"SMSNotificationLowerBound1", "SMSNotificationLowerBound2", "SMSNotificationLowerBound3",
	"SMSNotificationLowerBound4", "SMSNotificationLowerBound5", "SMSNotificationLowerBound6"};

public static final String [] prefSMSNotificationUpperBound = {
	"SMSNotificationUpperBound1", "SMSNotificationUpperBound2", "SMSNotificationUpperBound3",
	"SMSNotificationUpperBound4", "SMSNotificationUpperBound5", "SMSNotificationUpperBound6"};
public static final String prefNumberTmpSensorSMS = "numberTmpSensorSMS";

//
public static final String prefIsAutoReport = "autoReport";
public static final String prefUpperBoundTmpSMS = "upperBoundTmpSMS";
public static final String prefLowerBoundTmpSMS = "lowerBoundTmpSMS";

public static final String prefNumberSensorReleWarm = "numberSensorReleWarm";
public static final String prefNumberReleWarm = "numberReleWarm";
public static final String prefTextSMSAlarm = "textSMSAlarm";
public static final String prefIsAutoPowerOffAlarm = "isAutoPowerOffAlarm";
public static final String prefTextSMSTitle = "textSMSTitle";
public static final String prefAdditionalNumber = "additionalNumber";
public static final String prefNameTmpSensor1 = "nameTmpSensor1";
public static final String prefNameTmpSensor2 = "nameTmpSensor2";
public static final String prefNameTmpSensor3 = "nameTmpSensor3";
public static final String prefNameAdditionalSensor4 = "nameAdditionalSensor4";
public static final String prefNameAdditionalSensor5 = "nameAdditionalSensor5";
public static final String prefNameAdditionalSensor6 = "nameAdditionalSensor6";
public static final String prefUSSD = "USSD";
public static final String prefUSSD2 = "USSD2";
public static final String prefIsUpr = "isUpr";
	////
public static final String prefIsDeviceDefaulDevtParam = "isDefaultDevParametrs";
public static final String prefIsSendAnyway = "isSendAnyway";
public static final String prefNumNotification = "numNotification";
public static final String prefIsSMSPostanovka = "isSMSPostanovka";
public static final String prefIsEnableTimer = "isEnableTimer";
public static final String prefIsTimerRunning = "isTimerRunningInt";
public static final String prefIsSMSSnjatie = "isSMSSnjatie";
public static final String prefIsSMSInIncoming = "isSMSInIncoming";
	//for recive sms broadcast action
public final static String BROADCAST_ACTION_RCVSMS = "com.therm.thermicscontrolbroadcastrecivesms";
public final static String BROADCAST_ACTION_CLEARUNREADEDSMS = "com.therm.thermicscontrolbroadcastclearsms";
public final static String BROADCAST_ACTION_RCVPHONECALL = "com.therm.thermicscontrolbroadcastrecivephonecall";
public final static String PARAM_SMS = "sms";
public final static String PARAM_SMSTIME = "time";
public final static String FUNCTION_RELE1 = "function_rele1";
public final static String FUNCTION_RELE2 = "function_rele2";
public final static String FUNCTION_RELE3 = "function_rele3";

public static final String prefTmpReleWarm = "tmpReleWarm";

public static final String [] FUNCTION_TMP_SENSOR = {
	"function_tmp_sensor1", "function_tmp_sensor2", "function_tmp_sensor3",
	"function_tmp_sensor4", "function_tmp_sensor5", "function_tmp_sensor6"};

public static final String [] TMPN_RELE = {
	"tmp1_rele", "tmp2_rele", "tmp3_rele",
	"tmp4_rele", "tmp5_rele", "tmp6_rele"};

public static final String [] TMPN_RELE_NIGHT = {
	"tmp1_rele_night", "tmp2_rele_night", "tmp3_rele_night",
	"tmp4_rele_night", "tmp5_rele_night", "tmp6_rele_night"};

public static final String [] prefTEMP_NIGHT_CONFIG = {
	"temp_night_config11", "tmp1_rele_night", "temp_night_config13","temp_night_config14",
	"temp_night_config21", "tmp2_rele_night", "temp_night_config23","temp_night_config24",
	"temp_night_config31", "tmp3_rele_night", "temp_night_config33","temp_night_config34",
	"temp_night_config41", "tmp4_rele_night", "temp_night_config43","temp_night_config44",
	"temp_night_config51", "tmp5_rele_night", "temp_night_config53","temp_night_config54",
	"temp_night_config61", "tmp6_rele_night", "temp_night_config63","temp_night_config64"};

public static final String [] prefTEMP_DAY_CONFIG = {
	"temp_day_config11", "tmp1_rele", "temp_day_config13","temp_day_config14",
	"temp_day_config21", "tmp2_rele", "temp_day_config23","temp_day_config24",
	"temp_day_config31", "tmp3_rele", "temp_day_config33","temp_day_config34",
	"temp_day_config41", "tmp4_rele", "temp_day_config43","temp_day_config44",
	"temp_day_config51", "tmp5_rele", "temp_day_config53","temp_day_config54",
	"temp_day_config61", "tmp6_rele", "temp_day_config63","temp_day_config64"};

public final static String []NTEMP_CONFIG =  { "ntemp_config1","ntemp_config2", "ntemp_config3" };

public final static String FUNCTION_UPR = "function_upr";

public final static String prefIsSetAutoRele1Control = "setAutoRele1Control";
public static final String [] prefActiveZoneValue = {
	"activeZoneValue1", "activeZoneValue2",
	"activeZoneValue3", "activeZoneValue4"};

public final static String prefContactPhoneNumbers = "contactPhoneNumbers";
public final static String prefContactPhoneNames = "contactPhoneNames";

public final static String prefLastSystemReport = "lastSystemReport";
public final static String prefLastSystemReportTime = "lastSystemReportTime";

public static SystemConfig settings_st;
public final static String prefDeviceVersion = "deviceVersion";

// имена атрибутов для Map
static final String ATTRIBUTE_PHONE = "phone";
static final String ATTRIBUTE_CONTACT_NAME = "contactName";
static final String ATTRIBUTE_POSIITION = "position";
static final String ATTRIBUTE_AVAILABLE = "available";

public final static int deviceBefore01112011 = 1; 
public final static int deviceBefore01112012 = 2;
public final static int deviceAfter01112012 = 3;

public static final String prefIsAutoRequest = "prefIsAutoRequest";
	public static final String prefIsAutoRequestEnabled = "prefIsAutoRequestEnabled";
	public static final String[] prefNumberSensorsReleWarm = {"numberSensorReleWarm1", "numberSensorReleWarm2", "numberSensorReleWarm3"};

public boolean onOptionsItemSelected(MenuItem item) {
//	switch (item.getItemId()) {
//    case R.id.help:
    	//Toast.makeText(getApplicationContext(), "menu", Toast.LENGTH_LONG).show();
    	startActivity(new Intent(this, HelpInformationActivity.class));
//    return true;
//    default:
    return super.onOptionsItemSelected(item);
//}
}

//@Override
//protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//     Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//        @Override
//        public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
//            Log.e("Alert","Lets See if it Works !!!");
//        }
//    });}
}


