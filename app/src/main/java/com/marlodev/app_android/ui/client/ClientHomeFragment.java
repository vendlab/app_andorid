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
import com.marlodev.app_android.domain.Tag;
import com.marlodev.app_android.ui.home.customer.DetailActivity;
import com.marlodev.app_android.viewmodel.ClientHomeVM;

import java.util.List;

/**
 * Fragment profesional para Home del cliente.
 * - Solo UI: muestra productos y tags.
 * - Observa LiveData y eventos WS.
 * - No filtra productos por tags aún.
 */
public class ClientHomeFragment extends Fragment {

    private FragmentClientHomeBinding binding;
    private ClientHomeVM clientHomeVM;
    private PopularAdapter popularAdapter;
    private TagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentClientHomeBinding.inflate(inflater, container, false);
        clientHomeVM = new ViewModelProvider(requireActivity()).get(ClientHomeVM.class);

        setupAdapters();
        observeViewModel();

        // Conectar WebSocket de productos
        clientHomeVM.startWebSocket();
        clientHomeVM.observeWebSocketEvents(getViewLifecycleOwner());

        return binding.getRoot();
    }

    /** Configura adapters de UI */
    private void setupAdapters() {
        // Adapter de productos
        popularAdapter = new PopularAdapter();
        binding.popularView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.popularView.setAdapter(popularAdapter);
        popularAdapter.setOnProductClickListener(this::openProductDetail);

        // Adapter de tags
        tagAdapter = new TagAdapter();
        binding.tagRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.tagRecyclerView.setAdapter(tagAdapter);

        // Por ahora no filtramos al hacer clic en tags
    }

    /** Observa LiveData de ViewModel */
    private void observeViewModel() {
        // Productos
        clientHomeVM.getProducts().observe(getViewLifecycleOwner(), this::updatePopularProducts);
        clientHomeVM.getIsLoadingProducts().observe(getViewLifecycleOwner(), this::showLoadingProducts);
        clientHomeVM.getProductErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        // Tags
        clientHomeVM.getTags().observe(getViewLifecycleOwner(), this::updateTags);
        clientHomeVM.getIsLoadingTags().observe(getViewLifecycleOwner(), this::showLoadingTags);
        clientHomeVM.getTagErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    /** Actualiza RecyclerView de productos */
    private void updatePopularProducts(List<Product> products) {
        boolean hasData = products != null && !products.isEmpty();
        popularAdapter.setProducts(hasData ? products : List.of());
        binding.popularView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    /** Actualiza RecyclerView de tags */
    private void updateTags(List<Tag> tags) {
        boolean hasData = tags != null && !tags.isEmpty();
        tagAdapter.setTags(hasData ? tags : List.of());
        binding.tagRecyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    /** Mostrar loading de productos */
    private void showLoadingProducts(Boolean loading) {
        binding.progressBarPopular.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
    }

    /** Mostrar loading de tags */
    private void showLoadingTags(Boolean loading) {
        binding.progressBarTags.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
    }

    /** Mostrar error genérico */
    private void showError(String error) {
        if (error != null && !error.isBlank()) {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    /** Abrir detalle de producto */
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
