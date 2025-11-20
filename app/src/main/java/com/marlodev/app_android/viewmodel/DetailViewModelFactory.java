package com.marlodev.app_android.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.repository.ProductRepository;

/**
 * Factory profesional para crear instancias de DetailViewModel.
 * - Proporciona la dependencia (ProductRepository) necesaria.
 * - Desacopla la creación del ViewModel de la UI (Activity).
 */
public class DetailViewModelFactory implements ViewModelProvider.Factory {

    private final ProductRepository productRepository;

    public DetailViewModelFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailViewModel.class)) {
            return (T) new DetailViewModel(productRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
