package com.impalapay.uk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AttachCardActivity extends FragmentActivity {
	//View view;
	EditText card_number_et, cardname_et, valid_month_et, valid_year_et,
			 name_of_card_et, name_of_card_owner_et;
	Button attact_a_card_bt;
	int count = 0;
	String cardname, card_number, cvc = " ", name_of_card, userid,
			name_of_owner_card, valid_month, valid_year, currentyear1,addr_line1,addr_line2,zip_code,city_name,state_code,state,country;
	static String card;
	int valid_month_in, valid_year_in, current_day, current_month;
	int keyDel, valid_year_int, valid_month_int, current_year;
	int cardLength = 0;
	ProgressDialog dialog;
	Boolean validCard = true;
	Boolean edit = false;
	boolean showDebitDialog = false;
	TextView cvc_et;
	private TextView back_tv;
	private TextView next_tv;
	Spinner address_state;
	EditText addressline1,addressline2,addresspincode,address_city;
	ImageView cvv00;
	LinearLayout address_ly;
	String country_iso2,base;
	boolean address_required=false;

	int active_card_ref_id = 0;
	private String[] statearr;
	private String[] statearr1;
	AttachCardActivity activity = this;
	String address;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.attach_card_demo);
		//base=Common_data.getPreferences(AttachCardActivity.this, "base");
		base = new PrefManager<String>(getApplicationContext()).get("base","US");
		
		xmlid();
		Common_data.setupUI(findViewById(R.id.hide), AttachCardActivity.this);
		getdatafrompref();

		//Common_data.setPreference(AttachCardActivity.this, "attachcard", "0");
		new PrefManager<String>(getApplicationContext()).set("attachcard","0");



		Calendar c = Calendar.getInstance();
		current_day = c.get(Calendar.DATE);
		current_month = c.get(Calendar.MONTH);

		current_month = current_month + 1;

		SimpleDateFormat currentyear = new SimpleDateFormat("yy");
		currentyear1 = currentyear.format(new Date(System.currentTimeMillis()));

		current_year = Integer.parseInt(currentyear1);


		cvv00.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				final Dialog d = new Dialog(AttachCardActivity.this);
				d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				d.requestWindowFeature(Window.FEATURE_NO_TITLE);
				d.setCancelable(false);
				d.setContentView(R.layout.cvv);

				Button btn_cvv = (Button) d.findViewById(R.id.btn_cvv);
				btn_cvv.setText("Close");
				btn_cvv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//finish();
						d.dismiss();
					}
				});
				d.show();

			}
		});

		card_number_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.d("s.toString().length()", "" + s.toString().length());

				if (s.toString().length() < 4) {
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
				}
				if (s.toString().length() == 4) {
					card_number = card_number_et.getText().toString();

					Log.d("in card detail", "" + card_number);

					getCardVendor(card_number);

					getrightcardimage();
				}

				if ((s.toString().length() > cardLength)
						&& (s.toString().length() < 19)) {
					if (s.toString().length() % 5 == 4) {
						card_number_et.setText(s.toString() + "-");
						card_number_et.setSelection(s.toString().length() + 1);
					}
				}

				else if (s.toString().length() == 19) {
				}
				cardLength = s.toString().length();
			}

			private void getrightcardimage() {
				if (card.equals("Visa International")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.visa_card_img);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;

				} else if (card.equals("Amex Card")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.amex_card_img);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;

				} else if (card.equals("Master Card")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.master_card_img);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;
				} else if (card.equals("Japan Credit Bureau Card")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.jcb_card_img);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;
				} else if (card.equals("Discover Credit Card")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.discover_card_img);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;
				} else if (card.equals("Dinner club International")) {
					Drawable draw = getResources().getDrawable(
							R.drawable.dinner_club_imag);
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, draw, null);
					validCard = true;
				}

				else if (card.equals("blank")) {
					card_number_et.setCompoundDrawablesWithIntrinsicBounds(
							null, null, null, null);
					validCard = true; //false;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		attact_a_card_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getvalues();
				
				if (validation()) {
					if (validation2()) {
						if (validCard) {
							if(address_required){
								if(validation3()){
									registerOnServer();

								}
							}else{
								registerOnServer();
							}
							//acceptToBeDebited();

						} else {
							Toast.makeText(AttachCardActivity.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
		
		back_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();	
			}
		});
		
		next_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getvalues();

                Log.i("Validation 1",String.valueOf(validation()));
				if (validation()) {
					Log.i("1 Here Message","I am here");
					if (validation2()) {
						Log.i("2 Here Message","I am here");
						if (validCard) {
							Log.i("NOt Here Message","I am here");
							if(address_required){
								if(validation3()){
									Log.i("Message","I am here");
									registerOnServer();

								}
							}else{
								registerOnServer();
							}
							//acceptToBeDebited();

						} else {
							Toast.makeText(AttachCardActivity.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
	
	}

	@Override
	public void onStop() {
			//Common_data.alertPinDialog(AttachCardActivity.this);
		super.onStop();
	}
	
	
	private void getdatafrompref() {
		//userid = Common_data.getPreferences(AttachCardActivity.this, "userid");
		userid = new PrefManager<String>(getApplicationContext()).get("userid","");
	}

	private void xmlid() {
		
		back_tv=(TextView) findViewById(R.id.back_tv);
		next_tv=(TextView) findViewById(R.id.done_tv);
		
		addressline1=(EditText) findViewById(R.id.address_line1_et);
		addressline2=(EditText) findViewById(R.id.address_line2_et);
		addresspincode=(EditText) findViewById(R.id.zip_code_et);
		
		cardname_et = (EditText) findViewById(R.id.cardname_et);
		card_number_et = (EditText) findViewById(R.id.card_number_et);
		valid_month_et = (EditText) findViewById(R.id.valid_month_et);
		valid_year_et = (EditText) findViewById(R.id.valid_year_et);
		cvc_et = (TextView) findViewById(R.id.cvc);
		name_of_card_owner_et = (EditText)findViewById(R.id.name_of_card_et);
		attact_a_card_bt = (Button)findViewById(R.id.add_account_bt);
		cvv00 = (ImageView) findViewById(R.id.cvv00);
		address_city=(EditText)findViewById(R.id.city_et);
		address_state=(Spinner)findViewById(R.id.state_sp);
		address_ly=(LinearLayout)findViewById(R.id.address_ly);
		if(base.equalsIgnoreCase("CAD")){
			country_iso2 ="ca";
		}else if(base.equalsIgnoreCase("USD")){
			country_iso2 ="us";
		}

		LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
		back_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		//if (Common_data.getPreferences(AttachCardActivity.this, "zipcode").equals("")) {
		if(new PrefManager<String>(getApplicationContext()).get("zipcode","").equals("")){
			    Log.d("Request Legal Data", "To be requested");
			addressDetails();
		} else {
			address_ly.setVisibility(View.GONE);
			address_required=false;
		}
	}

	public void addressDetails(){
		address_required=true;
		address_ly.setVisibility(View.VISIBLE);
		get_states();
		if(base.equalsIgnoreCase("CAD")){
			addresspincode.setHint("Postal Code");
			//addresspincode.setText(Common_data.getPreferences(AttachCardActivity.this, "zipcode"));
			addresspincode.setText(new PrefManager<String>(getApplicationContext()).get("zipcode",""));
			addressline1.setHint("Street Address");
			//addressline1.setText(Common_data.getPreferences(AttachCardActivity.this, "address"));
			addressline1.setText(new PrefManager<String>(getApplicationContext()).get("address",""));

			addressline2.setHint("Apt./Unit number");
			//if(Common_data.getPreferences(AttachCardActivity.this, "address2").equalsIgnoreCase("null")){
			if (new PrefManager<String>(getApplicationContext()).get("address2","null").equalsIgnoreCase("null")){
				addressline2.setText("");
			}else{
				//addressline2.setText(Common_data.getPreferences(AttachCardActivity.this, "address2"));
				addressline2.setText(new PrefManager<String>(getApplicationContext()).get("address2","null"));
			}

			//address_city.setText(Common_data.getPreferences(AttachCardActivity.this, "city"));
			address_city.setText(new PrefManager<String>(getApplicationContext()).get("city",""));
			addresspincode.setInputType(InputType.TYPE_CLASS_TEXT);

		}else if(base.equalsIgnoreCase("USD")){
			addresspincode.setHint("Zip Code");
			addresspincode.setInputType(InputType.TYPE_CLASS_NUMBER);

		}
	}

	private void getvalues() {
		cardname = cardname_et.getText().toString();
		card_number = card_number_et.getText().toString();
		card_number = card_number.replace("-", "");
        Log.d("cn", card_number);
		valid_month = valid_month_et.getText().toString();
		valid_year = valid_year_et.getText().toString();

		cvc = cvc_et.getText().toString();
		name_of_owner_card = name_of_card_owner_et.getText().toString();
		Log.d("address", String.valueOf(address_required));

		if(address_required){
			address = addressline1.getText().toString();
			city_name = address_city.getText().toString();
			addr_line2 = addressline2.getText().toString();
			zip_code = addresspincode.getText().toString();
			state_code=statearr1[address_state.getSelectedItemPosition()].trim();
			state=statearr[address_state.getSelectedItemPosition()].trim();
			country=country_iso2;

		}else{
			//address=Common_data.getPreferences(AttachCardActivity.this, "address");
			//addr_line2=Common_data.getPreferences(AttachCardActivity.this, "address2");
			//city_name=Common_data.getPreferences(AttachCardActivity.this, "city");
			//zip_code=Common_data.getPreferences(AttachCardActivity.this, "zipcode");
			//state_code=Common_data.getPreferences(AttachCardActivity.this, "state");
			//state=Common_data.getPreferences(AttachCardActivity.this, "state");
			//country=Common_data.getPreferences(AttachCardActivity.this, "country");

			address = new PrefManager<String>(getApplicationContext()).get("address","");
			addr_line2 = new PrefManager<String>(getApplicationContext()).get("address2","");
			city_name = new PrefManager<String>(getApplicationContext()).get("city","");
			zip_code = new PrefManager<String>(getApplicationContext()).get("zipcode","");
			state_code = new PrefManager<String>(getApplicationContext()).get("state","");
			state = new PrefManager<String>(getApplicationContext()).get("state","");
			country = new PrefManager<String>(getApplicationContext()).get("country","");

		}


	}

	private void acceptToBeDebited() {
		final Dialog d = new Dialog(AttachCardActivity.this);
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.dialog2);
		d.setCancelable(false);
		d.show();

		final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv1);
		final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv1);

		final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img1);
		final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img1);

		//tv_reject.setVisibility(View.GONE);
		//iv_reject.setVisibility(View.GONE);


		tv_reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
			}
		});

		iv_reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
			}
		});



		verify_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				d.dismiss();
				registerOnServer();
			}
		});
		verify_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
				registerOnServer();
			}
		});

	}

	private void registerOnServer() {
		//Toast.makeText(activity, "Add card called", Toast.LENGTH_SHORT).show();
		//count = Integer.parseInt(Common_data.getPreferences(AttachCardActivity.this, card_number.substring(card_number.length() - 6)));
		count = Integer.parseInt(new PrefManager<String>(getApplicationContext()).get(card_number.substring(card_number.length() - 6),"0"));
		Log.e("pre", String.valueOf(count));
		if (count >=2){
			new AlertDialog.Builder(AttachCardActivity.this)
					.setTitle("Info")
					.setMessage("Please Contact Customer Care for further assistance in Attaching your Card.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
						}
					})

					.show();
		}else {
			RequestParams params = new RequestParams();
			JSONObject object = new JSONObject();

			try {
				Log.d("trycard ", "" + card);

				object.put("user_id", userid);
				object.put("card_nickname", cardname);
				object.put("card_number", card_number);
				object.put("card_expiry_date",valid_month+"/"+valid_year);
				//object.put("expmonth", valid_month);
				//object.put("expyear", valid_year);
				object.put("card_cvv", cvc);
				object.put("address_line1", address);
				object.put("address_line2", addr_line2);
				object.put("city_name", city_name);
				object.put("state", state);
				object.put("zip_code", zip_code);
				object.put("state_code", state_code);
				object.put("country", country);

				params.put("request", object.toString());
				Log.d("request", params.toString());
			}

			catch (JSONException e) {
				e.printStackTrace();
			}
			RestHttpClient.postParams("banking/addCard", params, new RegistrationHandler(), Login.token);
		}

	}

	class RegistrationHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
				dialog = ProgressDialog.show(AttachCardActivity.this,null, "Attaching card... ");
			
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject json = new JSONObject(result);

					boolean b = false;
					int response_code = json.getInt("code");
					Log.i("Response Code ", String.valueOf(response_code));
					if(1001 == response_code)
						b = true;
					final String msg = json.optString("message");

					if (b) {
						JSONObject dataJObject = json.getJSONObject("data");

						final int ref_id = dataJObject.getInt("ref_id");
						active_card_ref_id = ref_id;

						final int active_card_status = dataJObject.getInt("card_status");

						if(active_card_status == 1){
							final Dialog d=new Dialog(AttachCardActivity.this);
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
									if(SendMoney3.transfer_money){
										d.dismiss();
										SendMoney3.transfer_money = false;
										Intent i=new Intent(AttachCardActivity.this,SendMoney3.class);
										i.putExtra("iswhat", "sendmoney");
										startActivity(i);
										finish();
									}
									else{
										/*finish();
										startActivity(getIntent());*/
										d.dismiss();
										getSupportFragmentManager().popBackStack();
										finish();

//											FragmentManager fm = getSupportFragmentManager();
//											FragmentTransaction fragmentTransaction = fm.beginTransaction();
//											fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard(), "ManageCard");
//											fragmentTransaction.addToBackStack(null);
//											fragmentTransaction.commit();
//											overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

									}
								}
							});
							d.show();
						}
						else{
							showDebitDialog = true;
						/*JSONArray array = jsonObject.getJSONArray("data");
						JSONObject object = array.getJSONObject(0);

						Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("carddetails");
						Common_data.setPreference(AttachCardActivity.this,"carddetails_array",carddetails_array.toString());
						*/
							final Dialog d = new Dialog(AttachCardActivity.this);
							d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
							d.setContentView(R.layout.dialog3);
							d.setCancelable(false);
							d.show();

							final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv2);
							final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv2);

							final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img2);
							final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img2);
							final EditText random_amount_et = (EditText) d.findViewById(R.id.random_amount_et);

							//tv_reject.setVisibility(View.GONE);
							//iv_reject.setVisibility(View.GONE);


							tv_reject.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									d.dismiss();
									getCards();
								}
							});

							iv_reject.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									d.dismiss();
									getCards();
								}
							});



							verify_tv.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if(random_amount_et.getText().toString().equals("")){
										Toast.makeText(getApplicationContext(), "Enter the amount",Toast.LENGTH_SHORT).show();
									}
									else{
										d.dismiss();
										double amount = Double.parseDouble(random_amount_et.getText().toString());
										confirmDebitAmount(amount,ref_id);
									}

								}
							});
							verify_img.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									if(random_amount_et.getText().toString().equals("")){
										Toast.makeText(getApplicationContext(), "Enter the amount",Toast.LENGTH_SHORT).show();
									}
									else{
										d.dismiss();
										double amount = Double.parseDouble(random_amount_et.getText().toString());
										confirmDebitAmount(amount,ref_id);
									}
								}
							});
						}

					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(AttachCardActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText("Your session has expired. Please Login Again.");
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								Intent i = new Intent(AttachCardActivity.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(AttachCardActivity.this);
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
					}else if(1040 == response_code){
						final Dialog d=new Dialog(AttachCardActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_dialog);
						tv_success.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								finish();
								startActivity(getIntent());
							}
						});
						d.show();
					}
					else {
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(AttachCardActivity.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										registerOnServer();
										count++;
										//Common_data.setPreference(AttachCardActivity.this, card_number.substring(card_number.length() - 6), String.valueOf(count));
										new PrefManager<String>(getApplicationContext()).set(card_number.substring(card_number.length() - 6), String.valueOf(count));
									}
								})
								.setNegativeButton("Edit", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										edit = true;
										addressDetails();
										count++;
										//Common_data.setPreference(AttachCardActivity.this, card_number.substring(card_number.length() - 6), String.valueOf(count));
										new PrefManager<String>(getApplicationContext()).set(card_number.substring(card_number.length() - 6), String.valueOf(count));

										//finish();
										//startActivity(getIntent());
									}
								})
								.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										finish();
									}
								})
								.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			//Common_data.showAccountVerifyDialog(AttachCardActivity.this);
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.i("onFailure", content);
			Log.i("Error Message", error.toString());
			super.onFailure(statusCode, error, content);
			Toast.makeText(getApplicationContext(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(AttachCardActivity.this)
					.setTitle("Info")
					.setMessage("Network Error. Please retry!")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							registerOnServer();
						}
					})
					.setNegativeButton("Quit", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
							//startActivity(getIntent());
						}
					})
					.show();
		}
	}

	private void confirmDebitAmount(double amt, int ref_id) {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			Log.d("trycard ", "" + card);

			object.put("user_id", userid);
			object.put("ref_id", ref_id);
			object.put("amount", amt);

			params.put("request", object.toString());
			Log.d("request", params.toString());
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("banking/cardVerificationAmount", params, new AmountHandler(), Login.token);
	}

	class AmountHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			dialog = ProgressDialog.show(AttachCardActivity.this,null, "Verifying card... ");

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject json = new JSONObject(result);

					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;
					final String msg = json.optString("message");
					if (b) {
						//JSONArray array = json.getJSONArray("data");
						JSONObject object = json.getJSONObject("data");
						Log.d("data", object.toString());
						active_card_ref_id = 0;
						//Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("card");
						Log.d("card",carddetails_array.toString());
						//Common_data.setPreference(AttachCardActivity.this, "carddetails_array", carddetails_array.toString());
						new PrefManager<String>(getApplicationContext()).set("carddetails_array",carddetails_array.toString());

						final Dialog d=new Dialog(AttachCardActivity.this);
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
								if(SendMoney3.transfer_money){
									SendMoney3.transfer_money = false;
									Intent i=new Intent(AttachCardActivity.this,SendMoney3.class);
									i.putExtra("iswhat", "sendmoney");
									startActivity(i);
									finish();
								}
								else{
									finish();
									startActivity(getIntent());

									/*FragmentManager fm = getSupportFragmentManager();
									FragmentTransaction fragmentTransaction = fm.beginTransaction();
									fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard(), "ManageCard");
									fragmentTransaction.addToBackStack(null);
									fragmentTransaction.commit();
									overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
								}
							}
						});
						d.show();
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(AttachCardActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText("Your session has expired. Please Login Again.");
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								Intent i = new Intent(AttachCardActivity.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(AttachCardActivity.this);
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
					else {
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(AttachCardActivity.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										final int ref_id = active_card_ref_id;
										showDebitDialog = true;
						/*JSONArray array = jsonObject.getJSONArray("data");
						JSONObject object = array.getJSONObject(0);

						Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("carddetails");
						Common_data.setPreference(AttachCardActivity.this,"carddetails_array",carddetails_array.toString());
						*/
										final Dialog d = new Dialog(AttachCardActivity.this);
										d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
										d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
										d.setContentView(R.layout.dialog3);
										d.setCancelable(false);
										d.show();

										final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv2);
										final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv2);

										final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img2);
										final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img2);
										final EditText random_amount_et = (EditText) d.findViewById(R.id.random_amount_et);

										//tv_reject.setVisibility(View.GONE);
										//iv_reject.setVisibility(View.GONE);


										tv_reject.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View arg0) {
												d.dismiss();
												//getCards();
											}
										});

										iv_reject.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View arg0) {
												d.dismiss();
												//getCards();
											}
										});



										verify_tv.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												if(random_amount_et.getText().toString().equals("")){
													Toast.makeText(getApplicationContext(), "Enter the amount",Toast.LENGTH_SHORT).show();
												}
												else{
													d.dismiss();
													double amount = Double.parseDouble(random_amount_et.getText().toString());
													confirmDebitAmount(amount,ref_id);
												}

											}
										});
										verify_img.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View arg0) {
												if(random_amount_et.getText().toString().equals("")){
													Toast.makeText(getApplicationContext(), "Enter the amount",Toast.LENGTH_SHORT).show();
												}
												else{
													d.dismiss();
													double amount = Double.parseDouble(random_amount_et.getText().toString());
													confirmDebitAmount(amount,ref_id);
												}
											}
										});
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										startActivity(getIntent());
									}
								}).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			cardname_et.setText("");
			card_number_et.setText("");
			valid_month_et.setText("");
			valid_year_et.setText("");
			cvc_et.setText("");
			name_of_card_owner_et.setText("");
			addresspincode.setText("");
			addressline1.setText("");
			addressline2.setText("");
			address_city.setText("");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			//Common_data.showAccountVerifyDialog(AttachCardActivity.this);
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			super.onFailure(statusCode, error, content);
			Toast.makeText(getApplicationContext(), "Network Error. Please retry!", Toast.LENGTH_SHORT).show();
		}
	}

	private void getCards() {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {

			object.put("user_id", userid);

			params.put("request", object.toString());
			Log.d("request", params.toString());
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("banking/getCards", params, new GetCardsHandler(), Login.token);
	}

	class GetCardsHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			dialog = ProgressDialog.show(AttachCardActivity.this,null, "Retrieving cards... ");

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject json = new JSONObject(result);

					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;
					final String msg = json.optString("message");
					if (b) {
						//JSONArray array = json.getJSONArray("data");
						JSONObject object = json.getJSONObject("data");
						Log.d("data", object.toString());
						active_card_ref_id = 0;
						//Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("card");
						Log.d("card",carddetails_array.toString());
						//Common_data.setPreference(AttachCardActivity.this, "carddetails_array", carddetails_array.toString());
						new PrefManager<String>(getApplicationContext()).set("carddetails_array",carddetails_array.toString());

						finish();
						startActivity(getIntent());
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(AttachCardActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText("Your session has expired. Please Login Again.");
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								Intent i = new Intent(AttachCardActivity.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(AttachCardActivity.this);
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
					else {
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(AttachCardActivity.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										getCards();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										startActivity(getIntent());
									}
								}).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			cardname_et.setText("");
			card_number_et.setText("");
			valid_month_et.setText("");
			valid_year_et.setText("");
			cvc_et.setText("");
			name_of_card_owner_et.setText("");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			//Common_data.showAccountVerifyDialog(AttachCardActivity.this);
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.i("onFailure", content);
			super.onFailure(statusCode, error, content);
			Toast.makeText(getApplicationContext(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
		}
	}

	private boolean validation() {
		String REQUIRED_MSG = "Please enter the valid card number";
		String REQUIRED_MSG1 = "Please enter the valid card name";
		String REQUIRED_MSG2 = "Please enter the valid expiry month";
		String REQUIRED_MSG3 = "Please enter the valid expiry year";
		String Emaildegi = "Please enter the name of card owner ";

		boolean flag = false;

		/*if (cardname.length() == 0) {

			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG1, Toast.LENGTH_SHORT).show();
		} else*/ if (card_number.length() != 16) {
			
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		} else if (valid_month.length() < 2) {
			
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG2, Toast.LENGTH_SHORT).show();
		}

		else if (valid_year.length() < 2) {
			
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG3, Toast.LENGTH_SHORT).show();
		}
		else if(cvc_et.getText().toString().length() < 3){
			
			Toast.makeText(AttachCardActivity.this, "Please Enter Card CVV", Toast.LENGTH_SHORT).show();
		}
		/*else if (name_of_owner_card.length() == 0) {
			Toast.makeText(AttachCardActivity.this, Emaildegi, Toast.LENGTH_SHORT).show();
		}
		
		else if(addressline1.getText().toString().trim().length()==0){
			Toast.makeText(AttachCardActivity.this, "Please Enter Address", Toast.LENGTH_SHORT).show();
		}
		else if(addresspincode.getText().toString().length()==0){
			Toast.makeText(AttachCardActivity.this, "Please Enter Pincode", Toast.LENGTH_SHORT).show();
		}*/
		
		else {
			flag = true;
		}
		return flag;
	}

	private boolean validation2() {
		boolean flag = false;
		String REQUIRED_MSG2 = "Please enter the valid expiry month";
		String REQUIRED_MSG = "Please enter the valid expiry year";
		String REQUIRED_MSG3 = "Please enter the valid Card Number";

		valid_month_int = Integer.parseInt(valid_month);

		valid_year_int = Integer.parseInt(valid_year);

		if (valid_year_int == current_year) {
			if (valid_month_int < current_month) {
				Toast.makeText(AttachCardActivity.this, REQUIRED_MSG2, Toast.LENGTH_SHORT).show();
			}else{
				flag = true;
			}
		}

		else if (valid_month_int > 12) {
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG2, Toast.LENGTH_SHORT).show();
		}

		else if (valid_year_int < current_year) {
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		}

		else if (card_number.length() != 16 ) {
			Toast.makeText(AttachCardActivity.this, REQUIRED_MSG3, Toast.LENGTH_SHORT).show();
		} else {
			flag = true;
		}
		return flag;
	}

	private boolean validation3() {
		boolean flag = false;
		 if (address.length() == 0) {

			Toast.makeText(AttachCardActivity.this, "Please Enter Address Line 1", Toast.LENGTH_SHORT).show();
		}
		else if (zip_code.length() == 0) {

			Toast.makeText(AttachCardActivity.this, "Please Enter Zip Code or Postal Code", Toast.LENGTH_SHORT).show();
		}
		else if (city_name.length() == 0) {

			Toast.makeText(AttachCardActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
		}
		else if (state_code.equals("Select State")) {

			Toast.makeText(AttachCardActivity.this, "Please select your State", Toast.LENGTH_SHORT).show();
		}else if(state_code.equals("Select Province")){
			 Toast.makeText(AttachCardActivity.this, "Please select your Province", Toast.LENGTH_SHORT).show();
		}else {
			 flag = true;
		 }

		return flag;
	}

	public static String getCardVendor(String cardNumber) {
		Log.d("cardNumber in mad", "" + cardNumber);

		if (cardNumber.startsWith("4")) {
			Log.d("Visa", "Visa International");
			card = "Visa International";
			return "Visa International";
		}

		else if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
			card = "Amex Card";
			return "Amex Card";
		}

		else if (cardNumber.startsWith("51") || cardNumber.startsWith("52")
				|| cardNumber.startsWith("53") || cardNumber.startsWith("54")
				|| cardNumber.startsWith("55")) {
			card = "Master Card";
			return "Master Card";
		} else if (cardNumber.startsWith("35")) {
			card = "Japan Credit Bureau Card";
			return "Japan Credit Bureau Card";
		} else if (cardNumber.startsWith("60") || cardNumber.startsWith("62")
				|| cardNumber.startsWith("64") || cardNumber.startsWith("65")) {
			card = "Discover Credit Card";
			return "Discover Credit Card";
		} else if (cardNumber.startsWith("30") || cardNumber.startsWith("36")
				|| cardNumber.startsWith("38") || cardNumber.startsWith("39")) {
			card = "Dinner club International";
			return "Dinner club International";
		} else {
			card = "blank";
		}
		return cardNumber;
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
						if (edit){
							//statearr1[0] = Common_data.getPreferences(AttachCardActivity.this, "state");
							statearr1[0] = new PrefManager<String>(getApplicationContext()).get("state","");
						}


						for (int i = 0; i < state_data.length(); i++) {
							JSONObject state_data_object = state_data.getJSONObject(i);
							statearr[i+1] = state_data_object.getString("state_province");
							statearr1[i+1] = state_data_object.getString("state_province_a");
							if (edit){
								if (statearr1[i+1].equals(statearr1[0])){
									statearr[0] = statearr[i+1];
								}
							}
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AttachCardActivity.this, R.layout.custom_spinner_right_text, statearr);
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
						final Dialog d=new Dialog(AttachCardActivity.this);
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
						final Dialog d=new Dialog(AttachCardActivity.this);
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
			//Toast.makeText(getBaseContext(), errorResponse,Toast.LENGTH_SHORT).show();
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

	private void networkErrorDialog() {

		final Dialog d=new Dialog(AttachCardActivity.this);
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setCancelable(false);
		d.setContentView(R.layout.network_error);

		Button tryagain=(Button) d.findViewById(R.id.try_again);
		tryagain.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(AttachCardActivity.this, AttachCardActivity.class);
				startActivity(i);
				finish();
			}
		});
		d.show();
	}


}
