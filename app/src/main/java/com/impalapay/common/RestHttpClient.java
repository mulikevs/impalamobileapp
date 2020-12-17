package com.impalapay.common;

import android.content.Context;
import android.util.Log;

import com.impalapay.uk.MainActivity;
import com.impalapay.uk.SpleshScreen;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.ByteArrayEntity;

import java.io.UnsupportedEncodingException;


public class RestHttpClient 
{
	private static final String APPID = "3";
	private static final String ACCESSKEY = "18101987QWERTY";
	private static String VERSIONNAME = SpleshScreen.version_name; //"1.2.9";



	//public static String GG ="http://10.0.2.2/apisawa/";
	//public static String GG ="http://192.168.0.111/apisawa_staging/apisawa_staging/";
	//public static String GG ="https://www.sawa-pay.com/apisawa_live/";
	//public static String GG ="http://197.232.39.174:8081/sawapay/";
	//public static String GG ="http://192.168.0.110/apisawa_prod/";
	//public static String GG ="https://www.sawa-pay.com/apisawa_staging/";
	//public static String GG ="http://192.168.30.124:8081/apisawa2/";
	//public static String GG ="http://192.168.30.66:8081/comet/";
	//public static String GG ="https://impalapayuk.impalapay.net/";
	//public static String GG ="http://197.232.39.174:8081/apisawa2/";
	public static String GG ="http://197.232.39.174:80/apisawa2/";
	//public static String GG ="http://192.168.30.124:8081/apisawa2/";




	public static AsyncHttpClient client = new AsyncHttpClient();

	
	public static void post(String url, AsyncHttpResponseHandler handler)
	{
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
		client.addHeader("access-key", ACCESSKEY);
		client.addHeader("app-id", APPID);
		client.addHeader("version-name", VERSIONNAME);
		client.post(getAbsoluteUrl(url), handler);
		 client.setTimeout(30000);
	}

    public static void post(String url, AsyncHttpResponseHandler handler, String token)
    {
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
        Log.i("url_12", getAbsoluteUrl(url));

        client.addHeader("access-key", ACCESSKEY);
        client.addHeader("app-id", APPID);
		client.addHeader("version-name", VERSIONNAME);
        client.addHeader("token", token);
        client.post(getAbsoluteUrl(url), handler);
        client.setTimeout(30000);
    }

	public static void postParams(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) 
	{  
		Log.i("url_12", getAbsoluteUrl(url));
		System.out.println("EEH KAKA NOMA "+params);
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
		Log.i("params", ""+params);
		client.addHeader("access-key", ACCESSKEY);
		client.addHeader("app-id", APPID);
		client.addHeader("version-name", VERSIONNAME);
		client.post(getAbsoluteUrl(url), params, responseHandler);
		 client.setTimeout(30000);
	}

    public static void postParams(String url, RequestParams params, AsyncHttpResponseHandler handler, String token)
    {
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
        Log.i("url_12", getAbsoluteUrl(url));
        Log.i("params", ""+params);
		Log.i("the TOKEN",token);
		Log.i("VERSION",VERSIONNAME);


		client.addHeader("access-key", ACCESSKEY);
        client.addHeader("app-id", APPID);
		client.addHeader("version-name", VERSIONNAME);
        client.addHeader("token", token);
        client.post(getAbsoluteUrl(url), params, handler);
        client.setTimeout(30000);
    }
	
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler)
	{
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
		client.addHeader("access-key", ACCESSKEY);
		client.addHeader("app-id", APPID);
		client.addHeader("version-name", VERSIONNAME);
		client.post(getAbsoluteUrl(url), params, handler);
	}

	public static void postDirect(Context con, String url, String params, AsyncHttpResponseHandler handler)
	{
		if(VERSIONNAME.equals("")){
			VERSIONNAME = MainActivity.version_name;
		}
		try
		{
			ByteArrayEntity bob = new ByteArrayEntity(params.getBytes("UTF-8"));
			client.addHeader("access-key", ACCESSKEY);
			client.addHeader("app-id", APPID);
			client.addHeader("version-name", VERSIONNAME);
			client.post(con, getAbsoluteUrl(url), bob, "application/json",	handler);
		}
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void get(String url, AsyncHttpResponseHandler handler) 
	{
		client.get(url, handler);
	}

	public static void _get(String url, AsyncHttpResponseHandler handler) 
	{
		client.get(getAbsoluteUrl(url), handler);
	}

	public static String getAbsoluteUrl(String SubUrl) 
	{
		Log.d("url", GG + SubUrl);
		Log.d("url", "here");
		return GG + SubUrl;
	}

	public static void cancelRequestMethods(Context con, boolean bolean) 
	{
		client.cancelRequests(con, bolean);
	}
}
