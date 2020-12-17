package com.impalapay.uk.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.RestHttpClient;
import com.impalapay.models.BeneficaryDetailModel;
import com.impalapay.uk.EditBeneficiary;
import com.impalapay.uk.Login;
import com.impalapay.uk.MainActivity;
import com.impalapay.uk.ManageBeneficiaries;
import com.impalapay.uk.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BeneficiaryAdapter extends BaseAdapter  {



    private Context mContext;

    ArrayList<BeneficaryDetailModel> array;
    static ArrayList<BeneficaryDetailModel> array1;

    public BeneficiaryAdapter(Context context, ArrayList<BeneficaryDetailModel> array) {
        super();
        mContext = context;
        this.array = array;
        this.array1 = array;

    }
    @Override
    public int getCount() {
        return array.size();
    }



    static class ViewHolder{
        ImageView ico;
        TextView fname;
        TextView lname;
        TextView msisdn;
        TextView bank;
        TextView bank_branch;
        TextView bank_account_name;
        TextView bank_account_number;
        Button btn_edit_beneficiary,btn_delete_beneficiary;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder=new ViewHolder();
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.beneficiary_detail, null);

            holder.ico=(ImageView) view.findViewById(R.id.iv_beneficiary);
            holder.fname=(TextView) view.findViewById(R.id.tv_fname);
            holder.lname=(TextView) view.findViewById(R.id.tv_lname);
            holder.msisdn =((TextView) view.findViewById(R.id.tv_msisdn));
            holder.bank =((TextView) view.findViewById(R.id.tv_bank));
            holder.bank_branch =((TextView) view.findViewById(R.id.tv_bank_branch));
            holder.bank_account_name=((TextView) view.findViewById(R.id.tv_bank_account_name));
            holder.bank_account_number=((TextView) view.findViewById(R.id.tv_bank_account_number));
            holder.btn_edit_beneficiary = (Button) view.findViewById(R.id.btn_edit_beneficiary);
            holder.btn_delete_beneficiary=(Button) view.findViewById(R.id.btn_delete_beneficiary);


            view.setTag(holder);
        }else{
            holder=(ViewHolder) view.getTag();
        }


        final BeneficaryDetailModel model=(BeneficaryDetailModel) getItem(position);
        //holder.mode.setText(model.getDel_method().toUpperCase(Locale.getDefault())+" ("+model.getReceipient_name()+")");
        //holder.date.setText(model.getDate());
        holder.fname.setText(Html.fromHtml("<b>First Name: </b>" + model.getFName()));
        holder.lname.setText(Html.fromHtml("<b>Last Name: </b>" + model.getLName()));
        holder.msisdn.setText(Html.fromHtml("<b>Phone Number: </b>" + model.getMobile()));
        holder.bank.setText(Html.fromHtml("<b>Bank: </b>" + model.getBank_name()));
        holder.bank_branch.setText(Html.fromHtml("<b>Bank Branch: </b>" + model.getBranch_name()));
        holder.bank_account_name.setText(Html.fromHtml("<b>Bank Account Name: </b>" + model.getAccount_name()));
        holder.bank_account_number.setText(Html.fromHtml("<b>Bank Account No.: </b>" + model.getAccount_number()));
        holder.btn_delete_beneficiary.setTag(position);

        holder.btn_edit_beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditBeneficiary.beneficiary_position = position;
                BeneficaryDetailModel model1 = (BeneficaryDetailModel) getItem(position);
                Log.d("Beneficiary ID", model1.getBeniid());
                Log.d("Position", String.valueOf(position));
                Log.d("Array size", String.valueOf(array.size()));
                MainActivity.start_fragment(MainActivity.main_activity, new EditBeneficiary(), "EditBeneficiary");
            }
        });
        holder.btn_delete_beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pos = v.getTag().toString();
                BeneficaryDetailModel model1 = (BeneficaryDetailModel) getItem(position);
               deleteBeneficiary(pos,model1.getBeniid());
            }
        });

        //String stat=model.getStatus();
		
		/*if(stat.equalsIgnoreCase("pending") || stat.equalsIgnoreCase("failed"))
		{
			holder.status.setText(stat);
			holder.status.setTextColor(Color.RED);
		}
		else{

			holder.status.setText(stat);
			holder.status.setTextColor(Color.GREEN); //Color.parseColor("#247024")
		} */
		
		/*if(model.getPayment_method().equals("card")){
			holder.ico.setImageDrawable(mContext.getResources().getDrawable(R.drawable.card));
		}
		else{
			holder.ico.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wallet));
		} */

        return view;
    }



    public Object getItem(int position) {
        return array.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public static Object getArrayItem(int position) {
        return array1.get(position);
    }
    private void deleteBeneficiary(String pos, String beniid) {
        try {

            RequestParams params = new RequestParams();
            JSONObject object = new JSONObject();
            try {

                object.put("userid", Common_data.getPreferences(mContext, "userid"));
                object.put("beneficiary_id", beniid);
                params.put("request", object.toString());

            }

            catch (JSONException e) {
                e.printStackTrace();
            }
            //Toast.makeText(mContext,beniid,Toast.LENGTH_LONG).show();

            RestHttpClient.postParams("banking/deleteBeneficiary", params,new DeleteBeneficiariesHandler(pos), Login.token);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DeleteBeneficiariesHandler extends AsyncHttpResponseHandler {
      String position;

        public DeleteBeneficiariesHandler(String pos) {
         position=pos;
        }

        @Override
        public void onStart() {
            super.onStart();
            ManageBeneficiaries.pdialog=ProgressDialog.show(mContext, "", "Deleting...");
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onSuccess(String content) {
            super.onSuccess(content);
            ManageBeneficiaries.pdialog.dismiss();
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
                        array.remove(position);

                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

                        MainActivity.start_fragment(MainActivity.main_activity, new ManageBeneficiaries(), "ManageBeneficiaries");
//                        final Dialog d=new Dialog(mContext);
//                        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                        d.setCancelable(false);
//                        d.setContentView(R.layout.success_response);
//                        TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
//                        tv_success.setText(msg);
//                        Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
//                        btn_ok.setText("Ok");
//                        btn_ok.setOnClickListener(new View.OnClickListener() {
//
//                            @Override
//                            public void onClick(View arg0) {
//                                //Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
//                                //Common_data.Preferencesclear(MainActivity.th);
//
//                                //Intent i = new Intent(getActivity(), MainActivity.class);
//                                //startActivity(i);
//
//                                d.dismiss();
//                                MainActivity.start_fragment(MainActivity.main_activity, new ManageBeneficiaries(), "ManageBeneficiaries");
//
//                            }
//                        });
//                        d.show();
                    }
                    else if(1020 == response_code){
                        final Dialog d=new Dialog(mContext);
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
                                Intent i = new Intent(mContext, Login.class);
                                mContext.startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else if(1030 == response_code){
                        //Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
                        final Dialog d=new Dialog(mContext);
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
                                i.setData(Uri.parse(mContext.getString(R.string.app_store_url)));
                                mContext.startActivity(i);
                            }
                        });
                        d.show();
                    }
                    else{
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                }
            }
        }
        @Override
        public void onFinish() {
            super.onFinish();
            if(ManageBeneficiaries.pdialog.isShowing())
                ManageBeneficiaries.pdialog.dismiss();
        }

    }

}

