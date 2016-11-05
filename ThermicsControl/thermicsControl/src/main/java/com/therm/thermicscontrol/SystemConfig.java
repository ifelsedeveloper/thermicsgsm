package com.therm.thermicscontrol;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import timber.log.Timber;

public class SystemConfig {

	private long _id;
	SharedPreferences _settings;
	private String _name;
	private String _tableTimer;
	private String _tableAutorequest;

	public SystemConfig(long idSystem) {
		Timber.i("event_tag_settings_param", "id system = " + Long.toString(idSystem));
		if(idSystem == 1) {
			_settings = ContextApplication.getAppContext().getSharedPreferences(
					BaseActivity.MYSYSTEM_PREFERENCES, Context.MODE_MULTI_PROCESS);
		} else {
			_settings = ContextApplication.getAppContext().getSharedPreferences(
					Long.toString(idSystem), Context.MODE_MULTI_PROCESS);
		}
	}

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getTableTimer() {
		return _tableTimer;
	}

	public void setTableTimer(String tableTimer) {
		_tableTimer = tableTimer;
	}

	public String getTableAutoRequest() {
		return _tableAutorequest;
	}

	public void setTableAutoRequest(String tableAutorequest) {
		_tableAutorequest = tableAutorequest;
	}

	// common methods
	public boolean isExist(String param) {
		return _settings.contains(param);
	}

	// methods for get/set value from settings
	// main menu
	public boolean getIsGuardian() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsGuardian) == true)
			result = _settings.getBoolean(BaseActivity.prefIsGuardian, result);
		else
			setIsGuardian(result);
		return result;
	}

	public void setIsGuardian(boolean isGuardian) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsGuardian, isGuardian);
		prefEditor.commit();
		return;
	}

	// settings parameters
	// tmp water
	public int getTmpWater() {
		int result = (int) 70.0;
		if (_settings.contains(BaseActivity.prefTmpWater) == true)
			result = _settings.getInt(BaseActivity.prefTmpWater, result);
		else
			setTmpWater(result);
		return result;
	}

	public void setTmpWater(int tmpWater) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefTmpWater, tmpWater);
		prefEditor.commit();
		return;
	}

	public int getDevVersion() {
		int result = 3;
		if (_settings.contains(BaseActivity.prefDeviceVersion) == true)
			result = _settings.getInt(BaseActivity.prefDeviceVersion, result);
		else
			setTmpWater(result);
		return result;
	}

	public void setDevVersion(int deviceVersion) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefDeviceVersion, deviceVersion);
		prefEditor.commit();

		return;
	}

	public int getTmpAir() {
		int result = (int) 70.0;
		if (_settings.contains(BaseActivity.prefTmpAir) == true)
			result = _settings.getInt(BaseActivity.prefTmpAir, result);
		else
			setTmpAir(result);
		return result;
	}

	public void setTmpAir(int tmpWater) {
		_settings.edit().putInt(BaseActivity.prefTmpAir, tmpWater).commit();
	}

	public static int numReles = 3;

	public boolean getIsRele(int nrele) {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsRele[nrele]) == true)
			result = _settings.getBoolean(BaseActivity.prefIsRele[nrele],
					result);
		else
			setIsRele(nrele, result);
		Timber.i("event_tag_settings_param","get " + Long.toString(_id)+" " + getName()+" | "+ Integer.toString(nrele)+" | "+ Boolean.toString(result));
		return result;
	}

	public void setIsRele(int nrele, boolean isRele) {
		Timber.i("event_tag_settings_param", "set " + Long.toString(_id)+" " + getName()+" | "+ Integer.toString(nrele)+" | "+ Boolean.toString(isRele));
		_settings.edit().putBoolean(BaseActivity.prefIsRele[nrele], isRele).commit();
	}

	public boolean getIsUpr() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsUpr) == true)
			result = _settings.getBoolean(BaseActivity.prefIsUpr, result);
		else
			setIsUpr(result);
		return result;
	}

	public void setIsUpr(boolean isUpr) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsUpr, isUpr);
		prefEditor.commit();
		//prefEditor.apply();
		return;
	}

	public boolean getIsMicrophone() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsMicrophone) == true)
			result = _settings
					.getBoolean(BaseActivity.prefIsMicrophone, result);
		else
			setIsMicrophone(result);
		return result;
	}

	public void setIsMicrophone(boolean isMicrophone) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsMicrophone, isMicrophone);
		prefEditor.commit();
		//prefEditor.apply();
		return;
	}

	public static String phone_number = "+7913XXXXXXX";

	// configure system
	public String getNumberSIM() {
		String result = phone_number;
		if (_settings.contains(BaseActivity.prefNumberSIM) == true)
			result = _settings.getString(BaseActivity.prefNumberSIM, result);
		else
			setNumberSIM(result);
		return result;
	}

	public void setNumberSIM(String numberSIM) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefNumberSIM, numberSIM);
		prefEditor.commit();
		return;
	}

	public static String phone_number2 = "";

	public String getNumberSIM2() {
		String result = phone_number2;
		if (_settings.contains(BaseActivity.prefNumberSIM2) == true)
			result = _settings.getString(BaseActivity.prefNumberSIM2, result);
		else
			setNumberSIM2(result);
		return result;
	}

	public void setNumberSIM2(String numberSIM2) {
		phone_number2.replace(phone_number2, numberSIM2);
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefNumberSIM2, numberSIM2);
		prefEditor.commit();
		return;
	}

	public boolean getIsFirstSim() {
		boolean result = true;
		if (_settings.contains(BaseActivity.prefIsFirstSimSIM) == true)
			result = _settings.getBoolean(BaseActivity.prefIsFirstSimSIM,
					result);
		else
			setIsFirstSim(result);
		return result;
	}

	public static boolean isFirstSIM = true;

	public void setIsFirstSim(boolean value) {
		isFirstSIM = value;
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsFirstSimSIM, value);
		prefEditor.commit();
	}

	public String getPinSIM() {
		String result = "00000";
		if (_settings.contains(BaseActivity.prefPinSIM) == true)
			result = _settings.getString(BaseActivity.prefPinSIM, result);
		else
			setPinSIM(result);
		return result;
	}

	public void setPinSIM(String PinSIM) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefPinSIM, PinSIM);
		prefEditor.commit();
		return;
	}

	public boolean getIsDailyReport() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsDailyReport) == true)
			result = _settings.getBoolean(BaseActivity.prefIsDailyReport,
					result);
		else
			setIsRele(2, result);
		return result;
	}

	public void setIsDailyReport(boolean isDailyReport) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsDailyReport, isDailyReport);
		prefEditor.commit();
		return;
	}

	public CNotificationSMS getCNotificationSMS(int number) {
		CNotificationSMS result = new CNotificationSMS();

		if ((_settings.contains(BaseActivity.prefIsSMSNotification[number]) == true)
				&& (_settings
						.contains(BaseActivity.prefSMSNotificationLowerBound[number]) == true)
				&& (_settings
						.contains(BaseActivity.prefSMSNotificationUpperBound[number]) == true)) {
			// extract 3 values from preference
			result.enable = _settings.getBoolean(
					BaseActivity.prefIsSMSNotification[number], result.enable);
			result.lower_bound = _settings.getInt(
					BaseActivity.prefSMSNotificationLowerBound[number],
					result.lower_bound);
			result.upper_bound = _settings.getInt(
					BaseActivity.prefSMSNotificationUpperBound[number],
					result.upper_bound);
		} else
			setCNotificationSMS(number, result);

		return result;
	}

	public void setCNotificationSMS(int number, CNotificationSMS notificationSMS) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		// put 3 values to system preference
		prefEditor.putBoolean(BaseActivity.prefIsSMSNotification[number],
				notificationSMS.enable);
		prefEditor.putInt(BaseActivity.prefSMSNotificationLowerBound[number],
				notificationSMS.lower_bound);
		prefEditor.putInt(BaseActivity.prefSMSNotificationUpperBound[number],
				notificationSMS.upper_bound);
		prefEditor.commit();
		return;
	}

	public final static int numberSensors = 7;
	public final static int numberButtons = 4;

	private int getPosConfigButton(int nsensor, int nbutton) {
		if (nsensor > 0)
			return (nsensor - 1) * numberButtons + nbutton;
		else
			return -1;
	}

	public int getTempNightConfig(int nsensor, int nbutton) {
		int result = 0;
		int pos = getPosConfigButton(nsensor, nbutton);
		if (pos > -1) {
			if (_settings.contains(BaseActivity.prefTEMP_NIGHT_CONFIG[pos]) == true)
				result = _settings.getInt(
						BaseActivity.prefTEMP_NIGHT_CONFIG[pos], result);
			else
				setTempNightConfig(nsensor, nbutton, result);
		}
		return result;
	}

	public void setTempNightConfig(int nsensor, int nbutton, int temp) {
		int pos = getPosConfigButton(nsensor, nbutton);
		if (pos > -1) {
			SharedPreferences.Editor prefEditor = _settings.edit();
			prefEditor.putInt(BaseActivity.prefTEMP_NIGHT_CONFIG[pos], temp);
			prefEditor.commit();
			//prefEditor.apply();
		}
		return;
	}

	public int getTempDayConfig(int nsensor, int nbutton) {
		int result = 0;
		int pos = getPosConfigButton(nsensor, nbutton);
		if (pos > -1) {
			if (_settings.contains(BaseActivity.prefTEMP_DAY_CONFIG[pos]) == true)
				result = _settings.getInt(
						BaseActivity.prefTEMP_DAY_CONFIG[pos], result);
			else
				setTempDayConfig(nsensor, nbutton, result);
		}
		return result;
	}

	public void setTempDayConfig(int nsensor, int nbutton, int temp) {
		int pos = getPosConfigButton(nsensor, nbutton);
		if(pos == 0) Log.d("save parameters 2", "setTempDayConfig 0");
		if (pos > -1) {
			SharedPreferences.Editor prefEditor = _settings.edit();
			prefEditor.putInt(BaseActivity.prefTEMP_DAY_CONFIG[pos], temp);
			prefEditor.commit();
			//prefEditor.apply();
		}
		return;
	}
	
	public void setActiveReleTemp(int dayTemp, int nightTemp){
		int nButton = getNTempConfig(1);
		int nSensor = getNumberSensorReleWarm();
		setTempDayConfig(nSensor,nButton,dayTemp);
		setTempNightConfig(nSensor,nButton,nightTemp);
	}
	

	public int getNTempConfig(int nrele) {
		int result = 1;
		if (_settings.contains(BaseActivity.NTEMP_CONFIG[nrele]) == true)
			result = _settings.getInt(BaseActivity.NTEMP_CONFIG[nrele], result);
		else {
			// set day temp config
			int numberSensorTMPReleWarm = getNumberSensorReleWarm();
			int tmp_rele_warm = getTempDayConfig(numberSensorTMPReleWarm,
					result);
			int tmp_rele_warm_night = getTempNightConfig(
					numberSensorTMPReleWarm, result);
			setTempDayConfig(getNumberSensorReleWarm(), result, tmp_rele_warm);
			setTempNightConfig(getNumberSensorReleWarm(), result, tmp_rele_warm_night);
			setNTempConfig(nrele, result);
		}
		return result;
	}

	public void setNTempConfig(int nrele ,int nconfig_temp) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.NTEMP_CONFIG[nrele], nconfig_temp);
		prefEditor.commit();
		//prefEditor.apply();
		return;
	}

	public int getNumberSensorReleWarm() {
		int result = 0;
		if (_settings.contains(BaseActivity.prefNumberSensorReleWarm) == true)
			result = _settings.getInt(BaseActivity.prefNumberSensorReleWarm,
					result);
		else
			setNumberSensorReleWarm(result);
		return result;
	}

	public void setNumberSensorReleWarm(int numberSensorReleWarm) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberSensorReleWarm,
				numberSensorReleWarm);
		prefEditor.commit();
		prefEditor.apply();
		return;
	}

	public int getNumberSensorReleWarm(int nRele) {
		int result = 0;
		if (this._settings.contains(BaseActivity.prefNumberSensorsReleWarm[nRele])) {
			return this._settings.getInt(BaseActivity.prefNumberSensorsReleWarm[nRele], 0);
		}
		if (getNumberReleWarm() == nRele + 1) {
			result = getNumberSensorReleWarm();
		}
		setNumberSensorReleWarm(nRele, result);
		return result;
	}

	public void setNumberSensorReleWarm(int nRele, int nSensor) {
		SharedPreferences.Editor prefEditor = this._settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberSensorsReleWarm[nRele], nSensor);
		prefEditor.commit();
		prefEditor.apply();
	}

	public int getNumberReleWarm() {
		int result = 2;
		if (_settings.contains(BaseActivity.prefNumberReleWarm) == true)
			result = _settings.getInt(BaseActivity.prefNumberReleWarm, result);
		else
			setNumberReleWarm(result);
		return result;
	}

	public void setNumberReleWarm(int numberReleWarm) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberReleWarm, numberReleWarm);
		prefEditor.commit();
		prefEditor.apply();
		return;
	}

	public String getTextSMSAlarm() {
		String result = "Тревога! Нарушен охраняемый периметр дома!";
		if (_settings.contains(BaseActivity.prefTextSMSAlarm) == true)
			result = _settings.getString(BaseActivity.prefTextSMSAlarm, result);
		else
			setTextSMSAlarm(result);
		return result;
	}

	public void setTextSMSAlarm(String TextSMSAlarm) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefTextSMSAlarm, TextSMSAlarm).commit();
		return;
	}

	public String getTextSMSTitle() {
		String result = "Система KSYTAL :";
		if (_settings.contains(BaseActivity.prefTextSMSTitle) == true)
			result = _settings.getString(BaseActivity.prefTextSMSTitle, result);
		else
			setTextSMSTitle(result);
		return result;
	}

	public void setLastSystemReport(String LastSystemReport) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefLastSystemReport,
				LastSystemReport).commit();
		return;
	}

	public String getLastSystemReport() {
		String result = "";
		if (_settings.contains(BaseActivity.prefLastSystemReport) == true)
			result = _settings.getString(BaseActivity.prefLastSystemReport,
					result);
		else
			setLastSystemReport(result);
		return result;
	}

	public void setLastSystemReportTime(long LastSystemReportTime) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putLong(BaseActivity.prefLastSystemReportTime,
				LastSystemReportTime);
		prefEditor.commit();
		return;
	}

	public long getLastSystemReportTime() {
		long result = 0;
		if (_settings.contains(BaseActivity.prefLastSystemReportTime) == true)
			result = _settings.getLong(BaseActivity.prefLastSystemReportTime,
					result);
		else
			setLastSystemReportTime(result);
		return result;
	}

	public void setTextSMSTitle(String TextSMSTitle) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefTextSMSTitle, TextSMSTitle);
		prefEditor.commit();
		return;
	}

	public int getNumberTmpSensorSMS() {
		int result = 0;
		if (_settings.contains(BaseActivity.prefNumberTmpSensorSMS) == true)
			result = _settings.getInt(BaseActivity.prefNumberTmpSensorSMS,
					result);
		else
			setNumberTmpSensorSMS(result);
		return result;
	}

	public void setNumberTmpSensorSMS(int numberTmpSensorSMS) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefNumberTmpSensorSMS,
				numberTmpSensorSMS);
		prefEditor.commit();
		return;
	}

	public boolean getIsDeviceDefaultDevParam() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsDeviceDefaulDevtParam) == true)
			result = _settings.getBoolean(
					BaseActivity.prefIsDeviceDefaulDevtParam, result);
		else
			setIsDeviceDefaultDevParam(result);
		return result;
	}

	public void setIsDeviceDefaultDevParam(boolean isDeviceDefaulDevtParam) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsDeviceDefaulDevtParam,
				isDeviceDefaulDevtParam);
		prefEditor.commit();
		return;
	}

	public boolean getIsAutoPowerOnAlarm() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsAutoPowerOffAlarm) == true)
			result = _settings.getBoolean(BaseActivity.prefIsAutoPowerOffAlarm,
					result);
		else
			setIsAutoPowerOnAlarm(result);
		return result;
	}

	public void setIsAutoPowerOnAlarm(boolean isAutoPowerOnAlarm) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsAutoPowerOffAlarm,
				isAutoPowerOnAlarm);
		prefEditor.commit();
		return;
	}

	public boolean getIsSendAnyway() {
		boolean result = true;
		if (_settings.contains(BaseActivity.prefIsSendAnyway) == true)
			result = _settings
					.getBoolean(BaseActivity.prefIsSendAnyway, result);
		else
			setIsSendAnyway(result);
		return result;
	}

	public void setIsSendAnyway(boolean isSendAnyway) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSendAnyway, isSendAnyway);
		prefEditor.commit();
		return;
	}

	public boolean getIsSMSPostanovka() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsSMSPostanovka) == true)
			result = _settings.getBoolean(BaseActivity.prefIsSMSPostanovka,
					result);
		else
			setIsSMSPostanovka(result);
		return result;
	}

	public void setIsSMSPostanovka(boolean isSMSPostanovka) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor
				.putBoolean(BaseActivity.prefIsSMSPostanovka, isSMSPostanovka);
		prefEditor.commit();
		return;
	}

	public boolean getIsEnableTimer() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsEnableTimer) == true)
			result = _settings.getBoolean(BaseActivity.prefIsEnableTimer,
					result);
		else
			setIsEnableTimer(result);
		return result;
	}

	public void setIsEnableTimer(boolean isEnableTimer) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsEnableTimer, isEnableTimer);
		prefEditor.commit();
		return;
	}

	public boolean getIsTimerRunning(int nrele) {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsTimerRunning) == true) {
			int value = _settings.getInt(BaseActivity.prefIsTimerRunning, 0);
			int check = 1;
			check = check << nrele;
			result = (value & check) > 0;
		} else
			setIsTimerRunning(nrele, result);
		return result;
	}

	public boolean getIsTimerRunning() {
		if (_settings.contains(BaseActivity.prefIsTimerRunning))
			return _settings.getInt(BaseActivity.prefIsTimerRunning, 0) > 0;
		else
			return false;
	}

	public void setIsTimerRunningFalse() {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putInt(BaseActivity.prefIsTimerRunning, 0);
		prefEditor.commit();
		return;
	}

	public void setIsTimerRunning(int nrele, boolean isTimerRunning) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		int value = _settings.getInt(BaseActivity.prefIsTimerRunning, 0);

		int check = 1;
		check = check << nrele;

		if ((value & check) > 0 && (!isTimerRunning))
			value = value - check;

		if (isTimerRunning)
			value = value | check;

		prefEditor.putInt(BaseActivity.prefIsTimerRunning, value);
		prefEditor.commit();

		return;
	}

	public boolean getIsSMSSnjatie() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsSMSSnjatie) == true)
			result = _settings
					.getBoolean(BaseActivity.prefIsSMSSnjatie, result);
		else
			setIsSMSSnjatie(result);
		return result;
	}

	public void setIsSMSSnjatie(boolean isSMSSnjatie) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSMSSnjatie, isSMSSnjatie);
		prefEditor.commit();
		return;
	}

	public String getUSSDSIM() {
		String result = "*100#";
		if (_settings.contains(BaseActivity.prefUSSD) == true)
			result = _settings.getString(BaseActivity.prefUSSD, result);
		else
			setUSSDSIM(result);
		return result;
	}

	public void setUSSDSIM(String USSD) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefUSSD, USSD);
		prefEditor.commit();
		return;
	}

	public String getUSSDSIM2() {
		String result = "*100#";
		if (_settings.contains(BaseActivity.prefUSSD2) == true)
			result = _settings.getString(BaseActivity.prefUSSD2, result);
		else
			setUSSDSIM2(result);
		return result;
	}

	public void setUSSDSIM2(String USSD2) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.prefUSSD2, USSD2);
		prefEditor.commit();
		return;
	}

	public String getFunctionRele1() {
		String result = "Освещение";
		if (_settings.contains(BaseActivity.FUNCTION_RELE1) == true)
			result = _settings.getString(BaseActivity.FUNCTION_RELE1, result);
		else
			setFunctionRele1(result);
		return result;
	}

	public void setFunctionRele1(String functionRele1) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE1, functionRele1);
		prefEditor.commit();
		return;
	}

	public String getFunctionRele2() {
		String result = "Котел";
		if (_settings.contains(BaseActivity.FUNCTION_RELE2) == true)
			result = _settings.getString(BaseActivity.FUNCTION_RELE2, result);
		else
			setFunctionRele2(result);
		return result;
	}

	public void setFunctionRele2(String functionRele2) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE2, functionRele2);
		prefEditor.commit();
		return;
	}

	public String getFunctionRele3() {
		String result = "Сирена";
		if (_settings.contains(BaseActivity.FUNCTION_RELE3) == true)
			result = _settings.getString(BaseActivity.FUNCTION_RELE3, result);
		else
			setFunctionRele3(result);
		return result;
	}

	public String getFunctionRele(int i) {
		switch (i) {
		case 0:
			return getFunctionRele1();
		case 1:
			return getFunctionRele2();
		case 2:
			return getFunctionRele3();
		}
		return "";
	}

	public void setFunctionRele3(String functionRele3) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_RELE3, functionRele3);
		prefEditor.commit();
		return;
	}

	public String getFunctionUpr() {
		String result = "Управление";
		if (_settings.contains(BaseActivity.FUNCTION_UPR) == true)
			result = _settings.getString(BaseActivity.FUNCTION_UPR, result);
		else
			setFunctionUpr(result);
		return result;
	}

	public void setFunctionUpr(String functionUpr) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putString(BaseActivity.FUNCTION_UPR, functionUpr);
		prefEditor.commit();
		return;
	}

	public void setFunctionTmpSensor(int number, String function) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor
				.putString(BaseActivity.FUNCTION_TMP_SENSOR[number], function);
		prefEditor.commit();
		return;
	}

	static String[] functions_tmp_sensor = { "Температура воздуха  котельной",
			"Температура воды", "Температура  воздуха", "Доп. датчик 4",
			"Доп. датчик 5", "Доп. датчик 6" };

	public String getFunctionTmpSensor(int number) {
		String result = functions_tmp_sensor[number];
		if (_settings.contains(BaseActivity.FUNCTION_TMP_SENSOR[number]) == true)
			result = _settings.getString(
					BaseActivity.FUNCTION_TMP_SENSOR[number], result);
		else
			setFunctionTmpSensor(number, result);

		return result;
	}

	public boolean getIsSetAutoRele1Control() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsSetAutoRele1Control) == true)
			result = _settings.getBoolean(
					BaseActivity.prefIsSetAutoRele1Control, result);
		else
			setIsSetAutoRele1Control(result);
		return result;
	}

	public void setIsSetAutoRele1Control(boolean isSetAutoRele1Control) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsSetAutoRele1Control,
				isSetAutoRele1Control);
		prefEditor.commit();
		return;
	}

	public boolean getIsSMSInIncoming() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsSMSInIncoming) == true)
			result = _settings.getBoolean(BaseActivity.prefIsSMSInIncoming,
					result);
		else
			setIsSMSInIncoming(result);
		return result;
	}

	public void setIsSMSInIncoming(boolean isSMSInIncoming) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor
				.putBoolean(BaseActivity.prefIsSMSInIncoming, isSMSInIncoming);
		prefEditor.commit();
		return;
	}

	public ArrayList<Map<String, String>> getNumbersBroadcast() {
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		int i;
		for (i = 0; i < 10; i++) {
			String prefContactPhoneNumber = String.format(Locale.US,
					"contactPhoneNumber%d", i);
			String prefContactPhoneName = String.format(Locale.US,
					"contactPhoneName%d", i);
			String prefContactPhonePos = String.format(Locale.US,
					"contactPhonePos%d", i);
			String prefContactPhoneAvailable = String.format(Locale.US,
					"contactPhoneAvailable%d", i);
			if (_settings.contains(prefContactPhoneNumber) == true) {
				String phone_number = _settings.getString(
						prefContactPhoneNumber, "number");
				String contact_name = _settings.getString(prefContactPhoneName,
						"name");
				String phone_pos = _settings
						.getString(prefContactPhonePos, "0");
				String phone_available = _settings.getString(
						prefContactPhoneAvailable, "1");
				HashMap<String, String> contact = new HashMap<String, String>();
				contact.put(BaseActivity.ATTRIBUTE_PHONE, phone_number);
				contact.put(BaseActivity.ATTRIBUTE_CONTACT_NAME, contact_name);
				contact.put(BaseActivity.ATTRIBUTE_POSIITION, phone_pos);
				contact.put(BaseActivity.ATTRIBUTE_AVAILABLE, phone_available);

				if (Integer.parseInt(phone_available) == 0)
					result.add(contact);
			}
		}

		return result;
	}

	public void setNumbersBroadcast(ArrayList<Map<String, String>> numbers) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		for (Map<String, String> map : numbers) {
			int i = Integer.parseInt(map.get(BaseActivity.ATTRIBUTE_POSIITION));

			String prefContactPhoneNumber = String.format(Locale.US,
					"contactPhoneNumber%d", i);
			String prefContactPhoneName = String.format(Locale.US,
					"contactPhoneName%d", i);
			String prefContactPhonePos = String.format(Locale.US,
					"contactPhonePos%d", i);
			String prefContactPhoneAvailable = String.format(Locale.US,
					"contactPhoneAvailable%d", i);

			prefEditor.putString(prefContactPhoneNumber,
					map.get(BaseActivity.ATTRIBUTE_PHONE));
			prefEditor.putString(prefContactPhoneName,
					map.get(BaseActivity.ATTRIBUTE_CONTACT_NAME));
			prefEditor.putString(prefContactPhonePos,
					map.get(BaseActivity.ATTRIBUTE_POSIITION));
			prefEditor.putString(prefContactPhoneAvailable,
					map.get(BaseActivity.ATTRIBUTE_AVAILABLE));

		}
		prefEditor.commit();
	}

	public int[] getActiveZoneValue() {
		int[] result = { 1, 1, 1, 1 };
		for (int i = 0; i < 4; i++) {
			if (_settings.contains(BaseActivity.prefActiveZoneValue[i]) == true)
				result[i] = _settings.getInt(
						BaseActivity.prefActiveZoneValue[i], result[i]);
			else {
				setActiveZoneValue(result);
				break;
			}
		}

		return result;
	}

	public void setActiveZoneValue(int[] activeZone) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		for (int i = 0; i < 4; i++) {
			prefEditor.putInt(BaseActivity.prefActiveZoneValue[i],
					activeZone[i]);
		}
		prefEditor.commit();
	}

	static public int getNumNotification() {
		return SystemConfig.numNotification_.get();
	}

	static AtomicInteger numNotification_ = new AtomicInteger(0);

	static public void setNumNotification(int numNotification) {
		SystemConfig.numNotification_.set(numNotification);
		return;
	}

	static public void incNumNotification() {
		SystemConfig.numNotification_.incrementAndGet();
		return;
	}

	static public void clearNumNotification() {
		SystemConfig.numNotification_.set(0);
		return;
	}
	//to do make common for all systems
	public int getNumNotificationSaved() {
		SharedPreferences prefernce = ContextApplication.getAppContext().getSharedPreferences(
				BaseActivity.MYSYSTEM_PREFERENCES, Context.MODE_PRIVATE);
		int result = 0;
		if (prefernce.contains(BaseActivity.prefNumNotification) == true)
			result = prefernce.getInt(BaseActivity.prefNumNotification, result);
		else
			setNumNotification(result);
		return result;

	}

	public void setNumNotificationSaved(int numNotification) {
		SharedPreferences prefernce = ContextApplication.getAppContext().getSharedPreferences(
				BaseActivity.MYSYSTEM_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = prefernce.edit();
		prefEditor.putInt(BaseActivity.prefNumNotification, numNotification);
		prefEditor.commit();
		return;
	}

	public boolean getIsAutoRequest() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsAutoRequest) == true)
			result = _settings.getBoolean(BaseActivity.prefIsAutoRequest, result);
		else
			setIsAutoRequest(result);
		return result;
	}

	public void setIsAutoRequest(boolean IsAutoRequest) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsAutoRequest, IsAutoRequest);
		prefEditor.commit();
		return;
	}
	
	public boolean getIsAutoRequestEnabled() {
		boolean result = false;
		if (_settings.contains(BaseActivity.prefIsAutoRequestEnabled) == true)
			result = _settings.getBoolean(BaseActivity.prefIsAutoRequestEnabled, result);
		else
			setIsAutoRequestEnabled(result);
		return result;
	}

	public void setIsAutoRequestEnabled(boolean IsAutoRequestEnabled) {
		SharedPreferences.Editor prefEditor = _settings.edit();
		prefEditor.putBoolean(BaseActivity.prefIsAutoRequestEnabled, IsAutoRequestEnabled);
		prefEditor.commit();
		return;
	}
	
	@Override
	public String toString() {
		return _name;
	}
}
