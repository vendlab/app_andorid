package com.marlodev.app_android.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;

import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.admin.AdminMainActivity;
import com.marlodev.app_android.ui.barista.BaristaMainActivity;
import com.marlodev.app_android.ui.delivery.DeliveryMainActivity;
import com.marlodev.app_android.utils.SessionManager;

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
        SessionManager session = SessionManager.getInstance(this);

        Intent intent;

        if (session.isLoggedIn()) {
            // Obtiene rol del usuario
            String role = session.getRole();

            switch (role.toUpperCase()) {
                case "ADMIN":
                case "ROLE_ADMIN":
                    intent = new Intent(this, AdminMainActivity.class);
                    break;
                case "BARISTA":
                case "ROLE_BARISTA":
                    intent = new Intent(this, BaristaMainActivity.class);
                    break;
                case "DELIVERY":
                case "ROLE_DELIVERY":
                    intent = new Intent(this, DeliveryMainActivity.class);
                    break;
                default: // CLIENTE
                    intent = new Intent(this, MainActivity.class);
                    break;
            }
        } else {
            // Invitado
            intent = new Intent(this, MainActivity.class);
        }

        // Limpia la pila de actividades para que no quede atrás
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
