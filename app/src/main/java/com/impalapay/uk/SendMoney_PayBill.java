package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.MPesaPaybillListModel;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SendMoney_PayBill extends FragmentActivity {
	
	
	AutoCompleteTextView buisness_number;
	TextView selected_mpesa_name;
	EditText account_number,discription;
	Button next2Paybill;
	
	
	ArrayList<MPesaPaybillListModel> array_model_mpesa;
	
	ArrayList<String> arraynumber;
	ArrayList<String> arrayname;
	ArrayList<String> arrayBoth;
	
	private TextView back_tv;
	private TextView next_tv;
	
	
	
	//static data
	static String number,name, accountnumber,dis;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paybill);

		init();
		Common_data.setupUI(findViewById(R.id.hide), SendMoney_PayBill.this);

		//getApp().touch();
	}

	private void init() {
		
		
		
		back_tv=(TextView) findViewById(R.id.back_tv);
		next_tv=(TextView) findViewById(R.id.done_tv);
		next_tv.setText("Next");
		
		arraynumber=new ArrayList<String>();
		arrayname=new ArrayList<String>();
		arrayBoth=new ArrayList<String>();
		
		buisness_number=(AutoCompleteTextView) findViewById(R.id.buisness_number);
		selected_mpesa_name=(TextView) findViewById(R.id.selected_mpesa_name);
		account_number=(EditText) findViewById(R.id.account_number);
		discription=(EditText) findViewById(R.id.discription);
		next2Paybill=(Button) findViewById(R.id.next2Paybill);
		array_model_mpesa=new ArrayList<MPesaPaybillListModel>();
		
		number="";
		name="";
		accountnumber ="";

		RestHttpClient.post("banking/getPaybillNames", new GetPaybillListHandler());
		applylistener();
	}
	
	private void applylistener() {
		
		
		back_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Common_data.setPreference(getApplicationContext(), "alertshow", "false");
				finish();
			}
		});
		
		next_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validation()) {

					String a = buisness_number.getText().toString();
					boolean flagnumber = arraynumber.contains(a);
					boolean flagname = arrayname.contains(a);
					if (flagname || flagnumber) {
						if (Common_data.isSimInserted(SendMoney_PayBill.this)) {
							accountnumber = account_number.getText().toString();
							dis = discription.getText().toString();
							Intent i = new Intent(SendMoney_PayBill.this, SendMoney3.class);
							i.putExtra("iswhat", "paybill");
							startActivity(i);
							finish();
						} else {
							Toast.makeText(SendMoney_PayBill.this, "No Sim Detected, Please Insert Sim Card!!!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(SendMoney_PayBill.this, "Invalid Business Name/Number", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		
		
		next2Paybill.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (validation()) {

					String a = buisness_number.getText().toString();
					boolean flagnumber = arraynumber.contains(a);
					boolean flagname = arrayname.contains(a);
					if (flagname || flagnumber) {
						if (Common_data.isSimInserted(SendMoney_PayBill.this)) {
							accountnumber = account_number.getText().toString();
							dis = discription.getText().toString();
							Intent i = new Intent(SendMoney_PayBill.this, SendMoney3.class);
							i.putExtra("iswhat", "paybill");
							startActivity(i);
							finish();
						} else {
							Toast.makeText(SendMoney_PayBill.this, "No Sim Detected, Please Insert Sim Card!!!", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(SendMoney_PayBill.this, "Invalid Business Name/Number", Toast.LENGTH_SHORT).show();
					}
				}
			}


		});
		
		
		buisness_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				selected_mpesa_name.setVisibility(View.GONE);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {


			}

			@Override
			public void afterTextChanged(Editable arg0) {
				name = "";
				number = "";

			}
		});
		
		buisness_number.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> p, View v, int pos, long arg3) {
				String item = p.getItemAtPosition(pos).toString();
				//int ps=arraynumber.indexOf(item);
				Log.d("postion", item);
				int ps = arrayname.indexOf(item);
				Log.d("postiondsklfjsd fsd", ps + "");
				if (ps == -1)
					ps = arraynumber.indexOf(item);

				selected_mpesa_name.setVisibility(View.VISIBLE);
				selected_mpesa_name.setText("Name: " + array_model_mpesa.get(ps).getPaybillName() + " (" + array_model_mpesa.get(ps).getPaybillNumber() + ")");

				name = array_model_mpesa.get(ps).getPaybillName();
				number = array_model_mpesa.get(ps).getPaybillNumber();
			}
		});
	}
	
	/*@Override
	public void onBackPressed() {
	
	}*/

	private boolean validation() {
		String businessnumber=buisness_number.getText().toString();
		String acno=account_number.getText().toString();
		
		if(businessnumber.length()==0){
			Toast.makeText(getApplicationContext(), "Enter Buisness Number", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(acno.length()==0){
			Toast.makeText(getApplicationContext(), "Enter Account Number", Toast.LENGTH_SHORT).show();
			return false;
		}
		/*else if(acno.length()<10){
			Toast.makeText(getApplicationContext(), "Account number should be 20 digit", Toast.LENGTH_SHORT).show();
			return false;
		}*/
		
		else {
			return true;
		}
	}
	
	
	
	class GetPaybillListHandler extends AsyncHttpResponseHandler {

		ProgressDialog p;

		@Override
		public void onStart() {
			super.onStart();

			p = ProgressDialog.show(SendMoney_PayBill.this, "", "Loading...");
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);

			if (content.length() > 0) {
				try {
					array_model_mpesa.clear();
					arrayBoth.clear();
					arrayname.clear();
					arraynumber.clear();
					
					JSONObject json = new JSONObject(content);
					Log.i("Response", json.toString());
					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;

					String msg = json.getString("message");
					if (b) {
						
						JSONArray array = json.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							
							JSONObject object = array.getJSONObject(i);
							
							MPesaPaybillListModel model=new MPesaPaybillListModel();
							model.setPaybillName(object.optString("name"));
							model.setPaybillNumber(object.optString("number"));
							array_model_mpesa.add(model);
							arraynumber.add(object.optString("number"));
							arrayname.add(object.optString("name"));
						}
						arrayBoth.addAll(arrayname);
						arrayBoth.addAll(arraynumber);
						ArrayAdapter<String> ad=new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_layout,R.id.tv_item,arrayBoth);
						buisness_number.setAdapter(ad);
						buisness_number.setThreshold(1);
					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(SendMoney_PayBill.this);
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
								Intent i = new Intent(SendMoney_PayBill.this, Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(SendMoney_PayBill.this);
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
					
					
				} catch (Exception e) {

				}

			}
		}

		@Override
		public void onFinish() {
			super.onFinish();
			if (p != null && p.isShowing())
				p.dismiss();
			
		}

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog();
		}
	}
	
	 public void networkErrorDialog(){
		 
		 final Dialog d=new Dialog(SendMoney_PayBill.this);
		 d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		 d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 d.setCancelable(false);
		 d.setContentView(R.layout.network_error);
		 
		 Button tryagain=(Button) d.findViewById(R.id.try_again);
		 tryagain.setText("close");
		 tryagain.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View arg0) {
				 Common_data.setPreference(getApplicationContext(), "alertshow", "false");
				 Intent i = new Intent(SendMoney_PayBill.this, MainActivity.class);
				 startActivity(i);
			 }
		 });
		 d.show();
		 
	 }
	 
	 
	 
	 @SuppressWarnings("deprecation")
		@Override
		protected void onStop() {
		 	Common_data.alertPinDialog(SendMoney_PayBill.this);
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
