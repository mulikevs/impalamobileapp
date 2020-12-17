package com.impalapay.uk.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.impalapay.models.TransactionHistoryModel;
import com.impalapay.uk.R;

import java.util.ArrayList;

public class TransactionAdapter extends BaseAdapter {

	
	
	private Context mContext;

	ArrayList<TransactionHistoryModel> array;

	public TransactionAdapter(Context context, ArrayList<TransactionHistoryModel> array) {
		super();
		mContext = context;
		this.array = array;

	}
	@Override
	public int getCount() {
		return array.size();
	}
	
	static class ViewHolder{
		ImageView ico;
		TextView date;
		TextView mode;
		TextView recipient;
		TextView ref_id;
		TextView txn_code;
		TextView amount;
		TextView status;
		ProgressBar status_progress;
	}

	@SuppressLint("NewApi")
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder=new ViewHolder();
		if(view==null){
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.transaction_history_detail, null);

			holder.ico=(ImageView) view.findViewById(R.id.iv_trnsc_histry);
			holder.date=(TextView) view.findViewById(R.id.tv_date_time);
			holder.mode=(TextView) view.findViewById(R.id.tv_trnsc_mode);
			holder.recipient =((TextView) view.findViewById(R.id.tv_recipient));
			holder.ref_id =((TextView) view.findViewById(R.id.tv_trnsc_ref_id));
			holder.txn_code =((TextView) view.findViewById(R.id.tv_trnsc_txn_code));
			holder.amount=((TextView) view.findViewById(R.id.tv_trnsc_txn_amount));
			holder.status_progress=((ProgressBar) view.findViewById(R.id.status_progress));
			holder.status=((TextView) view.findViewById(R.id.tv_status));
				view.setTag(holder);
		}else{
			holder=(ViewHolder) view.getTag();
		}
		
		
		TransactionHistoryModel model=(TransactionHistoryModel) getItem(position);
		String status_str = "";
		switch(model.getStatus()){
			case "0":
				status_str = "Pending";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
				holder.status_progress.setProgress(45);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.light_green)));
				break;
			case "1":
				status_str = "Pending";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
				holder.status_progress.setProgress(45);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.light_green)));
				break;
			case "2":
				if (model.getDel_method().equals("41")){
					status_str = "Awaiting Cash-Out";
				}else {
					status_str = "Queued";
				}
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
				holder.status_progress.setProgress(15);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.yellow)));
				break;
			case "3":
				String delivery_status=model.getDelivery_status();

				if(delivery_status.equals("0")){
					status_str = "Delivered";
					holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.success_color));
					holder.status_progress.setProgress(100);
					holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.success_color)));
				}else{
					status_str = "Queued";
					holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.yellow));
					holder.status_progress.setProgress(15);
					holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.yellow)));
				}

				break;
			case "29":
				status_str = "Incomplete Processing";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.blue_latter));
				holder.status_progress.setProgress(100);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.logo_theme)));
				break;
			case "-9":
				status_str = "Incomplete Processing";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.blue_latter));
				holder.status_progress.setProgress(100);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.blue_latter)));
				break;
			case "-12":
				status_str = "Cancelled";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.blue_latter));
				holder.status_progress.setProgress(100);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.blue_latter)));
				break;
			default:
				status_str = "Failed";
				holder.status.setTextColor(ContextCompat.getColor(mContext,R.color.red_text));
				holder.status_progress.setProgress(100);
				holder.status_progress.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext,R.color.red_text)));
		}

		holder.date.setText(Html.fromHtml("<b>"+model.getDate()+"</b>"));
				//holder.mode.setText(model.getDel_method().toUpperCase(Locale.getDefault())+" ("+model.getReceipient_name()+")");
				//holder.date.setText(model.getDate());
		holder.recipient.setText(Html.fromHtml("<b>Recipient: </b>" + model.getReceipient_name()));
		holder.ref_id.setText(Html.fromHtml("<b>Ref. ID: </b>" + model.getRefID()));
		holder.txn_code.setText(Html.fromHtml("<b>Txn Code: </b>"+ model.getTxnCode()) );
		holder.amount.setText(model.getSend());
		holder.status.setText(Html.fromHtml(status_str));
		
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

}

