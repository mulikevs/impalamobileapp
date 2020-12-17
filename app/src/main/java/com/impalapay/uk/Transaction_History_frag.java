package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.TransactionHistoryModel;
import com.impalapay.uk.adapters.TransactionAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Transaction_History_frag extends Fragment implements
		OnRefreshListener {

	View view;

	ListView mListView;
	ArrayList<TransactionHistoryModel> array;
	TextView tvremainamount,emptyView;
	TransactionAdapter smsListAdapter;
	SwipeRefreshLayout swipeView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.transaction_history, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();

		getHistroyDetail();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
				getHistroyDetail();

			swipeView.postDelayed(new Runnable() {
				@Override
				public void run() {
					swipeView.setRefreshing(false);
				}
			}, 1000);
		};
	};

	@Override
	public void onStop() {
			//Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	private void init() {

		tvremainamount = (TextView) view.findViewById(R.id.tvremainamount);
		tvremainamount.setText(Sawapay_Main_Screen.base+" "+ Common_data.getPreferences(getActivity(), "balance"));

		mListView = (ListView) view.findViewById(R.id.history_listview);
		array = new ArrayList<TransactionHistoryModel>();
		emptyView=(TextView) view.findViewById(R.id.empty_history);
		
		swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_view);
		swipeView.setOnRefreshListener(this);
		/*swipeView.setColorScheme(android.R.color.holo_blue_dark,android.R.color.holo_green_dark,
				android.R.color.holo_red_dark, 
				android.R.color.holo_orange_dark); */
		
		
		mListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				
				  int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ?0 : mListView.getChildAt(0).getTop();
				  swipeView.setEnabled((topRowVerticalPosition >= 0));
			}
		});
	}

	private void getHistroyDetail() {

		try {

			RequestParams params = new RequestParams();
			JSONObject object = new JSONObject();
			Log.d("request", "running");

			object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
			params.put("request", object.toString());

			Log.d("request", params.toString());

			//RestHttpClient.postParams("balancehistory", params,new HistroyDetailHandler());
			RestHttpClient.postParams("auth/transactionHistory", params,new HistroyDetailHandler(), Login.token);
		}

		catch (JSONException e) {
			e.printStackTrace();
		}

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

			if (content.length() > 0) {

				try {

					JSONObject json = new JSONObject(content);

					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;

					String msg = json.getString("message");
					if (b) {
						array.clear();
						JSONArray jarray = json.optJSONArray("data");
						Log.i("sly", "data\n" + jarray.toString());

						for (int k = 0; k < jarray.length(); k++) {

							JSONObject obj = jarray.optJSONObject(k);

							TransactionHistoryModel model = new TransactionHistoryModel();


							model.setRefID(obj.optString("id"));
							model.setTxnCode(obj.optString("txn_code"));
							model.setSend(obj.optString("disbursement_currency")+" "+ obj.optString("amount_received"));
							model.setReceipient_name(obj.optString("recipient"));
							model.setDate(obj.optString("date"));
							model.setStatus(obj.optString("status"));
							model.setDelivery_status(obj.optString("dlr_status"));
							model.setDel_method(obj.optString("disbursement_method"));

							//if(!model.getDel_method().equalsIgnoreCase("withdrawl")){
							array.add(model);
							//}

						}

					}
					else if(1020 == response_code){
						final Dialog d=new Dialog(getActivity());
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("Ok");
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

					smsListAdapter = new TransactionAdapter(getActivity(), array);
					mListView.setAdapter(smsListAdapter);
					mListView.setEmptyView(emptyView);

				} catch (Exception e) {

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

	@Override
	public void onRefresh() {
		

		swipeView.postDelayed(new Runnable() {

			@Override
			public void run() {
				swipeView.setRefreshing(true);
				handler.sendEmptyMessage(0);
			}
		}, 1000);
	}
	
}
