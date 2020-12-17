package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity implements View.OnClickListener {
    TextView forgot_pass_tv, singup;
    static TextView session_tv;
    Button login_bt;
    EditText password_et, username_et;
    String password, username;
    ProgressDialog dialog;
    Context context;
    CheckBox remamber_me_chk;
    View view_line;
    Login activity = this;
    View footer_view;
    View ca_footer_view;
    ImageView small_logo,imageView2;
    ImageView big_logo_ly;
    CheckBox rem_email;
    //private LinearLayout activityRootView, dynemic_footer_ly, big_logo_ly;
    private LinearLayout activityRootView, dynemic_footer_ly;

    public static String token = null;
    public static int expiry = 0;

    String country_iso2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        MainActivity.start_timer = false;
        country_iso2 = Common_data.getPreferences(Login.this, "country_iso2");
        getXmlId();


        getInputValue();
        forgot_pass_tv.setOnClickListener(this);
        //singup.setOnClickListener(this);
        login_bt.setOnClickListener(this);


        //Common_data.setPreference(getApplicationContext(), "alertshow", "true");
        new PrefManager<String>(getApplicationContext()).set("alertshow", "true");
        //	remamber_me_chk.setOnClickListener(this);
		
		/* if(Common_data.getPreferences(activity, "loginflag").equals("1"))
	        {
	        	Intent	intent = new Intent(Login.this, MainActivity.class);
				startActivity(intent);
	        }*/

    }

    private void getXmlId() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer_view = inflater.inflate(R.layout.footer, null);
        ca_footer_view = inflater.inflate(R.layout.footer_ca, null);

        view_line = findViewById(R.id.view_line);

        dynemic_footer_ly = (LinearLayout) findViewById(R.id.dynemic_footer_ly);
        Log.i("Country ISO", country_iso2);
        if (country_iso2.equalsIgnoreCase("us")) {
            dynemic_footer_ly.addView(footer_view);
        } else {
            //dynemic_footer_ly.addView(ca_footer_view);

        }

        session_tv = (TextView) findViewById(R.id.session_tv);

        rem_email = (CheckBox) findViewById(R.id.rem_email);

        password_et = (EditText) findViewById(R.id.password_et);
        username_et = (EditText) findViewById(R.id.username_et);
        forgot_pass_tv = (TextView) findViewById(R.id.forgot_pass_tv);
        //singup=(TextView)findViewById(R.id.singup);
        login_bt = (Button) findViewById(R.id.login_bt);

        //big_logo_ly = (LinearLayout) findViewById(R.id.big_logo_ly);
        //big_logo_ly = (ImageView) findViewById(R.id.big_logo_ly);

        imageView2 = (ImageView) findViewById(R.id.imageView2);


        small_logo = (ImageView) findViewById(R.id.small_logo);



/**
        activityRootView = (LinearLayout) findViewById(R.id.activehook);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > 100) {
                    // singup.setVisibility(View.GONE);
                    footer_view.setVisibility(View.GONE);
                    big_logo_ly.setVisibility(View.GONE);
                    small_logo.setVisibility(View.VISIBLE);
                    view_line.setVisibility(View.VISIBLE);
                } else {
                    // singup.setVisibility(View.VISIBLE);
                    footer_view.setVisibility(View.VISIBLE);
                    big_logo_ly.setVisibility(View.VISIBLE);
                    small_logo.setVisibility(View.GONE);
                    view_line.setVisibility(View.GONE);
                }
            }
        });**/


        String isfillemail = Common_data.getPreferences(activity, "emailfill");
        String fillemail = Common_data.getPreferences(activity, "filluser");
        if (isfillemail.equals("true")) {
            username_et.setText(fillemail);
            rem_email.setChecked(true);
            password_et.requestFocus();
        }

    }

    @Override
    public void onClick(View yash) {
        //remamber_me_chk=(CheckBox)findViewById(R.id.remamber_me_chk);
        switch (yash.getId()) {
            case R.id.forgot_pass_tv:

                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;


//		case R.id.singup:
//
//			Intent intent1=new Intent(getApplicationContext(),Registration.class);
//			startActivity(intent1);
//			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//
//			break;
//

            case R.id.login_bt:

                getInputValue();
                if (validation()) {
                    registerOnServer();
                }
                break;


            default:
                break;
        }


    }

    private void getInputValue() {
        password = password_et.getText().toString();
        username = username_et.getText().toString();
    }

    private void registerOnServer() {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            object.put("email", username);
            object.put("password", password);
            object.put("udid", " ");
            object.put("gcmid", " ");
            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("auth/login", params, new RegistrationHandler());
    }

    class RegistrationHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            super.onStart();
            if (activity != null && !activity.isFinishing()) {

                dialog = new ProgressDialog(activity).show(activity, null, "Login...");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            super.onSuccess(result);
            Log.d("response login", result);
            if (result.length() > 0) {


                try {
                    JSONObject jsonObject = new JSONObject(result);
                    //SAMPLE FULL LOGIN RESPONSE
                    //loginresponse: {"code":1001,"message":"Success","token":"8-ea7825f5c3e60109f0aaeda9403c73cf","expiry":600,
                    //"data":{
                    // "auth":{"id":"8","msisdn":"+254715290374","email":"eugenechimita41@gmail.com","email_verified":"1","msisdn_verified":"1",
                    //          "otp_num":"6770","pin":null,"push":null,"alert_notify_status":"0","daily_transfer_limit":"1000",
                    //          "monthly_transfer_limit":"2500"},
                    // "ewallet":{"balance":"0"},
                    // "profile":{"f_name":"Machette","m_name":null,"l_name":"Eugene","ssn":"XXX-XX-4444","passport_num":"585858555225885522222",
                    //          "birth_date":"1989-10-24","currency":"USD","address_line_one":"Alska one","address_line_two":"Alaska two","city":"Anchorage",
                    //          "state":"AK","country":"US","zip_code":"99501","base":"USD","convert_to":"KES"},
                    // "bank":[],
                    // "card":[{"card_ref":"3","card_status":"1","card_nickname":"Eugene Chimita","masked_card_number":"99XXXXXX99","card_cvv":"45",
                    //          "card_expiry_date":"44"}],
                    // "delivery_countries":[{"destination":"Kenya"},{"destination":"Uganda"},{"destination":"Zimbabwe"}]}}

                    Log.i("loginresponse", jsonObject.toString());

                    boolean b = false;
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
					/*
					Login has been successfull at this point we want to extract the data now..
					 */
                    if (1001 == response_code)
                        b = true;

                    if (b) {
                        session_tv.setVisibility(View.GONE);
                        token = jsonObject.getString("token");
                        expiry = jsonObject.getInt("expiry");
                        if (rem_email.isChecked()) {
                            //Common_data.setPreference(activity, "emailfill", "true");
                            //Common_data.setPreference(activity, "filluser", username);
                            new PrefManager<String>(getApplicationContext()).set("emailfill", "true");
                            new PrefManager<String>(getApplicationContext()).set("filluser", username);

                        } else {
                            //Common_data.setPreference(activity, "emailfill", "false");
                            //Common_data.setPreference(activity, "filluser", "");
                            new PrefManager<String>(getApplicationContext()).set("emailfill", "false");
                            new PrefManager<String>(getApplicationContext()).set("filluser", "");
                        }

                        //JSONArray array=jsonObject.getJSONArray("data");
                        //HERE WE SAVE TO PREFERANCE ALL THE INFORMATION THAT WE RECEIVE FROM THE LOGIN RESPONSE(data)
                        JSONObject data = jsonObject.getJSONObject("data");
                        //Common_data.setPreference(activity, "data", result);
                        new PrefManager<String>(getApplicationContext()).set("data", result);

                        //HERE WE EXTRACT THE CARDARRAY OBJECT SAVE TO SHAREDPREFERENCE ALL THE INFORMATION
                        // THAT WE RECEIVE FROM THE LOGIN RESPONSE(carddetails_array)
                        JSONArray carddetails_array = data.getJSONArray("card");
                        Log.d("card", carddetails_array.toString());
                        //Common_data.setPreference(activity, "carddetails_array", carddetails_array.toString());
                        new PrefManager<String>(getApplicationContext()).set("carddetails_array", carddetails_array.toString());

                        //String data=Common_data.getPreferences(activity, "data");

                        //HERE WE EXTRACT THE BANKARRAY OBJECT SAVE TO SHAREDPREFERENCE ALL THE INFORMATION
                        // THAT WE RECEIVE FROM THE LOGIN RESPONSE(bankdetails_array)
                        JSONArray bankdetails_array = data.getJSONArray("bank");
                        Log.d("card", carddetails_array.toString());
                        //Common_data.setPreference(activity, "bankdetails_array", bankdetails_array.toString());
                        new PrefManager<String>(getApplicationContext()).set("bankdetails_array", bankdetails_array.toString());

                        JSONObject auth = data.getJSONObject("auth");

                        JSONObject ewallet = data.getJSONObject("ewallet");

                        JSONObject profile = data.getJSONObject("profile");

                        JSONArray bank = data.getJSONArray("bank");

                        JSONArray card = data.getJSONArray("card");


                        //HERE WE EXTRACT THE DELIVERYCOUNTRIES OBJECT SAVE TO SHAREDPREFERENCE ALL THE INFORMATION
                        // THAT WE RECEIVE FROM THE LOGIN RESPONSE(delivery_countries)
                        JSONArray delivery_countries = data.getJSONArray("delivery_countries");
                        new PrefManager<String>(activity).set("delivery_countries", delivery_countries.toString());

                        //EXTRACT AUTH DATA AND SAVE IN SHARED PREFERENCES
                        new PrefManager<String>(activity).set("userid", auth.getString("id"));
                        new PrefManager<String>(activity).set("password", password);
                        new PrefManager<String>(activity).set("email", auth.getString("email"));
                        new PrefManager<String>(activity).set("phone", auth.getString("msisdn"));
                        new PrefManager<String>(activity).set("pin", auth.getString("pin"));
                        new PrefManager<String>(activity).set("push", auth.getString("push"));
                        new PrefManager<String>(activity).set("otpno", auth.getString("otp_num"));
                        new PrefManager<String>(activity).set("mobileotp", auth.getString("msisdn_verified"));
                        new PrefManager<String>(activity).set("emailotp", auth.getString("email_verified"));


                        //EXTRACT PROFILE DATA AND SAVE IN SHARED PREFERENCES
                        new PrefManager<String>(activity).set("fname", profile.getString("f_name"));
                        new PrefManager<String>(activity).set("lname", profile.getString("l_name"));
                        new PrefManager<String>(activity).set("ssn", profile.getString("ssn"));
                        new PrefManager<String>(activity).set("passport_num", profile.getString("passport_num"));
                        new PrefManager<String>(activity).set("dob", profile.getString("birth_date"));
                        new PrefManager<String>(activity).set("currency", profile.getString("currency"));
                        new PrefManager<String>(activity).set("country", profile.getString("country"));
                        new PrefManager<String>(activity).set("address", profile.getString("address_line_one"));
                        new PrefManager<String>(activity).set("address2", profile.getString("address_line_two"));
                        new PrefManager<String>(activity).set("convert", profile.getString("convert_to"));
                        new PrefManager<String>(activity).set("base", profile.getString("base"));
                        new PrefManager<String>(activity).set("city", profile.getString("city"));
                        new PrefManager<String>(activity).set("state", profile.getString("state"));
                        new PrefManager<String>(activity).set("zipcode", profile.getString("zip_code"));

                        //EXTRACT EWALLET DATA AND SAVE IN SHARED PREFERENCES
                        new PrefManager<String>(activity).set("balance", ewallet.getString("balance"));


                        String switc = auth.optString("alert_notify_status");

                        Log.d("alert notify", switc);
                        if (switc.equals("0"))
                            new PrefManager<String>(getApplicationContext()).set("switch", "false");
                        else
                            new PrefManager<String>(getApplicationContext()).set("switch", "true");


                        if (auth.getString("pin") == null) {
                            new PrefManager<String>(activity).set("pin", "");

                        } else {
                            new PrefManager<String>(activity).set("pin", auth.getString("pin"));
                        }

                        //DEALING WITH BANK DETAILS IN SHARED PREFERENCES
                        /**
                         *
                         */
                        if (bank.length() > 0) {
                            for (int i = 0; i < bank.length(); i++) {
                                JSONObject bankdetails_object = bank.getJSONObject(i);


                                new PrefManager<String>(activity).set("bankdetailsid", bankdetails_object.getString("id"));
                                new PrefManager<String>(activity).set("bank_name", bankdetails_object.getString("bank_name"));
                                new PrefManager<String>(activity).set("aba_number", bankdetails_object.getString("aba_number"));
                                new PrefManager<String>(activity).set("accountno", bankdetails_object.getString("account_number"));
                                new PrefManager<String>(activity).set("currency", bankdetails_object.getString("currency"));
                                new PrefManager<String>(activity).set("type", bankdetails_object.getString("type"));
                                new PrefManager<String>(activity).set("aba_number", bankdetails_object.getString("aba_number"));
                                new PrefManager<String>(activity).set("bankcountry", bankdetails_object.getString("country"));
                                //Common_data.setPreference(activity, "bankdetailstime", bankdetails_object.getString("bankdetailstime"));
                                //Common_data.setPreference(activity, "userid", bankdetails_object.getString("userid"));
                            }
                        }


                        if (card.length() > 0) {
                            for (int i = 0; i < card.length(); i++) {
                                JSONObject carddetails_object = card.getJSONObject(i);

                                new PrefManager<String>(activity).set("card_ref", carddetails_object.getString("card_ref"));
                                new PrefManager<String>(activity).set("card_number", carddetails_object.getString("masked_card_number"));
                                new PrefManager<String>(activity).set("expiry_date", carddetails_object.getString("card_expiry_date"));
                                new PrefManager<String>(activity).set("cvv", carddetails_object.getString("card_cvv"));
                                // Common_data.setPreference(activity, "ownername", carddetails_object.getString("owner_name"));
                                //Common_data.setPreference(activity, "cardaddtime", carddetails_object.getString("cardaddtime"));
                                //Common_data.setPreference(activity, "cardcountry", carddetails_object.getString("verify"));
                            }
                        }
                        
                        Intent intent = new Intent(activity, MainActivity.class);
                        //Intent	intent = new Intent(activity, SendMoney1_Frag.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(Login.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(getString(R.string.app_store_url)));
                                startActivity(i);

                                finish();
                            }
                        });
                        d.show();
                    } else if (1040 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        //Registration.show_otp_dialog = true;
                        //Registration.userid = jsonObject.getString("user_id");
                        Intent intent = new Intent(activity, Otp.class);
                        //Intent	intent = new Intent(activity, SendMoney1_Frag.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                        finish();
                    } else {
                        //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(Login.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                d.dismiss();
                            }
                        });
                        d.show();
                        //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Login:\n"+e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onFinish() {

            super.onFinish();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {

            super.onFailure(statusCode, error, content);
        }
    }

    private boolean validation() {

        String Emaildegi = "Please enter your E-mail Address";
        String password_ = "Please enter your Password";

        boolean flag = false;

        if (username.length() == 0) {

            Toast.makeText(Login.this, Emaildegi, Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {

            Toast.makeText(Login.this, password_, Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(Login.this, SpleshScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
