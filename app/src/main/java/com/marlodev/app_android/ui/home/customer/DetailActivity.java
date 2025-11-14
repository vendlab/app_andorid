package com.marlodev.app_android.ui.home.customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ActivityDetailBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.ui.auth.LoginActivity;
import com.marlodev.app_android.utils.SessionManager;
import com.marlodev.app_android.viewmodel.ClientProductDetailVM;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private ClientProductDetailVM vm;
    private SessionManager sessionManager;

    private long productId;

    private final ActivityResultLauncher<Intent> loginLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (sessionManager.isLoggedIn()) {
                    vm.addToCart(productId, getQuantity());
                } else {
                    Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtQuantity.setText("1");
        binding.btnPlus.setOnClickListener(v -> {
            int quantity = getQuantity();
            quantity++;  // incrementa
            binding.txtQuantity.setText(String.valueOf(quantity));
        });

        binding.btnMinus.setOnClickListener(v -> {
            int quantity = getQuantity();
            if (quantity > 1) {  // nunca menos de 1
                quantity--;
                binding.txtQuantity.setText(String.valueOf(quantity));
            }
        });
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.getInsetsController().setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }

        vm = new ViewModelProvider(this).get(ClientProductDetailVM.class);
        sessionManager = SessionManager.getInstance(this);


        binding.btnArrowLeft.setOnClickListener(v -> finish());

        productId = getIntent().getLongExtra("productId", -1);
        if (productId != -1) vm.loadProduct(productId);

        binding.btnAddCart.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                vm.addToCart(productId, getQuantity());
            } else {
                loginLauncher.launch(new Intent(this, LoginActivity.class));
            }
        });
        observeViewModel();
    }

    private void observeViewModel() {

        vm.getProduct().observe(this, this::displayProduct);

        vm.getIsLoading().observe(this, loading ->
                binding.progressBarDetail.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE)
        );

        vm.getErrorMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                Log.e("DETAIL", msg);
            }
        });

        vm.getNavigationEvent().observe(this, event -> {
            if (event == null) return;

            if (event.startsWith("GO_LOGIN_FIRST")) {
                loginLauncher.launch(new Intent(this, LoginActivity.class));
            }

            if (event.equals("ADDED_TO_CART")) {
                Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProduct(Product product) {
        if (product == null) return;

        binding.txtTitleProduct.setText(product.getName());
        binding.tvCurrentPrice.setText("S/." + product.getPrice());
        binding.tvOldPrice.setText("S/." + product.getOldPrice());
        binding.txtDescripcion.setText(product.getDescription());

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(this)
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(binding.imageView);
        } else {
            binding.imageView.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    private int getQuantity() {
        return Integer.parseInt(binding.txtQuantity.getText().toString());
    }
}
