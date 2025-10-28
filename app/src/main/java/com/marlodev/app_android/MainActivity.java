package com.marlodev.app_android;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ismaeldivita.chipnavigation.ChipNavigationBar; // ✅ Importa esta librería si usas ChipNavigationBar
import com.marlodev.app_android.R;
import com.marlodev.app_android.ui.admin.AdminDashboardActivity;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.ui.client.ClientPerfilActivity;
import com.marlodev.app_android.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar bottomNavigation;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = SessionManager.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation); // Asegúrate de usar el id correcto en tu layout XML

        // 🔍 Revisa sesión y rol al abrir la app
        checkSessionAndRedirect();

        // ⚙️ Configura la barra inferior
        setupBottomNavigation();
    }

    /**
     * Comprueba si hay sesión activa y redirige según el rol.
     * Si no hay sesión, se queda en MainActivity (modo invitado).
     */
    private void checkSessionAndRedirect() {
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();

            if ("ADMIN".equalsIgnoreCase(role)) {
                // 🔐 Usuario administrador → redirigir al dashboard
                Intent intent = new Intent(this, AdminDashboardActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            if ("CLIENT".equalsIgnoreCase(role)) {
                Toast.makeText(this, "Bienvenido cliente " + sessionManager.getEmail(), Toast.LENGTH_SHORT).show();
            }

        } else {
            // 👤 Usuario invitado
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Configura los eventos de los ítems del menú inferior
     */
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(id -> {
            if (id == R.id.menu_home) {
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.menu_car) {
                Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.menu_search) {
                Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.menu_delivery) {
                Toast.makeText(this, "Delivery", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.menu_perfil) {
                openProfileOrGuest();
            }
        });
    }

    /**
     * Abre la pantalla según si hay sesión y rol
     */
    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();

            if ("ADMIN".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, ClientPerfilActivity.class));
            }

        } else {
            Toast.makeText(this, "Estás en modo invitado. Inicia sesión para acceder al perfil.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
