package com.impalapay.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.uk.MainActivity;
import com.impalapay.uk.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Common_data
{
	/*********** Method for getting the status of internet ********/
	/*public static final boolean isInternetOn(Context con) {
		InternetStatus internetstatus = new InternetStatus(con);
		boolean isOnline = internetstatus.isOnline();
		return isOnline;
	}
	
*/
	public static boolean isInternetOn(Context con)
	{
		boolean param;
		InternetStatus status = new InternetStatus(con);
		param = status.isOnline();
		return param;
	}

	public static Map<String, String> getAvailableCurrencies()
	{
		Locale[] locales = Locale.getAvailableLocales();

		Map<String, String> currencies = new TreeMap<String, String>();
		for (Locale locale : locales)
		{
			try
			{
				currencies.put(locale.getDisplayCountry(),Currency.getInstance(locale).getCurrencyCode());

			} catch (Exception e) {

			}
		}
		return currencies;
	}

	public static void showAccountVerifyDialog(final Activity act) {
		final Dialog d = new Dialog(act);
		d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.account_verfication_dialog);
		d.setCancelable(false);
		d.show();

		final LinearLayout accept_ly = (LinearLayout) d.findViewById(R.id.accept_ly);
		final LinearLayout reject_ly = (LinearLayout) d.findViewById(R.id.reject_ly);
		final LinearLayout verify_ly = (LinearLayout) d.findViewById(R.id.verify_ly);

		final EditText dedcuted_amount = (EditText) d.findViewById(R.id.deducted_amount_et);

		final TextView account_verify_tv = (TextView) d.findViewById(R.id.account_verify_tv);
		final TextView tv_accept = (TextView) d.findViewById(R.id.accept_tv);
		final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv);
		final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv);
		final TextView tries_tv = (TextView) d.findViewById(R.id.tries_tv);

		final ImageView iv_accept = (ImageView) d.findViewById(R.id.accept_img);
		final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img);
		final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img);


		tv_reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
					/*d.dismiss();
					
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageAccount());
					ft.addToBackStack(null);
					ft.commit();*/
				act.finish();
			}
		});

		iv_reject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageAccount());
					ft.addToBackStack(null);
					ft.commit();*/
				act.finish();
			}
		});

		iv_accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				account_verify_tv.setText(act.getString(R.string.account_varify_accept));
				dedcuted_amount.setVisibility(View.VISIBLE);
				reject_ly.setVisibility(View.GONE);
				accept_ly.setVisibility(View.GONE);
				verify_ly.setVisibility(View.VISIBLE);
				tries_tv.setVisibility(View.VISIBLE);
			}
		});

		tv_accept.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				account_verify_tv.setText(act.getString(R.string.account_varify_accept));
				dedcuted_amount.setVisibility(View.VISIBLE);
				reject_ly.setVisibility(View.GONE);
				accept_ly.setVisibility(View.GONE);
				verify_ly.setVisibility(View.VISIBLE);
				tries_tv.setVisibility(View.VISIBLE);

			}
		});

		verify_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageAccount());
					ft.addToBackStack(null);
					ft.commit();*/
				act.finish();
			}
		});
		verify_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageAccount());
					ft.addToBackStack(null);
					ft.commit();*/
				act.finish();
			}
		});

	}

	public static void setupUI(View view,final Activity act) {

		//Set up touch listener for non-text box views to hide keyboard.
		if(!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					MainActivity.hideSoftKeyboard(act,v);
					return false;
				}

			});
		}

		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				View innerView = ((ViewGroup) view).getChildAt(i);

				setupUI(innerView,act);
			}
		}
	}
	public static String MonthName(String expmonth){

		String monthchar="";

		if(expmonth.equalsIgnoreCase("1") || expmonth.equalsIgnoreCase("01") )
		{
			monthchar ="January";
		}
		else if(expmonth.equalsIgnoreCase("2")|| expmonth.equalsIgnoreCase("02") )
		{
			monthchar ="February";
		}
		else if(expmonth.equalsIgnoreCase("3")|| expmonth.equalsIgnoreCase("03") )
		{
			monthchar ="March";
		}

		else if(expmonth.equalsIgnoreCase("4")|| expmonth.equalsIgnoreCase("04") )
		{
			monthchar ="April";
		}

		else if(expmonth.equalsIgnoreCase("5")|| expmonth.equalsIgnoreCase("05") )
		{
			monthchar ="May";
		}

		else if(expmonth.equalsIgnoreCase("6")|| expmonth.equalsIgnoreCase("06") )
		{
			monthchar ="June";
		}

		else if(expmonth.equalsIgnoreCase("7")|| expmonth.equalsIgnoreCase("07") )
		{
			monthchar ="July";
		}

		else if(expmonth.equalsIgnoreCase("8")|| expmonth.equalsIgnoreCase("08") )
		{
			monthchar ="August";
		}
		else if(expmonth.equalsIgnoreCase("9")|| expmonth.equalsIgnoreCase("09") )
		{
			monthchar ="September";
		}
		else if(expmonth.equalsIgnoreCase("10"))
		{
			monthchar ="October";
		}

		else if(expmonth.equalsIgnoreCase("11"))
		{
			monthchar ="November";
		}
		else if(expmonth.equalsIgnoreCase("12"))
		{
			monthchar ="December";
		}
		return monthchar;

	}

	/************************************************/
	/***************************************************** set shared preferences **************************************************/
	public static void setPreference(Context con, String key, String value)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwambeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit();
		//editor.putString(key, value);
		//editor.putString("countrywithcode", "{\"country\":\"United States of America\",\"country_code\":\"US\"}");
        editor.putString("countrywithcode","{\"code\":1001,\"message\":\"\",\"currency\":false,\"data\":[{\"country\":\"United States of America\",\"country_code\":\"US\"}]}");

        Log.i("firstdata",value.toString());

        editor.commit();
	}


	public static void setPreference2(Context con, String key, String value)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwambeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit();
		//editor.putString(key, value);
		//editor.putString("countrywithcode", "{\"country\":\"United States of America\",\"country_code\":\"US\"}");
		editor.putString("countrywithcode","{\"code\":1001,\"message\":\"\",\"currency\":false,\"data\":[{\"country\":\"United States of America\",\"country_code\":\"US\"}]}");

		Log.i("firstdata",value.toString());

		editor.commit();
	}

	/**************************************************** get shared preferences ***************************************************/
	public static String getPreferences(Context con, String key)
	{
		SharedPreferences sharedPreferences = con.getSharedPreferences("sylvestermwambeke@gmail.com", 0);
		String value = sharedPreferences.getString(key, "0");

		Log.i("contextdata",value.toString());
		return value;
	}

	public static void Preferencesclear(Context con)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwmabeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	public static void deletePref(Context con, String key)
	{
		SharedPreferences preferences = con.getSharedPreferences("sylvestermwmabeke@gmail.com", 0);
		SharedPreferences.Editor editor = preferences.edit().remove(key);
		editor.clear();
		editor.commit();
	}

	public static String formatDate(int year, int month, int day) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy");
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static String formatDate1(int year, int month, int day) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yy");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		return sdf.format(date);
	}

	public static void closeKeyboard(Context c, IBinder windowToken) {
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(windowToken, 0);
	}


	public static void alertPinDialog(final Activity activity){
		final String pin = Common_data.getPreferences(activity, "pin");
		String switchStatus = Common_data.getPreferences(activity, "switch");

		Log.d("PIN", "" + pin);
		if (switchStatus.equals("true")) {
			if (!pin.equals("")) {
				final Dialog na = new Dialog(activity);

				na.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

				na.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				na.setContentView(R.layout.pin_active_dialog);
				na.setCancelable(false);
				final EditText active_pin_et = (EditText) na.findViewById(R.id.active_pin_et);
				final ImageView active_confirm_pin = (ImageView) na.findViewById(R.id.active_confirm_pin);
				//na.show();

				if (active_confirm_pin != null){
					active_confirm_pin.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String pin_st = active_pin_et.getText().toString();

							if (pin_st.equals(pin)) {
								na.dismiss();
							}

							else {
								Toast.makeText(activity, "Please Check Your PIN",Toast.LENGTH_LONG).show();
							}

						}
					});
				}
			}
		}

	}
	/*private void showSetPinDialog() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(
				MainActivity.this);
		builder1.setTitle("Set Security Pin");

		builder1.setCancelable(false);
		builder1.setPositiveButton("Set Pin",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						FragmentManager fm = getSupportFragmentManager();
						FragmentTransaction st = fm.beginTransaction();
						st.replace(R.id.lk_profile_fragment, new Setting_Frag());
						st.addToBackStack(null);
						st.commit();
						overridePendingTransition(R.anim.slide_in,
								R.anim.slide_out);
					}
				});
		TextView tv = new TextView(MainActivity.this);
		tv.setText("Set Your Pin (security code) From Settings");
		tv.setTextSize(18);
		tv.setPadding(15, 15, 10, 15);
		tv.setTextColor(Color.BLUE);
		builder1.setView(tv);
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}*/

	public static boolean isSimInserted(Activity act) {
		/*boolean f=false;
		try {
			TelephonyManager telMgr = (TelephonyManager) act.getSystemService(Context.TELEPHONY_SERVICE);

			if (telMgr.getSimState() != TelephonyManager.SIM_STATE_ABSENT)
				f= true;
			else
				f=false;



		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;*/

		return true;
	}

	public static String getMCCMNC(Activity act) {
		String MCCMNC="";
		int mcc=0;
		int mnc=0;
		try {
			TelephonyManager telMgr = (TelephonyManager)act.getSystemService(Context.TELEPHONY_SERVICE);

			//if (isSimInserted(act)){
			String mccmnc1 = telMgr.getNetworkOperator();
			Log.i("MCCMNC", "" + mccmnc1);

			if (mccmnc1 != null) {
				mcc = Integer.parseInt(mccmnc1.substring(0, 3));
				mnc = Integer.parseInt(mccmnc1.substring(3));
				Log.i("MCC", "" + mcc);
				Log.i("MNC", "" + mnc);

				MCCMNC = mcc + "" + mnc;
			}

			Log.i("SimState", "" + "sim present"+telMgr.getSimState()+"\nmccmnc="+MCCMNC);
			/*} else{
				Toast.makeText(act, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_LONG).show();
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return MCCMNC;
	}

	public static String getDateAndTime(){

		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

		//DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.getDefault()).format(new Date());
		return currentDateTimeString;
	}
}