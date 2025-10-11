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

public class ProductRepository {

    private final ProductApiService apiService;
    private final ProductWebSocketManager webSocketManager;
    private final MutableLiveData<ArrayList<Product>> productsLiveData = new MutableLiveData<>(new ArrayList<>());

    public ProductRepository() {
        apiService = ApiClient.getRetrofitInstance().create(ProductApiService.class);
        webSocketManager = new ProductWebSocketManager();
        webSocketManager.connect();

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
                Product created = Product.fromWebSocketEvent(event);
                currentList.add(0, created);
                break;


            case "UPDATE":
                for (int i = 0; i < currentList.size(); i++) {
                    if (Objects.equals(currentList.get(i).getId(), event.id)) {
                        Product updated = Product.fromWebSocketEvent(event);

                        // ✅ Evita que imageUrls sea null
                        if (updated.getImageUrls() == null) {
                            updated.setImageUrls(Collections.emptyList());
                        }

                        currentList.set(i, updated);
                        break;
                    }
                }
                break;






            case "DELETE":
                currentList.removeIf(p -> p.getId() == event.id);
                break;



            case "IMAGES_UPDATE":
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

        productsLiveData.postValue(currentList);
    }

    // --------------------------
    // 🔹 Carga inicial desde REST
    // --------------------------
    public LiveData<ArrayList<Product>> getProducts() {
        loadProducts();
        return productsLiveData;
    }

    private void loadProducts() {
        apiService.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productsLiveData.postValue(new ArrayList<>(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void disconnect() {
        webSocketManager.disconnect();
    }
}

