package com.impalapay.uk;

import java.util.ArrayList;

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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.impalapay.common.RestHttpClient;

public class WithdrawFund extends Fragment {

	View view;
	TextView currnt_bal_tv, currency_tv;
	Spinner card_sp;
	EditText amount;
	Button withdraw_btn;
	String selcardno;

	private String pData;
	boolean emailflag=false;
	ArrayAdapter<String> cardAdapter;
	//ArrayList<String> cardname_al = new ArrayList<String>();
	//ArrayList<String> cardnumber_al = new ArrayList<String>();
	
	String otp;
	ArrayList<String> bankname_al = new ArrayList<String>();
	ArrayList<String> banknumber_al = new ArrayList<String>();
	
	private int cardlength;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.withdraw_fund,container, false);

		return view;
	}
	@Override
	public void onStop() {
		//	Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		Common_data.setupUI(view.findViewById(R.id.hide), getActivity());
		String emailotp= Common_data.getPreferences(getActivity(), "emailotp");
		
		if(emailotp.equals("1"))
			emailflag=true;
		else
			emailflag=false;
		getdatafromshardprf();

	}

	private void init() {

		currnt_bal_tv = (TextView) view.findViewById(R.id.current_balnce_withdraw);
		currnt_bal_tv.setText(Sawapay_Main_Screen.base+" "+ Common_data.getPreferences(getActivity(), "balance"));

		currency_tv = (TextView) view.findViewById(R.id.currency_tv_withdraw);

		amount = (EditText) view.findViewById(R.id.amount_withdraw_et);

		card_sp = (Spinner) view.findViewById(R.id.card_select_sp_withdraw);
		card_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int pos, long arg3) {
				selcardno=banknumber_al.get(bankname_al.indexOf(card_sp.getSelectedItem().toString()));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		withdraw_btn = (Button) view.findViewById(R.id.withdraw);

		withdraw_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(bankname_al.size() ==0)
				{
					Toast.makeText(getActivity(), "Please Attach Bank", Toast.LENGTH_SHORT).show();
				}else if(amount.getText().toString().length()==0){
					Toast.makeText(getActivity(), "Enter Amount", Toast.LENGTH_SHORT).show();
				}else if(!emailflag){
						showOTPDialog("e");
					
				}else{
					withdrawFund();
				}
			}

			
		});
		
		
	/*	amount.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
			   // DecimalFormat form = new DecimalFormat("0.00");
			   // String FormattedText=form.format(form);
			   // amount.setText(FormattedText);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}}); */
	/*	amount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				String ss=s.toString();
				if(ss.toString().contains(".")){
					String t=ss.substring(ss.lastIndexOf(".")+1);
					Log.d("decimal", t);
					if(t.length()>2)
					{
						String finalvalue=ss.substring(ss.lastIndexOf("."));
								String dt=t.substring(1,2);
						amount.setText(finalvalue+dt);
						InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    	imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
						
					}
				}
			}
			
			
			
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});*/
	}
	
	private void withdrawFund() {
		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try 
		{
			
			
			object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
			object.put("actno", selcardno);
		
			object.put("amount", amount.getText().toString());
		
			params.put("request", object.toString());
			RestHttpClient.postParams("withdrawlFund", params, new WithdrawFoundHandler());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public class WithdrawFoundHandler extends AsyncHttpResponseHandler{
		ProgressDialog p;

		@Override
		public void onStart() {
			super.onStart();

			p = ProgressDialog.show(getActivity(), "", "Loading...");
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);

			if (content.length() > 0) {
				try {

					JSONObject json = new JSONObject(content);
					boolean b = json.optBoolean("result");
					if (b) {
						JSONArray ja=json.getJSONArray("data");
						JSONObject jo=ja.getJSONObject(0);
						Common_data.setPreference(getActivity(), "balance", "" + jo.optString("balance"));
						currnt_bal_tv.setText(Sawapay_Main_Screen.base+" "+jo.optString("balance"));
						Toast.makeText(getActivity(), json.optString("message"), Toast.LENGTH_SHORT).show();
						amount.setText("");
						card_sp.setSelection(0);
					} 
					else{
						Toast.makeText(getActivity(), json.optString("message"), Toast.LENGTH_SHORT).show();
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
			Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	
	private void getdatafromshardprf() {
		pData = Common_data.getPreferences(getActivity(), "data");

		try {
			JSONObject jsonObject = new JSONObject(pData);
			JSONArray array = jsonObject.getJSONArray("data");
			JSONObject object = array.getJSONObject(0);

		//	JSONArray carddetails_array = object.getJSONArray("carddetails");
			/*JSONArray carddetails_array = object.getJSONArray("bankdetails");

			cardlength = carddetails_array.length();

			if (carddetails_array.length() > 0) {
				for (int i = 0; i < carddetails_array.length(); i++) {
					JSONObject carddetails_object = carddetails_array
							.getJSONObject(i);

					String cardna = carddetails_object.getString("bank_name");
					cardname_al.add(cardna);
					cardnumber_al.add(carddetails_object.getString("accountno"));
				}
			}
			if (cardname_al.size() > 0) {
				cardAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, cardname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				card_sp.setAdapter(cardAdapter);
			}
			*/
			JSONArray bankDetails_array=object.optJSONArray("bankdetails");
			
			
			if(bankDetails_array.length()>0){
				for(int i=0;i<bankDetails_array.length();i++){
					
					JSONObject bankobj=bankDetails_array.optJSONObject(i);
					
					bankname_al.add(bankobj.optString("bank_name"));
					banknumber_al.add(bankobj.getString("accountno"));
				}
			}
			
			if (bankname_al.size() > 0) {
				cardAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankname_al);
				cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				card_sp.setAdapter(cardAdapter);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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
