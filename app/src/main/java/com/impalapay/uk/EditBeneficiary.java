package com.impalapay.uk;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.Constant;
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

public class EditBeneficiary extends Fragment implements OnClickListener {

    boolean flag=true;
    View view;
    Button bank_add,save;
    EditText ed_mobile,confirm_ed_mobile, ed_fname, ed_lname,ed_email,ed_address,ed_city,ed_account_name,
            ed_account_number,ed_confirm_account_number,name__beneficiary;
    Spinner country_spinner,bank_spinner,branch_spinner,country_code_sp1,confirm_country_code_sp1;
    LinearLayout bank_add_ly;
    //Spinner mobile_code_sp;
    String countrywithcode, country_code,confirm_country_code;
    ArrayList<String> country_al = new ArrayList<String>();
    ArrayList<String> CountryCode_al = new ArrayList<String>();
    Locale local[]=Locale.getAvailableLocales();
    ArrayList<String> country_code_al = new ArrayList<String>();

    ArrayList<String> bankName_array = new ArrayList<String>();
    ArrayList<String> branch_array = new ArrayList<String>();

    ArrayAdapter<String> bankadapter;
    ArrayAdapter<String> branchadapter;

    public static int beneficiary_position;
    BeneficaryDetailModel beneficiaryDetailModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_edit_beneficiary, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        Common_data.setupUI(view.findViewById(R.id.hide_edit), getActivity());
        callspinner();
    }
    @Override
    public void onStop() {
        //Common_data.alertPinDialog(getActivity());
        super.onStop();
    }
    private void callspinner()
    {
        //countrywithcode = Common_data.getPreferences(getActivity(),"countrywithcode");
        //getcontrycode(countrywithcode);
        //Collections.sort(country_code_al);
        //ArrayAdapter<String> contrycodeAdapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner_text, country_code_al);
        //contrycodeAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        //	mobile_code_sp.setAdapter(contrycodeAdapter);
        //mobile_code_sp.setSelection(47);
		
		/*for(Locale loc:local){
			
			if(!loc.getDisplayCountry().equals("") && !country_al.contains(loc.getDisplayCountry()))
				country_al.add(loc.getDisplayCountry());
		}*/

        country_al.add("---Select---");
        country_al.add("Kenya");
        //country_al.add("USA");
        //Collections.sort(country_al);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, country_al);
        countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_spinner.setAdapter(countryAdapter);

        try {
            RestHttpClient.post("banking/getBankNames", new BankNameHandler(), Login.token);
        } catch (Exception e) {

        }


    }



    class BankNameHandler extends AsyncHttpResponseHandler{

        ProgressDialog pdialog;
        @Override
        public void onStart() {
            super.onStart();
            pdialog=ProgressDialog.show(getActivity(), "", "Loading...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if(content.length()>0){

                try {

                    JSONObject json=new JSONObject(content);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if(1001 == response_code)
                        b = true;

                    if(b)
                    {
                        JSONArray jarray=json.optJSONArray("data");
                        bankName_array.clear();
                        bankName_array.add("---Select---");
                        //bankadapter.notifyDataSetChanged();
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject J=jarray.optJSONObject(i);
                            bankName_array.add(J.optString("bank_name").trim());

                        }
                        if(bankName_array.size()!=0){

                            bankadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankName_array);
                            bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            bank_spinner.setAdapter(bankadapter);

                            if( (!beneficiaryDetailModel.getBank_name().equals(null)) &&
                                    (bank_add_ly.getVisibility() == View.VISIBLE) ) {
                                int spinnerPosition = bankadapter.getPosition(beneficiaryDetailModel.getBank_name().trim());
                                bank_spinner.setSelection(spinnerPosition);
                            }
                        }

                    }
                    else{
                        bankName_array.clear();
                        bankName_array.add("---Select---");
                        bankadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankName_array);
                        bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        bank_spinner.setAdapter(bankadapter);
                    }

                } catch (Exception e) {

                }
            }


        }

        @Override
        public void onFinish() {
            super.onFinish();
            if(pdialog.isShowing())
                pdialog.dismiss();
        }

    }

    private void init() {
        beneficiaryDetailModel=(BeneficaryDetailModel) BeneficiaryAdapter.getArrayItem(beneficiary_position);

        ed_mobile=(EditText) view.findViewById(R.id.mobile_number_beneficiary_edit);
        confirm_ed_mobile=(EditText) view.findViewById(R.id.confirm_mobile_number_beneficiary_edit);
        ed_fname =(EditText) view.findViewById(R.id.fname__beneficiary_edit);
        ed_lname =(EditText) view.findViewById(R.id.lname__beneficiary_edit);
        ed_email=(EditText) view.findViewById(R.id.email__beneficiary_edit);
        ed_address=(EditText) view.findViewById(R.id.address_beneficiary_edit);
        ed_city=(EditText) view.findViewById(R.id.city_beneficiary_edit);
        ed_account_name=(EditText) view.findViewById(R.id.account_name_beneficiary_edit);
        ed_account_number=(EditText) view.findViewById(R.id.account_number_beneficiary_edit);
        ed_confirm_account_number = (EditText) view.findViewById(R.id.confirm_account_number_beneficiary_edit);
        //mobile_code_sp=(Spinner) view.findViewById(R.id.mobile_code_sp_edit);

        country_spinner=(Spinner) view.findViewById(R.id.country_of_resdience_beneficiary_edit);
        bank_spinner=(Spinner) view.findViewById(R.id.select_bank_sp_beneficiary_edit);
        branch_spinner=(Spinner) view.findViewById(R.id.select_bank_branch_sp_beneficiary_edit);
        country_code_sp1 = (Spinner) view.findViewById(R.id.country_code_sp1_edit);
        confirm_country_code_sp1 = (Spinner) view.findViewById(R.id.confirm_country_code_sp1_edit);

        bank_add_ly=(LinearLayout) view.findViewById(R.id.bank_add_ly_edit);

        bank_add=(Button) view.findViewById(R.id.add_bank_btn_beneficiary_edit);
        save=(Button) view.findViewById(R.id.save_btn_beneficiary_edit);


        save.setOnClickListener(this);
        bank_add.setOnClickListener(this);
        bank_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long arg3) {
                if (!bank_spinner.getSelectedItem().toString().equals("---Select---")) {
                    getBranchName(bank_spinner.getSelectedItem().toString());
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

        ed_fname.setText(beneficiaryDetailModel.getFName());
        ed_lname.setText(beneficiaryDetailModel.getLName());
        ed_email.setText(beneficiaryDetailModel.getEmail());
        ed_address.setText(beneficiaryDetailModel.getAddress());
        ed_city.setText(beneficiaryDetailModel.getCity());

        if(beneficiaryDetailModel.getMobile().startsWith("+254")){
            country_code_sp1.setSelection(0);
            confirm_country_code_sp1.setSelection(0);
            ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
            confirm_ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
        }
        else if(beneficiaryDetailModel.getMobile().startsWith("+1")){
            country_code_sp1.setSelection(3);
            confirm_country_code_sp1.setSelection(3);
            ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(2));
            confirm_ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(2));
        }
        else if(beneficiaryDetailModel.getMobile().startsWith("+263")){
            country_code_sp1.setSelection(2);
            confirm_country_code_sp1.setSelection(2);
            ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
            confirm_ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
        }
        else if(beneficiaryDetailModel.getMobile().startsWith("+256")){
            country_code_sp1.setSelection(1);
            confirm_country_code_sp1.setSelection(1);
            ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
            confirm_ed_mobile.setText(beneficiaryDetailModel.getMobile().trim().substring(4));
        }
        else{
            ed_mobile.setText(beneficiaryDetailModel.getMobile().trim());
            confirm_ed_mobile.setText(beneficiaryDetailModel.getMobile().trim());
        }

        if(beneficiaryDetailModel.getAccount_number().trim().equals("")){
            bank_add_ly.setVisibility(View.GONE);
        }
        else{
            bank_add_ly.setVisibility(View.VISIBLE);
            ed_account_name.setText(beneficiaryDetailModel.getAccount_name());
            ed_account_number.setText(beneficiaryDetailModel.getAccount_number());
            ed_confirm_account_number.setText(beneficiaryDetailModel.getAccount_number());
        }

    }

    private void getBranchName(String bank) {


        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();
        try
        {
            object.put("bank_name", bank);

            params.put("request", object.toString());

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("banking/getBankBranches", params, new BranchNameHandler(),Login.token);
    }

    class BranchNameHandler extends AsyncHttpResponseHandler{

        ProgressDialog pdialog;
        @Override
        public void onStart() {
            super.onStart();
            Log.d("Branch names onStart", "on start");
            pdialog=ProgressDialog.show(getActivity(), "", "Loading...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            Log.d("Branch names onSuccess", content);
            if(content.length()>0){

                try {

                    JSONObject json=new JSONObject(content);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if(1001 == response_code)
                        b = true;

                    if(b)
                    {
                        JSONArray jarray=json.optJSONArray("data");
                        branch_array.clear();
                        branch_array.add("---Select---");
                        try{
                            branchadapter.notifyDataSetChanged();
                        }
                        catch(NullPointerException e){
                            e.printStackTrace();
                        }

                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject J=jarray.optJSONObject(i);
                            branch_array.add(J.optString("branch_name").trim());
                        }

                        if(branch_array.size()!=0){

                            branchadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, branch_array);
                            branchadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                            branch_spinner.setAdapter(branchadapter);

                            if( (!beneficiaryDetailModel.getBranch_name().equals(null)) &&
                                    (bank_add_ly.getVisibility() == View.VISIBLE) ) {
                                int spinnerPosition = branchadapter.getPosition(beneficiaryDetailModel.getBranch_name().trim());
                                branch_spinner.setSelection(spinnerPosition);
                            }
                        }

                    }else{
                        branch_array.clear();
                        branch_array.add("---Select---");
                        try{
                            branchadapter.notifyDataSetChanged();
                        }
                        catch(NullPointerException e){
                            e.printStackTrace();
                        }
                        branchadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, branch_array);
                        branchadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                        branch_spinner.setAdapter(branchadapter);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        @Deprecated
        public void onFailure(Throwable error) {
            super.onFailure(error);
            Log.d("Branch names onFailure", "on failure");
        }

        @Override
        public void onFinish() {
            super.onFinish();
            Log.d("Branch names", "onFinish");
            if(pdialog.isShowing())
                pdialog.dismiss();
        }

    }






    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.add_bank_btn_beneficiary_edit:
                if(flag){
                    bank_add_ly.setVisibility(View.VISIBLE);
                    flag=false;
                }else{
                    flag=true;
                    bank_add_ly.setVisibility(View.GONE);
                }
                break;


            case R.id.save_btn_beneficiary_edit:
                Toast.makeText(getActivity(), "Edit button clicked!", Toast.LENGTH_SHORT).show();
                if(bank_add_ly.getVisibility()==View.VISIBLE){
                    if(validiationBank())
                        editBeneficiary();
                }
                else{
                    if(validiation())
                        editBeneficiary();
                }

                break;

            default:
                break;
        }
    }


    private void editBeneficiary() {

        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();
        try
        {

            //object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
            object.put("userid", new PrefManager<String>(getActivity().getApplicationContext()).get("userid","0"));
            object.put("beneficiary_id", Integer.parseInt(beneficiaryDetailModel.getBeniid()));
            object.put("f_name", ed_fname.getText().toString().trim());
            object.put("l_name", ed_lname.getText().toString().trim());
            object.put("msisdn", country_code+ed_mobile.getText().toString());
            object.put("email", ed_email.getText().toString());
            object.put("physical_address", ed_address.getText().toString());
            object.put("city", ed_city.getText().toString());
            object.put("country", country_spinner.getSelectedItem().toString());
            if(bank_add_ly.getVisibility()==View.VISIBLE)
            {
                object.put("bank_name", bank_spinner.getSelectedItem().toString());
                object.put("branch_name", branch_spinner.getSelectedItem().toString());
                object.put("account_name", ed_account_name.getText().toString());
                object.put("account_number", ed_account_number.getText().toString());
            }
            else{
                object.put("bank_name", "");
                object.put("branch_name", "");
                object.put("account_name", "");
                object.put("account_number", "");
            }
            params.put("request", object.toString());

        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.d("request", params.toString());

        RestHttpClient.postParams("banking/editBeneficiary", params, new EditBeneficiaryHandler(), Login.token);

    }



    class EditBeneficiaryHandler extends AsyncHttpResponseHandler{

        ProgressDialog pdialog;
        @Override
        public void onStart() {
            super.onStart();
            pdialog=ProgressDialog.show(getActivity(), "", "Editing...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);

            if(content.length()>0){

                try {

                    JSONObject json=new JSONObject(content);

                    boolean b = false;
                    int response_code = json.getInt("code");
                    if(1001 == response_code)
                        b = true;
                    String msg=json.optString("message");
                    if(b)
                    {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

                                //Intent i = new Intent(getActivity(), MainActivity.class);
                                //startActivity(i);

                                d.dismiss();
                                MainActivity.start_fragment(MainActivity.main_activity, new ManageBeneficiaries(), "ManageBeneficiaries");

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
                    else{
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                }
            }
        }
        @Override
        public void onFinish() {
            super.onFinish();
            if(pdialog.isShowing())
                pdialog.dismiss();
        }

    }


    private boolean validiation() {
        String f_name= ed_fname.getText().toString();
        String l_name= ed_lname.getText().toString();
        String mobile=ed_mobile.getText().toString();
        String confirm_mobile = confirm_ed_mobile.getText().toString();
        String email=ed_email.getText().toString().trim();
        String address=ed_address.getText().toString().trim();
        String city=ed_city.getText().toString().trim();
        String country = country_spinner.getSelectedItem().toString();
        country_code = country_code_sp1.getSelectedItem().toString();
        confirm_country_code = confirm_country_code_sp1.getSelectedItem().toString();

        boolean flag=false;


        if(! country_code.equals(confirm_country_code)){
            Toast.makeText(getActivity(), "Country code does not match with its confirmation", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length()==0)
        {

            Toast.makeText(getActivity(), "Mobile number cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else if(! mobile.equals(confirm_mobile)){
            Toast.makeText(getActivity(), "Mobile number does not match with its confirmation", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length() != 9 && country_code.equals("+254")){
            Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length() != 10 && country_code.equals("+1")){
            Toast.makeText(getActivity(), "Mobile number should have 10 digits", Toast.LENGTH_SHORT).show();
        }
        else if(email.length() > 0){
            if(!email.matches(Constant.emailPattern)){
                Toast.makeText(getActivity(), "Enter Valid Email Address", Toast.LENGTH_SHORT).show();
            }
        }

        else if(f_name.length()==0){
            Toast.makeText(getActivity(), "first name cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else if(l_name.length()==0){
            Toast.makeText(getActivity(), "Last name cannot be blank", Toast.LENGTH_SHORT).show();
        }

        else if(city.length()==0)
        {
            Toast.makeText(getActivity(), "City/Town cannot be blank", Toast.LENGTH_SHORT).show();

        }
		/*else if(country.equals("---Select---"))
		{
			Toast.makeText(getActivity(), "Select country", Toast.LENGTH_SHORT).show();

		}*/
        else{
            flag=true;
        }

        return flag;
    }
    private boolean validiationBank() {

        boolean flag=false;
        country_code = country_code_sp1.getSelectedItem().toString();
        confirm_country_code = confirm_country_code_sp1.getSelectedItem().toString();
        String f_name=ed_fname.getText().toString().trim();
        String l_name=ed_lname.getText().toString().trim();
        String mobile=ed_mobile.getText().toString();
        String confirm_mobile=confirm_ed_mobile.getText().toString();
        String email=ed_email.getText().toString().trim();
        String address=ed_address.getText().toString().trim();
        String city=ed_city.getText().toString().trim();
        String country = country_spinner.getSelectedItem().toString();
        String bank_name = bank_spinner.getSelectedItem().toString();
        String branch_name = "";
        try{
            branch_name = branch_spinner.getSelectedItem().toString();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        String account_name = ed_account_name.getText().toString().trim();
        String account_number=ed_account_number.getText().toString();
        String confirm_account_number = ed_confirm_account_number.getText().toString();


        if(! country_code.equals(confirm_country_code)){
            Toast.makeText(getActivity(), "Country code does not match with its confirmation", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length()== 0){
            Toast.makeText(getActivity(), "Enter mobile number", Toast.LENGTH_SHORT).show();
        }
        else if(! mobile.equals(confirm_mobile)){
            Toast.makeText(getActivity(), "Mobile number does not match with its confirmation", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length() != 9 && country_code.equals("+254")){
            Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
        }
        else if(mobile.length() != 10 && country_code.equals("+1")) {
            Toast.makeText(getActivity(), "Mobile number should have 10 digits", Toast.LENGTH_SHORT).show();
        }
        else if(email.length() > 0){
            if(!email.matches(Constant.emailPattern)){
                Toast.makeText(getActivity(), "Enter Correct Email", Toast.LENGTH_SHORT).show();
            }
        }
        else if(f_name.length()==0){
            Toast.makeText(getActivity(), "first name cannot be blank", Toast.LENGTH_SHORT).show();
        }
        else if(l_name.length()==0){
            Toast.makeText(getActivity(), "Last name cannot be blank", Toast.LENGTH_SHORT).show();
        }

        else if(city.length()==0)
        {
            Toast.makeText(getActivity(), "City cannot be blank", Toast.LENGTH_SHORT).show();

        }
		/*else if(country.equals("---Select---"))
		{
			Toast.makeText(getActivity(), "Please select country", Toast.LENGTH_SHORT).show();

		}*/
        else if(bank_name.equals("---Select---"))
        {
            Toast.makeText(getActivity(), "Please select bank name", Toast.LENGTH_SHORT).show();

        }
        else if( (branch_name.equals("---Select---")) || (branch_name.length()==0) || (branch_name.equals("")) )
        {
            Toast.makeText(getActivity(), "Please select branch name", Toast.LENGTH_SHORT).show();

        }
        else if(account_name.length()==0)
        {
            Toast.makeText(getActivity(), "Account name cannot be blank", Toast.LENGTH_SHORT).show();

        }
        else if(account_number.length()==0)
        {
            Toast.makeText(getActivity(), "Account number cannot be blank", Toast.LENGTH_SHORT).show();

        }
        else if(! account_number.equals(confirm_account_number))
        {
            Toast.makeText(getActivity(), "Account number does not match its confirmation", Toast.LENGTH_SHORT).show();

        }
        else{
            flag=true;
        }
        return flag;

    }
}
