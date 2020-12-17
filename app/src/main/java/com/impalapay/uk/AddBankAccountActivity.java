package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddBankAccountActivity extends FragmentActivity implements
		OnItemSelectedListener {

	// Spinner country_tv;
	String country, bank_name, aba_number, account_number, address_line1, address_line2, city, state, zip_code, userid,state_code;
	int swiftcode_int;
	EditText bank_et, account_number_et, aba_number_et;
	Button add_account_bt;
	ProgressDialog dialog;
	ArrayList<String> country_al = new ArrayList<String>();

	int cardLength = 0;
	private TextView back_tv;
	private TextView next_tv;

	EditText address_line1_et, address_line2_et, zip_code_et, city_et, state_et,address_city;
	LinearLayout address_ly;
	String country_iso2,base;
	boolean address_required=false;
	Spinner address_state;

	int active_card_ref_id = 0;
	private String[] statearr;
	private String[] statearr1;
	AddBankAccountActivity activity = this;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_bank_account);
		base=Common_data.getPreferences(AddBankAccountActivity.this, "base");
		xmlid();
		Common_data.setupUI(findViewById(R.id.hide),
				AddBankAccountActivity.this);
		Toast.makeText(activity, Common_data.getPreferences(AddBankAccountActivity.this, "address"), Toast.LENGTH_SHORT).show();
		// country_provider();

		// country_tv.setOnItemSelectedListener(this);

		/*
		 * aba_number_et.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int arg1, int
		 * arg2, int arg3) { Log.d("s.toString().length()",
		 * ""+s.toString().length());
		 * 
		 * if((s.toString().length() > cardLength) && (s.toString().length() <
		 * 14)) { if(s.toString().length() < 5) { if(s.toString().length()%5==4)
		 * { aba_number_et.setText(s.toString()+"-");
		 * aba_number_et.setSelection(s.toString().length()+1);
		 * 
		 * } cardLength = s.toString().length(); } if(s.toString().length() > 5
		 * && s.toString().length() < 8) { Log.d("in codition","in codition");
		 * 
		 * if(s.toString().length()%5==2) { Log.d("in if","in if");
		 * aba_number_et.setText(s.toString()+"-");
		 * aba_number_et.setSelection(s.toString().length()+1);
		 * 
		 * }cardLength = s.toString().length(); }
		 * 
		 * if(s.toString().length() > 8 && s.toString().length() < 11) {
		 * Log.d("in codition 2","in codition 2" );
		 * 
		 * if(s.length()==10) { Log.d("in if","in if");
		 * aba_number_et.setText(s.toString()+"-");
		 * aba_number_et.setSelection(s.toString().length()+1); } cardLength =
		 * s.toString().length(); } } cardLength = s.toString().length(); }
		 * 
		 * @Override public void beforeTextChanged(CharSequence arg0, int arg1,
		 * int arg2, int arg3) {
		 * 
		 * }
		 * 
		 * @Override public void afterTextChanged(Editable arg0) { } });
		 */

		add_account_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getdata();
				Log.d("bank_name", "" + bank_name);
				Log.d("aba_number", "" + aba_number);
				Log.d("masked_card_number", "" + account_number);
				Log.d("country", "" + country);
				Log.d("userid", "" + userid);

				if (validation()) {
					if (address_required) {
						if (validation3()) {
							registerOnServer();

						}
					} else {
						registerOnServer();
					}
				}
			}

		});

		back_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Common_data.setPreference(getApplicationContext(), "alertshow", "false");
				finish();
			}
		});
		next_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getdata();
				Log.d("bank_name", "" + bank_name);
				Log.d("aba_number", "" + aba_number);
				Log.d("masked_card_number", "" + account_number);
				Log.d("country", "" + country);
				Log.d("userid", "" + userid);

				if (validation()) {
					if (address_required) {
						if (validation3()) {
							registerOnServer();

						}
					} else {
						registerOnServer();
					}
				}
			}
		});
	}

	@Override
	public void onStop() {
		Common_data.alertPinDialog(AddBankAccountActivity.this);
		super.onStop();
	}

	public void registerOnServer() {
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			object.put("user_id", userid);
			object.put("bank_name", bank_name);
			object.put("routing_number", aba_number);
			object.put("account_number", account_number);
			object.put("address_line1", address_line1);
            object.put("address_line2", address_line2);
            object.put("city_name", city);
            object.put("state", state);
			object.put("zip_code", zip_code);
			object.put("state_code", state_code);
            object.put("country", country);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		params.put("request", object.toString());

		Log.d("request", object.toString());

		RestHttpClient.postParams("banking/add", params, new RegistrationHandler());
	}

	class RegistrationHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {
			super.onStart();

			dialog = ProgressDialog.show(AddBankAccountActivity.this, "",
					"Adding Bank Account...");

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
					boolean b = false;
                    if(1001 == response_code) b = true;
					String msg = jsonObject.getString("message");
					if (b) {
						//JSONArray array = jsonObject.getJSONArray("data");
						JSONObject object = jsonObject.getJSONObject("data");

						Common_data.setPreference(AddBankAccountActivity.this,
								"data", result);

						JSONArray bankdetails_array = object
								.getJSONArray("bank");
                        Log.d("bankdetails_array", bankdetails_array.toString());
						Common_data.setPreference(AddBankAccountActivity.this,
								"bankdetails_array",
								bankdetails_array.toString());

						Toast.makeText(AddBankAccountActivity.this,
								"Your account has been successfully added",
								Toast.LENGTH_SHORT).show();

						/*
						 * FragmentManager fm = getFragmentManager();
						 * FragmentTransaction ft = fm.beginTransaction();
						 * ft.replace(R.id.lk_profile_fragment, new
						 * ManageAccount()); ft.addToBackStack(null);
						 * ft.commit();
						 */

						final Dialog d=new Dialog(AddBankAccountActivity.this);
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
								finish();
								d.dismiss();
							}
						});
						d.show();

					}
					else if(1010 == response_code){
						final Dialog d=new Dialog(AddBankAccountActivity.this);
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
								//Intent i = new Intent(AddBankAccountActivity.this, Login.class);
								//startActivity(i);
								d.dismiss();
							}
						});
						d.show();
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(AddBankAccountActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("Update Now");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								Intent i = new Intent(AddBankAccountActivity.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(AddBankAccountActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("Ok");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								d.dismiss();
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(getString(R.string.app_store_url)));
								startActivity(i);
							}
						});
						d.show();
					}
					else{
						String message = jsonObject.getString("message");

						Log.d("message:", message);

						Toast.makeText(AddBankAccountActivity.this, message,
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			//bank_et.setText("");
			//aba_number_et.setText("");
			//account_number_et.setText("");
			Log.d("onFinish", "onFinish");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			try {
				/*String what = getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat",
						what);*/
				Common_data.setPreference(getApplicationContext(), "from",
						"link");
			} catch (Exception e) {
				e.printStackTrace();
			}
			//finish();
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			Log.i("onFailure", content);
			super.onFailure(statusCode, error, content);
		}
	}

	private void getdata() {
		bank_name = bank_et.getText().toString();
		aba_number = aba_number_et.getText().toString();
		account_number = account_number_et.getText().toString();
		address_line1 = address_line1_et.getText().toString();
		address_line2 = address_line2_et.getText().toString();
		city = city_et.getText().toString();
        //state = state_et.getText().toString();
		zip_code = zip_code_et.getText().toString();
		userid = Common_data.getPreferences(AddBankAccountActivity.this,
				"userid");

		if(address_required){
			address_line1 = address_line1_et.getText().toString();
			city = address_city.getText().toString();
			address_line2 = address_line2_et.getText().toString();
			zip_code = zip_code_et.getText().toString();
			state_code=statearr1[address_state.getSelectedItemPosition()].trim();
			state=statearr[address_state.getSelectedItemPosition()].trim();
			country=country_iso2;

		}else{
			address_line1=Common_data.getPreferences(AddBankAccountActivity.this, "address");
			address_line2=Common_data.getPreferences(AddBankAccountActivity.this, "address2");
			city=Common_data.getPreferences(AddBankAccountActivity.this, "city");
			zip_code=Common_data.getPreferences(AddBankAccountActivity.this, "zipcode");
			state_code=Common_data.getPreferences(AddBankAccountActivity.this, "state");
			state=Common_data.getPreferences(AddBankAccountActivity.this, "state");
			country=Common_data.getPreferences(AddBankAccountActivity.this, "country");
		}

	}

	private void xmlid() {

		back_tv = (TextView) findViewById(R.id.back_tv);
		next_tv = (TextView) findViewById(R.id.done_tv);

		address_line1_et = (EditText) findViewById(R.id.address_line1_et);
		address_line2_et = (EditText) findViewById(R.id.address_line2_et);
        city_et = (EditText) findViewById(R.id.city_et);
        //state_et = (EditText) findViewById(R.id.state_et);
		zip_code_et = (EditText) findViewById(R.id.zip_code_et);

		// country_tv=(Spinner)findViewById(R.id.country_tv);
		bank_et = (EditText) findViewById(R.id.bank_et);
		aba_number_et = (EditText) findViewById(R.id.aba_number_et);
		account_number_et = (EditText) findViewById(R.id.account_number_et);
		add_account_bt = (Button) findViewById(R.id.add_account_bt);
		address_city=(EditText)findViewById(R.id.city_et);
		address_state=(Spinner)findViewById(R.id.state_sp);
		address_ly=(LinearLayout)findViewById(R.id.address_ly);
		if(base.equalsIgnoreCase("CAD")){
			country_iso2 ="ca";
		}else if(base.equalsIgnoreCase("USD")){
			country_iso2 ="us";
		}

		if (Common_data.getPreferences(AddBankAccountActivity.this, "ssn").equals("null") &&
				Common_data.getPreferences(AddBankAccountActivity.this, "passport_num").equals("null")) {
			Log.d("Request Legal Data", "To be requested");
			address_required=true;
			get_states();
			if(base.equalsIgnoreCase("CAD")){
				zip_code_et.setHint("Postal Code");
				zip_code_et.setInputType(InputType.TYPE_CLASS_TEXT);

			}else if(base.equalsIgnoreCase("USD")){
				zip_code_et.setHint("Zip Code");
				zip_code_et.setInputType(InputType.TYPE_CLASS_NUMBER);

			}

		} else {
			address_ly.setVisibility(View.GONE);
			address_required=false;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {

		// country= String.valueOf(country_tv.getSelectedItem());
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	private boolean validation() {
		String REQUIRED_MSG = "Please enter the bank name";
		String REQUIRED_MSG2 = "Please enter the routing number";
		String REQUIRED_MSG3 = "Please enter the Account number";
		String REQUIRED_MSG24 = "Please enter the correct routing number";
		String REQUIRED_MSG5 = "Account number should be 16 digits";

		boolean flag = false;

		if (bank_name.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, REQUIRED_MSG,
					Toast.LENGTH_SHORT).show();
		} else if (aba_number.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, REQUIRED_MSG2,
					Toast.LENGTH_SHORT).show();
		}
		else if (account_number.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, REQUIRED_MSG3,
					Toast.LENGTH_SHORT).show();
		}
		/*else if (address_line1_et.getText().toString().trim().length() == 0) {

			Toast.makeText(AddBankAccountActivity.this,
					"Please Enter Address Line 1", Toast.LENGTH_SHORT).show();
		}
		else if (zip_code_et.getText().toString().length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, "Enter Zip Code",
					Toast.LENGTH_SHORT).show();
		}
        else if (city_et.getText().toString().length() == 0) {

            Toast.makeText(AddBankAccountActivity.this, "Enter city code",
                    Toast.LENGTH_SHORT).show();
        }
        else if (state_et.getText().toString().length() == 0) {

            Toast.makeText(AddBankAccountActivity.this, "Enter state code",
                    Toast.LENGTH_SHORT).show();
        }*/
        else {
			flag = true;
		}
		return flag;
	}
	private boolean validation3() {
		boolean flag = false;
		if (address_line1.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, "Please Enter Address Line 1", Toast.LENGTH_SHORT).show();
		}
		else if (zip_code.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, "Please Enter Zip Code or Postal Code", Toast.LENGTH_SHORT).show();
		}
		else if (city.length() == 0) {

			Toast.makeText(AddBankAccountActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
		}
		else if (state_code.equals("Select State")) {

			Toast.makeText(AddBankAccountActivity.this, "Please select your State", Toast.LENGTH_SHORT).show();
		}else if(state_code.equals("Select Province")){
			Toast.makeText(AddBankAccountActivity.this, "Please select your Province", Toast.LENGTH_SHORT).show();
		}else {
			flag = true;
		}

		return flag;
	}

	private void country_provider() {
		try {
			RestHttpClient.postParams("auth/getCountryDetails", null, new GetCountry());
		} catch (Exception e) {

		}
	}

	/******* Async Task class for running background Services *******/
	class GetCountry extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {
			dialog = ProgressDialog.show(AddBankAccountActivity.this, "",
					"Loading...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			super.onSuccess(result);
			Log.i("response", result.toString());
			dialog.dismiss();
			try {
				if (result.length() > 0) {
					parseJson(result);
				} else {
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailure(Throwable e, String errorResponse) {
			dialog.dismiss();
			Toast.makeText(AddBankAccountActivity.this,
					"unable to reach server", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFinish() {
			super.onFinish();
			dialog.dismiss();
		}
	}

	private void parseJson(String jsonarray) {

		if (jsonarray != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonarray);
				//boolean b = jsonObject.getBoolean("result");
                int response_code = jsonObject.getInt("code");
				String msg = jsonObject.getString("message");
				if (1001 == response_code) {
					JSONArray array = jsonObject.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						String country = object.getString("country");
						country_al.add(country);
						callspinner();
					}
				}
				else if(1030 == response_code){
					//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
					final Dialog d=new Dialog(AddBankAccountActivity.this);
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
						}
					});
					d.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	private void callspinner() {
		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(
				AddBankAccountActivity.this, R.layout.custom_spinner_text,
				country_al);
		countryAdapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		// country_tv.setAdapter(countryAdapter);

	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
	private void get_states() {
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
		ProgressDialog pd;
		@SuppressWarnings("static-access")
		@Override
		public void onStart()
		{
			if(activity!=null && !activity.isFinishing())
			{
				Log.d("onStart", "onStart");
				pd=new ProgressDialog(activity).show(activity, null, "Retrieving states... ");
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result)
		{
			Log.i("response", result.toString());

			super.onSuccess(result);
			Log.i("response", result.toString());
			pd.dismiss();
			try {
				if (result.length() > 0)
				{
					Log.i("response", "" + result);
					Log.d("response", "" + result);
					JSONObject jsonObject=new JSONObject(result);
					int response_code = jsonObject.getInt("code");
					String message = jsonObject.getString("message");

					if(1001 == response_code)
					{
						JSONArray state_data = jsonObject.getJSONArray("data");
						Log.d("state jsonarray", state_data.toString());
						statearr = new String[state_data.length()+1];
						statearr1 = new String[state_data.length()+1];
						if(base.equalsIgnoreCase("CAD")){
							statearr[0] = "Select Province";
							statearr1[0] = "Select Province";
						}else if(base.equalsIgnoreCase("USD")){
							statearr[0] = "Select State";
							statearr1[0] = "Select State";
						}else{
							statearr[0] = "Select State";
							statearr1[0] = "Select State";
						}

						for (int i = 0; i < state_data.length(); i++) {
							JSONObject state_data_object = state_data.getJSONObject(i);
							statearr[i+1] = state_data_object.getString("state_province");
							statearr1[i+1] = state_data_object.getString("state_province_a");
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddBankAccountActivity.this, R.layout.custom_spinner_right_text, statearr);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// attaching data adapter to spinner
						address_state.setAdapter(dataAdapter);
						address_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent, View view,
													   int position, long arg3) {
								// TODO Auto-generated method stub

								String val = (String) address_state.getItemAtPosition(position);


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
						//Log.d("statearr[6]",statearr[3]);
						//Log.d("statearr1[6]",statearr1[3]);
						//System.exit(1);
					}else if(1030 == response_code){
						Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(AddBankAccountActivity.this);
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
						final Dialog d=new Dialog(AddBankAccountActivity.this);
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
			Toast.makeText(getBaseContext(), errorResponse,Toast.LENGTH_SHORT).show();
			pd.dismiss();
			//networkErrorDialog();
		}

		@Override
		public void onFinish()
		{
			super.onFinish();
			pd.dismiss();
		}
	}
}
