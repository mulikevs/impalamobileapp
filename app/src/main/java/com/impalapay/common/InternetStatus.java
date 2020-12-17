package com.impalapay.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/****************** Checking the Internet connection state ********************/
public class InternetStatus {

	static Context context;
	ConnectivityManager connectivityManager;
	NetworkInfo wifiInfo, mobileInfo;
	boolean connected = false;

	public InternetStatus(Context ctx) {
		context = ctx;

	}

	public boolean isOnline() {
		try {
			connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
				if (info != null)
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							connected = true;
						}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return connected;
	}
}
