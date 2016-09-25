package com.therm.thermicscontrol;

import com.therm.thermicscontrol.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class HelpInformationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_help_information);
		
		try{
			WebView webView = (WebView) findViewById(R.id.webViewHelpInformation);
			WebSettings settings = webView.getSettings();
			settings.setDefaultTextEncodingName("utf-8");
			webView.loadUrl("file:///android_asset/THERMICS-GSM.htm");
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_contact_information, menu);
		return true;
	}

}
