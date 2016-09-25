package com.therm.thermicscontrol;

import android.app.ProgressDialog;
import android.os.CountDownTimer;
import android.util.Log;

public class CounterTimer extends CountDownTimer {

    public CounterTimer(long millisInFuture, long countDownInterval)
    {
        super(millisInFuture, countDownInterval);
        isOverTime = false;
    }

    String title = "";
    ProgressDialog pd;

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setProgressDialog(ProgressDialog pd)
    {
        this.pd = pd;
    }

    @Override
    public void onFinish()
    {
        Log.i("Main","Timer Completed");

        pd.setTitle(title);
    }


    boolean isOverTime = false;
    @Override
    public void onTick(long millisUntilFinished)
    {
        pd.setTitle(title + "\n(" + (millisUntilFinished/1000) + " сек)");
        if((millisUntilFinished/1000)==1)
        {
            isOverTime = true;
            this.pd.dismiss();
        }
    }
}
