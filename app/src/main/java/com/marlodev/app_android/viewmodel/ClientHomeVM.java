
package com.marlodev.app_android.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.marlodev.app_android.domain.Banner;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.model.BannerWebSocketEvent;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.BannerApiService;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.TagApiService;
import com.marlodev.app_android.repository.BannerRepository;
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
    private final BannerRepository bannerRepository;

    private final LiveData<List<Product>> products;
    private final LiveData<List<Tag>> tags;
    private final LiveData<List<Banner>> banners;
    private final LiveData<String> productErrorMessage;
    private final LiveData<String> tagErrorMessage;
    private final LiveData<String> bannerErrorMessage;
    private final LiveData<Boolean> isLoadingProducts;
    private final LiveData<Boolean> isLoadingTags;
    private final LiveData<Boolean> isLoadingBanners;

    public ClientHomeVM(@NonNull Application application) {
        super(application);

        String token = SessionManager.getInstance(application).getToken();

        // Inicializar servicios
        ProductApiService productApi = ApiClient.getClient(application).create(ProductApiService.class);
        TagApiService tagApi = ApiClient.getClient(application).create(TagApiService.class);
        BannerApiService bannerApi = ApiClient.getClient(application).create(BannerApiService.class);

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

        bannerRepository = new BannerRepository(bannerApi,
                new com.marlodev.app_android.network.GenericWebSocketManager<>(
                        BuildConfig.WS_URL,
                        token,
                        "/topic/banners",
                        BannerWebSocketEvent.class
                )
        );

        // LiveData expuestos
        products = productRepository.products;
        tags = tagRepository.tags;
        banners = bannerRepository.banners;

        productErrorMessage = productRepository.errorMessage;
        tagErrorMessage = tagRepository.errorMessage;
        bannerErrorMessage = bannerRepository.errorMessage;

        isLoadingProducts = productRepository.isLoading;
        isLoadingTags = tagRepository.isLoading;
        isLoadingBanners = bannerRepository.isLoading;

        // Cargar datos iniciales
        loadProducts();
        loadTags();
        loadBanners();
    }

    /** Cargar productos inicial */
    public void loadProducts() {
        productRepository.loadProducts();
    }

    /** Cargar tags inicial */
    public void loadTags() {
        tagRepository.loadTags();
    }
    /** Cargar Banners inicial */
    public void loadBanners() {
        bannerRepository.loadBanners();
    }

    /** Conectar WebSocket de productos */
    /** Conectar WebSocket de Banners */
    public void startWebSocket() {
        productRepository.connectWebSocket();
        bannerRepository.connectWebSocket();
    }


    /** Observar eventos WebSocket de productos */
    public void observeWebSocketEvents(@NonNull LifecycleOwner owner) {
        productRepository.observeWebSocketEvents(owner);
        bannerRepository.observeWebSocketEvents(owner);
    }

    /** Getters LiveData para UI */
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Tag>> getTags() { return tags; }
    public LiveData<List<Banner>> getBanners() { return banners; }

    public LiveData<String> getProductErrorMessage() { return productErrorMessage; }
    public LiveData<String> getTagErrorMessage() { return tagErrorMessage; }
    public LiveData<String> getBannerErrorMessage() { return bannerErrorMessage; }

    public LiveData<Boolean> getIsLoadingProducts() { return isLoadingProducts; }
    public LiveData<Boolean> getIsLoadingTags() { return isLoadingTags; }
    public LiveData<Boolean> getIsLoadingBanners() { return isLoadingBanners; }

    /** Limpiar recursos */
    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG_LOG, "🧹 Limpiando recursos WebSocket y repositorios");
        productRepository.shutdown();
        bannerRepository.shutdown();
        // tagRepository no tiene WS
    }
}
