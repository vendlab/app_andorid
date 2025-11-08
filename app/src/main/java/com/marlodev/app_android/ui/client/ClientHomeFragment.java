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

import com.marlodev.app_android.adapter.PopularAdapter;
import com.marlodev.app_android.adapter.SliderAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.ProductViewModel;

import java.util.List;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ProductViewModel productViewModel;
    private PopularAdapter popularAdapter;
    private SliderAdapter sliderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        setupAdapters();
        observeViewModel();

        return binding.getRoot();
    }

    private void setupAdapters() {
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularView.setAdapter(popularAdapter);
        popularAdapter.setOnProductClickListener(this::openProductDetail);
//        sliderAdapter = new SliderAdapter();
//        binding.viewPagerSlider.setAdapter(sliderAdapter);
    }

    private void observeViewModel() {
        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            updatePopularProducts(products);
        });

        productViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::showPopularLoading);

        productViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePopularProducts(List<Product> products) {
        boolean hasData = products != null && !products.isEmpty();
        popularAdapter.setProducts(hasData ? products : List.of());
        binding.popularView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    private void showPopularLoading(Boolean isLoading) {
        boolean loading = Boolean.TRUE.equals(isLoading);
        binding.progressBarPopular.setVisibility(loading ? View.VISIBLE : View.GONE);

        // Si no hay productos, mantenemos visible el loading
        if (!loading && popularAdapter.getItemCount() > 0) {
            binding.popularView.setVisibility(View.VISIBLE);
        } else if (loading) {
            binding.popularView.setVisibility(View.GONE);
        }
    }

    private void openProductDetail(Product product) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("productId", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
