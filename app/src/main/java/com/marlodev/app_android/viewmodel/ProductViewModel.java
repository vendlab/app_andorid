
package com.marlodev.app_android.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.repository.ProductRepository;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository = new ProductRepository();

    public LiveData<ArrayList<Product>> getProducts() {
        return repository.getProducts();
    }

    // ✅ Nuevo método para obtener un producto por ID
    public LiveData<Product> getProductById(long productId) {
        return Transformations.map(repository.getProducts(), products -> {
            if (products != null) {
                for (Product p : products) {
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
        repository.disconnect();
    }
}
