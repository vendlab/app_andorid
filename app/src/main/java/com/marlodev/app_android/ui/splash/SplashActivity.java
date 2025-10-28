package com.marlodev.app_android.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Espera unos segundos antes de decidir a dónde ir
        HandlerCompat.createAsync(getMainLooper())
                .postDelayed(this::navigateNext, SPLASH_DURATION);
    }

    /**
     * ✅ Decide si ir al MainActivity o a otra pantalla según sesión
     * Si hay sesión → MainActivity (logueado)
     * Si no hay sesión → también MainActivity (modo invitado)
     */
    private void navigateNext() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        // 👉 Siempre redirige a MainActivity
        Intent intent = new Intent(this, MainActivity.class);

        // Si en algún momento quieres personalizar el flujo:
        // if (isLoggedIn) intent = new Intent(this, MainActivity.class);
        // else intent = new Intent(this, GuestActivity.class); // opcional

        startActivity(intent);
        finish();
    }
}
