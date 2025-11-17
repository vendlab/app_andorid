package com.marlodev.app_android.ui.delivery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.marlodev.app_android.MainActivity;
import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.client.PedidoAdapter;
import com.marlodev.app_android.domain.Pedido;
import com.marlodev.app_android.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeFragment extends Fragment implements PedidoAdapter.OnPedidoClickListener {

    private RecyclerView recyclerPedidos;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> listaPedidos;
    private OnPedidoSelectedListener listener;
    private MaterialButton btnLogout;

    public interface OnPedidoSelectedListener {
        void onPedidoSelected(Pedido pedido);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoSelectedListener) {
            listener = (OnPedidoSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnPedidoSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery_home, container, false);

        // INICIALIZAR BOTÓN AQUÍ ⬇⬇⬇⬇
        initViews(view);
        setupLogoutButton();

        recyclerPedidos = view.findViewById(R.id.recyclerViewPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPedidos = new ArrayList<>();
        listaPedidos.add(new Pedido(1, "Benjamin Rumay", "Jirón Del Comercio 456, Cajamarca, Perú", "1.2 km"));
        listaPedidos.add(new Pedido(2, "Fany Palomino", "Avenida Los Héroes 1020, Cajamarca, Perú", "3.5 km"));
        listaPedidos.add(new Pedido(3, "Hugo Silva", "Jirón Dos de Mayo 315, Cajamarca, Perú", "2.1 km"));

        pedidoAdapter = new PedidoAdapter(listaPedidos, this);
        recyclerPedidos.setAdapter(pedidoAdapter);

        return view;
    }

    private void initViews(View root) {
        btnLogout = root.findViewById(R.id.btnLogout);
    }

    private void setupLogoutButton() {
        btnLogout.setOnClickListener(v -> {
            // Limpia la sesión
            SessionManager.getInstance(requireContext()).clear();

            // Redirigir a MainActivity
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            requireActivity().finish();
        });
    }

    @Override
    public void onAceptarClick(Pedido pedido) {
        Toast.makeText(getContext(), "Pedido de " + pedido.getCliente() + " aceptado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetalleClick(Pedido pedido) {
        if (listener != null) listener.onPedidoSelected(pedido);
    }
}
