package com.marlodev.app_android.ui.client;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.ProductViewModel;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ProductViewModel productViewModel;
    private PopularAdapter popularAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        initPopular();

        return binding.getRoot();
    }

    private void initPopular() {
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.popularView.setAdapter(popularAdapter);

        popularAdapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
        });

        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                popularAdapter.setProducts(products);
            }
        });

        productViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBarPopular.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
            // **IMPORTANTE**: También oculta el RecyclerView mientras carga para evitar mostrar datos antiguos.
            // Aunque esto no es estrictamente necesario, es una buena práctica.
            binding.popularView.setVisibility(Boolean.TRUE.equals(isLoading) ? View.GONE : View.VISIBLE);
        });

        // 🎯 Llama a loadProducts() solo si los productos no se han cargado.
        // Esto asume que productViewModel.getProducts() devuelve un LiveData
        // que mantiene el estado de los datos ya cargados.
        if (productViewModel.getProducts().getValue() == null || productViewModel.getProducts().getValue().isEmpty()) {
            productViewModel.loadProducts();
        }
    }
}
