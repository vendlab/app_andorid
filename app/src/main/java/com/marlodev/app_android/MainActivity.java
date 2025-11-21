package com.marlodev.app_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
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

        // 1. Configurar UI profesional (Edge-to-Edge)
        setupEdgeToEdgeDisplay();

        // 2. Redirigir si es necesario (ej. si es Admin)
        if (checkSessionAndRedirect()) {
            return; // No continuar si se va a redirigir
        }

        // 3. Configurar la navegación solo si no se ha redirigido
        setupBottomNavigation();
    }

    /**
     * Configura la app para usar toda la pantalla (detrás de las barras de estado y navegación).
     * Es la práctica moderna recomendada para una UI consistente y sin saltos de color.
     */
    private void setupEdgeToEdgeDisplay() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
            int left = insets.getSystemWindowInsetLeft();
            int top = insets.getSystemWindowInsetTop();
            int right = insets.getSystemWindowInsetRight();
            int bottom = insets.getSystemWindowInsetBottom();

            // Aplicar los insets como padding a la vista raíz.
            // Esto crea un "área segura" para todo el contenido de la actividad.
            // El padding inferior empujará la ChipNavigationBar hacia arriba para que no se solape
            // con la barra de navegación del sistema.
            view.setPadding(left, top, right, bottom);

            // Ya no es necesario (y era incorrecto) aplicar padding directamente a la barra de navegación.
            // El padding de la vista raíz ya la posiciona correctamente.

            // Devolvemos los insets para que otros listeners (si los hubiera) puedan usarlos.
            return insets;
        });
    }


    /**
     * Verifica la sesión del usuario. Si es un administrador, lo redirige a su
     * actividad correspondiente y finaliza esta. Si es un invitado, muestra un mensaje.
     * @return `true` si se ha iniciado una redirección, `false` en caso contrario.
     */
    private boolean checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
            return false; // No redirige, se queda como invitado
        }

        if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
            startActivity(new Intent(this, AdminMainActivity.class));
            finish();
            return true; // Se ha redirigido
        }

        return false; // Es un cliente logueado, no se redirige
    }

    /**
     * Configura la barra de navegación inferior (ChipNavigationBar).
     * Establece el fragmento inicial y maneja la selección de ítems.
     */
    private void setupBottomNavigation() {
        ChipNavigationBar navBar = binding.bottomNavigation;

        // Cargar el fragmento inicial solo si no hay uno ya (evita recrearlo en giros de pantalla)
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            navBar.setItemSelected(R.id.menu_home, true); 
            replaceFragment(new ClientHomeFragment());
        }

        navBar.setOnItemSelectedListener(this::handleNavigationItemSelected);
    }

    /**
     * Maneja los clics en los ítems de la barra de navegación.
     * @param id El ID del ítem seleccionado.
     */
    private void handleNavigationItemSelected(int id) {
        Fragment selectedFragment = null;

        if (id == R.id.menu_home) {
            selectedFragment = new ClientHomeFragment();
        } else if (id == R.id.menu_car) {
            selectedFragment = new ClientCarFragment();
        } else if (id == R.id.menu_delivery) {
            selectedFragment = new ClientOrderFragment();
        } else if (id == R.id.menu_perfil) {
            openProfileOrLogin();
            return; // El método se encarga de la navegación o reemplazo
        }

        if (selectedFragment != null) {
            replaceFragment(selectedFragment);
        }
    }

    /**
     * Reemplaza el fragmento actual en el contenedor principal.
     * @param fragment La nueva instancia del fragmento a mostrar.
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Abre la pantalla de perfil si el usuario está logueado.
     * Si es un invitado, lo redirige a la pantalla de Login.
     */
    private void openProfileOrLogin() {
        if (sessionManager.isLoggedIn()) {
            replaceFragment(new ClientPerfilFragment());
        } else {
            Toast.makeText(this, "Inicia sesión para ver tu perfil", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            // Opcional: restaurar la selección de la navbar al fragmento anterior si el usuario cancela el login
        }
    }
}
