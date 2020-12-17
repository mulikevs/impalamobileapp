package com.impalapay.uk;

/**
 * Created by Sly on 2016-02-26.
 */
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.List;

public class  ControlApplication extends Application{
    int count=0;
    MyCounter timer;

    private static final String TAG=ControlApplication.class.getName();

    @Override
    public void onCreate()
    {
        super.onCreate();
        timer = new MyCounter(300000,60000);
        //timer.start();

    }

    public void touch()
    {
        //timer = null;
        if(MainActivity.start_timer){
            timer.start();
        }

    }

    public void initiate_timer(){
        timer = new MyCounter(10000,1000);
        timer.start();
    }

    public class MyCounter extends CountDownTimer{

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {

            System.out.println("Timer Completed.");
            Log.d("finish is called", "finish is called");

            /*final Dialog d=new Dialog(MainActivity.main_activity);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setCancelable(false);
            d.setContentView(R.layout.success_response);
            TextView tv_success = (TextView) d.findViewById(R.id.tv_success);

            tv_success.setText("Your session has expired. Please login again");

            Button btn_ok = (Button) d.findViewById(R.id.btn_ok);
            btn_ok.setText("Ok");
            btn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent();
                    intent.setClass(ControlApplication.this, SpleshScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            d.show(); */

           /* if(isAppIsInBackground(getApplicationContext())){
                Login.session_tv.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setClass(ControlApplication.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //System.exit(0);
                //finish();

                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
            else{
                Login.session_tv.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setClass(ControlApplication.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } */

        }

        @Override
        public void onTick(long millisUntilFinished) {
            System.out.println("Timer  : " + (millisUntilFinished / 1000));
        }

        private boolean isAppIsInBackground(Context context) {
            boolean isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }

            return isInBackground;
        }
    }

}
