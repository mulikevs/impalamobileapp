package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends Activity implements OnClickListener {

	TextView login_bt;
	Button forgot_bt, update_pwd;
	EditText getemailid_ed;
	String emailid;
	EditText code_et, pwd_et, cnfrm_pwd_et;
	ProgressDialog dialog;

	ForgotPassword activity = this;
	private String userId;
    public static String token = null;
    public static int token_expiry = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgot_password);
		xmlid();
		getdata();

	}

	private void getdata() {
		emailid = getemailid_ed.getText().toString();
	}

	private void xmlid() {
		login_bt = (TextView) findViewById(R.id.login_bt);
		forgot_bt = (Button) findViewById(R.id.send_verification_code);
		getemailid_ed = (EditText) findViewById(R.id.getemailid_ed);

		code_et = (EditText) findViewById(R.id.verifiaction_code);
		pwd_et = (EditText) findViewById(R.id.new_password);
		cnfrm_pwd_et = (EditText) findViewById(R.id.confirm_password);
		update_pwd = (Button) findViewById(R.id.update_password);

		login_bt.setOnClickListener(this);
		forgot_bt.setOnClickListener(this);
		update_pwd.setOnClickListener(this);

	}

	@Override
	public void onClick(View yash) {

		switch (yash.getId()) {
		case R.id.login_bt:

			Intent intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
			break;

		case R.id.send_verification_code:

			getdata();
			if (validation()) {
				registerOnServer();
			}
			break;

		case R.id.update_password:

			String code = code_et.getText().toString().trim();
			String pwd = pwd_et.getText().toString();
			String cnfpwd = cnfrm_pwd_et.getText().toString();

			if (code.length() == 0 || pwd.length() == 0 || cnfpwd.length() == 0) {

				Toast.makeText(getApplicationContext(), "Fill all Details",Toast.LENGTH_SHORT).show();
			}
			else if (!pwd.equals(cnfpwd)) {
				Toast.makeText(getApplicationContext(),"Passwords do not match", Toast.LENGTH_SHORT).show();
			} 
			else if(pwd.length()<6){
				Toast.makeText(getApplicationContext(),"Password length should be a minimum of 6 characters", Toast.LENGTH_SHORT).show();
			}
			else{
				Log.d("verification_code", code);
				Log.d("new_password", pwd);
				Log.d("confirm_new_password", cnfpwd);
				updatePassword(code, pwd, cnfpwd);
			}
			
			break;

		default:
			break;
		}

	}


	private void updatePassword(String code, String pwd, String confirm_pwd) {
		
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {

			object.put("verification_code", code);
			object.put("new_password", pwd);
			object.put("confirm_new_password", confirm_pwd);

			params.put("request", object.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("auth/updatePassword", params, new PasswordUpdateHandler(), token);
	}

	class PasswordUpdateHandler extends AsyncHttpResponseHandler {

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			Log.d("onStart", "onStart");
			Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onFinish() {
			super.onFinish();
			Log.d("onFinish", "onFinish");
			if (dialog.isShowing())
				dialog.dismiss();
		}

		@Override
		public void onStart() {
			super.onStart();
			if (activity != null && !activity.isFinishing()) {
				Log.d("onStart", "onStart");
				dialog = ProgressDialog.show(activity, null, "Loading .. ");
			}
		}

		@Override
		@Deprecated
		public void onSuccess(String result) {
			super.onSuccess(result);
			Log.d("onSuccess", "onSuccess");
			if (result.length() > 0) {
				try {

					JSONObject json = new JSONObject(result);

					boolean b = false;
					int resultCode = json.getInt("code");
					if(1001 == resultCode) {
						b = true;
					}
                    String msg = json.getString("message");
					if (b) {
						String message = json.getString("message");

						Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
						Intent i = new Intent(activity, Login.class);
						startActivity(i);
						overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
					}
					else if(1030 == resultCode){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(ForgotPassword.this);
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
					else {
						String message = json.getString("message");

						Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

					}
				} catch (Exception e) {

				}

			}
		}

	}

	private void registerOnServer() {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			System.out.println("email" + emailid);

			object.put("email", emailid);

			params.put("request", object.toString());

			Log.d("request", params.toString());

		}

		catch (JSONException e) {
            Log.d("JSONException", e.toString());
            Toast.makeText(getBaseContext(),"Error occured.",Toast.LENGTH_SHORT).show();
		}

		RestHttpClient.postParams("auth/forgotPassword", params, new ForgotPasswordHandler());
	}

	class ForgotPasswordHandler extends AsyncHttpResponseHandler {

		@SuppressWarnings("static-access")
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			if (activity != null && !activity.isFinishing()) {
				Log.d("onStart", "onStart");
				dialog = new ProgressDialog(activity).show(activity, null,
						"Loading .. ");
			}
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {
			// TODO Auto-generated method stub
			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);

				try {
					JSONObject jsonObject = new JSONObject(result);
					boolean b = false;
					int resultCode = jsonObject.getInt("code");
					String msg = jsonObject.getString("message");
					if(1001 == resultCode) {
                        b = true;
                    }
                    String message = jsonObject.getString("message");

					if (b) {
                        token = jsonObject.getString("token");
                        token_expiry = jsonObject.getInt("expiry");
						
						Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
						code_et.setEnabled(true);
						code_et.requestFocus();
						pwd_et.setEnabled(true);
						cnfrm_pwd_et.setEnabled(true);
						update_pwd.setEnabled(true);
					}
					else if(1030 == resultCode){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(ForgotPassword.this);
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

					if (!b) {
						
						Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
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
			super.onFailure(statusCode, error, content);
			Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private boolean validation() {

		String Emaildegi = "Please enter your E-mail Address";

		boolean flag = false;

		if (emailid.length() == 0) {
			
			Toast.makeText(ForgotPassword.this, Emaildegi, Toast.LENGTH_SHORT).show();
		}

		else {
			flag = true;
		}
		return flag;
	}

}
