package com.marlodev.app_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.ui.admin.AdminDashboardActivity;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.ui.client.CleintOrderFragment;
import com.marlodev.app_android.ui.client.ClientCarFragment;
import com.marlodev.app_android.ui.client.ClientHomeFragment;
import com.marlodev.app_android.ui.client.ClientPerfilFragment;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.viewmodel.ProductViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private ProductViewModel productViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);


        // Cambiar el color de la barra de estado aquí
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen1));


        // Resto de tu inicialización
        setupWebSocket();
        checkSessionAndRedirect();
        setupBottomNavigation();
    }

    private void setupWebSocket() {
        Log.i("MainActivity", "🔌 Iniciando conexión WebSocket...");
        String wsUrl = "ws://10.0.2.2:5050/ws-products/websocket";
        String token = sessionManager.getToken();
        productViewModel.initWebSocket(wsUrl, token);

        productViewModel.getProductEventLiveData().observe(this, event -> {
            if (event != null) {
                productViewModel.handleWebSocketEvent(event);
                Log.d("MainActivity", "📨 Evento recibido: " + event.getAction());
            }
        });
    }

    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = sessionManager.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
        }
    }


    private void setupBottomNavigation() {
        ChipNavigationBar chipNavigationBar = binding.bottomNavigation;
        chipNavigationBar.setBackgroundColor(getResources().getColor(R.color.colorGrey100));

        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ClientHomeFragment())
                    .commit();
            chipNavigationBar.setItemSelected(R.id.menu_home, true);
        }

        chipNavigationBar.setOnItemSelectedListener(id -> {
            Fragment fragment = null;

            if (id == R.id.menu_home) fragment = new ClientHomeFragment();
            else if (id == R.id.menu_car) fragment = new ClientCarFragment();
            else if (id == R.id.menu_delivery) fragment = new CleintOrderFragment();
            else if (id == R.id.menu_perfil) {
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

    /**
     * Abre el perfil del usuario como fragment o el login si no está logueado
     */
    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            Fragment fragment;

            if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
                // Para Admin seguimos abriendo Activity
                startActivity(new Intent(this, AdminDashboardActivity.class));
                return;
            } else {
                // Para cliente, cargamos el Fragment de perfil
                fragment = new ClientPerfilFragment();
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

        } else {
            Toast.makeText(this, "Estás en modo invitado. Inicia sesión para acceder al perfil.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productViewModel.disconnectWebSocket();
    }
}

