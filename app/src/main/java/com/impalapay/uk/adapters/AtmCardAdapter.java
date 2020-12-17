package com.impalapay.uk.adapters;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.impalapay.uk.R;

public class AtmCardAdapter extends ArrayAdapter<String> {
	
	private final Activity context;
	
	private final Integer[] imageId;
	private final Integer[] overlay;
	private final String[] cardtype;
	private final String[]cardname;
	private final String[]accountnumber;
	private final String[] verify;
	int banklength,cardlength;
	int totallength;
	String checkverify;

	public AtmCardAdapter(FragmentActivity activity, Integer[] imageId2, Integer[] overlay2,String[] cardname2, String[] cardtype2, String[] account_number2, String[] verify2,int banklength2,int cardlength2) 
	{
			super(activity, R.layout.single_atmcard_item, cardname2);
			this.context = activity;
			this.cardname = cardname2;
			this.imageId = imageId2;
			this.accountnumber=account_number2;
			this.cardtype = cardtype2;
			this.verify=verify2;
			this.overlay=overlay2;
			this.banklength=banklength2;
			this.cardlength=cardlength2;
			
		}

	@Override
	public View getView(int position, View view, ViewGroup parent) 
	{
	LayoutInflater inflater = context.getLayoutInflater();
	View rowView= inflater.inflate(R.layout.single_atmcard_item, null, true);
	
	RelativeLayout atm_lay=(RelativeLayout)rowView.findViewById(R.id.atm_lay);
	
	TextView card_name = (TextView) rowView.findViewById(R.id.card_name);
	TextView card_name_bank = (TextView) rowView.findViewById(R.id.card_type_text);
	TextView account_number = (TextView) rowView.findViewById(R.id.account_number);
	
	TextView verify_tv=(TextView)rowView.findViewById(R.id.verify_tv);
	ImageView card_type = (ImageView) rowView.findViewById(R.id.card_type_im);
	
	TextView verify_im = (TextView) rowView.findViewById(R.id.verify_im);
	
	ImageView backround_overlay= (ImageView) rowView.findViewById(R.id.backround_overlay);
	
	card_name.setText(cardname[position]);
	card_name_bank.setText(cardtype[position]);
	account_number.setText(accountnumber[position]);
	//card_type.setImageResource(imageId[position]);
	//backround_overlay.setImageResource(overlay[position]);
	
	
	
	checkverify=verify[position];
	
	Log.d("checkverify", ""+checkverify);
	
	if(checkverify.equals("0"))
	{
		verify_tv.setVisibility(View.VISIBLE);
		verify_im.setVisibility(View.VISIBLE);
	}
	if (checkverify.equals("2")){
		verify_tv.setText("Under Verification");
		verify_tv.setVisibility(View.VISIBLE);
	}
	
	else if(checkverify.equals("1"))
	{
		verify_tv.setVisibility(View.INVISIBLE);
		verify_im.setVisibility(View.INVISIBLE);
	}
	
	totallength=banklength+cardlength;
	
	Log.d("totallength", ""+totallength);
	Log.d("banklength", ""+banklength);
	Log.d("cardlength", ""+cardlength);
	
	/*for(int i=0;i<banklength;i++)
	{
		if(position==i)
		{
			atm_lay.setBackgroundResource(R.drawable.atm_card_corner_green);
		}
	}*/
	
	
	/*for(int i=banklength;i<totallength;i++)
	{
		
			String colo=cardtype[i];
			
			Log.d("colo", colo);
			Log.d("position", ""+position);
			
			if(position==i)
			{
			
			
			if(colo.equals("Visa International"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_skyblue);
			}
			
			else if(colo.equals("Amex Card"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_yellow);
			}
			
			
			
			else if(colo.equals("Master Card"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_brow);
			}
			
			
			
			else if(colo.equals("Japan Credit Bureau Card"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_jcb);
			}
			
			
			
			else if(colo.equals("Discover Credit Card"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_discover);
			}
			
			
			else if(colo.equals("Dinner club International"))
			{
				atm_lay.setBackgroundResource(R.drawable.atm_card_corner_dinner);
			}
			}
	
	}*/
	
	
	
	return rowView;
	}
	}