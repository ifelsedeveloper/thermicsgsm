package com.therm.thermicscontrol;


import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;

import android.telephony.SmsMessage;

import android.widget.Toast;
//import android.util.Log;

public class SMSMonitor extends BroadcastReceiver {
	
	
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
		if (intent != null && intent.getAction() != null &&
				ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
			Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
			SmsMessage[] messages = new SmsMessage[pduArray.length];
			for (int i = 0; i < pduArray.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
			}
			String sms_from = messages[0].getDisplayOriginatingAddress();
			CSettingsPref settings=new CSettingsPref(context.getSharedPreferences(BaseActivity.MYSYSTEM_PREFERENCES, BaseActivity.MODE_MULTI_PROCESS));
			if(checkSMSKsytal(sms_from,settings))
			{
				//Toast.makeText(, text, duration)
				StringBuilder bodyText = new StringBuilder();
				for (int i = 0; i < messages.length; i++) {
			      bodyText.append(messages[i].getMessageBody());
			    }
				String body = bodyText.toString();
				Intent mIntent = new Intent(context, SmsService.class);
				//Log.i("database 2",body);
				if(mIntent!=null)
				{
					mIntent.putExtra("sms_body", body);
					mIntent.putExtra("incoming_number", sms_from);
					context.startService(mIntent);
				}
				if(!settings.getIsSMSInIncoming()) abortBroadcast();
			}
		}
		}
		catch(Exception e)
		{
			Toast.makeText(context, "Error in sms monitor: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	public static boolean checkSMSKsytal(String sms_from, CSettingsPref settings)
	{
		boolean res = false;
		if(sms_from!=null)
		{
			res = compareSIMNumbers(sms_from,settings.getNumberSIM2()) || compareSIMNumbers(sms_from,settings.getNumberSIM());
		}
		return res;
	}
	
	public static boolean compareSIMNumbers(String number1,String number2)
	{
		boolean res = false;
		if(number1.length() > 0 && number2.length() > 0)
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
}
