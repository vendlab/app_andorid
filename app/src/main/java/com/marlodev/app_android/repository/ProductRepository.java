package com.marlodev.app_android.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.ProductWebSocketManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio para gestionar los datos de los productos.
 *
 * Esta clase es la única fuente de verdad para los datos de los productos. Se encarga de obtener los datos
 * de la API REST y de mantenerlos actualizados en tiempo real a través de un WebSocket.
 */
public class ProductRepository {

    private static final String TAG = "ProductRepository";

    private final ProductApiService productApiService;
    private final ProductWebSocketManager webSocketManager;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>();
    public final LiveData<List<Product>> products = _products;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoading;

    /**
     * Constructor.
     *
     * @param productApiService El servicio para obtener datos de la API REST.
     * @param webSocketManager  El gestor para la comunicación por WebSocket.
     */
    public ProductRepository(ProductApiService productApiService, ProductWebSocketManager webSocketManager) {
        this.productApiService = productApiService;
        this.webSocketManager = webSocketManager;
        _products.setValue(new ArrayList<>());
        observeWebSocketEvents();
    }

    /**
     * Carga la lista inicial de productos desde la API.
     */
    public void loadProducts() {
        _isLoading.setValue(true);
        productApiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body());
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e(TAG, "Error al cargar los productos", t);
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * Comienza a observar los eventos del WebSocket para actualizar la lista de productos.
     */
    private void observeWebSocketEvents() {
        webSocketManager.getProductEventLiveData().observeForever(this::handleWebSocketEvent);
    }

    /**
     * Procesa un evento WebSocket y actualiza la lista de productos local.
     *
     * @param event El evento recibido.
     */
    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        if (event == null || event.getAction() == null) return;

        List<Product> currentList = new ArrayList<>(Objects.requireNonNull(_products.getValue()));

        switch (event.getAction()) {
            case "CREATE":
                Product newProduct = Product.fromWebSocketEvent(event);
                currentList.add(newProduct);
                Log.d(TAG, "Producto CREADO: " + newProduct.getName());
                break;

            case "UPDATE":
                Product updatedProduct = Product.fromWebSocketEvent(event);
                currentList.removeIf(p -> p.getId().equals(updatedProduct.getId()));
                currentList.add(updatedProduct);
                Log.d(TAG, "Producto ACTUALIZADO: " + updatedProduct.getName());
                break;

            case "DELETE":
                currentList.removeIf(p -> p.getId().equals(event.getId()));
                Log.d(TAG, "Producto ELIMINADO: " + event.getId());
                break;
        }

        _products.postValue(currentList);
    }

    /**
     * Inicia la conexión del WebSocket.
     */
    public void connectWebSocket() {
        webSocketManager.connect();
    }

    /**
     * Cierra la conexión del WebSocket y libera los recursos.
     */
    public void disconnectWebSocket() {
        webSocketManager.disconnect();
    }
}
