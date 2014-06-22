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
			SystemConfig settings=SystemConfigDataSource.sharedInstanceSystemConfigDataSource().getSystemConfig(sms_from);
			if(settings != null)
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

}
