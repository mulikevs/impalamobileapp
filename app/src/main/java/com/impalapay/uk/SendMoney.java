package com.impalapay.uk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.uk.adapters.MainPageTranscationAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SendMoney extends FragmentActivity implements OnClickListener {

	RadioButton radio_bank, radio_ewallet;
	RadioButton radio_card;
	LinearLayout select_card_ly, select_bank_ly,current_bal_ly,new_card_ly;
	Button next;
	TextView select_card_tv,apply_coupon_tv,current_bal_tv;
	Spinner selct_sp,card_sp;
	EditText coupen_code_et,remarks,reference,et_card_number,et_card_cvv,et_card_expiry_date;
	ImageView cvv0;
	TextView fraud_awareness;
	String  userid;
	ProgressDialog dialog;
	ArrayAdapter<String> dataAdapter;

	public String card_numbers[];
	public static JSONArray card_json_array = new JSONArray();
	public static JSONArray active_card_json_array = new JSONArray();
	public static JSONObject card_json_object = new JSONObject();
	public Map<Integer, String> selected_card = new HashMap<>();
	public static boolean saved_cards = true;
	public static String masked_card_number;
	public static int card_ref;
	public static boolean transfer_money = false;
	
	ArrayList<String> cardname_al = new ArrayList<String>();
	ArrayList<String> cardnumber_al = new ArrayList<String>();
	
	ArrayList<String> bankname_al = new ArrayList<String>();
	ArrayList<Integer> bankid_al = new ArrayList<Integer>();
	ArrayList<String> banknumber_al = new ArrayList<String>();
	
	private String pData;
	private static double apply_per_value=0;
	ArrayAdapter<String> cardAdapter;
	private TextView back_tv;
	private TextView next_tv;
	private String switchStatus;
	
	
	//static 
	static String pay_type="ewallet";
	static int pos;
	static String remarks_st="",reference_st="", card_number_st = "", card_cvv_st = "", card_expiry_date_st = "";
	static int coupen_per;
	static String what;

	private static final char card_number_space = ' ';
	private int active_cards_size = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.send_money3);
		
		init();


		//radio_card.setEnabled(false);
		Common_data.setupUI(findViewById(R.id.hide), SendMoney.this);
		
		ApplyOnClick();

		//getApp().touch();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			Log.d("onStart", "yes");
			getClientCards();
			getClientData();
			
		//String sd=	Common_data.getPreferences(getApplicationContext(), "from");
		String sd = new PrefManager<String>(getApplicationContext()).get("from","");
		/*if(sd.equals("link")){
			current_bal_ly.setVisibility(View.GONE);
			pay_type="card";
		
			select_card_tv.setText("Select Account");
			select_card_ly.setVisibility(View.VISIBLE);
			
			cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, bankname_al);
			cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			selct_sp.setAdapter(cardAdapter);
		}else{
			if(cardname_al.size()>0){
				current_bal_ly.setVisibility(View.GONE);
				pay_type="card";
				radio_card.setChecked(true);
				
				select_card_tv.setText("Select Card");
				select_card_ly.setVisibility(View.VISIBLE);
				
				cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, cardname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				selct_sp.setAdapter(cardAdapter);
			}
			else if(bankname_al.size()>0){
				current_bal_ly.setVisibility(View.GONE);
				pay_type="bank";
			
				select_card_tv.setText("Select Account");
				select_card_ly.setVisibility(View.VISIBLE);
				
				cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, bankname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				selct_sp.setAdapter(cardAdapter);
			}
			else{
				radio_ewallet.setChecked(true);
				
				pay_type="ewallet";
				select_card_ly.setVisibility(View.GONE);
				current_bal_ly.setVisibility(View.VISIBLE);
			}
				
		} */
		
			what=getIntent().getExtras().getString("iswhat");
			
			
			if(what.equals(""))
				//what= Common_data.getPreferences(getApplicationContext(), "iswhat");
				what = new PrefManager<String>(getApplicationContext()).get("iswhat","");
			//Common_data.setPreference(getApplicationContext(), "from", "");
			new PrefManager<String>(getApplicationContext()).set("from","");
		
			if (SendMoney1_Frag.type != null) {
				if (SendMoney1_Frag.type.equalsIgnoreCase("paybill") || SendMoney1_Frag.type.equalsIgnoreCase("mpesa")){
					//reference.setVisibility(View.GONE);
			    }
				else if (what.equalsIgnoreCase("sendmoney")&& SendMoney1_Frag.type.equalsIgnoreCase("ewallet")) {
					//reference.setVisibility(View.GONE);
					//remarks.setVisibility(View.GONE);
				} 
				else if (what.equalsIgnoreCase("main")) {

						if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("paybill")|| MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("mpesa"))
							reference.setVisibility(View.GONE);
						else if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("ewallet")) {
							reference.setVisibility(View.GONE);
							remarks.setVisibility(View.GONE);
						}
						Log.d("model data ",MainPageTranscationAdapter.clonemodel.getDel_method());
					}
			
			} else{
				if (what.equalsIgnoreCase("main")) {

						if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("paybill")|| MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("mpesa"))
							reference.setVisibility(View.GONE);
						else if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("ewallet")) {
							reference.setVisibility(View.GONE);
							remarks.setVisibility(View.GONE);
						}
						Log.d("model data ",MainPageTranscationAdapter.clonemodel.getDel_method());
					}
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



	private void init() {
		pay_type="card";
		back_tv=(TextView) findViewById(R.id.back_tv);
		next_tv=(TextView) findViewById(R.id.done_tv);
		next_tv.setText("Next");
		//fraud_awareness=(TextView)findViewById(R.id.fraud_awareness);
		
		radio_card = (RadioButton) findViewById(R.id.radio_card);
		radio_card.setChecked(true);
		radio_bank = (RadioButton) findViewById(R.id.radio_bank);
		radio_ewallet = (RadioButton) findViewById(R.id.radio_ewallet);

		remarks=(EditText) findViewById(R.id.remarks);
		reference=(EditText) findViewById(R.id.reference);
		
		select_card_ly = (LinearLayout) findViewById(R.id.card_ly);
		select_bank_ly = (LinearLayout) findViewById(R.id.bank_ly);
		new_card_ly = (LinearLayout) findViewById(R.id.new_card_ly);

		LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
		back_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(	what.equals("airtime")){

					//Common_data.setPreference(getApplicationContext(), "alertshow", "false");
					new PrefManager<String>(getApplicationContext()).set("alertshow","false");
					finish();

				}else if(what.equals("paybill")){
					Intent ii=new Intent(SendMoney.this,SendMoney_PayBill.class);
					startActivity(ii);
					finish();
				}
				else if(what.equals("main")){
					finish();
				}else{
					Intent ii=new Intent(SendMoney.this,SendMoney2.class);
					startActivity(ii);
					finish();
				}
			}
		});


		card_sp = (Spinner) findViewById(R.id.card_sp);

		next = (Button) findViewById(R.id.next3);

		select_card_tv = (TextView) findViewById(R.id.change_title_tv);
		
		apply_coupon_tv=(TextView) findViewById(R.id.apply_coupon_tv);
		
		selct_sp = (Spinner) findViewById(R.id.select_bank_sp);

		current_bal_ly=(LinearLayout) findViewById(R.id.current_bal_ly);
		current_bal_tv=(TextView) findViewById(R.id.user_bal_tv);
		
		coupen_code_et=(EditText) findViewById(R.id.coupen_code_et);

		et_card_number=(EditText) findViewById(R.id.et_card_number);
		et_card_cvv=(EditText) findViewById(R.id.et_card_cvv);
		et_card_expiry_date=(EditText) findViewById(R.id.et_card_expiry_date);
		cvv0 = (ImageView) findViewById(R.id.cvv0);
		cvv0.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				final Dialog d = new Dialog(SendMoney.this);
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


		et_card_number.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				// Remove spacing char
				/*if (s.length() > 0 && (s.length() % 5) == 0) {
					final char c = s.charAt(s.length() - 1);
					if (card_number_space == c) {
						s.delete(s.length() - 1, s.length());
					}
				}
				// Insert char where needed.
				if (s.length() > 0 && (s.length() % 5) == 0) {
					char c = s.charAt(s.length() - 1);
					// Only if its a digit where there should be a space we insert a space
					if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(card_number_space)).length <= 3) {
						s.insert(s.length() - 1, String.valueOf(card_number_space));
					}
				}*/
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});



		et_card_expiry_date.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				String mLastInput = "";
				// /tv.setText(txtMessage.getText().toString().length());
				if(et_card_expiry_date.getText().toString().length() == 2){
					String month = et_card_expiry_date.getText().toString();
					if(!month.contains("/")){
						et_card_expiry_date.setText(month+"/");
						et_card_expiry_date.setSelection(et_card_expiry_date.getText().length());
					}
				}

				/*String input = s.toString();
				SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.GERMANY);
				Calendar expiryDateDate = Calendar.getInstance();
				try {
					expiryDateDate.setTime(formatter.parse(input));
				}catch (ParseException e) {
					if (s.length() == 2 && !mLastInput.endsWith("/")) {
						int month = Integer.parseInt(input);
						if (month <= 12) {
							et_card_expiry_date.setText(et_card_expiry_date.getText().toString() + "/");
							et_card_expiry_date.setSelection(et_card_expiry_date.getText().toString().length());
						}
					} else if (s.length() == 2 && mLastInput.endsWith("/")) {
						int month = Integer.parseInt(input);
						if (month <= 12) {
							et_card_expiry_date.setText(et_card_expiry_date.getText().toString().substring(0, 1));
							et_card_expiry_date.setSelection(et_card_expiry_date.getText().toString().length());
						} else {
							et_card_expiry_date.setText("");
							et_card_expiry_date.setSelection(et_card_expiry_date.getText().toString().length());
							Toast.makeText(getApplicationContext(), "Enter a valid month", Toast.LENGTH_LONG).show();
						}
					} else if (s.length() == 1) {
						int month = Integer.parseInt(input);
						if (month > 1) {
							et_card_expiry_date.setText("0" + et_card_expiry_date.getText().toString() + "/");
							et_card_expiry_date.setSelection(et_card_expiry_date.getText().toString().length());
						}
					} else {

					}
					mLastInput = et_card_expiry_date.getText().toString();
					return;
				}*/
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});


		//String bal= Common_data.getPreferences(getApplicationContext(), "balance");
		String bal = new PrefManager<String>(getApplicationContext()).get("balance","");
		current_bal_tv.setText(Sawapay_Main_Screen.base+" "+bal);
		coupen_per=0;


		
		
	}



	
	private void ApplyOnClick() {
		radio_card.setOnClickListener(this);
		radio_bank.setOnClickListener(this);
		radio_ewallet.setOnClickListener(this);
		apply_coupon_tv.setOnClickListener(this);
		next.setOnClickListener(this);
		back_tv.setOnClickListener(this);
		next_tv.setOnClickListener(this);
		selct_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				pos = arg2;
			}

			;

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.radio_bank:
			pay_type="bank";
			current_bal_ly.setVisibility(View.GONE);
			select_card_ly.setVisibility(View.GONE);
			select_bank_ly.setVisibility(View.VISIBLE);
			getClientBanks();
			if(bankname_al.size()>0)
			{
				select_card_tv.setText("Select Account");
				select_bank_ly.setVisibility(View.VISIBLE);
				
				cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, bankname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				selct_sp.setAdapter(cardAdapter);
				
			}
			else{
				select_bank_ly.setVisibility(View.GONE);
				
				AlertDialog.Builder builder=new AlertDialog.Builder(SendMoney.this);
				builder.setMessage("Add Bank Details")
				.setCancelable(false)
				.setPositiveButton("Add Bank",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent i=new Intent(SendMoney.this,AddBankAccountActivity.class);
						i.putExtra("iswhat", what);
						startActivity(i);
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						dialog.cancel();
						//Intent i=new Intent(SendMoney.this,MainActivity.class);
						//startActivity(i);
					}
				});

				AlertDialog alertDialog = builder.create();
				alertDialog.show();
				
				Toast.makeText(SendMoney.this, "No Attached Bank Details Found", Toast.LENGTH_SHORT).show();
			}
			
			break;
		case R.id.radio_card:
			pay_type="card";
			current_bal_ly.setVisibility(View.GONE);
			select_card_ly.setVisibility(View.VISIBLE);
			select_bank_ly.setVisibility(View.GONE);

			getClientCards();
			/*if (cardname_al.size() > 0) { */
				
				//select_card_tv.setText("Select Card");
				//select_card_ly.setVisibility(View.VISIBLE);
				
				/*cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, cardname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				selct_sp.setAdapter(cardAdapter);*/
			/*}
			else{
				select_card_ly.setVisibility(View.GONE);
				
				Toast.makeText(SendMoney.this, "No Attached Card Found", Toast.LENGTH_SHORT).show();
				
				AlertDialog.Builder builder=new AlertDialog.Builder(SendMoney.this);
				
				builder
				.setMessage("Add Card Details")
				.setCancelable(false)
				.setPositiveButton("Add Card",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent i=new Intent(SendMoney.this,AttachCardActivity.class);
						i.putExtra("iswhat", what);
						startActivity(i);
					}
				  })
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						
						dialog.cancel();
						//Intent i=new Intent(SendMoney.this,MainActivity.class);
						//startActivity(i);
					}
				});

				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			} */

//			if((active_cards_size <= 1)){
//				Toast.makeText(SendMoney.this, "No Attached Card Found!", Toast.LENGTH_SHORT).show();
//
//				AlertDialog.Builder builder=new AlertDialog.Builder(SendMoney.this);
//
//				builder
//						.setMessage("Add Card Details")
//						.setCancelable(false)
//						.setPositiveButton("Add Card",new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,int id) {
//								transfer_money = true;
//								Intent i=new Intent(SendMoney.this,AttachCardActivity.class);
//								i.putExtra("iswhat", what);
//								startActivity(i);
//							}
//						})
//						.setNegativeButton("No",new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,int id) {
//
//								dialog.cancel();
//								//finish();
//								//startActivity(getIntent());
//							}
//						});
//
//				AlertDialog alertDialog = builder.create();
//				alertDialog.show();
//			}
			
			break;
		case R.id.radio_ewallet:
			pay_type="ewallet";
			select_card_ly.setVisibility(View.GONE);
			select_bank_ly.setVisibility(View.GONE);
			current_bal_ly.setVisibility(View.VISIBLE);
			break;

		case R.id.next3:
			if(validation()){
				if(validation1())
				{
					remarks_st=remarks.getText().toString();
					reference_st=reference.getText().toString();
					//if(Common_data.getPreferences(getApplicationContext(),"base").equalsIgnoreCase("CAD")){
					if(new PrefManager<String>(getApplicationContext()).get("base","").equalsIgnoreCase("CAD")) {
						Intent i = new Intent(SendMoney.this, SendMoney4.class);
						startActivity(i);
						finish();
						//}else if(Common_data.getPreferences(getApplicationContext(),"base").equalsIgnoreCase("USD") && Common_data.getPreferences(getApplicationContext(),"show_fraud_message").equalsIgnoreCase("true")){

					}else if (new PrefManager<String>(getApplicationContext()).get("base","").equalsIgnoreCase("USD")&&new PrefManager<String>(getApplicationContext()).get("show_fraud_message","").equalsIgnoreCase("true")){
						Intent i=new Intent(SendMoney.this,SendMoney4.class);
						startActivity(i);
						finish();

					}else{
						final	Dialog d=new Dialog(SendMoney.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.fraud_awareness);
						TextView btn_ok = (TextView) d.findViewById(R.id.proceed);
						TextView number = (TextView) d.findViewById(R.id.countryNo);
						final CheckBox show_message=(CheckBox)d.findViewById(R.id.show_message);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								if(show_message.isChecked()){
									//Common_data.setPreference(getApplicationContext(),"show_fraud_message","true");
									new PrefManager<String>(getApplicationContext()).set("show_fraud_message","true");
								}else{
									//Common_data.setPreference(getApplicationContext(),"show_fraud_message","false");
									new PrefManager<String>(getApplicationContext()).set("show_fraud_message","false");

								}
								d.dismiss();
								Intent i=new Intent(SendMoney.this,SendMoney4.class);
								startActivity(i);
								finish();
							}
						});
						number.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								startActivity(new Intent(Intent.ACTION_CALL,
										Uri.parse("tel:"+getString(R.string.contact_phone))));


								//Has to be reqritten to check for permission first.
							}
						});
						d.show();

					}
					

				}
			}
			break;
		case R.id.apply_coupon_tv:
			if(coupen_code_et.getText().toString().length()==0){
				Toast.makeText(getApplicationContext(), "Enter Coupon Code", Toast.LENGTH_SHORT).show();
			}
			else
			{
				applyCoupon();
			}
			break;
			
		case R.id.done_tv:
			if(validation()){
				if(validation1()){
					remarks_st=remarks.getText().toString();
					reference_st=reference.getText().toString();
					
					
					Intent i=new Intent(SendMoney.this,SendMoney4.class);
					startActivity(i);
					finish();
				}
			}
			break;
			
			
		case R.id.back_tv:
			if(	what.equals("airtime")){
			
				Common_data.setPreference(getApplicationContext(), "alertshow", "false");
				finish();
				
			}else if(what.equals("paybill")){
				Intent ii=new Intent(SendMoney.this,SendMoney_PayBill.class);
				startActivity(ii);
				finish();
			}
			else if(what.equals("main")){
				finish();
			}else{
				Intent ii=new Intent(SendMoney.this,SendMoney2.class);
				startActivity(ii);
				finish();
			}
			
			break;
		default:
			break;
		}
	}
	
	public boolean validation1(){
		boolean flag=false;

		try{
						
			int w=0;

			if(reference.getVisibility()==View.VISIBLE){

				if(reference.getText().toString().trim().length()==0){
					if(w==0)
						Toast.makeText(getApplicationContext(), "Enter Reference", Toast.LENGTH_SHORT).show();
					w=0;
					flag=false;
					return flag;
				}
				else{
					flag=true;
				}
			}

			if(remarks.getVisibility()==View.VISIBLE){
				if(remarks.getText().toString().trim().length()==0){
					Toast.makeText(getApplicationContext(), "Enter Remarks", Toast.LENGTH_SHORT).show();
					w=1;
					flag=false;
					return flag;
				}
				else{
					flag=true;
				}
			}

			if(remarks.getVisibility()==View.GONE && reference.getVisibility()==View.GONE)
			{
				flag=true;
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	
	private boolean validation() {
		
		
		if(radio_card.isChecked()){
			card_number_st = et_card_number.getText().toString().trim().replace(" ", "");
			card_cvv_st = et_card_cvv.getText().toString().trim();
			card_expiry_date_st = et_card_expiry_date.getText().toString().trim();

			if(saved_cards){
				String masked_card_number = card_sp.getSelectedItem().toString();
				if(masked_card_number.equals("Select Card")){
					Toast.makeText(getApplicationContext(), "Please select card", Toast.LENGTH_SHORT).show();
					return false;
				}
				try {
					card_json_object = active_card_json_array.getJSONObject(card_sp.getSelectedItemPosition()-1);
				} catch (JSONException e) {
					e.printStackTrace();
					finish();
					startActivity(getIntent());
				}
			}
			else{
				if(card_number_st.length() < 16){
					Toast.makeText(getApplicationContext(), "Please Enter Valid Card Number", Toast.LENGTH_SHORT).show();
					return false;
				}

				if(card_cvv_st.length() < 3){
					Toast.makeText(getApplicationContext(), "Please Enter Valid Card CVV", Toast.LENGTH_SHORT).show();
					return false;
				}

			/*if(card_expiry_date_st.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")){
				Toast.makeText(getApplicationContext(), "Please Enter Valid Card Expiry Date.", Toast.LENGTH_SHORT).show();
				return false;
			} */

				if(card_expiry_date_st.length() < 5){
					Toast.makeText(getApplicationContext(), "Please Enter Valid Card Expiry Date. Use / to separate month and year.", Toast.LENGTH_SHORT).show();
					return false;
				}

				if(! card_expiry_date_st.contains("/")){
					Toast.makeText(getApplicationContext(), "Kindly Enter Valid Card Expiry Date. Use / to separate month and year.", Toast.LENGTH_SHORT).show();
					return false;
				}
			}

			return true;
		}
		else if(radio_bank.isChecked()){
			
			if(bankname_al.size()==0){
				Toast.makeText(getApplicationContext(), "Please Attach Bank Account", Toast.LENGTH_SHORT).show();
				return false;
			}
			return true;
		}
		else if(radio_ewallet.isChecked()){
			
			if(current_bal_tv.getText().toString().equals("0")){
				Toast.makeText(getApplicationContext(), "No Sufficent Balance in your eWallet", Toast.LENGTH_SHORT).show();
				return false;		
			}
			return true;
			
		}
	
		return true;
	}
	
	
	
	private void getClientData() {
		pData = Common_data.getPreferences(SendMoney.this, "data");
		Log.d("getClientData(); data=>", pData);
		cardname_al.clear();
		bankname_al.clear();
		bankid_al.clear();
		cardnumber_al.clear();
		banknumber_al.clear();
		
		try {
			JSONObject object = new JSONObject(pData);
			JSONObject jsonObject = object.getJSONObject("data");
			//JSONObject object = array.getJSONObject(0);

			//JSONArray carddetails_array = jsonObject.getJSONArray("card");
			JSONArray carddetails_array = new JSONArray(Common_data.getPreferences(SendMoney.this, "carddetails_array"));
			Log.d("getClientData(); card=>", carddetails_array.toString());
			card_json_array = carddetails_array;

			active_cards_size = 1;
			if (carddetails_array.length() > 0) {
				for (int i = 0; i < carddetails_array.length(); i++) {
					JSONObject carddetails_object = carddetails_array
							.getJSONObject(i);

					//String cardna = carddetails_object.getString("card_name");
					//cardname_al.add(cardna);
					//cardnumber_al.add(carddetails_object.getString("card_number"));

					if(carddetails_object.getString("card_status").equals("1")){
						active_cards_size++;
						active_card_json_array.put(carddetails_array.getJSONObject(i));
					}
					Log.d("card",carddetails_object.getString("card_status"));
				}
			}

			card_numbers = new String[active_cards_size];
			card_numbers[0] = "Select Card";

			if (carddetails_array.length() > 0) {
				saved_cards = true;
				//card_sp.setVisibility(View.VISIBLE);
				//new_card_ly.setVisibility(View.GONE);
				int j = 1;
				for (int i = 0; i < carddetails_array.length(); i++) {
					JSONObject carddetails_object = carddetails_array
							.getJSONObject(i);

					//String cardna = carddetails_object.getString("card_name");
					//cardname_al.add(cardna);
					//cardnumber_al.add(carddetails_object.getString("card_number"));

					if(carddetails_object.getString("card_status").equals("1")){
						card_numbers[j] = carddetails_object.getString("masked_card_number")+" ("
								+carddetails_object.getString("card_nickname")+")";
						Log.d("card",card_numbers[j]);
						j++;
					}
				}
			}

			Log.d("Size card array", carddetails_array.length()+" ");


			String[] test = {"2222222","444444"};
			Log.d("test[0]", test[0]+" ");
			 dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text, card_numbers);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// attaching data adapter to spinner
			card_sp.setAdapter(dataAdapter);
			card_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
										   int position, long arg3) {
					// TODO Auto-generated method stub

					String val = (String) card_sp.getItemAtPosition(position);
					JSONArray carddetails_array1 = null;
					try{
						carddetails_array1 = new JSONArray(Common_data.getPreferences(SendMoney.this, "carddetails_array"));
					}
					catch (JSONException e){
						e.printStackTrace();
					}

					Log.d("card_sp", val);

					if ( 0 == position && active_cards_size < 2) {
						Toast.makeText(SendMoney.this, "No Attached Card Found...", Toast.LENGTH_SHORT).show();

						AlertDialog.Builder builder=new AlertDialog.Builder(SendMoney.this);

						builder
								.setMessage("Add Card Details")
								.setCancelable(false)
								.setPositiveButton("Add Card",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										transfer_money = true;
										Intent i=new Intent(SendMoney.this,AttachCardActivity.class);
										i.putExtra("iswhat", what);
										startActivity(i);
									}
								})
								.setNegativeButton("No",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {

										dialog.cancel();
										//finish();
										//startActivity(getIntent());
									}
								});

						AlertDialog alertDialog = builder.create();
						alertDialog.show();
					} else {
						Toast.makeText(SendMoney.this, "" + val, Toast.LENGTH_SHORT).show();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			JSONArray bankDetails_array= new JSONArray(Common_data.getPreferences(SendMoney.this, "bankdetails_array")); //jsonObject.optJSONArray("bank");
			
			
			if(bankDetails_array.length()>0){
				for(int i=0;i<bankDetails_array.length();i++){
					
					JSONObject bankobj=bankDetails_array.optJSONObject(i);
					
					bankname_al.add(bankobj.getString("bank_name"));
					bankid_al.add(bankobj.getInt("id"));
					banknumber_al.add(bankobj.getString("account_number"));
				}
			}
			Log.d("Size array", bankname_al.size() + " ");
			//System.exit(1);
			//callspinner();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void getClientCards() {
		userid = Common_data.getPreferences(SendMoney.this, "userid");
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
			dialog = ProgressDialog.show(SendMoney.this,null, "Retrieving cards... ");

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
						//Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray carddetails_array = object.getJSONArray("card");
						Log.d("card",carddetails_array.toString());
						card_json_array = carddetails_array;

						active_cards_size = 1;
						if (carddetails_array.length() > 0) {
							for (int i = 0; i < carddetails_array.length(); i++) {
								JSONObject carddetails_object = carddetails_array
										.getJSONObject(i);

								//String cardna = carddetails_object.getString("card_name");
								//cardname_al.add(cardna);
								//cardnumber_al.add(carddetails_object.getString("card_number"));

								if(carddetails_object.getString("card_status").equals("1")){
									active_cards_size++;
									active_card_json_array.put(carddetails_array.getJSONObject(i));
								}
								Log.d("card",carddetails_object.getString("card_status"));
							}
						}
						card_numbers = new String[active_cards_size];
						card_numbers[0] = "Select Card";

						if (carddetails_array.length() > 0) {
							saved_cards = true;
							//card_sp.setVisibility(View.VISIBLE);
							//new_card_ly.setVisibility(View.GONE);
							int j = 1;
							for (int i = 0; i < carddetails_array.length(); i++) {
								JSONObject carddetails_object = carddetails_array
										.getJSONObject(i);

								//String cardna = carddetails_object.getString("card_name");
								//cardname_al.add(cardna);
								//cardnumber_al.add(carddetails_object.getString("card_number"));

								if(carddetails_object.getString("card_status").equals("1")){
									card_numbers[j] = carddetails_object.getString("masked_card_number")+" ("
											+carddetails_object.getString("card_nickname")+")";
									Log.d("card",card_numbers[j]);
									j++;
								}
							}
						}

						Log.d("Size card array", carddetails_array.length()+" ");

						String[] test = {"2222222","444444"};
						Log.d("test[0]", test[0]+" ");
						 dataAdapter = new ArrayAdapter<String>(SendMoney.this, R.layout.custom_spinner_right_text, card_numbers);
						dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						// attaching data adapter to spinner
						card_sp.setAdapter(dataAdapter);

						finish();
						startActivity(getIntent());
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(SendMoney.this);
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
								Intent i = new Intent(SendMoney.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(SendMoney.this);
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
						new AlertDialog.Builder(SendMoney.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										getClientCards();
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

	private void callspinner() {
		/*if (cardname_al.size() > 0) {
			cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, cardname_al);
			cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			selct_sp.setAdapter(cardAdapter);
		}
		else{
			select_card_ly.setVisibility(View.GONE);
		}*/
	}
	
	/*@Override
	public void onBackPressed() {
		
	}*/

	private void getClientBanks() {
		userid = Common_data.getPreferences(SendMoney.this, "userid");
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
		RestHttpClient.postParams("banking/getClientBanks", params, new GetBanksHandler(), Login.token);

	}

	class GetBanksHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			ProgressDialog pd;
			pd = ProgressDialog.show(SendMoney.this,null, "Retrieving banks... ");

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
						//Common_data.setPreference(AttachCardActivity.this, "data", result);
						JSONArray banksdetails_array = object.getJSONArray("card");
						Log.d("bank",banksdetails_array.toString());
						if(banksdetails_array.length()>0){
							for(int i=0;i<banksdetails_array.length();i++){

								JSONObject bankobj=banksdetails_array.optJSONObject(i);

								bankname_al.add(bankobj.getString("bank_name"));
								bankid_al.add(bankobj.getInt("id"));
								banknumber_al.add(bankobj.getString("account_number"));
							}
						}
						Log.d("Size array", bankname_al.size() + " ");
						//System.exit(1);
						cardAdapter = new ArrayAdapter<String>(SendMoney.this,R.layout.custom_spinner_text, bankname_al);
						cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
						selct_sp.setAdapter(cardAdapter);

						finish();
						startActivity(getIntent());
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(SendMoney.this);
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
								Intent i = new Intent(SendMoney.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(SendMoney.this);
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
						new AlertDialog.Builder(SendMoney.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										getClientCards();
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

	
	
private void applyCoupon() {

		if(card_number_st.equals("")){
			Toast.makeText(getApplicationContext(), "Coupon promotion is coming soon.", Toast.LENGTH_SHORT).show();
		}
		/*RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try {
			object.put("userid", Common_data.getPreferences(getApplicationContext(), "userid"));
			object.put("couponcode", coupen_code_et.getText().toString());
			
			params.put("request", object.toString());
			
			Log.d("deals_detail", params.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RestHttpClient.postParams("applyCoupon", params, new ApplyCouponHandler()); */
	}
	
	class ApplyCouponHandler extends AsyncHttpResponseHandler{
		
		ProgressDialog dialog;
		
		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);
			
			Log.d("content", content);
			if(content.length()>0){
				
				try {
					JSONObject json=new JSONObject(content);
					boolean b=json.optBoolean("result");
					String msg=json.optString("message");
					if(b){
						
						JSONArray array=json.getJSONArray("data");
						JSONObject jobj=array.getJSONObject(0);
						
						Toast.makeText(SendMoney.this, msg, Toast.LENGTH_SHORT).show();
						apply_per_value=Double.parseDouble(jobj.getString("discount_per"));
						coupen_per=Integer.parseInt(jobj.getString("discount_per"));
						ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData data = ClipData.newPlainText("", "");
						clipBoard.setPrimaryClip(data);
						coupen_code_et.setEnabled(false);
						//apply_coupon_tv.setBackgroundColor(color.dark_gray);
						apply_coupon_tv.setEnabled(false);
					}else{
						Toast.makeText(SendMoney.this, msg, Toast.LENGTH_SHORT).show();
						
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}
		@Override
		public void onFinish() {
			super.onFinish();
			if(dialog.isShowing())
				dialog.dismiss();
		}
		
		@Override
		public void onStart() {
			super.onStart();
			dialog=ProgressDialog.show(SendMoney.this, "", "Please Wait...");
		}
		
		
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			if(dialog.isShowing())
				dialog.dismiss();
			Toast.makeText(SendMoney.this, "Network error", Toast.LENGTH_SHORT).show();
			
		}
		
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		//Common_data.alertPinDialog(SendMoney.this);
		super.onStop();
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
	
}
