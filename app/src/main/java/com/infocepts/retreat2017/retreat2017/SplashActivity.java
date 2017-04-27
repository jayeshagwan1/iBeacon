package com.infocepts.retreat2017.retreat2017;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

/**
 * Splash activity for the application
 * 
 */
public class SplashActivity extends Activity
{
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private Handler mHandler = new Handler(); 
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() 
        {
            /** Start your app main activity */
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);

            /** close this activity */
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(this,"No Internet connection", Toast.LENGTH_LONG).show();
            finish(); //Calling this method to close this activity when internet is not available.
        }
        setContentView(R.layout.splash_screen);
        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conMan.getActiveNetworkInfo() != null && conMan.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onDestroy()
    {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }
}