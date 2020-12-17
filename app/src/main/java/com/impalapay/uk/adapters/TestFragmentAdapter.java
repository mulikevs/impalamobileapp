package com.impalapay.uk.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.impalapay.uk.SpleshScreenPageOne;


public class TestFragmentAdapter extends FragmentPagerAdapter  {
 
  
	public TestFragmentAdapter(FragmentManager fm)
	{
		super(fm);
	}
	
	@Override
	public Fragment getItem(int index) 
	{
		switch (index) 
		{
		case 0:
			return new SpleshScreenPageOne();
		case 1:
			return new SpleshScreenPageOne();
		case 2:
			return new SpleshScreenPageOne();
		case 3:
			return new SpleshScreenPageOne();
	}
		return null;
	}

	
	
	@Override
	public int getCount() {
		return 4;
	}

   
}