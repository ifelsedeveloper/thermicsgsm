package com.therm.thermicscontrol;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class FunctionReleActivity extends BaseActivity {

    public static final String TAG_events = "event_tag_settings_param";
    public SystemConfig settings = null;
    String function_rele1;
    String function_rele2;
    String function_rele3;
    String function_upr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_function_rele);

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
        EditText text = (EditText) findViewById(R.id.editTextFunctionRele1);
        text.setText(settings.getFunctionRele1());

        text = (EditText) findViewById(R.id.editTextFunctionRele2);
        text.setText(settings.getFunctionRele2());

        text = (EditText) findViewById(R.id.editTextFunctionRele3);
        text.setText(settings.getFunctionRele3());

        text = (EditText) findViewById(R.id.editTextFunctionUpr);
        text.setText(settings.getFunctionUpr());
    }

    //save parameters to system
    public void saveParam() {
        EditText text = (EditText) findViewById(R.id.editTextFunctionRele1);
        function_rele1 = text.getText().toString();
        settings.setFunctionRele1(function_rele1);

        text = (EditText) findViewById(R.id.editTextFunctionRele2);
        function_rele2 = text.getText().toString();
        settings.setFunctionRele2(function_rele2);

        text = (EditText) findViewById(R.id.editTextFunctionRele3);
        function_rele3 = text.getText().toString();
        settings.setFunctionRele3(function_rele3);

        text = (EditText) findViewById(R.id.editTextFunctionUpr);
        function_upr = text.getText().toString();
        settings.setFunctionUpr(function_upr);
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
