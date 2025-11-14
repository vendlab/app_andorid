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


    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

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
                boolean exists = currentList.stream().anyMatch(p -> p.getId().equals(product.getId()));
                if (!exists) {
                    currentList.add(product);
                    Log.d(TAG, "🟢 Producto CREADO: " + product.getName());
                }
                break;

            case "UPDATE":
            case "IMAGES_UPDATE":
                boolean replaced = false;
                for (int i = 0; i < currentList.size(); i++) {
                    if (currentList.get(i).getId().equals(product.getId())) {
                        currentList.set(i, product);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    currentList.add(product);
                }
                Log.d(TAG, "🟡 Producto ACTUALIZADO: " + product.getName());
                break;

            case "DELETE":
                currentList.removeIf(p -> p.getId().equals(product.getId()));
                Log.d(TAG, "🔴 Producto ELIMINADO: " + product.getId());
                break;

            default:
                Log.w(TAG, "⚪ Acción desconocida: " + event.getAction());
        }

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
    private void retryLoadProductsWithDelay(int attempt) {
        int delay = Math.min(5000 * attempt, 20000); // hasta 20 seg máximo
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::loadProducts, delay);
    }

    public void loadProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _products.postValue(response.body());
                    Log.d(TAG, "✅ Productos cargados: " + response.body().size());
                } else {
                    _errorMessage.postValue("Error al cargar los productos (" + response.code() + ")");
                    Log.e(TAG, "⚠️ Error al cargar productos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "❌ Falló la carga de productos: " + t.getMessage(), t);
                _errorMessage.postValue("No se pudo conectar al servidor, reintentando...");
                retryLoadProductsWithDelay(1);
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
                    _errorMessage.postValue("Error al cargar los productos (" + response.code() + ")");
                }
                onComplete.run();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _errorMessage.postValue("Error de red");
                retryLoadProductsWithDelay(1);
                onComplete.run();
            }
        });
    }

    /** Obtener producto por ID */

    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();

        // 1️⃣ Buscar localmente
        List<Product> list = _products.getValue();
        Product local = null;

        if (list != null) {
            local = list.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);
        }

        if (local != null) {
            liveData.postValue(local);
            return liveData; // ⬅ No hace falta llamar a la API
        }

        // 2️⃣ Solo si NO hay producto local, ir a la API
        apiService.getProductById(id).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                liveData.postValue(null);
            }
        });

        return liveData;
    }

}

