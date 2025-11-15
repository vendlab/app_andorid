package com.marlodev.app_android.ui.delivery;

import android.content.Context;
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

import com.marlodev.app_android.R;
import com.marlodev.app_android.adapter.PedidoAdapter;
import com.marlodev.app_android.domain.Pedido;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeFragment extends Fragment implements PedidoAdapter.OnPedidoClickListener {

    private RecyclerView recyclerPedidos;
    private PedidoAdapter pedidoAdapter;
    private List<Pedido> listaPedidos;
    private OnPedidoSelectedListener listener;

    // Interfaz para comunicar con la Activity
    public interface OnPedidoSelectedListener {
        void onPedidoSelected(Pedido pedido);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPedidoSelectedListener) {
            listener = (OnPedidoSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " debe implementar OnPedidoSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_delivery_home, container, false);
        recyclerPedidos = view.findViewById(R.id.recyclerViewPedidos);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos simulados
        listaPedidos = new ArrayList<>();
        listaPedidos.add(new Pedido(1, "Benjamin Rumay", "Jirón Del Comercio 456, Cajamarca, Perú", "1.2 km"));
        listaPedidos.add(new Pedido(2, "Fany Palomino", "Avenida Los Héroes 1020, Cajamarca, Perú", "3.5 km"));
        listaPedidos.add(new Pedido(3, "Hugo Silva", "Jirón Dos de Mayo 315, Cajamarca, Perú", "2.1 km"));

        pedidoAdapter = new PedidoAdapter(listaPedidos, this);
        recyclerPedidos.setAdapter(pedidoAdapter);

        return view;
    }

    @Override
    public void onAceptarClick(Pedido pedido) {
        Toast.makeText(getContext(), "Pedido de " + pedido.getCliente() + " aceptado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetalleClick(Pedido pedido) {
        if (listener != null) {
            listener.onPedidoSelected(pedido);
        }
    }
}
