package com.impalapay.uk;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.Constant;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends FragmentActivity implements OnClickListener,
        OnItemSelectedListener {

    TextView login, bod_tv, country_tv, contry_tv, termsndcondition, privcaypolicay;
    EditText first_name_et, last_name_et, email_et, mobile_et, pass_et,
            conf_pass_et, address_line1, zip_code_reg, city_reg, state_reg, referral_code_et, occupation;
    Button register_bt;
    String first_name, last_name, email, mobile, pass, conf_pass, country, country_iso2,
            currency, dob = "", city = "", username = " ", password = " ", gender,
            country_code = "", addr_line1 = "", zip_code = "", city_name = "", state_code = "", referral_code = "";
    int byear, bmonth, bday;
    int current_year, current_month, current_day;
    LinearLayout city_ly, nationality_ly;
    Spinner state, currency_sp, country_sp, city_sp, gender_sp,mobile_sp;
    StringBuilder bod;
    Context context;
    ArrayList<String> country_al = new ArrayList<String>();
    ArrayList<String> CountryCode_al = new ArrayList<String>();
    ArrayList<String> currency_al = new ArrayList<String>();


    public static boolean show_otp_dialog = false;


    String Countrywithcode;
    String special = "!@#$%^&*()_";
    String pattern = ".*[" + Pattern.quote(special) + "].*";
    public static String userid = "";

    private String[] statearr_ = {"Select State", "Alabama", "Arizona", "Arkansas", "Colorado", "Connecticut", "Delaware",
            "District of Columbia", "Florida", "Georgia", "Idaho",
            "Illinois", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine",
            "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
            "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "North Dakota", "Oklahoma",
            "Oregon", "Rhode Island", "South Carolina", "South Dakota", "Utah", "Vermont", "Washington", "West Virginia",
            "Wisconsin", "Wyoming"};
    private String[] statearr1_ = {"Select State", "AL", "AZ", "AR", "CO", "CT", "DE", "DC", "FL",
            "GA", "ID", "IL", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT",
            "NE", "NV", "NH", "NJ", "NM", "ND", "OK", "OR", "RI", "SC", "SD", "UT", "VT", "WA", "WV", "WI", "WY"};
    //private String[] genderarr = {"Legal Gender", "Male", "Female"};
    private String[] genderarr = {"Male", "Female"};

    private String[] statearr;
    private String[] statearr1;

    private String[] cityarr = {"Alabama", "Alaska", "Arizona", "Arkansas",
            "California", "Colorado", "Connecticut", "Delaware", "Florida",
            "Georgia", "Hawaii", "Illinois", "Indiana", "Iowa", "Kansas",
            "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts",
            "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana",
            "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico",
            "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma",
            "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
            "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"};
    String[] country_arr = {"United States of America", "Canada"};
    Registration activity = this;
    ProgressDialog dialog;
    private int countrypostion = 0;


    EditText otp_et;
    boolean resultflag = false;

    @TargetApi(19)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registration);
        xmlid();
        //nationality_ly.setVisibility(View.GONE);

        Countrywithcode = Common_data.getPreferences(getBaseContext(), "countrywithcode");

        Log.i("Countrywithcode", Countrywithcode);
        Log.i("Countrywithmine", new PrefManager<String>(getApplicationContext()).get("countrywithcode2","white"));


        parseJson(Countrywithcode);
        register_bt.setOnClickListener(this);

        Map<String, String> currencies = Common_data.getAvailableCurrencies();
        Log.i("thecurrencies",currencies.toString());

        for (String country : currencies.keySet()) {
            String currencyCode = currencies.get(country);



			  //System.out.println(country + " => " + currencyCode); //
			 //Log.d("on Map country", ""+country); Log.d("on Map currencyCode", ""+currencyCode);
            //Log.i("on Map country", ""+country); Log.d("on Map currencyCode", ""+currencyCode);
            //currencies arraylist
            currency_al.add(currencyCode);
        }
        Log.i("currencyaray",currency_al.toString());

        //Assign Gender Data to Spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text, genderarr);
        genderAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        gender_sp.setAdapter(genderAdapter);


        country_al = new ArrayList<>(new LinkedHashSet<>(country_al));

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text, cityarr);
        cityAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        city_sp.setAdapter(cityAdapter);



        //currency_sp.setOnItemSelectedListener(this);
        city_sp.setOnItemSelectedListener(this);

        bod_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View yash) {
                Calendar c = Calendar.getInstance();
                current_day = c.get(Calendar.DATE);
                current_month = c.get(Calendar.MONTH);
                current_year = c.get(Calendar.YEAR);
                new DatePickerDialog(Registration.this, datePickerListener, current_year - 30, current_month, current_day).show();
                ;
            }
        });

        termsndcondition.setOnClickListener(this);
        privcaypolicay.setOnClickListener(this);

		/*ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text, statearr);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		state.setAdapter(dataAdapter);
		state.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long arg3) {
				// TODO Auto-generated method stub

				String val = (String) state.getItemAtPosition(position);


				if (val.equals("Select State")) {

				} else {
					Toast.makeText(activity, ""+val, Toast.LENGTH_SHORT).show();
					//getCities(val);
				}


			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

        if (show_otp_dialog) {
            showAccountVerifyDialog();
        }
    }

    private void parseJson(String jsonarray) {
        if (jsonarray != null) {
            try {
                System.out.println("We are here machete");
                JSONObject jsonObject = new JSONObject(jsonarray);
                System.out.println("the value for the obkect is"+jsonObject.toString());
                int response_code = jsonObject.getInt("code");
                if (1001 == response_code) {
                    JSONArray array = jsonObject.getJSONArray("data");
                    country_al.add("Country of Residence");
                    country_al.add("United States of America");
                    country_al.add("Canada");

                    System.out.println("the value of the json array"+array.toString());

                    for (int i = 0; i < array.length(); i++) {
                        //Looping through the Array Object([{"country":"United States of America","country_code":"US"}])
                        JSONObject object = array.getJSONObject(i);
                        String country = object.getString("country");
                        //Add the Counrt to the ArrayList
                        country_al.add(country);
                        System.out.println("after adding an array now countryarraylist"+country_al.toString());

                        String countrycode = object.getString("country_code");

                        System.out.println("we have looped the array successfully"+country+" the code "+countrycode);

                        if (!CountryCode_al.contains(countrycode)){
                            System.out.println("countrycode arraylist empty only contains"+CountryCode_al.toString());

                            CountryCode_al.add(countrycode);

                            System.out.println("after adding an array now it has"+CountryCode_al.toString());

                            callspinner();

                        }

                    }


                    // for (int i = 0; i < array.length(); i++) {
                       // JSONObject object = array.getJSONObject(i);
                       // String country = object.getString("country");
                        //country_al.add(country);
                       // String countrycode = object.getString("country_code");
                       // if (!CountryCode_al.contains(countrycode) && countrycode.equalsIgnoreCase("+1"))
                           // CountryCode_al.add(countrycode);

                        //callspinner();
                    //}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void callspinner() {

        System.out.println("we are now on the Spinner");


        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text, country_al);
        countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_sp.setAdapter(countryAdapter);

        country_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                      int postion, long arg3) {
                countrypostion = postion;
                country = String.valueOf(country_sp.getSelectedItem());
                Log.d("country", "" + country);

                if (country.equals("United States of America")) {
                    zip_code_reg.setHint("Zip Code");
                    zip_code_reg.setInputType(InputType.TYPE_CLASS_NUMBER);
                    currency = "USD";
                    country_iso2 = "US";
                    city_ly.setVisibility(View.GONE);
                    //get_states();
                    city = String.valueOf(city_sp.getSelectedItem());
                    Log.d("city", "" + city);
                } else if (country.equals("Canada")) {
                    zip_code_reg.setHint("Postal Code");
                    zip_code_reg.setInputType(InputType.TYPE_CLASS_TEXT);
                    currency = "CAD";
                    country_iso2 = "CA";
                    //get_states();
                    city_ly.setVisibility(View.GONE);
                    city = String.valueOf(city_sp.getSelectedItem());
                    Log.d("city", "" + city);

                } else {
                    city_ly.setVisibility(View.GONE);
                    currency = "";
                    country_iso2 = "";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
/**
        Collections.sort(CountryCode_al);
        ArrayAdapter<String> codeAdappter = new ArrayAdapter<String>(this, R.layout.custom_spinner_text, CountryCode_al);
        countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mobile_sp.setAdapter(codeAdappter);
        mobile_sp.setSelection(CountryCode_al.indexOf("+1"));
        mobile_sp.setEnabled(false);**/
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            byear = selectedYear;
            bmonth = selectedMonth;
            bday = selectedDay;
            //String monthst=Common_data.MonthName((bmonth+1)+"");
            //bod_tv.setText(new StringBuilder().append(bday).append("-").append(Common_data.MonthName((bmonth+1)+"")).append("-").append(byear).append(" "));
            bod_tv.setText(Common_data.formatDate(byear, bmonth, bday));
            StringBuilder bodsb = ((new StringBuilder().append(bday)
                    .append("-").append(bmonth + 1).append("-").append(byear)
                    .append(" ")));

            //dob = bod_tv.getText().toString();
            dob = Common_data.formatDate1(byear, bmonth, bday);

            if (current_year < byear) {
                bod_tv.setText("Date of Birth");
                Toast.makeText(getBaseContext(), "Please select correct date", Toast.LENGTH_LONG).show();
            }

            if (current_year == byear) {
                if (current_month < bmonth) {
                    bod_tv.setText("Date of Birth");
                    Toast.makeText(getBaseContext(),
                            "Please select correct date", Toast.LENGTH_LONG)
                            .show();
                }

                if (current_day < bday) {
                    bod_tv.setText("Date of Birth");
                    Toast.makeText(getBaseContext(),
                            "Please select correct date", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    };

    private void xmlid() {
        currency_sp = (Spinner) findViewById(R.id.currency_sp);
        first_name_et = (EditText) findViewById(R.id.first_name_et);
        last_name_et = (EditText) findViewById(R.id.last_name_et);
        bod_tv = (TextView) findViewById(R.id.bod_tv);
        email_et = (EditText) findViewById(R.id.email_et);
        mobile_et = (EditText) findViewById(R.id.mobile_et);
        pass_et = (EditText) findViewById(R.id.pass_et);
        conf_pass_et = (EditText) findViewById(R.id.conf_pass_et);
        address_line1 = (EditText) findViewById(R.id.address_line1);
        zip_code_reg = (EditText) findViewById(R.id.zip_code_reg);
        city_reg = (EditText) findViewById(R.id.city_reg);
        occupation = (EditText) findViewById(R.id.occupation_et);
        //state_reg = (EditText) findViewById(R.id.state_reg);
        state = (Spinner) findViewById(R.id.state);
        //country_tv was pointing to a wrong file hence the issue with crashing
        //country_sp = (Spinner) findViewById(R.id.country_tv);
        country_sp = (Spinner) findViewById(R.id.country_sp);
        referral_code_et = (EditText) findViewById(R.id.referral_code);
        register_bt = (Button) findViewById(R.id.register_bt);
        city_ly = (LinearLayout) findViewById(R.id.city_ly);
        city_sp = (Spinner) findViewById(R.id.city_sp);
        gender_sp = (Spinner) findViewById(R.id.gender_sp);
        contry_tv = (TextView) findViewById(R.id.contry_tv);
        mobile_sp = (Spinner) findViewById(R.id.mobile_sp);
        termsndcondition = (TextView) findViewById(R.id.terms_and_condition);
        privcaypolicay = (TextView) findViewById(R.id.privacy_policy);
        nationality_ly = (LinearLayout) findViewById(R.id.layout_nationality);
    }

    @Override
    public void onClick(View yash) {

        switch (yash.getId()) {

            case R.id.register_bt:

                getInputValue();

                if (validation()) {
                    //if (passvalidation()) {
                    //if (passvalidation2()) {
                    registerOnServer();

                    //}
                    //}
                }
                //showAccountVerifyDialog();
                break;

            case R.id.terms_and_condition:
                //showTeramsCondition();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.terms_url)));
                startActivity(i);
                break;
            case R.id.privacy_policy:
                Intent p = new Intent(Intent.ACTION_VIEW);
                p.setData(Uri.parse(getString(R.string.privacy_url)));
                startActivity(p);
                break;
            default:
                break;
        }
    }

    private void showTeramsCondition() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
        builder.setMessage("Terms And Conditions");
        ScrollView sv = new ScrollView(Registration.this);
        final TextView tv = new TextView(Registration.this);
        //tv.setTextColor(color.blue_latter);
        //tv.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        tv.setText("https://www.sawa-pay.com/tos.html");
        tv.setPadding(10, 0, 10, 5);

        sv.addView(tv);

        builder.setView(sv);
        builder.setPositiveButton("Close",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        builder.show();

        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(tv.getText().toString()));
                startActivity(i);
            }
        });
    }

    private void registerOnServer() {
        Log.d("registerOnServer()", "registerOnServer()");
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {

            if (CountryCode_al.size() > 0)
               // country_code = mobile_sp.getSelectedItem().toString();

           // String codemobile = country_code + mobile// String codemobile = "1" + mobile;

            object.put("fname", first_name);
            object.put("lname", last_name);
            object.put("email", email);
            //object.put("msisdn", codemobile);
            object.put("msisdn", "+254799098070");

            object.put("password", pass);
            object.put("occupation", occupation.getText().toString());
            object.put("confirm_password", conf_pass);
            object.put("country", country_iso2);
            object.put("currency", currency);
            object.put("gender", gender);
            object.put("dob", dob);
            object.put("address_line1", addr_line1);
            object.put("zip_code", zip_code);
            object.put("referral_code", referral_code);
            object.put("city_name", city_name);
            object.put("state_code", state_code);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        params.put("request", object.toString());

        Log.d("request registration", object.toString());

        RestHttpClient.postParams("auth/register", params, new RegistrationHandler());
System.out.print("we are here now on sending to servers");
    }

    class RegistrationHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                dialog = new ProgressDialog(activity).show(activity, null, "Registering... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            // TODO Auto-generated method stub
            super.onSuccess(result);

            Log.d("onSuccess", "onSuccess!");

            Log.d("Response", result);

            if (result.length() > 0) {
                Log.d("response", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    if (1001 == response_code)
                        resultflag = true;
                    else
                        resultflag = false;
                    boolean b = resultflag;
                    String message = jsonObject.getString("message");
                    if (b) {
                        //JSONArray array = jsonObject.getJSONArray("data");
                        //JSONObject object = array.getJSONObject(0);
                        JSONObject object = jsonObject.getJSONObject("data");

                        userid = object.getString("userid");
                        //Common_data.setPreference(activity, "userid", userid);
                        new PrefManager<String>(getApplicationContext()).set("userid",userid);

                        //Common_data.setPreference(activity, "phone", object.getString("msisdn"));
                        new PrefManager<String>(getApplicationContext()).set("phone",object.getString("msisdn"));
						/*Common_data.setPreference(activity, "fname",object.getString("fname"));
						Common_data.setPreference(activity, "last_name",object.getString("lname"));
						Common_data.setPreference(activity, "email",object.getString("email"));
						Common_data.setPreference(activity, "phone",object.getString("msisdn"));
						Common_data.setPreference(activity, "password",object.getString("password"));
						Common_data.setPreference(activity, "country",object.getString("country"));
						Common_data.setPreference(activity, "currency",object.getString("currency"));

						Common_data.setPreference(activity, "dob",object.getString("dob"));*/

						/*Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show(); */
                        final Dialog d = new Dialog(Registration.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Ok");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                d.dismiss();
                                //showAccountVerifyDialog();
                                Common_data.setPreference(activity, "otpRequired", "yes");
                                Intent intent = new Intent(Registration.this, Otp.class);
                                intent.putExtra("userid", userid);
                                startActivity(intent);
//								Intent i=new Intent(Registration.this,Login.class);
//								startActivity(i);
//								finish();
                            }
                        });
                        d.show();

                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(Registration.this);
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
                        final Dialog d = new Dialog(Registration.this);
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
                                d.dismiss();
                            }
                        });
                        d.show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Error has occured", Toast.LENGTH_SHORT).show();
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
            if (resultflag) {
                //showAccountVerifyDialog();
            }
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", content);
            super.onFailure(statusCode, error, content);
        }
    }

    private boolean passvalidation() {
        boolean flag = false;

        String REQUIRED_MSG = "Please enter combination of special characters";
        String pass_ = pass;

        Log.d("pass_", "" + pass_);
        Log.d("pattern", "" + pattern);
        if (pass_.length() < 6) {
            Toast.makeText(Registration.this, "Password length must be of 6 characters..", Toast.LENGTH_SHORT).show();
            flag = false;
        } else
            flag = true;

		/*Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(pass_);
		boolean b = m.matches();

		Log.d("boolean b", "" + b);

		flag = b;
		if (!b) {
			Toast.makeText(Registration.this, REQUIRED_MSG, Toast.LENGTH_SHORT).show();
		}*/

        return flag;
    }

    private boolean passvalidation2() {
        boolean flag = false;

        String REQUIRED_MSG = "Please enter combination of special characters & numeric";
        String pass_ = pass;

        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pass_);

        boolean isMatched = matcher.matches();

        flag = isMatched;
        if (!isMatched) {
            Toast.makeText(Registration.this, REQUIRED_MSG, Toast.LENGTH_SHORT).show();
        }
        return flag;
    }

    private boolean validation() {
        String REQUIRED_MSG = "First name should have at least 3 characters";
        String REQUIRED_MSG1 = "Last name should have at least 3 characters";
        String REQUIRED_MSG2 = "Please enter your mobile number";
        String REQUIRED_MSG23 = "Please enter a valid US/CA mobile phone number";
        String REQUIRED_MSG3 = "Please select your Date of Birth";
        String REQUIRED_OCCUPATION = "Please enter your Occupation";
        String Emailpatten = "Please enter valid email address";
        String Emaildegi = "Please enter your email address";
        String passwordlength = "Please enter password with a minimum of 6 characters";
        String passwordmatch = "Your passwords don't match";

        boolean flag = false;

        if (first_name.length() <= 2) {

            Toast.makeText(Registration.this, REQUIRED_MSG, Toast.LENGTH_SHORT).show();
        } else if (last_name.length() <= 2) {

            Toast.makeText(Registration.this, REQUIRED_MSG1, Toast.LENGTH_SHORT).show();
        } else if (dob.length() == 0 || dob.equalsIgnoreCase("Date of Birth")) {

            Toast.makeText(Registration.this, REQUIRED_MSG3, Toast.LENGTH_SHORT).show();
        }
		
		/*else if(countrypostion==0){
			Toast.makeText(activity, "Please Select Your Nationality", Toast.LENGTH_SHORT).show();
		}*/
        else if (email.length() == 0) {

            Toast.makeText(Registration.this, Emaildegi, Toast.LENGTH_SHORT).show();
        } else if (!email.matches(Constant.emailPattern)) {

            Toast.makeText(Registration.this, Emailpatten, Toast.LENGTH_SHORT).show();
        } else if (mobile.length() == 0) {

            Toast.makeText(Registration.this, REQUIRED_MSG2, Toast.LENGTH_SHORT).show();
        } else if (mobile.length() < 10) {

            Toast.makeText(Registration.this, REQUIRED_MSG23, Toast.LENGTH_SHORT).show();
        } else if (mobile.charAt(0) == '0') {

            Toast.makeText(Registration.this, "Please don't start with zero for your mobile number", Toast.LENGTH_SHORT).show();
        } else if (occupation.getText().toString().length() == 0) {
            Toast.makeText(Registration.this, REQUIRED_OCCUPATION, Toast.LENGTH_SHORT).show();
        } else if (country_iso2.equals("")) {  //HaPA IKO SHIDA
            Toast.makeText(Registration.this, "Please select your Country", Toast.LENGTH_SHORT).show();
        } else if (gender.equals("Select Gender")) {

            Toast.makeText(Registration.this, "Please select a Gender", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {

            Toast.makeText(Registration.this, passwordlength, Toast.LENGTH_SHORT).show();
        } else if (!pass.equals(conf_pass)) {

            Toast.makeText(Registration.this, passwordmatch, Toast.LENGTH_SHORT).show();
        } else {
            flag = true;

        }

        return flag;
    }

    private void getInputValue() {
        Log.d("getInputValue() ", "getInputValue() ");
        first_name = first_name_et.getText().toString();
        last_name = last_name_et.getText().toString();
        email = email_et.getText().toString();
        mobile = mobile_et.getText().toString().trim();
        gender = gender_sp.getSelectedItem().toString().trim();
//		zip_code = zip_code_reg.getText().toString().trim();
//		referral_code = referral_code_et.getText().toString().trim();
//		city_name = city_reg.getText().toString().trim();
//		//state_code = state.getSelectedItem().toString().trim();
//		state_code = statearr1[state.getSelectedItemPosition()].trim();
        pass = pass_et.getText().toString().trim();
        conf_pass = conf_pass_et.getText().toString().trim();
        addr_line1 = "";
        zip_code = "";
        referral_code = "";
        city_name = "";
        //state_code = state.getSelectedItem().toString().trim();
        state_code = "";

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int postion,
                               long arg3) {
        //currency = String.valueOf(currency_sp.getSelectedItem());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }


    public void showAccountVerifyDialog() {
        final Dialog d = new Dialog(Registration.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.otp_verfication_dialog);
        d.setCancelable(false);
        d.show();

        otp_et = (EditText) d.findViewById(R.id.otp_et);

        final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv);
        final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv);

        final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img);
        final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img);

        tv_reject.setVisibility(View.GONE);
        iv_reject.setVisibility(View.GONE);


        tv_reject.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Registration.this, Login.class);
                startActivity(i);
                finish();
                d.dismiss();
            }
        });

        iv_reject.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Registration.this, Login.class);
                startActivity(i);
                finish();
                d.dismiss();
            }
        });


        verify_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (otp_et.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Enter your OTP", Toast.LENGTH_SHORT).show();
                } else {
                    sendOTP();
                }
            }
        });
        verify_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (otp_et.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please Enter your One Time Password", Toast.LENGTH_SHORT).show();
                } else {
                    sendOTP();
                }
            }
        });
        final TextView resendotp = (TextView) d.findViewById(R.id.resendotp);
        resendotp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                resendOtp();

            }
        });
    }

    private void resendOtp() {

        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {
            object.put("user_id", userid);

            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RestHttpClient.postParams("auth/resendOTP", params, new ResendOtpHandler());

    }

    public class ResendOtpHandler extends AsyncHttpResponseHandler {
        ProgressDialog progressDialog;

        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            progressDialog = ProgressDialog.show(Registration.this, "", "Please Wait...");
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
                    if (1001 == response_code)
                        b = true;
                    String message = jsonObject.getString("message");
                    if (b) {

                        Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        otp_et.setText("");
                        Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
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

    public void sendOTP() {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {
            object.put("user_id", userid);
            object.put("otp", otp_et.getText().toString());

            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        RestHttpClient.postParams("auth/verifyOTP", params, new OTPHandler());
    }

    public class OTPHandler extends AsyncHttpResponseHandler {
        ProgressDialog pDialog;

        public void onStart() {
            // TODO Auto-generated method stub
            super.onStart();
            pDialog = ProgressDialog.show(Registration.this, "", "Please Wait...");
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
                    if (1001 == response_code)
                        b = true;

                    String message = jsonObject.getString("message");
                    if (b) {
                        //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(Registration.this);
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
                                Intent i = new Intent(Registration.this, Login.class);
                                startActivity(i);
                                finish();
                            }
                        });
                        d.show();
                    } else {
                        otp_et.setText("");
                        Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
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

    private void get_states() {
        // TODO Auto-generated method stub

        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            //object.put("userid", userid);
            object.put("country_iso2", country_iso2);
            params.put("request", object.toString());
            Log.d("request", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestHttpClient.postParams("auth/getStates_Provinces", params, new GetStates());
    }

    /******* Async Task class for running background Services *******/
    class GetStates extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {
            if (activity != null && !activity.isFinishing()) {
                Log.d("onStart", "onStart");
                dialog = new ProgressDialog(activity).show(activity, null, "Retrieving states... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {
            Log.i("response", result.toString());

            super.onSuccess(result);
            Log.i("response", result.toString());
            dialog.dismiss();
            try {
                if (result.length() > 0) {
                    Log.i("response", "" + result);
                    Log.d("response", "" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");

                    if (1001 == response_code) {
                        JSONArray state_data = jsonObject.getJSONArray("data");
                        Log.d("state jsonarray", state_data.toString());
                        statearr = new String[state_data.length() + 1];
                        statearr1 = new String[state_data.length() + 1];
                        if (currency.equalsIgnoreCase("CAD")) {
                            statearr[0] = "Select Province";
                            statearr1[0] = "Select Province";
                        } else if (currency.equalsIgnoreCase("USD")) {
                            statearr[0] = "Select State";
                            statearr1[0] = "Select State";
                        } else {
                            statearr[0] = "Select State";
                            statearr1[0] = "Select State";
                        }

                        for (int i = 0; i < state_data.length(); i++) {
                            JSONObject state_data_object = state_data.getJSONObject(i);
                            statearr[i + 1] = state_data_object.getString("state_province");
                            statearr1[i + 1] = state_data_object.getString("state_province_a");
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Registration.this, R.layout.custom_spinner_right_text, statearr);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner
                        state.setAdapter(dataAdapter);
                        state.setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long arg3) {
                                // TODO Auto-generated method stub

                                String val = (String) state.getItemAtPosition(position);


                                if (val.equals("Select State")) {

                                } else {
                                    Toast.makeText(activity, "" + val, Toast.LENGTH_SHORT).show();
                                    //getCities(val);
                                }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub

                            }
                        });
                        //Log.d("statearr[6]",statearr[3]);
                        //Log.d("statearr1[6]",statearr1[3]);
                        //System.exit(1);
                    } else if (1030 == response_code) {
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(Registration.this);
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
                        final Dialog d = new Dialog(Registration.this);
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
            networkErrorDialog();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            dialog.dismiss();
        }
    }


    public void networkErrorDialog() {

        final Dialog d = new Dialog(Registration.this);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.network_error);

        Button tryagain = (Button) d.findViewById(R.id.try_again);
        tryagain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(Registration.this, Registration.class);
                startActivity(i);
                finish();
            }
        });
        d.show();
    }
}
