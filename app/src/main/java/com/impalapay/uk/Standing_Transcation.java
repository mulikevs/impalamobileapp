package com.impalapay.uk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Standing_Transcation extends FragmentActivity implements OnItemSelectedListener, OnClickListener {

	Spinner frequency_sp, every_sp;
	TextView days_mnth, start_date_tv;
	RadioButton pay_now, standing_tracnsaction;
	LinearLayout all_layout, date_layout;
	EditText et_payment_number;
	Button submit, cancel;
	Spinner spinner_purpose;
	String payment_purpose = null;

	String arrayftype[] = { "Days", "Months" };

	ArrayAdapter<String> modeAdapter;
	int current_year, current_month, current_day;
	int byear, bmonth, bday;
	ArrayList<String> alist;
	Standing_Transcation activity = this;
	ProgressDialog dialog;

	// ---NEW
	String del_method = "", payment_method = "";
	private String ordernumber = "0";
	private String switchStatus;
	private LinearLayout legal_data_layout;
	private EditText ssn_et;
	private EditText passport_num_et;
	private String ssn = "";
	private String passport_num = "";
	boolean request_legal_data = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.standing_instruction);
		Log.i("Sawapay Rate",String.valueOf(SendMoney1_Frag.exResult_double));
		try {
			init();
			getDataFromIntent();

			Log.e("what", payment_method);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		//getApp().touch();
	}

	private void getDataFromIntent() throws Exception {
		try {
			payment_method = SendMoney3.pay_type;

			if (SendMoney3.what.equalsIgnoreCase("airtime"))
				del_method = "airtime";
			else if (SendMoney3.what.equalsIgnoreCase("main"))
				del_method = "main";
			else
				del_method = SendMoney1_Frag.type;

			Log.d("details", SendMoney2.bankname + "\n" + del_method);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Random r = new Random();
		int ordern = 100000 + (int) (r.nextFloat() * 899900);
		int is = r.nextInt(9999);
		ordernumber = ordern + "" + is;
		Log.d("orderno", ordernumber + "");
	}

	private void init() {
		alist = new ArrayList<String>();

		frequency_sp = (Spinner) findViewById(R.id.payment_frequency_sp);
		every_sp = (Spinner) findViewById(R.id.sp_days_mnth);

		days_mnth = (TextView) findViewById(R.id.tv_days_mnth);
		start_date_tv = (TextView) findViewById(R.id.start_date_tv);

		pay_now = (RadioButton) findViewById(R.id.pay_now_radio);
		standing_tracnsaction = (RadioButton) findViewById(R.id.standing_inst_radio);

		all_layout = (LinearLayout) findViewById(R.id.all_layout);
		all_layout.setVisibility(View.GONE);


		legal_data_layout = (LinearLayout) findViewById(R.id.legal_data_layout);
		ssn_et = (EditText) findViewById(R.id.ssn_et);
		passport_num_et = (EditText) findViewById(R.id.passport_num_et);

		//Log.d("SSN", Common_data.getPreferences(Standing_Transcation.this, "ssn"));
		Log.d("SSN",new PrefManager<String>(getApplicationContext()).get("ssn",""));
		//if(Common_data.getPreferences(Standing_Transcation.this, "base").equals("CAD") ) {
		if(new PrefManager<String>(getApplicationContext()).get("base","").equals("CAD")) {
			legal_data_layout.setVisibility(View.GONE);
			request_legal_data = false;
		}else{
			//Log.d("Passport Number", Common_data.getPreferences(Standing_Transcation.this, "passport_num"));
			Log.d("Passport Number",new PrefManager<String>(getApplicationContext()).get("passport_num",""));
			//if (Common_data.getPreferences(Standing_Transcation.this, "ssn").equals("null") &&
					//Common_data.getPreferences(Standing_Transcation.this, "passport_num").equals("null")) {
			if (new PrefManager<String>(getApplicationContext()).get("ssn","").equals("null") &&
					new PrefManager<String>(getApplicationContext()).get("passport_num","").equals("null")) {
				Log.d("Request Legal Data", "To be requested");
				request_legal_data = true;
			} else {
				legal_data_layout.setVisibility(View.GONE);
				request_legal_data = false;
			}
		}
		spinner_purpose = (Spinner) findViewById(R.id.purpose_spinner);
		spinner_purpose.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				payment_purpose = spinner_purpose.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});


		date_layout = (LinearLayout) findViewById(R.id.date_layout);

		et_payment_number = (EditText) findViewById(R.id.nmbr_of_pymnt_et);

		submit = (Button) findViewById(R.id.pay_submit);
		cancel = (Button) findViewById(R.id.cancel);

		frequency_sp.setOnItemSelectedListener(this);

		start_date_tv.setOnClickListener(this);
		pay_now.setOnClickListener(this);
		standing_tracnsaction.setOnClickListener(this);
		date_layout.setOnClickListener(this);
		submit.setOnClickListener(this);
		cancel.setOnClickListener(this);

		alist.clear();

		for (int i = 1; i <= 5; i++) {
			alist.add(i + "");
		}

		modeAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_spinner_text, alist);
		modeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		every_sp.setAdapter(modeAdapter);
		days_mnth.setText("Days");

		ArrayAdapter<String> first = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner_text,arrayftype);
		first.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		frequency_sp.setAdapter(first);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long arg3) {

		if (pos == 1) {
			alist.clear();

			days_mnth.setText("Month");
			for (int i = 1; i <= 12; i++) {
				alist.add(i + "");
			}
			modeAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_spinner_text, alist);
			modeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			modeAdapter.notifyDataSetChanged();

		} else if (pos == 0) {
			alist.clear();
			days_mnth.setText("Day");

			for (int i = 1; i <= 5; i++) {
				alist.add(i + "");
			}
			modeAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.custom_spinner_text, alist);
			modeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			modeAdapter.notifyDataSetChanged();

		} else {
		}
		every_sp.setSelection(0);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.pay_now_radio:
			all_layout.setVisibility(View.GONE);
			break;

		case R.id.standing_inst_radio:
			Toast.makeText(getApplicationContext(), "Under Development",Toast.LENGTH_SHORT).show();
			all_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.date_layout:

			break;
		case R.id.start_date_tv:
			Calendar c = Calendar.getInstance();
			current_day = c.get(Calendar.DATE);
			current_month = c.get(Calendar.MONTH);
			current_year = c.get(Calendar.YEAR);
			new DatePickerDialog(Standing_Transcation.this, datePickerListener,current_year, current_month, current_day).show();
			break;

		case R.id.pay_submit:
			if (!pay_now.isChecked()) {
				if (validation())
					Toast.makeText(getApplicationContext(),"Under Development", Toast.LENGTH_SHORT).show();
			} else
				// sendMoney();
			    if(request_legal_data){
					if( (ssn_et.getText().toString().toString().equals("") || ssn_et.getText().toString().toString().length() < 4)
							&& (passport_num_et.getText().toString().trim().equals("") ) ){
						Toast.makeText(getApplicationContext(),"Enter either your Passport Number or the last 4 digits of your SSN", Toast.LENGTH_SHORT).show();
					}
					else{
						if (payment_method.equalsIgnoreCase("bank")) {
							String msg = "Please note that ACH transactions normally take 3 to 5 days to process. " +
									"Once successfully processed and the money is deducted from your bank account, " +
									"we will send you a confirmation email and at that point your funds will be delivered to " +
									"the intended recipient. To process the ACH transaction click Yes, " +
									"or click No to cancel the transaction.";
							new AlertDialog.Builder(Standing_Transcation.this)
									.setTitle("Info")
									.setMessage(msg)
									.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											showConfirmTrancationWithPassword();
										}
									})
									.setNegativeButton("No", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {
											// TODO Auto-generated method stub
											//finish();
											//startActivity(getIntent());
											Toast.makeText(getApplicationContext(),"You have chosen to cancel the transaction.", Toast.LENGTH_SHORT).show();
											Intent i = new Intent(Standing_Transcation.this,SendMoney4.class);
											startActivity(i);
										}
									}).show();
						}
						else{
							showConfirmTrancationWithPassword();
						}
					}
				}
			    else {
					if (payment_method.equalsIgnoreCase("bank")) {
						String msg = "Please note that ACH transactions normally take 2 to 5 days to process. " +
								"Once successfully processed and the money is deducted from your bank account, " +
								"we will send you a confirmation email and at that point your funds will be delivered to " +
								"the intended recipient. To process the ACH transaction click Yes, " +
								"or click No to cancel the transaction.";
						new AlertDialog.Builder(Standing_Transcation.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										showConfirmTrancationWithPassword();
									}
								})
								.setNegativeButton("No", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										//finish();
										//startActivity(getIntent());
										Toast.makeText(getApplicationContext(),"You have chosen to cancel the transaction.", Toast.LENGTH_SHORT).show();
										Intent i = new Intent(Standing_Transcation.this,SendMoney4.class);
										startActivity(i);
									}
								}).show();
					}
					else{
						showConfirmTrancationWithPassword();
					}
				}
			break;

		case R.id.cancel:
			finish();
			break;
		default:
			break;
		}
	}

	private JSONObject loadPaysafe(){
		JSONObject jsonObject = new JSONObject();

		RequestParams params = new RequestParams();
		JSONObject request = new JSONObject();

		//request.put("merchantRefNum")



		return jsonObject;
	}

	private void sendMoney() {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			//object.put("userid", Common_data.getPreferences(activity, "userid"));
			object.put("userid", new PrefManager<String>(getApplicationContext()).get("userid",""));


			object.put("payment_method", payment_method);

			if (payment_method.equalsIgnoreCase("card")) {
				Log.d("sendMoney", "from card");
				if(SendMoney3.saved_cards){
					object.put("new_card", false);
					object.put("card_ref", SendMoney3.card_json_object.getString("card_ref"));
					object.put("masked_card_number", SendMoney3.card_json_object.getString("masked_card_number"));
				}
				else{
					object.put("new_card", true);
					object.put("card_number", SendMoney3.card_number_st);
					object.put("card_cvv", SendMoney3.card_cvv_st);
					object.put("card_expiry_date", SendMoney3.card_expiry_date_st);
				}

				//object.put("card_name", SendMoney4.name);
			} else if (payment_method.equalsIgnoreCase("bank")) {
				Log.d("sendMoney3", "from bank");
				object.put("client_bank_id", SendMoney4.client_bank_id);
                object.put("bank_name", SendMoney4.name);
				object.put("bank_account_number", SendMoney4.bankno);

			} else if (payment_method.equalsIgnoreCase("ewallet")) {
				Log.d("sendMoney3", "from ewallet");
				object.put("remitter_email", SendMoney4.email);
			} else if (payment_method.equalsIgnoreCase("interac")){
				Log.d("sendMoney3", "from interac");

			}

			object.put("delivery_method", del_method);
			object.put("transfer_purpose", payment_purpose);

			if (del_method.equals("airtime")) {
                Log.d("sendMoney3", "to airtime");
				object.put("send", AirTime_Frag.debit);
				object.put("receive", AirTime_Frag.recharge_amount);
				object.put("totalamount", AirTime_Frag.recharge_amount);
				object.put("del_method", del_method);

				object.put("mpesano", AirTime_Frag.mobile);
				object.put("recp", "");
			} else if (del_method.equalsIgnoreCase("main")) {
                Log.d("sendMoney3", "to main");
				String del = MainPageTranscationAdapter.clonemodel.getDel_method();
				object.put("send", SendMoney4.clonemodel4.getSend());
				object.put("receive", SendMoney4.clonemodel4.getReceve());
				object.put("totalamount", SendMoney4.amountforTranscation);

				object.put("payment_method", payment_method);

				if (payment_method.equalsIgnoreCase("card")) {
					Log.d("sendMoney3", "from card");
					object.put("remitter_card_number", SendMoney4.cardno);
					object.put("remitter_card_name", SendMoney4.name);
				} else if (payment_method.equalsIgnoreCase("bank")) {
					Log.d("sendMoney3", "from bank");

					object.put("remitter_bank_name", "");
					object.put("remitter_aba_number", "");
					object.put("remitter_account_number", SendMoney4.bankno);
				} else if (payment_method.equalsIgnoreCase("ewallet")) {
					Log.d("sendMoney3", "from ewallet");
					object.put("remitter_email", SendMoney4.email);
				}

				object.put("del_method", del);

				if (del.equalsIgnoreCase("bank")) {
                    Log.d("sendMoney", "to bank");
					object.put("comm", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("bank_name", SendMoney4.clonemodel4.getBankname());
					object.put("branchname",SendMoney4.clonemodel4.getBranchname());
					object.put("actn", SendMoney4.clonemodel4.getAct_number());
					object.put("recp",SendMoney4.clonemodel4.getReceipient_name());
					object.put("reference", SendMoney3.reference_st);

				} else if (del.equalsIgnoreCase("mpesa")) {
                    Log.d("sendMoney", "to mpesa");
					object.put("comm", 0);
					object.put("remarks", SendMoney4.clonemodel4.getRemarks());
					object.put("bank_name", "");
					object.put("branchname", "");
					object.put("actn", "");
					object.put("mpesano",SendMoney4.clonemodel4.getMpesa_number());
					object.put("recp",SendMoney4.clonemodel4.getReceipient_name());
					object.put("reference",SendMoney4.clonemodel4.getReference());
					object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));
				} else if (del.equalsIgnoreCase("paybill")) {
                    Log.d("sendMoney", "to paybill");
					object.put("recp",SendMoney4.clonemodel4.getReceipient_name());
					object.put("payno", SendMoney4.clonemodel4.getPaybillno());
					object.put("reference", SendMoney4.clonemodel4.getReference());
					object.put("dis", SendMoney4.clonemodel4.getDiscription());
					object.put("comm", 0);
					object.put("bank_name", SendMoney4.clonemodel4.getBankname());
					object.put("branchname", SendMoney4.clonemodel4.getBranchname());
					object.put("remarks", SendMoney4.clonemodel4.getRemarks());
					object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));
					
				} else if (del.equalsIgnoreCase("ewallet")) {
                    Log.d("sendMoney", "to ewallet");
					object.put("comm", SendMoney4.clonemodel4.getCommission());
					object.put("recp",SendMoney4.clonemodel4.getReceipient_name());
					object.put("email", SendMoney4.clonemodel4.getPay_email());
					
				
				}
			} else {
                Log.d("sendMoney", "to else");
				if(request_legal_data){
					if(ssn_et.getText().toString().trim().equals("")){
						object.put("ssn", "");
					}
					else{
						object.put("ssn", "XXX-XX-" + ssn_et.getText().toString().trim());
					}
					object.put("passport_num", passport_num_et.getText().toString().trim());

				}
				object.put("base_currency", SendMoney1_Frag.base);
				object.put("converted_currency", SendMoney1_Frag.convert);
				object.put("sawapay_fx_rate", SendMoney1_Frag.exResult_double);
				object.put("send_amount", SendMoney1_Frag.send);
				object.put("receive_amount", SendMoney1_Frag.receve);
				object.put("transfer_fees", SendMoney1_Frag.commission);
				object.put("total_cost", SendMoney1_Frag.total_am);
				object.put("delivery_method", del_method);

				if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_cash_pickup")){
					if(SendMoneyCashPickup.ben_id.equals("")){
						object.put("recipient_key", SendMoneyCashPickup.newly_ben_id);
					}
					else{
						object.put("recipient_key", SendMoneyCashPickup.ben_id);
					}
				}else {
					if(SendMoney2.ben_id.equals("")){
						object.put("recipient_key", SendMoney2.newly_ben_id);
					}
					else{
						object.put("recipient_key", SendMoney2.ben_id);
					}
				}



				if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && del_method.equalsIgnoreCase("bank")) {
                    Log.d("sendMoney", "to bank");

					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_bank_name", SendMoney2.bankname);
					object.put("recipient_branch_name", SendMoney2.branchname);
					object.put("recipient_account_number", SendMoney2.account_no);
					object.put("recipient_name", SendMoney2.name_static);
					object.put("reference", SendMoney3.reference_st);
					object.put("recipient_msisdn",SendMoney2.mobile_static.replace(" ", ""));

				} else if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && del_method.equalsIgnoreCase("mpesa")) {
                    Log.d("sendMoney3", "to mpesa");
					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_msisdn",SendMoney2.mobile_static.replace(" ", ""));
					object.put("recipient_name", SendMoney2.name_static);
					object.put("reference", SendMoney3.reference_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));

				} else if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && del_method.equalsIgnoreCase("paybill")) {
                    Log.d("sendMoney3", "to paybill");
					object.put("commission", 0);
					object.put("recipient_name", SendMoney_PayBill.name);
					object.put("recipient_number", SendMoney_PayBill.number);
					object.put("paybill_account_number", SendMoney_PayBill.accountnumber);
					object.put("description", SendMoney_PayBill.dis);
					object.put("remarks", SendMoney3.remarks_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));
				}else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && del_method.equalsIgnoreCase("uganda_airtel_money")) {
					Log.d("sendMoney3", "to uganda_airtel_money");
					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_msisdn",SendMoney2.mobile_static.replace(" ", ""));
					object.put("recipient_name", SendMoney2.name_static);
					object.put("reference", SendMoney3.reference_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));

				} else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && del_method.equalsIgnoreCase("uganda_mtn_money")) {
					Log.d("sendMoney3", "to mtn_noney");
					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_msisdn",SendMoney2.mobile_static.replace(" ", ""));
					object.put("recipient_name", SendMoney2.name_static);
					object.put("reference", SendMoney3.reference_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));

				} else if (SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && del_method.equalsIgnoreCase("zimbabwe_telecash")) {
					Log.d("sendMoney3", "to Telecel");
					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_msisdn",SendMoney2.mobile_static.replace(" ", ""));
					object.put("recipient_name", SendMoney2.name_static);
					object.put("reference", SendMoney3.reference_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));

				}else if (SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && del_method.equalsIgnoreCase("zimbabwe_cash_pickup")) {
					Log.d("sendMoney3", "to Pickup");
					object.put("commission", 0);
					object.put("remarks", SendMoney3.remarks_st);
					object.put("recipient_msisdn",SendMoneyCashPickup.mobile_static);
					object.put("recipient_name", SendMoneyCashPickup.name_static);
					object.put("reference", SendMoney3.reference_st);
					//object.put("mccmnc", Common_data.getMCCMNC(Standing_Transcation.this));

				} else if (del_method.equalsIgnoreCase("ewallet")) {
                    Log.d("sendMoney3", "to ewallet");
					object.put("commission", 0);
					object.put("recipient_name", SendMoney2.name_static);
					object.put("email", SendMoney2.email_static);
				}

				else if (del_method.equals("airtime")) {
                    Log.d("sendMoney3", "to airtime");
					object.put("recipient_msisdn", AirTime_Frag.mobile);
					object.put("recipient_name", "");
				}

			}
			params.put("request", object.toString());
			Log.d("transfer request", params.toString());
			RestHttpClient.postParams("banking/transferMoney", params, new NewTaskHandler(), Login.token);
		}

		catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// NEW
	class NewTaskHandler extends AsyncHttpResponseHandler {


		@SuppressWarnings("static-access")
		@Override
		public void onStart() {
			super.onStart();
			dialog = ProgressDialog.show(Standing_Transcation.this, null,"Processing your transaction... ");
		}


		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			super.onSuccess(result);

			Log.d("resoponse teradslkfs", result);
			System.out.println("SENDMONEY RESPONSE MACHETTE "+result);
			if (result.length() > 0) {

				try {

					JSONObject json = new JSONObject(result);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    Log.e("response_code", String.valueOf(response_code));
                    if(1001 == response_code)
                        b = true;
					final String msg = json.optString("message");
					//String balance=json.optString("balance");
					//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();

					if (b) {
						dialog.dismiss();
						if (payment_method.equalsIgnoreCase("interac")){
							Intent intent = new Intent(Standing_Transcation.this, InteracWebview.class);
							intent.putExtra("url", msg);
							startActivity(intent);
							finish();
						}else {
							final Dialog d=new Dialog(activity);
							d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							d.requestWindowFeature(Window.FEATURE_NO_TITLE);
							d.setCancelable(false);
							d.setContentView(R.layout.success_response);
							TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
							tv_success.setText(msg);


							if(request_legal_data){
								if(! ssn_et.getText().toString().trim().equals("")){
									//Common_data.setPreference(activity, "ssn", "XXX-XX-" + ssn_et.getText().toString().trim());
									new PrefManager<String>(getApplicationContext()).set("ssn","XXX-XX-" + ssn_et.getText().toString().trim());

								}
								if(! passport_num_et.getText().toString().trim().equals("")){
									//Common_data.setPreference(activity, "passport_num", passport_num_et.getText().toString().trim());
									//Common_data.setPreference(activity, "pas", passport_num_et.getText().toString().trim());
									new PrefManager<String>(getApplicationContext()).set("passport_num",passport_num_et.getText().toString().trim());
									new PrefManager<String>(getApplicationContext()).set("pas",passport_num_et.getText().toString().trim());

								}
							}


						/*Common_data.setPreference(activity, "pas", passport_num_et.getText().toString().trim());
						Common_data.setPreference(activity, "passport_num", passport_num_et.getText().toString().trim()); */

							Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
							btn_ok.setText("OK");
							btn_ok.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View arg0) {
									d.dismiss();
									finish();
									//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
									Intent i = new Intent(Standing_Transcation.this, MainActivity.class);
									startActivity(i);
								}
							});
							d.show();
						}
						/*if (payment_method.equalsIgnoreCase("ewallet")) {
							String rembal=Common_data.getPreferences(getApplicationContext(), "balance");
							if(rembal.equals(""))
									rembal="0";

							double bal=Double.parseDouble(rembal);

							bal = bal - SendMoney1_Frag.send;
							Common_data.setPreference(activity, "balance", ""+ bal);
						}
						if(del_method.equals("main") && payment_method.equalsIgnoreCase("ewallet"))
						{
							String rembal=Common_data.getPreferences(getApplicationContext(), "balance");
							if(rembal.equals(""))
									rembal="0";

							double bal=Double.parseDouble(rembal);

							bal = bal - Integer.parseInt(SendMoney4.clonemodel4.getSend());
							Common_data.setPreference(activity, "balance", ""+ bal);
						}*/
						//Common_data.setPreference(activity, "balance",balance);
                      /*  new AlertDialog.Builder(activity)
                                .setTitle("Info")
                                .setMessage("Your request has been received. It is being processed.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        finish();
                                        //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Standing_Transcation.this,MainActivity.class);
                                        startActivity(i);
                                    }
                                }).show(); */


					}
					else if(1010 == response_code){
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(activity)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										showConfirmTrancationWithPassword();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										Intent i = new Intent(Standing_Transcation.this, SendMoney3.class);
										startActivity(i);
									}
								}).show();
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(Standing_Transcation.this);
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
								Intent i = new Intent(Standing_Transcation.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(Standing_Transcation.this);
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
					else if(1040 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(activity)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("YES", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										Intent i = new Intent(Standing_Transcation.this, TransferLimit.class);
										startActivity(i);
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										Intent i = new Intent(Standing_Transcation.this, SendMoney3.class);
										startActivity(i);
									}
								}).show();
					}
					else {
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(activity)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										showConfirmTrancationWithPassword();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										finish();
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										Intent i = new Intent(Standing_Transcation.this, SendMoney3.class);
										startActivity(i);
									}
								}).show();
					}

				} catch (Exception e) {

				}

			}
		}

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			Toast.makeText(getApplicationContext(), "Network Error!",Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(activity)
					.setTitle("Network Error")
					.setMessage("Sorry. An error occurred. Do not retry the transaction. Contact customer care for further assistance.")
					.setNegativeButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							finish();
							//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
							Intent i = new Intent(Standing_Transcation.this, MainActivity.class);
							startActivity(i);
						}
					}).show();

		}


		@Override
		public void onFinish() {
			super.onFinish();
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
		}

	}

	private boolean validation() {
		String number = et_payment_number.getText().toString();
		String date = start_date_tv.getText().toString();
		int num = 0;
		boolean flag = false;

		if (number.length() != 0)
			num = Integer.parseInt(number);

		if (number.length() == 0)
			Toast.makeText(getApplicationContext(), "Enter number of payments",Toast.LENGTH_SHORT).show();
		else if (number.equals("0"))
			Toast.makeText(getApplicationContext(),"Enter valid number of payments", Toast.LENGTH_SHORT).show();
		else if (num > 999)
			Toast.makeText(getApplicationContext(),"Number of payments less than 999", Toast.LENGTH_SHORT).show();
		else if (date.equalsIgnoreCase("Select start date"))
			Toast.makeText(getApplicationContext(), "select start date",Toast.LENGTH_SHORT).show();
		else
			flag = true;

		return flag;

	}

	public void showConfirmTrancationWithPassword() {
		if (payment_purpose == null || payment_purpose.equals("-Select Transfer Purpose-")){
			Toast.makeText(Standing_Transcation.this, "Please Select a Transfer Purpose", Toast.LENGTH_SHORT).show();
			return;
		}
		final Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);

		final Dialog dialog = new Dialog(activity);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.password_verify);

		final EditText pwd = (EditText) dialog.findViewById(R.id.password_et_verfy);
		final TextView proceed = (TextView) dialog.findViewById(R.id.proceed);
		proceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String pwdd = pwd.getText().toString();

				if (pwd.getText().toString().length() != 0) {
					//if (pwdd.equals(Common_data.getPreferences(getApplicationContext(), "password"))) {
					if (pwdd.equals(new PrefManager<String>(getApplicationContext()).get("password",""))) {
						sendMoney();
						dialog.dismiss();
					} else {
						Toast.makeText(activity, "Wrong Password", Toast.LENGTH_SHORT).show();
					}
                } else {
					pwd.startAnimation(animation1);
				}
			}
		});
		dialog.show();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onStop() {
		//Common_data.alertPinDialog(Standing_Transcation.this);
		super.onStop();
	}
	
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			byear = selectedYear;
			bmonth = selectedMonth;
			bday = selectedDay;
			start_date_tv.setText(new StringBuilder().append(bday).append("-")
					.append(bmonth + 1).append("-").append(byear).append(" "));

			StringBuilder bodsb = ((new StringBuilder().append(bday)
					.append("-").append(bmonth + 1).append("-").append(byear)
					.append(" ")));

			if (current_year > byear) {
				start_date_tv.setText("Select Start Date");
				Toast.makeText(getBaseContext(), "Please select correct date ",Toast.LENGTH_LONG).show();
			}

			if (current_year == byear) {
				if (current_month > bmonth) {
					start_date_tv.setText("Select Start Date");
					Toast.makeText(getBaseContext(),"Please select correct date", Toast.LENGTH_LONG).show();
				}

				if (current_day > bday) {
					if (current_month > bmonth) {
						start_date_tv.setText("Select Start Date");
						Toast.makeText(getBaseContext(),"Please select correct date", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	};

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


	/*private void startWebView(String url) {

		final Dialog interac = new Dialog(activity);
		interac.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		interac.requestWindowFeature(Window.FEATURE_NO_TITLE);
		interac.setContentView(R.layout.interac_webview);

		WebView webView = (WebView) interac.findViewById(R.id.interac_webview);
		WebSettings settings = webView.getSettings();

		settings.setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Loading...");
		progressDialog.show();

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
					interac.show();
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(getApplicationContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

			}
		});
		webView.loadUrl(url);
	}*/

}
