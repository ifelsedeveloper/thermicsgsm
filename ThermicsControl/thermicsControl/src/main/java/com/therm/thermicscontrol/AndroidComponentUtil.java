package com.therm.thermicscontrol;

/**
 * Created by vfadeev on 27/09/16.
 */

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class AndroidComponentUtil {

    public static String[] getPermissions() {
        return PERMISSIONS;
    }

    private static final String[] PERMISSIONS = { Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.WRITE_SMS, Manifest.permission.READ_CONTACTS,
            Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.WAKE_LOCK
    };

    public static String getEnsureRunningAction(Context ctx) {
        return ctx.getPackageName() + ".ACTION_ENSURE_RUNNING";
    }

    public static int getRequestCode(Class serviceCls) {
        return CRC32.tableLookup(serviceCls.getName().getBytes(Charset.defaultCharset()));
    }

    public static void toggleComponent(Context context, Class componentClass, boolean enable) {
        ComponentName componentName = new ComponentName(context, componentClass);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(componentName,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void ensureRunning(Class serviceCls, Context context, long repeatEveryMillis) {
        Intent myIntent = new Intent(context, serviceCls);
        myIntent.setPackage(context.getPackageName());
        myIntent.setAction(getEnsureRunningAction(context));
        PendingIntent pendingIntent = PendingIntent.getService(context, getRequestCode(serviceCls), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatEveryMillis, repeatEveryMillis, pendingIntent);
    }

    public static void ensureRunning(Class serviceCls, Context context, long repeatEveryMillis, boolean fireImmediately) {
        Intent myIntent = new Intent(context, serviceCls);
        myIntent.setPackage(context.getPackageName());
        myIntent.setAction(getEnsureRunningAction(context));
        PendingIntent pendingIntent = PendingIntent.getService(context, getRequestCode(serviceCls), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000, repeatEveryMillis, pendingIntent);
    }

    public static String[] missingPermissions(Context context, String... permissions) {
        List<String> listPermissionsNeeded = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= 23 && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission);
                }
            }
        }
        return listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]);
    }

}