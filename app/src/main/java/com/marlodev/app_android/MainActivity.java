package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.ui.admin.AdminMainActivity;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.ui.client.ClientCarFragment;
import com.marlodev.app_android.ui.client.ClientHomeFragment;
import com.marlodev.app_android.ui.client.ClientOrderFragment;
import com.marlodev.app_android.ui.client.ClientPerfilFragment;
import com.marlodev.app_android.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);

        checkSessionAndRedirect();
        setupBottomNavigation();
    }

    /**
     * Verifica la sesión y redirige según rol.
     */
    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
        }
    }

    /**
     * Configura la navegación inferior y los fragments iniciales.
     */
    private void setupBottomNavigation() {
        ChipNavigationBar chipNavigationBar = binding.bottomNavigation;
        chipNavigationBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGrey100));

        // Fragmento inicial
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            replaceFragment(new ClientHomeFragment());
            chipNavigationBar.setItemSelected(R.id.menu_home, true);
        }

        chipNavigationBar.setOnItemSelectedListener(id -> {
            if (id == R.id.menu_home) {
                replaceFragment(new ClientHomeFragment());
            } else if (id == R.id.menu_car) {
                replaceFragment(new ClientCarFragment());
            } else if (id == R.id.menu_delivery) {
                replaceFragment(new ClientOrderFragment());
            } else if (id == R.id.menu_perfil) {
                openProfileOrGuest();
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void openProfileOrGuest() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Estás en modo invitado. Inicia sesión para acceder al perfil.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
            startActivity(new Intent(this, AdminMainActivity.class));
            return;
        }

        replaceFragment(new ClientPerfilFragment());
    }
}
