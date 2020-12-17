package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.BeneficaryDetailModel;
import com.impalapay.uk.adapters.BeneficiaryAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ManageBeneficiaries extends Fragment implements
        OnRefreshListener {

    View view;

    ListView mListView;
      ArrayList<BeneficaryDetailModel> array;
    ArrayList<BeneficaryDetailModel> array_filtered;
    TextView tvremainamount,emptyView;
    public static BeneficiaryAdapter smsListAdapter;
    SwipeRefreshLayout swipeView;
    Button btn_add_beneficiary;
    EditText et_search_beneficiary;
   public static ProgressDialog pdialog;

    JSONArray beni_jarray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_manage_beneficiaries, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();

        getBeneficiaries();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            getBeneficiaries();

            swipeView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeView.setRefreshing(false);
                }
            }, 1000);
        };
    };

    @Override
    public void onStop() {
        //Common_data.alertPinDialog(getActivity());
        super.onStop();
    }
    private void init() {

        //tvremainamount = (TextView) view.findViewById(R.id.tvremainamount);
        //tvremainamount.setText(Sawapay_Main_Screen.base+" "+ Common_data.getPreferences(getActivity(), "balance"));

        mListView = (ListView) view.findViewById(R.id.beneficiary_listview);
        array = new ArrayList<BeneficaryDetailModel>();
        array_filtered = new ArrayList<BeneficaryDetailModel>();
        emptyView=(TextView) view.findViewById(R.id.empty_beneficiary);
        btn_add_beneficiary = (Button) view.findViewById(R.id.btn_add_beneficiary);
        btn_add_beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               MainActivity.start_fragment(MainActivity.main_activity, new Add_Beneficiary(), "Add_Beneficiary");
            }
        });

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_view);
        swipeView.setOnRefreshListener(this);
		/*swipeView.setColorScheme(android.R.color.holo_blue_dark,android.R.color.holo_green_dark,
				android.R.color.holo_red_dark, 
				android.R.color.holo_orange_dark); */


        mListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

                int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ?0 : mListView.getChildAt(0).getTop();
                swipeView.setEnabled((topRowVerticalPosition >= 0));
            }
        });

        et_search_beneficiary = (EditText) view.findViewById(R.id.et_search_beneficiary);

        et_search_beneficiary.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub


                String val = et_search_beneficiary.getText().toString().toLowerCase(Locale.getDefault());


                if (val.length() != 0) {
                    array_filtered.clear();
                    if (beni_jarray != null){
                        for (int i = 0; i < beni_jarray.length(); i++) {

                            JSONObject jo = beni_jarray.optJSONObject(i);
                            BeneficaryDetailModel model = new BeneficaryDetailModel();

                            if( jo.optString("f_name").trim().toLowerCase(Locale.getDefault()).startsWith(val)
                                    || jo.optString("l_name").trim().toLowerCase(Locale.getDefault()).startsWith(val)
                                    || jo.optString("msisdn").trim().toLowerCase(Locale.getDefault()).startsWith(val) ){

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
                                array_filtered.add(model);

                            }

                        }
                    }

                    smsListAdapter = new BeneficiaryAdapter(getActivity(), array_filtered);
                    mListView.setAdapter(smsListAdapter);
                    mListView.setEmptyView(emptyView);

                } else if (count == 0) {
                    Log.i("In", "Else if");
                    smsListAdapter = new BeneficiaryAdapter(getActivity(), array);
                    mListView.setAdapter(smsListAdapter);
                    mListView.setEmptyView(emptyView);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }


            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void getBeneficiaries() {

        try {

            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            try {

                //object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
                object.put("userid", new PrefManager<String>(getActivity().getApplicationContext()).get("userid",""));
                params.put("request", object.toString());

            }

            catch (JSONException e) {
                e.printStackTrace();
            }

            RestHttpClient.postParams("banking/getBeneficiary", params,new BeneficiariesHandler(),Login.token);
        }

        catch (Exception e) {
            e.printStackTrace();
        }

    }

    class BeneficiariesHandler extends AsyncHttpResponseHandler {

        ProgressDialog pdialog;
        int p = 0;

        @Override
        public void onStart() {
            super.onStart();
            pdialog = ProgressDialog.show(getActivity(), "", "Loading...");
        }

        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if (content.length() > 0) {

                try {

                    JSONObject json = new JSONObject(content);
                    boolean b = false;
                    int response_code = json.getInt("code");
                    String msg = json.getString("message");
                    if(1001 == response_code)
                        b = true;

                    if (b) {
                        beni_jarray = json.optJSONArray("data");
                        Log.d("ShowBeneficiary", beni_jarray.toString());
                        array.clear();
                        for (int i = 0; i < beni_jarray.length(); i++) {

                            JSONObject jo = beni_jarray.optJSONObject(i);
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
                            array.add(model);
                        }


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
                        btn_ok.setText("OK");
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

                    smsListAdapter = new BeneficiaryAdapter(getActivity(), array);
                    mListView.setAdapter(smsListAdapter);
                    mListView.setEmptyView(emptyView);

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
    }

    @Override
    public void onRefresh() {
        et_search_beneficiary.setText("");
        swipeView.postDelayed(new Runnable() {

            @Override
            public void run() {
                swipeView.setRefreshing(true);
                handler.sendEmptyMessage(0);
            }
        }, 1000);
    }


}
