package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.TransactionHistoryModel;
import com.impalapay.uk.adapters.MainPageTranscationAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Sawapay_Main_Screen extends Fragment implements OnClickListener {
    public static double exResult_double = 0.00;
	View view;
	Button paybill, send_money,airtime_main;
	TextView user_name_tv;
	TextView pdetailsendvalue, pdetailrecevievalue,crnt_bal_topup_tv_utility,title_transcation;
	ListView list_send_money;

	ArrayList<TransactionHistoryModel> array;
	MainPageTranscationAdapter adapter;
	
	public static String base;
	public static String convert;
//	public static String crncy_snd_sybl="";
//	public static String crncy_recv_sybl="";
	TextView emptyview;
	public static String exRate;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.sawapay_main_frag, container, false);

		
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			init();
			getCurrentConversionRate();
			//Toast.makeText(getActivity(), Common_data.getDateAndTime(), Toast.LENGTH_SHORT).show();
			Log.d("data and time", Common_data.getDateAndTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void init() throws Exception {

		base = Common_data.getPreferences(getActivity().getApplicationContext(), "base");
		convert = Common_data.getPreferences(getActivity().getApplicationContext(), "convert");
		
		//crncy_snd_sybl = Currency.getInstance(base).getSymbol().toString();
		//crncy_recv_sybl = Currency.getInstance(convert).getSymbol().toString();

		
		array =new ArrayList<TransactionHistoryModel>();
		
		pdetailrecevievalue=(TextView) view.findViewById(R.id.pdetailrecevievalue);
		pdetailsendvalue=(TextView) view.findViewById(R.id.pdetailsendvalue);
		
		title_transcation=(TextView) view.findViewById(R.id.title_transcation);
		title_transcation.setVisibility(View.GONE);
		crnt_bal_topup_tv_utility=(TextView) view.findViewById(R.id.crnt_bal_topup_tv_utility);
		crnt_bal_topup_tv_utility.setText(base+" "+ Common_data.getPreferences(getActivity(), "balance"));
		
		emptyview=(TextView) view.findViewById(R.id.empty);
		
		list_send_money = (ListView) view.findViewById(R.id.list_send_money);
		
		paybill = (Button) view.findViewById(R.id.pay_bill_main);
		send_money = (Button) view.findViewById(R.id.send_money_main);
		
		airtime_main=(Button) view.findViewById(R.id.airtime_main);
		
		user_name_tv = (TextView) view.findViewById(R.id.user_name_tv);
		String f = Common_data.getPreferences(getActivity(), "fname");
		String l = Common_data.getPreferences(getActivity(), "lname");

		user_name_tv.setText(f + " " + l);
		paybill.setOnClickListener(this);
		send_money.setOnClickListener(this);
		airtime_main.setOnClickListener(this);
	}

	@Override
	public void onClick(View p) {

		switch (p.getId()) {
		case R.id.pay_bill_main:
			SendMoney1_Frag sf1 = new SendMoney1_Frag();

			Bundle args = new Bundle();
			args.putBoolean("checked", true);
			sf1.setArguments(args);

			FragmentManager fs = getActivity().getSupportFragmentManager();
			FragmentTransaction f = fs.beginTransaction();
			f.replace(R.id.lk_profile_fragment, sf1);

			f.commit();
			break;
		case R.id.send_money_main:

			SendMoney1_Frag sf = new SendMoney1_Frag();

			Bundle a = new Bundle();
			a.putBoolean("checked", false);
			sf.setArguments(a);

			FragmentManager fs1 = getActivity().getSupportFragmentManager();
			FragmentTransaction f1 = fs1.beginTransaction();
			f1.replace(R.id.lk_profile_fragment, sf);

			f1.commit();

			break;
			
		case R.id.airtime_main:

			AirTime_Frag af = new AirTime_Frag();
			
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.lk_profile_fragment, af);

			ft.commit();

			break;
		default:
			break;
		}
	}

	private void getHistroyDetail() {
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
			params.put("request", object.toString());

		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("auth/transactionHistory", params,new HistroyDetailHandler(), Login.token);
	}

	class HistroyDetailHandler extends AsyncHttpResponseHandler {

		ProgressDialog pdialog;
		int p = 0;

		@Override
		public void onStart() {
			super.onStart();
			pdialog = ProgressDialog.show(getActivity(), "", "Loading...");
		}

		@Override
		public void onSuccess(String content) {
			super.onSuccess(content);
			Log.i("Response",""+content);
			int start=0,end=0;
			if (content.length() > 0) {

				try {

					JSONObject json = new JSONObject(content);

					boolean b = false;
					int response_code = json.getInt("code");
					String msg = json.getString("message");
					if(1001 == response_code)
						b = true;

					if (b) {
						JSONArray jarray = json.optJSONArray("data");
						Log.d("array", jarray.toString());
						
						if(jarray.length()>4)
							start=jarray.length()-1;
						else
							start=jarray.length()-1;
						
						if(jarray.length()>4)
							end=start-3;
						else
							end=0;
						Log.d("Position variable",jarray.length()+" "+ start+" "+end);
						int i=0;
						for (i = 0; i < jarray.length(); i++) {

							JSONObject obj = jarray.optJSONObject(i);
							
							TransactionHistoryModel model = new TransactionHistoryModel();

							
							model.setTxnCode(obj.optString("id"));
							model.setSend(obj.optString("amount_sent"));
							model.setReceipient_name(obj.optString("recipient"));
							model.setDate(obj.optString("date"));

							//if(!model.getDel_method().equalsIgnoreCase("withdrawl")){
								array.add(model);
							//}
							
						}
						
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(getActivity());
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("Ok");
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
					title_transcation.setVisibility(View.VISIBLE);
					adapter = new MainPageTranscationAdapter(getActivity(), array);
					list_send_money.setAdapter(adapter);
					list_send_money.setEmptyView(emptyview);

				} catch (Exception e) {
						e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			super.onFinish();
			if (pdialog.isShowing())
				pdialog.dismiss();

		}
	}

	private void getCurrentConversionRate() {
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();
		try {
			object.put("cur_from", base);
			object.put("cur_to", convert);

			params.put("request", object.toString());
			Log.d("CurrentConversionRate", params.toString());
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("request", params.toString());

		RestHttpClient.postParams("auth/getConversionRate", params,new ConversionRateHandler(),Login.token);
	}

	class ConversionRateHandler extends AsyncHttpResponseHandler {

		ProgressDialog pdialog;

		@Override
		public void onStart() {
			super.onStart();
			pdialog = ProgressDialog.show(getActivity(), "", "Loading...");
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);

			if (content.length() > 0) {

				try {

					JSONObject json = new JSONObject(content);
					Log.d("Response array", json.toString());
					boolean b = false;
					int response_code = json.getInt("code");
					String msg = json.getString("message");
					if(1001 == response_code)
						b = true;

					if (b) {
						JSONArray array = json.optJSONArray("data");
                        Log.d("data array", array.toString());
						JSONObject jobj = array.optJSONObject(0);

                        exResult_double = Double.parseDouble(jobj.optString("amount"));

						exRate=String.valueOf(exResult_double);
						pdetailsendvalue.setText(base + " 1.0");
						pdetailrecevievalue.setText( " "+convert+" "+String.format("% .2f",exResult_double));


					}
					else if(1010 == response_code){
						final Dialog d=new Dialog(getActivity());
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
								Intent i = new Intent(getActivity(), Login.class);
								startActivity(i);
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(getActivity());
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("OK");
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
			if (pdialog.isShowing())
				pdialog.dismiss();
			getHistroyDetail();

		}

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog(getActivity(), new Sawapay_Main_Screen());
		}
	}

	public void networkErrorDialog(Activity act, final Fragment f) {

		final Dialog d = new Dialog(act);
		d.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setCancelable(false);
		d.setContentView(R.layout.network_error);

		Button tryagain = (Button) d.findViewById(R.id.try_again);
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
