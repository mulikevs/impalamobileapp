package com.impalapay.uk.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.impalapay.uk.R;
import com.impalapay.uk.navigation_listview.utills.Model;

import java.util.ArrayList;

public class NavigationAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Model> list;
	
	public NavigationAdapter(Context context, ArrayList<Model> list) {
		this.context = context;
		this.list = list;
	}
	
	class ViewHolder {
		TextView txtNavTitle;
		ImageView imvNavIcon;
		TextView txtNavTitleSel;
		ImageView imvNavIconSel;
		RelativeLayout rlNavRow;
		RelativeLayout rlNavRowSel;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
       
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.selected_list_item, parent, false);
            holder = new ViewHolder();
            holder.txtNavTitle 		= (TextView) convertView.findViewById(R.id.txtNavTitle);
            holder.imvNavIcon 		= (ImageView) convertView.findViewById(R.id.imvNavIcon);
            holder.txtNavTitleSel 	= (TextView) convertView.findViewById(R.id.txtNavTitleSel);
            holder.imvNavIconSel 	= (ImageView) convertView.findViewById(R.id.imvNavIconSel);
            holder.rlNavRow 		= (RelativeLayout) convertView.findViewById(R.id.rlNavRow);
            holder.rlNavRowSel 		= (RelativeLayout) convertView.findViewById(R.id.rlNavRowSel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
       Model model = (Model) getItem(position);
       
       holder.txtNavTitle.setText(model.getTitle());
       holder.imvNavIcon.setImageResource(model.getIcon());
       holder.txtNavTitleSel.setText(model.getTitle());
       holder.imvNavIconSel.setImageResource(model.getIcon());
       
       if(model.isSelected()) {
    	   holder.rlNavRow.setVisibility(View.GONE);
    	   holder.rlNavRowSel.setVisibility(View.VISIBLE);
       } else {
    	   holder.rlNavRow.setVisibility(View.VISIBLE);
    	   holder.rlNavRowSel.setVisibility(View.GONE);
       }
        
       return convertView;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return list.indexOf(list.get(position));
	}

}
