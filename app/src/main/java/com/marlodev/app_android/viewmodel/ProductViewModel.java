package com.marlodev.app_android.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.repository.ProductRepository;

import java.util.ArrayList;

/**
 * ProductViewModel (Enterprise-Level)
 * --------------------------------------------
 * 🔹 Patrón: MVVM
 * 🔹 Responsabilidad: Lógica de presentación + puente entre UI y Repository
 * 🔹 Ciclo de vida: se limpia correctamente al destruirse
 * 🔹 Seguridad: obtiene datos autenticados mediante JWT
 * --------------------------------------------
 */
public class ProductViewModel extends AndroidViewModel {

    private final ProductRepository repository;

    // LiveData expuestos a la UI
    private final LiveData<Boolean> onAuthError;
    private final LiveData<ArrayList<Product>> products;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        repository = new ProductRepository(application.getApplicationContext());
        onAuthError = repository.getAuthError();
        products = repository.getProducts();
    }

    public LiveData<ArrayList<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getOnAuthError() {
        return onAuthError;
    }

    /**
     * Devuelve un producto específico por ID dentro del LiveData.
     */
    public LiveData<Product> getProductById(long productId) {
        return Transformations.map(products, list -> {
            if (list != null) {
                for (Product p : list) {
                    if (p.getId() == productId) {
                        return p;
                    }
                }
            }
            return null;
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clear(); // Libera conexiones WebSocket y observadores
    }
}
