package com.impalapay.uk.navigation_listview.utills;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.impalapay.uk.R;

public class StartProfileActivity extends Fragment
{

	static View view;
	Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.activity_startprofile, null);
		context=this.getActivity();
		return view;

	}
}