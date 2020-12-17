package com.impalapay.uk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.squareup.picasso.Picasso;

public class Deals_detail_frag extends Fragment implements OnClickListener {

	
	View view;
	TextView itemPrice,description_tv,item_name_tv;
	EditText coupon_edit;
	Button applyCoupon,activateCoupon;
	ImageView iv;
	String userid,dealsid,discription,imgurl,code;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view=inflater.inflate(R.layout.deals_detail, container, false); 
	
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getXmlId();
		getBundleData();
		//Common_data.setupUI(view.findViewById(R.id.hide),getActivity());
		
	}
	private void getXmlId() {
	
		iv=(ImageView) view.findViewById(R.id.deal_icon);
		itemPrice=(TextView) view.findViewById(R.id.itemPrice);
		coupon_edit=(EditText) view.findViewById(R.id.coupon_edit);
		applyCoupon=(Button) view.findViewById(R.id.applyCoupon);
		activateCoupon=(Button) view.findViewById(R.id.activate_dealButton);
		description_tv=(TextView) view.findViewById(R.id.description);
		item_name_tv=(TextView) view.findViewById(R.id.item_name);
		//applyCoupon.setOnClickListener(this);
		activateCoupon.setOnClickListener(this);
		
	}
	@Override
	public void onStop() {
			//Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	
	private void getBundleData() {
		
		userid= Common_data.getPreferences(getActivity(), "userid");
		
		dealsid = getArguments().getString("id");
		imgurl=getArguments().getString("img");
		//price = getArguments().getString("price");
		discription= getArguments().getString("dis");
		code=getArguments().getString("coupon");
		//item_name= getArguments().getString("itemname");
		
		iv.setTag(imgurl);
		description_tv.setText(discription);
		description_tv.setEnabled(false);
		//itemPrice.setText(price);
		//item_name_tv.setText(item_name);
		Picasso.with(getActivity()).load(imgurl).into(iv);
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.activate_dealButton:
			
			/*if(coupon_edit.getText().toString().trim().length()==0){
					Toast.makeText(getActivity(), "Enter Coupon Code !!!", Toast.LENGTH_SHORT).show();
			}
			else{
				applyCoupon();
			}*/
			ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData data = ClipData.newPlainText("", code);
			clipBoard.setPrimaryClip(data);
			Toast.makeText(getActivity(), "Your Coupon Code is Copied", Toast.LENGTH_SHORT).show();
			activateCoupon.setText(code);
			activateCoupon.setEnabled(false	);
			break;

		default:
			break;
		}
		
	}

	private void applyCoupon() {
			
		
		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try {
			object.put("userid", userid);
			object.put("dealid", dealsid);
			object.put("couponcode", coupon_edit.getText().toString());
			
			params.put("request", object.toString());
			
			Log.d("deals_detail", params.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		RestHttpClient.postParams("applyCoupon", params, new ApplyCouponHandler());
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
						
						itemPrice.setText(jobj.optString("price"));
						
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						
						applyCoupon.setEnabled(false);
						coupon_edit.setEnabled(false);
						
						ClipboardManager clipBoard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
						ClipData data = ClipData.newPlainText("", "");
						clipBoard.setPrimaryClip(data);
					}else{
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						
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
			dialog=ProgressDialog.show(getActivity(), "", "Please Wait...");
		}
		
		
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			if(dialog.isShowing())
				dialog.dismiss();
			Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
}
