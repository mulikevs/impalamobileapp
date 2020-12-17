package com.impalapay.uk;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;

public class Customer_Support_frag extends Fragment implements OnClickListener{
	
	
	View view;
	
	Button customersubmit_bt;
	EditText sub,msg;
	ProgressDialog pdialog1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.activity_customer_support, container, false);
		
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		xmlgetId();	
		
		Common_data.setupUI(view.findViewById(R.id.hide), getActivity());
	}
	@Override
	public void onStop() {
			//Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	private void xmlgetId() {
		sub = (EditText)view.findViewById(R.id.sub_edit);
		msg = (EditText)view.findViewById(R.id.message_et);
		customersubmit_bt=(Button)view.findViewById(R.id.customer_bt);
		customersubmit_bt.setOnClickListener(this);		
	}

	@Override
	public void onClick(View p) {
		
		switch (p.getId()) {
		
		case R.id.customer_bt:
			if (validation())
			{
			 customer_submit();
			}
			break;
		default:
			break;
		}
	}
	
	private boolean validation() {
		String subjectstring = "Please enter the subject";
		String messagestring = "Please enter the message";
		
		boolean flag = false;
		
		if (sub.length() == 0) 
		{
			Toast.makeText(getActivity(), subjectstring, Toast.LENGTH_SHORT).show();
		} else if (msg.length() == 0) 
		{
			Toast.makeText(getActivity(), messagestring, Toast.LENGTH_SHORT).show();
		} 
		
		else 
		{
			flag = true;
		}
		return flag;
		// TODO Auto-generated method stub
		//return false;
	}



	private void customer_submit()
	{
		
		try 
		{
			RequestParams params=new RequestParams();
			JSONObject obj=new JSONObject();
			obj.put("userid", Common_data.getPreferences(getActivity(), "userid"));
			obj.put("subject", sub.getText().toString());
			obj.put("message", msg.getText().toString());
			params.put("request", obj.toString());
			Log.d("request", params.toString());

			RestHttpClient.postParams("coustomerSupport", params, new SupportHandler());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	class SupportHandler extends AsyncHttpResponseHandler
	{

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			// TODO Auto-generated method stub
			super.onFailure(error);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			super.onFinish();
			if(pdialog1.isShowing())
				pdialog1.dismiss();
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			
			pdialog1=ProgressDialog.show(getActivity(), null, "Loading .. ");
			
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			// TODO Auto-generated method stub
			super.onSuccess(content);
			if(content.length()>0){
				
				
				try {
					JSONObject json=new JSONObject(content);
					boolean b=json.optBoolean("result");
					String msg1=json.optString("message");
					if(b){
						Toast.makeText(getActivity(), msg1, Toast.LENGTH_SHORT).show();
						sub.setText("");
						msg.setText("");}
					else
					{
						Toast.makeText(getActivity(), msg1, Toast.LENGTH_SHORT).show();
					}
	
				} catch (Exception e) {
					
				}
			}
		}
				
	}
	

}
