package com.impalapay.uk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@SuppressLint("ResourceAsColor")
public class ProfileActivity extends Activity implements View.OnClickListener, OnItemSelectedListener {

	TextView edit_tv,back_tv,username_tv,done_tv,user_dob_tv,user_currency_tv,user_email_tv,user_mobileno_tv,user_contry_tv,user_address_tv,user_zipcode_tv,stateText,zipText;
	EditText zipcode_et,first_name_et,last_name_et,user_email_et,user_mobileno_et,user_address_et,user_zipcode_et;
	String city="",userid,Countrywithcode,fname,lname,email,phone,dob,currency,country,address,zipcode,first_zipcode,total_address,addressline2_,state_;
	Context context;
	String country_iso2;
	TextView pretext;
	Spinner state;
	String city_ = "";
	EditText city_edit;
	List<String> citiesList;
	MainActivity myMain = new MainActivity();

	String new_address_line1, new_address_line2, new_state, new_city, new_zip_code;
	
	EditText addressline1,addressline2,zipcode_edit;

	private String[] statearr_ = { "Select State","Alabama","Arizona","Arkansas","Colorado","Connecticut","Delaware",
			"District of Columbia","Florida","Georgia","Idaho",
			"Illinois","Iowa","Kansas","Kentucky","Louisiana","Maine",
			"Maryland","Massachusetts","Michigan","Minnesota","Mississippi",
			"Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","North Dakota","Oklahoma",
			"Oregon","Rhode Island","South Carolina","South Dakota","Utah","Vermont","Washington","West Virginia",
			"Wisconsin","Wyoming" };
	private String[] statearr1_ = { "Select State","AL","AZ","AR","CO","CT","DE","DC","FL",
			"GA","ID","IL","IA","KS","KY","LA","ME","MD","MA","MI","MN","MS","MO","MT",
			"NE","NV","NH","NJ","NM","ND","OK","OR","RI","SC","SD","UT","VT","WA","WV","WI","WY" };

	private String[] statearr;
	private String[] statearr1;

	//	ScrollView fill_sv; EditText address_first_line_et,address_second_line_et,zipcode_et;
	//String  address_first,address_second,
	ImageView back_im,currency_drop,contry_drop;
	int current_year,current_month,current_day,byear,bmonth,bday;
	Spinner user_basecurrency_sp,user_contry_sp;
	ArrayList<String>currency_al= new ArrayList<String>();
	ArrayList<String> CountryCode_al= new ArrayList<String>();
	ArrayList<String> country_al= new ArrayList<String>();
	RelativeLayout uneditdetail_rl; //fill_address_rl
	ProgressDialog dialog;
	ProfileActivity activity=this;
	String base;
	String val;
	Spinner mobile_code_sp;
	ArrayAdapter<String> dataAdapter;
	ArrayAdapter<String> dataAdapter1;
	//ArrayList<String> country_code_al = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_profile);

		getXmlId();
		
		Countrywithcode= Common_data.getPreferences(getBaseContext(), "countrywithcode");
		base= Common_data.getPreferences(getApplicationContext(), "base");
		if(base.equalsIgnoreCase("CAD")){
			country_iso2="ca";
		}else if(base.equalsIgnoreCase("USD")){
			country_iso2="us";
		}else{

		}
	
		parseJson(Countrywithcode);
		
		getDataFormPrefrance();
		
		addressline1.setEnabled(false);
		addressline1.setClickable(false);
		addressline1.setFocusableInTouchMode(false);
		addressline2.setEnabled(false);
		addressline2.setClickable(false);
		addressline2.setFocusableInTouchMode(false);
		state.setEnabled(false);
		city_edit.setEnabled(false);
		city_edit.setEnabled(false);
		city_edit.setFocusableInTouchMode(false);
		zipcode_edit.setEnabled(false);
		zipcode_edit.setClickable(false);
		zipcode_edit.setFocusableInTouchMode(false);
		
		
		done_tv.setVisibility(View.GONE);
		edit_tv.setVisibility(View.VISIBLE);
		pretext.setVisibility(View.GONE);

		get_states();
		/*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text, statearr);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		state.setAdapter(dataAdapter);

		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text, statearr1);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		if (!state.equals(null)) {
			int spinnerPosition = dataAdapter1.getPosition(state_);
			state.setSelection(spinnerPosition);
		}*/

		base= Common_data.getPreferences(getApplicationContext(), "base");
		
		
		done_tv.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View arg0) 
			{
				
					getdata();
					if(validationforsecond())
					{
					registerOnServer();
					}
				
			}
			
			


		
			private boolean validationforsecond() {
				
				String REQUIRED_MSG = "Please enter First Name";
				String REQUIRED_MSG1 = "Please enter Last Name";
				String REQUIRED_MSG2 = "Please enter Mobile Number";
				String REQUIRED_MSG3 = "Please enter Full Address";
				String REQUIRED_MSG4 = "Please enter valid zip code";
				
				boolean	flag=false;
				
				if(new_address_line1.length()==0)
				{
					
					Toast.makeText(ProfileActivity.this, "Please enter Address Line 1", Toast.LENGTH_SHORT).show();
				}
				else if(new_state.equals("Select State"))
				{
					
					Toast.makeText(ProfileActivity.this, "Please select state", Toast.LENGTH_SHORT).show();
				}
				
				else if(new_city.length()==0)
				{
					
					Toast.makeText(ProfileActivity.this, "Please enter city", Toast.LENGTH_SHORT).show();
				}
				else if(new_zip_code.length()==0)
				{
					Toast.makeText(ProfileActivity.this, "Please enter Zip Code", Toast.LENGTH_SHORT).show();
				}
				/*else if(zipcode.length()<3|zipcode.length()>5)
				{
					
					Toast.makeText(ProfileActivity.this, REQUIRED_MSG4, Toast.LENGTH_SHORT).show();
				}*/
				else
				{
					flag=true;
				}
				
				return flag;
			}


			private void getdata() 
			{
				fname=first_name_et.getText().toString().trim();
				lname=last_name_et.getText().toString().trim();
				phone=user_mobileno_et.getText().toString().trim();
				address=addressline1.getText().toString().trim();
				zipcode=zipcode_edit.getText().toString().trim();
				addressline2_ = addressline2.getText().toString().trim();

				new_address_line1 = addressline1.getText().toString().trim();
				new_address_line2 = addressline2.getText().toString().trim();
				//new_state = String.valueOf(state.getSelectedItem());
				new_state = statearr1[state.getSelectedItemPosition()].trim();
				new_city = city_edit.getText().toString().trim();
				new_zip_code = zipcode_edit.getText().toString().trim();
				
			}

		});
		
		
		setdata();
		
		back_im.setOnClickListener(this);
		back_tv.setOnClickListener(this);
		edit_tv.setOnClickListener(this);
		
		user_basecurrency_sp.setOnItemSelectedListener(this);
		user_contry_sp.setOnItemSelectedListener(this);
		
		 Map<String, String> currencies= Common_data.getAvailableCurrencies();
			
		  for (String country : currencies.keySet()) 
		  	{
	            String currencyCode = currencies.get(country);
	            currency_al.add(currencyCode);
	        }
		  
		 Collections.sort(currency_al);
		 currency_al = new ArrayList<String>(new LinkedHashSet<String>(currency_al));
		
		ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this,R.layout.custom_spinner_right_text, currency_al);
		currencyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		user_basecurrency_sp.setAdapter(currencyAdapter);
		
		user_basecurrency_sp.setSelection(currency_al.indexOf(Common_data.getPreferences(getApplicationContext(), "currency")));
			
		user_contry_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(done_tv.getVisibility()==View.VISIBLE)
				{
					
				}
			}
		});
		
		state.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				
				val  = (String) state.getItemAtPosition(position);
				//Toast.makeText(activity, ""+val, Toast.LENGTH_SHORT).show();
				
				String st= Common_data.getPreferences(activity, "state");
				/*if (val.equals("Select State") || val.equals(st) ) {
					
				}else{
					//getCities(val);
				}*/
				
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		user_dob_tv.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View yash) 
			{
				if(done_tv.getVisibility()==View.VISIBLE)
				{
				Calendar c=Calendar.getInstance();
				current_day=c.get(Calendar.DATE);
				current_month=c.get(Calendar.MONTH);
				current_year=c.get(Calendar.YEAR);
				new DatePickerDialog(ProfileActivity.this, datePickerListener, current_year-30, current_month, current_day).show();
				}
			}
		});

		//getApp().touch();
	}

	@Override
	protected void onStop() {
		Common_data.alertPinDialog(ProfileActivity.this);
		super.onStop();
	}
	protected void getCities(String val) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		RequestParams params = new RequestParams();
		try {
			obj.put("state", val);
			params.put("request", obj.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("state",""+obj.toString());
		RestHttpClient.postParams("selectcity", params, new CitiesHandler());
		
	}
	
	public class CitiesHandler extends AsyncHttpResponseHandler{
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			dialog=new ProgressDialog(activity).show(activity, null, "Getting cities...");
		}
		
		@Override
		@Deprecated
		public void onSuccess(String response) {
			// TODO Auto-generated method stub
			super.onSuccess(response);
			Log.i("Response",""+response);
			dialog.dismiss();
			try{
				
				JSONObject obj = new JSONObject(response);
				JSONArray data = obj.optJSONArray("data");
				if(data.length()!=0){
					citiesList = new ArrayList<String>();
					for(int i=0;i<data.length();i++){
						
						String val = data.optString(i);
						citiesList.add(val);
						Log.i("CityList",""+citiesList.size());
					}
				}
				final String [] addressStrings = new String [citiesList.size()];
				for(int i=0; i<citiesList.size(); i++)
				    addressStrings[i] = citiesList.get(i).toString();
				
				 AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			        builder.setTitle("Select Your City");
			        builder.setItems(addressStrings, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int position) {
			                // Do something with the selection
			                city_edit.setText(addressStrings[position]);
			                city_ = addressStrings[position];
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();

			    
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			// TODO Auto-generated method stub
			super.onFailure(error);
			dialog.dismiss();
		}
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			dialog.dismiss();
		}
	}

	
	private int getIndex(Spinner spinner, String myString) {

		int index = 0;
        Log.i("MyString", "x" + myString);
        for (int i=0;i< statearr.length;i++){
        	String val = statearr[i];
            if (val.equals(myString)){
                index = i;
			}
		}
        Log.i("INdex", "" + index);
        return index;
}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() 
	{
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
		{
			byear = selectedYear;
			bmonth = selectedMonth;
			bday = selectedDay;
			//user_dob_tv.setText(new StringBuilder().append(bday).append("-").append(bmonth + 1).append("-").append(byear).append(" "));
			user_dob_tv.setText(Common_data.formatDate(byear, bmonth, bday));
			StringBuilder bodsb= ((new StringBuilder().append(bday).append("-").append(bmonth + 1).append("-").append(byear).append(" ")));
			dob=user_dob_tv.getText().toString();
			
			if(current_year < byear)
			{
				user_dob_tv.setText("Date of Birth");
				Toast.makeText(getBaseContext(), "Please select correct date", Toast.LENGTH_LONG).show();
			}
			
			if (current_year == byear)
			{
				if(current_month <  bmonth)
				{
					user_dob_tv.setText("Date of Birth");
					Toast.makeText(getBaseContext(), "Please select correct date", Toast.LENGTH_LONG).show();
				}
				
				if(current_day <  bday)
				{
					user_dob_tv.setText("Date of Birth");
					Toast.makeText(getBaseContext(), "Please select correct date", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	private void setdata() 
	{
		username_tv.setText(""+fname+" "+lname);
		user_dob_tv.setText(dob);

         Log.i("Base", base);
		if(base.equalsIgnoreCase("CAD")){
			user_currency_tv.setText("Canadian Dollar");
			stateText.setText("Province");
			zipText.setText("Postal Code");
			user_contry_tv.setText("Canada");
			zipcode_edit.setInputType(InputType.TYPE_CLASS_TEXT);
		}else{
			user_currency_tv.setText("US Dollar");
			stateText.setText("State");
			zipText.setText("Zip Code");
			zipcode_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
			user_contry_tv.setText("US");
		}


		//user_contry_tv.setText(country);
		user_email_tv.setText(email);
		user_mobileno_tv.setText(phone);
		
		addressline1.setText(address);
		if (addressline2_.equalsIgnoreCase("null")){
			addressline2.setText("");
		}else{
			addressline2.setText(addressline2_);
		}

		city_edit.setText(city_);

		zipcode_edit.setText(zipcode);

	}

	private void getDataFormPrefrance()  //change getBaseContext to getApplicationContext
	{
		//fname= Common_data.getPreferences(getApplicationContext(), "fname");
		//lname= Common_data.getPreferences(getApplicationContext(), "lname");
		//email= Common_data.getPreferences(getApplicationContext(), "email");
		//phone= Common_data.getPreferences(getApplicationContext(), "phone").replace("+1", "");

		fname=new PrefManager<String>(getApplicationContext()).get("fname","test");
		lname=new PrefManager<String>(getApplicationContext()).get("lname","test");
		email = new PrefManager<String>(getApplicationContext()).get("email","test");
		phone = new PrefManager<String>(getApplicationContext()).get("phone","test");


		//dob= Common_data.getPreferences(getApplicationContext(), "dob");
		dob = new PrefManager<String>(getApplicationContext()).get("dob","dob");



		//currency= Common_data.getPreferences(getBaseContext(), "currency");
		currency = new PrefManager<String>(getApplicationContext()).get("currency","currency");
		
		//pradeep
		//base= Common_data.getPreferences(getApplicationContext(), "base");
		base = new PrefManager<String>(getApplicationContext()).get("base","base");
		//---------
		//state_= Common_data.getPreferences(getApplicationContext(), "state");
		//addressline2_ = Common_data.getPreferences(getApplicationContext(), "address2");
		//country= Common_data.getPreferences(getApplicationContext(), "country");
		//userid= Common_data.getPreferences(getApplicationContext(), "userid");
		//address= Common_data.getPreferences(getApplicationContext(), "address");
		//zipcode= Common_data.getPreferences(getApplicationContext(), "zipcode");
		//city_= Common_data.getPreferences(getApplicationContext(), "city");
		state_ = new PrefManager<String>(getApplicationContext()).get("state_","state_");
		addressline2_ = new PrefManager<String>(getApplicationContext()).get("address2","address2");
		country = new PrefManager<String>(getApplicationContext()).get("country","country");
		userid = new PrefManager<String>(getApplicationContext()).get("userid","userid");
		address = new PrefManager<String>(getApplicationContext()).get("address","address");
		zipcode = new PrefManager<String>(getApplicationContext()).get("zipcode","zipcode");
		city_ = new PrefManager<String>(getApplicationContext()).get("city","city");




	}

	private void getXmlId() 
	{
		back_im=(ImageView)findViewById(R.id.back_im);
		back_tv=(TextView)findViewById(R.id.back_tv);
		done_tv=(TextView)findViewById(R.id.done_tv);
		username_tv=(TextView)findViewById(R.id.username_tv);
		first_name_et=(EditText)findViewById(R.id.first_name_et);
		user_dob_tv=(TextView)findViewById(R.id.user_dob_tv);
		user_currency_tv=(TextView)findViewById(R.id.user_currency_tv);
		user_email_tv=(TextView)findViewById(R.id.user_email_tv);
		user_mobileno_tv=(TextView)findViewById(R.id.user_mobileno_tv);
		user_mobileno_et=(EditText)findViewById(R.id.user_mobileno_et);
		user_contry_tv=(TextView)findViewById(R.id.user_contry_tv);


		LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
		back_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent2=new Intent(getBaseContext(),MainActivity.class);
				startActivity(intent2);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});


		//user_address_tv=(TextView)findViewById(R.id.user_address_tv);
		//user_address_et=(EditText)findViewById(R.id.user_address_et);
		//user_zipcode_tv=(TextView)findViewById(R.id.user_zipcode_tv);
		//user_zipcode_et=(EditText)findViewById(R.id.user_zipcode_et);
		stateText=(TextView) findViewById(R.id.stateText);
		
		state = (Spinner)findViewById(R.id.stateSpinner);
		city_edit = (EditText)findViewById(R.id.cityedit);
		addressline1 = (EditText)findViewById(R.id.address_line1_et);
		addressline2 = (EditText)findViewById(R.id.address_line2_et);
		zipcode_edit = (EditText)findViewById(R.id.zipCode);
		zipText=(TextView)findViewById(R.id.zipText);
		
		edit_tv=(TextView)findViewById(R.id.edit_tv);
		last_name_et=(EditText)findViewById(R.id.last_name_et);
		currency_drop=(ImageView)findViewById(R.id.currency_drop);
		contry_drop=(ImageView)findViewById(R.id.contry_drop);
		user_basecurrency_sp=(Spinner)findViewById(R.id.user_basecurrency_sp);
		user_contry_sp=(Spinner)findViewById(R.id.user_contry_sp);
		//fill_address_rl=(RelativeLayout)findViewById(R.id.fill_address_rl);
		//uneditdetail_rl=(RelativeLayout)findViewById(R.id.uneditdetail_rl);
		pretext = (TextView)findViewById(R.id.preText);
	//	fill_sv=(ScrollView)findViewById(R.id.fill_sv);
	//	address_first_line_et=(EditText)findViewById(R.id.address_first_line_et);
	//	address_second_line_et=(EditText)findViewById(R.id.address_second_line_et);
		//zipcode_et=(EditText)findViewById(R.id.zipcode_et);
		mobile_code_sp=(Spinner) findViewById(R.id.mobile_code_sp);
	}

	@Override
	public void onClick(View yash) {
		
		
		switch (yash.getId()) {
		
		case R.id.back_im:
			Intent intent=new Intent(getBaseContext(),MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;

			
			
		case R.id.back_tv:
			Intent intent2=new Intent(getBaseContext(),MainActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			break;
			
		
			
		case R.id.edit_tv:

			Toast.makeText(getBaseContext(), "Please note that you can only edit address details", Toast.LENGTH_SHORT).show();
			addressline1.setEnabled(true);
			addressline1.setClickable(true);
			addressline1.setFocusableInTouchMode(true);
			//addressline1.setTextColor(Color.BLACK);
			addressline2.setEnabled(true);
			addressline2.setClickable(true);
			addressline2.setFocusableInTouchMode(true);
			//addressline2.setTextColor(Color.BLACK);
			state.setEnabled(true);




//			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text1, statearr);
//			dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
//			// attaching data adapter to spinner
//			state.setAdapter(dataAdapter);
////
//			ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text1, statearr1);
//			dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
//
//			if (!state.equals(null)) {
//				int spinnerPosition = dataAdapter1.getPosition(state_);
//				state.setSelection(spinnerPosition);
//			}

			city_edit.setEnabled(true);
			city_edit.setEnabled(true);
			city_edit.setFocusableInTouchMode(true);
			//city_edit.setTextColor(Color.BLACK);
			zipcode_edit.setEnabled(true);
			zipcode_edit.setClickable(true);
			zipcode_edit.setFocusableInTouchMode(true);
			//zipcode_edit.setTextColor(Color.BLACK);
			
			edit_tv.setVisibility(View.GONE);
			//pretext.setVisibility(View.VISIBLE);
			done_tv.setVisibility(View.VISIBLE);

			
			//first_name_et.setVisibility(View.VISIBLE);
			//username_tv.setVisibility(View.GONE);
			//first_name_et.setText(fname);
			
			//last_name_et.setVisibility(View.VISIBLE);
			//username_tv.setVisibility(View.GONE);
			//last_name_et.setText(lname);
			
			//user_mobileno_et.setVisibility(View.VISIBLE);
			//mobile_code_sp.setVisibility(View.VISIBLE);
			//user_mobileno_tv.setVisibility(View.GONE);
			//user_mobileno_et.setText(phone);
			
			//user_address_et.setVisibility(View.VISIBLE);
			//user_address_tv.setVisibility(View.GONE);
		
			//user_address_et.setText(address);
			
			//ser_zipcode_et.setVisibility(View.VISIBLE);
			//user_zipcode_tv.setVisibility(View.GONE);
			//user_zipcode_et.setText(zipcode);
			
			//user_email_tv.setTextColor(R.color.light_gry);
			//currency_drop.setVisibility(View.VISIBLE);
			//contry_drop.setVisibility(View.VISIBLE);
			
		//	user_basecurrency_sp.setVisibility(View.VISIBLE);
			//user_currency_tv.setVisibility(View.GONE);
			//user_currency_tv.setTextColor(R.color.light_gry);
			
			//user_contry_tv.setVisibility(View.GONE);
			//user_contry_sp.setVisibility(View.VISIBLE);
			//contry_drop.setVisibility(View.VISIBLE);
			
			
			
			break;
			
			
		case R.id.user_dob_tv:
			
			break;
			
		default:
			break;
		}
		
		
	}
	private void parseJson(String jsonarray) {
		if (jsonarray != null)
		{
			try
			{
				JSONObject jsonObject=new JSONObject(jsonarray);
				boolean b = false;
				int response_code = jsonObject.getInt("code");
				if(1001 == response_code)
					b = true;
				if(b)
				{
					JSONArray array=jsonObject.getJSONArray("data");
					   
					   for (int i=0;i<array.length();i++)
					   {
						JSONObject object=array.getJSONObject(i);
						country=object.getString("country");
						country_al.add(country);
						String countrycode=object.getString("country_code");
						CountryCode_al.add(countrycode);
						callspinner();
					   }
				}
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void callspinner() {
		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,R.layout.custom_spinner_right_text, country_al);
		countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		user_contry_sp.setAdapter(countryAdapter);
		user_contry_sp.setSelection(country_al.indexOf(Common_data.getPreferences(getApplicationContext(), "country")));
		
		ArrayAdapter<String> code = new ArrayAdapter<String>(this,R.layout.custom_spinner_right_text, CountryCode_al);
		countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mobile_code_sp.setAdapter(code);
	}

	
			private void registerOnServer() {
				
				RequestParams params=new RequestParams();
				JSONObject object=new JSONObject();
				try 
				{
					//object.put("userid", userid);
					object.put("address_line1", new_address_line1);
					object.put("address_line2", new_address_line2);
					object.put("state", new_state);
					object.put("city", new_city);
					object.put("zip_code", new_zip_code);

					
					params.put("request", object.toString());
					Log.d("request", params.toString());
				} 

				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				RestHttpClient.postParams("auth/updateProfile", params, new RegistrationHandler(), Login.token);
	}

			class RegistrationHandler extends AsyncHttpResponseHandler
			{
				@SuppressWarnings("static-access")
				@Override
				public void onStart() 
				{
					super.onStart();
					if(activity!=null && !activity.isFinishing())
					{
						Log.d("onStart", "onStart");
						dialog=new ProgressDialog(activity).show(activity, null, "Updating... ");
					}
				}
				@SuppressWarnings("deprecation")
				@Override
				public void onSuccess(String result) 
				{
					super.onSuccess(result);
					if(result.length()>0)
					{
						Log.d("response", result);
						try
						{
							JSONObject jsonObject=new JSONObject(result);
							boolean b = false;
							int response_code = jsonObject.getInt("code");
							if(1001 == response_code)
								b = true;
							String msg=jsonObject.optString("message");
							if(b)
							{
								Common_data.setPreference(activity, "address", new_address_line1);
								Common_data.setPreference(activity, "address2", new_address_line2);
								Common_data.setPreference(activity, "city", new_city);
								Common_data.setPreference(activity, "state", new_state);
								Common_data.setPreference(activity, "zipcode", new_zip_code);

								
								Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

								
								Intent intent1=new Intent(getApplicationContext(),ProfileActivity.class);
								startActivity(intent1);
								finish();
								overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
							}
							else if(1020 == response_code){
								final Dialog d=new Dialog(ProfileActivity.this);
								d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
								d.requestWindowFeature(Window.FEATURE_NO_TITLE);
								d.setCancelable(false);
								d.setContentView(R.layout.success_response);
								TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
								tv_success.setText(msg);
								Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
								btn_ok.setText("OK");
								btn_ok.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										//Common_data.Preferencesclear(MainActivity.th);
										Intent i = new Intent(ProfileActivity.this, Login.class);
										startActivity(i);
									}
								});
								d.show();
							}
							else if(1030 == response_code){
								//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
								final Dialog d=new Dialog(ProfileActivity.this);
								d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
								d.requestWindowFeature(Window.FEATURE_NO_TITLE);
								d.setCancelable(false);
								d.setContentView(R.layout.dialog1);
								TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
								tv_dialog.setText(msg);
								Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
								btn_ok.setText("Update Now");
								btn_ok.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View arg0) {
										d.dismiss();
										Intent i = new Intent(Intent.ACTION_VIEW);
										i.setData(Uri.parse(getString(R.string.app_store_url)));
										startActivity(i);

										finish();
									}
								});
								d.show();
							}
							else
							{
							Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
							}
						}
						catch (JSONException e) 
						{
							e.printStackTrace();
						}
					}
				}
				@Override
				public void onFinish() 
				{
					Log.d("onFinish", "onFinish");
					super.onFinish();
					if(dialog!=null && dialog.isShowing())
					{
						dialog.dismiss();
					}
				}
				@Override
				@Deprecated
				public void onFailure(int statusCode, Throwable error, String content) 
				{
					Log.d("onFailure", "onFailure");
					super.onFailure(statusCode, error, content);
				}
			}

			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int pos, long arg3) {
				
				currency= String.valueOf(user_basecurrency_sp.getSelectedItem());
				country=String.valueOf(user_contry_sp.getSelectedItem());	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
			@Override
			public void onBackPressed() {
				Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intent1);
				finish();
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
				super.onBackPressed();
			}

	public ControlApplication getApp()
	{
		return (ControlApplication)this.getApplication();
	}


	@Override
	public void onUserInteraction()
	{
		super.onUserInteraction();
		//getApp().touch();
		Log.d("User", "User interaction to " + this.toString());
		Log.e("My Activity Touched", "My Activity Touched");

	}

	private void get_states()
	{
		// TODO Auto-generated method stub

		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try
		{
			//object.put("userid", userid);
			object.put("country_iso2", country_iso2);
			params.put("request", object.toString());
			Log.d("request", params.toString());
		}

		catch (JSONException e)
		{
			e.printStackTrace();
		}

			RestHttpClient.postParams("auth/getStates_Provinces", params, new GetStates());
	}

	/******* Async Task class for running background Services *******/
	class GetStates extends AsyncHttpResponseHandler
	{
		@SuppressWarnings("static-access")
		@Override
		public void onStart()
		{
			if(activity!=null && !activity.isFinishing())
			{
				Log.d("onStart", "onStart");
				dialog=new ProgressDialog(activity).show(activity, null, "Retrieving states... ");
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result)
		{
			Log.i("response", result.toString());

			super.onSuccess(result);
			Log.i("response", result.toString());
			dialog.dismiss();
			try {
				if (result.length() > 0)
				{
					Log.i("response", "" + result);
					Log.d("response", "" + result);
					JSONObject jsonObject=new JSONObject(result);
					int response_code = jsonObject.getInt("code");
					String message = jsonObject.getString("message");
					JSONArray state_data = jsonObject.getJSONArray("data");
					if(1001 == response_code)
					{
						Log.d("state jsonarray", state_data.toString());
						statearr = new String[state_data.length()+1];
						statearr1 = new String[state_data.length()+1];
						if(base.equalsIgnoreCase("CAD")){
							statearr[0] = "Select Province";
							statearr1[0] = "Select Province";
						}else if(base.equalsIgnoreCase("USD")){
							statearr[0] = "Select State";
							statearr1[0] = "Select State";
						}

						for (int i = 0; i < state_data.length(); i++) {
							JSONObject state_data_object = state_data.getJSONObject(i);
							statearr[i+1] = state_data_object.getString("state_province");
							statearr1[i+1] = state_data_object.getString("state_province_a");
						}
						 dataAdapter = new ArrayAdapter<String>(ProfileActivity.this, R.layout.custom_spinner_right_text1, statearr);
						dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);
						// attaching data adapter to spinner
						state.setAdapter(dataAdapter);

						 dataAdapter1 = new ArrayAdapter<String>(ProfileActivity.this, R.layout.custom_spinner_right_text1, statearr1);
						dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item1);

						if (!state.equals(null)) {
							int spinnerPosition = dataAdapter1.getPosition(state_);
							state.setSelection(spinnerPosition);
						}
						state.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent, View view,
													   int position, long arg3) {
								// TODO Auto-generated method stub

								String val = (String) state.getItemAtPosition(position);


								if (val.equals("Select State")) {

								} else {
									Toast.makeText(activity, ""+val, Toast.LENGTH_SHORT).show();
									//getCities(val);
								}


							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
						});


					}else if(1030 == response_code){
						Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(ProfileActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(message);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("Update Now");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								d.dismiss();
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(getString(R.string.app_store_url)));
								startActivity(i);

								finish();
							}
						});
						d.show();
					}
					else {
						Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(ProfileActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(message);
						tv_dialog.setTextColor(Color.RED);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								d.dismiss();
							}
						});
						d.show();
					}
				}
				else
				{

				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}



		@Override
		public void onFailure(Throwable e, String errorResponse)
		{

			Log.i("response", ""+errorResponse);
			dialog.dismiss();
			networkErrorDialog();
		}

		@Override
		public void onFinish()
		{
			super.onFinish();
			dialog.dismiss();
		}
	}


	public void networkErrorDialog(){

		final Dialog d=new Dialog(ProfileActivity.this);
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setCancelable(false);
		d.setContentView(R.layout.network_error);

		Button tryagain=(Button) d.findViewById(R.id.try_again);
		tryagain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(ProfileActivity.this, Registration.class);
				startActivity(i);
				finish();
			}
		});
		d.show();
	}
}
