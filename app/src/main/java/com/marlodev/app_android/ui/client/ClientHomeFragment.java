package com.marlodev.app_android.ui.client;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.ArrayList;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        setupAdapters();
        observeViewModel();
    }

    private void initViewModel() {
        Context context = requireContext().getApplicationContext();
        String token = SessionManager.getInstance(context).getToken();
        ProductApiService productApi = ApiClient.getClient(context).create(ProductApiService.class);
        TagApiService tagApi = ApiClient.getClient(context).create(TagApiService.class);
        BannerApiService bannerApi = ApiClient.getClient(context).create(BannerApiService.class);
        ProductRepository productRepository = new ProductRepository(productApi, new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/products", ProductWebSocketEvent.class));
        TagRepository tagRepository = new TagRepository(tagApi);
        BannerRepository bannerRepository = new BannerRepository(bannerApi, new GenericWebSocketManager<>(BuildConfig.WS_URL, token, "/topic/banners", BannerWebSocketEvent.class));
        ClientHomeVMFactory factory = new ClientHomeVMFactory(productRepository, tagRepository, bannerRepository);
        clientHomeVM = new ViewModelProvider(this, factory).get(ClientHomeVM.class);
    }

    private void setupAdapters() {
        // Popular Products & Tags
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularView.setAdapter(popularAdapter);
        popularAdapter.setOnProductClickListener(this::openProductDetail);

        tagAdapter = new TagAdapter();
        binding.tagRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.tagRecyclerView.setAdapter(tagAdapter);

        // --- ARQUITECTURA CORRECTA: Lógica encapsulada en el Adapter ---

        // 1. Instanciar el adapter pasándole el ViewPager2. El constructor del adapter se encarga de TODA la configuración.
        bannerAdapter = new BannerAdapter(requireContext(), binding.bannerViewPager);

        // 2. Asignar el adaptador.
        binding.bannerViewPager.setAdapter(bannerAdapter);

        // 3. Crear y establecer el estado de carga INICIAL para prevenir el "layout fantasma".
        List<Banner> initialSkeletons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            initialSkeletons.add(new Banner(null, null, null, null, null, true));
        }
        bannerAdapter.setSliderItems(initialSkeletons);

        // 4. El resto de la configuración (TabLayout, listeners) permanece aquí.
        new TabLayoutMediator(binding.bannerTabLayout, binding.bannerViewPager, (tab, position) -> {
            tab.setCustomView(R.layout.tab_custom_dot);
        }).attach();

        bannerAdapter.setOnBannerClickListener(banner -> {
            if (!banner.isSkeleton()) {
                Snackbar.make(binding.getRoot(), "Banner: " + banner.getTitle(), Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.bannerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dot = tab.getCustomView().findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(30);
                    dot.setLayoutParams(params);
                    dot.setSelected(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    View dot = tab.getCustomView().findViewById(R.id.dotView);
                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(9);
                    dot.setLayoutParams(params);
                    dot.setSelected(false);
                }
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeViewModel() {
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), popularAdapter::submitList);
        clientHomeVM.getTags().observe(getViewLifecycleOwner(), tagAdapter::submitList);
        clientHomeVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        clientHomeVM.getBanners().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null && !banners.isEmpty()) {
                bannerAdapter.setSliderItems(banners);
                if (binding.bannerTabLayout.getTabCount() > 0) {
                    binding.bannerTabLayout.selectTab(binding.bannerTabLayout.getTabAt(0));
                }
            }
        });

        clientHomeVM.startWebSocket();
        clientHomeVM.observeWebSocketEvents(getViewLifecycleOwner());
    }

    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void openProductDetail(Product product) {
        if (product.isSkeleton()) return;
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
        binding = null;
    }
}
