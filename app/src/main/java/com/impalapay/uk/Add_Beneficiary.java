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
import android.view.WindowManager;
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
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Add_Beneficiary extends Fragment implements OnClickListener {
	
	boolean flag=true;
	View view;
	Button bank_add,save;
	TextView phone_code1,phone_code2;
	EditText ed_mobile,confirm_ed_mobile, ed_fname, ed_lname,ed_email,ed_address,ed_city,ed_account_name,
			ed_account_number,ed_confirm_account_number,name__beneficiary;
	Spinner country_spinner,bank_spinner,branch_spinner,country_code_sp1,confirm_country_code_sp1;
	LinearLayout bank_add_ly;
	String country;
	//Spinner mobile_code_sp;
	String countrywithcode, country_code,confirm_country_code;
	ArrayList<String> country_al = new ArrayList<String>();
	ArrayList<String> CountryCode_al = new ArrayList<String>();
	Locale local[]=Locale.getAvailableLocales();
	ArrayList<String> country_code_al = new ArrayList<String>();
	
	ArrayList<String> bankName_array = new ArrayList<String>();
	ArrayList<String> branch_array = new ArrayList<String>();

	ArrayAdapter<String> branchadapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.add_beneficiary, container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		Common_data.setupUI(view.findViewById(R.id.hide), getActivity());
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
		country_al.add("Uganda");
		country_al.add("Zimbabwe");
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
							
							ArrayAdapter<String> bankadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankName_array);
							bankadapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
							bank_spinner.setAdapter(bankadapter);
						}	
						
					}
					else{
						bankName_array.clear();
						bankName_array.add("---Select---");
						ArrayAdapter<String> bankadapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, bankName_array);
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
			
		ed_mobile=(EditText) view.findViewById(R.id.mobile_number_beneficiary);
		confirm_ed_mobile=(EditText) view.findViewById(R.id.confirm_mobile_number_beneficiary);
		ed_fname =(EditText) view.findViewById(R.id.fname__beneficiary);
		ed_lname =(EditText) view.findViewById(R.id.lname__beneficiary);
		ed_email=(EditText) view.findViewById(R.id.email__beneficiary);
		ed_address=(EditText) view.findViewById(R.id.address_beneficiary);
		ed_city=(EditText) view.findViewById(R.id.city_beneficiary);
		ed_account_name=(EditText) view.findViewById(R.id.account_name_beneficiary);
		ed_account_number=(EditText) view.findViewById(R.id.account_number_beneficiary);
		ed_confirm_account_number = (EditText) view.findViewById(R.id.confirm_account_number_beneficiary);
		phone_code1 = (TextView) view.findViewById(R.id.phone_code1);
		phone_code2 = (TextView) view.findViewById(R.id.phone_code2);
		//mobile_code_sp=(Spinner) view.findViewById(R.id.mobile_code_sp);

		//phone_code1.setOn
		
		country_spinner=(Spinner) view.findViewById(R.id.country_of_resdience_beneficiary);
		bank_spinner=(Spinner) view.findViewById(R.id.select_bank_sp_beneficiary);
		branch_spinner=(Spinner) view.findViewById(R.id.select_bank_branch_sp_beneficiary);
		country_code_sp1 = (Spinner) view.findViewById(R.id.country_code_sp1);
		confirm_country_code_sp1 = (Spinner) view.findViewById(R.id.confirm_country_code_sp1);

		country_code_sp1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				confirm_country_code_sp1.setSelection(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		confirm_country_code_sp1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				country_code_sp1.setSelection(i);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		
		bank_add_ly=(LinearLayout) view.findViewById(R.id.bank_add_ly);
		
		bank_add=(Button) view.findViewById(R.id.add_bank_btn_beneficiary);
		save=(Button) view.findViewById(R.id.save_btn_beneficiary);

		
		save.setOnClickListener(this);
		bank_add.setOnClickListener(this);
		bank_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> p, View v,int pos, long arg3) {
				if(! bank_spinner.getSelectedItem().toString().equals("---Select---")){
					getBranchName(bank_spinner.getSelectedItem().toString());
				}
				else{
					branch_array.clear();
					try{
						branchadapter.notifyDataSetChanged();
					}
					catch(NullPointerException e){
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
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
		case R.id.add_bank_btn_beneficiary:
			if(flag){
				bank_add_ly.setVisibility(View.VISIBLE);
				flag=false;
			}else{
				flag=true;
				bank_add_ly.setVisibility(View.GONE);
			}
			break;

			
		case R.id.save_btn_beneficiary:
			
			if(bank_add_ly.getVisibility()==View.VISIBLE){
				if(validiationBank())
					saveBeneficiary();
			}
			else{
				if(validiation())
					saveBeneficiary();
			}
			
			break;
			
		default:
			break;
		}
	}


	private void saveBeneficiary() {
		
		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try 
		{
			if(country_code.startsWith("+254")){
				country="Kenya";
			}else if(country_code.startsWith("+256")){
				country="Uganda";
			}else if(country_code.startsWith("+263")){
				country="Zimbabwe";
			}else if(country_code.startsWith("+1")){
				country="United States of America";
			}
			object.put("userid", Common_data.getPreferences(getActivity(), "userid"));
			object.put("f_name", ed_fname.getText().toString().trim());
			object.put("l_name", ed_lname.getText().toString().trim());
			object.put("msisdn", country_code+ed_mobile.getText().toString());
			object.put("email", ed_email.getText().toString());
			object.put("physical_address", ed_address.getText().toString());
			object.put("city", ed_city.getText().toString());
			object.put("country", country);

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
	
		RestHttpClient.postParams("banking/addBeneficiary", params, new AddBeneficiaryHandler(), Login.token);
		
	}


	
	class AddBeneficiaryHandler extends AsyncHttpResponseHandler{
		
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
								d.dismiss();
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
		else if(mobile.length() != 9 && country_code.equals("+256")){
			Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
		}
		else if(mobile.length() != 9 && country_code.equals("+263")){
			Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
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
		else if(mobile.length() != 9 && country_code.equals("+256")){
			Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
		}
		else if(mobile.length() != 9 && country_code.equals("+263")){
			Toast.makeText(getActivity(), "Mobile number should have 9 digits", Toast.LENGTH_SHORT).show();
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
