package com.marlodev.app_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.MainViewModel;
import com.marlodev.app_android.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PopularAdapter popularAdapter;
    private MainViewModel mainViewModel;
    private ProductViewModel productViewModel;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        loadUserRole();

        initBanner();
        initTag();
        initPopular();
        setupBottomNavigation();
    }

    private void loadUserRole() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        role = prefs.getString("role", "CLIENT");
        Toast.makeText(this, "Bienvenido! Rol: " + role, Toast.LENGTH_SHORT).show();
    }

    // --- Todo tu código existente se mantiene igual ---
    private void setupBottomNavigation() {
        binding.bottomNavigation.setItemSelected(R.id.menu_home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            // TODO: manejar navegación
        });
    }

    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        mainViewModel.loadBanner().observe(this, banners -> {
            if (banners != null && !banners.isEmpty()) {
                setupBannerSlider(banners);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });
    }

    private void setupBannerSlider(ArrayList<BannerModel> banners) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(banners, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    private void initTag() {
        binding.preogresBarTag.setVisibility(View.VISIBLE);
        mainViewModel.loadCategory().observe(this, tags -> {
            binding.tagsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.tagsView.setAdapter(new TagAdapter(tags));
            binding.tagsView.setNestedScrollingEnabled(true);
            binding.preogresBarTag.setVisibility(View.GONE);
        });
    }

    private void initPopular() {
        RecyclerView recyclerView = binding.popularView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularAdapter = new PopularAdapter();
        recyclerView.setAdapter(popularAdapter);

        popularAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
        });

        binding.preogresBarPopular.setVisibility(View.VISIBLE);
        productViewModel.getProducts().observe(this, products -> {
            binding.preogresBarPopular.setVisibility(View.GONE);
            popularAdapter.setProducts(products);
        });
    }
}
