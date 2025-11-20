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
        bannerAdapter = new BannerAdapter(requireContext(), binding.bannerViewPager);
        binding.bannerViewPager.setAdapter(bannerAdapter);

        // --- ✅ Restaurar la configuración profesional del ViewPager2 ---
        binding.bannerViewPager.setClipToPadding(false);
        binding.bannerViewPager.setClipChildren(false);
        binding.bannerViewPager.setOffscreenPageLimit(3);

        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(dpToPx(8)));
        compositeTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            float scale = 0.85f + r * 0.15f;
            page.setScaleY(scale);
            page.setScaleX(scale);
            page.setAlpha(0.5f + r * 0.5f);
        });
        binding.bannerViewPager.setPageTransformer(compositeTransformer);
        // --- Fin de la restauración ---

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
