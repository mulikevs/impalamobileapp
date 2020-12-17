package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.Constant;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.BeneficaryDetailModel;
import com.impalapay.models.Beni;
import com.impalapay.uk.adapters.ContactAdapter;
import com.impalapay.uk.adapters.DummyAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mac on 12/14/17.
 */

public class SendMoneyCashPickup extends FragmentActivity implements TextWatcher,
        View.OnClickListener {

    Dialog dialog;
    LinearLayout add_recp_ly;
    boolean flag = true;
    boolean warn = false;
    public String c_name = null;
    public String c_contact = null;

    TextView back_tv, next_tv, select_rec_tv;

    //ADD RECIEPIENT
    EditText name_beni_et, f_name_beni_et, l_name_beni_et, number_beni_et, confirm_number_beni_et,
            email_beni_et, account_name_beni, account_number_beni, confirm_account_number_beni,
            address_ben_et, city_ben_et;
    Spinner country_ben_sp, country_code_sp, confirm_country_code_sp;
    Spinner bank_sp, branch_sp;
    //Spinner mobile_code_sp;
    LinearLayout bank_add_ly;

    Button save_recipient;
    ArrayList<String> country_code_al = new ArrayList<String>();
    ArrayList<String> country_al = new ArrayList<String>();
    ArrayList<String> bankName_array = new ArrayList<String>();
    ArrayList<String> branch_array = new ArrayList<String>();
    ArrayAdapter<String> branchadapter;
    private String countrywithcode;


    //transcation_details_sendmoney_ly
    LinearLayout transcation_details_sendmoney_ly;
    LinearLayout tranc_name_ly, tranc_number_ly, tranc_email_ly, tranc_acno_ly, tranc_bank_ly, tranc_branch_ly;

    LinearLayout recipient_addr_details;
    public static int is_contact_from_phonebook = 0;
    public static String recipient_physical_addr = "";
    public static String recipient_country = "";
    public static String recipient_city = "";
    public static String recipient_email = "";
    EditText recipient_physical_addr_et;
    EditText recipient_country_et;
    EditText recipient_city_et, mobile_et;
    String recipient_phone;

    TextView tranc_name_tv, tranc_number_tv, tranc_email_tv, tranc_acno_tv, tranc_bank_tv, tranc_branch_tv;


    ContactAdapter adapter;
    Button next, add_recipent;
    EditText edit;
    TextView first, second;
    LinearLayout layout1, layout2;
    ScrollView parent_scroll, scroll1, scroll2;
    DummyAdapter dummy;
    List<List<BeneficaryDetailModel>> two_contacts;
    LinearLayout selected_rec_ly;
    List<Beni> beniList;
    List<Beni> conList;
    List<List<Beni>> allList;

    ArrayList<BeneficaryDetailModel> array_beni;
    ArrayList<BeneficaryDetailModel> array_clone;
    private LinearLayout activityRootView;
    public TextView warnn;


    //UDPATE DIALOG
    Spinner choose_bank_sp_beni;
    Spinner choose_bank_branch_sp_beni;
    String name = "", f_name = "", l_name = "", acno = "", country_code, confirm_country_code;

    //static data
    static String name_static = "", mobile_static = "", email_static = "";
    static String bankname = "", branchname = "", account_no = "", ben_id = "", newly_ben_id = "";
    //static boolean isSimInserted=false;
    String name_up = "", mobile_up = "", email_up = "", id_up = "", city_up = "", country_up = "", address_up = "";
    private String f_name_static, l_name_static;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.send_money_cash_pickup);

        init();
        warnn = (TextView) findViewById(R.id.warn);
        Common_data.setupUI(findViewById(R.id.hide), SendMoneyCashPickup.this);

        dummy = new DummyAdapter(getApplicationContext(), 0, array_clone);

        parent_scroll.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                scroll1.getParent().requestDisallowInterceptTouchEvent(false);
                scroll2.getParent().requestDisallowInterceptTouchEvent(false);

                return false;
            }
        });
        scroll1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                v.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });
        scroll2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                v.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        //getApp().touch();

    }


    private void init() {

        array_beni = new ArrayList<BeneficaryDetailModel>();
        array_clone = new ArrayList<BeneficaryDetailModel>();
        beniList = new ArrayList<Beni>();
        conList = new ArrayList<Beni>();
        allList = new ArrayList<List<Beni>>();
        back_tv = (TextView) findViewById(R.id.back_tv);
        next_tv = (TextView) findViewById(R.id.done_tv);
        next_tv.setText("Next");
        parent_scroll = (ScrollView) findViewById(R.id.parent_scroll);

        edit = (EditText) findViewById(R.id.auto_complete);
        first = (TextView) findViewById(R.id.title1);
        second = (TextView) findViewById(R.id.title2);
        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        scroll1 = (ScrollView) findViewById(R.id.scroll1);
        scroll2 = (ScrollView) findViewById(R.id.scroll2);
        back_tv.setOnClickListener(this);
        next_tv.setOnClickListener(this);
        add_recp_ly = (LinearLayout) findViewById(R.id.add_recp_ly);
        selected_rec_ly = (LinearLayout) findViewById(R.id.selected_rec_ly);

        //ADD RECIPIENT LY
        name_beni_et = (EditText) findViewById(R.id.name_beni_et);
        f_name_beni_et = (EditText) findViewById(R.id.f_name_beni_et);
        address_ben_et = (EditText) findViewById(R.id.address_ben_et);
        country_ben_sp = (Spinner) findViewById(R.id.country_ben_sp);
        city_ben_et = (EditText) findViewById(R.id.city_ben_et);
        l_name_beni_et = (EditText) findViewById(R.id.l_name_beni_et);
        country_code_sp = (Spinner) findViewById(R.id.country_code_sp);
        confirm_country_code_sp = (Spinner) findViewById(R.id.confirm_country_code_sp);
        mobile_et = (EditText) findViewById(R.id.mobile_et);

        LinearLayout back_layout = (LinearLayout) findViewById(R.id.back_layout);
        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        country_code_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        number_beni_et = (EditText) findViewById(R.id.number_beni_et);
        confirm_number_beni_et = (EditText) findViewById(R.id.confirm_number_beni_et);
        email_beni_et = (EditText) findViewById(R.id.email_beni_et);
        account_name_beni = (EditText) findViewById(R.id.account_name_beni);
        account_number_beni = (EditText) findViewById(R.id.account_number_beni);
        confirm_account_number_beni = (EditText) findViewById(R.id.confirm_account_number_beni);
        bank_sp = (Spinner) findViewById(R.id.choose_bank_sp_beni);
        branch_sp = (Spinner) findViewById(R.id.choose_bank_branch_sp_beni);
        save_recipient = (Button) findViewById(R.id.save_beni);
        save_recipient.setOnClickListener(this);
        //mobile_code_sp=(Spinner) findViewById(R.id.mobile_code_sp);
        bank_add_ly = (LinearLayout) findViewById(R.id.bank_add_ly);


        select_rec_tv = (TextView) findViewById(R.id.select_rec_tv);

        if (SendMoney1_Frag.type.equals("bank"))
            bank_add_ly.setVisibility(View.VISIBLE);
        else
            bank_add_ly.setVisibility(View.GONE);


        //transcation detaisls show
        transcation_details_sendmoney_ly = (LinearLayout) findViewById(R.id.transcation_details_sendmoney_ly);
        tranc_name_ly = (LinearLayout) findViewById(R.id.tranc_name_ly);
        tranc_number_ly = (LinearLayout) findViewById(R.id.tranc_number_ly);
        tranc_email_ly = (LinearLayout) findViewById(R.id.tranc_email_ly);
        tranc_acno_ly = (LinearLayout) findViewById(R.id.tranc_acno_ly);
        tranc_bank_ly = (LinearLayout) findViewById(R.id.tranc_bank_ly);
        tranc_branch_ly = (LinearLayout) findViewById(R.id.tranc_branch_ly);

        recipient_addr_details = (LinearLayout) findViewById(R.id.recipient_addr_details);
        recipient_physical_addr_et = (EditText) findViewById(R.id.recipient_physical_addr);
        recipient_country_et = (EditText) findViewById(R.id.recipient_country);
        recipient_city_et = (EditText) findViewById(R.id.recipient_city);

        tranc_name_tv = (TextView) findViewById(R.id.trans_name_tv);
        tranc_number_tv = (TextView) findViewById(R.id.tranc_number_tv);
        tranc_email_tv = (TextView) findViewById(R.id.tranc_email_tv);
        tranc_acno_tv = (TextView) findViewById(R.id.tranc_acno_tv);
        tranc_bank_tv = (TextView) findViewById(R.id.tranc_bank_tv);
        tranc_branch_tv = (TextView) findViewById(R.id.tranc_branch_tv);


        name_static = "";
        mobile_static = "";
        email_static = "";

        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                if (validation()) {

                    if (1 == is_contact_from_phonebook) {
                        recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                        recipient_city = recipient_city_et.getText().toString();
                        recipient_country = recipient_country_et.getText().toString();
                        if (recipient_physical_addr.length() <= 0) {
                            //Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
                        } else if (recipient_city.length() <= 0) {
                            //Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
                        } else if (recipient_country.length() <= 0) {
                            //Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Log.d("type", SendMoney1_Frag.type);
                    //if(SendMoney1_Frag.type.equals("bank") || SendMoney1_Frag.type.equals("ewallet"))
                    if (SendMoney1_Frag.type.equals("ewallet")) {
                        if (1 == is_contact_from_phonebook) {
                            recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                            recipient_city = recipient_city_et.getText().toString();
                            recipient_country = recipient_country_et.getText().toString();
                            if (recipient_physical_addr.length() <= 0) {
                                //Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
                            } else if (recipient_city.length() <= 0) {
                                //Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
                            } else if (recipient_country.length() <= 0) {
                                //Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
                            } else {
                                //verifyUser();
                            }
                            verifyUser();
                        } else {
                            verifyUser();
                        }

                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("mpesa")) {
                        /*if(Common_data.isSimInserted(SendMoney2.this))
						{*/
                        if (getValidSafariMobile(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("254")) {
                                //mobile_static = "+"+mobile_static;
                            }

                            if (1 == is_contact_from_phonebook) {
                                recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                                recipient_city = recipient_city_et.getText().toString();
                                recipient_country = recipient_country_et.getText().toString();
									/*if(recipient_physical_addr.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_city.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_country.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
									}
									else{*/
                                //}
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            }

                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Safaricom mobile number", Toast.LENGTH_SHORT).show();
                        }
						/*}
						else{

							Toast.makeText(SendMoney2.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
						}*/
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("uganda_airtel_money")) {
						/*if(Common_data.isSimInserted(SendMoney2.this))
						{*/
                        if (getValidUgAirtelMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("256")) {
                                //mobile_static = "+"+mobile_static;
                            }

                            if (1 == is_contact_from_phonebook) {
                                recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                                recipient_city = recipient_city_et.getText().toString();
                                recipient_country = recipient_country_et.getText().toString();
									/*if(recipient_physical_addr.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_city.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_country.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
									}
									else{*/

                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                                //}
                            } else {
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            }

                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Ugandan Airtel mobile number", Toast.LENGTH_SHORT).show();
                        }
						/*}
						else{

							Toast.makeText(SendMoney2.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
						}*/
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("uganda_mtn_money")) {
						/*if(Common_data.isSimInserted(SendMoney2.this))
						{*/
                        if (getValidUgMTNMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+256"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("256")) {
                                //mobile_static = "+"+mobile_static;
                            }

                            if (1 == is_contact_from_phonebook) {
                                recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                                recipient_city = recipient_city_et.getText().toString();
                                recipient_country = recipient_country_et.getText().toString();
									/*if(recipient_physical_addr.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_city.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_country.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
									}
									else{*/

                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                                //}
                            } else {
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            }

                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Ugandan MTN mobile number", Toast.LENGTH_SHORT).show();
                        }
						/*}
						else{

							Toast.makeText(SendMoney2.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
						}*/
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("zimbabwe_telecash")) {
						/*if(Common_data.isSimInserted(SendMoney2.this))
						{*/
                        if (getValidZimTelecashMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("263")) {
                                //mobile_static = "+"+mobile_static;
                            }

                            if (1 == is_contact_from_phonebook) {
                                recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                                recipient_city = recipient_city_et.getText().toString();
                                recipient_country = recipient_country_et.getText().toString();
									/*if(recipient_physical_addr.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_city.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_country.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
									}
									else{*/
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                                //}
                            } else {

                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            }

                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Zimbabwe Telecash mobile number", Toast.LENGTH_SHORT).show();
                        }
						/*}
						else{

							Toast.makeText(SendMoney2.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
						}*/
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Zimbabwe") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("zimbabwe_cash_pickup")) {
						/*if(Common_data.isSimInserted(SendMoney2.this))
						{*/
                        // if(getValidZimTelecashMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())){
                        if (mobile_static.startsWith("0")) {
                            //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                        } else if (mobile_static.startsWith("263")) {
                            //mobile_static = "+"+mobile_static;
                        }

                        if (1 == is_contact_from_phonebook) {
                            recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                            recipient_city = recipient_city_et.getText().toString();
                            recipient_country = recipient_country_et.getText().toString();
									/*if(recipient_physical_addr.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_city.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
									}
									else if(recipient_country.length() <= 0){
										Toast.makeText(SendMoney2.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
									}
									else{*/
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();
                            //}
                        } else {
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();
                        }

//                        }
//                        else{
//                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Zimbabwe mobile number", Toast.LENGTH_SHORT).show();
//                        }
						/*}
						else{

							Toast.makeText(SendMoney2.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
						}*/
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equalsIgnoreCase("bank")) {
                        if (account_no.length() <= 0) {
                            Toast.makeText(SendMoneyCashPickup.this, "Recipient's bank account number should be available. Please Add Recipient first.", Toast.LENGTH_SHORT).show();

                        } else {
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();

                        }
                    } else {
                        if (1 == is_contact_from_phonebook) {
                            Log.d("Else", "Eti");
                            recipient_physical_addr = recipient_physical_addr_et.getText().toString();
                            recipient_city = recipient_city_et.getText().toString();
                            recipient_country = recipient_country_et.getText().toString();
                            if (recipient_physical_addr.length() <= 0) {
                                Toast.makeText(SendMoneyCashPickup.this, "Please enter recipient's physical address.", Toast.LENGTH_SHORT).show();
                            } else if (recipient_city.length() <= 0) {
                                Toast.makeText(SendMoneyCashPickup.this, "Please enter recipient's city/town.", Toast.LENGTH_SHORT).show();
                            } else if (recipient_country.length() <= 0) {
                                Toast.makeText(SendMoneyCashPickup.this, "Please enter recipient's country.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            }
                        } else {
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }
        });


        add_recipent = (Button) findViewById(R.id.add_beni);

        add_recipent.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                addRecipient();
            }
        });
        f_name_beni_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = f_name_beni_et.getText().toString().toString().trim();
                f_name = val;

            }
        });
        l_name_beni_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = l_name_beni_et.getText().toString().toString().trim();
                l_name = val;
                name_static = f_name + " " + l_name;
            }
        });
        number_beni_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String val = number_beni_et.getText().toString().toString().trim();
                mobile_static = country_code_sp.getSelectedItem().toString() + val;
                ben_id = "0";
                Log.d("mobile", mobile_static);

            }
        });

        edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                //is_contact_from_phonebook = 0;

                //add_recp_ly.setVisibility(View.GONE);

                String val = edit.getText().toString().toLowerCase(Locale.getDefault());
                layout1.removeAllViews();
                layout2.removeAllViews();
                if (val.length() != 0) {
                    beniList.clear();
                    conList.clear();
                    allList.clear();
                    sortFromBeni(val);
                    sortFromContact(val);
                    if (beniList.size() == 0 && conList.size() == 0)
                        selected_rec_ly.setVisibility(View.GONE);
                } else if (count == 0) {
                    Log.i("In", "Else if");
                    beniList.clear();
                    conList.clear();
                    allList.clear();
                    layout1.removeAllViews();
                    layout2.removeAllViews();
                    scroll1.setVisibility(View.GONE);
                    scroll2.setVisibility(View.GONE);
                    selected_rec_ly.setVisibility(View.GONE);
                    first.setVisibility(View.GONE);
                    second.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //is_contact_from_phonebook = 0;
            }


            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                //View book = null;
                //View ben = null;
                allList.clear();
                //	if(!allList.contains(beniList))
                allList.add(beniList);
                //	if(!allList.contains(conList))
                allList.add(conList);

                Log.i("SIZE1", "" + allList.get(0).size());
                Log.i("SIZE2", "" + allList.get(1).size());

                if (allList.get(0).size() != 0) {
                    scroll1.setVisibility(View.VISIBLE);
                    first.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.VISIBLE);
                    selected_rec_ly.setVisibility(View.VISIBLE);
                    for (int i = 0; i < allList.get(0).size(); i++) {
                        String name = allList.get(0).get(i).getName();
                        String number = allList.get(0).get(i).getNumber();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View beni = inflater.inflate(R.layout.content, null);
                        TextView nam = (TextView) beni.findViewById(R.id.recipient_name);
                        TextView number_ = (TextView) beni.findViewById(R.id.number);
                        nam.setText(name);
                        number_.setText(number);
                        layout1.addView(beni);
                        int position = layout1.indexOfChild(beni);
                        Log.i("Position", "" + position);
                        beni.setTag(position);

                        beni.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                int position = (Integer) v.getTag();
                                MainActivity.hideSoftKeyboard(SendMoneyCashPickup.this, v);

                                name_static = allList.get(0).get(position).getName();
                                mobile_static = allList.get(0).get(position).getNumber();
                                email_static = allList.get(0).get(position).getEmail();
                                ben_id = allList.get(0).get(position).getBeniid();
                                bankname = allList.get(0).get(position).getBank_name();
                                branchname = allList.get(0).get(position).getBranch_name();
                                account_no = allList.get(0).get(position).getAccount_number();

                                name_up = allList.get(0).get(position).getName();
                                mobile_up = allList.get(0).get(position).getNumber();
                                email_up = allList.get(0).get(position).getEmail();
                                id_up = allList.get(0).get(position).getBeniid();
                                city_up = allList.get(0).get(position).getCity();
                                country_up = allList.get(0).get(position).getCountry();
                                address_up = allList.get(0).get(position).getAddress();

                                recipient_email = allList.get(0).get(position).getEmail();
                                recipient_city = allList.get(0).get(position).getCity();
                                recipient_country = allList.get(0).get(position).getCountry();
                                recipient_physical_addr = allList.get(0).get(position).getAddress();

                                Toast.makeText(SendMoneyCashPickup.this, "" + allList.get(0).get(position).getName() + " is Selected", Toast.LENGTH_SHORT).show();
                                transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);


                                tranc_name_tv.setText(allList.get(0).get(position).getName());
                                tranc_number_tv.setText(allList.get(0).get(position).getNumber());
                                tranc_email_tv.setText(allList.get(0).get(position).getEmail());

                                if (!allList.get(0).get(position).getAccount_number().equals("")) {

                                    tranc_acno_ly.setVisibility(View.VISIBLE);
                                    tranc_bank_ly.setVisibility(View.VISIBLE);
                                    tranc_branch_ly.setVisibility(View.VISIBLE);

                                    tranc_acno_tv.setText(allList.get(0).get(position).getAccount_number());
                                    tranc_bank_tv.setText(allList.get(0).get(position).getBank_name());
                                    tranc_branch_tv.setText(allList.get(0).get(position).getBranch_name());

                                } else {
                                    tranc_acno_ly.setVisibility(View.GONE);
                                    tranc_bank_ly.setVisibility(View.GONE);
                                    tranc_branch_ly.setVisibility(View.GONE);
                                }

                                selected_rec_ly.setVisibility(View.GONE);

                                edit.setText("");

                            }
                        });
                    }


                } else {
                    first.setVisibility(View.GONE);
                    layout1.setVisibility(View.GONE);
                    scroll1.setVisibility(View.GONE);
                    layout1.removeAllViews();
                }


                if (allList.get(1).size() != 0) {
                    scroll2.setVisibility(View.VISIBLE);
                    second.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.VISIBLE);
                    selected_rec_ly.setVisibility(View.VISIBLE);
                    for (int i = 0; i < allList.get(1).size(); i++) {

                        String name = allList.get(1).get(i).getName();
                        String number = allList.get(1).get(i).getNumber();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View aBook = inflater.inflate(R.layout.content, null);
                        TextView nam = (TextView) aBook.findViewById(R.id.recipient_name);
                        TextView numb = (TextView) aBook.findViewById(R.id.number);
                        nam.setText(name);
                        numb.setText(number);
                        layout2.addView(aBook);
                        int position = layout2.indexOfChild(aBook);
                        Log.i("Position", "" + position);
                        aBook.setTag(position);
                        warnn.setVisibility(View.GONE);
                        number_beni_et.setText("");
                        f_name_beni_et.setText("");

                        final String name_b = name;
                        final String num = number;
                        aBook.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int position = (Integer) v.getTag();

                                c_name = name_b;
                                c_contact = num.replace(" ", "");
                                String[] split_name = name_b.split(" ");
                                if (split_name.length < 2) {
                                    final Dialog d = new Dialog(SendMoneyCashPickup.this);
                                    d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    d.setCancelable(false);
                                    d.setContentView(R.layout.success_response);
                                    TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                                    tv_success.setText("Recipient must have the correct full first and last name. Please edit manually");
                                    Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                                    btn_ok.setText("OK");
                                    btn_ok.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            /*Intent intent = getIntent();
                                            overridePendingTransition(0, 0);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(intent);*/
                                            warn = true;
                                            addRecipient();
                                            d.dismiss();
                                        }
                                    });
                                    d.show();
                                } else {
                                    Log.d("from phonebook", "Checking..." + allList.get(1).get(position).getIsFromPhonebook());
                                    if (1 == allList.get(1).get(position).getIsFromPhonebook()) {
                                        Log.d("from phonebook", "Yes");
                                        is_contact_from_phonebook = 1;
                                        recipient_addr_details.setVisibility(View.VISIBLE);
                                    } else {
                                        Log.d("from phonebook", "No");
                                    }

                                    MainActivity.hideSoftKeyboard(SendMoneyCashPickup.this, v);
                                    name_static = allList.get(1).get(position).getName();
                                    mobile_static = allList.get(1).get(position).getNumber();
                                    email_static = "";
                                    ben_id = "";
                                    bankname = "";
                                    branchname = "";
                                    account_no = "";

                                    Toast.makeText(SendMoneyCashPickup.this, "" + allList.get(1).get(position).getName() + " is Selected", Toast.LENGTH_SHORT).show();

                                    transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);

                                    tranc_name_tv.setText(allList.get(1).get(position).getName());
                                    tranc_number_tv.setText(allList.get(1).get(position).getNumber());
                                    String email = allList.get(1).get(position).getEmail();
                                    if (email.matches(Constant.emailPattern)) {
                                        tranc_email_ly.setVisibility(View.VISIBLE);
                                        tranc_email_tv.setText(allList.get(1).get(position).getEmail());

                                    } else {
                                        tranc_email_ly.setVisibility(View.GONE);
                                    }

                                    tranc_acno_ly.setVisibility(View.GONE);
                                    tranc_bank_ly.setVisibility(View.GONE);
                                    tranc_branch_ly.setVisibility(View.GONE);
                                    selected_rec_ly.setVisibility(View.GONE);

                                    edit.setText("");
                                }
                            }
                        });
                    }


                } else {
                    second.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    scroll2.setVisibility(View.GONE);
                    layout2.removeAllViews();
                }
            }
        });

        bank_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long arg3) {
                //getBranchName(bank_sp.getSelectedItem().toString());
                if (!bank_sp.getSelectedItem().toString().equals("---Select---")) {
                    getBranchName(bank_sp.getSelectedItem().toString());
                } else {
                    branch_array.clear();
                    try {
                        branchadapter.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        country_al.add("---Select---");
        country_al.add("Kenya");
        country_al.add("Uganda");
        //Collections.sort(country_al);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(SendMoneyCashPickup.this, R.layout.custom_spinner_text, country_al);
        countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_ben_sp.setAdapter(countryAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            showBeneficiary();
            getcontact();
            //add_recp_ly.setVisibility(View.GONE);
            transcation_details_sendmoney_ly.setVisibility(View.GONE);
            name_static = "";
            mobile_static = "";
            email_static = "";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getValidSafariMobile(String mobile) {

        if (mobile.startsWith("0") && (mobile.length() != 10)) {
            return false;
        } else if (mobile.startsWith("254") && (mobile.length() != 12)) {
            return false;
        } else if (mobile.startsWith("+254") && (mobile.length() != 13)) {
            return false;
        }


        boolean flagMobile = false;
        String mob = mobile;

        Log.i("mobileh", "c" + mob);
        String safri1 = "254";

        int sa = 700;

        String comman = "";

        for (int i = 0; i <= 92; i++) {


            if (!(i > 30 && i < 89)) {
                Log.d("saf1", safri1 + "" + (sa + i));
                comman = safri1 + "" + (sa + i);
                if (mob.startsWith("+" + safri1 + "" + (sa + i)) || mob.startsWith(safri1 + "" + (sa + i)) || mob.startsWith("0" + (sa + i))) {
                    flagMobile = true;
                    break;
                }

            }
        }

        return flagMobile;

    }

    public boolean getValidUgAirtelMSISSDN(String mobile) {

        if (mobile.startsWith("0") && (mobile.length() != 10)) {
            return false;
        } else if (mobile.startsWith("256") && (mobile.length() != 12)) {
            return false;
        } else if (mobile.startsWith("+256") && (mobile.length() != 13)) {
            return false;
        }


        boolean flagMobile = false;
        String mob = mobile;

        Log.i("mobileh", "c" + mob);
        String safri1 = "256";

        int sa = 750;

        String comman = "";

        for (int i = 0; i <= 9; i++) {


            //if(!(i>30 && i<89))
            //{
            Log.d("saf1", safri1 + "" + (sa + i));
            comman = safri1 + "" + (sa + i);
            if (mob.startsWith("+" + safri1 + "" + (sa + i)) || mob.startsWith(safri1 + "" + (sa + i)) || mob.startsWith("0" + (sa + i))) {
                flagMobile = true;
                break;
            }

            //}
        }

        return flagMobile;

    }

    public boolean getValidZimTelecashMSISSDN(String mobile) {

        if (mobile.startsWith("0") && (mobile.length() != 10)) {
            return false;
        } else if (mobile.startsWith("263") && (mobile.length() != 12)) {
            return false;
        } else if (mobile.startsWith("+263") && (mobile.length() != 13)) {
            return false;
        }


        boolean flagMobile = false;
        String mob = mobile;

        Log.i("mobileh", "c" + mob);
        String safri1 = "263";

        int sa = 730;

        String comman = "";

        for (int i = 0; i <= 9; i++) {


            //if(!(i>30 && i<89))
            //{
            Log.d("saf1", safri1 + "" + (sa + i));
            comman = safri1 + "" + (sa + i);
            if (mob.startsWith("+" + safri1 + "" + (sa + i)) || mob.startsWith(safri1 + "" + (sa + i)) || mob.startsWith("0" + (sa + i))) {
                flagMobile = true;
                break;
            }

            //}
        }

        return flagMobile;

    }


    public boolean getValidUgMTNMSISSDN(String mobile) {

        if (mobile.startsWith("0") && (mobile.length() != 10)) {
            return false;
        } else if (mobile.startsWith("256") && (mobile.length() != 12)) {
            return false;
        } else if (mobile.startsWith("+256") && (mobile.length() != 13)) {
            return false;
        }


        boolean flagMobile = false;
        String mob = mobile;

        Log.i("mobileh", "c" + mob);
        String safri1 = "256";

        int sa = 770;

        String comman = "";

        for (int i = 0; i <= 19; i++) {


            //if(!(i>30 && i<89))
            //{
            Log.d("saf1", safri1 + "" + (sa + i));
            comman = safri1 + "" + (sa + i);
            if (mob.startsWith("+" + safri1 + "" + (sa + i)) || mob.startsWith(safri1 + "" + (sa + i)) || mob.startsWith("0" + (sa + i))) {
                flagMobile = true;
                break;
            }

            //}
        }

        return flagMobile;

    }

    public void addRecipient() {
        Log.d("flag", "" + flag);
        if (flag) {

            name_static = "";
            mobile_static = "";
            email_static = "";
            ben_id = "";
            bankname = "";
            branchname = "";
            account_no = "";


            if (warn) {
                edit.setText("");
                warnn.setVisibility(View.VISIBLE );
                if (c_contact.length() > 9) {
                    number_beni_et.setText(c_contact.substring(c_contact.length() - 9));
                } else {
                    number_beni_et.setText(c_contact);
                }
                f_name_beni_et.setText(c_name);
                TextView or = (TextView) findViewById(R.id.or);
                //or.setVisibility(View.GONE);

            }
            select_rec_tv.setVisibility(View.GONE);
            if (!warn)
                edit.setVisibility(View.GONE);
            add_recp_ly.setVisibility(View.VISIBLE);
            selected_rec_ly.setVisibility(View.GONE);
            transcation_details_sendmoney_ly.setVisibility(View.GONE);
            flag = false;
            try {

				/*countrywithcode = Common_data.getPreferences(SendMoney2.this,"countrywithcode");
				getcontrycode(countrywithcode);
				Collections.sort(country_code_al);
				ArrayAdapter<String> contrycodeAdapter = new ArrayAdapter<String>(SendMoney2.this, R.layout.custom_spinner_text, country_code_al);
				contrycodeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
				mobile_code_sp.setAdapter(contrycodeAdapter);
				mobile_code_sp.setSelection(47);
				*/


                RestHttpClient.post("banking/getBankNames", new BankNameHandler(), Login.token);
            } catch (Exception e) {

            }
        } else {
            add_recp_ly.setVisibility(View.GONE);
            select_rec_tv.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            flag = true;
        }

        try {
            getcontact();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class BankNameHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Loading...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if (1001 == response_code)
                        b = true;

					/*if(b)
					{
						JSONArray jarray=json.optJSONArray("data");

						for (int i = 0; i < jarray.length(); i++) {
							JSONObject J=jarray.optJSONObject(i);
							bankName_array.add(J.optString("bank_name"));

						}
						if(bankName_array.size()!=0){

							ArrayAdapter<String> bankadapter = new ArrayAdapter<String>(SendMoney2.this,R.layout.custom_spinner_text, bankName_array);
							bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
							bank_sp.setAdapter(bankadapter);
							try {
								choose_bank_sp_beni.setAdapter(bankadapter);
							} catch (Exception e) {

							}
						}

					}*/
                    if (b) {
                        JSONArray jarray = json.optJSONArray("data");
                        bankName_array.clear();
                        bankName_array.add("---Select---");
                        //bankadapter.notifyDataSetChanged();
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject J = jarray.optJSONObject(i);
                            bankName_array.add(J.optString("bank_name"));

                        }
                        if (bankName_array.size() != 0) {

                            ArrayAdapter<String> bankadapter = new ArrayAdapter<String>(SendMoneyCashPickup.this, R.layout.custom_spinner_text, bankName_array);
                            bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            bank_sp.setAdapter(bankadapter);
                        }

                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoneyCashPickup.this);
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
                                Intent i = new Intent(SendMoneyCashPickup.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else {
                        bankName_array.clear();
                        bankName_array.add("---Select---");
                        ArrayAdapter<String> bankadapter = new ArrayAdapter<String>(SendMoneyCashPickup.this, R.layout.custom_spinner_text, bankName_array);
                        bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        bank_sp.setAdapter(bankadapter);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
            networkErrorDialog(SendMoneyCashPickup.this);
        }
    }


    private void getBranchName(String bank) {


        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            object.put("bank_name", bank);

            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("banking/getBankBranches", params, new BranchNameHandler(), Login.token);
    }

    class BranchNameHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Getting Bank Branches...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);


                    boolean b = false;

					/*
					if(b)
					{
						JSONArray jarray=json.optJSONArray("data");
						branch_array.clear();
						for (int i = 0; i < jarray.length(); i++) {
							JSONObject J=jarray.optJSONObject(i);
							branch_array.add(J.optString("branch_name"));
						}

						if(branch_array.size()!=0){

							branchadapter = new ArrayAdapter<String>(SendMoney2.this,R.layout.custom_spinner_text, branch_array);
							branchadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
							branch_sp.setAdapter(branchadapter);
							try {
								choose_bank_branch_sp_beni.setAdapter(branchadapter);
							} catch (Exception e) {

							}
						}

					}
					*/
                    int response_code = json.getInt("code");
                    if (1001 == response_code)
                        b = true;

                    if (b) {
                        JSONArray jarray = json.optJSONArray("data");
                        branch_array.clear();
                        branch_array.add("---Select---");
                        try {
                            branchadapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject J = jarray.optJSONObject(i);
                            branch_array.add(J.optString("branch_name"));
                        }

                        if (branch_array.size() != 0) {

                            branchadapter = new ArrayAdapter<String>(SendMoneyCashPickup.this, R.layout.custom_spinner_text, branch_array);
                            branchadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            branch_sp.setAdapter(branchadapter);
                        }

                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoneyCashPickup.this);
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
                                Intent i = new Intent(SendMoneyCashPickup.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else {
                        branch_array.clear();
                        branch_array.add("---Select---");
                        try {
                            branchadapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        branchadapter = new ArrayAdapter<String>(SendMoneyCashPickup.this, R.layout.custom_spinner_text, branch_array);
                        branchadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        branch_sp.setAdapter(branchadapter);
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
            networkErrorDialog(SendMoneyCashPickup.this);
        }
    }


    protected void sortFromContact(String val) {
        // TODO Auto-generated method stub
        conList.clear();
        allList.clear();
        for (int i = 0; i < array_clone.size(); i++) {
            Beni b = new Beni();
            String value = array_clone.get(i).getName();
            String number = array_clone.get(i).getMobile();
            String email = array_clone.get(i).getEmail();

            if (value.toLowerCase(Locale.getDefault()).startsWith(val)) {
                b.setName(value);
                b.setNumber(number);
                b.setEmail(email);
                b.setIsFromPhonebook(1);
                Log.d("getIsFromPhonebook", "here " + b.getIsFromPhonebook());
                b.setBeniid("0");

                conList.add(b);
            }
        }

    }

    protected void sortFromBeni(String val) {
        // TODO Auto-generated method stub
        beniList.clear();
        allList.clear();
        for (int i = 0; i < array_beni.size(); i++) {
            Beni b = new Beni();
            //String value1 = array_beni.get(i).getEmail();
            String value = array_beni.get(i).getName();
            String nubmber = array_beni.get(i).getMobile();

            if (value.toLowerCase(Locale.getDefault()).startsWith(val)) {
                b.setName(value);
                b.setNumber(nubmber);
                b.setBeniid(array_beni.get(i).getBeniid());
                b.setEmail(array_beni.get(i).getEmail());
                b.setAddress(array_beni.get(i).getAddress());
                b.setCity(array_beni.get(i).getCity());
                b.setCountry(array_beni.get(i).getCountry());
                b.setBank_name(array_beni.get(i).getBank_name());
                b.setBranch_name(array_beni.get(i).getBranch_name());
                b.setAccount_name(array_beni.get(i).getAccount_name());
                b.setAccount_number(array_beni.get(i).getAccount_number());
                b.setIsFromPhonebook(0);

                beniList.add(b);

            }
        }
    }

    private void getcontact() throws Exception {

        //two_contacts.clear();
        array_clone.clear();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Cursor people = getContentResolver().query(uri, projection, null, null, null);

        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int email = people.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        people.moveToFirst();
        do {
            BeneficaryDetailModel model = new BeneficaryDetailModel();
            String name = people.getString(indexName);
            String number = people.getString(indexNumber);
            String em = people.getString(email);

            model.setName(name);
            model.setMobile(number);
            model.setEmail(em);

            array_clone.add(model);

        } while (people.moveToNext());

        adapter = new ContactAdapter(getApplicationContext(), array_clone);

        adapter.notifyDataSetChanged();

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0)
            selected_rec_ly.setVisibility(View.GONE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //get country code
    void getcontrycode(String countrywithcode2) {

        if (countrywithcode2 != null) {
            try {
                JSONObject jsonobject = new JSONObject(countrywithcode2);
                boolean b = jsonobject.getBoolean("result");

                if (b) {
                    JSONArray array = jsonobject.getJSONArray("data");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String countycode = object.getString("country_code");

                        country_code_al.add(countycode);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    // ----show beneficary ----
    private void showBeneficiary() {

        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {

            object.put("userid", Common_data.getPreferences(SendMoneyCashPickup.this, "userid"));
            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestHttpClient.postParams("banking/getBeneficiary", params, new showBeneficaryHandler(), Login.token);

    }

    class showBeneficaryHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;

        View v;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Retrieving beneficiaries...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    boolean b = false;
                    int response_code = json.getInt("code");
                    if (1001 == response_code)
                        b = true;

                    if (b) {
                        JSONArray jarray = json.optJSONArray("data");
                        Log.d("ShowBeneficiary", jarray.toString());
                        array_beni.clear();
                        for (int i = 0; i < jarray.length(); i++) {

                            JSONObject jo = jarray.optJSONObject(i);
                            BeneficaryDetailModel model = new BeneficaryDetailModel();

                            model.setBeniid(jo.optString("id"));
                            model.setName(jo.optString("f_name") + " " + jo.optString("l_name"));
                            model.setFName(jo.optString("f_name"));
                            model.setLName(jo.optString("l_name"));
                            model.setMobile(jo.optString("msisdn"));
                            model.setEmail(jo.optString("email"));
                            model.setCountry(jo.optString("country"));
                            model.setCity(jo.optString("city"));
                            model.setAddress(jo.optString("address"));
                            model.setBranch_name(jo.optString("branch_name"));
                            model.setBank_name(jo.optString("bank_name"));
                            model.setAccount_number(jo.optString("account_number"));
                            model.setAccount_name(jo.optString("account_name"));
                            array_beni.add(model);
                        }


                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoneyCashPickup.this);
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText("Your session has expired. Please Login Again.");
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("Ok");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                Intent i = new Intent(SendMoneyCashPickup.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else {
                        Intent i = new Intent(SendMoneyCashPickup.this, MainActivity.class);
                        startActivity(i);
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
            try {
                getcontact();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            networkErrorDialog(SendMoneyCashPickup.this);
        }
    }

    private boolean validation() {
        Log.i("Mobile No ", mobile_et.getText().toString().trim());

        if (name_static.length() == 0 || mobile_static.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please Select Recipient", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }

    }


    @Override
    public void onClick(View p) {
        switch (p.getId()) {
            case R.id.back_tv:

                Common_data.setPreference(getApplicationContext(), "alertshow", "false");
                finish();
                break;

            case R.id.done_tv:
                if (validation()) {

                    //if(SendMoney1_Frag.type.equals("bank") || SendMoney1_Frag.type.equals("ewallet"))
                    if (SendMoney1_Frag.type.equals("ewallet")) {
                        verifyUser();
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Kenya") && SendMoney1_Frag.type.equalsIgnoreCase("mpesa")) {
                        if (Common_data.isSimInserted(SendMoneyCashPickup.this)) {
                            if (getValidSafariMobile(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                                if (mobile_static.startsWith("0")) {
                                    //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                                } else if (mobile_static.startsWith("254")) {
                                    //mobile_static = "+"+mobile_static;
                                }
                                Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                                i.putExtra("iswhat", "sendmoney");
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Safaricom mobile number", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(SendMoneyCashPickup.this, "Please Insert Sim For M-PESA Transfer", Toast.LENGTH_SHORT).show();
                        }
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("uganda_airtel_money")) {

                        if (getValidUgAirtelMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("256")) {
                                //mobile_static = "+"+mobile_static;
                            }
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Ugandan Airtel mobile number", Toast.LENGTH_SHORT).show();
                        }
                    } else if (SendMoney1_Frag.destination.equalsIgnoreCase("Uganda") &&
                            SendMoney1_Frag.type.equalsIgnoreCase("uganda_MTN_money")) {

                        if (getValidUgMTNMSISSDN(mobile_static.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").trim())) {
                            if (mobile_static.startsWith("0")) {
                                //mobile_static = "+254"+mobile_static.substring(1,mobile_static.length());
                            } else if (mobile_static.startsWith("256")) {
                                //mobile_static = "+"+mobile_static;
                            }
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SendMoneyCashPickup.this, "Please Provide a valid Ugandan MTN mobile number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                        i.putExtra("iswhat", "sendmoney");
                        startActivity(i);
                        finish();
                    }

                }
                break;


            case R.id.save_beni:
                if (validationBank()) {
                    saveBenificary();
                }
                break;

            default:
                break;
        }
    }


    //ADD RECIPIENT VALIDITON
    private boolean validationBank() {

        boolean flag = false;
		/*EditText name_beni_et,number_beni_et,email_beni_et,account_name_beni,account_number_beni;*/
        country_code = country_code_sp.getSelectedItem().toString();
        confirm_country_code = confirm_country_code_sp.getSelectedItem().toString();
        String name = name_beni_et.getText().toString().trim();
        String f_name = f_name_beni_et.getText().toString().trim();
        String l_name = l_name_beni_et.getText().toString().trim();
        String mobile = number_beni_et.getText().toString();
        String confirm_mobile = confirm_number_beni_et.getText().toString();
        String email = email_beni_et.getText().toString().trim();

        String ac_na = account_name_beni.getText().toString();
        String ac_no = account_number_beni.getText().toString().trim();
        String confirm_ac_no = confirm_account_number_beni.getText().toString().trim();
        String type = SendMoney1_Frag.type;

        name_static = name_beni_et.getText().toString().trim();
        f_name_static = f_name_beni_et.getText().toString().trim();
        l_name_static = l_name_beni_et.getText().toString().trim();
        mobile_static = number_beni_et.getText().toString();
        email_static = email_beni_et.getText().toString().trim();

        String address = address_ben_et.getText().toString().trim();
        String city = city_ben_et.getText().toString().trim();
        String country = country_ben_sp.getSelectedItem().toString();

        if (f_name.length() < 3) {

            Toast.makeText(SendMoneyCashPickup.this, "First Name should have at least 3 characters", Toast.LENGTH_SHORT).show();
        } else if (l_name.length() < 3) {

            Toast.makeText(SendMoneyCashPickup.this, "Last Name should have at least 3 characters", Toast.LENGTH_SHORT).show();
        } else if (!country_code.equals(confirm_country_code)) {
            Toast.makeText(SendMoneyCashPickup.this, "Country code does not match with its confirmation", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() == 0) {
            Toast.makeText(SendMoneyCashPickup.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
        } else if (!mobile.equals(confirm_mobile)) {
            Toast.makeText(SendMoneyCashPickup.this, "Mobile number does not match with its confirmation", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() != 9 && country_code.equals("+254")) {
            Toast.makeText(SendMoneyCashPickup.this, "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() != 10 && country_code.equals("+1")) {
            Toast.makeText(SendMoneyCashPickup.this, "Mobile number should have 10 digits", Toast.LENGTH_SHORT).show();
        } else if (email.length() > 0) {
            if (!email.matches(Constant.emailPattern)) {
                Toast.makeText(SendMoneyCashPickup.this, "Enter valid recipient's email", Toast.LENGTH_SHORT).show();
            }
        } else if (city.length() == 0) {
            Toast.makeText(SendMoneyCashPickup.this, "City/Town cannot be blank", Toast.LENGTH_SHORT).show();

        }
		/*else if(address.length()==0){
			//Toast.makeText(SendMoney2.this, "Enter address", Toast.LENGTH_SHORT).show();

		}

		else if(country.equals("---Select---"))
		{
			//Toast.makeText(SendMoney2.this, "Please select country", Toast.LENGTH_SHORT).show();

		}*/
        else if (type.equals("bank")) {
            bankname = bank_sp.getSelectedItem().toString();
            branchname = branch_sp.getSelectedItem().toString();
            account_no = ac_no;
            if (ac_na.length() == 0)
                Toast.makeText(SendMoneyCashPickup.this, "Enter name as per bank record", Toast.LENGTH_SHORT).show();
            else if (ac_no.length() == 0)
                Toast.makeText(SendMoneyCashPickup.this, "Enter account number", Toast.LENGTH_SHORT).show();
            else if (!ac_no.equals(confirm_ac_no))
                Toast.makeText(SendMoneyCashPickup.this, "Account number does not match with its confirmation", Toast.LENGTH_SHORT).show();
            else if (bankname.equals("---Select---")) {
                Toast.makeText(SendMoneyCashPickup.this, "Please select bank name", Toast.LENGTH_SHORT).show();

            } else if ((branchname.equals("---Select---")) || (branchname.length() == 0) || (branchname.equals(""))) {
                Toast.makeText(SendMoneyCashPickup.this, "Please select branch name", Toast.LENGTH_SHORT).show();

            } else
                flag = true;
        } else {
            flag = true;
        }
        return flag;

    }


    private void saveBenificary() {

        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            object.put("userid", Common_data.getPreferences(SendMoneyCashPickup.this, "userid"));
            object.put("msisdn", country_code + number_beni_et.getText().toString());
            object.put("email", email_beni_et.getText().toString());
            object.put("physical_address", address_ben_et.getText().toString());
            object.put("city", city_ben_et.getText().toString());
            object.put("country", country_ben_sp.getSelectedItem().toString());
            object.put("f_name", f_name_beni_et.getText().toString().trim());
            object.put("l_name", l_name_beni_et.getText().toString().trim());
            if (SendMoney1_Frag.type.equals("bank")) {
                object.put("bank_name", bank_sp.getSelectedItem().toString());
                object.put("branch_name", branch_sp.getSelectedItem().toString());
                object.put("account_number", account_number_beni.getText().toString());
                object.put("account_name", account_name_beni.getText().toString());
            } else {
                object.put("bank_name", "");
                object.put("branch_name", "");
                object.put("account_number", "");
                object.put("account_name", "");
            }

            params.put("request", object.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("banking/addBeneficiary", params, new SaveBeneficaryHandler(), Login.token);
    }


    class SaveBeneficaryHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Saving...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    //String msg = json.getString("message");
                    if (1001 == response_code)
                        b = true;
                    String msg = json.optString("message");
                    if (b) {
                        newly_ben_id = json.optString("beneficiary_id");
                        Toast.makeText(SendMoneyCashPickup.this, "Beneficiary added successfully", Toast.LENGTH_SHORT).show();
                        add_recp_ly.setVisibility(View.GONE);
                        select_rec_tv.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);

                        transcation_details_sendmoney_ly.setVisibility(View.VISIBLE);

                        tranc_name_tv.setText(f_name_beni_et.getText().toString() + " " + l_name_beni_et.getText().toString());
                        tranc_number_tv.setText(country_code + " " + number_beni_et.getText().toString());
                        tranc_email_tv.setText(email_beni_et.getText().toString());


                        if (SendMoney1_Frag.type.equals("bank")) {

                            tranc_acno_ly.setVisibility(View.VISIBLE);
                            tranc_branch_ly.setVisibility(View.VISIBLE);
                            tranc_bank_ly.setVisibility(View.VISIBLE);


                            tranc_branch_tv.setText(branchname);
                            tranc_bank_tv.setText(bankname);
                            tranc_acno_tv.setText(account_no);


                            name_static = tranc_name_tv.getText().toString();
                            mobile_static = country_code + number_beni_et.getText().toString();
                            email_static = email_beni_et.getText().toString();
                            bankname = bank_sp.getSelectedItem().toString();
                            branchname = branch_sp.getSelectedItem().toString();
                            account_no = account_number_beni.getText().toString();


                        } else {
                            tranc_acno_ly.setVisibility(View.GONE);
                            tranc_branch_ly.setVisibility(View.GONE);
                            tranc_bank_ly.setVisibility(View.GONE);

                            name_static = tranc_name_tv.getText().toString();
                            mobile_static = country_code + number_beni_et.getText().toString();
                            email_static = email_beni_et.getText().toString();
                            bankname = "";
                            branchname = "";
                            account_no = "";
                            ben_id = "";

                        }


                        name_beni_et.setText("");
                        f_name_beni_et.setText("");
                        l_name_beni_et.setText("");
                        number_beni_et.setText("");
                        confirm_number_beni_et.setText("");
                        email_beni_et.setText("");
                        address_ben_et.setText("");
                        city_ben_et.setText("");
                        country_ben_sp.setSelection(0);
                        bank_sp.setSelection(0);
                        branch_sp.setSelection(0);
                        account_number_beni.setText("");
                        confirm_account_number_beni.setText("");
                        account_number_beni.setText("");
                        showBeneficiary();
                    } else if (1020 == response_code) {
                        final Dialog d = new Dialog(SendMoneyCashPickup.this);
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
                                Intent i = new Intent(SendMoneyCashPickup.this, Login.class);
                                startActivity(i);
                            }
                        });
                        d.show();
                    } else if (1030 == response_code) {
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d = new Dialog(SendMoneyCashPickup.this);
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
                    } else {
                        Toast.makeText(SendMoneyCashPickup.this, msg, Toast.LENGTH_SHORT).show();
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
            Log.d("onFailure", error.toString());
            super.onFailure(error);
            networkErrorDialog(SendMoneyCashPickup.this);
        }

    }

    //network error dialog
    public void networkErrorDialog(final Activity act) {

        final Dialog d = new Dialog(act);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.network_error);

        Button tryagain = (Button) d.findViewById(R.id.try_again);
        tryagain.setText("Close");
        tryagain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                d.dismiss();
                Common_data.setPreference(getApplicationContext(), "alertshow", "false");
                act.finish();
            }
        });
        d.show();

    }


    private void verifyUser() {

        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {

            object.put("userid", Common_data.getPreferences(SendMoneyCashPickup.this, "userid"));
            if (SendMoney1_Frag.type.equalsIgnoreCase("bank")) {
                object.put("no", mobile_static.replace(" ", ""));
                object.put("type", "bank");
                object.put("email", email_static);
            } else {
                object.put("no", mobile_static.replace(" ", ""));
                object.put("type", "ewallet");
                object.put("email", email_static);
            }

            params.put("request", object.toString());

            Log.d("verify user request", params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestHttpClient.postParams("checkvalidation", params, new verifyUserHandler());

    }

    class verifyUserHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;

        View v;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Loading...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            Log.d("json response", content);
            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    boolean b = json.optBoolean("result");
                    String msg = json.optString("message");
                    if (b) {


                        JSONArray jarray = json.optJSONArray("data");

                        JSONObject J = jarray.getJSONObject(0);

                        if (SendMoney1_Frag.type.equals("bank")) {
                            email_static = "";
                            name_static = J.optString("account_name");
                            mobile_static = "";
                            ben_id = J.optString("id");
                            account_no = J.optString("account_number");
                            branchname = J.optString("branch_name");
                            bankname = J.optString("bank_name");
                            Log.d("bank name from 2", bankname);
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();

                        } else if (SendMoney1_Frag.type.equals("ewallet")) {
                            ben_id = "";
                            bankname = "";
                            branchname = "";
                            account_no = "";
                            mobile_static = "";
                            email_static = J.optString("email");
                            name_static = J.optString("name");

                            Log.d("chakefnign name", name_static);
                            Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                            i.putExtra("iswhat", "sendmoney");
                            startActivity(i);
                            finish();

                        }
                    } else {

                        transcation_details_sendmoney_ly.setVisibility(View.GONE);

                        if (msg.equalsIgnoreCase("Please add the beneficiary bank details")) {
                            name_static = "";
                            mobile_static = "";
                            email_static = "";
                            addRecipient();
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        } else if (msg.equalsIgnoreCase("Please add bank details of this user")) {
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            showUpdateDialog();
                        } else if (msg.equalsIgnoreCase("This User is not Register with SawaPay")) {
                            name_static = "";
                            mobile_static = "";
                            email_static = "";
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pdialog.isShowing())
                pdialog.dismiss();
            try {
                getcontact();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            networkErrorDialog(SendMoneyCashPickup.this);
        }
    }


	/*@Override
	public void onBackPressed() {

	} */

    public void showUpdateDialog() {

        dialog = new Dialog(SendMoneyCashPickup.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_beni);
        dialog.setCancelable(false);

        choose_bank_sp_beni = (Spinner) dialog.findViewById(R.id.choose_bank_sp_beni);
        choose_bank_branch_sp_beni = (Spinner) dialog.findViewById(R.id.choose_bank_branch_sp_beni);
        final EditText named = (EditText) dialog.findViewById(R.id.account_name_beni);
        final EditText numberd = (EditText) dialog.findViewById(R.id.account_number_beni);
        Button update = (Button) dialog.findViewById(R.id.update_beni);
        Button cancel_beni = (Button) dialog.findViewById(R.id.cancel_beni);
        cancel_beni.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                transcation_details_sendmoney_ly.setVisibility(View.GONE);
                email_static = "";
                mobile_static = "";
                name_static = "";
                dialog.dismiss();
            }
        });

        RestHttpClient.post("getPaybillName", new BankNameHandler());


        choose_bank_sp_beni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long arg3) {
                getBranchName(choose_bank_sp_beni.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    if (choose_bank_sp_beni.getSelectedItem().toString().length() == 0) {
                        Toast.makeText(SendMoneyCashPickup.this, "select Bank Name", Toast.LENGTH_SHORT).show();
                    } else if (choose_bank_branch_sp_beni.getSelectedItem().toString().length() == 0) {
                        Toast.makeText(SendMoneyCashPickup.this, "select branch Name", Toast.LENGTH_SHORT).show();
                    } else if (named.getText().toString().trim().length() == 0) {
                        Toast.makeText(SendMoneyCashPickup.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    } else if (numberd.getText().toString().length() == 0) {
                        Toast.makeText(SendMoneyCashPickup.this, "Enter Account Number", Toast.LENGTH_SHORT).show();
                    } else {
                        name = named.getText().toString();
                        acno = numberd.getText().toString();
                        updateBenificary();
                    }
                } catch (Exception e) {

                }

            }
        });
        dialog.show();

    }

    private void updateBenificary() {
        RequestParams params = new RequestParams();
        JSONObject obj = new JSONObject();

        try {
            //	obj.put("id", id_up);
            //obj.put("mobile", mobile_up);
            //	obj.put("address", address_up);
            obj.put("email", email_up);
            //	obj.put("city", city_up);
            //	obj.put("country", country_up);
            obj.put("bank_name", choose_bank_sp_beni.getSelectedItem().toString());
            obj.put("branch_name", choose_bank_branch_sp_beni.getSelectedItem().toString());
            obj.put("masked_card_number", acno);
            obj.put("account_name", name);
            //	obj.put("name", name_up);

            params.add("request", obj.toString());

            Log.d("update data", params.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RestHttpClient.post("updateBenifeciary", params, new UpdateBeniHandler());
    }

    class UpdateBeniHandler extends AsyncHttpResponseHandler {
        ProgressDialog pdialog;


        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(SendMoneyCashPickup.this, "", "Loading...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            Log.d("update response", content);
            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    boolean b = json.optBoolean("result");
                    String msg = json.optString("message");
                    Log.d("update response", content);
                    if (b) {
                        name_static = name;
                        account_no = acno;

                        bankname = choose_bank_sp_beni.getSelectedItem().toString();
                        branchname = choose_bank_branch_sp_beni.getSelectedItem().toString();

                        dialog.dismiss();
                        name_up = "";
                        mobile_up = "";
                        email_up = "";
                        id_up = "";
                        city_up = "";
                        country_up = "";
                        address_up = "";

                        Intent i = new Intent(SendMoneyCashPickup.this, SendMoney3.class);
                        i.putExtra("iswhat", "sendmoney");
                        startActivity(i);
                        finish();
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

        }
    }

    @Override
    protected void onStop() {

        //Common_data.alertPinDialog(SendMoney2.this);
        super.onStop();
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
