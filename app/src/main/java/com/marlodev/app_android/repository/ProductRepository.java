package com.marlodev.app_android.repository;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
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

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Product>> products = _products;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;


    // Scheduler para retry/backoff y reconexión WS
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ProductRepository(ProductApiService apiService,
                             GenericWebSocketManager<ProductWebSocketEvent> wsManager) {
        this.apiService = apiService;
        this.wsManager = wsManager;
    }

    /** Observa eventos del WebSocket */
    // ProductRepository.java

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
                // Agregar al inicio si no existe
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
                        // Reemplazamos el producto completo con los datos nuevos
                        updatedList.add(product);
                        found = true;
                    } else {
                        updatedList.add(p);
                    }
                }
                if (!found && event.getAction().equals("IMAGES_UPDATE")) {
                    // Si no estaba en la lista, agregarlo al final
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

        // Postear nueva lista para que LiveData actualice la UI
        _products.postValue(updatedList);
    }

    /** Conexión WS */
    public void connectWebSocket() {
        wsManager.connect();
    }

    /** Desconexión WS */
// Por esto:
    public void disconnectWebSocket() {
        wsManager.disconnect();
    }

    /** Asegura que el WS esté conectado */
    private void ensureWebSocketConnected() {
        if (!wsManager.isConnected()) {
            Log.d(TAG, "🔄 Reconectando WS...");
            wsManager.connect();
        }
    }

    /** Reconexión automática periódica WS */
    public void startWebSocketAutoReconnect() {
        scheduler.scheduleAtFixedRate(this::ensureWebSocketConnected, 10, 10, TimeUnit.SECONDS);
    }

    /** Retry con backoff exponencial */
    private void retryLoadProductsWithBackoff(int attempt) {
        long delay = Math.min(2000L * (long) Math.pow(2, attempt), 20000L); // hasta 20s
        Log.d(TAG, "⏱ Retry en " + delay + " ms (intento " + attempt + ")");
        scheduler.schedule(() -> loadProducts(attempt + 1), delay, TimeUnit.MILLISECONDS);
    }

    /** Carga productos */
    public void loadProducts() {
        loadProducts(1);
    }

    private void loadProducts(int attempt) {
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
                    retryLoadProductsWithBackoff(attempt);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                _isLoading.postValue(false);
                Log.e(TAG, "❌ Falló la carga: " + t.getMessage(), t);
                _errorMessage.postValue("Error de conexión, reintentando...");
                retryLoadProductsWithBackoff(attempt);
            }
        });
    }

    /** Carga con callback */
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
                _errorMessage.postValue("Error de red");
                retryLoadProductsWithBackoff(1);
                onComplete.run();
            }
        });
    }

    /** Obtener producto por ID */
    public LiveData<Product> getProductById(long id) {
        MutableLiveData<Product> liveData = new MutableLiveData<>();

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
            return liveData;
        }

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

    /** Cierra scheduler y WS para evitar leaks */
    public void shutdown() {
        scheduler.shutdownNow();
        disconnectWebSocket();
    }

}
