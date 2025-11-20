package com.marlodev.app_android.ui.client;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.marlodev.app_android.BuildConfig;
import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.client.BannerAdapter;
import com.marlodev.app_android.adapter.client.PopularAdapter;
import com.marlodev.app_android.adapter.client.TagAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.domain.Banner;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.BannerWebSocketEvent;
import com.marlodev.app_android.model.ProductWebSocketEvent;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.BannerApiService;
import com.marlodev.app_android.network.GenericWebSocketManager;
import com.marlodev.app_android.network.ProductApiService;
import com.marlodev.app_android.network.TagApiService;
import com.marlodev.app_android.repository.BannerRepository;
import com.marlodev.app_android.repository.ProductRepository;
import com.marlodev.app_android.repository.TagRepository;
import com.marlodev.app_android.ui.home.customer.ClientDetailActivity;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.viewmodel.ClientHomeVM;
import com.marlodev.app_android.viewmodel.ClientHomeVMFactory;

import java.util.List;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ClientHomeVM clientHomeVM;
    private PopularAdapter popularAdapter;
    private TagAdapter tagAdapter;
    private BannerAdapter bannerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);

        initViewModel();
        setupAdapters();
        observeViewModel();

        clientHomeVM.startWebSocket();
        clientHomeVM.observeWebSocketEvents(getViewLifecycleOwner());

        return binding.getRoot();
    }

    private void initViewModel() {
        Context context = requireContext().getApplicationContext();
        String token = SessionManager.getInstance(context).getToken();

        ProductApiService productApi = ApiClient.getClient(context).create(ProductApiService.class);
        TagApiService tagApi = ApiClient.getClient(context).create(TagApiService.class);
        BannerApiService bannerApi = ApiClient.getClient(context).create(BannerApiService.class);

        ProductRepository productRepository = new ProductRepository(productApi,
                new GenericWebSocketManager<>(
                        BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class
                )
        );
        TagRepository tagRepository = new TagRepository(tagApi);
        BannerRepository bannerRepository = new BannerRepository(bannerApi,
                new GenericWebSocketManager<>(
                        BuildConfig.WS_URL, token, "/topic/banners", BannerWebSocketEvent.class
                )
        );

        ClientHomeVMFactory factory = new ClientHomeVMFactory(productRepository, tagRepository, bannerRepository);
        clientHomeVM = new ViewModelProvider(this, factory).get(ClientHomeVM.class);
    }


    private void setupAdapters() {
        // Productos Populares
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.popularView.setAdapter(popularAdapter);
        popularAdapter.setOnProductClickListener(this::openProductDetail);

        // Tags
        tagAdapter = new TagAdapter();
        binding.tagRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.tagRecyclerView.setAdapter(tagAdapter);

        // Banners
        bannerAdapter = new BannerAdapter(requireContext());
        binding.bannerViewPager.setAdapter(bannerAdapter);

        // --- ✅ CLONACIÓN FINAL de la lógica de animación profesional ---
        binding.bannerViewPager.setOffscreenPageLimit(3); // Imprescindible para el peek a ambos lados

        // Configuración del RecyclerView interno (clave para evitar bugs visuales)
        binding.bannerViewPager.post(() -> {
            if (binding.bannerViewPager.getChildCount() > 0) {
                View child = binding.bannerViewPager.getChildAt(0);
                if (child instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) child;
                    recyclerView.setClipToPadding(false);
                    recyclerView.setClipChildren(false);
                    recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                }
            }
        });

        CompositePageTransformer transformer = new CompositePageTransformer();

        // Margen sutil entre páginas, clonado de su lógica original
        transformer.addTransformer(new MarginPageTransformer(dpToPx(4)));

        // CLON EXACTO de la coreografía de animación que funcionaba
        transformer.addTransformer((page, position) -> {
            float absPos = Math.abs(position);

            // Escala (lógica original para que el centro sea grande)
            float scaleX = 0.85f + (1 - absPos) * 0.15f;
            float scaleY = 0.90f + (1 - absPos) * 0.10f;
            page.setScaleX(scaleX);
            page.setScaleY(scaleY);

            // Opacidad (lógica original)
            float alpha = 0.65f + (1 - absPos) * 0.35f;
            page.setAlpha(alpha);

            // Elevación 3D (lógica original para la sombra y el orden Z)
            float elevation = (1 - absPos) * 100f;
            page.setTranslationZ(elevation);

            // Elevación de la tarjeta (lógica original para la sombra de Material)
            View cardContainer = page.findViewById(R.id.cardContainer);
            if (cardContainer instanceof MaterialCardView) {
                MaterialCardView card = (MaterialCardView) cardContainer;
                float cardElevation = 6f + (1 - absPos) * 6f;
                card.setCardElevation(cardElevation);
            }
        });

        binding.bannerViewPager.setPageTransformer(transformer);
        // --- Fin de la clonación ---

        new TabLayoutMediator(binding.bannerTabLayout, binding.bannerViewPager,
                (tab, position) -> {
                    View custom = LayoutInflater.from(requireContext())
                            .inflate(R.layout.tab_custom_dot, binding.bannerTabLayout, false);
                    tab.setCustomView(custom);
                }
        ).attach();

        bannerAdapter.setOnBannerClickListener(banner -> {
            if (!banner.isSkeleton()) { // Solo reaccionar a clicks en banners reales
                Snackbar.make(binding.getRoot(), "Banner: " + banner.getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });

        // Listener para la animación de los dots del banner
        binding.bannerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    View dot = view.findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(30);
                    params.height = dpToPx(9);
                    dot.setLayoutParams(params);
                    dot.setSelected(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    View dot = view.findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(9);
                    params.height = dpToPx(9);
                    dot.setLayoutParams(params);
                    dot.setSelected(false);
                }
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeViewModel() {
        // --- Productos, Tags y Banners ---
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), popularAdapter::submitList);
        clientHomeVM.getTags().observe(getViewLifecycleOwner(), tagAdapter::submitList);
        clientHomeVM.getBanners().observe(getViewLifecycleOwner(), banners -> {
            bannerAdapter.setSliderItems(banners != null ? banners : List.of());
            if (banners != null && !banners.isEmpty() && !banners.get(0).isSkeleton()) {
                if (binding.bannerTabLayout.getTabCount() > 0) {
                    TabLayout.Tab tab = binding.bannerTabLayout.getTabAt(0);
                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        });

        // --- Observador de Errores Unificado ---
        clientHomeVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void openProductDetail(Product product) {
        if (product.isSkeleton()) return; // No hacer nada si se hace clic en un esqueleto
        Intent intent = new Intent(requireContext(), ClientDetailActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevenir fugas de memoria
    }
}
