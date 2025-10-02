package com.marlodev.app_android.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;

public class SplashActivity extends AppCompatActivity {

    private static final int splash_duration = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Handler seguro para Android 14+
        HandlerCompat.createAsync(getMainLooper())
                .postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, splash_duration);
    }
}
