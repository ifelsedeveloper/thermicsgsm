package com.therm.thermicscontrol;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class ContextApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ContextApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return ContextApplication.context;
    }
}
