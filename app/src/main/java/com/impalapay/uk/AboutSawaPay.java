package com.impalapay.uk;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

public class AboutSawaPay extends Activity implements OnClickListener {

	TextView aboutsawapay, back_tv;
	TextView done;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_sawapay);

		ActionBar ab = getActionBar();
		ab.hide();

		aboutsawapay = (TextView) findViewById(R.id.text_aboutSawaPay);
		back_tv = (TextView) findViewById(R.id.back_tv);
		done = (TextView) findViewById(R.id.done_tv);
		done.setVisibility(View.GONE);

		back_tv.setOnClickListener(this);

		getContent();
	}

	private void getContent() {
		// TODO Auto-generated method stub
		JSONObject data = new JSONObject();
		RequestParams params = new RequestParams();

		params.put("request", data.toString());
		RestHttpClient.postParams("aboutUs", params,new GetContentResponseHandler());

	}

	private class GetContentResponseHandler extends AsyncHttpResponseHandler {
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String response) {
			// TODO Auto-generated method stub
			super.onSuccess(response);
			Log.i("response", "" + response);
			String content = "";
			try {

				JSONObject obj = new JSONObject(response);
				JSONArray data = obj.optJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject obj_ = data.optJSONObject(i);
					content = obj_.optString("about_sawapay");
				}

				aboutsawapay.setText(content);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onFailure(Throwable arg0) {
			// TODO Auto-generated method stub
			super.onFailure(arg0);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.back_tv:
			Common_data.setPreference(getApplicationContext(), "alertshow", "false");
			finish();
			break;

		}
	}

	@Override
	protected void onStop() {
		Common_data.alertPinDialog(AboutSawaPay.this);
		super.onStop();

	}

	@Override
	public void onBackPressed() {
		Common_data.setPreference(getApplicationContext(), "alertshow", "false");
		finish();
		
	}
}
