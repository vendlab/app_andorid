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
        setContentView(R.layout.activity_delivery_main);

        sessionManager = SessionManager.getInstance(this);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 🔷 Ajuste de padding para dispositivos con notch o barras de sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkSessionAndRedirect();
        setupBottomNavigation();
    }

    /**
     * Configura la barra de navegación inferior.
     * Define el fragmento inicial y los listeners de los menús.
     */
    private void setupBottomNavigation() {
        bottomNavigation.setBackgroundColor(getResources().getColor(R.color.colorGrey100));

        // 🔹 Fragmento por defecto al iniciar
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            loadFragment(new DeliveryHomeFragment());
            bottomNavigation.setItemSelected(R.id.menu_home_delivery, true);
        }

        // 🔹 Escucha de selección de ítems en el menú
        bottomNavigation.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            if (id == R.id.menu_home_delivery) {
                fragment = new DeliveryHomeFragment();
            } else if (id == R.id.menu_historial_delivery) {
                fragment = new DeliveryHistorialFragment();
            } else if (id == R.id.menu_ganancias_delivery) {
                fragment = new DeliveryGananciasFragment();
            } else if (id == R.id.menu_Mensajeria_delivery) {
                fragment = new DeliveryMensajeriaFragment();
            } else if (id == R.id.menu_perfil_delivery) {
                openProfileOrGuest();
                return;
            }
            if (fragment != null) loadFragment(fragment);
        });
    }

    /**
     * Muestra mensaje de bienvenida si el usuario no está logueado.
     */
    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Abre el perfil si hay sesión, o redirige al login si es invitado.
     */
    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            loadFragment(new DeliveryPerfilFragment());
        } else {
            Toast.makeText(this, "Inicia sesión para acceder al perfil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * Método auxiliar para cargar fragmentos.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
