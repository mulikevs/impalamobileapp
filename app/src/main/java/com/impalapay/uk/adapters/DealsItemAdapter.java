package com.impalapay.uk.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.impalapay.models.DealsItemModel;
import com.impalapay.uk.R;
import com.squareup.picasso.Picasso;

public class DealsItemAdapter extends BaseAdapter{

	View view;
	Context context;
	ArrayList<DealsItemModel> array;
	
	Bitmap myBitmap;
	public DealsItemAdapter(Context context,ArrayList<DealsItemModel> array) {
		this.context=context;
		this.array=array;
		
	}
	@Override
	public int getCount() {
		return this.array.size();
	}

	@Override
	public Object getItem(int pos) {
		return array.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return array.indexOf(array.get(arg0));
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.deals_list_item, null);


		ImageView ico=(ImageView) view.findViewById(R.id.img_offer_deals);
		TextView title=(TextView) view.findViewById(R.id.title_tv_deals);
		TextView discount=(TextView) view.findViewById(R.id.offer_tv_deals);
		TextView price=((TextView) view.findViewById(R.id.rate_tv_deals));
		TextView coupon_code_tv=(TextView) view.findViewById(R.id.coupon_code_tv);
		DealsItemModel model=(DealsItemModel) getItem(pos);
		
		Picasso.with(context).load(model.getImgUrl()).into(ico);
		
		title.setText(model.getTitle());
		discount.setText("Discount: "+model.getDiscount_per()+"%");
		price.setText("Price: "+model.getPrice());
		//coupon_code_tv.setText("Coupon Code: "+model.getCoupon_code());
		return view;
	}
}
