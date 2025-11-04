package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.ui.admin.AdminMainActivity;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.ui.client.ClientCarFragment;
import com.marlodev.app_android.ui.client.ClientHomeFragment;
import com.marlodev.app_android.ui.client.ClientOrderFragment;
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

        // Solo observamos LiveData del ViewModel
        observeProducts();

        checkSessionAndRedirect();
        setupBottomNavigation();
    }

    private void observeProducts() {
        // Aquí puedes observar cambios en la lista de productos (ej. actualizar RecyclerView)
        productViewModel.getProducts().observe(this, products -> {
            // TODO: Actualizar UI con la lista de productos
        });
    }

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
            else if (id == R.id.menu_delivery) fragment = new ClientOrderFragment();
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

    private void openProfileOrGuest() {
        if (sessionManager.isLoggedIn()) {
            if ("ADMIN".equalsIgnoreCase(sessionManager.getRole())) {
                startActivity(new Intent(this, AdminMainActivity.class));
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ClientPerfilFragment())
                        .commit();
            }
        } else {
            Toast.makeText(this, "Estás en modo invitado. Inicia sesión para acceder al perfil.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Delegamos desconexión al ViewModel
        productViewModel.disconnectWebSocket();
    }
}
