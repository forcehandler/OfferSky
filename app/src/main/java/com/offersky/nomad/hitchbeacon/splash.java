package com.offersky.nomad.hitchbeacon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import static com.offersky.nomad.hitchbeacon.Hitchbeacon.context;

public class splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(this, IconTabsActivity.class);
        Log.d("splash", "calling get user form splash");
        //TODO: EMergency debugging in place
        //getUser();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean loggedin = sharedPreferences.getBoolean(Constants.SIGNEDIN,false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Intent intent1 = new Intent(this,IntroActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Hitchbeacon.user == null){
                    Log.i("Splash", "going to intro");
                    startActivity(intent1);
                    finish();
                }else {
                    Log.i("Splash", "going to coupons");
                    startActivity(intent);
                    finish();
                }
            }
        }, 5000);

    }
}