package com.impalapay.uk;

import android.app.Activity;

import android.os.Bundle;

import android.util.Log;
import android.widget.RelativeLayout;


public class Control_Application extends Activity {

    RelativeLayout mylayout;

    private static final String TAG = Control_Application.class.getName();

    /**
     * Gets reference to global Application
     * @return must always be type of ControlApplication! See AndroidManifest.xml
     */
    public ControlApplication getApp()
    {
        return (ControlApplication)this.getApplication();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_control__application);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        getApp().touch();
        Log.d(TAG, "User interaction to " + this.toString());
        Log.e("My Activity Touched", "My Activity Touched");

    }

}