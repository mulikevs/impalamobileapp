package com.impalapay.uk;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.DealsItemModel;
import com.impalapay.uk.adapters.DealsItemAdapter;

public class Deals_frag extends Fragment {

	View view;
	ListView list;
	ArrayList<DealsItemModel> array;
	DealsItemAdapter adapter;
	TextView emptyview;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.activity_deals, container, false); // activity_deals

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getXmlId();

		try {
			RestHttpClient.post("deals", new GetDealsHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	@Override
	public void onStop() {
			//Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	private void getXmlId() {
		array = new ArrayList<DealsItemModel>();
		list = (ListView) view.findViewById(R.id.list_deals);
		emptyview=(TextView) view.findViewById(R.id.empty);
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long longs) {

				Deals_detail_frag ddf = new Deals_detail_frag();

				Bundle b = new Bundle();
				b.putString("id", array.get(pos).getDeals_id());
				b.putString("price", array.get(pos).getPrice());
				b.putString("img", array.get(pos).getImgUrl());
				b.putString("dis", array.get(pos).getDiscription());
				b.putString("itemname", array.get(pos).getTitle());
				b.putString("coupon", array.get(pos).getCoupon_code());

				ddf.setArguments(b);

				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.lk_profile_fragment, ddf);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
	}

	class GetDealsHandler extends AsyncHttpResponseHandler {

		ProgressDialog dialog;

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);

			if (content.length() > 0) {

				try {

					JSONObject json = new JSONObject(content);
					boolean b = json.optBoolean("result");

					if (b) {

						JSONArray jarray = json.getJSONArray("data");
						for (int i = 0; i < jarray.length(); i++) {

							JSONObject jo = jarray.getJSONObject(i);

							DealsItemModel model = new DealsItemModel();

							model.setDeals_id(jo.optString("id"));
							model.setTitle(jo.optString("title"));
							model.setImgUrl(jo.optString("image"));
							model.setDiscription(jo.optString("description"));
							model.setPrice(jo.optString("price"));
							model.setDiscount_fix(jo.optString("discount_fix"));
							model.setDiscount_per(jo.optString("discount_per"));
							model.setDiscount_etopup(jo.optString("discount_etopup"));
							model.setExpire(jo.optString("expire"));
							model.setCoupon_code(jo.optString("coupon_code"));
							model.setUser_limit(jo.optString("user_limit"));
							model.setUser_limit(jo.optString("users_limit"));
							model.setDate(jo.optString("timestamp"));

							array.add(model);

						}
					}
					
				} catch (Exception e) {

				}
				
			}
			adapter = new DealsItemAdapter(getActivity(), array);
			list.setAdapter(adapter);
			list.setEmptyView(emptyview);

			
		}

		@Override
		public void onStart() {
			super.onStart();

			dialog = ProgressDialog.show(getActivity(), "", "Loading...");
		}

		@Override
		public void onFinish() {
			super.onFinish();
			if (dialog.isShowing())
				dialog.dismiss();
		}

	}

}
