package com.offersky.nomad.hitchbeacon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("SPLASH_PREF", Context.MODE_PRIVATE);
        Log.i("IntroActivity", pref.getBoolean("first time", true) + " value in preferences");
        if(pref.getBoolean("first time", true))
        {

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("first time", false);
            editor.commit();
        }
        else
        {
            Intent intent = new Intent(IntroActivity.this, OtpAuth.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        //set transition animation
        setFadeAnimation();

        //disable skip button
        showSkipButton(false);
        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(IntroSlides.newInstance(R.layout.first_intro_slide));
        addSlide(IntroSlides.newInstance(R.layout.second_intro_slide));
        addSlide(IntroSlides.newInstance(R.layout.third_intro_slide));
        addSlide(IntroSlides.newInstance(R.layout.fourth_intro_slide));

        /*setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));*/
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent intent = new Intent(this, OtpAuth.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, OtpAuth.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
