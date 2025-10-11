//package com.marlodev.app_android;
//
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.CompositePageTransformer;
//import androidx.viewpager2.widget.MarginPageTransformer;
//
//import com.ismaeldivita.chipnavigation.ChipNavigationBar;
//import com.marlodev.app_android.adapter.PopularAdapter;
//import com.marlodev.app_android.adapter.SliderAdapter;
//import com.marlodev.app_android.adapter.TagAdapter;
//import com.marlodev.app_android.databinding.ActivityMainBinding;
//import com.marlodev.app_android.domain.BannerModel;
//import com.marlodev.app_android.domain.Product;
//import com.marlodev.app_android.viewmodel.MainViewModel;
//import com.marlodev.app_android.viewmodel.ProductViewModel;
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//
//    private ActivityMainBinding binding;
//    private MainViewModel viewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        viewModel = new MainViewModel();
//        initTag();
//        initBanner();
//        initPopular();
//        bottomNavigation();
//    }
//
//    private void bottomNavigation() {
//        binding.bottomNavigation.setItemSelected(R.id.menu_home, true);
//        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener(){
//            @Override
//            public void onItemSelected(int i) {
//            }
//        });
//    }
//
////    Firabase
//
//    private void initPopular() {
//        binding.preogresBarPopular.setVisibility(View.VISIBLE);
//        viewModel.loadPopular().observeForever(Product ->{
//            if(!Product.isEmpty()){
//                binding.popularView.setLayoutManager(
//                        new LinearLayoutManager (MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
//                binding.popularView.setAdapter(new PopularAdapter(Product));
//                binding.popularView.setNestedScrollingEnabled(true);
//            }
//            binding.preogresBarPopular.setVisibility(View.GONE);
//        });
//        viewModel.loadPopular();
//    }
//
//
//
////    spring boot
//
//
//     private void initBanner() {
//        binding.progressBarSlider.setVisibility(View.VISIBLE);
//        viewModel.loadBanner().observeForever(bannerModel -> {
//            if (bannerModel!=null && !bannerModel.isEmpty()){
//                banners(bannerModel);
//                binding.progressBarSlider.setVisibility(View.GONE);
//            }
//        });
//
//        viewModel.loadBanner();
//    }
//
//    private void banners(ArrayList<BannerModel> bannerModel) {
//        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModel,binding.viewPagerSlider));
//        binding.viewPagerSlider.setClipToPadding(false);
//        binding.viewPagerSlider.setClipChildren(false);
//        binding.viewPagerSlider.setOffscreenPageLimit(3);
//        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
//
//        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
//        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
//        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
//
//
//    }
//
//    private  void initTag(){
//        binding.preogresBarTag.setVisibility(View.VISIBLE);
//        viewModel.loadCategory().observeForever(tagModel -> {
//            binding.tagsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//            binding.tagsView.setAdapter(new TagAdapter(tagModel));
//            binding.tagsView.setNestedScrollingEnabled(true);
//            binding.preogresBarTag.setVisibility(View.GONE);
//        });
//     }
//
//
//
//}
//


package com.marlodev.app_android;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.adapter.SliderAdapter;
import com.marlodev.app_android.adapter.TagAdapter;
import com.marlodev.app_android.databinding.ActivityMainBinding;
import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.viewmodel.MainViewModel;
import com.marlodev.app_android.viewmodel.ProductViewModel;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PopularAdapter popularAdapter;
    private MainViewModel mainViewModel;
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        initBanner();
        initTag();
        initPopular();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setItemSelected(R.id.menu_home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> { });
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

        binding.preogresBarPopular.setVisibility(View.VISIBLE);
        productViewModel.getProducts().observe(this, products -> {
            binding.preogresBarPopular.setVisibility(View.GONE);
            popularAdapter.setProducts(products);
        });
    }
}
