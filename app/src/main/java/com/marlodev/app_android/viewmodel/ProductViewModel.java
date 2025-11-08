package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.utils.SessionManager;

import java.util.List;

/**
 * ViewModel profesional para manejar productos con soporte de API y WebSocket.
 * Incluye:
 * - Carga inicial de productos vía REST.
 * - Escucha de actualizaciones en tiempo real por WebSocket.
 * - Manejo de errores y estados de carga.
 * - Observación de estado de conexión WebSocket.
 */
public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository repository;
    private final LiveData<List<Product>> products;
    private final LiveData<Boolean> isLoading;
    private final LiveData<String> errorMessage;

    private final GenericWebSocketManager<ProductWebSocketEvent> webSocketManager;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        // 🔹 Inicializar dependencias base
        ProductApiService apiService = ApiClient.getClient(application)
                .create(ProductApiService.class);
        SessionManager session = SessionManager.getInstance(application);
        String token = session.getToken();

        // 🔹 Configuración del WebSocket (usa ws:// local o wss:// para producción)
        String wsUrl = "ws://10.0.2.2:8080/ws-products";
//        String wsUrl = "wss://ecommerce-backend-o9y5.onrender.com/ws-products";

        // 🔹 Crear WebSocket manager
        webSocketManager = new GenericWebSocketManager<>(
                wsUrl,
                token,
                "/topic/products",
                ProductWebSocketEvent.class
        );

        // 🔹 Crear repositorio
        repository = new ProductRepository(apiService, webSocketManager);

        // 🔹 Exponer LiveData
        products = repository.products;
        isLoading = repository.isLoading;
        errorMessage = repository.errorMessage;

        // 🔹 Cargar datos iniciales
        repository.loadProducts();

        // 🔹 Conectar WebSocket
        repository.connectWebSocket();

        // 🔹 Monitorear estado de conexión WebSocket
        observeWebSocketState();
    }

    private void observeWebSocketState() {
        webSocketManager.getConnectionState().observeForever(state -> {
            switch (state) {
                case CONNECTED:
                    Log.d("ProductViewModel", "🟢 WS conectado");
                    break;
                case CONNECTING:
                    Log.d("ProductViewModel", "🕓 Intentando conectar...");
                    break;
                case DISCONNECTED:
                    Log.d("ProductViewModel", "🔴 Desconectado, intentando reconectar...");
                    webSocketManager.reconnectWithDelay(5000);
                    break;
                case ERROR:
                    Log.d("ProductViewModel", "⚠️ Error de conexión WS");
                    break;
            }
        });
    }

    // 🔹 Getters públicos para LiveData observables
    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Product> getProductById(long id) {
        return repository.getProductById(id);
    }

    // 🔹 Limpieza final al destruir el ViewModel
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("ProductViewModel", "🧹 Cerrando WebSocket al limpiar el ViewModel");
        repository.disconnectWebSocket();
    }
}
