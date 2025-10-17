package com.marlodev.app_android.ui.home.customer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ActivityDetailBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.viewmodel.ProductViewModel;

public class DetailActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private ActivityDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI Edge-to-Edge
        EdgeToEdge.enable(this);

        // ViewBinding
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Configurar la barra de estado (status bar) con íconos blancos
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        } else {
            window.getDecorView().setSystemUiVisibility(
                    window.getDecorView().getSystemUiVisibility()
                            & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        // ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Botón atrás
        binding.btnArrowLeft.setOnClickListener(v -> finish());

        // Cargar producto
        long productId = getIntent().getLongExtra("productId", -1);
        if (productId != -1) {
            loadProductDetail(productId);
        }
    }


    /**
     * Carga los detalles del producto usando ProductViewModel
     */
    private void loadProductDetail(long productId) {
        productViewModel.getProductById(productId).observe(this, product -> {
            if (product != null) {
                displayProduct(product);
            }
        });
    }

    /**
     * Muestra los datos del producto en la UI
     */
    private void displayProduct(Product product) {
        binding.txtTitleProduct.setText(product.getName());
        binding.tvCurrentPrice.setText("S/." + product.getPrice());
        binding.tvOldPrice.setText("S/." + product.getOldPrice());
        binding.txtDescripcion.setText(product.getDescription());

        // Cargar imagen principal con Glide
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(binding.imageView);
        } else {
            binding.imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }
}
