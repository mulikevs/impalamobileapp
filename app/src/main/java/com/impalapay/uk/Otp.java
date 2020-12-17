package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Otp extends Activity {
    private TextView verify_tv,reject_tv,resend_otp, back_tv,done_tv,send_number;
    Button back_bt,cancel_resend,resend_action;
    private ImageView reject_img,verify_img;
    private EditText otp_et,new_otp;
    private String userid = null;
    LinearLayout layout1,layout2,reject_ly;
    Otp activity = this;
    String current_number;
    ArrayList<String> CountryCode_al = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_otp);

        //get userid
        //userid = Common_data.getPreferences(Otp.this, "userid");
        userid = new PrefManager<String>(getApplicationContext()).get("userid","white");

        Log.e("otp", userid);

        init();
    }

    /**
     * initialize all variables
     */
    private void init(){
        //bind
        verify_img = (ImageView) findViewById(R.id.verify_img);
        reject_img = (ImageView) findViewById(R.id.reject_img);
        verify_tv = (TextView) findViewById(R.id.verify_tv);
        reject_tv = (TextView) findViewById(R.id.reject_tv);
        resend_otp = (TextView) findViewById(R.id.resendotp);
        otp_et = (EditText) findViewById(R.id.otp_et);
        new_otp = (EditText) findViewById(R.id.new_otp);
        back_bt = (Button) findViewById(R.id.back_bt);
        cancel_resend = (Button) findViewById(R.id.cancel_resend);
        resend_action = (Button) findViewById(R.id.resend_otp);
        back_tv = (TextView) findViewById(R.id.back_tv);
        done_tv = (TextView) findViewById(R.id.done_tv);
        send_number = (TextView) findViewById(R.id.send_number);

        //String num = Common_data.getPreferences(this, "phone");
        String num = new PrefManager<String>(getApplicationContext()).get("phone","white");

        send_number.setText("We have sent an OTP to "+num);

        current_number = num;
        Log.e("sub", String.valueOf(num.length()));
        if (num.substring(0, 1).equals("+") ){
            current_number = num.substring(2);
            Log.e("sub", current_number);
        }
        //current_number = num.substring(2);
        Log.e("sub2", current_number);
        new_otp.setText(current_number);

        layout1 = (LinearLayout) findViewById(R.id.layer1);
        layout2 = (LinearLayout) findViewById(R.id.layer2);
        reject_ly = (LinearLayout) findViewById(R.id.reject_ly);

        back_bt.setVisibility(View.GONE);
        back_tv.setVisibility(View.GONE);
        done_tv.setVisibility(View.GONE);

        //listeners
        verify_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        verify_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                //resendOtp();
            }
        });

        resend_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_otp.getText().toString().length() == 0) {
                    Toast.makeText(Otp.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                } else if (new_otp.getText().toString().length() < 10) {
                    Toast.makeText(Otp.this, "Please enter a valid UK mobile phone number", Toast.LENGTH_SHORT).show();
                } else if (new_otp.getText().toString().charAt(0) == '0') {

                    Toast.makeText(Otp.this, "Please don't start with zero for your mobile number", Toast.LENGTH_SHORT).show();
                }else {
                    resendOtp();
                }
            }
        });

        cancel_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
            }
        });

        reject_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Otp.this, Login.class));
                finish();
            }
        });

        reject_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Otp.this, Login.class));
            }
        });

    }

    /**
     * Verify OTP send to user's msisdn
     */
    private void verify(){
        if(otp_et.getText().toString().length()==0){
            Toast.makeText(getApplicationContext(), "Please Enter your OTP", Toast.LENGTH_SHORT).show();
        }
        else{
            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();

            try {
                object.put("user_id", userid);
                object.put("otp", otp_et.getText().toString());

                params.put("request", object.toString());

            }

            catch (JSONException e) {
                e.printStackTrace();
            }


            RestHttpClient.postParams("auth/verifyOTP", params,new OTPHandler());
        }
    }

    /**
     * resend otp message to user's msisdn
     */
    public void resendOtp(){
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {

            current_number = new_otp.getText().toString();
            object.put("user_id", userid);
            object.put("msisdn", "+1"+current_number);

            params.put("request", object.toString());

        }

        catch (JSONException e) {
            e.printStackTrace();
        }


        RestHttpClient.postParams("auth/resendOTP", params,new ResendOtpHandler());
    }


    public class ResendOtpHandler extends AsyncHttpResponseHandler{
        ProgressDialog progressDialog;
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            progressDialog = ProgressDialog.show(Otp.this, "", "Please Wait...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            // TODO Auto-generated method stub
            super.onSuccess(result);

            if (result.length() > 0) {
                Log.d("response", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    boolean b = false;
                    if(1001 == response_code)
                        b = true;
                    String message = jsonObject.getString("message");
                    if (b) {
                        Toast.makeText(Otp.this, message,Toast.LENGTH_SHORT).show();
                        send_number.setText("We have sent an OTP to "+current_number);
                        Common_data.setPreference(Otp.this, "phone", current_number);
                        layout1.setVisibility(View.VISIBLE);
                        layout2.setVisibility(View.GONE);
                    }
                    else {
                        otp_et.setText("");
                        Toast.makeText(Otp.this, message,Toast.LENGTH_SHORT).show();
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
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", content);
            super.onFailure(statusCode, error, content);
        }
    }


    public class OTPHandler extends AsyncHttpResponseHandler{
        ProgressDialog pDialog;
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            pDialog = ProgressDialog.show(Otp.this, "", "Please Wait...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            // TODO Auto-generated method stub
            super.onSuccess(result);

            if (result.length() > 0) {
                Log.d("response", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    boolean b = false;
                    if(1001 == response_code)
                        b = true;

                    String message = jsonObject.getString("message");
                    if (b) {
                        //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(Otp.this);
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
                                //Common_data.setPreference(activity, "otpRequired", "no");
                                new PrefManager<String>(getApplicationContext()).set("otpRequired","no");


                                d.dismiss();
                                Intent i = new Intent(Otp.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        });
                        d.show();
                    }
                    else {
                        otp_et.setText("");
                        Toast.makeText(Otp.this, message,Toast.LENGTH_SHORT).show();
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
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", content);
            super.onFailure(statusCode, error, content);
        }
    }
}
