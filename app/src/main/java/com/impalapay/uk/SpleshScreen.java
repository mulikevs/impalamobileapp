package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.uk.adapters.TestFragmentAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpleshScreen extends FragmentActivity {

    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    // PageIndicator mIndicator;
    Button login_bt;
    Button signup;
    SpleshScreen activity = this;
    ProgressDialog dialog;
    SpleshScreen getActivity = this;
    TextView covert_rate_lb, ug_covert_rate_lb, zw_covert_rate_lb;
    TextView convert_rate, ug_convert_rate, zw_convert_rate;
    LinearLayout rate_ly, zw_rate_ly;
    public static double fx_rate;
    private double rate;
    ProgressDialog dd, f_dialog;
    String country_iso;
    LinearLayout us_footer, ca_footer;

    public static String version_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splesh_screen);
        Log.e("otp", new PrefManager<String>(getApplicationContext()).get("otpRequired", "white"));


        rate_ly = (LinearLayout) findViewById(R.id.rate_ly);
        rate_ly.setVisibility(View.GONE);
        zw_rate_ly = (LinearLayout) findViewById(R.id.zw_rate_ly);
        convert_rate = (TextView) findViewById(R.id.convert_rate);
        ug_convert_rate = (TextView) findViewById(R.id.ug_convert_rate);
        zw_convert_rate = (TextView) findViewById(R.id.zw_convert_rate);
        covert_rate_lb = (TextView) findViewById(R.id.ke_covert_rate_lb);
        ug_covert_rate_lb = (TextView) findViewById(R.id.ug_covert_rate_lb);
        zw_covert_rate_lb = (TextView) findViewById(R.id.zw_covert_rate_lb);
        us_footer = (LinearLayout) findViewById(R.id.us);
        ca_footer = (LinearLayout) findViewById(R.id.ca);
        ca_footer.setVisibility(View.GONE);


        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        //  mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        //  mIndicator.setViewPager(mPager);
        login_bt = (Button) findViewById(R.id.login_bt);

        try {
            version_name = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("version_name", e.getMessage());
        }

        checkinternetaccess();
        /**This two have been disabled
         //country();
         //forex_provider();**/

        country_provider();
        fees_provider();


        login_bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (checkinternetaccess()) {
                    Intent intent = new Intent(SpleshScreen.this, Login.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });

        signup = (Button) findViewById(R.id.singup);
        signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (checkinternetaccess()) {
                    Intent intent = new Intent(SpleshScreen.this, Registration.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            }
        });
    }


    private boolean checkinternetaccess() {

        boolean flag = false;
        if (!Common_data.isInternetOn(getActivity)) {
            Log.d("Network problem", "Network problem");
            Toast.makeText(getBaseContext(), "No Internet Access. Please check your Internet settings.", Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
        }
        return flag;
    }

    private void fees_provider() {
        try {
            RestHttpClient.postParams("auth/getTranferFees", null, new GetFees());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /******* Async Task class for running background Services *******/
    class GetFees extends AsyncHttpResponseHandler {

        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                dd = new ProgressDialog(activity).show(activity, null, "Getting Fees... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            Log.i("response", result.toString());

            System.out.println("Checking out logs for FX");

            super.onSuccess(result);
            Log.i("response", result.toString());
            dd.dismiss();
            try {
                if (result.length() > 0) {

                    Log.i("Fee response", "" + result);
                    //Log.d("Fee response", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
                    if (1001 == response_code) {
                        new PrefManager<String>(getApplicationContext()).set("transferFees", result);
                    } else if (1030 == response_code) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                    } else {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        tv_dialog.setTextColor(Color.RED);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(Throwable e, String errorResponse) {

            Log.i("response", "" + e.getLocalizedMessage().toString());
            dd.dismiss();
            networkErrorDialog(e.getLocalizedMessage().toString());
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dd.dismiss();
        }
    }

    private void forex_provider() {
        try {
            RestHttpClient.postParams("auth/getForexRates", null, new GetForex());

        } catch (Exception e) {

        }

    }

    /******* Async Task class for running background Services *******/
    class GetForex extends AsyncHttpResponseHandler {

        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                f_dialog = new ProgressDialog(activity).show(activity, null, "Getting Fx Rate... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            Log.i("response", result.toString());

            super.onSuccess(result);
            Log.i("response", result.toString());
            f_dialog.dismiss();
            try {
                if (result.length() > 0) {
                    Log.i("Fee response", "" + result);
                    Log.d("Fee response", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
                    if (1001 == response_code) {
                        new PrefManager<String>(getApplicationContext()).set("SawapayRates", result);
                    } else if (1030 == response_code) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                    } else {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        tv_dialog.setTextColor(Color.RED);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(Throwable e, String errorResponse) {

            Log.i("response", "" + errorResponse);
            f_dialog.dismiss();
            networkErrorDialog(e.getLocalizedMessage().toString());
        }

        @Override
        public void onFinish() {
            super.onFinish();
            f_dialog.dismiss();
        }
    }


    private void country_provider() {
        // TODO Auto-generated method stub
        try {
            RestHttpClient.postParams("auth/getCountryDetails", null, new GetCountry());

        } catch (Exception e) {

        }
    }

    /******* Async Task class for running background Services *******/
    class GetCountry extends AsyncHttpResponseHandler {
        ProgressDialog pg;

        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                pg = new ProgressDialog(activity).show(activity, null, "Getting Country Details... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            Log.i("response", result.toString());

            super.onSuccess(result);
            Log.i("response", result.toString());
            pg.dismiss();
            try {
                if (result.length() > 0) {
                    //rate_ly.setVisibility(View.VISIBLE);
                    Log.i("response", "" + result);
                    Log.d("response", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
                    if (1001 == response_code) {
                        fx_rate = Double.parseDouble(jsonObject.optString("currency"));

                        Common_data.setPreference(getBaseContext(), "countrywithcode", result);

                        new PrefManager<String>(getApplicationContext()).set("countrywithcode2", result);

                    } else if (1030 == response_code) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                    } else {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        tv_dialog.setTextColor(Color.RED);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(Throwable e, String errorResponse) {

            Log.i("response", "" + errorResponse);
            pg.dismiss();
            networkErrorDialog(e.getLocalizedMessage().toString());
        }

        @Override
        public void onFinish() {
            super.onFinish();
            pg.dismiss();
        }
    }

    private void country() {
        // TODO Auto-generated method stub
        try {
            RestHttpClient.postParams("auth/getOriginCountry", null, new GetOriginCountry());

        } catch (Exception e) {

        }
    }

    class GetOriginCountry extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                dialog = new ProgressDialog(activity).show(activity, null, "Getting FX Rate... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {

            super.onSuccess(result);
            dialog.dismiss();
            try {
                if (result.length() > 0) {

                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");
                    if (1001 == response_code) {
                        country_iso = jsonObject.optString("country_iso2");
                        new PrefManager<String>(getBaseContext()).set("country_iso2", country_iso);

                        if (country_iso.equalsIgnoreCase("us")) {
                            covert_rate_lb.setText("USD 1.00  -  ");
                            ug_covert_rate_lb.setText("USD 1.00  -  ");
                            zw_covert_rate_lb.setText("USD 1.00  -  ");
                            convert_rate.setText("KES " + String.valueOf(getTForexRate("USD", "KES")));
                            ug_convert_rate.setText("UGX " + String.valueOf(getTForexRate("USD", "UGX")));
                            zw_convert_rate.setText("USD " + String.valueOf(getTForexRate("USD", "USD")));
                            us_footer.setVisibility(View.VISIBLE);
                            ca_footer.setVisibility(View.GONE);
                            zw_rate_ly.setVisibility(View.GONE);

                        } else {

                            getTForexRate("CAD", "KES");
                            covert_rate_lb.setText("CAD 1.00  -  ");
                            ug_covert_rate_lb.setText("CAD 1.00  -  ");
                            zw_covert_rate_lb.setText("CAD 1.00  -  ");
                            convert_rate.setText("KES " + String.valueOf(getTForexRate("CAD", "KES")));
                            ug_convert_rate.setText("UGX " + String.valueOf(getTForexRate("CAD", "UGX")));
                            zw_convert_rate.setText("USD " + String.valueOf(getTForexRate("CAD", "USD")));
                        }
                    } else if (1030 == response_code) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                    } else {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        tv_dialog.setTextColor(Color.RED);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onFailure(Throwable e, String errorResponse) {

            Log.i("response", "" + errorResponse);
            dialog.dismiss();
            //networkErrorDialog();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dialog.dismiss();
        }
    }

    private void parseJson(String jsonarray) {
        if (jsonarray != null) {

            try {
                //rate_ly.setVisibility(View.VISIBLE);
                Log.i("response", "" + jsonarray);
                Log.d("response", "" + jsonarray);
                System.out.println("response: " + jsonarray);
                JSONObject jsonObject = new JSONObject(jsonarray);
                int response_code = jsonObject.getInt("code"); // jsonObject.optString("currency")
                convert_rate.setText("KES " + String.format("% .2f", Double.parseDouble(jsonObject.optString("currency"))));

                if (1001 == response_code) {
                    //Common_data.setPreference(getBaseContext(), "countrywithcode", jsonarray);
                    new PrefManager<String>(getBaseContext()).set("countrywithcode", jsonarray);
                    Log.d("jsonarray", jsonarray);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                networkErrorDialog(e.getLocalizedMessage().toString());
            }
        }
    }


    public void networkErrorDialog(String message) {

        final Dialog d = new Dialog(SpleshScreen.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.network_error);
        TextView tv_dialog = (TextView) d.findViewById(R.id.message_txt);
        tv_dialog.setText(message);
        Button tryagain = (Button) d.findViewById(R.id.try_again);
        tryagain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                d.dismiss();

                Intent i = new Intent(SpleshScreen.this, SpleshScreen.class);
                startActivity(i);
                finish();
            }
        });
        d.show();

    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            System.exit(0);
            //finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 4 * 1000);

        }
    }


    //GETTING CURRENT FX RATE
    private void getCurrentConversionRate(TextView tv, String base, String convert) {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            object.put("cur_from", base);
            object.put("cur_to", convert);

            params.put("request", object.toString());
            Log.d("ConversionRate", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("auth/getConversionRate", params, new ConversionRateHandler(convert, tv), Login.token);
    }

    class ConversionRateHandler extends AsyncHttpResponseHandler {
        TextView tv;
        String convert_;

        ConversionRateHandler(String convert_to, TextView tv1) {
            convert_ = convert_to;
            tv = tv1;
        }

        ProgressDialog pdialog;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SpleshScreen.this, "", "Getting FX Rate...");
        }

        @Override
        @Deprecated
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    Log.d("Response array", json.toString());
                    //boolean b=json.optBoolean("result");
                    boolean b = false;
                    int response_code = json.getInt("code");
                    String msg = json.getString("message");
                    Log.d("Response Code", String.valueOf(response_code));
                    if (1001 == response_code)
                        b = true;

                    if (b) {

                        JSONArray array = json.optJSONArray("data");
                        Log.d("data array", array.toString());
                        JSONObject jobj = array.optJSONObject(0);
                        tv.setText(convert_ + " " + String.format("% .4f", Double.parseDouble(jobj.optString("amount"))));
                        //tv.setText(jobj.optString("amount"));
                    } else if (1010 == response_code) {
                        final Dialog d = new Dialog(SpleshScreen.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("Retry");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        d.show();
                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SpleshScreen.this);
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
                                finish();
                                startActivity(getIntent());
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


        }

        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            finish();
            startActivity(getIntent());
        }
    }

    public Double getTForexRate(String base, String to) throws JSONException {

        double forex_amount = 0;
        String cur_to, cur_from;
        Log.d("Passed Items", "Base " + base + " Currency " + to);
        //JSONObject forexData= new JSONObject(Common_data.getPreferences(getApplicationContext(),"SawapayRates"));
        JSONObject forexData = new JSONObject(new PrefManager<String>(getApplicationContext()).get("SawapayRates", "white"));
        //Log.i("Preference Fees ",feeData.toString());

        JSONArray all = forexData.getJSONArray("data");

        for (int i = 0; i < all.length(); i++) {
            JSONObject obj = all.getJSONObject(i);
            cur_to = obj.getString("r_to");
            cur_from = obj.getString("r_from");


            if ((cur_from.equalsIgnoreCase(base)) && (cur_to.equalsIgnoreCase(to))) {
                forex_amount = Double.parseDouble(obj.getString("sawapay_rate"));
                Log.i("Fee Returned", String.valueOf(forex_amount));

            }


        }


        return forex_amount;

    }


}
