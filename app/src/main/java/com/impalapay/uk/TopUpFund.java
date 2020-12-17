package com.impalapay.uk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.impalapay.common.RestHttpClient;

public class TopUpFund extends Fragment{
	View view;
	String balance = "";
	Spinner cardname_sp;
	EditText amount_et,curruny_et,cvc_et;
	TextView cardnumber_tv,valid_month_tv,cardownername_tv,crnt_bal_topup_tv,no_attached_card_tv;
	String Data,cuurency_st,purpose_st;
	int cardlength;
//	ArrayList<String> cardname_al= new ArrayList<String>();
//	ArrayList<String> account_number_al= new ArrayList<String>();
//	ArrayList<String> expyear_al= new ArrayList<String>();
//	ArrayList<String> expmonth_al= new ArrayList<String>();
//	ArrayList<String> ownername_al= new ArrayList<String>();
	//ArrayList<String> cvc_al= new ArrayList<String>();
	ArrayList<String> coded_account_number_card_al= new ArrayList<String>();
	LinearLayout  card_detail_ly,f_ly,s_ly;
	ArrayList<String>currency_al= new ArrayList<String>();
	JSONArray carddetails_array;
	String cardNumber = "";
	String userId = "";
	ProgressDialog dialog;
	
	String otp;
	ArrayList<String> bankname_al = new ArrayList<String>();
	ArrayList<String> banknumber_al = new ArrayList<String>();
	
	//private String[] currency_arr = {"USD", "KES", "INR","LKR", "CAD", "AUD", "BRL","CNY","JPY" };
	//int cardpostion;
	
	
	boolean emailflag=false;
	ArrayAdapter<String> currencyAdapter;
	String cardname_st;
	Button upload_found_to_ewallet_btn;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.upload_fund_frg, container, false);

		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	

	getXmlId();
	String emailotp= Common_data.getPreferences(getActivity(), "emailotp");
	
	if(emailotp.equals("1"))
		emailflag=true;
	else
		emailflag=false;
	
	Common_data.setupUI(view.findViewById(R.id.hide), getActivity());
	
	getdatafromshardprf();
	
	
	
	 Map<String, String> currencies= Common_data.getAvailableCurrencies();
	
	  for (String country : currencies.keySet()) {
            String currencyCode = currencies.get(country);
        
            currency_al.add(currencyCode);
        }
	  
	
	  Collections.sort(currency_al);
	  currency_al=new ArrayList<String>(new LinkedHashSet<String>(currency_al));
	  
	  
	/*ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, currency_al);
	currencyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
	curruny_sp.setAdapter(currencyAdapter);
	*/
	
/*	ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, purpose_arr);
	purposeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
	purpose_sp.setAdapter(purposeAdapter);
	
	purpose_sp.setOnItemSelectedListener(this);
	
	curruny_sp.setOnItemSelectedListener(this);*/
	
/*	if(carddetails_array.length()>0)
	{
	cardname_sp.setOnItemSelectedListener(this);
	
	card_detail_ly.setVisibility(View.VISIBLE);
	f_ly.setVisibility(View.VISIBLE);
	s_ly.setVisibility(View.VISIBLE);
	no_attached_card_tv.setVisibility(View.GONE);
	}
	
	else if(carddetails_array.length()==0)
	{
	//card_detail_ly.setVisibility(View.INVISIBLE);
		f_ly.setVisibility(View.GONE);
		s_ly.setVisibility(View.GONE);
		no_attached_card_tv.setVisibility(View.VISIBLE);
	}*/
	
	}
	
	
	@Override
	public void onStop() {
		//	Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	
	
	private void getdatafromshardprf()
	{
		crnt_bal_topup_tv.setText(Sawapay_Main_Screen.base+" "+ Common_data.getPreferences(getActivity(), "balance"));
		Data= Common_data.getPreferences(getActivity(), "data");
		userId = Common_data.getPreferences(getActivity(), "userid");
		Log.i("Data for card",""+Data);
		
		try 
		{
			JSONObject jsonObject=new JSONObject(Data);
			JSONArray array=jsonObject.getJSONArray("data");
			JSONObject object=array.getJSONObject(0);	// TODO Auto-generated method stub
			
			/*carddetails_array=object.getJSONArray("carddetails");
			Log.d("Card Length", ""+carddetails_array.length());
			Log.d("carddetails_array", ""+carddetails_array);
			cardlength=carddetails_array.length();
			
			
			if(carddetails_array.length()>0)
			{
		
			for(int i=0; i<carddetails_array.length();i++)
			{
				JSONObject carddetails_object=carddetails_array.getJSONObject(i);
			
				String cardna=carddetails_object.getString("cardname");
				cardname_al.add(cardna);
				Log.d("cardname_al", ""+cardname_al);
				
				
				String  account_number_=carddetails_object.getString("cardno");
				account_number_al.add(account_number_);
				Log.d("account_number_al", ""+account_number_al);
				
				String expyear_=carddetails_object.getString("expyear");
				expyear_al.add(expyear_);
				Log.d("expyear_al", ""+expyear_al);
				
				
				String expmonth_=carddetails_object.getString("expmonth");
				expmonth_al.add(expmonth_);
				Log.d("expmonth_al", ""+expmonth_al);
				
				
				String ownername_=carddetails_object.getString("ownername");
				ownername_al.add(ownername_);  
				cvc_al.add(carddetails_object.getString("cvc"));
				Log.d("ownername_al", ""+ownername_al);
			}
		for(int i=0; i<carddetails_array.length();i++)
			{
				String acc_num=account_number_al.get(i);
				acc_num.length();
				StringBuffer sb=new StringBuffer(acc_num);  
				sb.replace(1,4,"xxx");
				sb.replace(4,5," ");
				sb.replace(5,9,"xxxx");
				sb.replace(9,10," ");
				sb.replace(10,14,"xxxx");
				sb.replace(14,15," ");
				Log.d("After Replace", ""+sb);
				String coded_account_number_card_=sb.toString();
				coded_account_number_card_al.add(coded_account_number_card_);
				Log.d("coded_account_number_card", ""+coded_account_number_card_al);
			}
		
			
			}
			else{
			}*/
			
			JSONArray bankDetails_array=object.optJSONArray("bankdetails");
			
			
			if(bankDetails_array.length()>0){
				for(int i=0;i<bankDetails_array.length();i++){
					
					JSONObject bankobj=bankDetails_array.optJSONObject(i);
					
					bankname_al.add(bankobj.optString("bank_name"));
					banknumber_al.add(bankobj.getString("accountno"));
				}
			}
			
			//Log.d("size bankDetails_array", bankDetails_array.length()+"");
			callspinner();
			
		}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
	}

	private void callspinner() 
	{
		if(bankname_al.size()>0)
		{
		
		currencyAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankname_al);
		currencyAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		cardname_sp.setAdapter(currencyAdapter);
		
		}
		Log.d("size banknumber", banknumber_al.size()+" "+bankname_al.size());
	}

	private void getXmlId() {
		//purpose_sp=(Spinner)view.findViewById(R.id.purpose_sp);
		cardname_sp=(Spinner)view.findViewById(R.id.cardname_sp);
		amount_et=(EditText)view.findViewById(R.id.amount_et);
		crnt_bal_topup_tv=(TextView) view.findViewById(R.id.crnt_bal_topup_tv);
		no_attached_card_tv = (TextView)view.findViewById(R.id.no_attached_card_tv);
		//curruny_sp=(Spinner)view.findViewById(R.id.curruny_sp);
		
		card_detail_ly=(LinearLayout)view.findViewById(R.id.card_detail_ly);
		f_ly=(LinearLayout)view.findViewById(R.id.f_ly);
		s_ly=(LinearLayout)view.findViewById(R.id.s_ly);
		cardnumber_tv=(TextView)view.findViewById(R.id.cardnumber_tv);
		valid_month_tv=(TextView)view.findViewById(R.id.expiry_date_tv);
		cardownername_tv=(TextView)view.findViewById(R.id.cardownername_tv);
		cvc_et=(EditText)view.findViewById(R.id.cvc_et);
		upload_found_to_ewallet_btn=(Button) view.findViewById(R.id.upload_found_to_ewallet_btn);
		upload_found_to_ewallet_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
				
				if(validation()){
					cardNumber=banknumber_al.get(bankname_al.indexOf(cardname_sp.getSelectedItem()));
					if(emailflag)
						uploadFundToEWallet();
					else
						showOTPDialog("e");
				}
				
				
				
			}

			
		});
	}

	
	
	private void uploadFundToEWallet() {
		 JSONObject data = new JSONObject();
		 RequestParams params = new RequestParams();
		 try {
			
			 data.put( "cvv","" );
			 data.put("card_number", cardNumber);
			 data.put("amount", amount_et.getText().toString());
			 data.put("userid", userId);
			 
		} catch (Exception e) {
			// TODO: handle exception
		}
		 
		 params.put("request", data.toString() );
		 Log.i("TopUpwallet", data.toString());
			RestHttpClient.postParams("topupEwallet", params, new TopUpWalletResponseHandler() );
	}

	private boolean validation() {
		String amout=amount_et.getText().toString();
		
		if(bankname_al.size()==0){
			Toast.makeText(getActivity(), "Please Attach Bank"	, Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(amout.length()==0){
			Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_SHORT).show();
			return false;
		}
		/*else if(cvc_et.getText().toString().length()==0){
			cvc_et.setError("Enter CVC");
			return false;
		}
		else if(!cvc_et.getText().toString().equals(cvc_al.get(cardpostion))){
			cvc_et.setError("Wrong CVC");
			return false;
		}*/
		else{
			return true;
		}
	}
	
	
	

	/*private void getdataandsetdata(int postion){
		
	String	coded_account_number_card=coded_account_number_card_al.get(postion);
	cardnumber_tv.setText(""+coded_account_number_card);
		
	String	expyear=expyear_al.get(postion);
	
	String	expmonth=expmonth_al.get(postion);
	
	String monthchar="";
	
	if(expmonth.equalsIgnoreCase("1") || expmonth.equalsIgnoreCase("01") )
	{
		monthchar ="Jan";
	}
	else if(expmonth.equalsIgnoreCase("2")|| expmonth.equalsIgnoreCase("02") )
	{
		monthchar ="Feb";
	}
	else if(expmonth.equalsIgnoreCase("3")|| expmonth.equalsIgnoreCase("03") )
	{
		monthchar ="March";
	}
	
	else if(expmonth.equalsIgnoreCase("4")|| expmonth.equalsIgnoreCase("04") )
	{
		monthchar ="April";
	}
	
	else if(expmonth.equalsIgnoreCase("5")|| expmonth.equalsIgnoreCase("05") )
	{
		monthchar ="May";
	}
	
	else if(expmonth.equalsIgnoreCase("6")|| expmonth.equalsIgnoreCase("06") )
	{
		monthchar ="June";
	}
	
	else if(expmonth.equalsIgnoreCase("7")|| expmonth.equalsIgnoreCase("07") )
	{
		monthchar ="July";
	}
	
	else if(expmonth.equalsIgnoreCase("8")|| expmonth.equalsIgnoreCase("08") )
	{
		monthchar ="Aug";
	}
	else if(expmonth.equalsIgnoreCase("9")|| expmonth.equalsIgnoreCase("09") )
	{
		monthchar ="Sept";
	}
	else if(expmonth.equalsIgnoreCase("10"))
	{
		monthchar ="Oct";
	}
	
	else if(expmonth.equalsIgnoreCase("11"))
	{
		monthchar ="Nov";
	}
	else if(expmonth.equalsIgnoreCase("12"))
	{
		monthchar ="Dec";
	}
	*/
	
	//expiry_date_tv.setText(""+monthchar+" - "+expyear);
	
	//String	ownername=ownername_al.get(postion);
	
	//cardownername_tv.setText(""+ownername);
	
	//}


	
	private class TopUpWalletResponseHandler extends AsyncHttpResponseHandler{
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			dialog=new ProgressDialog(getActivity()).show(getActivity(), null, "Loading .. ");
			
		}
		@Override
		@Deprecated
		public void onSuccess(String response) {
			// TODO Auto-generated method stub
			super.onSuccess(response);
			try {
				JSONObject data = new JSONObject(response);
				Log.d("json topup", data.toString());
				String message = data.optString("message");
				boolean b=data.optBoolean("result");
				if(b){
					
					//crnt_bal_topup_tv.setText(Sawapay_Main_Screen.base+" "+bal);
					amount_et.setText("");
					cvc_et.setText("");
					//Common_data.setPreference(getActivity(), "balance",""+bal);
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			dialog.dismiss();
		}
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			// TODO Auto-generated method stub
			super.onFailure(error);
			
		}
	}
	
	
	public void showOTPDialog(String status){
		
		final Dialog d = new Dialog(getActivity());
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.otp_dialog);
		d.setCancelable(false);
		d.show();
		
		EditText otp_et = (EditText) d.findViewById(R.id.otp_et);

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
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
			}
		});
		
	
}

private void resendEmail() {
	
	RequestParams params = new RequestParams();
	JSONObject object = new JSONObject();

	try {
		object.put("email", Common_data.getPreferences(getActivity(), "email"));
		
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
			dialog = ProgressDialog.show(getActivity(), "", "Please Wait...");
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
					
					Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
				}
				else {
					
					Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
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


	
}
