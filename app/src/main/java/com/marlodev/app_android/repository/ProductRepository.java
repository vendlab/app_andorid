package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional para productos.
 * Gestiona API REST y WebSocket, mantiene LiveData sincronizado.
 * No maneja reconexión WS, lo hace GenericWebSocketManager.
 */
public class ProductRepository {

    private static final String TAG = "ProductRepository";

    private final ProductApiService apiService;
    private final GenericWebSocketManager<ProductWebSocketEvent> wsManager;

    // LiveData internos
    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);

    // LiveData públicos
    public LiveData<List<Product>> products = _products;
    public LiveData<String> errorMessage = _errorMessage;
    public LiveData<Boolean> isLoading = _isLoading;

    public ProductRepository(ProductApiService apiService,
                             GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;
    }

    // -----------------------------
    // WebSocket
    // -----------------------------
    public void connectWebSocket() {
        wsManager.connect();
    }

    public void disconnectWebSocket() {
        wsManager.disconnect();
    }

    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
        wsManager.getEventLiveData().observe(owner, this::handleWebSocketEvent);
    }

    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = _products.getValue() != null ? _products.getValue() : new ArrayList<>();
        Product product = Product.fromWebSocketEvent(event);
        if (product.getId() == null) return;

        List<Product> updatedList = new ArrayList<>();
        boolean found = false;

        switch (event.getAction()) {
            case "CREATE":
                boolean exists = currentList.stream().anyMatch(p -> p.getId().equals(product.getId()));
                if (!exists) {
                    updatedList.add(product);
                    updatedList.addAll(currentList);
                    Log.d(TAG, "🟢 Producto CREADO: " + product.getName());
                } else {
                    updatedList.addAll(currentList);
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                for (Product p : currentList) {
                    if (p.getId().equals(product.getId())) {
                        updatedList.add(product);
                        found = true;
                    } else {
                        updatedList.add(p);
                    }
                }
                if (!found && event.getAction().equals("IMAGES_UPDATE")) {
                    updatedList.add(product);
                }
                Log.d(TAG, "🟡 Producto ACTUALIZADO: " + product.getName());
                break;

            case "DELETE":
                for (Product p : currentList) {
                    if (!p.getId().equals(product.getId())) {
                        updatedList.add(p);
                    }
                }
                Log.d(TAG, "🔴 Producto ELIMINADO: " + product.getId());
                break;

            default:
                updatedList.addAll(currentList);
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
        }

        _products.postValue(updatedList);
    }

    // -----------------------------
    // REST API
    // -----------------------------
    public void loadProducts() {
        _isLoading.postValue(true);
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _products.postValue(response.body());
                    Log.d(TAG, "✅ Productos cargados: " + response.body().size());
                } else {
                    _errorMessage.postValue("Error al cargar productos (" + response.code() + ")");
                    Log.e(TAG, "⚠️ Error al cargar productos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _isLoading.postValue(false);
                _errorMessage.postValue("Error de conexión: " + t.getMessage());
                Log.e(TAG, "❌ Falló la carga: " + t.getMessage(), t);
            }
        });
    }

    public void loadProductsWithCallback(Runnable onComplete) {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _products.postValue(response.body());
                } else {
                    _errorMessage.postValue("Error al cargar productos (" + response.code() + ")");
                }
                onComplete.run();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _errorMessage.postValue("Error de red: " + t.getMessage());
                onComplete.run();
            }
        });
    }

    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();
        List<Product> list = _products.getValue();
        if (list != null) {
            Product local = list.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            if (local != null) {
                liveData.postValue(local);
                return liveData;
            }
        }

        apiService.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                liveData.postValue(response.isSuccessful() ? response.body() : null);
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

    public void shutdown() {
        disconnectWebSocket();
    }
}
