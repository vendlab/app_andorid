package com.marlodev.app_android.ui.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.utils.SessionManager;

public class DeliveryMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ChipNavigationBar bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main); // tu XML compartido

        sessionManager = SessionManager.getInstance(this);

        // Ajuste Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation();
        checkSessionAndRedirect();
    }

    private void setupBottomNavigation() {
        ChipNavigationBar chipNavigationBar = findViewById(R.id.bottomNavigation);
        chipNavigationBar.setBackgroundColor(getResources().getColor(R.color.colorGrey100));

        // Fragment por defecto al iniciar
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new DeliveryHomeFragment())
                    .commit();
            chipNavigationBar.setItemSelected(R.id.menu_home_delivery, true);
        }

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            if (id == R.id.menu_home_delivery) fragment = new DeliveryHomeFragment();
            else if (id == R.id.menu_historial_delivery) fragment = new DeliveryHistorialFragment();
            else if (id == R.id.menu_ganancias_delivery) fragment = new DeliveryGananciasFragment();
            else if (id == R.id.menu_Mensajeria_delivery) fragment = new DeliveryMensajeriaFragment();
            else if (id == R.id.menu_perfil_delivery) {
                openProfileOrGuest();
                return;
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }
        });
    }

    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
        }
    }

    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new DeliveryPerfilFragment())
                    .commit();
        } else {
            Toast.makeText(this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
