package com.marlodev.app_android.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.marlodev.app_android.repository.BannerRepository;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.repository.TagRepository;

/**
 * Factory profesional para crear instancias de ClientHomeVM.
 * - Proporciona las dependencias (repositorios) necesarias.
 * - Desacopla la creación del ViewModel de la UI (Fragment/Activity).
 */
public class ClientHomeVMFactory implements ViewModelProvider.Factory {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final BannerRepository bannerRepository;

    public ClientHomeVMFactory(
        ProductRepository productRepository,
        TagRepository tagRepository,
        BannerRepository bannerRepository
    ) {
        this.productRepository = productRepository;
        this.tagRepository = tagRepository;
        this.bannerRepository = bannerRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ClientHomeVM.class)) {
            // Si la clase ViewModel solicitada es ClientHomeVM, crea una instancia con los repositorios.
            return (T) new ClientHomeVM(productRepository, tagRepository, bannerRepository);
        }
        // Si no es la clase esperada, lanza una excepción.
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
