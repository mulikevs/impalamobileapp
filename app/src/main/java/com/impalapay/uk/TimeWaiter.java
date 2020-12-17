package com.impalapay.uk;

import android.util.Log;

/**
 * Created by Sylvester Mwambeke on 2016-02-25.
 */
public class TimeWaiter extends Thread
{
    private static final String TAG=TimeWaiter.class.getName();
    private long lastUsed;
    private long period;
    private boolean stop;

    public TimeWaiter(long period)
    {
        this.period=period;
        stop=false;
    }

    public void run()
    {
        long idle=0;
        this.touch();
        do
        {
            idle=System.currentTimeMillis()-lastUsed;
            Log.d(TAG, "Application is idle for " + idle + " ms");
            try
            {
                Thread.sleep(5000); //check every 5 seconds
            }
            catch (InterruptedException e)
            {
                Log.d(TAG, "TimeWaiter interrupted!");
            }
            if(idle > period)
            {
                idle=0;
                //do something here - e.g. call popup or so
            }
        }
        while(!stop);
        Log.d(TAG, "Finishing TimeWaiter thread");
    }

    public synchronized void touch()
    {
        lastUsed=System.currentTimeMillis();
    }

    public synchronized void forceInterrupt()
    {
        this.interrupt();
    }

    //soft stopping of thread
   /* public synchronized void stop()
    {
        stop=true;
    }*/

    public synchronized void setPeriod(long period)
    {
        this.period=period;
    }

}
