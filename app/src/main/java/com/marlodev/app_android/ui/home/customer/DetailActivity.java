package com.marlodev.app_android.ui.home.customer;

import android.os.Bundle;
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

        // Inicializar ViewBinding
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        // Configurar botón de volver atrás
        binding.btnArrowLeft.setOnClickListener(v -> finish());

        // Obtener ID del producto enviado desde HomeActivity
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
