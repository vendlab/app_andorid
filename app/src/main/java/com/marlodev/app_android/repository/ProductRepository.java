package com.marlodev.app_android.repository;

import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.ProductWebSocketManager;
import com.marlodev.app_android.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ProductRepository (Enterprise-Level)
 * -------------------------------------------------
 * 🔹 Patrón: Repository (MVVM)
 * 🔹 Responsabilidad: Acceso a datos remotos (API + WebSocket)
 * 🔹 Seguridad: JWT Token gestionado por AuthInterceptor + SessionManager
 * 🔹 Comunicación: LiveData reactivos observados desde ViewModel
 * 🔹 Resiliencia: Manejo de errores y autenticación
 * -------------------------------------------------
 */
public class ProductRepository {

    private final ProductApiService apiService;
    private ProductWebSocketManager webSocketManager;

    private final MutableLiveData<ArrayList<Product>> _products = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> _authError = new MutableLiveData<>(false);
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();

    public ProductRepository(@NonNull Context context) {
        // SessionManager Singleton
        SessionManager sessionManager = SessionManager.getInstance(context);

        // Retrofit configurado con AuthInterceptor (añade el JWT automáticamente)
        this.apiService = ApiClient.getClient(context).create(ProductApiService.class);

        // Configuración de WebSocket solo si hay token válido
//        String token = sessionManager.getAuthToken();
//        if (token != null && !token.isEmpty()) {
//            webSocketManager = new ProductWebSocketManager(token);
//            webSocketManager.connect();
//            webSocketManager.getProductEventLiveData().observeForever(this::onWebSocketEvent);
//        }
    }

    // LiveData públicos (inmutables)
    public LiveData<ArrayList<Product>> getProducts() {
        return _products;
    }

    public LiveData<Boolean> getAuthError() {
        return _authError;
    }

    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    /**
     * Carga inicial o manual de productos desde la API REST.
     */
    public void loadProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _products.postValue(new ArrayList<>(response.body()));
                } else if (response.code() == 401 || response.code() == 403) {
                    _authError.postValue(true);
                } else {
                    _errorMessage.postValue("Error al cargar productos (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {
                _errorMessage.postValue("Error de red: " + t.getLocalizedMessage());
            }
        });
    }

    /**
     * Maneja eventos recibidos en tiempo real vía WebSocket.
     */
    @MainThread
    private void onWebSocketEvent(ProductWebSocketEvent event) {
        ArrayList<Product> current = _products.getValue();
        if (current == null) current = new ArrayList<>();

        switch (event.action != null ? event.action : "") {
            case "CREATE":
                current.add(0, Product.fromWebSocketEvent(event));
                break;

            case "UPDATE":
                for (int i = 0; i < current.size(); i++) {
                    if (Objects.equals(current.get(i).getId(), event.id)) {
                        Product updated = Product.fromWebSocketEvent(event);
                        if (updated.getImageUrls() == null) {
                            updated.setImageUrls(Collections.emptyList());
                        }
                        current.set(i, updated);
                        break;
                    }
                }
                break;

            case "DELETE":
                current.removeIf(p -> Objects.equals(p.getId(), event.id));
                break;

            case "IMAGES_UPDATE":
                if (event.imageUrl != null && !event.imageUrl.trim().isEmpty()) {
                    for (Product p : current) {
                        if (Objects.equals(p.getId(), event.id)) {
                            p.setImageUrls(Collections.singletonList(event.imageUrl));
                            break;
                        }
                    }
                }
                break;
        }

        _products.postValue(current);
    }

    /**
     * Cierra conexiones y observadores para evitar fugas de memoria.
     */
    public void clear() {
        if (webSocketManager != null) {
            webSocketManager.disconnect();
            webSocketManager.getProductEventLiveData().removeObserver(this::onWebSocketEvent);
        }
    }
}
