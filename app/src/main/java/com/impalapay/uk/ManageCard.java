package com.impalapay.uk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.uk.adapters.AtmCardAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageCard extends Fragment
{
    View  view ;
    Button attach_a_card_bt,link_account_bt;
    ListView card_listview;
    String Data,carddetails,cardtypest,paymentdetailsid_,bankdetailsid_;

    Integer[] final_imageId ;

    Integer[] final_overlay ;

    Integer[] overlay_card ;

    Integer[] overlay_bank ;

    String[] final_cardname_bank;

    String[] final_account_number;

    String[] final_cardtype;

    String[] final_verifye;

    Integer[] imageId_bank ;

    Integer[] imageId_card ;

    String[] cardname;

    String[] cardtype;

    String[] masked_card_number;

    String[] account_number_bank;

    String[] coded_accountnumber;

    String acc_num;

    String [] verify;

    String [] ownername;

    String []swiftcode;

    String []aba_number;

    String [] bankname;

    String []expyear;

    String []expmonth;

    String []country;

    String[]coded_account_number_bank;

    String[]coded_account_number_card;

    String[] bankdetailsid;

    ImageView crose;

    String card_id[];

    String card_status[];

    TextView cardnametv,cardnumbertv,exipitedtv,nameoncardtv;

    int cardlength,banklength;

    int total_length;

    String userid = "";

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.manage_card, container, false);

        return  view ;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getXmlId();
        Common_data.setPreference(getActivity(), "from", "");
        //userid = Common_data.getPreferences(getActivity(), "userid");
        userid = new PrefManager<String>(getActivity().getApplicationContext()).get("userid","");
        getUserCards();
    }
    @Override
    public void onResume()
    {

        getUserCards();
        attach_a_card_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (total_length < 5) {
                    final Dialog d=new Dialog(getActivity());
                    d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.setCancelable(false);
                    d.setContentView(R.layout.success_response);
                    TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                    tv_success.setText("Please note that you can only attach a Visa or MasterCard debit card with expiry date and cvv at the back of the card.");

                    Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                    btn_ok.setText("OK");
                    btn_ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            d.dismiss();
                            Intent i = new Intent(getActivity(), AttachCardActivity.class);
                            startActivity(i);
                        }
                    });
                    d.show();

					/*FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment, new AttachCardFrg());
					ft.addToBackStack(null);
					ft.commit();*/
                } else {
                    Toast.makeText(getActivity(), "The card limit is upto 5, so please delete any existing card", Toast.LENGTH_SHORT).show();
                }

            }
        });


        card_listview.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position ,long arg3)
            {
                if(position >= banklength)
                {
                    int pos=position-banklength;

                }

                if(position >= banklength)
                {
                    int pos=position;

                    final int card_ref = Integer.parseInt(card_id[pos]);

                    if(card_status[pos].equals("0")){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setMessage("Card needs verification.")
                                .setPositiveButton("Verify", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        final Dialog d = new Dialog(getActivity());
                                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                        d.setContentView(R.layout.dialog3);
                                        d.setCancelable(false);
                                        d.show();

                                        final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv2);
                                        final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv2);

                                        final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img2);
                                        final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img2);
                                        final EditText random_amount_et = (EditText) d.findViewById(R.id.random_amount_et);

                                        //tv_reject.setVisibility(View.GONE);
                                        //iv_reject.setVisibility(View.GONE);


                                        tv_reject.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                d.dismiss();
                                            }
                                        });

                                        iv_reject.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                d.dismiss();
                                            }
                                        });



                                        verify_tv.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View arg0) {
                                                if(random_amount_et.getText().toString().equals("")){
                                                    Toast.makeText(getActivity(), "Enter the amount",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    d.dismiss();
                                                    double amount = Double.parseDouble(random_amount_et.getText().toString());
                                                    confirmDebitAmount(amount,card_ref);
                                                }

                                            }
                                        });
                                        verify_img.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                if(random_amount_et.getText().toString().equals("")){
                                                    Toast.makeText(getActivity(), "Enter the amount",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    d.dismiss();
                                                    double amount = Double.parseDouble(random_amount_et.getText().toString());
                                                    confirmDebitAmount(amount,card_ref);
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Remove Card", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        removeCard(card_ref);
                                    }
                                }).show();

                    }
                    else{
                        final Dialog na=new Dialog(getActivity());
                        na.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        na.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        na.setContentView(R.layout.modified_card_dialog);
                        na.show();
                        cardnametv=(TextView)na.findViewById(R.id.cardnametv1);
                        cardnumbertv=(TextView)na.findViewById(R.id.cardnumbertv1);
                        exipitedtv=(TextView)na.findViewById(R.id.exipitedtv1);
                        nameoncardtv=(TextView)na.findViewById(R.id.nameoncardtv1);

                        ImageView delete_card=(ImageView)na.findViewById(R.id.delete_card1);

                        cardnametv.setText(""+cardname[pos]);
                        cardnumbertv.setText(""+masked_card_number[pos]);
                        //exipitedtv.setText(""+swiftcode[pos]);
                        //nameoncardtv.setText(country[pos]);

                        //bankdetailsid_=bankdetailsid[pos];

                        TextView title_delete=(TextView) na.findViewById(R.id.title_delete1);
                        title_delete.setText("Remove Card");
                        //final int card_ref_id = Integer.parseInt(card_id[pos]);
                        delete_card.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0)
                            {
                                na.dismiss();
                                removeCard(card_ref);
                            }
                            private void registerOnServer()
                            {

                                Log.d("registerOnServer()", "registerOnServer()");

                                RequestParams params=new RequestParams();
                                JSONObject object=new JSONObject();

                                try
                                {
                                    object.put("bankdetailsid", bankdetailsid_);
                                    params.put("request", object.toString());
                                    Log.d("request", params.toString());
                                }

                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                RestHttpClient.postParams("deletebank", params, new RegistrationHandler());
                            }


                            class RegistrationHandler extends AsyncHttpResponseHandler
                            {

                                @Override
                                public void onStart()
                                {
                                    super.onStart();
                                    if(getActivity()!=null && !getActivity().isFinishing())
                                    {
                                    }
                                }
                                @SuppressWarnings("deprecation")
                                @Override
                                public void onSuccess(String result)
                                {
                                    super.onSuccess(result);
                                    Log.d("onSuccess", "onSuccess");
                                    if(result.length()>0)
                                    {
                                        Log.d("response", result);
                                        try
                                        {
                                            JSONObject jsonObject=new JSONObject(result);
                                            boolean b=jsonObject.getBoolean("result");
                                            if(b)
                                            {
                                                String message=jsonObject.getString("message");
                                                Log.d("message:", message);
                                                na.dismiss();
                                                secondregisterOnServer();
                                            }
                                            if(!b)
                                            {
                                                String message=jsonObject.getString("message");
                                                Log.d("message:", message);
                                                na.dismiss();
                                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                                @Override
                                public void onFinish()
                                {
                                    Log.d("onFinish", "onFinish");
                                    super.onFinish();
                                }
                                @Override
                                @Deprecated
                                public void onFailure(int statusCode, Throwable error, String content)
                                {
                                    Log.d("onFailure", "onFailure");
                                    super.onFailure(statusCode, error, content);
                                }
                            }
                        });

                        ImageView cross1 = (ImageView) na.findViewById(R.id.cross1);
                        cross1.setOnClickListener(new OnClickListener()
                        {
                            @Override
                            public void onClick(View arg0)
                            {
                                na.dismiss();
                            }
                        });
                    }

                }
            }
        });




        link_account_bt.setOnClickListener( new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(total_length<5)
                {
                    Intent i=new Intent(getActivity(),AddBankAccountActivity.class);
                    startActivity(i);
				/*FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.replace(R.id.lk_profile_fragment, new LinkBankAccount());
				ft.addToBackStack(null);
				ft.commit();*/
                }
                else
                {
                    Toast.makeText(getActivity(), "The account limit is upto 5, so please delete any existing account",Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onResume();
    }

    public void getUserCards(){
        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();

        try
        {
            object.put("user_id", userid);
            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/getCards", params, new CardHandler());


    }

    //handle card retrival method

    class CardHandler extends AsyncHttpResponseHandler
    {
    ProgressDialog pDialog;
        @Override
        public void onStart()
        {
            super.onStart();
            if(getActivity()!=null && !getActivity().isFinishing())
            {
                Log.d("onStart", "onStart");
                pDialog = ProgressDialog.show(getActivity(),null, "Retrieving cards... ");

            }
        }
        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result)
        {
            super.onSuccess(result);
            Log.d("onSuccess", "onSuccess");


            if(result.length()>0)
            {
                Log.d("response", result);
                try
                {
                    JSONObject jsonObject=new JSONObject(result);
                   boolean b = false;
                    final String msg = jsonObject.optString("message");
                    int response_code = jsonObject.getInt("code");
                    if(1001 == response_code) {
                        b = true;
                        if (b) {
                            JSONObject object = jsonObject.getJSONObject("data");
                            JSONArray carddetails_array = object.getJSONArray("card");
                            // JSONArray carddetails_array = new JSONArray(Common_data.getPreferences(getActivity(), "carddetails_array"));
                            Log.d("Card Length", "" + carddetails_array.length());
                            Log.d("carddetails_array", "" + carddetails_array); //System.exit(1);
                            JSONArray bankdetails_array = new JSONArray();//jsonObject.getJSONArray("bank");
                            total_length = carddetails_array.length() + bankdetails_array.length();
                            Log.d("total_length", "" + total_length);
                            cardlength = carddetails_array.length();
                            banklength = bankdetails_array.length();

                            final_overlay = new Integer[total_length];

                            if (bankdetails_array.length() > 0) {
                                bankname = new String[bankdetails_array.length()];
                                imageId_bank = new Integer[bankdetails_array.length()];
                                overlay_bank = new Integer[bankdetails_array.length()];
                                account_number_bank = new String[bankdetails_array.length()];
                                coded_account_number_bank = new String[bankdetails_array.length()];
                                swiftcode = new String[bankdetails_array.length()];
                                aba_number = new String[bankdetails_array.length()];
                                country = new String[bankdetails_array.length()];
                                bankdetailsid = new String[bankdetails_array.length()];
                            }

                            if (bankdetails_array.length() > 0) {
                                for (int i = 0; i < bankdetails_array.length(); i++) {
                                    JSONObject bankdetails_object = bankdetails_array.getJSONObject(i);
                                    bankname[i] = bankdetails_object.getString("bank_name");
                                    account_number_bank[i] = bankdetails_object.getString("account_number");
                                    imageId_bank[i] = R.drawable.abcbank;
                                    swiftcode[i] = bankdetails_object.getString("aba_number");
                                    aba_number[i] = bankdetails_object.getString("aba_number");
                                    country[i] = bankdetails_object.getString("country");
                                    bankdetailsid[i] = bankdetails_object.getString("id");
                                    overlay_bank[i] = R.drawable.bank_overlay;
                                }

                                for (int i = 0; i < bankdetails_array.length(); i++) {
                                    acc_num = account_number_bank[i];
                                    Log.d("accountnumber", acc_num);
                                    int len = acc_num.length();

                                    String firsttwo = acc_num.substring(0, 2);
                                    String lasttwo = acc_num.substring(acc_num.length() - 2);

                                    int middlelenght = len - 4;

                                    StringBuffer sb = new StringBuffer();
			/*	sb.replace(1,4,"xxx");
				sb.replace(4,5," ");
				sb.replace(5,9,"xxxx");
				sb.replace(9,10," ");
				sb.replace(10,12,"xxxx");
				sb.replace(12,13," ");*/
                                    sb.append(firsttwo);
                                    for (int l = 1; l < middlelenght + 3; l++) {
                                        if (l == 4 || l == middlelenght + 2) {
                                            sb.append("x");
                                            continue;
                                        }
                                        if (l == 3 || l == middlelenght - 2 || l % 4 == 0)
                                            sb.append(" ");
                                        else
                                            sb.append("x");
                                    }

                                    sb.append(lasttwo);
                                    Log.d("After Replace", "" + sb);
                                    //coded_account_number_bank[i]=sb.toString();
                                    coded_account_number_bank[i] = account_number_bank[i];
                                }
                            }
                            if (carddetails_array.length() > 0) {
                                card_id = new String[carddetails_array.length()];
                                card_status = new String[carddetails_array.length()];
                                cardname = new String[carddetails_array.length()];
                                masked_card_number = new String[carddetails_array.length()];
                                verify = new String[carddetails_array.length()];
                                expyear = new String[carddetails_array.length()];
                                expmonth = new String[carddetails_array.length()];
                                ownername = new String[carddetails_array.length()];
                                cardtype = new String[carddetails_array.length()];
                                imageId_card = new Integer[carddetails_array.length()];
                                coded_account_number_card = new String[carddetails_array.length()];
                                overlay_card = new Integer[carddetails_array.length()];

                                for (int i = 0; i < carddetails_array.length(); i++) {
                                    JSONObject carddetails_object = carddetails_array.getJSONObject(i);
                                    card_id[i] = carddetails_object.getString("card_ref");
                                    card_status[i] = carddetails_object.getString("card_status");
                                    cardname[i] = carddetails_object.getString("card_nickname");
                                    masked_card_number[i] = carddetails_object.getString("masked_card_number");
                                    //verify[i]=carddetails_object.getString("verified");
                                    //expyear[i]=carddetails_object.getString("expiry_year");
                                    //expmonth[i]=carddetails_object.getString("expiry_month");
                                    //ownername[i]=carddetails_object.getString("card_owner");
                                    //cardtype[i]=carddetails_object.getString("card_type");
                                }
                                for (int i = 0; i < carddetails_array.length(); i++) {
                                    acc_num = masked_card_number[i];
                                    acc_num.length();
                                    StringBuffer sb = new StringBuffer(acc_num);
                                    sb.replace(1, 4, "xxx");
                                    sb.replace(4, 5, " ");
                                    sb.replace(5, 9, "xxxx");
                                    sb.replace(9, 10, " ");
                                    sb.replace(10, 14, "xxxx");
                                    sb.replace(14, 15, " ");
                                    Log.d("After Replace", "" + sb);
                                    //coded_account_number_card[i]=sb.toString();
                                    coded_account_number_card[i] = masked_card_number[i];
                                }
                            }

                            final_imageId = new Integer[total_length];
                            final_cardname_bank = new String[total_length];
                            final_account_number = new String[total_length];
                            final_cardtype = new String[total_length];
                            final_verifye = new String[total_length];
                            final_overlay = new Integer[total_length];

                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_imageId[i] = imageId_bank[i];
                            }
                            int banklength2 = banklength;
                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_imageId[banklength2] = imageId_card[i];
                                banklength2++;
                            }
                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_imageId", "" + final_imageId[i]);
                            }

                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_overlay[i] = overlay_bank[i];
                            }

                            int banklength23 = banklength;
                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_overlay[banklength23] = overlay_card[i];
                                banklength23++;
                            }

                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_overlay", "" + final_overlay[i]);
                            }

                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_cardname_bank[i] = bankname[i];
                                Log.d("final_cardname_bank", "" + final_cardname_bank[i]);
                            }
                            int banklength1 = banklength;
                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_cardname_bank[banklength1] = cardname[i];
                                banklength1++;
                            }

                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_cardname_bank", "" + final_cardname_bank[i]);
                            }


                            int banklength3 = banklength;
                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_account_number[i] = coded_account_number_bank[i];
                            }

                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_account_number[banklength3] = coded_account_number_card[i];
                                banklength3++;
                            }

                            int banklength4 = banklength;
                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_account_number", "" + final_account_number[i]);
                            }

                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_cardtype[i] = " ";
                            }

                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_cardtype[banklength4] = cardtype[i];
                                banklength4++;
                            }
                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_cardtype", "" + final_cardtype[i]);
                            }

                            int banklength5 = banklength;
                            for (int i = 0; i < bankdetails_array.length(); i++) {
                                final_verifye[i] = "1";
                            }

                            for (int i = 0; i < carddetails_array.length(); i++) {
                                final_verifye[banklength5] = card_status[i];
                                banklength5++;
                            }

                            for (int i = 0; i < total_length; i++) {
                                Log.d("final_verifye", "" + final_verifye[i]);
                            }
                            AtmCardAdapter adapter = new AtmCardAdapter(getActivity(), final_imageId, final_overlay, final_cardname_bank, final_cardtype, final_account_number, final_verifye, banklength, cardlength);
                            card_listview.setAdapter(adapter);
                        }
                    }else if(1020 == response_code){
                        final Dialog d=new Dialog(getActivity());
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
                    else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        getUserCards();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        getActivity().finish();
                                        startActivity(getActivity().getIntent());
                                    }
                                }).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

        }
        @Override
        public void onFinish()
        {
            pDialog.dismiss();
            Log.d("onFinish", "onFinish");
            super.onFinish();

        }
        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content)
        {
            Log.d("onFailure", "onFailure");
            super.onFailure(statusCode, error, content);
        }
    }




    private void getXmlId()
    {
        attach_a_card_bt =(Button)view.findViewById(R.id.add_account_bt1);
        card_listview=(ListView)view.findViewById(R.id.history_listview1);
        link_account_bt=(Button)view.findViewById(R.id.link_account_bt1);

    }

    private void secondregisterOnServer() {
        RequestParams params=new RequestParams();
        JSONObject object=new JSONObject();

        try
        {
            //String userid= Common_data.getPreferences(getActivity(), "userid");

            String userid = new PrefManager<String>(getActivity().getApplicationContext()).get("userid","");
            System.out.println("userid" +userid);
            object.put("userid", userid);
            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }
        RestHttpClient.postParams("getdata", params, new RegistrationHandlerSecond());
    }

    class RegistrationHandlerSecond extends AsyncHttpResponseHandler
    {

        @Override
        public void onStart()
        {
            super.onStart();
            if(getActivity()!=null && !getActivity().isFinishing())
            {
                Log.d("onStart", "onStart");

            }
        }
        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String result)
        {
            super.onSuccess(result);
            Log.d("onSuccess", "onSuccess");


            if(result.length()>0)
            {
                Log.d("response", result);
                try
                {
                    JSONObject jsonObject=new JSONObject(result);
                    boolean b=jsonObject.getBoolean("result");
                    if(b)
                    {
                       // Common_data.setPreference(getActivity(), "data", result);
                        new PrefManager<String>(getActivity().getApplicationContext()).set("data","");
                       // String data= Common_data.getPreferences(getActivity(), "data");

                        String data= new PrefManager<String>(getActivity().getApplicationContext()).get("data","");
                        Log.d("Data",""+data);

                        String message=jsonObject.getString("message");
                        Log.d("message:", message);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.lk_profile_fragment, new ManageCard());
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    if(!b)
                    {

                        String message=jsonObject.getString("message");
                        Log.d("message:", message);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }

        }
        @Override
        public void onFinish()
        {
            Log.d("onFinish", "onFinish");
            super.onFinish();

        }
        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content)
        {
            Log.d("onFailure", "onFailure");
            super.onFailure(statusCode, error, content);
        }
    }

    public  void showAccountVerifyDialog(final Activity act) {
        final Dialog d = new Dialog(act);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.account_verfication_dialog);
        d.setCancelable(false);
        d.show();

        final LinearLayout accept_ly = (LinearLayout) d.findViewById(R.id.accept_ly);
        final LinearLayout reject_ly = (LinearLayout) d.findViewById(R.id.reject_ly);
        final LinearLayout verify_ly = (LinearLayout) d.findViewById(R.id.verify_ly);

        final EditText dedcuted_amount = (EditText) d.findViewById(R.id.deducted_amount_et);

        final TextView account_verify_tv = (TextView) d.findViewById(R.id.account_verify_tv);
        final TextView tv_accept = (TextView) d.findViewById(R.id.accept_tv);
        final TextView tv_reject = (TextView) d.findViewById(R.id.reject_tv);
        final TextView verify_tv = (TextView) d.findViewById(R.id.verify_tv);
        final TextView tries_tv = (TextView) d.findViewById(R.id.tries_tv);

        final ImageView iv_accept = (ImageView) d.findViewById(R.id.accept_img);
        final ImageView iv_reject = (ImageView) d.findViewById(R.id.reject_img);
        final ImageView verify_img = (ImageView) d.findViewById(R.id.verify_img);


        tv_reject.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                d.dismiss();
					
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageCard());
					ft.addToBackStack(null);
					ft.commit();*/

            }
        });

        iv_reject.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageCard());
					ft.addToBackStack(null);
					ft.commit();*/
                //act.finish();
            }
        });

        iv_accept.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                account_verify_tv.setText(act.getString(R.string.account_varify_accept));
                dedcuted_amount.setVisibility(View.VISIBLE);
                reject_ly.setVisibility(View.GONE);
                accept_ly.setVisibility(View.GONE);
                verify_ly.setVisibility(View.VISIBLE);
                tries_tv.setVisibility(View.VISIBLE);
            }
        });

        tv_accept.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                account_verify_tv.setText(act.getString(R.string.account_varify_accept));
                dedcuted_amount.setVisibility(View.VISIBLE);
                reject_ly.setVisibility(View.GONE);
                accept_ly.setVisibility(View.GONE);
                verify_ly.setVisibility(View.VISIBLE);
                tries_tv.setVisibility(View.VISIBLE);

            }
        });

        verify_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageCard());
					ft.addToBackStack(null);
					ft.commit();*/
                //act.finish();
            }
        });
        verify_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                d.dismiss();
					/*FragmentTransaction ft = fm.beginTransaction();
					ft.replace(R.id.lk_profile_fragment,new ManageCard());
					ft.addToBackStack(null);
					ft.commit();*/
                //act.finish();
            }
        });

    }


    private void removeCard(int ref_id) {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {
            //Log.d("trycard ", "" + card);

            object.put("user_id", userid);
            object.put("ref_id", ref_id);

            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/removeCard", params, new RemoveCardHandler(), Login.token);
    }

    class RemoveCardHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {

            super.onStart();
            dialog = ProgressDialog.show(getActivity(), null, "Removing card... ");

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
                    if(1001 == response_code)
                        b = true;
                    final String msg = json.optString("message");
                    if (b) {
                        //JSONArray array = json.getJSONArray("data");
                        JSONObject object = json.getJSONObject("data");
                        Log.d("data", object.toString());

                        //Common_data.setPreference(AttachCardActivity.this, "data", result);
                        JSONArray carddetails_array = object.getJSONArray("card");
                        Log.d("card",carddetails_array.toString());
                        //Common_data.setPreference(getActivity(), "carddetails_array", carddetails_array.toString());

                        new PrefManager<String>(getActivity().getApplicationContext()).set("carddetails_array","");

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

                                // Reload current fragment
                                Fragment frg = null;
                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("ManageCard");
                                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.detach(frg);
                                ft.attach(frg);
                                ft.commit();

//                                FragmentManager fm = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                                fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard());
//                                fragmentTransaction.addToBackStack(null);
//                                fragmentTransaction.commit();
//                                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
                        tv_success.setText("Your session has expired. Please Login Again.");
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
                    else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        // Reload current fragment
                                        Fragment frg = null;
                                        frg = getActivity().getSupportFragmentManager().findFragmentByTag("ManageCard");
                                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.detach(frg);
                                        ft.attach(frg);
                                        ft.commit();

                                        /*FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                        fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard());
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
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
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            //Common_data.showAccountVerifyDialog(AttachCardActivity.this);
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", "onFailure");
            super.onFailure(statusCode, error, content);
            Toast.makeText(getActivity(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDebitAmount(double amt, int ref_id) {
        RequestParams params = new RequestParams();
        JSONObject object = new JSONObject();

        try {
            //Log.d("trycard ", "" + card);

            object.put("user_id", userid);
            object.put("ref_id", ref_id);
            object.put("amount", amt);

            params.put("request", object.toString());
            Log.d("request", params.toString());
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        RestHttpClient.postParams("banking/cardVerificationAmount", params, new AmountHandler(), Login.token);
    }

    class AmountHandler extends AsyncHttpResponseHandler {
        @SuppressWarnings("static-access")
        @Override
        public void onStart() {

            super.onStart();
            dialog = ProgressDialog.show(getActivity(), null, "Verifying card... ");

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
                    if(1001 == response_code)
                        b = true;
                    final String msg = json.optString("message");
                    if (b) {
                        //JSONArray array = json.getJSONArray("data");
                        JSONObject object = json.getJSONObject("data");
                        Log.d("data", object.toString());

                        //Common_data.setPreference(AttachCardActivity.this, "data", result);
                        JSONArray carddetails_array = object.getJSONArray("card");
                        Log.d("card",carddetails_array.toString());
                        //Common_data.setPreference(getActivity(), "carddetails_array", carddetails_array.toString());
                        new PrefManager<String>(getActivity().getApplicationContext()).set("carddetails_array",carddetails_array.toString());

                        final Dialog d=new Dialog(getActivity());
                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        d.setCancelable(false);
                        d.setContentView(R.layout.success_response);
                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
                        tv_success.setText(msg);
                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
                        btn_ok.setText("Ok");
                        btn_ok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
                                //Common_data.Preferencesclear(MainActivity.th);
                                d.dismiss();

                                // Reload current fragment
                                Fragment frg = null;
                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("ManageCard");
                                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.detach(frg);
                                ft.attach(frg);
                                ft.commit();

                                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
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
                        tv_success.setText("Your session has expired. Please Login Again.");
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
                    else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Info")
                                .setMessage(msg)
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        // Reload current fragment
                                        Fragment frg = null;
                                        frg = getActivity().getSupportFragmentManager().findFragmentByTag("ManageCard");
                                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.detach(frg);
                                        ft.attach(frg);
                                        ft.commit();

                                        /*FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                        fragmentTransaction.replace(R.id.lk_profile_fragment, new ManageCard());
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/
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
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
            //Common_data.showAccountVerifyDialog(AttachCardActivity.this);
        }

        @Override
        @Deprecated
        public void onFailure(int statusCode, Throwable error, String content) {
            Log.d("onFailure", "onFailure");
            super.onFailure(statusCode, error, content);
            Toast.makeText(getActivity(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
        }
    }

}
