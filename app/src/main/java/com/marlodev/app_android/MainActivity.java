package com.marlodev.app_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge; // Para UI Edge-to-Edge
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Para obtener ViewModels con ciclo de vida
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer; // Para efectos de transición en ViewPager2
import androidx.viewpager2.widget.MarginPageTransformer;

import com.ismaeldivita.chipnavigation.ChipNavigationBar; // Librería de BottomNavigation
import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.adapter.SliderAdapter;
import com.marlodev.app_android.adapter.TagAdapter;
import com.marlodev.app_android.databinding.ActivityMainBinding; // ViewBinding para la Activity
import com.marlodev.app_android.domain.BannerModel;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.MainViewModel;
import com.marlodev.app_android.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // ViewBinding para acceder a vistas de forma segura
    private ActivityMainBinding binding;

    // Adapter para la lista de productos populares
    private PopularAdapter popularAdapter;

    // ViewModels para manejar datos de UI
    private MainViewModel mainViewModel;
    private ProductViewModel productViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilita el modo Edge-to-Edge para la UI (mejora visual)
        EdgeToEdge.enable(this);

        // Inicializa ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializa ViewModels respetando el ciclo de vida de la Activity
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Inicializa componentes UI
        initBanner();        // Carga banners en el slider
        initTag();           // Carga categorías / tags
        initPopular();       // Carga productos populares
        setupBottomNavigation(); // Configura el BottomNavigation
    }

    /**
     * Configura el BottomNavigation
     */
    private void setupBottomNavigation() {
        // Selecciona el item Home por defecto
        binding.bottomNavigation.setItemSelected(R.id.menu_home, true);

        // Listener para manejar clicks en los items
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            // TODO: manejar navegación entre fragmentos o actividades según itemId
        });
    }

    /**
     * Inicializa el slider de banners
     */
    private void initBanner() {
        binding.progressBarSlider.setVisibility(View.VISIBLE); // Muestra ProgressBar

        // Observa la lista de banners desde el ViewModel
        mainViewModel.loadBanner().observe(this, banners -> {
            if (banners != null && !banners.isEmpty()) {
                setupBannerSlider(banners); // Configura el slider con banners
            }
            binding.progressBarSlider.setVisibility(View.GONE); // Oculta ProgressBar
        });
    }

    /**
     * Configura el ViewPager2 con los banners
     */
    private void setupBannerSlider(ArrayList<BannerModel> banners) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(banners, binding.viewPagerSlider));

        // Evita que el ViewPager2 recorte el padding o hijos
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);

        // Mantiene 3 páginas en memoria para suavizar scroll
        binding.viewPagerSlider.setOffscreenPageLimit(3);

        // Desactiva efecto de overscroll
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // Añade efecto de margen entre páginas
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(30));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    /**
     * Inicializa la lista de tags / categorías
     */
    private void initTag() {
        binding.preogresBarTag.setVisibility(View.VISIBLE); // Muestra ProgressBar

        mainViewModel.loadCategory().observe(this, tags -> {
            // Configura RecyclerView horizontal para los tags
            binding.tagsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.tagsView.setAdapter(new TagAdapter(tags));
            binding.tagsView.setNestedScrollingEnabled(true);

            binding.preogresBarTag.setVisibility(View.GONE); // Oculta ProgressBar
        });
    }

    /**
     * Inicializa la lista de productos populares
     */
    private void initPopular() {
        RecyclerView recyclerView = binding.popularView;

        // Configura layout horizontal para RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Inicializa adapter y lo asigna al RecyclerView
        popularAdapter = new PopularAdapter();
        recyclerView.setAdapter(popularAdapter);

//        #######

        // Configura click en producto
        popularAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("productId", product.getId()); // Pasamos solo el ID
            startActivity(intent);
        });
//        #######

        // Observa productos desde ProductViewModel
        binding.preogresBarPopular.setVisibility(View.VISIBLE); // Muestra ProgressBar
        productViewModel.getProducts().observe(this, products -> {
            binding.preogresBarPopular.setVisibility(View.GONE); // Oculta ProgressBar
            popularAdapter.setProducts(products); // Actualiza datos en el Adapter
        });
    }
}
