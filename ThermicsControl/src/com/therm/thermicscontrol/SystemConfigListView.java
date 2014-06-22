package com.therm.thermicscontrol;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class SystemConfigListView extends ListView {

	public SystemConfigListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
		          int position, long id) {

		        Toast.makeText(ContextApplication.getAppContext(), "test click", Toast.LENGTH_LONG).show();

		      }
		    });
	}

	
}
