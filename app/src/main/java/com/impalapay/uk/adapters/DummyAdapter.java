package com.impalapay.uk.adapters;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.impalapay.models.BeneficaryDetailModel;
import com.impalapay.uk.R;


public class DummyAdapter extends ArrayAdapter<BeneficaryDetailModel> {

	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_ITEM = 0;
	
	private ArrayList<BeneficaryDetailModel> items;
	private ArrayList<BeneficaryDetailModel> itemsAll;
	private ArrayList<BeneficaryDetailModel> suggestions;

	
	private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

	private LayoutInflater mInflater;

	TextView name_tv;
	
	@SuppressWarnings("unchecked")
	public DummyAdapter(Context context, int resource,ArrayList<BeneficaryDetailModel> items) {
		super(context, resource, items);
		this.items = items;
		this.itemsAll = (ArrayList<BeneficaryDetailModel>) items.clone();
		this.suggestions = new ArrayList<BeneficaryDetailModel>();
		 mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
			if (v == null) {
			int type=getItemViewType(position);
			Log.d("viewtype", type+"");
			switch (type) {
			case TYPE_ITEM:
				v = mInflater.inflate(R.layout.spinner_layout, null);
				name_tv = (TextView) v.findViewById(R.id.tv_item);
				BeneficaryDetailModel customer = items.get(position);
				name_tv.setText(customer.getName());
				break;
			case TYPE_SEPARATOR:
				v = mInflater.inflate(R.layout.header_autocomplet, null);
				name_tv = (TextView) v.findViewById(R.id.header_tv);
				name_tv.setText("From PhoneBook");
				break;
			}
		}
		
		return v;
	}

	
	public void addSectionHeaderItem(final ArrayList<BeneficaryDetailModel> items) {
		this.items.clear();
		this.items=items;
		sectionHeader.add(items.size() - 2);
		notifyDataSetChanged();
	}
	
	
	@Override
	public int getItemViewType(int position) {
		return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	
	
	@Override
	public Filter getFilter() {
		return nameFilter;
	}

	Filter nameFilter = new Filter() {
		@Override
		public String convertResultToString(Object resultValue) {
			String str = ((BeneficaryDetailModel) (resultValue)).getName();
			return str;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (constraint != null) {
				suggestions.clear();
				for (BeneficaryDetailModel customer : items) {
					if (customer.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
			ArrayList<BeneficaryDetailModel> filteredList = (ArrayList<BeneficaryDetailModel>) results.values;
			if (results != null && results.count > 0) {
				clear();
				for (BeneficaryDetailModel c : filteredList) {
					add(c);
				}
				notifyDataSetChanged();
			}
		}
	};
	
	
	
	

}
