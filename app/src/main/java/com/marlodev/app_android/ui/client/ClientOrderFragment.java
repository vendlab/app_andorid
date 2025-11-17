//package com.marlodev.app_android.ui.client;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull; // Importación necesaria para el override
//import androidx.annotation.Nullable;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.marlodev.app_android.R;
//import com.marlodev.app_android.utils.StatusBarUtil;
//public class ClientOrderFragment extends Fragment {
//
//    public ClientOrderFragment() {
//        // Required empty public constructor
//    }
//    public static ClientOrderFragment newInstance() {
//        return new ClientOrderFragment();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//
//        // Inflar el layout
//        View view = inflater.inflate(R.layout.fragment_client_order, container, false);
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
//}

package com.marlodev.app_android.ui.client;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.client.OrderAdapter;
import com.marlodev.app_android.network.ApiClient;
import com.marlodev.app_android.network.order.OrderApi;
import com.marlodev.app_android.repository.OrderRepository;
import com.marlodev.app_android.viewmodel.ClientOrderViewModel;

import java.util.ArrayList;

public class ClientOrderFragment extends Fragment {

    private ClientOrderViewModel viewModel;
    private OrderAdapter activeOrdersAdapter;
    private OrderAdapter historyOrdersAdapter;

    private RecyclerView rvActive;
    private RecyclerView rvHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_client_order, container, false);

        // Ajustar padding según barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(view);
        initAdapters();
        initViewModel();
        setupObservers();

        return view;
    }

    /** Inicializa las RecyclerViews */
    private void initViews(View view) {
        rvActive = view.findViewById(R.id.active_orders_recycler_view);
        rvHistory = view.findViewById(R.id.history_recycler_view);
    }

    /** Inicializa los Adapters y LayoutManagers */
    private void initAdapters() {
        activeOrdersAdapter = new OrderAdapter(new ArrayList<>());
        historyOrdersAdapter = new OrderAdapter(new ArrayList<>());

        rvActive.setLayoutManager(new LinearLayoutManager(getContext()));
        rvActive.setAdapter(activeOrdersAdapter);
        rvActive.setNestedScrollingEnabled(false); // importante para NestedScrollView

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(historyOrdersAdapter);
        rvHistory.setNestedScrollingEnabled(false); // importante para NestedScrollView
    }

    /** Inicializa el ViewModel con Repository */
    private void initViewModel() {
        // Crear instancia del API usando ApiClient
        OrderApi api = ApiClient.getClient(requireContext()).create(OrderApi.class);
        OrderRepository repository = new OrderRepository(api);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new ClientOrderViewModel(repository);
            }
        }).get(ClientOrderViewModel.class);
    }

    /** Configura los observadores del ViewModel */
    private void setupObservers() {
        viewModel.getActiveOrders().observe(getViewLifecycleOwner(), orders -> {
            activeOrdersAdapter.setOrders(orders);
        });

        viewModel.getHistoryOrders().observe(getViewLifecycleOwner(), orders -> {
            historyOrdersAdapter.setOrders(orders);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                // Aquí puedes mostrar un Toast o Snackbar
                // Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
