package com.marlodev.app_android.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.ProductWebSocketManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositorio encargado de gestionar productos.
 * Combina datos iniciales desde REST con actualizaciones en tiempo real vía WebSocket.
 */
public class ProductRepository {

    // Servicio API REST
    private final ProductApiService apiService;

    // WebSocket para recibir actualizaciones en tiempo real
    private final ProductWebSocketManager webSocketManager;

    // LiveData que mantiene la lista de productos para la UI
    private final MutableLiveData<ArrayList<Product>> productsLiveData = new MutableLiveData<>(new ArrayList<>());

    /**
     * Constructor del repositorio.
     * Inicializa la API REST y la conexión WebSocket.
     * Además, se suscribe a los eventos de WebSocket para actualizar LiveData.
     */
    public ProductRepository() {
        apiService = ApiClient.getRetrofitInstance().create(ProductApiService.class);
        webSocketManager = new ProductWebSocketManager();
        webSocketManager.connect();

        // Suscribirse a los eventos del WebSocket y manejarlos
        webSocketManager.productEventLiveData.observeForever(this::handleWebSocketEvent);
    }

    // --------------------------
    // 🔹 Manejo de eventos WebSocket
    // --------------------------
    private void handleWebSocketEvent(ProductWebSocketEvent event) {
        ArrayList<Product> currentList = productsLiveData.getValue();
        if (currentList == null) currentList = new ArrayList<>();

        switch (event.action) {

            case "CREATE":
                // Nuevo producto agregado, se inserta al inicio de la lista
                Product created = Product.fromWebSocketEvent(event);
                currentList.add(0, created);
                break;

            case "UPDATE":
                // Actualización de un producto existente
                for (int i = 0; i < currentList.size(); i++) {
                    if (Objects.equals(currentList.get(i).getId(), event.id)) {
                        Product updated = Product.fromWebSocketEvent(event);

                        // Evitar que imageUrls sea null
                        if (updated.getImageUrls() == null) {
                            updated.setImageUrls(Collections.emptyList());
                        }

                        currentList.set(i, updated);
                        break;
                    }
                }
                break;

            case "DELETE":
                // Eliminar producto de la lista si coincide el ID
                currentList.removeIf(p -> p.getId() == event.id);
                break;

            case "IMAGES_UPDATE":
                // Actualizar solo las imágenes del producto
                for (Product p : currentList) {
                    if (p.getId() == event.id) {
                        if (event.imageUrl != null && !event.imageUrl.trim().isEmpty()) {
                            p.setImageUrls(List.of(event.imageUrl));
                        } else {
                            p.setImageUrls(Collections.emptyList()); // ← sin imágenes
                        }
                        break;
                    }
                }
                break;
        }

        // Postear la lista actualizada a LiveData
        productsLiveData.postValue(currentList);
    }

    // --------------------------
    // 🔹 Carga inicial desde REST
    // --------------------------
    /**
     * Devuelve LiveData observable con los productos.
     * También dispara la carga inicial desde la API REST.
     */
    public LiveData<ArrayList<Product>> getProducts() {
        loadProducts(); // Carga inicial desde REST
        return productsLiveData;
    }

    /**
     * Llama al endpoint REST para obtener los productos.
     */
    private void loadProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualiza LiveData con los productos obtenidos
                    productsLiveData.postValue(new ArrayList<>(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Manejo de errores de red
                t.printStackTrace();
            }
        });
    }

    /**
     * Desconecta el WebSocket para liberar recursos.
     */
    public void disconnect() {
        webSocketManager.disconnect();
    }
}
