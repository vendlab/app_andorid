package com.marlodev.app_android.ui.client;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.client.BannerAdapter;
import com.marlodev.app_android.adapter.client.PopularAdapter;
import com.marlodev.app_android.adapter.client.TagAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.domain.Banner;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.ClientHomeVM;

import java.util.List;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ClientHomeVM clientHomeVM;
    private PopularAdapter popularAdapter;
    private TagAdapter tagAdapter;
    private BannerAdapter bannerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);
        clientHomeVM = new ViewModelProvider(requireActivity()).get(ClientHomeVM.class);

        setupAdapters();
        observeViewModel();

        clientHomeVM.startWebSocket();
        clientHomeVM.observeWebSocketEvents(getViewLifecycleOwner());

        return binding.getRoot();
    }

    /** ==================== CONFIGURAR ADAPTERS ==================== */
    private void setupAdapters() {
        // Productos
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

        // Generar dots personalizados
        new TabLayoutMediator(binding.bannerTabLayout, binding.bannerViewPager,
                (tab, position) -> {
                    View custom = LayoutInflater.from(requireContext())
                            .inflate(R.layout.tab_custom_dot, binding.bannerTabLayout, false);
                    tab.setCustomView(custom);
                }
        ).attach();

        // CLICK Banner
        bannerAdapter.setOnBannerClickListener(banner -> {
            Toast.makeText(requireContext(), "Banner: " + banner.getTitle(), Toast.LENGTH_SHORT).show();
        });

        /** ==================== LISTENER PARA LOS DOTS ==================== */
        binding.bannerTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    View dot = view.findViewById(R.id.dotView);

                    ViewGroup.LayoutParams params = dot.getLayoutParams();
                    params.width = dpToPx(30);  // rectángulo
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
                    params.width = dpToPx(9);  // vuelve a círculo
                    params.height = dpToPx(9);
                    dot.setLayoutParams(params);

                    dot.setSelected(false);
                }
            }

            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /** Convertir DP → PX */
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    /** ==================== OBSERVADORES ==================== */
    private void observeViewModel() {
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), this::updatePopularProducts);
        clientHomeVM.getIsLoadingProducts().observe(getViewLifecycleOwner(), this::showLoadingProducts);
        clientHomeVM.getProductErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        clientHomeVM.getTags().observe(getViewLifecycleOwner(), this::updateTags);
        clientHomeVM.getIsLoadingTags().observe(getViewLifecycleOwner(), this::showLoadingTags);
        clientHomeVM.getTagErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        clientHomeVM.getBanners().observe(getViewLifecycleOwner(), this::updateBanners);
        clientHomeVM.getIsLoadingBanners().observe(getViewLifecycleOwner(), loading ->
                binding.progressBarBanners.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE)
        );
        clientHomeVM.getBannerErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    /** ==================== ACTUALIZAR UI ==================== */
    private void updatePopularProducts(List<Product> products) {
        boolean hasData = products != null && !products.isEmpty();
        popularAdapter.setProducts(hasData ? products : List.of());
        binding.popularView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    private void updateTags(List<Tag> tags) {
        boolean hasData = tags != null && !tags.isEmpty();
        tagAdapter.setTags(hasData ? tags : List.of());
        binding.tagRecyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    private void updateBanners(List<Banner> banners) {
        boolean hasData = banners != null && !banners.isEmpty();
        bannerAdapter.setSliderItems(hasData ? banners : List.of());
        binding.bannerViewPager.setVisibility(hasData ? View.VISIBLE : View.GONE);

        // 🔥 Fix: seleccionar el primer dot correctamente
        if (binding.bannerTabLayout.getTabAt(0) != null) {
            binding.bannerTabLayout.getTabAt(0).select();
        }
    }

    private void showLoadingProducts(Boolean loading) {
        binding.progressBarPopular.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
    }

    private void showLoadingTags(Boolean loading) {
        binding.progressBarTags.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
    }

    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    /** Abrir detalle de producto */
    private void openProductDetail(Product product) {
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
