package com.impalapay.uk.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.impalapay.models.TransactionHistoryModel;
import com.impalapay.uk.R;
import com.impalapay.uk.Sawapay_Main_Screen;
import com.impalapay.uk.SendMoney;

public class MainPageTranscationAdapter extends BaseAdapter {

	private Context mContext;

	ArrayList<TransactionHistoryModel> array;
	public static TransactionHistoryModel clonemodel = new TransactionHistoryModel();

	public MainPageTranscationAdapter(Context context,
			ArrayList<TransactionHistoryModel> array) {
		super();
		mContext = context;
		this.array = array;

	}

	@Override
	public int getCount() {
		return array.size();
	}

	static class VIewHolder {
		TextView name;
		TextView ref_no;
		TextView amount_sent;
		Button paynow;
	}

	public View getView(int position, View view, ViewGroup parent) {

		VIewHolder holder = new VIewHolder();
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.main_screen_list_history_item,null);

			holder.name = (TextView) view.findViewById(R.id.recipient_name);
			holder.ref_no = (TextView) view.findViewById(R.id.ref_no);
			holder.amount_sent = ((TextView) view.findViewById(R.id.amount_sent));
			holder.paynow = ((Button) view.findViewById(R.id.pay));
			view.setTag(holder);
			
		} else {
			holder = (VIewHolder) view.getTag();
		}
		final int pos = position;
		final TransactionHistoryModel model = (TransactionHistoryModel) getItem(position);
		
		holder.name.setText(model.getReceipient_name());
		holder.ref_no.setText("Ref. ID: " + model.getTxnCode());
		holder.amount_sent.setText(Sawapay_Main_Screen.base + " " + model.getSend());

		holder.paynow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
				if(!model.getDel_method().equalsIgnoreCase("withdrawl")){
					clonemodel = array.get(pos);
					Intent i = new Intent(mContext, SendMoney.class);
					i.putExtra("iswhat", "main");
					mContext.startActivity(i);
				}
			}
		});

		return view;
	}

	public Object getItem(int position) {
		return array.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

}
