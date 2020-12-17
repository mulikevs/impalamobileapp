package com.impalapay.uk;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendMoney1_Frag extends Fragment{

	View view;
	Button next1,increase_limit_button;
	TextView pdetailsendvalue, pdetailrecevievalue,
			currency_base_tv,currency_convert_tv,pdetailfirst,fee_base_tv,total_cost_base_tv,
			daily_limit_tv,monthly_limit_tv;
	TextView tv_transfer_fees,total;
	EditText psendvalue;
	EditText precevievalue;
	Boolean sendAmount = false;

	Boolean conversion  = true;

	private String[] country_array;

	RadioButton radio_bank,radio_mpesa,radio_paybill,radio_ewallet, radio_Uganda_Airtel_Money,radio_Uganda_MTN_Money,radio_Zimbabwe_Telecash,radio_Zimbabwe_CashPickUP;
	Spinner sp_destination_country;
	LinearLayout ll_kenya, ll_uganda,ll_zimbabwe;

	static String destination = "";
	
	public static String base, convert;
	
	double tarifs = 0;

    double percent = 0;
    double abs = 0;
	String country_iso2;

	Double amount_enter_dou;
	public static double exResult_double = 0, exResult_double_rev=0, fees = 0;;
	String pamount_entersend = "1", pamount_enterreceve = "1",finalvalue = "1.00", userid="";
	String regex = "[0-9.]+";

	public static String version_name = "";
		
	
	//-----
	static double send,total_am,commission;
	static double receve;
	 static String type = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.send_money1, container, false);
		return view;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//base= Common_data.getPreferences(getActivity(), "base");
		base = new PrefManager<String>(getActivity().getApplicationContext()).get("base","");
		if(base.equalsIgnoreCase("CAD")){
			country_iso2="ca";
		}else if(base.equalsIgnoreCase("USD")){
			country_iso2="us";
		}else{

		}
		init();
		//Common_data.setupUI(view.findViewById(R.id.hide),getActivity());
		
		
		
	//	pdetailsendvalue.setText(Sawapay_Main_Screen.crncy_snd_sybl + "1.0 -");
	//	pdetailrecevievalue.setText(Sawapay_Main_Screen.crncy_recv_sybl+" "+convert);
		//geDeliveryCountries(country_iso2);
		try {
			getDeliveryCountries();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		convert = "KES";
		getCurrentConversionRate();
		
	}




	@Override
	public void onStart() {
		super.onStart();
	/*	PinLockControl pin=(PinLockControl) getActivity().getApplicationContext();
		String a=Common_data.getPreferences(getActivity(), "alertshow");
		//Toast.makeText(getActivity(),"Alert "+ a+ pin.isVisible(), Toast.LENGTH_LONG).show();
		if(a.equals("false"))
		{
			
			pin.isStop();
			Common_data.setPreference(getActivity(), "alertshow", "true");
		}*/
	}
	
	private void init() {

		//FROM SHARED PREFERENCE
		//base = Common_data.getPreferences(getActivity().getApplicationContext(), "base");
		//convert = Common_data.getPreferences(getActivity().getApplicationContext(), "convert");
		//userid = Common_data.getPreferences(getActivity().getApplicationContext(), "userid");
		base = new PrefManager<String>(getActivity().getApplicationContext()).get("base","USD");
		convert = new PrefManager<String>(getActivity().getApplicationContext()).get("convert","test");
		userid = new PrefManager<String>(getActivity().getApplicationContext()).get("userid","userid");
		Sawapay_Main_Screen.base = base;
		Sawapay_Main_Screen.convert = convert;
		
		currency_base_tv=(TextView) view.findViewById(R.id.currency_base_tv);
		fee_base_tv=(TextView) view.findViewById(R.id.fee_base_tv);
		total_cost_base_tv=(TextView) view.findViewById(R.id.total_cost_base_tv);
		currency_convert_tv=(TextView) view.findViewById(R.id.currency_convert_tv);
		pdetailfirst = (TextView) view.findViewById(R.id.pdetailfirst);
		
		currency_base_tv.setText(base);
		fee_base_tv.setText(base);
		total_cost_base_tv.setText(base);
		currency_convert_tv.setText(convert);
		
		
		
		psendvalue = (EditText) view.findViewById(R.id.psendvalue);
		precevievalue = (EditText) view.findViewById(R.id.precievevalue);
		psendvalue.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});
		//precevievalue.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,0)});

		psendvalue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b){
					sendAmount = true;
				}
			}
		});

		precevievalue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				if (b){
					sendAmount = false;
				}
			}
		});

		total = (TextView) view.findViewById(R.id.total);
		tv_transfer_fees = (TextView) view.findViewById(R.id.tv_transfer_fees);
		increase_limit_button = (Button) view.findViewById(R.id.increase_limit_button);
		daily_limit_tv = (TextView) view.findViewById(R.id.daily_limit_tv);
		monthly_limit_tv = (TextView) view.findViewById(R.id.monthly_limit_tv);

		pdetailsendvalue=(TextView) view.findViewById(R.id.pdetailsendvalue);
		pdetailrecevievalue=(TextView) view.findViewById(R.id.pdetailrecevievalue);

		ll_kenya = (LinearLayout) view.findViewById(R.id.ll_kenya);
		ll_uganda = (LinearLayout) view.findViewById(R.id.ll_uganda);
		ll_zimbabwe = (LinearLayout) view.findViewById(R.id.ll_zimbabwe);
		sp_destination_country = (Spinner) view.findViewById(R.id.sp_destination_country);
		radio_bank=(RadioButton) view.findViewById(R.id.radio_BANK);
		radio_mpesa=(RadioButton) view.findViewById(R.id.radio_MPESA);
		radio_paybill=(RadioButton) view.findViewById(R.id.radio_pay);
		radio_Uganda_Airtel_Money = (RadioButton) view.findViewById(R.id.radio_Uganda_Airtel_Money);
		radio_Uganda_MTN_Money = (RadioButton) view.findViewById(R.id.radio_Uganda_MTN_Money);
		radio_Zimbabwe_Telecash = (RadioButton) view.findViewById(R.id.radio_Zimbabwe_Telecash);
		radio_Zimbabwe_CashPickUP = (RadioButton) view.findViewById(R.id.radio_Zimbabwe_pickup);
		radio_ewallet=(RadioButton) view.findViewById(R.id.radio_eWallet);


		increase_limit_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), TransferLimit.class);
				startActivity(i);
			}


		});
		
		next1=(Button) view.findViewById(R.id.next1);
		
		next1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(validation()){

					if( destination.equalsIgnoreCase("Kenya") && (radio_paybill.isChecked() || type.equals("paybill")) ){
						Intent i = new Intent(getActivity(), SendMoney_PayBill.class);
						startActivity(i);
					}else {
						Log.d("pamont_enterreceive", "here " + pamount_enterreceve);
						//int rec_amt = Integer.parseInt(pamount_enterreceve);
						Double rec_amt = Double.parseDouble(precevievalue.getText().toString().replace(",", ""));
						if( (destination.equalsIgnoreCase("Kenya")) &&
								(radio_mpesa.isChecked() || type.equals("mpesa"))  && rec_amt > 70000 ){
							if (!sendAmount){
								Toast.makeText(getContext(), "The Maximum Receive Amount to an M-PESA Account is KES 70,000", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(getContext(), "The Maximum Receive Amount to an M-PESA Account is KES 70,000", Toast.LENGTH_SHORT).show();
							}
							return;
						}
						else if( (destination.equalsIgnoreCase("Uganda")) &&
								(radio_Uganda_Airtel_Money.isChecked() ||
										type.equals("uganda_airtel_money")) && rec_amt > 1800000  ){
							if (!sendAmount){
								Toast.makeText(getContext(), "The Maximum Receive Amount to Uganda Airtel Money Account is UGX 1,800,000", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(getContext(), "The Maximum Receive Amount to Uganda Airtel Money Account is UGX 1,800,000", Toast.LENGTH_SHORT).show();
							}
							return;
						}
						else if( (destination.equalsIgnoreCase("Uganda")) &&
								(radio_Uganda_MTN_Money.isChecked() ||
										type.equals("uganda_mtn_money")) && rec_amt > 1800000  ){
							if (!sendAmount){
								Toast.makeText(getContext(), "The Maximum Receive Amount to  Uganda MTN Money Account is UGX 1,800,000", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(getContext(), "The Maximum Receive Amount to  Uganda MTN Money Account is UGX 1,800,000", Toast.LENGTH_SHORT).show();
							}
							return;
						}else if( (destination.equalsIgnoreCase("Zimbabwe")) &&
								(radio_Zimbabwe_Telecash.isChecked() ||
										type.equals("zimbabwe_telecash")) && rec_amt > 500  ){
							if (!sendAmount){
								Toast.makeText(getContext(), "The Maximum Receive Amount to Zimbabwe TeleCash Account is USD 500", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(getContext(), "The Maximum Receive Amount to Zimbabwe TeleCash Account is USD 500", Toast.LENGTH_SHORT).show();
							}
							return;
						}
						else if( (destination.equalsIgnoreCase("Zimbabwe")) &&
								(radio_Zimbabwe_CashPickUP.isChecked() ||
										type.equals("zimbabwe_pickup")) && rec_amt > 500  ){
							/*if (!sendAmount){
								Toast.makeText(getContext(), "The Maximum Receive Amount using our Pickup Method is USD 500", Toast.LENGTH_SHORT).show();
							}else {
								Toast.makeText(getContext(), "The Maximum Receive Amount using our Pickup Method is USD 500", Toast.LENGTH_SHORT).show();
							}
							return;*/
						}if( destination.equalsIgnoreCase("Zimbabwe") && (radio_Zimbabwe_CashPickUP.isChecked() || type.equals("zimbabwe_pickup")) ){
							Intent i = new Intent(getActivity(), SendMoneyCashPickup.class);
							startActivity(i);

						}
						else{
							//Toast.makeText(getActivity(), type, Toast.LENGTH_SHORT).show();

							Intent i = new Intent(getActivity(), SendMoney2.class);
							startActivity(i);
						}
					}
				}
			}

			
		});
		
		
		psendvalue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				try {
					Handler handler = new Handler();
					if (s.length() != 0) {

						if (precevievalue.isFocused()) {

						} else {

								psendvalue.removeTextChangedListener(this);

								psendvalue.setText(s);

								psendvalue.addTextChangedListener(this);
								psendvalue.setSelection(s.length());

							final Runnable r = new Runnable() {

								public void run() {

								
									if (convert == "0") {
										
										pdetailsendvalue.setText("");
										pdetailrecevievalue.setText("");
									} else {
										getdata();
										if (pamount_entersend.matches(regex)) {
                                            Log.i("0d FX Rate ",String.valueOf(exResult_double));

											amount_enter_dou = Double.parseDouble(pamount_entersend);
											if(destination.equalsIgnoreCase("Zimbabwe")){
												exResult_double=Double.parseDouble(String.valueOf(truncateDecimal(exResult_double, 4)));
												Log.i("0d FX Rate ",String.valueOf(exResult_double));
											}else{
												exResult_double=Double.parseDouble(String.valueOf(truncateDecimal(exResult_double, 2)));
												Log.i("2d FX Rate ",String.valueOf(exResult_double));
											}
											
											double temp=amount_enter_dou * exResult_double;

											/*String t=temp+"";
											precevievalue.setText(t.substring(0,t.lastIndexOf(".")));*/

											//precevievalue.setText("67");
											BigDecimal a = new BigDecimal(temp);
											BigDecimal roundDown;

											DecimalFormat formatter = new DecimalFormat("#,###");
											if(destination.equalsIgnoreCase("Zimbabwe")){
												 roundDown = a.setScale(2, BigDecimal.ROUND_DOWN);
												precevievalue.setText(String.valueOf(roundDown));
											}else{
											 roundDown = a.setScale(0, BigDecimal.ROUND_DOWN);
												precevievalue.setText(String.valueOf(formatter.format(roundDown)));
											}

											Log.i("Receive",String.valueOf(roundDown));

											//double receiveval = Double.parseDouble(precevievalue.getText().toString());
											//double fees = (receiveval * tarifs) / 100;

											try {
												fees = getTransferFeeRate(base,convert,amount_enter_dou);
												Log.i("Fee",String.valueOf(fees));
											} catch (JSONException e) {
												e.printStackTrace();
											}
											DecimalFormat rateForm = new DecimalFormat("#.##");
											
											tv_transfer_fees.setText(rateForm.format(fees) + "");
											//double finalvaule=receiveval - fees;
												double finalvaule = amount_enter_dou +fees;
											total.setText(rateForm.format((finalvaule)) + "");

										}
										else{
											precevievalue.setText("");
											tv_transfer_fees.setText("0");
											total.setText("0");
										}

									}
								
									
										
								}
							};
							handler.postDelayed(r, 100);

						}
					}
					else{
						if (precevievalue.isFocused()) {

						} else {
							precevievalue.setText("");
							tv_transfer_fees.setText("0");
							total.setText("0");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}
		});

		precevievalue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

				try {
					Handler handler = new Handler();
					if (s.length() != 0) {

						if (psendvalue.isFocused()) {

						} else {

								precevievalue.removeTextChangedListener(this);

								precevievalue.setText(s);

								precevievalue.addTextChangedListener(this);
								precevievalue.setSelection(s.length());
		
							final Runnable r = new Runnable() {

								public void run() {
									
									if (base == "0") {
										pdetailsendvalue.setText("");
										pdetailrecevievalue.setText("");
									} else {
										
										getdata();
										if (pamount_enterreceve.matches(regex)) {
											amount_enter_dou = Double.parseDouble(pamount_enterreceve);
											DecimalFormat twoDForm = new DecimalFormat("#.##");
											if(destination.equalsIgnoreCase("Zimbabwe")){
												exResult_double=Double.parseDouble(String.valueOf(truncateDecimal(exResult_double, 4)));
												Log.i("0d FX Rate ",String.valueOf(exResult_double));
											}else{
												exResult_double=Double.parseDouble(String.valueOf(truncateDecimal(exResult_double, 2)));
												Log.i("2d FX Rate ",String.valueOf(exResult_double));
											}

											//exResult_double_rev=Double.valueOf(twoDForm.format(exResult_double_rev));
											try {
												fees = getTransferFeeRate(base,convert,(amount_enter_dou / exResult_double)); //SHIDA IKO HAPA
												Log.i("Fee",String.valueOf(fees));
											} catch (JSONException e) {
												e.printStackTrace();
											}

											//fees =0.0;
											//double temp=amount_enter_dou * exResult_double_rev;
											double t=(amount_enter_dou / exResult_double);
											BigDecimal a = new BigDecimal(t);
											BigDecimal temp = a.setScale(2, BigDecimal.ROUND_UP);
											Log.d("send Amount", String.valueOf(temp));
											
											psendvalue.setText(String.valueOf(truncateDecimal(t, 2)));
											//psendvalue.setText(String.valueOf(temp).trim());
											double receiveval = Double.parseDouble(psendvalue.getText().toString());
											//double fees = (receiveval * tarifs) / 100;

											DecimalFormat rateForm = new DecimalFormat("#.##");
											
											tv_transfer_fees.setText(rateForm.format(fees) + "");
											
											double finalvaule=receiveval + fees;
											//total.setText(rateForm.format(finalvaule) + "");
											total.setText(rateForm.format(finalvaule) + "");
										}else{
											psendvalue.setText("");
											tv_transfer_fees.setText("0");
											total.setText("0");
										}

									}
								}
							};
							handler.postDelayed(r, 100);
						}
					}
					else{
						
						if (psendvalue.isFocused()) {

						} else {
								psendvalue.setText("");
								tv_transfer_fees.setText("0");
								total.setText("0");
						}
						
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}
	
	private void setDataInStatic() {
		send=Double.parseDouble(psendvalue.getText().toString());
		receve=Double.parseDouble(precevievalue.getText().toString().replace(",", ""));
		total_am=Double.parseDouble(total.getText().toString());
		commission=Double.parseDouble(tv_transfer_fees.getText().toString());


		type = "";

		if(destination.equalsIgnoreCase("Kenya")){
			if(radio_bank.isChecked())
				type="bank";
			else if(radio_mpesa.isChecked())
				type="mpesa";
			else if(radio_ewallet.isChecked())
				type="ewallet";
			else if(radio_paybill.isChecked())
				type="paybill";
		}
		else if(destination.equalsIgnoreCase("Uganda")){
			if(radio_Uganda_Airtel_Money.isChecked()){
				type="uganda_airtel_money";
			}
			else if(radio_Uganda_MTN_Money.isChecked()){
				type="uganda_mtn_money";
			}
		}
		else if(destination.equalsIgnoreCase("Zimbabwe")) {
			if (radio_Zimbabwe_Telecash.isChecked()) {
				type = "zimbabwe_telecash";
			} else if (radio_Zimbabwe_CashPickUP.isChecked()) {
				type = "zimbabwe_cash_pickup";
			}
		}
	}
	
	
	//VALIDATION METHOD
	private boolean validation() {

		if (psendvalue.getText().toString().trim().length() == 0) {
			Toast.makeText(getActivity(), "Enter Send Value", Toast.LENGTH_SHORT).show();
			return false;
		} else if (precevievalue.getText().toString().length() == 0) {
			Toast.makeText(getActivity(), "Enter Receive Value", Toast.LENGTH_SHORT).show();
			return false;
		} else if (Double.parseDouble(psendvalue.getText().toString()) == 0) {
			Toast.makeText(getActivity(), "Invalid Sending Amount", Toast.LENGTH_SHORT).show();
			return false;
		} else if (Double.parseDouble(psendvalue.getText().toString()) > 1000 && base.equalsIgnoreCase("CAD")) {
			Toast.makeText(getActivity(), "Send Amount should not Exceed CAD 1,000", Toast.LENGTH_SHORT).show();
			return false;
		} else if (Double.parseDouble(precevievalue.getText().toString().replace(",", "")) == 0) {
			Toast.makeText(getActivity(), "Invalid Receive Amount", Toast.LENGTH_SHORT).show();
			return false;
		}else if (sp_destination_country.getSelectedItem() == "Delivery Country") {
			Toast.makeText(getActivity(), "Please Select Delivery Country", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(Double.parseDouble(precevievalue.getText().toString().replace(",", "")) < 500 && destination.equalsIgnoreCase("Kenya")){
			Toast.makeText(getActivity(), "Receive Amount should not be less than KES 500", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(Double.parseDouble(precevievalue.getText().toString().replace(",", "")) < 5000 && destination.equalsIgnoreCase("Uganda")){
			Toast.makeText(getActivity(), "Receive Amount should not be less than UGX 5000", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(Double.parseDouble(precevievalue.getText().toString().replace(",", "")) < 2 && destination.equalsIgnoreCase("Zimbabwe")){
			Toast.makeText(getActivity(), "Receive Amount should not be less than USD 2", Toast.LENGTH_SHORT).show();
			return false;
		}

		setDataInStatic();

		if(type.equals("")){
			Toast.makeText(getActivity(), "Select Delivery Method", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	
	//GETTING INPUT VALUE
	private void getdata() {
		try {
			pamount_entersend=psendvalue.getText().toString();
			pamount_enterreceve=precevievalue.getText().toString();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//GETTING CURRENT FX RATE
	private void getCurrentConversionRate() {
		precevievalue.getText().clear();
		psendvalue.getText().clear();

		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try 
		{
			object.put("cur_from", base);
			//object.put("cur_from", "USD");
			object.put("cur_to", convert);
			
			params.put("request", object.toString());
			Log.d("ConversionRate", params.toString());
		} 

		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		Log.d("request", params.toString());

		RestHttpClient.postParams("auth/getConversionRate", params,new ConversionRateHandler(),Login.token);
	}
	
	class ConversionRateHandler extends AsyncHttpResponseHandler{
		
		
		ProgressDialog pdialog;
		
		@Override
		public void onStart() {
			super.onStart();
			pdialog=ProgressDialog.show(getActivity(), "", "Getting FX Rate...");
		}
		
		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);
			
			if(content.length()>0){
				
				try {
					
					JSONObject json=new JSONObject(content);
					Log.d("Response array", json.toString());
					//boolean b=json.optBoolean("result");
					boolean b = false;
					int response_code = json.getInt("code");
					String msg = json.getString("message");
					Log.d("Response Code", String.valueOf(response_code));
					if(1001 == response_code)
						b = true;
					
					if(b)
					{
						//boolean dd=getArguments().getBoolean("checked");
						//if(!dd)
							radio_mpesa.setChecked(true);
						//else
							//radio_paybill.setChecked(true);
						
						JSONArray array=json.optJSONArray("data");
						Log.d("data array", array.toString());
						JSONObject jobj=array.optJSONObject(0);
						
						 exResult_double = Double.parseDouble(jobj.optString("amount"));
						Log.d("Dest", "" + destination);
						Log.d("Amount", "" + exResult_double);
						 //exResult_double_rev=1/exResult_double;

//						if(SpleshScreen.fx_rate != exResult_double){
//							pdetailfirst.setText("Your Welcome Rate: ");
//						}
						 
						 pdetailsendvalue.setText(Sawapay_Main_Screen.base + " 1.0 - ");

						if(destination.equalsIgnoreCase("Kenya") || destination.equalsIgnoreCase("Uganda") ){
							finalvalue = String.format("% .2f", exResult_double);
						}else{
							finalvalue = String.valueOf(truncateDecimal(exResult_double, 4));
						}

						 pdetailrecevievalue.setText(convert + " " + finalvalue);
						 
						 psendvalue.setHint("1.00".trim());
						 int receive_hint = (int) exResult_double;
						 //precevievalue.setHint(String.format("% .0f", exResult_double));
						 precevievalue.setHint(String.valueOf(receive_hint));
						 //getTariffsValue();

						getTransferLimits();
					}
					else if(1010 == response_code){
						final Dialog d=new Dialog(getActivity());
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
								Intent i = new Intent(getActivity(), MainActivity.class);
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
				}catch(Exception e){
					
				}
			}
		}
		@Override
		public void onFinish() {
			super.onFinish();
			if(pdialog.isShowing())
				pdialog.dismiss();

				

		}
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog(getActivity(),new SendMoney1_Frag());
		}
	}
	
	
	
	
	//GETTING TRAIFFS VALUE
	private void getTariffsValue() {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();
        try {
            object.put("cur_from", base);
            object.put("cur_to", convert);

            params.put("request", object.toString());
            Log.d("getTariffsValue", params.toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("request", params.toString());
		//RestHttpClient.post("tariffplan", new TarifsHandler());
        RestHttpClient.postParams("auth/getTariffRate", params, new TariffsHandler(), Login.token);
	}

	class TariffsHandler extends AsyncHttpResponseHandler {

		ProgressDialog p;

		@Override
		public void onStart() {
			super.onStart();

			p = ProgressDialog.show(getActivity(), "", "Getting Tariff...");
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);
            Log.d("Response", content);
			if (content.length() > 0) {
				try {
					JSONObject json = new JSONObject(content);
                    boolean b = false;
                    int response_code = json.getInt("code");
					String msg = json.getString("message");
                    if(1001 == response_code)
                        b = true;
					if (b) {
						JSONArray array = json.getJSONArray("data");
						JSONObject object = array.getJSONObject(0);
                            percent = object.optDouble("sawapay_percent");
							abs = object.optDouble("sawapay_abs");
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
					else if(1010 == response_code){
						final Dialog d=new Dialog(getActivity());
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
								Intent i = new Intent(getActivity(), MainActivity.class);
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
					else {
						//tarifs = 0;
						final Dialog d=new Dialog(getActivity());
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText("Error Occured");
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("Retry");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								Intent i = new Intent(getActivity(), MainActivity.class);
								startActivity(i);
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
			if (p != null && p.isShowing())
				p.dismiss();
			//networkErrorDialog(getActivity(),new EwalletDemo());
		
		}

		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog(getActivity(),new SendMoney1_Frag());
		}
	}


	private void geDeliveryCountries(String country_iso2) {
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

		RestHttpClient.postParams("auth/getActiveCountries", params, new GetCountry());

	}

	/******* Async Task class for running background Services *******/
	class GetCountry extends AsyncHttpResponseHandler
	{
		ProgressDialog pg;
		@SuppressWarnings("static-access")
		@Override
		public void onStart()
		{
			pg = ProgressDialog.show(getActivity(), "", "Getting Delivery Countries...");
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result)
		{
			Log.i("response", result.toString());

			super.onSuccess(result);
			Log.i("response", result.toString());
			pg.dismiss();
			try {
				if (result.length() > 0)
				{
					Log.i("response", "" + result);
					Log.d("response", "" + result);
					JSONObject jsonObject=new JSONObject(result);
					int response_code = jsonObject.getInt("code");
					String message = jsonObject.getString("message");
					JSONArray country_data = jsonObject.getJSONArray("data");
					if(1001 == response_code)
					{
						Log.d("country_data jsonarray", country_data.toString());
						country_array = new String[country_data.length()+1];
						country_array[0] = "Delivery Country";

						for (int i = 0; i < country_data.length(); i++) {
							JSONObject state_data_object = country_data.getJSONObject(i);
							country_array[i+1] = state_data_object.getString("destination");
						}
						//Collections.sort(country_array);
						ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, country_array);
						countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
						sp_destination_country.setAdapter(countryAdapter);
						int spinnerPosition = countryAdapter.getPosition("Kenya");
						sp_destination_country.setSelection(spinnerPosition);
						sp_destination_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> p, View v, int pos, long arg3) {
								if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Kenya")) {
									destination = "Kenya";
									precevievalue.setInputType(InputType.TYPE_CLASS_NUMBER);
									regex = "[0-9.]+";
									ll_kenya.setVisibility(View.VISIBLE);
									ll_uganda.setVisibility(View.GONE);
									ll_zimbabwe.setVisibility(View.GONE);
									convert = "KES";
									//Common_data.setPreference(getActivity(), "convert", convert);
									//Common_data.setPreference(getActivity(), "base", base);
									new PrefManager<String>(getActivity()).set("convert",convert);
									new PrefManager<String>(getActivity()).set("base",base);

									currency_convert_tv.setText(convert);

									//precevievalue.setText("");
									//psendvalue.setText("");

									if(conversion){
										conversion = false;
									}
									else{
										getCurrentConversionRate();
									}
								}
								if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Uganda")) {
									destination = "Uganda";
									regex = "[0-9.]+";
									precevievalue.setInputType(InputType.TYPE_CLASS_NUMBER);
									ll_uganda.setVisibility(View.VISIBLE);
									ll_kenya.setVisibility(View.GONE);
									ll_zimbabwe.setVisibility(View.GONE);
									convert = "UGX";
									//Common_data.setPreference(getActivity(), "convert", convert);
									//Common_data.setPreference(getActivity(), "base", base);
									new PrefManager<String>(getActivity()).set("convert",convert);
									new PrefManager<String>(getActivity()).set("base",base);

									currency_convert_tv.setText(convert);

									//precevievalue.setText("");
									//psendvalue.setText("");

									if(conversion){
										conversion = false;
									}
									else{
										getCurrentConversionRate();
									}
								}
								if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Zimbabwe")) {
									destination = "Zimbabwe";
									regex = "(-?[0-9]+(\\.[0-9]+)?)";
									ll_zimbabwe.setVisibility(View.VISIBLE);
									ll_kenya.setVisibility(View.GONE);
									ll_uganda.setVisibility(View.GONE);
									convert = "USD";
									//Common_data.setPreference(getActivity(), "convert", convert);
									//Common_data.setPreference(getActivity(), "base", base);

									new PrefManager<String>(getActivity()).set("convert",convert);
									new PrefManager<String>(getActivity()).set("base",base);

									currency_convert_tv.setText(convert);

									//precevievalue.setText("");
									//psendvalue.setText("");

									if(conversion){
										conversion = false;
									}
									else{
										getCurrentConversionRate();
									}
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});


					}else if(1030 == response_code){
						Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(getActivity());
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

								getActivity().finish();
							}
						});
						d.show();
					}
					else {
						Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(getActivity());
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
			pg.dismiss();
			//networkErrorDialog();
		}

		@Override
		public void onFinish()
		{
			super.onFinish();
			pg.dismiss();
		}
	}
	
	
	
	
	
	//network error dialog
	 public void networkErrorDialog(Activity act, final Fragment f){
			 
			 final Dialog d=new Dialog(act);
			 d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			 d.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 d.setCancelable(false);
			 d.setContentView(R.layout.network_error);
			 
			 Button tryagain=(Button) d.findViewById(R.id.try_again);
			 tryagain.setText("close");
			 tryagain.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
							/*FragmentManager fm = getFragmentManager();
			  				FragmentTransaction ft = fm.beginTransaction();
			  				ft.replace(R.id.lk_profile_fragment, f);
			  				ft.addToBackStack(null);
			  				ft.commit();
			  				d.dismiss();*/
					Intent i=new Intent(getActivity(),MainActivity.class);
					startActivity(i);
				}
			});
			 d.show();
			 
		 }



	private void getTransferLimits() {
		RequestParams params=new RequestParams();
		JSONObject object=new JSONObject();
		try
		{
			object.put("user_id", userid);

			params.put("request", object.toString());
			Log.d("getTransferLimits", params.toString());
		}

		catch (JSONException e)
		{
			e.printStackTrace();
		}
		Log.d("request", params.toString());

		RestHttpClient.postParams("auth/getTransferLimits", params, new TransferLimitsHandler(), Login.token);
	}

	class TransferLimitsHandler extends AsyncHttpResponseHandler{


		ProgressDialog pdialog;

		@Override
		public void onStart() {
			super.onStart();
			pdialog=ProgressDialog.show(getActivity(), "", "Getting Transfer Limits...");
		}

		@Override
		@Deprecated
		public void onSuccess(String content) {
			super.onSuccess(content);

			if(content.length()>0){

				try {

					JSONObject json=new JSONObject(content);
					Log.d("Response array", json.toString());
					//boolean b=json.optBoolean("result");
					boolean b = false;
					int response_code = json.getInt("code");
					String msg = json.getString("message");
					Log.d("Response Code", String.valueOf(response_code));
					if(1001 == response_code)
						b = true;

					if(b)
					{
						JSONObject jobj = json.optJSONObject("data");
						Log.d("data object", jobj.toString());

						daily_limit_tv.setText("Your Daily Transfer Limit: " +base+ " "+ jobj.getString("daily_transfer_limit"));
						monthly_limit_tv.setText("Your Monthly Transfer Limit: "+base+" "+jobj.getString("monthly_transfer_limit"));
					}
					else if(1010 == response_code){
						final Dialog d=new Dialog(getActivity());
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
								Intent i = new Intent(getActivity(), MainActivity.class);
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
				}catch(Exception e){

				}
			}
		}
		@Override
		public void onFinish() {
			super.onFinish();
			if(pdialog.isShowing())
				pdialog.dismiss();



		}
		@Override
		@Deprecated
		public void onFailure(Throwable error) {
			super.onFailure(error);
			networkErrorDialog(getActivity(),new SendMoney1_Frag());
		}
	}

//-------------------------


	public class DecimalDigitsInputFilter implements InputFilter {

		Pattern mPattern;

		public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
			mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

			Matcher matcher=mPattern.matcher(dest);
			if(!matcher.matches())
				return "";
			return null;
		}

	}

	public Double getTransferFeeRate(String base,String cur, Double amount) throws JSONException {

		double fee_amount = 0;
		double max_amount;
		double min_amount;
		String currency_to,currency_from;
		Log.d("Passed Items", "Base "+base+" Currency "+cur+" Amount "+amount);
		//JSONObject feeData= new JSONObject(Common_data.getPreferences(getActivity(),"transferFees"));
		JSONObject feeData= new JSONObject(new PrefManager<String>(getActivity().getApplicationContext()).get("transferFees",""));
		//Log.i("Preference Fees ",feeData.toString());
		System.out.println("TRANSFERFEESFROMPREFERENCE"+feeData.toString());

		JSONArray all= feeData.getJSONArray("data");

		//CONSIDER PLACING THEM UNDER HASHMAP BEFORE EXTRACTION IF A FIELD IS EMPTY AN ERROR IS THROWN
		for(int i=0; i<all.length(); i++) {
			JSONObject obj = all.getJSONObject(i);

				max_amount = Double.parseDouble(obj.getString("max_amount"));
				min_amount = Double.parseDouble(obj.getString("min_amount"));
				currency_to = obj.getString("currency_to");
				currency_from = obj.getString("currency_from");

			if((amount >= min_amount)  && (amount <= max_amount) && (currency_to .equalsIgnoreCase(cur)) && (currency_from.equalsIgnoreCase(base))) {
				fee_amount=Double.parseDouble(obj.getString("transfer_fee"));
				Log.i("Fee Returned",String.valueOf(fee_amount));

System.out.println("FEES CALCULATION amount "+amount+" >=minimum amount "+min_amount+" <="+max_amount+" currency equals to "+currency_from.equalsIgnoreCase(base)+" fee amoungt "+fee_amount);

			}


		}


		return  fee_amount;

	}

	private static BigDecimal truncateDecimal(double x, int numberofDecimals)
	{
		if ( x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
		}
	}



	private void getDeliveryCountries() throws JSONException{
		//JSONArray countries = new JSONArray(Common_data.getPreferences(getActivity().getApplicationContext(), "delivery_countries"));
		JSONArray countries = new JSONArray(new PrefManager<String>(getActivity().getApplicationContext()).get("delivery_countries","delivery_countries"));
		Log.d("country_data jsonarray", countries.toString());
		System.out.println("country_data jsonarray"+countries.toString());
		country_array = new String[countries.length()+1];
		country_array[0] = "Delivery Country";

		for (int i = 0; i < countries.length(); i++) {
			JSONObject state_data_object = countries.getJSONObject(i);
			country_array[i+1] = state_data_object.getString("destination");
		}
		//Collections.sort(country_array);
		ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(),R.layout.custom_spinner_text, country_array);
		countryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		sp_destination_country.setAdapter(countryAdapter);
		int spinnerPosition = countryAdapter.getPosition("Kenya");
		sp_destination_country.setSelection(spinnerPosition);
		sp_destination_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> p, View v, int pos, long arg3) {
				if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Kenya")) {
					destination = "Kenya";
					precevievalue.setInputType(InputType.TYPE_CLASS_NUMBER);
					regex = "[0-9.]+";
					ll_kenya.setVisibility(View.VISIBLE);
					ll_uganda.setVisibility(View.GONE);
					ll_zimbabwe.setVisibility(View.GONE);
					convert = "KES";
					//Common_data.setPreference(getActivity(), "convert", convert);
					//Common_data.setPreference(getActivity(), "base", base);
					new PrefManager<String>(getActivity()).set("convert",convert);
					new PrefManager<String>(getActivity()).set("base",base);
					currency_convert_tv.setText(convert);

					//precevievalue.setText("");
					//psendvalue.setText("");

					if(conversion){
						conversion = false;
					}
					else{
						getCurrentConversionRate();
					}
				}
				if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Uganda")) {
					destination = "Uganda";
					regex = "[0-9.]+";
					precevievalue.setInputType(InputType.TYPE_CLASS_NUMBER);
					ll_uganda.setVisibility(View.VISIBLE);
					ll_kenya.setVisibility(View.GONE);
					ll_zimbabwe.setVisibility(View.GONE);
					convert = "UGX";
					//Common_data.setPreference(getActivity(), "convert", convert);
					//Common_data.setPreference(getActivity(), "base", base);
					new PrefManager<String>(getActivity()).set("convert",convert);
					new PrefManager<String>(getActivity()).set("base",base);
					currency_convert_tv.setText(convert);

					//precevievalue.setText("");
					//psendvalue.setText("");

					if(conversion){
						conversion = false;
					}
					else{
						getCurrentConversionRate();
					}
				}
				if (sp_destination_country.getSelectedItem().toString().equalsIgnoreCase("Zimbabwe")) {
					destination = "Zimbabwe";
					regex = "(-?[0-9]+(\\.[0-9]+)?)";
					ll_zimbabwe.setVisibility(View.VISIBLE);
					ll_kenya.setVisibility(View.GONE);
					ll_uganda.setVisibility(View.GONE);
					convert = "USD";
					//Common_data.setPreference(getActivity(), "convert", convert);
					//Common_data.setPreference(getActivity(), "base", base);
					new PrefManager<String>(getActivity()).set("convert",convert);
					new PrefManager<String>(getActivity()).set("base",base);
					currency_convert_tv.setText(convert);

					//precevievalue.setText("");
					//psendvalue.setText("");

					if(conversion){
						conversion = false;
					}
					else{
						getCurrentConversionRate();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}
}
