package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.ui.admin.AdminDashboardActivity;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.ui.client.ClientPerfilActivity;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.viewmodel.ProductViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private PopularAdapter popularAdapter;
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = SessionManager.getInstance(this);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        setupWebSocket();
        checkSessionAndRedirect();
        setupBottomNavigation();
        initPopular();
    }

    // --------------------------
    // Inicializamos WebSocket (siempre)
    // --------------------------
    private void setupWebSocket() {
        // Se conecta siempre, incluso para invitados
        Log.i("MainActivity", "🔌 Iniciando conexión WebSocket para todos los usuarios...");

        String wsUrl = "ws://10.0.2.2:5050/ws-products/websocket";

        // Obtenemos el token. Si el usuario es invitado, 'token' será null.
        String token = sessionManager.getToken();

        // Tu ProductWebSocketManager ya maneja el caso de token nulo
        // (simplemente no añade el header de autorización).
        productViewModel.initWebSocket(wsUrl, token);

        // Observamos los eventos recibidos
        productViewModel.getProductEventLiveData().observe(this, event -> {
            if (event != null) {
                productViewModel.handleWebSocketEvent(event);
                Log.d("MainActivity", "📨 Evento recibido: " + event.getAction());
            }
        });
    }
    // --------------------------
    // Redirección según rol
    // --------------------------
    private void checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Bienvenido, estás navegando como invitado", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = sessionManager.getRole();
        if ("ADMIN".equalsIgnoreCase(role)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            finish();
        } else if ("CLIENT".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Bienvenido cliente " + sessionManager.getEmail(), Toast.LENGTH_SHORT).show();
        }
    }

    // --------------------------
    // Bottom navigation
    // --------------------------
    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(id -> {
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

    private void openProfileOrGuest() {
        Intent intent;
        if (sessionManager.isLoggedIn()) {
            intent = "ADMIN".equalsIgnoreCase(sessionManager.getRole())
                    ? new Intent(this, AdminDashboardActivity.class)
                    : new Intent(this, ClientPerfilActivity.class);
        } else {
            Toast.makeText(this, "Estás en modo invitado. Inicia sesión para acceder al perfil.", Toast.LENGTH_LONG).show();
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
    }

    // --------------------------
    // Inicializamos productos populares
    // --------------------------
    private void initPopular() {
        // Configuramos RecyclerView y Adapter
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        binding.popularView.setAdapter(popularAdapter);


        // ✅ Click listener de productos: ¡Abre DetailActivity!
        popularAdapter.setOnProductClickListener(product -> {
            Toast.makeText(this, "Seleccionaste: " + product.getName(), Toast.LENGTH_SHORT).show();
            // 1. Obtener el ID del producto (asumiendo que Product tiene getId())
            // 💡 AÑADE ESTA LÍNEA
            Log.d("DEBUG_ID", "ID de producto ENVIADO: " + product.getId());

            long productId = product.getId();

            // 2. Crear el Intent apuntando a la DetailActivity
            Intent intent = new Intent(this, com.marlodev.app_android.ui.home.customer.DetailActivity.class);

            // 3. Pasar el ID del producto (la clave debe ser "productId")
            intent.putExtra("productId", productId);

            // 4. Iniciar la Activity
            startActivity(intent);
        });

        // Observamos LiveData de productos
        productViewModel.getProducts().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                popularAdapter.setProducts(products);
            }
        });

        // Observamos LiveData de carga
        productViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBarPopular.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        // Iniciamos carga de productos
        productViewModel.loadProducts();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        productViewModel.disconnectWebSocket();
    }
}
