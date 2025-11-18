//package com.marlodev.app_android.viewmodel;
//
//import android.app.Application;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LiveData;
//
//import com.marlodev.app_android.domain.Product;
//import com.marlodev.app_android.domain.Tag;
//import com.marlodev.app_android.model.ProductWebSocketEvent;
//import com.marlodev.app_android.network.ApiClient;
//import com.marlodev.app_android.network.GenericWebSocketManager;
//import com.marlodev.app_android.network.ProductApiService;
//import com.marlodev.app_android.network.TagApiService;
//import com.marlodev.app_android.repository.ProductRepository;
//import com.marlodev.app_android.repository.TagRepository;
//import com.marlodev.app_android.utils.SessionManager;
//import com.marlodev.app_android.BuildConfig;
//
//import java.util.List;
//
///**
// * ViewModel profesional para ClientHomeFragment.
// * - Maneja carga inicial de productos.
// * - Observa eventos WS.
// * - Expone LiveData limpios a la UI.
// */
//public class ClientHomeVM extends AndroidViewModel {
//
//    private static final String TAG = "ClientHomeVM";
//
//    private final ProductRepository repository;
//    private final GenericWebSocketManager<ProductWebSocketEvent> webSocketManager;
//    private final TagRepository tagRepository;
//    private final LiveData<List<Tag>> tags;
//    private final LiveData<List<Product>> products;
//    private final LiveData<String> errorMessage;
//
//    public ClientHomeVM(@NonNull Application application) {
//        super(application);
//
//        String token = SessionManager.getInstance(application).getToken();
//
//        ProductApiService apiService = ApiClient.getClient(application)
//                .create(ProductApiService.class);
//
//
//
//        // Configuración del WebSocket
//        webSocketManager = new GenericWebSocketManager<>(
//                BuildConfig.WS_URL,
//                token,
//                "/topic/products",
//                ProductWebSocketEvent.class
//        );
//
//        // Inicializar repositorio
//        repository = new ProductRepository(apiService, webSocketManager);
//        tagRepository = new TagRepository(ApiClient.getClient(application).create(TagApiService.class));
//
//        // Exponer LiveData
//        products = repository.products;
//        tags = tagRepository.tags;
//        errorMessage = repository.errorMessage;
//
//        // Cargar productos inicial
//        loadProducts();
//        tagRepository.loadTags();
//    }
//
//    /** Carga inicial de productos */
//    public void loadProducts() {
//        repository.loadProducts();
//    }
//
//    /** Conectar websocket */
//    public void startWebSocket() {
//        repository.connectWebSocket();
//    }
//
//    /** Observar eventos WebSocket */
//    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
//        repository.observeWebSocketEvents(owner);
//    }
//
//    /** LiveData expuestos a UI */
//    public LiveData<List<Product>> getProducts() { return products; }
//    public LiveData<List<Tag>> getTags() { return tags; }
//    public LiveData<String> getErrorMessage() { return errorMessage; }
//    public LiveData<Boolean> getIsLoading() { return repository.isLoading; }
//
//
//    /** Cleanup profesional: cerrar WS y scheduler */
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        Log.d(TAG, "🧹 Limpiando recursos WebSocket");
//        repository.shutdown();
//    }
//}


package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.TagApiService;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.repository.TagRepository;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.BuildConfig;

import java.util.List;

/**
 * ViewModel profesional para ClientHomeFragment.
 * - Maneja productos y tags.
 * - Expone LiveData limpios.
 * - Observa WebSocket de productos.
 */
public class ClientHomeVM extends AndroidViewModel {
    private static final String TAG_LOG = "ClientHomeVM";

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;

    private final LiveData<List<Product>> products;
    private final LiveData<List<Tag>> tags;
    private final LiveData<String> productErrorMessage;
    private final LiveData<String> tagErrorMessage;
    private final LiveData<Boolean> isLoadingProducts;
    private final LiveData<Boolean> isLoadingTags;

    public ClientHomeVM(@NonNull Application application) {
        super(application);

        String token = SessionManager.getInstance(application).getToken();

        // Inicializar servicios
        ProductApiService productApi = ApiClient.getClient(application).create(ProductApiService.class);
        TagApiService tagApi = ApiClient.getClient(application).create(TagApiService.class);

        // Repositorios
        productRepository = new ProductRepository(productApi,
                new com.marlodev.app_android.network.GenericWebSocketManager<>(
                        BuildConfig.WS_URL,
                        token,
                        "/topic/products",
                        ProductWebSocketEvent.class
                )
        );

        tagRepository = new TagRepository(tagApi);

        // LiveData expuestos
        products = productRepository.products;
        tags = tagRepository.tags;
        productErrorMessage = productRepository.errorMessage;
        tagErrorMessage = tagRepository.errorMessage;
        isLoadingProducts = productRepository.isLoading;
        isLoadingTags = tagRepository.isLoading;

        // Cargar datos iniciales
        loadProducts();
        loadTags();
    }

    /** Cargar productos inicial */
    public void loadProducts() {
        productRepository.loadProducts();
    }

    /** Cargar tags inicial */
    public void loadTags() {
        tagRepository.loadTags();
    }

    /** Conectar WebSocket de productos */
    public void startWebSocket() {
        productRepository.connectWebSocket();
    }

    /** Observar eventos WebSocket de productos */
    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
        productRepository.observeWebSocketEvents(owner);
    }

    /** Getters LiveData para UI */
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Tag>> getTags() { return tags; }
    public LiveData<String> getProductErrorMessage() { return productErrorMessage; }
    public LiveData<String> getTagErrorMessage() { return tagErrorMessage; }
    public LiveData<Boolean> getIsLoadingProducts() { return isLoadingProducts; }
    public LiveData<Boolean> getIsLoadingTags() { return isLoadingTags; }

    /** Limpiar recursos */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG_LOG, "🧹 Limpiando recursos WebSocket y repositorios");
        productRepository.shutdown();
        // tagRepository no tiene WS
    }
}
