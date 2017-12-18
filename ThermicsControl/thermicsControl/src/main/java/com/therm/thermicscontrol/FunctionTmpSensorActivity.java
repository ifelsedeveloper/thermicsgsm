package com.therm.thermicscontrol;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class FunctionTmpSensorActivity extends BaseActivity {

    public static final String TAG_events = "event_tag_function_tmp_sensor";
    public SystemConfig settings = null;
    String[] function_tmp_sensor = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_function_tmpsensor);

        //create for work with shared preference
        settings = SystemConfigDataSource.getActiveSystem();
        loadParam();
        Log.i(TAG_events, "creating activity");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    //load parameters from system and display it on activity
    public void loadParam() {
        EditText text = (EditText) findViewById(R.id.editTextFunctionTmpSensor1);
        text.setText(settings.getFunctionTmpSensor(0));

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor2);
        text.setText(settings.getFunctionTmpSensor(1));

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor3);
        text.setText(settings.getFunctionTmpSensor(2));

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor4);
        text.setText(settings.getFunctionTmpSensor(3));

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor5);
        text.setText(settings.getFunctionTmpSensor(4));

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor6);
        text.setText(settings.getFunctionTmpSensor(5));
    }

    //save parameters to system
    public void saveParam() {
        EditText text = (EditText) findViewById(R.id.editTextFunctionTmpSensor1);
        function_tmp_sensor[0] = text.getText().toString();
        settings.setFunctionTmpSensor(0, function_tmp_sensor[0]);

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor2);
        function_tmp_sensor[1] = text.getText().toString();
        settings.setFunctionTmpSensor(1, function_tmp_sensor[1]);

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor3);
        function_tmp_sensor[2] = text.getText().toString();
        settings.setFunctionTmpSensor(2, function_tmp_sensor[2]);

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor4);
        function_tmp_sensor[3] = text.getText().toString();
        settings.setFunctionTmpSensor(3, function_tmp_sensor[3]);

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor5);
        function_tmp_sensor[4] = text.getText().toString();
        settings.setFunctionTmpSensor(4, function_tmp_sensor[4]);

        text = (EditText) findViewById(R.id.editTextFunctionTmpSensor6);
        function_tmp_sensor[5] = text.getText().toString();
        settings.setFunctionTmpSensor(5, function_tmp_sensor[5]);


    }

    public void OnClickOkButton(View v) {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
        saveParam();
        onBackPressed();
    }

    public void OnClickCancelButton(View v) {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
        onBackPressed();
    }
}
