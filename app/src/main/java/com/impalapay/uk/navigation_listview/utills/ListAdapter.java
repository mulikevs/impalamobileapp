package com.impalapay.uk.navigation_listview.utills;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.impalapay.uk.R;

public class ListAdapter extends BaseAdapter {
	Context context;
	private ArrayList<Model> navDrawerItems;
	String[] item_name;
	int image[];
	LayoutInflater inflater;
	int listitempostion;
	int selected_item;

	public ListAdapter(Context context, ArrayList<Model> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;
		Log.d("ListAdapter", "ListAdapter");

	}

	@Override
	public int getCount() {
		Log.d("getCount", "getCount");
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		Log.d("getItem", "" + position);
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		Log.d("getItemId", "" + position);
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(" position", "" + position);
		Log.d("ListAdapter position", "" + selected_item);

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

			if (position == 0) {
				convertView = mInflater.inflate(R.layout.first_list_item, null);
			} else if (position == selected_item) {
				Log.d("==getView==", "" + position);
				convertView = mInflater.inflate(R.layout.selected_list_item, null);
				ImageView imgIcon = (ImageView) convertView.findViewById(R.id.profile_img);
				TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
				imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
				txtTitle.setText(navDrawerItems.get(position).getTitle());
			} else {
				convertView = mInflater.inflate(R.layout.list_item, null);
				ImageView imgIcon = (ImageView) convertView.findViewById(R.id.profile_img);
				TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
				imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
				txtTitle.setText(navDrawerItems.get(position).getTitle());
			}
		}
		return convertView;
	}
}