package com.therm.thermicscontrol;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.therm.thermicscontrol.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;

import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.view.Window;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.app.Dialog;

import android.app.ProgressDialog;


@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class NumbersBroadcastActivity extends BaseActivity{
	
	static ListView lvData;
	static EditText main_number;
	static EditText main_name;
	public SystemConfig settings=null;
	public CSettingsDev settingsDev=null;
	final Context context = this;
	static Dialog dialog;
	static int pos_for_modification = 0;
	BroadcastReceiver br;
	
	static String phone_number;
	static String contact_name;
	
	Button buttonApplyNumbers_;
	Button buttonCancelNumbers_;
	
	Button buttonAddNumber_;
	Button buttonDeleteAllNumbers_;
	
	SimpleAdapter sAdapter;
	ArrayList<Map<String, String>> data;
	
	static ArrayList<Map<String, String>> numbersToAdd = new ArrayList<Map<String, String>>();
	static boolean[] numbersNeedUpdate = {false,false,false,false,false,false,false,false,false,false};
	static boolean[] numbersUsing = {true,false,false,false,false,false,false,false,false,false};
	static List<Integer> freeNumbers = new  ArrayList<Integer>();
	static String[] numbersToSet = new String[10];
	Map<String, String> m;
	Map<String, String> firstNumber;
	
	static boolean flag_added = true;
	static boolean flag_delete = false;
	
	public static final String TAG_events="event_tag_message_sytem";
	public static final int max_numbers = 10;
	static int count_numbers = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		//this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_numbers_broadcast);
		
		//create for work with shared preference
		Log.i(TAG_events,"start activity_numbers_broadcast sytem");
		
		//create for work with shared preference
		settings=SystemConfigDataSource.getActiveSystem();
		settingsDev= new CSettingsDev(settings,getApplicationContext());
		
		lvData = (ListView)findViewById(R.id.listViewNumbers);
		main_number = (EditText)findViewById(R.id.editTextNumber);
		main_name = (EditText)findViewById(R.id.editTextNameContact);
		buttonApplyNumbers_ = (Button)findViewById(R.id.buttonApplyNumbers);
		buttonCancelNumbers_ = (Button)findViewById(R.id.buttonCancelNumbers);
		buttonAddNumber_ = (Button)findViewById(R.id.buttonAddNumber);
		buttonDeleteAllNumbers_ = (Button)findViewById(R.id.buttonDeleteAllNumbers);
		//sender=new CSMSSender(settings.getNumberSIM(), getApplicationContext());
		// создаем BroadcastReceiver
	    br = new BroadcastReceiver() {
	      // действия при получении сообщений
	      public void onReceive(Context context, Intent intent) {
	        String sms = intent.getStringExtra(PARAM_SMS);
	        String time = intent.getStringExtra(PARAM_SMSTIME);
	        Log.d(TAG_events, "onReceive sms: "+sms+" ;time = "+time);
	        //отправляем полученное сообщение нашему классу
	        //if(run_sms_sender) settingsDev.smsRecive(sms);
	        if(!flag_delete)
	        {
	        for(int i=1;i<10;i++)
	        {
		        	//String prefix_str = String.format("0%d SMS=+7",i);
		        	String wrong_number = String.format(Locale.US,"0%d SMS=+7**********", i);
		        	if(sms.contains(wrong_number))
		        	{
		        		data.remove(pos_for_modification);
		        		sAdapter.notifyDataSetChanged();
		        	}
		        }
	        }
	        settingsDev.recvSMS(sms);
	      }
	    };
	    // создаем фильтр для BroadcastReceiver
	    IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION_RCVSMS);
	    // регистрируем (включаем) BroadcastReceiver
	    registerReceiver(br, intFilt);
		

	    // массивы данных
	    contact_name = "Имя";
	    phone_number = "Номер";
	    main_number.setText(phone_number);
	    main_name.setText(contact_name);

	    
	    ArrayList<Map<String, String>> ldata = settings.getNumbersBroadcast();
	    if(ldata.size()==0)
	    {
	
//		    // упаковываем данные в понятную для адаптера структуру
		    data = new ArrayList<Map<String, String>>();
	    }
	    else
	    {
	    	data = new ArrayList<Map<String, String>>();
	    	for(Map<String,String> lvalue : ldata)
		    {
		    	int available = Integer.parseInt(lvalue.get(ATTRIBUTE_AVAILABLE));
		    	if(available == 0)
		    	{
		    		m = new HashMap<String, String>();
				      m.put(ATTRIBUTE_CONTACT_NAME, lvalue.get(ATTRIBUTE_CONTACT_NAME));
				      m.put(ATTRIBUTE_PHONE, lvalue.get(ATTRIBUTE_PHONE));
				      m.put(ATTRIBUTE_POSIITION,lvalue.get(ATTRIBUTE_POSIITION));
				      m.put(ATTRIBUTE_AVAILABLE, "0");
				   data.add(m);

		    	}
		    		
		    }
	    }
	    
	    
	    
	    count_numbers =  data.size();
	    
	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = {  ATTRIBUTE_CONTACT_NAME,ATTRIBUTE_PHONE };
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.rowNameContact,R.id.rowPhoneNumber};
	    
	    //add old data
	    for (Map<String, String> s : numbersToAdd)
	    	data.add(s);
	    
	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.row_phone_number,
	        from, to);
	    lvData.setAdapter(sAdapter);
	    int i=0;
	    for(Map<String,String> lvalue : data)
	    {
	    	int pos = Integer.parseInt(lvalue.get(ATTRIBUTE_POSIITION));
	    	//int available = Integer.parseInt(lvalue.get(ATTRIBUTE_AVAILABLE));
	    	//if(available == 1)
	    		numbersUsing[pos]=true;
	    	i++;
	    }
	    for(i=0;i<10;i++)
	    	if(!numbersUsing[i])
	    		freeNumbers.add(i);
	    
	    dialog = new Dialog(context);
	    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.custom_dialog_change_delete);
		dialog.setTitle("Выберите действие");
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonChange);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		changeNumberAction();
        		dialog.dismiss();
            }
         });
		
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonDelete);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		deleteNumberAction();
        		dialog.dismiss();
            }
         });
		
		dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		dialogButton.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
                //perform action
        		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        		dialog.dismiss();
        		try
	    		{
	        		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	    		}
	    		catch(Exception e)
	    		{
	    			
	    		}
            }
         });
		
	    lvData.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int pos, long id) {
                // TODO Auto-generated method stub
            	//if(pos>0)
            	{
            		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            		Log.v("long clicked","pos"+" "+pos);
            		pos_for_modification = pos;
            		dialog.show();
            	}
                return true;
            }
        });
	    
	    firstNumber = new HashMap<String, String>();
	    firstNumber.put(ATTRIBUTE_CONTACT_NAME, contact_name);
	    firstNumber.put(ATTRIBUTE_PHONE, phone_number);
	    firstNumber.put(ATTRIBUTE_POSIITION,"0");
	    firstNumber.put(ATTRIBUTE_AVAILABLE, "0");
	    setVisibleApplayCancel();
	    lockButtonAddNumber();
	    lockButtonDeleteNumbers();
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void longPressNumberAction(int pos)
	{
		
	}
	
	public void deleteNumberAction()
	{
		flag_delete = true;
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setTitle("Удалить номер рассылки SMS?");
		//b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
		b.setPositiveButton("Удалить", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	ArrayList<Map<String, String>> old_data = settings.getNumbersBroadcast();
	        	
	        	if(pos_for_modification>old_data.size()-1)
	        	{
	        		data.remove(pos_for_modification);
	        		
	        		numbersToAdd.remove(pos_for_modification - old_data.size());
	        	}
	        	else
	        	{
	        		ArrayList<Map<String, String>> numberDelete = new ArrayList<Map<String, String>>();
	        		Map<String, String> removedNumber = data.remove(pos_for_modification);
	        		removedNumber.put(ATTRIBUTE_PHONE, "+7**********");
	        		removedNumber.put(ATTRIBUTE_AVAILABLE, "1");
	        		numberDelete.add(removedNumber);
	        		settings.setNumbersBroadcast(numberDelete);
		        	settingsDev.clearQueueCommands();
		        	settingsDev.setNumbersBroadcastCommand(numberDelete);
		    		//editTextNumberSensorTMPRele
		    		
		        	setVisibleApplayCancel();
		        	lockButtonAddNumber();
				    lockButtonDeleteNumbers();
		    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Удаление номера");
		    		settingsDev.sendCommands();		
		    		pd.show();
	        		//
	        	}
	        	freeNumbers.add(pos_for_modification);
	        	sAdapter.notifyDataSetChanged();
	        	main_number.setText(firstNumber.get(ATTRIBUTE_PHONE));
				main_name.setText(firstNumber.get(ATTRIBUTE_CONTACT_NAME));
	        	try
	    		{
	        		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	    		}
	    		catch(Exception e)
	    		{
	    			
	    		}
	        	
	        }
	      });
		b.setNegativeButton("Отмена", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	        	try
	    		{
	        		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	    		}
	    		catch(Exception e)
	    		{
	    			
	    		}
	        	flag_delete = false;
	        }});
		b.show();
	}
	
	public void changeNumberAction()
	{
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.name_phone_contact, null);

		AlertDialog.Builder alert = new AlertDialog.Builder(
				context);
		alert.setTitle("Изменение имени и номера");
		// set prompts.xml to alertdialog builder
		alert.setView(promptsView);

		final EditText inputName = (EditText) promptsView
				.findViewById(R.id.editTextNameContact);
		inputName.setText(data.get(pos_for_modification).get(ATTRIBUTE_CONTACT_NAME));
		
		final EditText inputPhone = (EditText) promptsView.findViewById(R.id.editTextPhoneContact);
		inputPhone.setText(data.get(pos_for_modification).get(ATTRIBUTE_PHONE));
		
		    alert.setPositiveButton("Принять", new DialogInterface.OnClickListener() {  
		    public void onClick(DialogInterface dialog, int whichButton) {  
		        String valueName = inputName.getText().toString();
		        String valuePhone =  getCorrectPhoneNumber(inputPhone.getText().toString());
		        
		        //check need to send
		        ArrayList<Map<String, String>> old_data = settings.getNumbersBroadcast();
	        	
	        	if(pos_for_modification>old_data.size()-1)
	        	{
	        		data.get(pos_for_modification).put(ATTRIBUTE_CONTACT_NAME, valueName);
	        		data.get(pos_for_modification).put(ATTRIBUTE_PHONE, valuePhone);
	        		
	        		numbersToAdd.get(pos_for_modification - old_data.size()).put(ATTRIBUTE_CONTACT_NAME, valueName);
	        		numbersToAdd.get(pos_for_modification - old_data.size()).put(ATTRIBUTE_PHONE, valuePhone);
	        	}
	        	else
	        	{
	        		String old_number = new String( data.get(pos_for_modification).get(ATTRIBUTE_PHONE));
	        		data.get(pos_for_modification).put(ATTRIBUTE_CONTACT_NAME, valueName);
	        		data.get(pos_for_modification).put(ATTRIBUTE_PHONE, valuePhone);
	        		ArrayList<Map<String, String>> numberModif = new ArrayList<Map<String, String>>();
	        		Map<String, String> myNumber = new HashMap<String, String>( data.get(pos_for_modification));
	        		myNumber.put(ATTRIBUTE_AVAILABLE, "0");
	        		numberModif.add(myNumber);
	        		settings.setNumbersBroadcast(numberModif);
	        		if(!myNumber.get(ATTRIBUTE_PHONE).equalsIgnoreCase(old_number))
	        		{
			        	settingsDev.clearQueueCommands();
			        	settingsDev.setNumbersBroadcastCommand(numberModif);
			        	//editTextNumberSensorTMPRele
		    		
		        		setVisibleApplayCancel();
		        		lockButtonAddNumber();
				    	lockButtonDeleteNumbers();
		    			LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Удаление номера");
		    			settingsDev.sendCommands();		
		    			pd.show();
	        		}
	        		//
	        	}
	        	main_number.setText(valuePhone);
				main_name.setText(valueName);
				contact_name = valueName;
				phone_number = valuePhone;
	        	sAdapter.notifyDataSetChanged();
	        	try
	    		{
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
	        		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	    		}
	    		catch(Exception e)
	    		{
	    			
	    		}
	        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        return;                  
		       }  
		     });  

		    alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

		        public void onClick(DialogInterface dialog, int which) {
		        	try
		    		{
		        		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		        		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		    		}
		    		catch(Exception e)
		    		{
		    			
		    		}
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		            return;   
		        }
		    });
		
		 // create alert dialog
		AlertDialog alertDialog = alert.create();
		    
		alertDialog.show();
	}
	
	public void loadParam()
	{
		ArrayList<Map<String, String>> data_setings = settings.getNumbersBroadcast();
		//add old data
		data.clear();
	    for (Map<String, String> s : data_setings)
	    	data.add(s);
	    sAdapter.notifyDataSetChanged();
	}

	public void setVisibleApplayCancel()
	{
		if(numbersToAdd.size()>0)
		{
			buttonApplyNumbers_.setVisibility(View.VISIBLE);
			buttonCancelNumbers_.setVisibility(View.VISIBLE);
		}
		else
		{
			buttonApplyNumbers_.setVisibility(View.GONE);
			buttonCancelNumbers_.setVisibility(View.GONE);
		}
	}
	
	public void lockButtonAddNumber()
	{
		if(!flag_added)
		{
			buttonAddNumber_.setClickable(true);
			buttonAddNumber_.setTextColor(getResources().getColor(R.color.text_button_color));
		}
		else
		{
			buttonAddNumber_.setClickable(false);
			buttonAddNumber_.setTextColor(getResources().getColor(R.color.disable_button));
		}
			
	}
	
	public void lockButtonDeleteNumbers()
	{
		if(data.size()>0)
		{
			buttonDeleteAllNumbers_.setClickable(true);
			buttonDeleteAllNumbers_.setTextColor(getResources().getColor(R.color.text_button_color));
		}
		else
		{
			buttonDeleteAllNumbers_.setClickable(false);
			buttonDeleteAllNumbers_.setTextColor(getResources().getColor(R.color.disable_button));
		}
			
	}
	
	String getCorrectPhoneNumber(String number)
	{
	
		String result = number;
		if(number.length()>0)
		{
			if(number.charAt(0)=='8')
			{
				result=String.format("+7%s", number.substring(1,number.length()));
			}
			
			if(number.charAt(0)!='8' && number.charAt(0)!='+' && number.length()<11)
			{
				result=String.format("+7%s", number);
			}
			
			if(number.charAt(0)=='7')
			{
				result=String.format("+%s", number);
			}
			result=result.replace(" ", "");
			result=result.replace("-", "");
		}
		else
		{
			Toast.makeText(getApplicationContext(), "Длина номера не может быть нулевой", Toast.LENGTH_LONG).show();
		}
		return result;
	}
	
	@Override
    protected void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
        //saveState.putBoolean("waiting",waiting);
    }
	
	CounterTimer timer = new CounterTimer(30000, 1000);
	ProgressDialog pd = null;
	static Handler hpd =null;
	public void LoadProgressDialog(int count_sms,String title)
	{
		timer = new CounterTimer(settingsDev.getTimeNeededToSend()*1000, 1000); 
		pd = new ProgressDialog(this);
	    pd.setTitle(title);
	    timer.setTitle(title);
	    timer.setProgressDialog(pd);
	      // меняем стиль на индикатор
	      pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	      // устанавливаем максимум
	      pd.setMax(count_sms);
	      // включаем анимацию ожидания
	      pd.setIndeterminate(true);
	      pd.setCancelable(false);
	      timer.start();
	      hpd = new Handler() {
	        public void handleMessage(Message msg) {
	          // выключаем анимацию ожидания
	          pd.setIndeterminate(false);
	          if (pd.getProgress() < pd.getMax()) {
	            // увеличиваем значения индикаторов
	            pd.incrementProgressBy(1);
	            pd.incrementSecondaryProgressBy(1);
	          } else {
	        	timer.cancel();
	            pd.dismiss();
	            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	            Toast.makeText(getApplicationContext(),"Все команды успешно отправлены",Toast.LENGTH_LONG).show();
	            
	          }
	        }
	      };
	      
	      settingsDev.setHandlerDialog(hpd);
	}
	
	private static final int CONTACT_PICKER_RESULT = 31415926;
	public void OnButtonNumberFromContacts(View v)
	{
		Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
	    startActivityForResult(i, CONTACT_PICKER_RESULT);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == CONTACT_PICKER_RESULT){
	        if(resultCode == RESULT_OK){
	            Uri uri = data.getData();
	            //System.out.println("uri: "+uri);
	            String number  = getContactPhoneNumber(this, uri.getLastPathSegment());
	            String name  = getContactName(this, uri.getLastPathSegment());
	             
	            phone_number =  getCorrectPhoneNumber(number);
	            contact_name =  name;
	            flag_added = false;
	             
	     	    main_number.setText(phone_number);
	     	    main_name.setText(contact_name);
	     	    lockButtonAddNumber();
	             //System.out.println("PHONE NUMBER: " + number);
	             //Toast.makeText(getApplicationContext(),name+" | "+ number, Toast.LENGTH_LONG).show();
	        }
	    }
	}
	
	private static final String TAG = "PhoneUtils";

	public static String getContactPhoneNumber(Context context, String contactId) {
	   int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
	   String phoneNumber = null;

	   String[] whereArgs = new String[] { String.valueOf(contactId), String.valueOf(type) };

	   Log.d(TAG, "Got contact id: "+contactId);

	   Cursor cursor = context.getContentResolver().query(
	                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
	                            null,
	                            ContactsContract.CommonDataKinds.Phone._ID + " = ? and " + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?", 
	                            whereArgs, 
	                            null);

	  int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

	  if (cursor != null) {
	       Log.d(TAG, "Returned contact count: "+cursor.getCount());
	       try {
	             if (cursor.moveToFirst()) {
	                 phoneNumber = cursor.getString(phoneNumberIndex);
	             }
	            } finally {
	               cursor.close();
	            }
	   }

	  Log.d(TAG, "Returning phone number: "+phoneNumber);
	  return phoneNumber;
	}
	
	
	public static String getContactName(Context context, String contactId) {
		   int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
		   String name = null;

		   String[] whereArgs = new String[] { String.valueOf(contactId), String.valueOf(type) };

		   Log.d(TAG, "Got contact id: "+contactId);

		   Cursor cursor = context.getContentResolver().query(
		                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		                            null,
		                            ContactsContract.CommonDataKinds.Phone._ID + " = ? and " + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?", 
		                            whereArgs, 
		                            null);

		  int nameIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

		  if (cursor != null) {
		       Log.d(TAG, "Returned contact count: "+cursor.getCount());
		       try {
		             if (cursor.moveToFirst()) {
		                 name = cursor.getString(nameIndex);
		             }
		            } finally {
		               cursor.close();
		            }
		   }

		  Log.d(TAG, "Returning phone number: "+name);
		  return name;
		}
	

	

	
	
	public void OnClickAddNumber(View v)
	{
		if(freeNumbers.size()>0)
		{
			if(!flag_added)
			{
				phone_number = main_number.getText().toString();
				contact_name = main_name.getText().toString();
				// создаем новый Map
				int pos = freeNumbers.remove(0);
			    m = new HashMap<String, String>();
			    m.put(ATTRIBUTE_PHONE, new String(phone_number));
			    m.put(ATTRIBUTE_POSIITION, String.format("%d", pos));
			    m.put(ATTRIBUTE_CONTACT_NAME, new String(contact_name));
			    m.put(ATTRIBUTE_AVAILABLE, "0");
			    // добавляем его в коллекцию
			    data.add(m);
			    numbersToAdd.add(m);
			    // уведомляем, что данные изменились
			    sAdapter.notifyDataSetChanged();
			    flag_added = true;
			    count_numbers++;
			    setVisibleApplayCancel();
			    lockButtonAddNumber();
			    lockButtonDeleteNumbers();
			    try
				{
					InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		    		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}
				catch(Exception e)
				{
					
				}
			}
		}
		else
			Toast.makeText(getApplicationContext(), "Максимальное число номеров для рассылки 9", Toast.LENGTH_LONG).show();
		
	}
	
	public void OnClickDeleteAllNumbers(View v)
	{
		if(data.size()>0)
		{
			flag_delete = true;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
			//save parameters
			final AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setIcon(android.R.drawable.ic_dialog_alert);
			b.setTitle("Очистить список рассылки SMS?");
			//b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
			b.setPositiveButton("Очистить", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	
		        	ArrayList<Map<String, String>> old_data = settings.getNumbersBroadcast();
					ArrayList<Map<String, String>> new_data = new ArrayList<Map<String, String>>();
					int i=0;
					for(Map<String,String> lvalue : old_data)
				    {
						int available = Integer.parseInt(lvalue.get(ATTRIBUTE_AVAILABLE));
				    	if(available == 0)
				    	{
							//if(i>0)
							{
								lvalue.put(ATTRIBUTE_AVAILABLE, "1");
								lvalue.put(ATTRIBUTE_PHONE, "+7**********");
								new_data.add(lvalue);
							}
							
				    	}
						//i++;
				    }
					
					
					freeNumbers = new ArrayList<Integer>();
					for(i=1;i<10;i++)
						freeNumbers.add(i);
					
					i++;
					numbersToAdd.clear();
					data.clear();
					//data.add(firstNumber);
					sAdapter.notifyDataSetChanged();
					setVisibleApplayCancel();
					lockButtonAddNumber();
				    lockButtonDeleteNumbers();
					main_number.setText(firstNumber.get(ATTRIBUTE_PHONE));
					main_name.setText(firstNumber.get(ATTRIBUTE_CONTACT_NAME));
					if(new_data.size()>0)
					{
						settingsDev.clearQueueCommands();
						settingsDev.setNumbersBroadcastCommand(new_data);
						settings.setNumbersBroadcast(new_data);
						
			    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Очистка списка рассылки SMS");
			    		settingsDev.sendCommands();		
			    		pd.show();
					}
					
		        }
		      });
			b.setNegativeButton("Отмена", new OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		        	flag_delete = false;
		        }});
			b.show();
			
			
		}
	}
	
	
	
	public void OnClickApplyButton(View v)
	{
		//numbersToAdd
		//save parameters
		if(numbersToAdd.size()>0)
		{
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		final AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setIcon(android.R.drawable.ic_dialog_alert);
		b.setTitle("Задать список рассылки SMS?");
		//b.setMessage("Отправить" + String.format(" %d ", settingsDev.sms_to_send.size()) + "SMS?");
		b.setPositiveButton("Установить", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	settings.setNumbersBroadcast(numbersToAdd);
	        	settingsDev.clearQueueCommands();
	        	settingsDev.setNumbersBroadcastCommand(numbersToAdd);
	    		//editTextNumberSensorTMPRele
	        	pos_for_modification = data.size() - numbersToAdd.size();
	        	numbersToAdd.clear();
	        	setVisibleApplayCancel();
	        	lockButtonAddNumber();
			    lockButtonDeleteNumbers();
	    		LoadProgressDialog(settingsDev.sms_to_send.size()+1,"Задание списка рассылки SMS");
	    		settingsDev.sendCommands();		
	    		pd.show();
	        }
	      });
		b.setNegativeButton("Отмена", new OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	        	
	        }});
		b.show();
		}
	}
	
	public void OnClickCancelButton(View v)
	{
		numbersToAdd.clear();
		loadParam();
		main_number.setText(firstNumber.get(ATTRIBUTE_PHONE));
		main_name.setText(firstNumber.get(ATTRIBUTE_CONTACT_NAME));
		setVisibleApplayCancel();
		lockButtonAddNumber();
	    lockButtonDeleteNumbers();
	    try
		{
			InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
    		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}
	
	protected void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(br);
	  }



	

	
//	@Override

	
}
