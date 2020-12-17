package com.impalapay.uk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.uk.adapters.MainPageTranscationAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 10/10/17.
 */

public class SendMoney3 extends FragmentActivity implements View.OnClickListener {


    RadioButton radio_bank, radio_ewallet;
    RadioButton radio_card, radio_interac;
    LinearLayout select_card_ly, select_bank_ly, current_bal_ly, new_card_ly;
    Button next;
    String address,addr_line2,zip_code,city_name,state_code,state,country;
    TextView select_card_tv, apply_coupon_tv, current_bal_tv;
    Spinner selct_sp, card_sp;
    EditText coupen_code_et, remarks, reference, et_card_number, et_card_cvv, et_card_expiry_date;
    ImageView cvv0;
    TextView fraud_awareness;
    EditText addressline1,addressline2,addresspincode,address_city;
    String userid;
    Boolean address_required = false;
    Spinner address_state;
    ProgressDialog dialog;
    ArrayAdapter<String> dataAdapter;
    String selected_bank_name,country_iso2;
    LinearLayout billingAddress, interacInstant;

    private String[] statearr;
    private String[] statearr1;
    SendMoney3 activity = this;

    public String card_numbers[];
    public String bank_names[];
    public static JSONArray card_json_array = new JSONArray();
    public static JSONArray active_card_json_array = new JSONArray();
    public static JSONObject card_json_object = new JSONObject();
    public Map<Integer, String> selected_card = new HashMap<>();
    public static boolean saved_cards = true;
    public static String masked_card_number;
    public static int card_ref;
    public static boolean transfer_money = false;

    ArrayList<String> cardname_al = new ArrayList<String>();
    ArrayList<String> cardnumber_al = new ArrayList<String>();

    ArrayList<String> bankname_al = new ArrayList<String>();
    ArrayList<Integer> bankid_al = new ArrayList<Integer>();
    ArrayList<String> banknumber_al = new ArrayList<String>();

    private String pData;
    private static double apply_per_value = 0;
    ArrayAdapter<String> cardAdapter;
    private TextView back_tv;
    private TextView next_tv;
    private String switchStatus;
    String base;


    //static
    static String pay_type = "ewallet";
    static int pos;
    static String remarks_st = "", reference_st = "", card_number_st = "", card_cvv_st = "", card_expiry_date_st = "";
    static int coupen_per;
    static String what;

    private static final char card_number_space = ' ';
    private int active_cards_size = 0;
    private int active_bank_size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.send_money3);

        //base = Common_data.getPreferences(SendMoney3.this, "base");
        base = new PrefManager<String>(getApplicationContext()).get("base","");
        init();


        //radio_card.setEnabled(false);
        Common_data.setupUI(findViewById(R.id.hide), SendMoney3.this);

        ApplyOnClick();

        //getApp().touch();
    }

    private void ApplyOnClick() {
        radio_bank.setOnClickListener(this);
        radio_card.setOnClickListener(this);
        radio_interac.setOnClickListener(this);
        radio_ewallet.setOnClickListener(this);
        apply_coupon_tv.setOnClickListener(this);
        next.setOnClickListener(this);
        back_tv.setOnClickListener(this);
        next_tv.setOnClickListener(this);
//        selct_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                pos = arg2;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//
//            }
//        });
    }

    private void init() {
        pay_type = "card";
        back_tv = (TextView) findViewById(R.id.back_tv);
        next_tv = (TextView) findViewById(R.id.done_tv);
        next_tv.setText("Next");
        //fraud_awareness=(TextView)findViewById(R.id.fraud_awareness);
        billingAddress = (LinearLayout) findViewById(R.id.address_ly);
        interacInstant = (LinearLayout) findViewById(R.id.interac_instant);
        billingAddress.setVisibility(View.GONE);
        interacInstant.setVisibility(View.GONE);

        LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (what.equals("airtime")) {

                    //Common_data.setPreference(getApplicationContext(), "alertshow", "false");
                    new PrefManager<String>(getApplicationContext()).set("alertshow", "false");
                    finish();

                } else if (what.equals("paybill")) {
                    Intent ii = new Intent(SendMoney3.this, SendMoney_PayBill.class);
                    startActivity(ii);
                    finish();
                } else if (what.equals("main")) {
                    finish();
                } else {
                    if (SendMoney1_Frag.type.equalsIgnoreCase("zimbabwe_cash_pickup")){
                        Intent ii = new Intent(SendMoney3.this, SendMoneyCashPickup.class);
                        startActivity(ii);
                        finish();
                    }else {
                        Intent ii = new Intent(SendMoney3.this, SendMoney2.class);
                        startActivity(ii);
                        finish();
                    }
                }

            }
        });


        addressline1=(EditText) findViewById(R.id.address_line1_et);
        addressline2=(EditText) findViewById(R.id.address_line2_et);
        addresspincode=(EditText) findViewById(R.id.zip_code_et);
        address_city=(EditText)findViewById(R.id.city_et);
        addresspincode.setInputType(InputType.TYPE_CLASS_TEXT);
        addresspincode.setHint("Postal Code");

        radio_card = (RadioButton) findViewById(R.id.radio_card);
        radio_card.setChecked(true);
        radio_bank = (RadioButton) findViewById(R.id.radio_bank);
        radio_ewallet = (RadioButton) findViewById(R.id.radio_ewallet);
        address_state=(Spinner)findViewById(R.id.state_sp);

        radio_interac = (RadioButton) findViewById(R.id.radio_interac);
        //remove interac option if user is not from CA
        if (base.equalsIgnoreCase("CAD")) {
            //add logic for CA transactions
            radio_bank.setVisibility(View.GONE);
        } else {
            //add logic for non CA transactions
            radio_interac.setVisibility(View.GONE);
        }

        if(base.equalsIgnoreCase("CAD")){
            country_iso2 ="ca";
        }else if(base.equalsIgnoreCase("USD")){
            country_iso2 ="us";
        }

        remarks = (EditText) findViewById(R.id.remarks);
        reference = (EditText) findViewById(R.id.reference);

        select_card_ly = (LinearLayout) findViewById(R.id.card_ly);
        select_bank_ly = (LinearLayout) findViewById(R.id.bank_ly);
        new_card_ly = (LinearLayout) findViewById(R.id.new_card_ly);

        card_sp = (Spinner) findViewById(R.id.card_sp);

        next = (Button) findViewById(R.id.next3);

        select_card_tv = (TextView) findViewById(R.id.change_title_tv);

        apply_coupon_tv = (TextView) findViewById(R.id.apply_coupon_tv);

        selct_sp = (Spinner) findViewById(R.id.select_bank_sp);

        current_bal_ly = (LinearLayout) findViewById(R.id.current_bal_ly);
        current_bal_tv = (TextView) findViewById(R.id.user_bal_tv);

        coupen_code_et = (EditText) findViewById(R.id.coupen_code_et);

        et_card_number = (EditText) findViewById(R.id.et_card_number);
        et_card_cvv = (EditText) findViewById(R.id.et_card_cvv);
        et_card_expiry_date = (EditText) findViewById(R.id.et_card_expiry_date);
        cvv0 = (ImageView) findViewById(R.id.cvv0);
        cvv0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final Dialog d = new Dialog(SendMoney3.this);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(false);
                d.setContentView(R.layout.cvv);

                Button btn_cvv = (Button) d.findViewById(R.id.btn_cvv);
                btn_cvv.setText("Close");
                btn_cvv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        //finish();
                        d.dismiss();
                    }
                });
                d.show();

            }
        });


        et_card_number.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        et_card_expiry_date.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String mLastInput = "";
                // /tv.setText(txtMessage.getText().toString().length());
                if (et_card_expiry_date.getText().toString().length() == 2) {
                    String month = et_card_expiry_date.getText().toString();
                    if (!month.contains("/")) {
                        et_card_expiry_date.setText(month + "/");
                        et_card_expiry_date.setSelection(et_card_expiry_date.getText().length());
                    }
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        //String bal = Common_data.getPreferences(getApplicationContext(), "balance");
        String bal = new PrefManager<String>(getApplicationContext()).get("balance","");
        current_bal_tv.setText(Sawapay_Main_Screen.base + " " + bal);
        coupen_per = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Log.d("onStart", "yes");
            //getClientCards();

            getClientData();

            what = getIntent().getExtras().getString("iswhat");


            if (what.equals(""))
                //what = Common_data.getPreferences(getApplicationContext(), "iswhat");
                what = new PrefManager<String>(getApplicationContext()).get("iswhat","");
                        //Common_data.setPreference(getApplicationContext(), "from", "");
            new PrefManager<String>(getApplicationContext()).get("from","");


            if (SendMoney1_Frag.type != null) {
                if (SendMoney1_Frag.type.equalsIgnoreCase("paybill") || SendMoney1_Frag.type.equalsIgnoreCase("mpesa")) {
                    //reference.setVisibility(View.GONE);
                } else if (what.equalsIgnoreCase("sendmoney") && SendMoney1_Frag.type.equalsIgnoreCase("ewallet")) {
                    //reference.setVisibility(View.GONE);
                    //remarks.setVisibility(View.GONE);
                } else if (what.equalsIgnoreCase("main")) {

                    if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("paybill") || MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("mpesa"))
                        reference.setVisibility(View.GONE);
                    else if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("ewallet")) {
                        reference.setVisibility(View.GONE);
                        remarks.setVisibility(View.GONE);
                    }
                    Log.d("model data ", MainPageTranscationAdapter.clonemodel.getDel_method());
                }

            } else {
                if (what.equalsIgnoreCase("main")) {

                    if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("paybill") || MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("mpesa"))
                        reference.setVisibility(View.GONE);
                    else if (MainPageTranscationAdapter.clonemodel.getDel_method().equalsIgnoreCase("ewallet")) {
                        reference.setVisibility(View.GONE);
                        remarks.setVisibility(View.GONE);
                    }
                    Log.d("model data ", MainPageTranscationAdapter.clonemodel.getDel_method());
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getClientData() {
        //pData = Common_data.getPreferences(SendMoney3.this, "data");
        pData = new PrefManager<String>(getApplicationContext()).get("data","");
        Log.d("getClientData(); data=>", pData);
        cardname_al.clear();
        bankname_al.clear();
        bankid_al.clear();
        cardnumber_al.clear();
        banknumber_al.clear();

        try {
            JSONObject object = new JSONObject(pData);
            JSONObject jsonObject = object.getJSONObject("data");
            //JSONObject object = array.getJSONObject(0);

            //JSONArray carddetails_array = jsonObject.getJSONArray("card");
            //JSONArray carddetails_array = new JSONArray(Common_data.getPreferences(SendMoney3.this, "cards_array"));
            JSONArray carddetails_array = new JSONArray(new PrefManager<String>(getApplicationContext()).get("cards_array",""));
            Log.d("getClientData(); card=>", carddetails_array.toString());
            card_json_array = carddetails_array;

            active_cards_size = 1;
            if (carddetails_array.length() > 0) {
                for (int i = 0; i < carddetails_array.length(); i++) {
                    JSONObject carddetails_object = carddetails_array
                            .getJSONObject(i);

                    //String cardna = carddetails_object.getString("card_name");
                    //cardname_al.add(cardna);
                    //cardnumber_al.add(carddetails_object.getString("card_number"));

                    if (carddetails_object.getString("card_status").equals("1")) {
                        active_cards_size++;
                        active_card_json_array.put(carddetails_array.getJSONObject(i));
                    }
                    Log.d("card", carddetails_object.getString("card_status"));
                }
            }

            card_numbers = new String[active_cards_size];
            card_numbers[0] = "Select Card";

            if (carddetails_array.length() > 0) {
                saved_cards = true;
                //card_sp.setVisibility(View.VISIBLE);
                //new_card_ly.setVisibility(View.GONE);
                int j = 1;
                for (int i = 0; i < carddetails_array.length(); i++) {
                    JSONObject carddetails_object = carddetails_array
                            .getJSONObject(i);

                    //String cardna = carddetails_object.getString("card_name");
                    //cardname_al.add(cardna);
                    //cardnumber_al.add(carddetails_object.getString("card_number"));

                    if (carddetails_object.getString("card_status").equals("1")) {
                        card_numbers[j] = carddetails_object.getString("masked_card_number") + " ("
                                + carddetails_object.getString("card_nickname") + ")";
                        Log.d("card", card_numbers[j]);
                        j++;
                    }
                }
            }

            Log.d("Size card array", carddetails_array.length() + " ");


            String[] test = {"2222222", "444444"};
            Log.d("test[0]", test[0] + " ");
            dataAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_right_text, card_numbers);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            card_sp.setAdapter(dataAdapter);
            card_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                    // TODO Auto-generated method stub

                    String val = (String) card_sp.getItemAtPosition(position);
                    JSONArray carddetails_array1 = null;
                    try {
                        //carddetails_array1 = new JSONArray(Common_data.getPreferences(SendMoney3.this, "carddetails_array"));
                        carddetails_array1 = new JSONArray(new PrefManager<String>(getApplicationContext()).get("carddetails_array",""));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("card_sp", val);

                    if (0 == position && active_cards_size < 2) {
                        Toast.makeText(SendMoney3.this, "No Attached Card Found...", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMoney3.this);

                        builder
                                .setMessage("Add Card Details")
                                .setCancelable(false)
                                .setPositiveButton("Add Card", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        transfer_money = true;

                                        final Dialog d=new Dialog(SendMoney3.this);
                                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        d.setCancelable(false);
                                        d.setContentView(R.layout.success_response);
                                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                                        tv_success.setText("Please note that you can only attach a Visa or MasterCard debit card with expiry date and cvv at the back of the card.");

                                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                                        btn_ok.setText("OK");
                                        btn_ok.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                d.dismiss();
                                                Intent i = new Intent(SendMoney3.this, AttachCardActivity.class);
                                                i.putExtra("iswhat", what);
                                                startActivity(i);
                                            }
                                        });
                                        d.show();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        //finish();
                                        //startActivity(getIntent());
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(SendMoney3.this, "" + val, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getClientBankData() {
        try {
            select_card_tv.setText("Select Account");
            select_bank_ly.setVisibility(View.VISIBLE);
            //JSONArray bankDetails_array = new JSONArray(Common_data.getPreferences(SendMoney3.this, "banks_array"));
            JSONArray bankDetails_array = new JSONArray(new PrefManager<String>(getApplicationContext()).get("banks_array",""));
            active_bank_size = 1;
            //jsonObject.optJSONArray("bank");
            if (bankDetails_array.length() > 0) {
                for (int i = 0; i < bankDetails_array.length(); i++) {
                    JSONObject carddetails_object = bankDetails_array
                            .getJSONObject(i);
                    active_bank_size++;

                }
            }


            bank_names = new String[active_bank_size];
            bank_names[0] = "Select Bank";
            int l = 1;
            if (bankDetails_array.length() > 0) {
                for (int i = 0; i < bankDetails_array.length(); i++) {

                    JSONObject bankobj = bankDetails_array.optJSONObject(i);
                    bank_names[l] = bankobj.getString("bank_name");
                    l++;
                    bankname_al.add(bankobj.getString("bank_name"));
                    bankid_al.add(bankobj.getInt("bank_ref"));
                    banknumber_al.add(bankobj.getString("masked_bank_number"));
                }
            }

            cardAdapter = new ArrayAdapter<String>(SendMoney3.this, R.layout.custom_spinner_text, bank_names);
            cardAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            selct_sp.setAdapter(cardAdapter);
            Log.d("Size array", bankname_al.size() + " ");
            selct_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    pos = i;
                    String val = (String) selct_sp.getItemAtPosition(i);
                    if (0 == i && active_bank_size < 2) {
                        select_bank_ly.setVisibility(View.GONE);

                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMoney3.this);
                        builder.setMessage("Add Bank Details")
                                .setCancelable(false)
                                .setPositiveButton("Add Bank", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(SendMoney3.this, AddBankAccountActivity.class);
                                        i.putExtra("iswhat", what);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        //Intent i=new Intent(SendMoney.this,MainActivity.class);
                                        //startActivity(i);
                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        Toast.makeText(SendMoney3.this, "No Attached Bank Details Found.", Toast.LENGTH_SHORT).show();
                    } else {
                        selected_bank_name = val;
                        Toast.makeText(SendMoney3.this, "" + val, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getClientCards() {
        //userid = Common_data.getPreferences(SendMoney3.this, "userid");
        userid = new PrefManager<String>(getApplicationContext()).get("userid","");
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {

            object.put("user_id", userid);

            params.put("request", object.toString());
            Log.d("request", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/getCards", params, new GetCardsHandler(), Login.token);
    }

    class GetCardsHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {

            super.onStart();
            dialog = ProgressDialog.show(SendMoney3.this, null, "Retrieving cards... ");

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {

            super.onSuccess(result);

            Log.d("onSuccess", "onSuccess");

            if (result.length() > 0) {
                Log.d("response", result);
                try {
                    JSONObject json = new JSONObject(result);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if (1001 == response_code)
                        b = true;
                    final String msg = json.optString("message");
                    if (b) {

                        System.out.print("THIS WORKED OUT SUCCESSFULL ");

                        //JSONArray array = json.getJSONArray("data");
                        JSONObject object = json.getJSONObject("data");
                        Log.d("data", object.toString());
                        JSONArray carddetails_array = object.getJSONArray("card");
                        //Common_data.deletePref(SendMoney3.this, "cards_array");
                        //Common_data.setPreference(SendMoney3.this, "cards_array", carddetails_array.toString());
                        new PrefManager<String>(getApplicationContext()).deletePref("cards_array");
                        new PrefManager<String>(getApplicationContext()).set("cards_array",carddetails_array.toString());
                        //getClientData();


                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText("Your session has expired. Please Login Again.");
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                d.dismiss();
                                Intent i = new Intent(SendMoney3.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now.");
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
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(SendMoney3.this)
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        getClientCards();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        finish();
                                        startActivity(getIntent());
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

            super.onFinish();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.i("onFailure", String.valueOf(error));
            super.onFailure(statusCode, error, content);
            Toast.makeText(getApplicationContext(), "Network Error. Please retry!", Toast.LENGTH_SHORT).show();
        }
    }


    private void getClientBanks() {
        //userid = Common_data.getPreferences(SendMoney3.this, "userid");
        userid = new PrefManager<String>(getApplicationContext()).get("userid","");
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {

            object.put("user_id", userid);

            params.put("request", object.toString());
            Log.d("request", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/getClientBanks", params, new GetBanksHandler(), Login.token);

    }

    class GetBanksHandler extends AsyncHttpResponseHandler {
        ProgressDialog pdialog;

        @SuppressWarnings("static-access")
        @Override
        public void onStart() {

            super.onStart();
            pdialog = ProgressDialog.show(SendMoney3.this, null, "Retrieving banks... ");

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result) {

            super.onSuccess(result);

            Log.d("onSuccess", "onSuccess");

            if (result.length() > 0) {
                Log.d("response", result);
                try {
                    JSONObject json = new JSONObject(result);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if (1001 == response_code)
                        b = true;
                    final String msg = json.optString("message");
                    if (b) {
                        //JSONArray array = json.getJSONArray("data");
                        JSONObject object = json.getJSONObject("data");
                        Log.d("data", object.toString());
                        //Common_data.setPreference(AttachCardActivity.this, "data", result);
                        JSONArray banksdetails_array = object.getJSONArray("bank");
                        //Common_data.setPreference(SendMoney3.this, "banks_array", banksdetails_array.toString());
                        new PrefManager<String>(getApplicationContext()).set("banks_array",banksdetails_array.toString());
                        getClientBankData();
                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText("Your session has expired. Please Login Again.");
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                d.dismiss();
                                Intent i = new Intent(SendMoney3.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now.");
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
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(SendMoney3.this)
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        getClientCards();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        finish();
                                        startActivity(getIntent());
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

            super.onFinish();
            if (pdialog != null && pdialog.isShowing()) {
                pdialog.dismiss();
            }

        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.i("onFailure", content);
            super.onFailure(statusCode, error, content);
            Toast.makeText(getApplicationContext(), "Network Error. Please retry!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_bank:
                pay_type = "bank";
                if (base.equalsIgnoreCase("CAD")) {
                    current_bal_ly.setVisibility(View.GONE);
                    select_card_ly.setVisibility(View.GONE);
                    Toast.makeText(this, "This Service is currently Unavailable for Canadian Users.", Toast.LENGTH_SHORT).show();
                } else {
                    current_bal_ly.setVisibility(View.GONE);
                    select_card_ly.setVisibility(View.GONE);
                    select_bank_ly.setVisibility(View.VISIBLE);

                    getClientBanks();
                }


                break;
            case R.id.radio_card:
                pay_type = "card";
                current_bal_ly.setVisibility(View.GONE);
                select_card_ly.setVisibility(View.VISIBLE);
                select_bank_ly.setVisibility(View.GONE);
                billingAddress.setVisibility(View.GONE);
                interacInstant.setVisibility(View.GONE);

                getClientCards();

                break;
            case R.id.radio_ewallet:
                pay_type = "ewallet";
                select_card_ly.setVisibility(View.GONE);
                select_bank_ly.setVisibility(View.GONE);
                current_bal_ly.setVisibility(View.VISIBLE);
                billingAddress.setVisibility(View.GONE);
                interacInstant.setVisibility(View.GONE);
                break;

            case R.id.radio_interac:
                pay_type = "interac";
                current_bal_ly.setVisibility(View.GONE);
                select_card_ly.setVisibility(View.GONE);
                select_bank_ly.setVisibility(View.GONE);
                interacInstant.setVisibility(View.VISIBLE);
                if (radio_interac.isChecked()){
                    //if (Common_data.getPreferences(SendMoney3.this, "zipcode").equals("")) {
                    if (new PrefManager<String>(getApplicationContext()).get("zipcode","").equals("")) {
                        Log.d("Request Legal Data", "To be requested");
                        billingAddress.setVisibility(View.VISIBLE);
                        address_required = true;
                        get_states();
                    }
                }
                break;

            case R.id.next3:
                if (address_required){
                    address = addressline1.getText().toString();
                    city_name = address_city.getText().toString();
                    addr_line2 = addressline2.getText().toString();
                    zip_code = addresspincode.getText().toString();
                    state_code=statearr1[address_state.getSelectedItemPosition()].trim();
                    state=statearr[address_state.getSelectedItemPosition()].trim();
                    country=country_iso2;
                    if (addressValidation()){
                        save_address();
                    }
                    //Log.e("haha", "address"+address+" ad1"+addr_line2+" city"+city_name+" zipCode"+zip_code+" stateCode"+state_code+" state"+state+" country"+country);
                }else {
                    next();
                }
                break;
            case R.id.apply_coupon_tv:
                if (coupen_code_et.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter Coupon Code", Toast.LENGTH_SHORT).show();
                } else {
                    //applyCoupon();
                }
                break;

            case R.id.done_tv:
                if (address_required){
                    address = addressline1.getText().toString();
                    city_name = address_city.getText().toString();
                    addr_line2 = addressline2.getText().toString();
                    zip_code = addresspincode.getText().toString();
                    state_code=statearr1[address_state.getSelectedItemPosition()].trim();
                    state=statearr[address_state.getSelectedItemPosition()].trim();
                    country=country_iso2;
                    if (addressValidation()){
                        save_address();
                    }
                    //Log.e("haha", "address"+address+" ad1"+addr_line2+" city"+city_name+" zipCode"+zip_code+" stateCode"+state_code+" state"+state+" country"+country);
                }else {
                    next();
                }
                break;
            case R.id.back_tv:
                Log.e("What", what);
                if (what.equals("airtime")) {

                    //Common_data.setPreference(getApplicationContext(), "alertshow", "false");
                    new PrefManager<String>(getApplicationContext()).set("alertshow","false");
                    finish();

                } else if (what.equals("paybill")) {
                    Intent ii = new Intent(SendMoney3.this, SendMoney_PayBill.class);
                    startActivity(ii);
                    finish();
                } else if (what.equals("main")) {
                    finish();
                } else {
                    if (SendMoney1_Frag.type.equalsIgnoreCase("zimbabwe_cash_pickup")){
                        Intent ii = new Intent(SendMoney3.this, SendMoneyCashPickup.class);
                        startActivity(ii);
                        finish();
                    }else {
                        Intent ii = new Intent(SendMoney3.this, SendMoney2.class);
                        startActivity(ii);
                        finish();
                    }
                }

                break;
            default:
                break;
        }

    }


    /**
     * Go to next screen if all the validations pass
     */
    public void next(){
        if (validation()) {
            if (validation1()) {
                remarks_st = remarks.getText().toString();
                reference_st = reference.getText().toString();
                //if (Common_data.getPreferences(getApplicationContext(), "base").equalsIgnoreCase("CAD")) {
                if (new PrefManager<String>(getApplicationContext()).get("base","").equalsIgnoreCase("CAD")) {
                    Intent i = new Intent(SendMoney3.this, SendMoney4.class);
                    startActivity(i);
                    finish();
                    //} else if (Common_data.getPreferences(getApplicationContext(), "base").equalsIgnoreCase("USD") && Common_data.getPreferences(getApplicationContext(), "show_fraud_message").equalsIgnoreCase("true")) {
                }else if (new PrefManager<String>(getApplicationContext()).get("base","").equalsIgnoreCase("USD")&&new PrefManager<String>(getApplicationContext()).get("show_fraud_message","").equalsIgnoreCase("true")){
                    Intent i = new Intent(SendMoney3.this, SendMoney4.class);
                    startActivity(i);
                    finish();

                } else {
                    final Dialog d = new Dialog(SendMoney3.this);
                    d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setCancelable(false);
                    d.setContentView(R.layout.fraud_awareness);
                    TextView btn_ok = (TextView) d.findViewById(R.id.proceed);
                    TextView number = (TextView) d.findViewById(R.id.countryNo);
                    final CheckBox show_message = (CheckBox) d.findViewById(R.id.show_message);
                    btn_ok.setText("Ok");
                    btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if (show_message.isChecked()) {
                                Common_data.setPreference(getApplicationContext(), "show_fraud_message", "true");
                            } else {
                                Common_data.setPreference(getApplicationContext(), "show_fraud_message", "false");
                            }
                            d.dismiss();
                            Intent i = new Intent(SendMoney3.this, SendMoney4.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    number.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:" + getString(R.string.contact_phone))));
                        }
                    });
                    d.show();

                }


            }
    }}

    private boolean addressValidation() {
        boolean flag = false;
        if (address.length() == 0) {

            Toast.makeText(SendMoney3.this, "Please Enter Address Line 1.", Toast.LENGTH_SHORT).show();
        }
        else if (zip_code.length() == 0) {

            Toast.makeText(SendMoney3.this, "Please Enter Zip Code or Postal Code.", Toast.LENGTH_SHORT).show();
        }
        else if (city_name.length() == 0) {

            Toast.makeText(SendMoney3.this, "Please Enter City Name.", Toast.LENGTH_SHORT).show();
        }
        else if (state_code.equals("Select State")) {

            Toast.makeText(SendMoney3.this, "Please select your State.", Toast.LENGTH_SHORT).show();
        }else if(state_code.equals("Select Province")){
            Toast.makeText(SendMoney3.this, "Please select your Province.", Toast.LENGTH_SHORT).show();
        }else {
            flag = true;
        }

        return flag;
    }

    private void get_states() {
        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();
        try
        {
            //object.put("userid", userid);
            object.put("country_iso2", country_iso2);
            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RestHttpClient.postParams("auth/getStates_Provinces", params, new GetStates());
    }
    /******* Async Task class for running background Services *******/
    class GetStates extends AsyncHttpResponseHandler
    {
        ProgressDialog pd;
        @SuppressWarnings("static-access")
        @Override
        public void onStart()
        {
            if(activity!=null && !activity.isFinishing())
            {
                Log.d("onStart", "onStart");
                pd=new ProgressDialog(activity).show(activity, null, "Retrieving states... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result)
        {
            Log.i("response", result.toString());

            super.onSuccess(result);
            Log.i("response", result.toString());
            pd.dismiss();
            try {
                if (result.length() > 0)
                {
                    Log.i("response", "" + result);
                    Log.d("response", "" + result);
                    JSONObject jsonObject=new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");

                    if(1001 == response_code)
                    {
                        JSONArray state_data = jsonObject.getJSONArray("data");
                        Log.d("state jsonarray", state_data.toString());
                        statearr = new String[state_data.length()+1];
                        statearr1 = new String[state_data.length()+1];
                        if(base.equalsIgnoreCase("CAD")){
                            statearr[0] = "Select Province";
                            statearr1[0] = "Select Province";
                        }else if(base.equalsIgnoreCase("USD")){
                            statearr[0] = "Select State";
                            statearr1[0] = "Select State";
                        }else{
                            statearr[0] = "Select State";
                            statearr1[0] = "Select State";
                        }

                        for (int i = 0; i < state_data.length(); i++) {
                            JSONObject state_data_object = state_data.getJSONObject(i);
                            statearr[i+1] = state_data_object.getString("state_province");
                            statearr1[i+1] = state_data_object.getString("state_province_a");
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SendMoney3.this, R.layout.custom_spinner_right_text, statearr);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // attaching data adapter to spinner
                        address_state.setAdapter(dataAdapter);
                        address_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long arg3) {
                                // TODO Auto-generated method stub

                                String val = (String) address_state.getItemAtPosition(position);


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
                        });
                        //Log.d("statearr[6]",statearr[3]);
                        //Log.d("statearr1[6]",statearr1[3]);
                        //System.exit(1);
                    }else if(1030 == response_code){
                        Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now.");
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
                    }
                    else {
                        Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(SendMoney3.this);
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
                }
                else
                {

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }



        @Override
        public void onFailure(Throwable e, String errorResponse)
        {

            Log.i("response", ""+errorResponse);
            //Toast.makeText(getBaseContext(), errorResponse,Toast.LENGTH_SHORT).show();
            pd.dismiss();
            //networkErrorDialog();
        }

        @Override
        public void onFinish()
        {
            super.onFinish();
            pd.dismiss();
        }
    }






    private void save_address() {
        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();
        try
        {
            //object.put("userid", userid);
            object.put("address", address);
            object.put("city_name", city_name);
            object.put("addr_line2", addr_line2);
            object.put("zip_code", zip_code);
            object.put("state_code", state_code);
            object.put("state", state);
            object.put("country", country);
            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        RestHttpClient.postParams("banking/addaddress", params, new AddAdress());
    }
    /******* Async Task class for running background Services *******/
    class AddAdress extends AsyncHttpResponseHandler
    {
        ProgressDialog pd;
        @SuppressWarnings("static-access")
        @Override
        public void onStart()
        {
            if(activity!=null && !activity.isFinishing())
            {
                Log.d("onStart", "onStart");
                pd=new ProgressDialog(activity).show(activity, null, "Saving Address... ");
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result)
        {
            Log.i("response", result);

            super.onSuccess(result);
            Log.i("response", result.toString());
            pd.dismiss();
            try {
                if (result.length() > 0)
                {
                    Log.i("response", "" + result);
                    Log.d("response", "" + result);
                    JSONObject jsonObject=new JSONObject(result);
                    int response_code = jsonObject.getInt("code");
                    String message = jsonObject.getString("message");

                    if(1001 == response_code)
                    {
                        //Common_data.setPreference(activity, "address", address);
                        //Common_data.setPreference(activity, "address2", addr_line2);
                        //Common_data.setPreference(activity, "zipcode", zip_code);
                        //Common_data.setPreference(activity, "state", state_code);
                        //Common_data.setPreference(activity, "city", city_name);
                        //Common_data.setPreference(activity, "country", country);

                        new PrefManager<String>(getApplicationContext()).set("address",address);
                        new PrefManager<String>(getApplicationContext()).set("address2",addr_line2);
                        new PrefManager<String>(getApplicationContext()).set("zipcode",zip_code);
                        new PrefManager<String>(getApplicationContext()).set("state",state_code);
                        new PrefManager<String>(getApplicationContext()).set("city",city_name);
                        new PrefManager<String>(getApplicationContext()).set("country",country);


                        Toast.makeText(getApplicationContext(), "Address Successfully Saved.", Toast.LENGTH_SHORT).show();
                        next();
                    }else if(1020 == response_code){
                        final Dialog d=new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText("Your session has expired. Please Login Again.");
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("OK");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                Intent i = new Intent(SendMoney3.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else if(1030 == response_code){
                        Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(SendMoney3.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.dialog1);
                        TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
                        tv_dialog.setText(message);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
                        btn_ok.setText("Update Now.");
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
                    }
                    else {
                        Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(SendMoney3.this);
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
                }
                else
                {

                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }



        @Override
        public void onFailure(Throwable e, String errorResponse)
        {

            Log.i("response", ""+errorResponse);
            //Toast.makeText(getBaseContext(), errorResponse,Toast.LENGTH_SHORT).show();
            pd.dismiss();
            //networkErrorDialog();
        }

        @Override
        public void onFinish()
        {
            super.onFinish();
            pd.dismiss();
        }
    }












    public boolean validation1() {
        boolean flag = false;

        try {

            int w = 0;

            if (reference.getVisibility() == View.VISIBLE) {

                if (reference.getText().toString().trim().length() == 0) {
                    if (w == 0)
                        Toast.makeText(getApplicationContext(), "Enter Reference.", Toast.LENGTH_SHORT).show();
                    w = 0;
                    flag = false;
                    return flag;
                } else {
                    flag = true;
                }
            }

            if (remarks.getVisibility() == View.VISIBLE) {
                if (remarks.getText().toString().trim().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter Remarks.", Toast.LENGTH_SHORT).show();
                    w = 1;
                    flag = false;
                    return flag;
                } else {
                    flag = true;
                }
            }

            if (remarks.getVisibility() == View.GONE && reference.getVisibility() == View.GONE) {
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    private boolean validation() {


        if (radio_card.isChecked()) {
            card_number_st = et_card_number.getText().toString().trim().replace(" ", "");
            card_cvv_st = et_card_cvv.getText().toString().trim();
            card_expiry_date_st = et_card_expiry_date.getText().toString().trim();

            if (saved_cards) {
                String masked_card_number = card_sp.getSelectedItem().toString();
                if (masked_card_number.equals("Select Card")) {
                    Toast.makeText(getApplicationContext(), "Please select card.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                try {
                    card_json_object = active_card_json_array.getJSONObject(card_sp.getSelectedItemPosition() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                    finish();
                    startActivity(getIntent());
                }
            } else {
                if (card_number_st.length() < 16) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Card Number.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (card_cvv_st.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Card CVV.", Toast.LENGTH_SHORT).show();
                    return false;
                }

			/*if(card_expiry_date_st.matches("(?:0[1-9]|1[0-2])/[0-9]{2}")){
                Toast.makeText(getApplicationContext(), "Please Enter Valid Card Expiry Date.", Toast.LENGTH_SHORT).show();
				return false;
			} */

                if (card_expiry_date_st.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Card Expiry Date. Use / to separate month and year.", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!card_expiry_date_st.contains("/")) {
                    Toast.makeText(getApplicationContext(), "Kindly Enter Valid Card Expiry Date. Use / to separate month and year.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        } else if (radio_bank.isChecked()) {

            if (bankname_al.size() == 0) {
                Toast.makeText(getApplicationContext(), "Please Attach Bank Account.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (selected_bank_name.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(getApplicationContext(), "Please select a Bank.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else if (radio_ewallet.isChecked()) {

            if (current_bal_tv.getText().toString().equals("0")) {
                Toast.makeText(getApplicationContext(), "No Sufficent Balance in your eWallet.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;

        }

        return true;
    }

    public ControlApplication getApp() {
        return (ControlApplication) this.getApplication();
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //getApp().touch();
        Log.d("User", "User interaction to " + this.toString());
        Log.e("My Activity Touched", "My Activity Touched");

    }

}
