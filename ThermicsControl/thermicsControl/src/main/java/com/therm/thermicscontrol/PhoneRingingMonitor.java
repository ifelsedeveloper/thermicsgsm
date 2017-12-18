package com.therm.thermicscontrol;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class PhoneRingingMonitor extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                TelephonyManager.EXTRA_STATE_RINGING)) {
            Bundle bundle = intent.getExtras();
            String sms_from = bundle.getString("incoming_number");
            Intent mIntent = new Intent(context, SmsService.class);
            mIntent.putExtra("sms_body", "Система Кситал: входящий вызов");
            mIntent.putExtra("incoming_number", sms_from);
            context.startService(mIntent);
        }

        abortBroadcast();
    }
}
