package com.impalapay.uk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impalapay.common.Common_data;
import com.impalapay.common.PrefManager;
import com.impalapay.common.RestHttpClient;
import com.impalapay.uk.adapters.NavigationAdapter;
import com.impalapay.uk.navigation_listview.utills.Model;
import com.impalapay.uk.navigation_listview.utills.SlidingMenuLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

	ListView listview;
	static SlidingMenuLayout slidingmenu_layout;
	Context context;
	Button lk_profile_menu, lk_profile_filter_btn,share_menu;
	ArrayList<String> values = new ArrayList<String>();
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<Model> navDrawerItems;
	private NavigationAdapter navigationAdapter;
	private Model selectedModel = null;
	RelativeLayout rlTitle;
	public static int logout_var = 0;
	String attachcardflag;
	String pin;
	EditText active_pin_et;
	ImageView active_confirm_pin;
	private String switchStatus;

	ProgressDialog dialog;

	LinearLayout hide_menu;
	private String isfillemail;
	private String fillemail;
	public static String version_name="";
	String base;

	public static boolean start_timer = false;
	public ControlApplication control = new ControlApplication();
	public static MainActivity main_activity;
	

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub

				//pin = Common_data.getPreferences(getBaseContext(), "pin");
		pin = new PrefManager<String>(getApplicationContext()).get("pin","");
	
				//String alert= Common_data.getPreferences(getApplicationContext(), "alertshow");

		String alert= new PrefManager<String>(getApplicationContext()).get("alertshow","");
			
				/*if(alert.equals("true"))
				{
				 
					Log.d("PIN", "" + pin);
					if (switchStatus.equals("true")) {
						if (!pin.equals("")) {
							final Dialog na = new Dialog(MainActivity.this);
			
							na.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			
							na.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
							na.setContentView(R.layout.pin_active_dialog);
							na.setCancelable(false);
							active_pin_et = (EditText) na.findViewById(R.id.active_pin_et);
							active_confirm_pin = (ImageView) na.findViewById(R.id.active_confirm_pin);
							na.show();
			
							active_confirm_pin.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									String pin_st = active_pin_et.getText().toString();
			
									if (pin_st.equals(pin)) {
										na.dismiss();
									}
			
									else {
										Toast.makeText(context, "Please Check Your PIN",Toast.LENGTH_LONG).show();
									}
			
								}
							});
						} else {
							showSetPinDialog();
						}
					}
				}else{
					Common_data.setPreference(getApplicationContext(), "alertshow", "true");
				}
		        */
				
				hideSoftKeyboard(MainActivity.this,hide_menu);
				
		super.onRestart();
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			MainActivity.hideSoftKeyboard(MainActivity.this,hide_menu);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		main_activity = MainActivity.this;
		start_timer = true;
		//control.initiate_timer();

		slidingmenu_layout = (SlidingMenuLayout) this.getLayoutInflater().inflate(R.layout.activity_main, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(slidingmenu_layout);

		// Common_data.setupUI(findViewById(R.id.lk_profile_fragment),
		// MainActivity.this);

		hide_menu = (LinearLayout) findViewById(R.id.hide_menu);

		//isfillemail= Common_data.getPreferences(getApplicationContext(), "emailfill");
		//base= Common_data.getPreferences(getApplicationContext(), "base");
		//fillemail= Common_data.getPreferences(getApplicationContext(), "filluser");
		//switchStatus = Common_data.getPreferences(getApplicationContext(), "switch");
		isfillemail = new PrefManager<String>(getApplicationContext()).get("emailfill","test");
		base = new PrefManager<String>(getApplicationContext()).get("base","USD");
		fillemail = new PrefManager<String>(getApplicationContext()).get("filluser","test");
		switchStatus = new PrefManager<String>(getApplicationContext()).get("switch","test");

		//System.out.println("The value of base1 "+base+" The value of base2 "+new PrefManager<String>(getApplicationContext()).get("base","white"));

		context = this;
		listview = (ListView) findViewById(R.id.list);
		rlTitle = (RelativeLayout) findViewById(R.id.rlTitle);

		//pin = Common_data.getPreferences(getBaseContext(), "pin");
		pin = new PrefManager<String>(getApplicationContext()).get("pin","");



		FragmentManager fm = getSupportFragmentManager();

		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		//fragmentTransaction.add(R.id.lk_profile_fragment,new Sawapay_Main_Screen());
		fragmentTransaction.add(R.id.lk_profile_fragment,new SendMoney1_Frag());
		fragmentTransaction.commit();

		attachcardflag = Common_data.getPreferences(context, "attachcard");

		if (attachcardflag.equals("1")) {
			display(4);
		}

		try
		{
			version_name = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			Log.d("version_name", e.getMessage());
		}

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		navDrawerItems = new ArrayList<Model>();

		// InputManager im=(InputManager)
		// context.getSystemService(Context.INPUT_METHOD_SERVICE);

		// adding nav drawer items to array
		/*
		 * navDrawerItems.add(new Model(navMenuTitles[0],
		 * navMenuIcons.getResourceId(0, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		 * navDrawerItems.add(new Model(navMenuTitles[2],
		 * navMenuIcons.getResourceId(2, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		 * navDrawerItems.add(new Model(navMenuTitles[4],
		 * navMenuIcons.getResourceId(4, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		 * navDrawerItems.add(new Model(navMenuTitles[6],
		 * navMenuIcons.getResourceId(6, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		 * navDrawerItems.add(new Model(navMenuTitles[8],
		 * navMenuIcons.getResourceId(8, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		 * navDrawerItems.add(new Model(navMenuTitles[10],
		 * navMenuIcons.getResourceId(10, -1))); navDrawerItems.add(new
		 * Model(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));
		 */

		//for (int i = 0; i <= 14; i++)
		for(int i = 0; i < navMenuTitles.length; i++)
			navDrawerItems.add(new Model(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		navigationAdapter = new NavigationAdapter(MainActivity.this,navDrawerItems);

		listview.setAdapter(navigationAdapter);

		// display(0);

		lk_profile_menu = (Button) findViewById(R.id.button_menu);

		rlTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				startActivity(intent);
				hide_menu.setVisibility(View.GONE);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		lk_profile_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				listview.smoothScrollToPosition(0);
				toggleMenu(v);
				hideSoftKeyboard(MainActivity.this,hide_menu);
				if(hide_menu.getVisibility()==View.GONE)
					hide_menu.setVisibility(View.VISIBLE);
				else
					hide_menu.setVisibility(View.GONE);
			}
		});

		hide_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hide_menu.setVisibility(View.GONE);
				listview.smoothScrollToPosition(0);
				toggleMenu(v);
				overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			}
		});

		listview.setOnItemClickListener(new SlideMenuClickListener());

		//getApp().touch();

		if (switchStatus.equals("true")) {
			if (pin.equals("")) {
				showSetPinDialog();
			}
		}

	}

	private void showSetPinDialog() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
		builder1.setTitle("Set Security Pin");

		builder1.setCancelable(false);
		builder1.setPositiveButton("Set Pin",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						FragmentManager fm = getSupportFragmentManager();
						FragmentTransaction st = fm.beginTransaction();
						st.replace(R.id.lk_profile_fragment, new Setting_Frag());
						st.addToBackStack(null);
						st.commit();
						overridePendingTransition(R.anim.slide_in,
								R.anim.slide_out);
					}
				});
		TextView tv = new TextView(MainActivity.this);
		tv.setText("Set Your Pin (security code) From Settings");
		tv.setTextSize(18);
		tv.setPadding(15, 15, 10, 15);
		tv.setTextColor(Color.BLUE);
		builder1.setView(tv);
		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public static void toggleMenu(View v) {
		slidingmenu_layout.toggleMenu();
	}

	public static void start_fragment(FragmentActivity act,Fragment frag, String tag) {
		FragmentManager fm = act.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.lk_profile_fragment, frag, tag);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		act.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			toggleMenu(view);
			display(position);
			Model model = (Model) parent.getItemAtPosition(position);
			if (selectedModel != null) {
				selectedModel.setSelected(false);
			}
			model.setSelected(true);
			selectedModel = model;
			navigationAdapter.notifyDataSetChanged();
		}
	}

	public static void hideSoftKeyboard(Activity activity,View view) {
		view = activity.getCurrentFocus();
		if (view != null) {  
		    InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		//InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	//	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public void display(int position) {
		FragmentManager fm = getSupportFragmentManager();
		switch (position) {
		/******************************* jump to HOME *****************************/
		case 0:
			hide_menu.setVisibility(View.GONE);
			Intent intent1 = new Intent(getBaseContext(), ProfileActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
			
			break;

		case 1:

			hide_menu.setVisibility(View.GONE);
			
			SendMoney1_Frag sf = new SendMoney1_Frag();

			Bundle a = new Bundle();
			a.putBoolean("checked", false);
			sf.setArguments(a);

			FragmentManager fs1 = getSupportFragmentManager();
			FragmentTransaction f1 = fs1.beginTransaction();
			f1.replace(R.id.lk_profile_fragment, sf, "SendMoney1");
			f1.addToBackStack(null);
			f1.commit();

			break;

		/*
			case 2:
		
		
			
			hide_menu.setVisibility(View.GONE);
			
			SendMoney1_Frag sf1 = new SendMoney1_Frag();

			Bundle args = new Bundle();
			args.putBoolean("checked", true);
			sf1.setArguments(args);

			FragmentManager fs = getSupportFragmentManager();
			FragmentTransaction f = fs.beginTransaction();
			f.replace(R.id.lk_profile_fragment, sf1);
			f.addToBackStack(null);
			f.commit();
			
			break;
		*/

		/*
		case 3:
	
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new AirTime_Frag());
			break;
		case 4:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new WithdrawFund());
			break;
			*/

		/*case 2:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new TopUpFund());
			break; */

		case 2:

			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new ManageCard(), "ManageCard");
			break;

		case 3:

				hide_menu.setVisibility(View.GONE);
				start_fragment(MainActivity.this, new ManageAccount(), "ManageAccount");
				break;



		case 4:
			hide_menu.setVisibility(View.GONE);
			//start_fragment(MainActivity.this, new Add_Beneficiary(),"Add_Beneficiary");
			start_fragment(MainActivity.this, new ManageBeneficiaries(),"ManageBeneficiaries");
			break;

		case 5:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Transaction_History_frag(),"Transaction_History_frag");
			break;

		case 6:
				hide_menu.setVisibility(View.GONE);
			String message = "Check out SawaPay, the new premier way to send money to Kenya, Uganda & Zimbabwe. Start saving today, download the app at https://www.sawa-pay.com/";
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);

			startActivity(Intent.createChooser(share, "Share with your friends & family"));
				break;

		case 7:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Setting_Frag(),"Setting_Frag");
			break;

		/*
			case 8:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Deals_frag());
			break;
		*/

		/*case 11:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Promote_frag());
			break; */

		case 8:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Contact_us_frag(), "Contact_us_frag");
			break;

		/*case 7:
			hide_menu.setVisibility(View.GONE);
			start_fragment(MainActivity.this, new Customer_Support_frag());
			break; */

		case 9:
			//logout_dialog();
			logout();
			break;

		
		default:

			break;
		}

	}

	//@Override
	public void onBackPressed() {
		/*Intent intent = new Intent(getBaseContext(), MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
		super.onBackPressed(); */
		hide_menu.setVisibility(View.GONE);
		Common_data.setPreference(MainActivity.this, "switch", "true");
		/*Intent intent = new Intent(context, SpleshScreen.class);
		startActivity(intent);*/
		//Common_data.Preferencesclear(context);

		Common_data.setPreference(getApplicationContext(), "emailfill", isfillemail);
		Common_data.setPreference(getApplicationContext(), "filluser", fillemail);
		//finish();
		/*if(getFragmentManager().getBackStackEntryCount() == 0) {
			super.onBackPressed();
		}
		else {
			getFragmentManager().popBackStack();
		} */

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment myFragment = fragmentManager.findFragmentByTag("SendMoney1");
		if (myFragment != null && myFragment.isVisible()) {
			Log.d("SendMoney1", "Visible");
			Common_data.Preferencesclear(context);
			Intent intent = new Intent(context, SpleshScreen.class);
			startActivity(intent);
		}
		else{
			Log.d("SendMoney1", "Invisible");
			hide_menu.setVisibility(View.GONE);

			SendMoney1_Frag sf = new SendMoney1_Frag();

			Bundle a = new Bundle();
			a.putBoolean("checked", false);
			sf.setArguments(a);

			FragmentManager fs1 = getSupportFragmentManager();
			FragmentTransaction f1 = fs1.beginTransaction();
			f1.replace(R.id.lk_profile_fragment, sf, "SendMoney1");
			f1.addToBackStack(null);
			f1.commit();
		}
	}

	public void logout_dialog() {

		final Context context = MainActivity.this;
		String title = "Logout";
		String message = "Are you sure to logout ?";
		String button1String = "YES";
		String button2String = "NO";
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton(button1String,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int arg1) {
						Common_data.setPreference(MainActivity.this, "switch", "true");
						Intent intent = new Intent(context, SpleshScreen.class);
						startActivity(intent);
						Common_data.Preferencesclear(context);
						
						Common_data.setPreference(getApplicationContext(), "emailfill", isfillemail);
						Common_data.setPreference(getApplicationContext(), "filluser", fillemail);
						finish();

					}
				});

		ad.setNegativeButton(button2String,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int arg1) {
					}
				});
		ad.show();

	}


	private void logout() {
		RequestParams params = new RequestParams();
		JSONObject object = new JSONObject();

		try {
			String user_id = Common_data.getPreferences(MainActivity.this, "userid");
			object.put("user_id", user_id);

			params.put("request", object.toString());
			Log.d("request", params.toString());
		}

		catch (JSONException e) {
			e.printStackTrace();
		}
		RestHttpClient.postParams("auth/logout", params, new LogoutHandler(), Login.token);
	}

	class LogoutHandler extends AsyncHttpResponseHandler {
		@SuppressWarnings("static-access")
		@Override
		public void onStart() {

			super.onStart();
			dialog = ProgressDialog.show(MainActivity.this, null, "Logging out... ");

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onSuccess(String result) {

			super.onSuccess(result);

			Log.d("onSuccess", "onSuccess");

			if (result.length() > 0) {
				Log.d("response", result);
				try {
					JSONObject json = new JSONObject(result);

					boolean b = false;
					int response_code = json.getInt("code");
					if(1001 == response_code)
						b = true;
					final String msg = json.optString("message");

					if (b) {

						hide_menu.setVisibility(View.GONE);
						Common_data.setPreference(MainActivity.this, "switch", "true");
						Common_data.Preferencesclear(context);

						Common_data.setPreference(getApplicationContext(), "emailfill", isfillemail);
						Common_data.setPreference(getApplicationContext(), "filluser", fillemail);


						final Dialog d=new Dialog(MainActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.success_response);
						TextView tv_success = (TextView) d.findViewById(R.id.tv_success);
						tv_success.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
						btn_ok.setText("OK");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								//Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
								//Common_data.Preferencesclear(MainActivity.th);
								d.dismiss();
								Intent i = new Intent(MainActivity.this, SpleshScreen.class);
								startActivity(i);
								finish();
							}
						});
						d.show();
					}
					else if(1030 == response_code){
						//Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
						final Dialog d=new Dialog(MainActivity.this);
						d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						d.requestWindowFeature(Window.FEATURE_NO_TITLE);
						d.setCancelable(false);
						d.setContentView(R.layout.dialog1);
						TextView tv_dialog = (TextView) d.findViewById(R.id.tv_dialog);
						tv_dialog.setText(msg);
						Button btn_ok = (Button) d.findViewById(R.id.btn_dialog);
						btn_ok.setText("Update Now");
						btn_ok.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								d.dismiss();
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(getString(R.string.app_store_url)));
								startActivity(i);
							}
						});
						d.show();
					}
					else {
						Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
						new AlertDialog.Builder(MainActivity.this)
								.setTitle("Info")
								.setMessage(msg)
								.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										logout();
									}
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										// TODO Auto-generated method stub
										//finish();
										//startActivity(getIntent());
									}
								}).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onFinish() {
			super.onFinish();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			/*try {
				String what=getIntent().getExtras().getString("iswhat");
				Common_data.setPreference(getApplicationContext(), "iswhat", what);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			//Common_data.showAccountVerifyDialog(AttachCardActivity.this);
		}

		@Override
		@Deprecated
		public void onFailure(int statusCode, Throwable error, String content) {
			Log.d("onFailure", "onFailure");
			super.onFailure(statusCode, error, content);
			Toast.makeText(getApplicationContext(), "Network Error. Please retry!",Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("Info")
					.setMessage("Network Error. Please retry!")
					.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							logout();
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							//finish();
							//startActivity(getIntent());
						}
					}).show();
		}
	}


	/*private void timeout() {

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d("MainActivity", "timeout");
				Common_data.Preferencesclear(context);
				Intent intent = new Intent(MainActivity.this, SpleshScreen.class);
				startActivity(intent);

			}
		}, 10000);

	}

	@Override
	protected void onPause() {
		super.onPause();
		timeout();
	}*/

	public ControlApplication getApp()
	{
		return (ControlApplication)this.getApplication();
	}


	@Override
	public void onUserInteraction()
	{
		super.onUserInteraction();
		//getApp().touch();
		Log.d("User", "User interaction to "+this.toString());
		Log.e("My Activity Touched", "My Activity Touched");

	}
	
}