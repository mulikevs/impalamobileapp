package com.impalapay.uk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contact_us_frag extends Fragment  {

	View view;
	TextView address, email, number, contact_terms_and_condition, contact_privacy_policy;
	Spinner country_sp;
	String Countrywithcode;
	String country;
	ArrayList<String> country_al = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_contact_us, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		xmlgetId();
		//Common_data.setupUI(view.findViewById(R.id.hide),getActivity());
		
		Countrywithcode = Common_data.getPreferences(getActivity(), "countrywithcode");
		parseJson(Countrywithcode);
		
		
		

	}
	@Override
	public void onStop() {
			//Common_data.alertPinDialog(getActivity());
		super.onStop();
	}
	private void parseJson(String jsonarray) {

		if (jsonarray != null) {
			try {
				JSONObject jsonObject = new JSONObject(jsonarray);
				boolean b = false;
				int response_code = jsonObject.getInt("code");
				if(1001 == response_code)
					b = true;
				if (b) {
					JSONArray array = jsonObject.getJSONArray("data");

					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.getJSONObject(i);
						String country = object.getString("country");
						country_al.add(country);

						callspinner();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private void callspinner() {

		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.custom_spinner_text, country_al);
		countryAdapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		country_sp.setAdapter(countryAdapter);
		country_sp.setSelection(91);
		country_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int postion, long arg3) {

				Log.i("hello", "dg");
				country = String.valueOf(country_sp.getSelectedItem());
				Log.i("country", "" + country);

				getCountryDetails(country);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	protected void getCountryDetails(String country) {
		// TODO Auto-generated method stub
		JSONObject data = new JSONObject();
		RequestParams params = new RequestParams();
		try {
			data.put("countryname", country);
		} catch (Exception e) {
			// TODO: handle exception
		}
		params.put("request", data.toString());
		RestHttpClient.postParams("contactUs", params,
				new GetCountryResponseHandler());
	}

	private void xmlgetId() {

		country_sp = (Spinner) view.findViewById(R.id.country_sp);
		address = (TextView) view.findViewById(R.id.countryAddress);
		email = (TextView) view.findViewById(R.id.countryEmail);
		number = (TextView) view.findViewById(R.id.countryNo);

		contact_terms_and_condition = (TextView) view.findViewById(R.id.contact_terms_and_condition);
		contact_privacy_policy = (TextView) view.findViewById(R.id.contact_privacy_policy);

		address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.site_url)));
				startActivity(i);
			}
		});
		final String email_addr = getString(R.string.contact_email);
		email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = "sylvestermwambeke@gmail.com";
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto",email_addr, null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject" +
						"");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
			}
		});

		number.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String toDial="+254724938184";
				startActivity(new Intent(Intent.ACTION_CALL,
						Uri.parse("tel:"+getString(R.string.contact_phone))));
			}
		});

		contact_terms_and_condition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.terms_url)));
				startActivity(i);
			}
		});

		contact_privacy_policy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.privacy_url)));
				startActivity(i);
			}
		});
	}

	private class GetCountryResponseHandler extends AsyncHttpResponseHandler {
		@Override
		@Deprecated
		public void onSuccess(String response) {
			// TODO Auto-generated method stub
			super.onSuccess(response);
			Log.i("response", "" + response);
			String address_ = "";
			String email_ = "";
			String contactno_ = "";
			try {

				JSONObject obj = new JSONObject(response);
				JSONArray data = obj.optJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject obj_ = data.optJSONObject(i);
					address_ = obj_.optString("address");
					email_ = obj_.optString("email");
					contactno_ = obj_.optString("contactno");
				}

				if (address_.equals("")) {
					address.setText("Not Available");
					email.setText("Not Available");
					number.setText("Not Available");
				} else {
					address.setText(address_);
					email.setText(email_);
					number.setText(contactno_);

				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
		}

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			// TODO Auto-generated method stub
			super.onFailure(error);
		}
	}
}
