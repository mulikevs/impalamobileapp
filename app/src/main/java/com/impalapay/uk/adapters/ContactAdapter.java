package com.impalapay.uk.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.impalapay.models.BeneficaryDetailModel;
import com.impalapay.uk.R;

public class ContactAdapter extends BaseAdapter {

	ArrayList<BeneficaryDetailModel> array;
	Context context;
	View view;
	public static int VIEWTYPE = 0;

	public ContactAdapter(Context context,ArrayList<BeneficaryDetailModel> array) {
		this.context = context;
		this.array = array;

	}

	@Override
	public int getCount() {
		return array.size();
	}

	@Override
	public BeneficaryDetailModel getItem(int pos) {
		return array.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return array.indexOf(array.get(arg0));
	}

	@Override
	public View getView(int pos, View arg1, ViewGroup arg2) {
		LayoutInflater infl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = infl.inflate(R.layout.contact_list_item, null);
		
		TextView tname = (TextView) view.findViewById(R.id.recipient_name);
		TextView mobile = (TextView) view.findViewById(R.id.number);
		BeneficaryDetailModel model = getItem(pos);
		tname.setText(model.getName());
		mobile.setText(model.getMobile());

		return view;
	}

}
