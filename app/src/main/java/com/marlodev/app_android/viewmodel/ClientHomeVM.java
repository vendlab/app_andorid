package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.BuildConfig;

import java.util.List;

/**
 * ViewModel profesional para ClientHomeFragment.
 * - Maneja carga inicial de productos.
 * - Observa eventos WS.
 * - Expone LiveData limpios a la UI.
 */
public class ClientHomeVM extends AndroidViewModel {

    private static final String TAG = "ClientHomeVM";

    private final ProductRepository repository;
    private final GenericWebSocketManager<ProductWebSocketEvent> webSocketManager;

    private final LiveData<List<Product>> products;
    private final LiveData<String> errorMessage;

    public ClientHomeVM(@NonNull Application application) {
        super(application);

        ProductApiService apiService = ApiClient.getClient(application)
                .create(ProductApiService.class);
        String token = SessionManager.getInstance(application).getToken();

        // Configuración del WebSocket
        webSocketManager = new GenericWebSocketManager<>(
                BuildConfig.WS_URL,
                token,
                "/topic/products",
                ProductWebSocketEvent.class
        );

        // Inicializar repositorio
        repository = new ProductRepository(apiService, webSocketManager);

        // Exponer LiveData
        products = repository.products;
        errorMessage = repository.errorMessage;

        // Cargar productos inicial
        loadProducts();
    }

    /** Carga inicial de productos */
    public void loadProducts() {
        repository.loadProductsWithCallback(() -> {
            // Repository ya actualiza isLoading.
        });
    }

    /** Conectar websocket */
    public void startWebSocket() {
        repository.connectWebSocket();
    }

    /** Observar eventos WebSocket */
    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
        repository.observeWebSocketEvents(owner);
    }

    /** LiveData expuestos a UI */
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return repository.isLoading; }


    /** Cleanup profesional: cerrar WS y scheduler */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "🧹 Limpiando recursos WebSocket");
        repository.shutdown();
    }
}
