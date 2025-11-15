package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.utils.SessionManager;

import java.util.List;

/**
 * ViewModel profesional para manejar productos.
 *
 * Funcionalidades:
 * 1️⃣ Carga inicial de productos vía REST.
 * 2️⃣ Escucha de actualizaciones en tiempo real vía WebSocket.
 * 3️⃣ Manejo de errores y estados de carga.
 * 4️⃣ Observación de estado de conexión WebSocket.
 */
public class ClientHomeVM extends AndroidViewModel {

    // 🔹 Repositorio de productos
    private final ProductRepository repository;

    // 🔹Loading
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    // 🔹 Datos LiveData expuestos a la UI
    private final LiveData<List<Product>> products;
    private final LiveData<String> errorMessage;

    // 🔹 Manager de WebSocket para actualizaciones en tiempo real
    private final GenericWebSocketManager<ProductWebSocketEvent> webSocketManager;

    public ClientHomeVM(@NonNull Application application) {
        super(application);
        // -----------------------------
        // Inicializar servicios y token
        // -----------------------------
        ProductApiService apiService = ApiClient.getClient(application)
                .create(ProductApiService.class);
        SessionManager session = SessionManager.getInstance(application);
        String token = session.getToken();

        // -----------------------------
        // Configurar WebSocket
        // -----------------------------
//        String wsUrl = "ws://10.0.2.2:8080/ws-products";
        String wsUrl = "wss://ecommerce-backend-o9y5.onrender.com/ws-products";

        // 🔹 Crear WebSocket manager
        webSocketManager = new GenericWebSocketManager<>(
                wsUrl,
                token,
                "/topic/products",
                ProductWebSocketEvent.class
        );

        // -----------------------------
        // Crear repositorio
        // -----------------------------
        repository = new ProductRepository(apiService, webSocketManager);

        // -----------------------------
        // Exponer LiveData de repositorio
        // -----------------------------
        products = repository.products;
        errorMessage = repository.errorMessage;


        // ---- Cargar productos
        loadProducts();

        // -----------------------------
        // Conectar WebSocket para updates en tiempo real
        // -----------------------------
        repository.connectWebSocket();

        // -----------------------------
        // Monitorear estado del WebSocket
        // -----------------------------
        observeWebSocketState();
    }

    // ❤️ Aquí se usa el loading



    public void loadProducts() {
        _isLoading.setValue(true);

        repository.loadProductsWithCallback(() -> {
            _isLoading.postValue(false);
        });
    }



    // 🔹 Observa estado de conexión WebSocket y reconecta si es necesario
    private void observeWebSocketState() {
        webSocketManager.getConnectionState().observeForever(state -> {
            switch (state) {
                case CONNECTED:
                    Log.d("ClientHomeVM", "🟢 WS conectado");
                    break;
                case CONNECTING:
                    Log.d("ClientHomeVM", "🕓 Intentando conectar...");
                    break;
                case DISCONNECTED:
                    Log.d("ClientHomeVM", "🔴 Desconectado, reconectando...");
                    webSocketManager.reconnectWithDelay(5000); // reconexión automática
                    break;
                case ERROR:
                    Log.d("ClientHomeVM", "⚠️ Error de conexión WS");
                    break;
            }
        });
    }

    // -----------------------------
    // Getters públicos para la UI
    // -----------------------------

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    // 🔹 Limpiar recursos al destruir el ViewModel
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("ClientHomeVM", "🧹 Cerrando WebSocket al limpiar el ViewModel");
        repository.disconnectWebSocket();
    }
}
