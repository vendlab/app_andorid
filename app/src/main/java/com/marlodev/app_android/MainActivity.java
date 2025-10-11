package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.adapter.SliderAdapter;
import com.marlodev.app_android.adapter.TagAdapter;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.viewmodel.MainViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new MainViewModel();

        initTag();
        initBanner();
        initPopular();
        setupBottomNavigation();
    }

    /*** Inicializa la navegación inferior (ChipNavigationBar)*/
    private void setupBottomNavigation() {
        // Selecciona por defecto el ítem Home
        binding.bottomNavigation.setItemSelected(R.id.menu_home, true);

        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                if (id == R.id.menu_home) {
                    // Ya estamos en el Home
                } else if (id == R.id.menu_delivery) {
                    // Abrir Dashboard Delivery
                    Intent intent = new Intent(MainActivity.this, DashboardDeliveryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    /*** Carga la lista de productos populares */
    private void initPopular() {
        binding.preogresBarPopular.setVisibility(View.VISIBLE);

        viewModel.loadPopular().observeForever(products -> {
            if (products != null && !products.isEmpty()) {
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
                );
                binding.popularView.setAdapter(new PopularAdapter(products));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.preogresBarPopular.setVisibility(View.GONE);
        });

        viewModel.loadPopular();
    }

    /*** Carga los banners del slider*/
    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);

        viewModel.loadBanner().observeForever(bannerModel -> {
            if (bannerModel != null && !bannerModel.isEmpty()) {
                banners(bannerModel);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });

        viewModel.loadBanner();
    }

    /*** Configura el ViewPager2 con banners*/
    private void banners(ArrayList<BannerModel> bannerModel) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModel, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(transformer);
    }

    /*** Carga las categorías (tags)*/
    private void initTag() {
        binding.preogresBarTag.setVisibility(View.VISIBLE);

        viewModel.loadCategory().observeForever(tagModel -> {
            binding.tagsView.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            );
            binding.tagsView.setAdapter(new TagAdapter(tagModel));
            binding.tagsView.setNestedScrollingEnabled(true);
            binding.preogresBarTag.setVisibility(View.GONE);
        });
    }
}
