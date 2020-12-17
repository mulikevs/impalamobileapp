package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.BankDetModel;
import com.impalapay.models.CardDetModel;
import com.impalapay.models.TransactionHistoryModel;
import com.impalapay.uk.adapters.MainPageTranscationAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SendMoney4 extends FragmentActivity implements OnClickListener {

	TextView tv_send,tv_revece,tv_amount,tv_profit,tv_final_amount,tv_fee;
	TextView debited_amount,recharge_amount,mobile_number;
	TextView delivery_method_tv;
	TextView recipent_name,recipent_mobile;
	TextView payment_method_tv;
	TextView pay_name,pay_number,pay_ac_number;
	LinearLayout recpt_details_pay_ly,transcation_details_aittime_ly,transcation_details_sendmoney_ly,delevry_method_ly,recpt_details_ly;
	
	TextView back_tv, next_tv;
	
	String backString="";
	//-------CARD---
	TextView cardnumber_tv, expiry_date_tv,cardownername_tv;
	LinearLayout card_detail_ly;
	EditText otp_et;
	
	//-------BANK---
	TextView bank_name,bank_number;
	LinearLayout bank_details_ly;
	
	//-------EWALLET---
	TextView current_bal_tv;
	LinearLayout current_bal_ly;
	Button next4;
	
	ArrayList<CardDetModel> cardArray = new ArrayList<CardDetModel>();
	
	
	ArrayList<BankDetModel> bankArray = new ArrayList<BankDetModel>();
	LinearLayout bank_ly;
	TextView bankmname, branchname;
	private String pData;
	String otp;
	public static TransactionHistoryModel clonemodel4=new TransactionHistoryModel();

	
	//STATIC DATA
	static int client_bank_id;
	static String name,cardno,bankno,email;
	static String amountforTranscation="";
	
	boolean emailflag=false;
	boolean mobileflag=false;
	String del_method;
	private String switchStatus;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.send_money4);
		
		backString= SendMoney3.what;
		Log.d("backString", backString);
		Log.d("Pay type", SendMoney3.pay_type);
		
		init();
		//otp= Common_data.getPreferences(getApplicationContext(), "otpno");
		otp= new PrefManager<String>(getApplicationContext()).get("otpno","");
		getClientData();
		setData();
		
		//String mobileotp= Common_data.getPreferences(getApplicationContext(), "mobileotp");
		String mobileotp = new PrefManager<String>(getApplicationContext()).get("mobileotp","");
		//String emailotp= Common_data.getPreferences(getApplicationContext(), "emailotp");
		String emailotp = new PrefManager<String>(getApplicationContext()).get("emailotp","");
		String me = "Computer Scientist"; System.out.println(me);
		if(emailotp.equals("1"))
			emailflag=true;
		else
			emailflag=false;
		
		if(mobileotp.equals("1"))
			mobileflag=true;
		else
			mobileflag=false;

		//getApp().touch();
	}

	/*@Override
	public void onResume() {
		super.onResume();  // Always call the superclass method first

		init();
	} */

	

	private void init() {

		LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
		back_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent b=new Intent(SendMoney4.this,SendMoney3.class);
				b.putExtra("iswhat", backString);
				startActivity(b);
				finish();
			}
		});

		
			back_tv = (TextView) findViewById(R.id.back_tv);
			next_tv = (TextView) findViewById(R.id.done_tv);
			next_tv.setText("");
		
			tv_send=(TextView) findViewById(R.id.final_send_tv);
			tv_revece=(TextView) findViewById(R.id.final_receve_tv);
			tv_amount=(TextView) findViewById(R.id.amount_tv);
		    tv_fee=(TextView) findViewById(R.id.transfer_fees_tv);
			tv_profit=(TextView) findViewById(R.id.coupon_profit_tv);
			tv_final_amount=(TextView) findViewById(R.id.final_total_amount_tv);
			delivery_method_tv=(TextView) findViewById(R.id.delevry_method_tv);
			recipent_name=(TextView) findViewById(R.id.recpt_name);
			recipent_mobile=(TextView) findViewById(R.id.recpt_number);
			payment_method_tv=(TextView) findViewById(R.id.payment_method_tv);
			
			
			pay_name=(TextView) findViewById(R.id.pay_name);
			pay_number=(TextView) findViewById(R.id.pay_number);
			pay_ac_number=(TextView) findViewById(R.id.pay_ac_number);
			recpt_details_pay_ly=(LinearLayout) findViewById(R.id.recpt_details_pay_ly);
			
			debited_amount=(TextView) findViewById(R.id.debited_amount);
			recharge_amount=(TextView) findViewById(R.id.recharge_amount);
			mobile_number=(TextView) findViewById(R.id.mobile_number);
			
			transcation_details_aittime_ly=(LinearLayout) findViewById(R.id.transcation_details_aittime_ly);
			transcation_details_sendmoney_ly=(LinearLayout) findViewById(R.id.transcation_details_sendmoney_ly);
			delevry_method_ly=(LinearLayout) findViewById(R.id.delevry_method_ly);
			recpt_details_ly=(LinearLayout) findViewById(R.id.recpt_details_ly);
						
			cardnumber_tv=(TextView) findViewById(R.id.cardnumber_tv);
			expiry_date_tv =(TextView) findViewById(R.id.expiry_date_tv);
			cardownername_tv=(TextView) findViewById(R.id.cardownername_tv);
			card_detail_ly=(LinearLayout) findViewById(R.id.card_detail_ly);
		
			bank_name=(TextView) findViewById(R.id.bank_name_tv);
			bank_number=(TextView) findViewById(R.id.bank_number_tv);
			bank_details_ly=(LinearLayout) findViewById(R.id.bank_details_ly);
			
			current_bal_ly=(LinearLayout) findViewById(R.id.current_bal_ly);
			current_bal_tv=(TextView) findViewById(R.id.user_bal_tv);
			
			//LinearLayout bank_ly;
			//TextView bankmname,branchname;
			
			bank_ly=(LinearLayout) findViewById(R.id.bank_ly);
			bankmname=(TextView) findViewById(R.id.bankname);
			branchname =(TextView) findViewById(R.id.branchname);
			bank_ly.setVisibility(View.GONE);

			back_tv.setOnClickListener(this);

			next4=(Button) findViewById(R.id.next4);
			next4.setOnClickListener(this);
			
	}
	
	private void getClientData() {
		//pData = Common_data.getPreferences(SendMoney4.this, "data");
		pData = new PrefManager<String>(getApplicationContext()).get("data","");
        Log.d("getClientData(); data=>", pData);
		try {
			JSONObject object = new JSONObject(pData);
			JSONObject jsonObject = object.getJSONObject("data");
			//JSONObject object = array.getJSONObject(0);

			//JSONArray carddetails_array = new JSONArray(Common_data.getPreferences(SendMoney4.this, "cards_array")); //jsonObject.getJSONArray("card");
			JSONArray carddetails_array = new JSONArray(new PrefManager<String>(getApplicationContext()).get("cards_array",""));

//			if (carddetails_array.length() > 0) {
//				for (int i = 0; i < carddetails_array.length(); i++) {
//					CardDetModel model=new CardDetModel();
//					JSONObject carddetails_object = carddetails_array.getJSONObject(i);
//					model.setCardname(carddetails_object.getString("card_nickname"));
//					model.setCardno(carddetails_object.getString("masked_card_number"));
//					model.setExtDate(carddetails_object.getString("card_expiry_date"));
//					model.setOwner_name(carddetails_object.getString("card_owner"));
//
//					cardArray.add(model);
//                    Log.i("Card Details Selected",carddetails_array.toString());
//				}
//			}

			//JSONArray bankDetails_array=new JSONArray(Common_data.getPreferences(SendMoney4.this, "banks_array")); //jsonObject.optJSONArray("bank");
			JSONArray bankDetails_array = new JSONArray(new PrefManager<String>(getApplicationContext()).get("banks_array",""));

			Log.i("Arr",bankDetails_array.toString());
			
			
			if(bankDetails_array.length()>0){
				for(int i=0;i<bankDetails_array.length();i++){
					BankDetModel model=new BankDetModel();
					JSONObject bankobj=bankDetails_array.optJSONObject(i);

					model.setclientbankid(bankobj.optInt("bank_ref"));
					model.setbankname(bankobj.optString("bank_name"));
					model.setaccountno(bankobj.getString("masked_bank_number"));
					model.setabanumber(bankobj.getString("aba_number"));
					model.setCountry(bankobj.getString("country"));
					model.setSwift_code(bankobj.getString("aba_number"));
					
					bankArray.add(model);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next4:
			
			if(SendMoney3.pay_type.equals("ewallet")){
				
				if(emailflag)
				{
						Intent i = new Intent(SendMoney4.this,Standing_Transcation.class);
						i.putExtra("what", "");
						startActivity(i);
				}else{
					//showOTPDialog("e");
				}
				
			}
			else 
			{
				if(mobileflag){
					Intent i = new Intent(SendMoney4.this,Standing_Transcation.class);
					i.putExtra("what", "");
					startActivity(i);	
				}else
				{
					//showOTPDialog("m");
				}
				
			}
			break;

		case R.id.back_tv:
		
			Intent b=new Intent(SendMoney4.this,SendMoney3.class);
			b.putExtra("iswhat", backString);
			startActivity(b);
			finish();
			
		default:
			break;
		}
	}

	
	public void showOTPDialog(String status){
		
			final Dialog d = new Dialog(SendMoney4.this);
			d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			d.setContentView(R.layout.otp_dialog);
			d.setCancelable(false);
			d.show();
			
			otp_et = (EditText) d.findViewById(R.id.otp_et);

			final TextView request = (TextView) d.findViewById(R.id.request);
			final TextView cancel = (TextView) d.findViewById(R.id.cancel);
			final TextView status_otp=(TextView) d.findViewById(R.id.status_otp);
			final LinearLayout reject_ly=(LinearLayout) d.findViewById(R.id.reject_ly);
			final TextView resendotp=(TextView) d.findViewById(R.id.resendotp);
			final TextView msg=(TextView) d.findViewById(R.id.msg);
			final TextView resendemail=(TextView) d.findViewById(R.id.resendemail);
			
			if(status.equals("m")){
				status_otp.setText("Your Mobile Number not verified");
				otp_et.setVisibility(View.VISIBLE);
				msg.setVisibility(View.VISIBLE);
				resendemail.setVisibility(View.GONE);
			}else if(status.equals("e")){
				status_otp.setText("Your Email not verified please verify and login again");
				otp_et.setVisibility(View.GONE);
				request.setVisibility(View.GONE);
				reject_ly.setVisibility(View.GONE);
				cancel.setText("OK");
				resendotp.setVisibility(View.GONE);
				resendemail.setVisibility(View.VISIBLE);
				msg.setVisibility(View.GONE);
			}
			
			resendemail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					resendEmail();
				}
			});
			
			request.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(otp_et.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "Please Enter your One Time Password", Toast.LENGTH_SHORT).show();
					}
					else{
							d.dismiss();
							sendOTP();
					}
				}
			});
			cancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					d.dismiss();
				}
			});
			
			resendotp.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					resendOtp();
					
				}
			});
		
	}
	
private void resendEmail() {
		
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			//object.put("email", Common_data.getPreferences(getApplicationContext(), "email"));
			object.put("email", new PrefManager<String>(getApplicationContext()).get("email",""));

			
			params.put("request", object.toString());
			
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		RestHttpClient.postParams("resendemailverify", params,new ResendEmailHandler());
		
	}
	
	public class ResendEmailHandler extends AsyncHttpResponseHandler{
		ProgressDialog dialog;
		public void onStart() {
			// TODO Auto-generated method stub
			
			super.onStart();
				dialog = ProgressDialog.show(SendMoney4.this, "", "Please Wait...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			super.onSuccess(result);

			if (result.length() > 0) {
				Log.d("response", result);

				try {
					JSONObject jsonObject = new JSONObject(result);
					boolean b = jsonObject.getBoolean("result");
					String message = jsonObject.getString("message");
					if (b) {
						
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
					}
					else {
						otp_et.setText("");
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onFinish() {
			Log.d("onFinish", "onFinish");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			super.onFailure(statusCode, error, content);
		}
	}
	

	
private void resendOtp() {
		
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			//object.put("userid", Common_data.getPreferences(getApplicationContext(), "userid"));
			object.put("userid", new PrefManager<String>(getApplicationContext()).get("userid",""));


			params.put("request", object.toString());
			
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		RestHttpClient.postParams("resendotp", params,new ResendOtpHandler());
		
	}
	
	public class ResendOtpHandler extends AsyncHttpResponseHandler{
		ProgressDialog dialog;
		public void onStart() {
			// TODO Auto-generated method stub
			
			super.onStart();
				dialog = ProgressDialog.show(SendMoney4.this, "", "Please Wait...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			super.onSuccess(result);

			if (result.length() > 0) {
				Log.d("response", result);

				try {
					JSONObject jsonObject = new JSONObject(result);
					boolean b = jsonObject.getBoolean("result");
					String message = jsonObject.getString("message");
					if (b) {
						
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
					}
					else {
						otp_et.setText("");
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onFinish() {
			Log.d("onFinish", "onFinish");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			super.onFailure(statusCode, error, content);
		}
	}
	
	
	public void sendOTP(){
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			//object.put("userid", Common_data.getPreferences(getApplicationContext(), "userid"));
			object.put("userid", new PrefManager<String>(getApplicationContext()).get("userid",""));

			object.put("otpno", otp_et.getText().toString());
			
			params.put("request", object.toString());
			
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		RestHttpClient.postParams("smsverification", params,new OTPHandler());
	}
	
	public class OTPHandler extends AsyncHttpResponseHandler{
		ProgressDialog dialog;
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
				dialog = ProgressDialog.show(SendMoney4.this, "", "Please Wait...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			super.onSuccess(result);

			if (result.length() > 0) {
				Log.d("response", result);

				try {
					JSONObject jsonObject = new JSONObject(result);
					boolean b = jsonObject.getBoolean("result");
					String message = jsonObject.getString("message");
					if (b) {
						mobileflag=true;
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
						
						//Common_data.setPreference(getApplicationContext(), "mobileotp", "1");
						new PrefManager<String>(getApplicationContext()).set("mobileotp","1");
						Intent i = new Intent(SendMoney4.this,Standing_Transcation.class);
						i.putExtra("what", "");
						startActivity(i);	
					}
					else {
						otp_et.setText("");
						Toast.makeText(SendMoney4.this, message,Toast.LENGTH_SHORT).show();
						//showOTPDialog("m");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onFinish() {
			Log.d("onFinish", "onFinish");
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			super.onFailure(statusCode, error, content);
		}
	}
	
	
	/*@Override
	public void onBackPressed() {
	
	}*/

	private void setData() {
		DecimalFormat twoDForm = new DecimalFormat("0.00");
		DecimalFormat formatter = new DecimalFormat("#,###.00");
		
		if(SendMoney3.what.equals("paybill")){
			if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("bank"))
				del_method="Bank Account";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("mpesa"))
				del_method="M-PESA";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("paybill"))
				del_method="PayBill";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && SendMoney1_Frag.type.equals("uganda_airtel_money"))
				del_method="Ugandan Airtel Money";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && SendMoney1_Frag.type.equals("uganda_mtn_money"))
				del_method="Ugandan MTN Money";
			else if(SendMoney1_Frag.type.equals("ewallet"))
				del_method="Ewallet";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_telecash"))
				del_method="zimbabwe_telecash";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_cash_pickup"))
				del_method="zimbabwe_cash_pickup";
			
			transcation_details_aittime_ly.setVisibility(View.GONE);
			delevry_method_ly.setVisibility(View.VISIBLE);
			recpt_details_ly.setVisibility(View.GONE);
			recpt_details_pay_ly.setVisibility(View.VISIBLE);
			transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);
		
			bank_ly.setVisibility(View.GONE);

			tv_send.setText(SendMoney1_Frag.base+" "+twoDForm.format(SendMoney1_Frag.send));
			tv_fee.setText(SendMoney1_Frag.base+" "+twoDForm.format(SendMoney1_Frag.fees));
			tv_revece.setText(SendMoney1_Frag.convert + " " + formatter.format(SendMoney1_Frag.receve));
			tv_amount.setText(SendMoney1_Frag.base+" "+formatter.format(SendMoney1_Frag.total_am));
			
			double coupon_amt =SendMoney1_Frag.receve;
			coupon_amt =(coupon_amt * SendMoney3.coupen_per)/100;
			tv_profit.setText("+"+SendMoney1_Frag.convert+" "+ coupon_amt);
			Double total_receive_amount = SendMoney1_Frag.receve + coupon_amt;
			tv_final_amount.setText((SendMoney1_Frag.convert+" "+ formatter.format(total_receive_amount)));
			
			delivery_method_tv.setText(del_method);
			
			pay_name.setText(SendMoney_PayBill.name);
			pay_number.setText(SendMoney_PayBill.number);
			pay_ac_number.setText(SendMoney_PayBill.accountnumber);
			amountforTranscation=(coupon_amt +SendMoney1_Frag.total_am)+"";
		}
		else if(SendMoney3.what.equals("airtime")){
			transcation_details_aittime_ly.setVisibility(View.VISIBLE);
			delevry_method_ly.setVisibility(View.GONE);
			recpt_details_ly.setVisibility(View.GONE);
			recpt_details_pay_ly.setVisibility(View.GONE);
			transcation_details_sendmoney_ly.setVisibility(View.GONE);
			bank_ly.setVisibility(View.GONE);
			debited_amount.setText(SendMoney1_Frag.base+" "+AirTime_Frag.debit);
			mobile_number.setText(AirTime_Frag.mobile);
			recharge_amount.setText(SendMoney1_Frag.convert+" "+AirTime_Frag.recharge_amount);
			
			next4.setText("Send Airtime");
			amountforTranscation=AirTime_Frag.recharge_amount;
		}else if(SendMoney3.what.equals("main"))
		{

			clonemodel4=MainPageTranscationAdapter.clonemodel;
			transcation_details_aittime_ly.setVisibility(View.GONE);
			delevry_method_ly.setVisibility(View.VISIBLE);
			recpt_details_ly.setVisibility(View.VISIBLE);
			recpt_details_pay_ly.setVisibility(View.GONE);
			transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);
			tv_fee.setText(SendMoney1_Frag.base+" "+twoDForm.format(SendMoney1_Frag.fees));
			tv_send.setText(SendMoney1_Frag.base+" "+twoDForm.format(MainPageTranscationAdapter.clonemodel.getSend()));
			tv_revece.setText(SendMoney1_Frag.convert+" "+formatter.format(MainPageTranscationAdapter.clonemodel.getReceve()));
			tv_amount.setText(SendMoney1_Frag.base+" "+formatter.format(MainPageTranscationAdapter.clonemodel.getAmount()));
			
			double plus=Double.parseDouble(MainPageTranscationAdapter.clonemodel.getReceve()); //.getAmount());
			double am=Double.parseDouble(MainPageTranscationAdapter.clonemodel.getAmount());
			am=(am* SendMoney3.coupen_per)/100;
			tv_profit.setText("+"+SendMoney1_Frag.convert+" "+am);
			tv_final_amount.setText(SendMoney1_Frag.convert+" "+formatter.format((am+plus)));
			
			
			if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("bank"))
				del_method="Bank Account";
			else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("mpesa"))
				del_method="M-PESA";
			else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("paybill"))
				del_method="PayBill";
			else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("ewallet"))
				del_method="Ewallet";
			
			
			delivery_method_tv.setText(del_method);
			
			
			
			if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("bank"))
			{
				bank_ly.setVisibility(View.VISIBLE);
				recipent_mobile.setText(MainPageTranscationAdapter.clonemodel.getAct_number());
				recipent_name.setText(MainPageTranscationAdapter.clonemodel.getReceipient_name());
				
				bankmname.setText(MainPageTranscationAdapter.clonemodel.getBankname());
				branchname.setText(MainPageTranscationAdapter.clonemodel.getBranchname());
				
			}else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("mpesa"))
			{
				recipent_mobile.setText(MainPageTranscationAdapter.clonemodel.getMpesa_number());
				recipent_name.setText(MainPageTranscationAdapter.clonemodel.getReceipient_name());
			}else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("paybill"))
			{	
				recpt_details_ly.setVisibility(View.GONE);
				recpt_details_pay_ly.setVisibility(View.VISIBLE);
				pay_name.setText(MainPageTranscationAdapter.clonemodel.getReceipient_name());
				pay_number.setText(MainPageTranscationAdapter.clonemodel.getPaybillno());
				pay_ac_number.setText(MainPageTranscationAdapter.clonemodel.getReference());
				
			}else if(MainPageTranscationAdapter.clonemodel.getDel_method().equals("ewallet")){
				recipent_mobile.setText("");
				recipent_mobile.setVisibility(View.GONE);
				recipent_name.setText(MainPageTranscationAdapter.clonemodel.getPay_email());
			}else{
				recipent_mobile.setText("");
				recipent_name.setText("");
			}
			
			
			amountforTranscation=(am+plus)+"";
		}
		else{
			
			if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("bank"))
				del_method="Bank Account";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("mpesa"))
				del_method="M-PESA";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("paybill"))
				del_method="PayBill";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && SendMoney1_Frag.type.equals("uganda_airtel_money"))
				del_method="Ugandan Airtel Money";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") && SendMoney1_Frag.type.equals("uganda_mtn_money"))
				del_method="Ugandan MTN Money";
			else if(SendMoney1_Frag.type.equals("ewallet"))
				del_method="Ewallet";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_telecash"))
				del_method="zimbabwe_telecash";
			else if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_cash_pickup"))
				del_method="Cash Pickup";
			
			transcation_details_aittime_ly.setVisibility(View.GONE);
			delevry_method_ly.setVisibility(View.VISIBLE);
			recpt_details_ly.setVisibility(View.VISIBLE);
			recpt_details_pay_ly.setVisibility(View.GONE);
			transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);
			tv_fee.setText(SendMoney1_Frag.base+" "+twoDForm.format(SendMoney1_Frag.fees));
			tv_send.setText(SendMoney1_Frag.base+" "+twoDForm.format(SendMoney1_Frag.send));
			tv_revece.setText(SendMoney1_Frag.convert+" "+ formatter.format(SendMoney1_Frag.receve));
			tv_amount.setText(SendMoney1_Frag.base+" "+formatter.format(SendMoney1_Frag.total_am));
			
			double am=SendMoney1_Frag.receve;
			am=(am* SendMoney3.coupen_per)/100;
			tv_profit.setText("+"+SendMoney1_Frag.convert+" "+am);
			tv_final_amount.setText(SendMoney1_Frag.convert+" "+(formatter.format(am+SendMoney1_Frag.receve)));
			
			delivery_method_tv.setText(del_method);
		
			if(SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equals("bank"))
			{
				recipent_mobile.setText(SendMoney2.account_no);
				recipent_name.setText(SendMoney2.name_static);
				bank_ly.setVisibility(View.VISIBLE);
				bankmname.setText(SendMoney2.bankname);
				branchname.setText(SendMoney2.branchname);
				
			}
			else if(SendMoney1_Frag.type.equals("ewallet"))
			{
				recipent_mobile.setText("");
				recipent_name.setText(SendMoney2.email_static);
				
			}else{
				if(SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") && SendMoney1_Frag.type.equals("zimbabwe_cash_pickup")){
					recipent_mobile.setText(SendMoneyCashPickup.mobile_static);
					recipent_name.setText(SendMoneyCashPickup.name_static);
				}else{
					recipent_mobile.setText(SendMoney2.mobile_static);
					recipent_name.setText(SendMoney2.name_static);
				}
			}
			
			
			amountforTranscation=(am+SendMoney1_Frag.total_am)+"";
		}


		if(SendMoney3.pay_type.equals("card")){
			payment_method_tv.setText("Card");
		}
		else if(SendMoney3.pay_type.equals("bank")){
			payment_method_tv.setText("Bank Account");
		}
		else if(SendMoney3.pay_type.equals("ewallet")){
			payment_method_tv.setText("E-Wallet");
		}else if (SendMoney3.pay_type.equals("interac")){
			payment_method_tv.setText("Interac Online");
		}

		
		if(SendMoney3.pay_type.equals("card")){
			
			card_detail_ly.setVisibility(View.VISIBLE);
			if(SendMoney3.saved_cards){
				try {
					cardnumber_tv.setText(SendMoney3.card_json_object.getString("masked_card_number"));
					expiry_date_tv.setText(SendMoney3.card_json_object.getString("card_expiry_date"));
					cardownername_tv.setText("");
					cardno="";
				} catch (JSONException e) {
					e.printStackTrace();
					finish();
					startActivity(getIntent());
				}
			}
			else{
				cardnumber_tv.setText(SendMoney3.card_number_st);
				expiry_date_tv.setText(SendMoney3.card_expiry_date_st);
				cardownername_tv.setText("");
				cardno="";
			}


			/*cardnumber_tv.setText(cardArray.get(SendMoney3.pos).getCardno());
			expiry_date_tv.setText(cardArray.get(SendMoney3.pos).getExtDate());
			cardownername_tv.setText(cardArray.get(SendMoney3.pos).getOwner_name());
			cardno=cardArray.get(SendMoney3.pos).getCardno();
			name=cardArray.get(SendMoney3.pos).getOwner_name();*/
			
		}else if(SendMoney3.pay_type.equals("bank")){
			bank_details_ly.setVisibility(View.VISIBLE);
			bank_name.setText(bankArray.get(SendMoney3.pos - 1).getbankname());
			bank_number.setText(bankArray.get(SendMoney3.pos - 1).getaccountno());
			client_bank_id = bankArray.get(SendMoney3.pos -1).getclientbankid();
			bankno=bankArray.get(SendMoney3.pos -1).getaccountno();
			name=bankArray.get(SendMoney3.pos -1).getbankname();
		}
		else if(SendMoney3.pay_type.equals("ewallet")){
			current_bal_ly.setVisibility(View.VISIBLE);
			//current_bal_tv.setText(SendMoney1_Frag.base+" "+ Common_data.getPreferences(getApplicationContext(), "balance"));
			current_bal_tv.setText(SendMoney1_Frag.base+" "+ new PrefManager<String>(getApplicationContext()).get("balance",""));

			//email= Common_data.getPreferences(getApplicationContext(), "email");
			email = new PrefManager<String>(getApplicationContext()).get("email","");
		}
		
	}

	
	@Override
	protected void onStop() {
		//Common_data.alertPinDialog(SendMoney4.this);
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
