package com.impalapay.uk;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Promote_frag extends Fragment implements OnClickListener {

	View view;
	Button google,facebook,whatsapp,instagram,twitter;
	TextView share_app;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.activity_promote, container, false);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}
	private void init() {
	
		google=(Button) view.findViewById(R.id.google_bt);
		facebook=(Button) view.findViewById(R.id.facebook_bt);
		whatsapp=(Button) view.findViewById(R.id.whatapp_bt);
		instagram=(Button) view.findViewById(R.id.instagram_bt);
		twitter=(Button) view.findViewById(R.id.twiter_bt);
		share_app=(TextView) view.findViewById(R.id.share_app);
		
		google.setOnClickListener(this);
		facebook.setOnClickListener(this);
		instagram.setOnClickListener(this);
		whatsapp.setOnClickListener(this);
		twitter.setOnClickListener(this);
		
		
		share_app.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.google_bt:
			showshareDialog("com.google.android.apps.plus","Google plus");
			break;

		case R.id.facebook_bt:
			showshareDialog("com.facebook.katana","Facebook"); // com.facebook.katana
			
			break;
		case R.id.instagram_bt:
			showshareDialog("com.instagram.android","Instagram");
			break;
		case R.id.twiter_bt:
			showshareDialog("com.twitter.android","Twitter");
			break;
		case R.id.whatapp_bt:
			showshareDialog("com.whatsapp","WhatsApp");
			break;
		case R.id.share_app:
			showshareDialog();
			break;
			
		default:
			break;
		}
	}
	
	private void showshareDialog() {

		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		share.putExtra(Intent.EXTRA_SUBJECT, "ImpalaPayUK");
		share.putExtra(Intent.EXTRA_TEXT,"Send Money to Kenya with ImpalaPayUK. Instant transfers. Zero Fees. \nhttp://impalapay.network/");
		startActivity(Intent.createChooser(share, "Share ImpalaPayUK using"));
	}
	
	private void showshareDialog(String pkg,String name) {
		try {
			
			
			if(packageInstalledOrNot(pkg))
			{
				
				Intent share = new Intent(android.content.Intent.ACTION_SEND);
			    share.setType("text/plain");
			    share.setPackage(pkg);
			    share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    share.putExtra(Intent.EXTRA_SUBJECT, "ImpalaPayUK");
			    share.putExtra(Intent.EXTRA_TEXT, "Send Money to Kenya with ImpalaPayUK. Instant transfers. Zero Fees.");
			    startActivity(share);

			}
			else{
				Toast.makeText(getActivity(), name+" Not Found in Your device", Toast.LENGTH_SHORT).show();
			}
			
		} catch (Exception e) {
			Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	protected boolean packageInstalledOrNot(String pkgName) 
	{
		boolean app_installed = false;
		try {
			ApplicationInfo af = getActivity().getPackageManager().getApplicationInfo(pkgName, 0);
			app_installed = true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			app_installed = false;
		}
		return app_installed;
	}
}
