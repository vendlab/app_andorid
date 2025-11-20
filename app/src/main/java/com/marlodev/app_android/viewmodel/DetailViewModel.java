package com.marlodev.app_android.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.repository.ProductRepository;

/**
 * ViewModel profesional para la pantalla de detalle de un producto.
 * - Recibe el ProductRepository para desacoplar la obtención de datos.
 * - Expone LiveData para que la UI observe el producto, el estado de carga y los errores.
 */
public class DetailViewModel extends ViewModel {

    private final ProductRepository productRepository;

    // LiveData privados
    private final MediatorLiveData<Product> _product = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();

    // LiveData públicos para la UI
    public final LiveData<Product> product = _product;
    public final LiveData<Boolean> isLoading = _isLoading;
    public final LiveData<String> errorMessage = _errorMessage;

    public DetailViewModel(@NonNull ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Carga los detalles de un producto por su ID.
     * El resultado se observa a través del LiveData `product`.
     * @param productId El ID del producto a cargar.
     */
    public void loadProductById(long productId) {
        _isLoading.setValue(true);

        // El repositorio devuelve un LiveData, que observamos aquí.
        LiveData<Product> productSource = productRepository.getProductById(productId);

        // Usamos un MediatorLiveData para "escuchar" el resultado del repositorio.
        _product.addSource(productSource, productData -> {
            if (productData != null) {
                _product.setValue(productData);
                _isLoading.setValue(false);
            } else {
                _errorMessage.setValue("No se pudo cargar el producto.");
                _isLoading.setValue(false);
            }
            // Dejamos de observar la fuente para no tener fugas de memoria.
            _product.removeSource(productSource);
        });
    }
}
