package com.therm.thermicscontrol;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import java.lang.Math;

@SuppressLint("NewApi")
public class CSettingsPref {
	
	static SharedPreferences _settings;
	public CSettingsPref(SharedPreferences settings)
	{
		_settings=settings;
	}
	
	//common methods
	public boolean isExist(String param)
	{
		return _settings.contains(param);
	}
	
	//methods for get/set value from settings
	//main menu
	public boolean getIsGuardian()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsGuardian) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsGuardian, result);
		else 
			setIsGuardian(result);
		return result;
	}
	
	public void setIsGuardian(boolean isGuardian)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsGuardian, isGuardian);
		prefEditor.commit();
		return ;
	}
	
	//settings parameters
	//tmp water
	public int getTmpWater()
	{
		int result=(int) 70.0;
		if (_settings.contains(BaseActivity.prefTmpWater) == true)
			 result = _settings.getInt(BaseActivity.prefTmpWater, result);
		else 
			setTmpWater(result);
		return result;
	}
	
	public void setTmpWater(int tmpWater)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefTmpWater, tmpWater);
		prefEditor.commit();
		return ;
	}
	
	public int getDevVersion()
	{
		int result=3;
		if (_settings.contains(BaseActivity.prefDeviceVersion) == true)
			 result = _settings.getInt(BaseActivity.prefDeviceVersion, result);
		else 
			setTmpWater(result);
		return result;
	}
	
	public void setDevVersion(int deviceVersion)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefDeviceVersion, deviceVersion);
		prefEditor.commit();
		return ;
	}
	
	public int getTmpAir()
	{
		int result=(int) 70.0;
		if (_settings.contains(BaseActivity.prefTmpAir) == true)
			 result = _settings.getInt(BaseActivity.prefTmpAir, result);
		else 
			setTmpAir(result);
		return result;
	}
	
	public void setTmpAir(int tmpWater)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefTmpAir, tmpWater);
		prefEditor.commit();
		return ;
	}

	public boolean getIsRele1()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsRele1) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsRele1, result);
		else 
			setIsRele1(result);
		return result;
	}
	
	public void setIsRele1(boolean isRele1)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsRele1, isRele1);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsRele2()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsRele2) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsRele2, result);
		else 
			setIsRele2(result);
		return result;
	}
	
	public void setIsRele2(boolean isRele2)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsRele2, isRele2);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsRele3()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsRele3) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsRele3, result);
		else 
			setIsRele3(result);
		return result;
	}
	
	public void setIsRele3(boolean isRele3)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsRele3, isRele3);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsUpr()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsUpr) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsUpr, result);
		else 
			setIsUpr(result);
		return result;
	}
	
	public void setIsUpr(boolean isUpr)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsUpr, isUpr);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsMicrophone()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsMicrophone) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsMicrophone, result);
		else 
			setIsMicrophone(result);
		return result;
	}
	
	public void setIsMicrophone(boolean isMicrophone)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsMicrophone, isMicrophone);
		prefEditor.commit();
		return ;
	}
	
	public static String phone_number="+7913XXXXXXX";
	//configure system
	public String getNumberSIM()
	{
		String result=phone_number;
		if (_settings.contains(BaseActivity.prefNumberSIM) == true) 
			 result = _settings.getString(BaseActivity.prefNumberSIM, result);
		else 
			setNumberSIM(result);
		return result;
	}
	
	public void setNumberSIM(String numberSIM)
	{
		phone_number = new String( numberSIM);
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefNumberSIM, numberSIM);
		prefEditor.commit();
		return ;
	}
	
	public static String phone_number2="";
	public String getNumberSIM2()
	{
		String result=phone_number2;
		if (_settings.contains(BaseActivity.prefNumberSIM2) == true) 
			 result = _settings.getString(BaseActivity.prefNumberSIM2, result);
		else 
			setNumberSIM2(result);
		return result;
	}
	
	public void setNumberSIM2(String numberSIM2)
	{
		phone_number2.replace(phone_number2, numberSIM2);
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefNumberSIM2, numberSIM2);
		prefEditor.commit();
		return ;
	}
	
	
	public boolean getIsFirstSim()
	{
		boolean result = true;
		if (_settings.contains(BaseActivity.prefIsFirstSimSIM) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsFirstSimSIM, result);
		else 
			setIsFirstSim(result);
		return result;
	}
	
	public static boolean isFirstSIM = true;
	public void setIsFirstSim(boolean value)
	{
		isFirstSIM = value; 
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsFirstSimSIM, value);
		prefEditor.commit();
	}
	
	public String getPinSIM()
	{
		String result="00000";
		if (_settings.contains(BaseActivity.prefPinSIM) == true) 
			 result = _settings.getString(BaseActivity.prefPinSIM, result);
		else 
			setPinSIM(result);
		return result;
	}
	
	public void setPinSIM(String PinSIM)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefPinSIM, PinSIM);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsDailyReport()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsDailyReport) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsDailyReport, result);
		else 
			setIsRele3(result);
		return result;
	}
	
	public void setIsDailyReport(boolean isDailyReport)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsDailyReport, isDailyReport);
		prefEditor.commit();
		return ;
	}
	
	public CNotificationSMS getCNotificationSMS(int number)
	{
		CNotificationSMS result=new CNotificationSMS();

		if ((_settings.contains(BaseActivity.prefIsSMSNotification[number]) == true)
			&&(_settings.contains(BaseActivity.prefSMSNotificationLowerBound[number]) == true)
			&&(_settings.contains(BaseActivity.prefSMSNotificationUpperBound[number]) == true))
		{
			//extract 3 values from preference
			result.enable = _settings.getBoolean(BaseActivity.prefIsSMSNotification[number], result.enable);
			result.lower_bound=_settings.getInt(BaseActivity.prefSMSNotificationLowerBound[number], result.lower_bound);
			result.upper_bound=_settings.getInt(BaseActivity.prefSMSNotificationUpperBound[number], result.upper_bound);
		}
		else 
			setCNotificationSMS(number,result);

		return result;
	}
	
	public void setCNotificationSMS(int number,CNotificationSMS notificationSMS)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		//put 3 values to system preference
		prefEditor.putBoolean(BaseActivity.prefIsSMSNotification[number], notificationSMS.enable);
		prefEditor.putInt(BaseActivity.prefSMSNotificationLowerBound[number], notificationSMS.lower_bound);
		prefEditor.putInt(BaseActivity.prefSMSNotificationUpperBound[number], notificationSMS.upper_bound);
		prefEditor.commit();
		return ;
	}
	
	public int getTempNightConfig(int number)
	{
		int result=0;
		if (_settings.contains(BaseActivity.prefTEMP_NIGHT_CONFIG[number]) == true)
			 result = _settings.getInt(BaseActivity.prefTEMP_NIGHT_CONFIG[number], result);
		else 
			setTempNightConfig(number, result);
		return result;
	}
	
	public void setTempNightConfig(int number, int temp)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefTEMP_NIGHT_CONFIG[number], temp);
		prefEditor.commit();
		return ;
	}
	
	public int getTempDayConfig(int number)
	{
		int result=0;
		if (_settings.contains(BaseActivity.prefTEMP_DAY_CONFIG[number]) == true)
			 result = _settings.getInt(BaseActivity.prefTEMP_DAY_CONFIG[number], result);
		else 
			setTempDayConfig(number, result);
		return result;
	}
	
	public void setTempDayConfig(int number, int temp)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefTEMP_DAY_CONFIG[number], temp);
		prefEditor.commit();
		return ;
	}
	
	public int getNTempConfig()
	{
		int result=1;
		if (_settings.contains(BaseActivity.NTEMP_CONFIG) == true)
			 result = _settings.getInt(BaseActivity.NTEMP_CONFIG, result);
		else
		{
			//set day temp config
			int numberSensorTMPReleWarm=getNumberSensorReleWarm();		
			int tmp_rele_warm=getTmpReleWarm(numberSensorTMPReleWarm-1);
			int tmp_rele_warm_night=getTmpReleWarmNight(numberSensorTMPReleWarm-1);
			setTempDayConfig(result, tmp_rele_warm);
			setTempNightConfig(result, tmp_rele_warm_night);
			setNTempConfig(result);
		}
		return result;
	}
	
	public void setNTempConfig(int nconfig_temp)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.NTEMP_CONFIG, nconfig_temp);
		prefEditor.commit();
		return ;
	}
	
	public int getNumberSensorReleWarm()
	{
		int result=1;
		if (_settings.contains(BaseActivity.prefNumberSensorReleWarm) == true)
			 result = _settings.getInt(BaseActivity.prefNumberSensorReleWarm, result);
		else 
			setNumberSensorReleWarm(result);
		return result;
	}
	
	public void setNumberSensorReleWarm(int numberSensorReleWarm)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberSensorReleWarm, numberSensorReleWarm);
		prefEditor.commit();
		return ;
	}
	
	public int getNumberReleWarm()
	{
		int result=2;
		if (_settings.contains(BaseActivity.prefNumberReleWarm) == true)
			 result = _settings.getInt(BaseActivity.prefNumberReleWarm, result);
		else 
			setNumberReleWarm(result);
		return result;
	}
	
	public void setNumberReleWarm(int numberReleWarm)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberReleWarm, numberReleWarm);
		prefEditor.commit();
		return ;
	}
	
	public String getTextSMSAlarm()
	{
		String result="Тревога! Нарушен охраняемый периметр дома!";
		if (_settings.contains(BaseActivity.prefTextSMSAlarm) == true) 
			 result = _settings.getString(BaseActivity.prefTextSMSAlarm, result);
		else 
			setTextSMSAlarm(result);
		return result;
	}
	
	public void setTextSMSAlarm(String TextSMSAlarm)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefTextSMSAlarm, TextSMSAlarm);
		prefEditor.commit();
		return ;
	}
	
	public String getTextSMSTitle()
	{
		String result="Система KSYTAL :";
		if (_settings.contains(BaseActivity.prefTextSMSTitle) == true) 
			 result = _settings.getString(BaseActivity.prefTextSMSTitle, result);
		else 
			setTextSMSTitle(result);
		return result;
	}
	
	public void setLastSystemReport(String LastSystemReport)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefLastSystemReport, LastSystemReport);
		prefEditor.commit();
		return ;
	}
	
	public String getLastSystemReport()
	{
		String result="";
		if (_settings.contains(BaseActivity.prefLastSystemReport) == true) 
			 result = _settings.getString(BaseActivity.prefLastSystemReport, result);
		else 
			setLastSystemReport(result);
		return result;
	}
	
	public void setLastSystemReportTime(long LastSystemReportTime)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putLong(BaseActivity.prefLastSystemReportTime, LastSystemReportTime);
		prefEditor.commit();
		return ;
	}
	
	public long getLastSystemReportTime()
	{
		long result=0;
		if (_settings.contains(BaseActivity.prefLastSystemReportTime) == true) 
			 result = _settings.getLong(BaseActivity.prefLastSystemReportTime, result);
		else 
			setLastSystemReportTime(result);
		return result;
	}
	
	public void setTextSMSTitle(String TextSMSTitle)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefTextSMSTitle, TextSMSTitle);
		prefEditor.commit();
		return ;
	}
	
	public int getNumberTmpSensorSMS()
	{
		int result=0;
		if (_settings.contains(BaseActivity.prefNumberTmpSensorSMS) == true)
			 result = _settings.getInt(BaseActivity.prefNumberTmpSensorSMS, result);
		else 
			setNumberTmpSensorSMS(result);
		return result;
	}
	
	public void setNumberTmpSensorSMS(int numberTmpSensorSMS)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberTmpSensorSMS, numberTmpSensorSMS);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsDeviceDefaultDevParam()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsDeviceDefaulDevtParam) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsDeviceDefaulDevtParam, result);
		else 
			setIsDeviceDefaultDevParam(result);
		return result;
	}
	
	public void setIsDeviceDefaultDevParam(boolean isDeviceDefaulDevtParam)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsDeviceDefaulDevtParam, isDeviceDefaulDevtParam);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsAutoPowerOnAlarm()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsAutoPowerOffAlarm) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsAutoPowerOffAlarm, result);
		else 
			setIsAutoPowerOnAlarm(result);
		return result;
	}
	
	public void setIsAutoPowerOnAlarm(boolean isAutoPowerOnAlarm)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsAutoPowerOffAlarm, isAutoPowerOnAlarm);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsSendAnyway()
	{
		boolean result=true;
		if (_settings.contains(BaseActivity.prefIsSendAnyway) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsSendAnyway, result);
		else 
			setIsSendAnyway(result);
		return result;
	}
	
	public void setIsSendAnyway(boolean isSendAnyway)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSendAnyway, isSendAnyway);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsSMSPostanovka()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsSMSPostanovka) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsSMSPostanovka, result);
		else 
			setIsSMSPostanovka(result);
		return result;
	}
	
	public void setIsSMSPostanovka(boolean isSMSPostanovka)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSMSPostanovka, isSMSPostanovka);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsEnableTimer()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsEnableTimer) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsEnableTimer, result);
		else 
			setIsEnableTimer(result);
		return result;
	}
	
	public void setIsEnableTimer(boolean isEnableTimer)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsEnableTimer, isEnableTimer);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsTimerRunning(int nrele)
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsTimerRunning) == true) 
		{
			int value = _settings.getInt(BaseActivity.prefIsTimerRunning, 0);
			int check = 1;
			check = check << nrele;
			result = (value & check) > 0;
		}
		else 
			setIsTimerRunning(nrele,result);
		return result;
	}
	public boolean getIsTimerRunning()
	{
		if (_settings.contains(BaseActivity.prefIsTimerRunning)) 
			return _settings.getInt(BaseActivity.prefIsTimerRunning, 0) > 0;
		else return false; 
	}
	
	public void setIsTimerRunningFalse()
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefIsTimerRunning, 0);
		prefEditor.commit();
		prefEditor.apply();
		return ;
	}
	
	public void setIsTimerRunning(int nrele,boolean isTimerRunning)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		int value = _settings.getInt(BaseActivity.prefIsTimerRunning, 0);

		int check = 1;
		check = check << nrele;
		
		if((value & check) > 0 && (!isTimerRunning))
			value = value - check;
		
		if(isTimerRunning)
			value = value | check;
		
		prefEditor.putInt(BaseActivity.prefIsTimerRunning, value);
		prefEditor.commit();
		prefEditor.apply();
		return ;
	}
	
	public boolean getIsSMSSnjatie()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsSMSSnjatie) == true) 
			result = _settings.getBoolean(BaseActivity.prefIsSMSSnjatie, result);
		else 
			setIsSMSSnjatie(result);
		return result;
	}
	
	public void setIsSMSSnjatie(boolean isSMSSnjatie)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSMSSnjatie, isSMSSnjatie);
		prefEditor.commit();
		return ;
	}
	
	public String getUSSDSIM()
	{
		String result="*100#";
		if (_settings.contains(BaseActivity.prefUSSD) == true) 
			 result = _settings.getString(BaseActivity.prefUSSD, result);
		else 
			setUSSDSIM(result);
		return result;
	}
	
	public void setUSSDSIM(String USSD)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefUSSD, USSD);
		prefEditor.commit();
		return ;
	}
	
	public String getUSSDSIM2()
	{
		String result="*100#";
		if (_settings.contains(BaseActivity.prefUSSD2) == true) 
			 result = _settings.getString(BaseActivity.prefUSSD2, result);
		else 
			setUSSDSIM2(result);
		return result;
	}
	
	public void setUSSDSIM2(String USSD2)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefUSSD2, USSD2);
		prefEditor.commit();
		return ;
	}
	
	public String getFunctionRele1()
	{
		String result="Освещение";
		if (_settings.contains(BaseActivity.FUNCTION_RELE1) == true) 
			 result = _settings.getString(BaseActivity.FUNCTION_RELE1, result);
		else 
			setFunctionRele1(result);
		return result;
	}
	
	public void setFunctionRele1(String functionRele1)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE1, functionRele1);
		prefEditor.commit();
		return ;
	}
	
	public String getFunctionRele2()
	{
		String result="Котел";
		if (_settings.contains(BaseActivity.FUNCTION_RELE2) == true) 
			 result = _settings.getString(BaseActivity.FUNCTION_RELE2, result);
		else 
			setFunctionRele2(result);
		return result;
	}
	
	public void setFunctionRele2(String functionRele2)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE2, functionRele2);
		prefEditor.commit();
		return ;
	}
	
	public String getFunctionRele3()
	{
		String result="Сирена";
		if (_settings.contains(BaseActivity.FUNCTION_RELE3) == true) 
			 result = _settings.getString(BaseActivity.FUNCTION_RELE3, result);
		else 
			setFunctionRele3(result);
		return result;
	}
	
	public String getFunctionRele(int i)
	{
		switch(i)
		{
		case 0:
			return getFunctionRele1();
		case 1:
			return getFunctionRele2();
		case 2:
			return getFunctionRele3();
		}
		return "";
	}
	
	public boolean getReleValue(int nrele)
	{
		boolean res = false;
		switch(nrele)
		{
		case 0:
			res = getIsRele1();
		case 1:
			res = getIsRele2();
		case 2:
			res = getIsRele3();
		}

		return res;
	}
	
	public void setFunctionRele3(String functionRele3)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE3, functionRele3);
		prefEditor.commit();
		return ;
	}
	
	public String getFunctionUpr()
	{
		String result="Управление";
		if (_settings.contains(BaseActivity.FUNCTION_UPR) == true) 
			 result = _settings.getString(BaseActivity.FUNCTION_UPR, result);
		else 
			setFunctionUpr(result);
		return result;
	}
	
	public void setFunctionUpr(String functionUpr)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_UPR, functionUpr);
		prefEditor.commit();
		return ;
	}
	
	
	public void setFunctionTmpSensor(int number,String function)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_TMP_SENSOR[number], function);
		prefEditor.commit();
		return ;
	}
	
	static String[] functions_tmp_sensor={"Температура воздуха  котельной","Температура воды","Температура  воздуха","Доп. датчик 4","Доп. датчик 5","Доп. датчик 6"};
	public String getFunctionTmpSensor(int number)
	{
		String result = functions_tmp_sensor[number];
		if (_settings.contains(BaseActivity.FUNCTION_TMP_SENSOR[number]) == true) 
			 result = _settings.getString(BaseActivity.FUNCTION_TMP_SENSOR[number], result);
		else
			setFunctionTmpSensor(number,result);
		
		return result;
	}
	
	public boolean getIsSetAutoRele1Control()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsSetAutoRele1Control) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsSetAutoRele1Control, result);
		else 
			setIsSetAutoRele1Control(result);
		return result;
	}
	
	public void setIsSetAutoRele1Control(boolean isSetAutoRele1Control)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSetAutoRele1Control, isSetAutoRele1Control);
		prefEditor.commit();
		return ;
	}
	
	public boolean getIsSMSInIncoming()
	{
		boolean result=false;
		if (_settings.contains(BaseActivity.prefIsSMSInIncoming) == true) 
			 result = _settings.getBoolean(BaseActivity.prefIsSMSInIncoming, result);
		else 
			setIsSMSInIncoming(result);
		return result;
	}
	
	public void setIsSMSInIncoming(boolean isSMSInIncoming)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSMSInIncoming, isSMSInIncoming);
		prefEditor.commit();
		return ;
	}
	
	public  ArrayList<Map<String, String>> getNumbersBroadcast()
	{
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		int i;
		for(i=0;i<10;i++)
		{
			String prefContactPhoneNumber =String.format(Locale.US,"contactPhoneNumber%d", i);
			String prefContactPhoneName =String.format(Locale.US,"contactPhoneName%d", i);
			String prefContactPhonePos =String.format(Locale.US,"contactPhonePos%d", i);
			String prefContactPhoneAvailable =String.format(Locale.US,"contactPhoneAvailable%d", i);
			if (_settings.contains(prefContactPhoneNumber) == true)
			{
				String phone_number = _settings.getString(prefContactPhoneNumber, "number");
				String contact_name = _settings.getString(prefContactPhoneName, "name");
				String phone_pos = _settings.getString(prefContactPhonePos, "0");
				String phone_available = _settings.getString(prefContactPhoneAvailable, "1");
				HashMap<String,String> contact = new HashMap<String, String>();
				contact.put(BaseActivity.ATTRIBUTE_PHONE, phone_number);
				contact.put(BaseActivity.ATTRIBUTE_CONTACT_NAME, contact_name);
				contact.put(BaseActivity.ATTRIBUTE_POSIITION, phone_pos);
				contact.put(BaseActivity.ATTRIBUTE_AVAILABLE, phone_available);
				
				if(Integer.parseInt(phone_available)==0)
					result.add(contact);
			}
		}
		
		return result;
	}
	
	public void setNumbersBroadcast(ArrayList<Map<String, String>> numbers)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
//		int i=0;
		for(Map<String, String> map : numbers)
		{
//			if(Integer.parseInt(map.get(BaseActivity.ATTRIBUTE_POSIITION)) == i)
//			{
				int i = Integer.parseInt( map.get(BaseActivity.ATTRIBUTE_POSIITION) );
			
				String prefContactPhoneNumber =String.format(Locale.US,"contactPhoneNumber%d", i);
				String prefContactPhoneName =String.format(Locale.US,"contactPhoneName%d", i);
				String prefContactPhonePos =String.format(Locale.US,"contactPhonePos%d", i);
				String prefContactPhoneAvailable =String.format(Locale.US,"contactPhoneAvailable%d", i);
				
				prefEditor.putString(prefContactPhoneNumber, map.get(BaseActivity.ATTRIBUTE_PHONE));
				prefEditor.putString(prefContactPhoneName, map.get(BaseActivity.ATTRIBUTE_CONTACT_NAME));
				prefEditor.putString(prefContactPhonePos, map.get(BaseActivity.ATTRIBUTE_POSIITION));
				prefEditor.putString(prefContactPhoneAvailable, map.get(BaseActivity.ATTRIBUTE_AVAILABLE));
//			}
//			i++;
//			if(i>9) break;
		}
		prefEditor.commit();
	}
	
	public int [] getActiveZoneValue()
	{
		int [] result = {1,1,1,1};
		for(int i=0;i<4;i++)
		{
			if (_settings.contains(BaseActivity.prefActiveZoneValue[i]) == true) 
				result[i] = _settings.getInt(BaseActivity.prefActiveZoneValue[i], result[i]);
			else
			{
				setActiveZoneValue(result);
				break;
			}
		}

		return result;
	}
	
	public void setActiveZoneValue(int [] activeZone)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		for(int i=0;i<4;i++)
		{
			prefEditor.putInt(BaseActivity.prefActiveZoneValue[i],activeZone[i]);
		}
		prefEditor.commit();
	}
	
	//settings parameters
		public int getTmpReleWarm(int n_sensor)
		{
			if(n_sensor>-1 && n_sensor<6)
			{
				int result=0;
				if (_settings.contains(BaseActivity.TMPN_RELE[n_sensor]) == true)
					 result = _settings.getInt(BaseActivity.TMPN_RELE[n_sensor], result);
				else 
					setTmpReleWarm(n_sensor,result);
				return result;
			}
			else return 0;
		}
		
		public void setTmpReleWarm(int n_sensor,int tmpReleWarm)
		{
			SharedPreferences.Editor prefEditor = _settings.edit();
			prefEditor.putInt(BaseActivity.TMPN_RELE[n_sensor], tmpReleWarm);
			prefEditor.commit();
			return ;
		}
	
		//settings parameters
		public int getTmpReleWarmNight(int n_sensor)
		{
			if(n_sensor>-1 && n_sensor<6)
			{
				int result=0;
				if (_settings.contains(BaseActivity.TMPN_RELE_NIGHT[n_sensor]) == true)
					 result = _settings.getInt(BaseActivity.TMPN_RELE_NIGHT[n_sensor], result);
				else 
					setTmpReleWarmNight(n_sensor,result);
				return result;
			}
			else return 0;
		}
		
		public void setTmpReleWarmNight(int n_sensor,int tmpReleWarm)
		{
			SharedPreferences.Editor prefEditor = _settings.edit();
			prefEditor.putInt(BaseActivity.TMPN_RELE_NIGHT[n_sensor], tmpReleWarm);
			prefEditor.commit();
			return ;
		}
		
	static public int getNumNotification()
	{
//		int result=0;
//		if (_settings.contains(BaseActivity.prefNumNotification) == true) 
//			 result = _settings.getInt(BaseActivity.prefNumNotification, result);
//		else 
//			setNumNotification(result);
//		return result;
		return CSettingsPref.numNotification_.get();
	}
	
	static AtomicInteger  numNotification_=new AtomicInteger(0);
	static public void setNumNotification(int numNotification)
	{
//		SharedPreferences.Editor prefEditor = _settings.edit();
//		prefEditor.putInt(BaseActivity.prefNumNotification, numNotification);
//		prefEditor.commit();
		CSettingsPref.numNotification_.set(numNotification);
		return ;
	}
	static public void incNumNotification()
	{
//		int num =getNumNotification();
//		num++;
//		SharedPreferences.Editor prefEditor = _settings.edit();
//		prefEditor.putInt(BaseActivity.prefNumNotification, num);
//		prefEditor.commit();
		CSettingsPref.numNotification_.incrementAndGet();
		return ;
	}
	static public void clearNumNotification()
	{
//		int num = 0;
//		SharedPreferences.Editor prefEditor = _settings.edit();
//		prefEditor.putInt(BaseActivity.prefNumNotification, num);
//	 	boolean res = prefEditor.commit();
//	 	while(!res)
//	 		res = prefEditor.commit();
		CSettingsPref.numNotification_.set(0);
		return ;
	}
	
	public int getNumNotificationSaved()
	{
		int result=0;
		if (_settings.contains(BaseActivity.prefNumNotification) == true) 
			 result = _settings.getInt(BaseActivity.prefNumNotification, result);
		else 
			setNumNotification(result);
		return result;

	}
	
	public void setNumNotificationSaved(int numNotification)
	{
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumNotification, numNotification);
		prefEditor.commit();
		return ;
	}
}
