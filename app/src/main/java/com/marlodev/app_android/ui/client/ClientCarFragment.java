package com.marlodev.app_android.ui.client;

import com.marlodev.app_android.R;


//public class ClientCarFragment extends Fragment {
//
//    public ClientCarFragment() { }
//
//    public static ClientCarFragment newInstance() {
//        return new ClientCarFragment();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        // Inflar el layout
//        View view = inflater.inflate(R.layout.fragment_cliente_carrito, container, false);
//
//        // Ajustar padding según barras del sistema (status bar, navigation bar)
//        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
//            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//        return view;
//    }
//
//
//}



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.marlodev.app_android.adapter.ItemProductCarAdapter;
import com.marlodev.app_android.databinding.FragmentClienteCarritoBinding;
import com.marlodev.app_android.model.order.CartItem;
import com.marlodev.app_android.viewmodel.ClientCartVM;

import java.util.List;

public class ClientCarFragment extends Fragment {

    private FragmentClienteCarritoBinding binding;
    private ClientCartVM cartVM;
    private ItemProductCarAdapter cartAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentClienteCarritoBinding.inflate(inflater, container, false);

        // Ajustar padding por barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViewModel();
        setupRecyclerView();
        setupListeners();

        return binding.getRoot();
    }

    private void setupViewModel() {
        cartVM = new ViewModelProvider(requireActivity()).get(ClientCartVM.class);

        cartVM.getCartItems().observe(getViewLifecycleOwner(), this::updateCartItems);

        cartVM.getTotalItems().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalItems.setText(String.valueOf(total))
        );

        cartVM.getTotalPrice().observe(getViewLifecycleOwner(), total ->
                binding.txtTotalPriceCar.setText("S/. " + total)
        );

        cartVM.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);
    }

    private void setupRecyclerView() {
        cartAdapter = new ItemProductCarAdapter();
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartAdapter);

        // Listener para modificar cantidad
        cartAdapter.setOnQuantityChangeListener((cartItem, newQty) -> {
            cartVM.updateQuantity(cartItem, newQty);
        });

        // Listener para eliminar
        cartAdapter.setOnDeleteClickListener(cartItem -> {
            cartVM.deleteItem(cartItem);
        });
    }

    private void setupListeners() {
        binding.backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnRealizarOrder.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Checkout pendiente de implementar", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateCartItems(List<CartItem> items) {
        cartAdapter.setItems(items);
        binding.cartItemsRecyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (message != null && !message.isBlank()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
