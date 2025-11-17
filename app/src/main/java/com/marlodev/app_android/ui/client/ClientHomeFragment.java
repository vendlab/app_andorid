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

import com.marlodev.app_android.adapter.client.PopularAdapter;
import com.marlodev.app_android.adapter.client.TagAdapter;
import com.marlodev.app_android.databinding.FragmentClientHomeBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.domain.TagsModel;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.ClientHomeVM;

import java.util.List;

public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ClientHomeVM clientHomeVM;
    private PopularAdapter popularAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);

        clientHomeVM = new ViewModelProvider(requireActivity()).get(ClientHomeVM.class);

        // Observa LiveData de productos y errores
        observeViewModel();

        // 🔹 Paso 5: Observers WS ligados al ciclo de vida
        clientHomeVM.observeWebSocketEvents(getViewLifecycleOwner());
        clientHomeVM.observeWebSocketState(getViewLifecycleOwner());

        // 🔹 Paso 6: Conectar WebSocket
        clientHomeVM.startWebSocket();

        // Configura adapters (si quieres mantener setupPopularAdapter separado, también puedes llamarlo aquí)
        setupPopularAdapter();




        return binding.getRoot();
    }



    // --- PRODUCTOS POPULARES ---
    private void setupPopularAdapter() {
        popularAdapter = new PopularAdapter();

        binding.popularView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        binding.popularView.setAdapter(popularAdapter);

        popularAdapter.setOnProductClickListener(this::openProductDetail);
    }


    private void observeViewModel() {
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), this::updatePopularProducts);
        clientHomeVM.getIsLoading().observe(getViewLifecycleOwner(), this::showLoading);
        clientHomeVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void updatePopularProducts(List<Product> products) {
        boolean hasData = products != null && !products.isEmpty();

        popularAdapter.setProducts(hasData ? products : List.of());

        binding.popularView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    private void showLoading(Boolean loading) {
        boolean isLoading = Boolean.TRUE.equals(loading);

        binding.progressBarPopular.setVisibility(isLoading ? View.VISIBLE : View.GONE);

        binding.popularView.setVisibility(
                !isLoading && popularAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE
        );
    }

    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

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
