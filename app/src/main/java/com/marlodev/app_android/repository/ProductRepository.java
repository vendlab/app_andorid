package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio profesional para productos.
 * Gestiona API REST y WebSocket, mantiene LiveData sincronizado.
 */
public class ProductRepository {

    private static final String TAG = "ProductRepository";

    private final ProductApiService apiService;
    private final GenericWebSocketManager<ProductWebSocketEvent> wsManager;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Product>> products = _products;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private androidx.lifecycle.Observer<ProductWebSocketEvent> wsObserver;

    public ProductRepository(ProductApiService apiService,
                             GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;
        observeWebSocketEvents();
    }

    /** Observa eventos del WebSocket */
    private void observeWebSocketEvents() {
        wsObserver = this::handleWebSocketEvent;
        wsManager.getEventLiveData().observeForever(wsObserver);
    }

    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = new ArrayList<>(Objects.requireNonNull(_products.getValue()));
        Product product = Product.fromWebSocketEvent(event);

        if (product.getId() == null) return; // ignorar productos sin ID

        switch (event.getAction()) {
            case "CREATE":
                // 🔹 Solo agregar si no existe ya en la lista
                boolean exists = currentList.stream()
                        .anyMatch(p -> p.getId().equals(product.getId()));
                if (!exists) {
                    currentList.add(product);
                    Log.d(TAG, "🟢 Producto CREADO: " + product.getName());
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                // 🔹 Reemplaza el producto existente, si no existe lo agrega
                boolean replaced = false;
                for (int i = 0; i < currentList.size(); i++) {
                    if (currentList.get(i).getId().equals(product.getId())) {
                        currentList.set(i, product); // reemplaza en la misma posición
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    currentList.add(product); // si no estaba, lo agrega al final
                }
                Log.d(TAG, "🟡 Producto ACTUALIZADO: " + product.getName());
                break;

            case "DELETE":
                // 🔹 Elimina el producto por ID
                currentList.removeIf(p -> p.getId().equals(product.getId()));
                Log.d(TAG, "🔴 Producto ELIMINADO: " + product.getId());
                break;

            default:
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
        }

        // 🔹 Actualiza el LiveData para que el adapter se refresque automáticamente
        _products.postValue(currentList);
    }

    /** Conexión WS con token */
    public void connectWebSocket() {
        wsManager.connect();
    }

    /** Desconecta WS */
    public void disconnectWebSocket() {
        if (wsObserver != null) {
            wsManager.getEventLiveData().removeObserver(wsObserver);
            wsObserver = null;
        }
        wsManager.disconnect();
    }

    /** Carga inicial de productos vía API */
    public void loadProducts() {
        _isLoading.setValue(true);
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body());
                    Log.d(TAG, "✅ Productos cargados: " + response.body().size());
                } else {
                    Log.e(TAG, "⚠️ Error al cargar productos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _isLoading.setValue(false);
                Log.e(TAG, "❌ Falló la carga de productos: " + t.getMessage(), t);
            }
        });
    }

    /** Obtener producto por ID */
    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> productLiveData = new MutableLiveData<>();
        apiService.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productLiveData.postValue(response.body());
                } else {
                    productLiveData.postValue(null);
                    Log.w(TAG, "⚠️ Producto no encontrado ID: " + id);
                }
            }
            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                productLiveData.postValue(null);
                Log.e(TAG, "❌ Falló la conexión para producto ID: " + id, t);
            }
        });
        return productLiveData;
    }
}
