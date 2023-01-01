package com.princemartbd.dealer.activity.Base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.princemartbd.dealer.R;
import com.princemartbd.dealer.helper.Session;

public class SplashActivity extends Activity {

    Activity activity;
    Session session;
    final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = SplashActivity.this;
        session = new Session(activity);
        session.setBoolean("update_skip", false);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimary));

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> startActivity(new Intent(SplashActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)), SPLASH_TIME_OUT);
    }
}
