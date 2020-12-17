package com.impalapay.uk;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class Setting_Frag extends Fragment implements OnClickListener {

	View view;
	ImageView basecurrency_tv;
	String old_pin, re_new_pin, new_pin, basecurrency_st, convertcurrency_st,
			user_id, password, pin, push, base, convert, old_password,
			new_password, res_new_password, type_pin_st, retype_pin_st;
	ImageButton change_password_ib, change_pin_ib, transfer_limit_ib;
	Spinner basecurrency_sp, convertcurrency_sp;
	ArrayList<String> currency_al = new ArrayList<String>();
	EditText new_pin_et, type_pin_et, retype_pin, type_new_pin_et,
			type_pin_re_et, res_new_password_et, old_password_et,
			new_password_et, re_new_pin_et, old_pin_et;
	ImageView discard_im, confirm_pas_im, discard_pin, confirm_pin;
	Boolean change_pass_bl = false, validationforpin_bl = false;
	String special = "!@#$%^&*()_";
	ProgressDialog dialog;
	Button save;
	LinearLayout share_ly, hide_menu;

	LinearLayout about_Sawapay_ly, change_pwd_ly, transfer_limit_ly, securitypin_chnge_ly;

	// SettingsActivity activity=this;
	TextView about_Sawapay;
	int change = 0;
	String notifystatus = "0";
	Switch switchPin, switchSigninRem;
	String cur;
	String pattern = ".*[" + Pattern.quote(special) + "].*";
	String switchStatus;
String changeDialog="no";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_settings, container, false);
		return view;
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		user_id = Common_data.getPreferences(getActivity(), "userid");
		base = Common_data.getPreferences(getActivity(), "base");
		getXmlId();

		switchStatus = Common_data.getPreferences(getActivity(), "switch");

		Map<String, String> currencies = Common_data.getAvailableCurrencies();
		for (String country : currencies.keySet()) {
			String currencyCode = currencies.get(country);

			cur = cur + "," + currencyCode;
			currency_al.add(currencyCode);
			currency_al.add(base);
		}
		if (!currency_al.contains("KES"))
			currency_al.add("KES");

		Collections.sort(currency_al);

		currency_al = new ArrayList<String>(new LinkedHashSet<String>(
				currency_al));

		ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.custom_spinner_text, currency_al);
		currencyAdapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		basecurrency_sp.setAdapter(currencyAdapter);
		convertcurrency_sp.setAdapter(currencyAdapter);

		password = Common_data.getPreferences(getActivity(), "password");
		pin = Common_data.getPreferences(getActivity(), "pin");
		push = Common_data.getPreferences(getActivity(), "push");
		base = Common_data.getPreferences(getActivity(), "base");
		convert = Common_data.getPreferences(getActivity(), "convert");

		switchSigninRem.setThumbDrawable(getResources().getDrawable(
				R.drawable.switch_thumb_shape_gray));
		switchSigninRem.setTrackDrawable(getResources().getDrawable(
				R.drawable.switch_track_shape_gray));

		if (switchStatus.equals("true")) {
			switchPin.setThumbDrawable(getResources().getDrawable(
					R.drawable.switch_thumb_shape));
			switchPin.setTrackDrawable(getResources().getDrawable(
					R.drawable.switch_track_shape_blue));
			switchPin.setChecked(true);
		} else {
			switchPin.setThumbDrawable(getResources().getDrawable(
					R.drawable.switch_thumb_shape_gray));
			switchPin.setTrackDrawable(getResources().getDrawable(
					R.drawable.switch_track_shape_gray));
			switchPin.setChecked(false);

		}
		basecurrency_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				basecurrency_st = String.valueOf(basecurrency_sp
						.getSelectedItem());
				base = basecurrency_st;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		convertcurrency_sp
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						convertcurrency_st = String.valueOf(convertcurrency_sp
								.getSelectedItem());
						convert = convertcurrency_st;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		int pos = currency_al.indexOf(base);
		int pos1 = currency_al.indexOf("KES");

		basecurrency_sp.setSelection(pos);
		convertcurrency_sp.setSelection(pos1);
		Common_data.setPreference(getActivity(), "first_time_pin", "0");

		switchPin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean b) {
				if (b) {
					Toast.makeText(getActivity(), "Now Click on save Button",Toast.LENGTH_SHORT).show();
					switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
					switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
					notifystatus = "1";
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getActivity());
					builder.setMessage("Security Pin Off ")
							.setCancelable(false)
							.setPositiveButton("Save",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											switchPin.setChecked(false);
											notifystatus = "0";
											Toast.makeText(getActivity(),"Now Click on save Button",Toast.LENGTH_SHORT).show();
											switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
											switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
										}
									})
							.setNegativeButton("Cancel Changes",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											dialog.cancel();
											switchPin.setChecked(true);
											notifystatus = "1";
											switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
											switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
										}
									});

					AlertDialog alertDialog = builder.create();
					alertDialog.show();
				}
			}
		});

		switchSigninRem
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean b) {
						if (b) {
							switchSigninRem.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
							switchSigninRem.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
						} else {
							switchSigninRem.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
							switchSigninRem.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
						}
					}
				});

	}

	
	private void getXmlId() {

		share_ly = (LinearLayout) view.findViewById(R.id.share_ly);
		hide_menu = (LinearLayout) view.findViewById(R.id.hide_menu);

		about_Sawapay_ly = (LinearLayout) view.findViewById(R.id.about_Sawapay_ly);
		// noti_rem_ly = (LinearLayout) view.findViewById(R.id.noti_rem_ly);
		// alert_rem_ly = (LinearLayout) view.findViewById(R.id.alert_rem_ly);
		change_pwd_ly = (LinearLayout) view.findViewById(R.id.change_pwd_ly);
		transfer_limit_ly = (LinearLayout) view.findViewById(R.id.transfer_limit_ly);
		securitypin_chnge_ly = (LinearLayout) view.findViewById(R.id.securitypin_chnge_ly);

		change_password_ib = (ImageButton) view.findViewById(R.id.change_password_ib);
		transfer_limit_ib = (ImageButton) view.findViewById(R.id.transfer_limit_ib);
		change_pin_ib = (ImageButton) view.findViewById(R.id.change_pin_ib);
		basecurrency_sp = (Spinner) view.findViewById(R.id.basecurrency_sp);
		convertcurrency_sp = (Spinner) view.findViewById(R.id.convertcurrency_sp);
		about_Sawapay = (TextView) view.findViewById(R.id.about_Sawapay);
		save = (Button) view.findViewById(R.id.save_setting);

		switchPin = (Switch) view.findViewById(R.id.switchPin);
		switchSigninRem = (Switch) view.findViewById(R.id.switchSigninRem);

		about_Sawapay.setOnClickListener(this);
		save.setOnClickListener(this);
		change_password_ib.setOnClickListener(this);
		transfer_limit_ib.setOnClickListener(this);
		change_pin_ib.setOnClickListener(this);
		share_ly.setOnClickListener(this);

		basecurrency_sp.setEnabled(false);
		convertcurrency_sp.setEnabled(false);

		about_Sawapay_ly.setOnClickListener(this);
		// noti_rem_ly.setOnClickListener(this);
		// alert_rem_ly.setOnClickListener(this);
		change_pwd_ly.setOnClickListener(this);
		transfer_limit_ly.setOnClickListener(this);
		securitypin_chnge_ly.setOnClickListener(this);

	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View p) {
		switch (p.getId()) {

		case R.id.change_password_ib:
			changePasswordDialog();
			break;

			case R.id.transfer_limit_ib:
				transferLimit();
				break;

		case R.id.change_pin_ib:

			changepinDialog();
			break;

		case R.id.save_setting:

			registerOnServer();
			break;
		case R.id.about_Sawapay:
			Intent intent6 = new Intent(getActivity(), AboutSawaPay.class);
			startActivity(intent6);
			getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
			break;

		case R.id.share_ly:
			showshareDialog();
			break;

		case R.id.about_Sawapay_ly:
			/*Intent in = new Intent(getActivity(), AboutSawaPay.class);
			startActivity(in);
			getActivity().overridePendingTransition(R.anim.slide_in,R.anim.slide_out);*/
			/*Intent about = new Intent(Intent.ACTION_VIEW);
			about.setData(Uri.parse(getString(R.string.about_url)));
			startActivity(about);*/
			break;

		case R.id.noti_rem_ly:
			if (switchSigninRem.isChecked()) {
				switchSigninRem.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
				switchSigninRem.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
			} else {
				switchSigninRem.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
				switchSigninRem.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
			}
			break;

		case R.id.alert_rem_ly:

			if (switchPin.isChecked()) {
				Common_data.setPreference(getActivity(), "switch", "true");
				Toast.makeText(getActivity(), "Security Pin Alert ON",Toast.LENGTH_SHORT).show();
				switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
				switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Security Pin Off ")
				.setCancelable(false)
						.setPositiveButton("Save",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										switchPin.setChecked(false);
										Common_data.setPreference(getActivity(), "switch", "false");
										switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
										switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
									}
								})
						.setNegativeButton("Cancel Changes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										dialog.cancel();
										switchPin.setChecked(true);
										Common_data.setPreference(getActivity(), "switch", "true");
										switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
										switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
									}
								});

				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}

			break;

		case R.id.change_pwd_ly:
			changePasswordDialog();
			break;

			case R.id.transfer_limit_ly:
				transferLimit();

		case R.id.securitypin_chnge_ly:
			changepinDialog();

			break;

		}
	}
	@Override
	public void onStart() {
		super.onStart();
		try {
			notifystatus = switchPin.isChecked()?"1":"0";
			changeDialog="no";
			MainActivity.hideSoftKeyboard(getActivity(),hide_menu);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showshareDialog() {

		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		share.putExtra(Intent.EXTRA_SUBJECT, "Sawapay");
		share.putExtra(
				Intent.EXTRA_TEXT,
				"Send Money to Kenya with SawaPay. Instant transfers. Zero Fees. \n https://www.sawa-pay.com/");
		startActivity(Intent.createChooser(share, "Share Sawapay using"));
	}

	private void changePasswordDialog() {

		final Dialog na = new Dialog(getActivity());
		na.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		na.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		na.setContentView(R.layout.change_password_dialog);
		na.setCancelable(false);
		na.show();

		old_password_et = (EditText) na.findViewById(R.id.old_password_et);
		new_password_et = (EditText) na.findViewById(R.id.new_password_et);
		res_new_password_et = (EditText) na
				.findViewById(R.id.res_new_password_et);
		discard_im = (ImageView) na.findViewById(R.id.discard_im);
		confirm_pas_im = (ImageView) na.findViewById(R.id.confirm_pas_im);
		discard_im.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.start_fragment(getActivity(), new Setting_Frag(), "Setting_Frag");
				na.dismiss();


			}
		});

		confirm_pas_im.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				res_new_password = res_new_password_et.getText().toString();
				old_password = old_password_et.getText().toString();
				new_password = new_password_et.getText().toString();
				Log.d("password", "" + password);
				Log.d("old_password", "" + old_password);

				Common_data.setupUI(hide_menu, getActivity());
				if (password.equals(old_password)) {

					if (validation()) {
						// if (passvalidation()) {
						change_pass_bl = true;

						Log.d("change_pass_bl", "" + change_pass_bl);
						Log.d("new_password", "" + new_password);
						password = new_password;
						na.dismiss();
						changeDialog = "yes";
						changePassword();
						// }
					}
				} else {
					Toast.makeText(getActivity(), "Please enter your valid old password ", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void transferLimit(){
		Intent i = new Intent(getActivity(), TransferLimit.class);
		startActivity(i);
	}

	private void changepinDialog() {

		if (pin.equals("")) {
			final Dialog change_pin_dia = new Dialog(getActivity());
			change_pin_dia.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			change_pin_dia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			change_pin_dia.setContentView(R.layout.first_time_pin_dialog);
			change_pin_dia.setCancelable(false);
			//change_pin_dia.show();

			type_pin_et = (EditText) change_pin_dia.findViewById(R.id.type_pin_et);
			retype_pin = (EditText) change_pin_dia.findViewById(R.id.retype_pin);
			discard_pin = (ImageView) change_pin_dia.findViewById(R.id.discard_pin);
			confirm_pin = (ImageView) change_pin_dia.findViewById(R.id.confirm_pin);

			discard_pin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					change_pin_dia.dismiss();
					MainActivity.start_fragment(getActivity(), new Setting_Frag(), "Setting_Frag");
				}
			});

			confirm_pin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					type_pin_st = type_pin_et.getText().toString();
					retype_pin_st = retype_pin.getText().toString();

					if (validationforpin()) {

						validationforpin_bl = true;
						Log.d("validationforpin_bl", "" + validationforpin_bl);
						pin = type_pin_st;
						Log.d("type_pin_st", "" + type_pin_st);
						Log.d("retype_pin_st", "" + retype_pin_st);
						change_pin_dia.dismiss();
						changeDialog="yes";
						registerOnServer();
					}
				}
			});
		}

		else if (!pin.equals("")) {
			final Dialog change_pin_dia = new Dialog(getActivity());
			change_pin_dia.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			change_pin_dia.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			change_pin_dia.setContentView(R.layout.change_pin_dialog);
			change_pin_dia.setCancelable(false);
			//change_pin_dia.show();

			old_pin_et = (EditText) change_pin_dia.findViewById(R.id.old_pin_et);
			new_pin_et = (EditText) change_pin_dia.findViewById(R.id.new_pin_et);
			re_new_pin_et = (EditText) change_pin_dia.findViewById(R.id.re_new_pin_et);

			discard_pin = (ImageView) change_pin_dia.findViewById(R.id.discard_pin);
			confirm_pin = (ImageView) change_pin_dia.findViewById(R.id.confirm_pin);
			confirm_pin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					old_pin = old_pin_et.getText().toString();
					re_new_pin = re_new_pin_et.getText().toString();
					new_pin = new_pin_et.getText().toString();
					Log.d("old_pin", "" + old_pin);
					Log.d("pin", "" + pin);

					if (old_pin.equals(pin)) {
						if (pinvalidation()) {

							pin = new_pin;
							Log.d("old_pin", "" + old_pin);
							Log.d("re_new_pin", "" + re_new_pin);
							Log.d("new_pin", "" + new_pin);
							change_pin_dia.dismiss();
							registerOnServer();
						}
					} else {
						Toast.makeText(getActivity(), "Please Check Your PIN",
								Toast.LENGTH_LONG).show();
					}
				}
			});

			discard_pin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					change_pin_dia.dismiss();
					MainActivity.start_fragment(getActivity(), new Setting_Frag(), "Setting_Frag");
					MainActivity.hideSoftKeyboard(getActivity(),hide_menu);
				}
			});

		}

	}

	private void registerOnServer() {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();
		try {
			object.put("userid", user_id);
			object.put("pin", pin);
			object.put("password", password);
			object.put("base", base);
			object.put("convert", convert);
			object.put("push", push);
			object.put("alertnotifystatus", notifystatus);
			params.put("request", object.toString());
			Log.d("request", params.toString());

		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("updatesetting", params,
				new RegistrationHandler());
	}

	private void changePassword() {

		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();
		try {
			object.put("userid", user_id);
			object.put("old_password", old_password);
			object.put("new_password", new_password);
			object.put("confirm_new_password", res_new_password);
			params.put("request", object.toString());
			Log.d("request", params.toString());
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("auth/changePassword", params,
				new ChangePasswordHandler(), Login.token);
	}

	private boolean validationforpin() {
		String REQUIRED_MSG4 = "Both Passwords don't Match";
		String REQUIRED_MSG5 = "Please enter the PIN";
		String REQUIRED_MSG = "Please enter the Re-type PIN";
		boolean flag = false;

		if (type_pin_st.length() == 0) {
			Toast.makeText(getActivity(), REQUIRED_MSG5, Toast.LENGTH_SHORT).show();
		} else if (retype_pin_st.length() == 0) {

			Toast.makeText(getActivity(), REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		} else if (!type_pin_st.equals(retype_pin_st)) {

			Toast.makeText(getActivity(), REQUIRED_MSG4, Toast.LENGTH_SHORT).show();
		} else {
			flag = true;
		}
		return flag;
	}

	private boolean validation() {
		String REQUIRED_MSG = "Please enter the new password";
		String REQUIRED_MSG1 = "Please enter the confirmation for new password";
		String REQUIRED_MSG2 = "New password does not match its confirmation.";
		String REQUIRED_MSG4 = "A password must have at least 6 characters.";
		boolean flag = false;

		if (new_password.length() == 0) {

			Toast.makeText(getActivity(), REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		} else if (res_new_password.length() == 0) {

			Toast.makeText(getActivity(), REQUIRED_MSG1, Toast.LENGTH_SHORT).show();
		} else if (!new_password.equals(res_new_password)) {

			Toast.makeText(getActivity(), REQUIRED_MSG2, Toast.LENGTH_SHORT).show();
		} else if (new_password.length() < 6) {

			Toast.makeText(getActivity(), REQUIRED_MSG4, Toast.LENGTH_SHORT).show();
		} else if (res_new_password.length() < 6) {

			Toast.makeText(getActivity(), REQUIRED_MSG4, Toast.LENGTH_SHORT).show();
		} else {
			flag = true;
		}
		return flag;
	}

	private boolean pinvalidation() {
		String REQUIRED_MSG4 = "Both Password does not Match";
		String REQUIRED_MSG5 = "Please enter the PIN";
		String REQUIRED_MSG = "Please enter the Re-type PIN";
		String REQUIRED_MSG3 = "Please enter minmum 5 characters the PIN";

		boolean flag = false;

		if (re_new_pin.length() == 0) {

			Toast.makeText(getActivity(), REQUIRED_MSG5, Toast.LENGTH_SHORT).show();
		}

		else if (new_pin.length() < 5) {

			Toast.makeText(getActivity(), REQUIRED_MSG3, Toast.LENGTH_SHORT).show();
		} else if (new_pin.length() == 0) {

			Toast.makeText(getActivity(), REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		} else if (!re_new_pin.equals(new_pin)) {

			Toast.makeText(getActivity(), REQUIRED_MSG4, Toast.LENGTH_SHORT).show();
		} else {
			flag = true;
		}
		return flag;
	}

	@SuppressLint("NewApi")
	class ChangePasswordHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			Log.d("onStart", "onStart");
			dialog = new ProgressDialog(getActivity()).show(getActivity(),null, "Changing Password...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);
			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject json = new JSONObject(result);
					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;
					String msg=json.optString("message");
					if (b) {
						Common_data.setPreference(getActivity(), "password", new_password);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(getActivity());
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText("Your password has been changed successfully.");

						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("Ok");
						btn_ok.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {

								if (d != null && d.isShowing()){
									d.dismiss();
								}
								//MainActivity.start_fragment(getActivity(), new Setting_Frag());
							}
						});
						d.show();
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
					else {
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(getActivity())
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										changePasswordDialog();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
										MainActivity.start_fragment(getActivity(), new Setting_Frag(), "Setting_Frag");
									}
								}).show();
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



	@SuppressLint("NewApi")
	class RegistrationHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			Log.d("onStart", "onStart");
			dialog = new ProgressDialog(getActivity()).show(getActivity(),null, "Loading .. ");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);
			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject jsonObject = new JSONObject(result);
					boolean b = jsonObject.getBoolean("result");
					String msg = jsonObject.optString("message");
					if (b) {
						
							if (notifystatus.equals("0") && changeDialog.equals("no")) {
								switchPin.setChecked(false);
								Common_data.setPreference(getActivity(), "switch", "false");
								switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
								switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
							} else if(notifystatus.equals("1") && changeDialog.equals("no")){
								switchPin.setChecked(true);
								Common_data.setPreference(getActivity(), "switch", "true");
								switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
								switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
						}
						
						Common_data.setPreference(getActivity(), "password", password);
						Common_data.setPreference(getActivity(), "pin", pin);
						Common_data.setPreference(getActivity(), "base", base);
						Common_data.setPreference(getActivity(), "convert", convert);
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
						MainActivity.start_fragment(getActivity(), new Setting_Frag(), "Setting_Frag");
					}
					if (!b) {
						
							if (notifystatus.equals("0")  && changeDialog.equals("no")) {
								switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape));
								switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_blue));
								switchPin.setChecked(true);
							} else if(notifystatus.equals("1") && changeDialog.equals("no")){
								switchPin.setThumbDrawable(getResources().getDrawable(R.drawable.switch_thumb_shape_gray));
								switchPin.setTrackDrawable(getResources().getDrawable(R.drawable.switch_track_shape_gray));
								switchPin.setChecked(false);
	
							}
						
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
