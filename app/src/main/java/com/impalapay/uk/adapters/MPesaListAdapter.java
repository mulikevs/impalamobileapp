package com.impalapay.uk.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.impalapay.models.MPesaPaybillListModel;
import com.impalapay.uk.R;


public class MPesaListAdapter extends ArrayAdapter<MPesaPaybillListModel> {

	private ArrayList<MPesaPaybillListModel> items;
	private ArrayList<MPesaPaybillListModel> itemsAll;
	private ArrayList<MPesaPaybillListModel> suggestions;

	
	@SuppressWarnings("unchecked")
	public MPesaListAdapter(Context context, int resource,ArrayList<MPesaPaybillListModel> items) {
		super(context, resource, items);
		this.items = items;
		
		this.itemsAll = (ArrayList<MPesaPaybillListModel>) items.clone();
		this.suggestions = new ArrayList<MPesaPaybillListModel>();
			
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = inflater.inflate(R.layout.spinner_layout, null);
				TextView name_tv = (TextView) v.findViewById(R.id.tv_item);
				MPesaPaybillListModel customer = items.get(position);
				name_tv.setText(customer.getPaybillNumber());
			
		return v;
	}

	
	
	@Override
	public Filter getFilter() {
		return nameFilter;
	}

	Filter nameFilter = new Filter() {
		@Override
		public String convertResultToString(Object resultValue) {
			String str = ((MPesaPaybillListModel) (resultValue)).getPaybillNumber();
			return str;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (constraint != null) {
				suggestions.clear();
				for (MPesaPaybillListModel customer : items) {
					if (customer.getPaybillNumber().startsWith(constraint+"")) {
						suggestions.add(customer);
					}
				}
				FilterResults filterResults = new FilterResults();
				filterResults.values = suggestions;
				filterResults.count = suggestions.size();
				return filterResults;
			} else {
				return new FilterResults();
			}
		}

		@Override
		protected void publishResults(CharSequence constraint,FilterResults results) {
			@SuppressWarnings("unchecked")
			ArrayList<MPesaPaybillListModel> filteredList = (ArrayList<MPesaPaybillListModel>) results.values;
			if (results != null && results.count > 0) {
				clear();
				for (MPesaPaybillListModel c : filteredList) {
					add(c);
				}
				notifyDataSetChanged();
			}
		}
	};
	
	
	
	

}
