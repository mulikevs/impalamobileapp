package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class AirTime_Frag extends Fragment {

	View view;
	EditText mobile_et,confirm_mobile_et;
	TextView pdetailsendvalue,pdetailrecevievalue;
	Button next;
	Spinner amount_sp;
	ArrayList<String> array=new ArrayList<String>();
	private String base,convert;
	//private String crncy_snd_sybl,crncy_recv_sybl;
	private double exResult_double,exResult_double_rev;
	
	//STATIC DATA
	static String mobile,recharge_amount,debit;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.sendairtime1, container, false);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		try {
			init();
			//Common_data.setupUI(view.findViewById(R.id.hide),getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getCurrentConversionRate();
	}
	private void init() throws Exception{
		
		base = Common_data.getPreferences(getActivity().getApplicationContext(), "base");
		convert = Common_data.getPreferences(getActivity().getApplicationContext(), "convert");
		
		
		
		mobile_et=(EditText) view.findViewById(R.id.mobile_number);
		confirm_mobile_et=(EditText) view.findViewById(R.id.confirm_mobile_number);
		
		pdetailsendvalue=(TextView) view.findViewById(R.id.pdetailsendvalue);
		pdetailrecevievalue=(TextView) view.findViewById(R.id.pdetailrecevievalue);
		
		amount_sp=(Spinner) view.findViewById(R.id.amount_sp);
		
		next=(Button) view.findViewById(R.id.next_air_1);
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(validation()){
					
					mobile="+254"+mobile_et.getText().toString();
					recharge_amount=amount_sp.getSelectedItem().toString();
					double de=exResult_double_rev*Integer.parseInt(recharge_amount);
					 debit = String.format("% .2f", de);
					 
				Intent i=new Intent(getActivity(),SendMoney.class);
				i.putExtra("iswhat", "airtime");
				startActivity(i);
				}
			}

			
		});
		
		array.add("5");
		array.add("10");
		array.add("20");
		array.add("50");
		array.add("100");
		array.add("200");
		array.add("250");
		array.add("500");
		array.add("1000");
		array.add("2000");
		array.add("5000");
		
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, R.id.tv_item,array);
		amount_sp.setAdapter(adapter);
		
		
		
	}
	
	
	private boolean validation() {
		String m=mobile_et.getText().toString();
		String cm=confirm_mobile_et.getText().toString();
		if(m.length()==0)
		{
			Toast.makeText(getActivity(), "Enter Mobile number", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		else if(cm.length()==0){
			Toast.makeText(getActivity(), "Enter Confirm Mobile number", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(m.length()<9){
			Toast.makeText(getActivity(), "Mobile number Should be 9 digit", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(!m.equals(cm)){
			Toast.makeText(getActivity(), "Mobile Number Not Match", Toast.LENGTH_SHORT).show();
			return false;
		}
		else {
			return true;
		}
		
	}
	
	
	private void getCurrentConversionRate() {
		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try 
		{
			object.put("cur_from", base);
			object.put("cur_to", convert);
			
			params.put("request", object.toString());
			Log.d("getCurrentConversionRate", params.toString());
		} 

		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		Log.d("request", params.toString());
	
		RestHttpClient.postParams("getConvertAmount", params, new ConversionRateHandler());
	}
class ConversionRateHandler extends AsyncHttpResponseHandler{
		
		
		ProgressDialog pdialog;
		
		
		@Override
		public void onStart() {
			super.onStart();
			pdialog=ProgressDialog.show(getActivity(), "", "Loading...");
		}
		
		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);
			
			if(content.length()>0){
				
				try {
					
					JSONObject json=new JSONObject(content);
					Log.d("Response array", json.toString());
					boolean b=json.optBoolean("result");
					
					if(b)
					{
						JSONArray array=json.optJSONArray("data");
						Log.d("data array", array.toString());
						JSONObject jobj=array.optJSONObject(0);
						
						exResult_double = Double.parseDouble(jobj.optString("amount"));
						 exResult_double_rev=1/exResult_double;
					
						 
						// pamount_entersend = "1";
						// amount_enter_dou = Double.parseDouble(pamount_entersend);
						 pdetailsendvalue.setText(Sawapay_Main_Screen.base + " 1.0 - ");
						 String finalvalue = String.format("% .2f", exResult_double);
						 pdetailrecevievalue.setText(Sawapay_Main_Screen.convert+" "+finalvalue);
						
					}
				}catch(Exception e){
					
				}
			}
		}
		@Override
		public void onFinish() {
			super.onFinish();
			if(pdialog.isShowing())
				pdialog.dismiss();
				

		}
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog(getActivity(),new AirTime_Frag());
		}
	}


public void networkErrorDialog(Activity act, final Fragment f){
	 
	 final Dialog d=new Dialog(act);
	 d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	 d.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 d.setCancelable(false);
	 d.setContentView(R.layout.network_error);
	 
	 Button tryagain=(Button) d.findViewById(R.id.try_again);
	 tryagain.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			
					FragmentManager fm = getFragmentManager();
	  				FragmentTransaction ft = fm.beginTransaction();
	  				ft.replace(R.id.lk_profile_fragment, f);
	  				ft.addToBackStack(null);
	  				ft.commit();
	  				d.dismiss();
		}
	});
	 d.show();
	 
}

	
}
