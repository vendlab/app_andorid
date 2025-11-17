package com.marlodev.app_android.adapter.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.Pedido;

import java.util.List;

/**
 * Adapter del RecyclerView que muestra la lista de pedidos en la pantalla principal del delivery.
 */
public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private final List<Pedido> listaPedidos;
    private final OnPedidoClickListener listener;

    // Interfaz para manejar eventos de click de cada pedido
    public interface OnPedidoClickListener {
        void onAceptarClick(Pedido pedido);
        void onDetalleClick(Pedido pedido);
    }

    public PedidoAdapter(List<Pedido> listaPedidos, OnPedidoClickListener listener) {
        this.listaPedidos = listaPedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_home, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.txtCliente.setText("Cliente: " + pedido.getCliente());
        holder.txtDireccion.setText(pedido.getDireccion());
        holder.txtDistancia.setText("Distancia: " + pedido.getDistancia());

        holder.btnAceptar.setOnClickListener(v -> {
            if (listener != null) listener.onAceptarClick(pedido);
        });

        holder.btnDetalle.setOnClickListener(v -> {
            if (listener != null) listener.onDetalleClick(pedido);
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos != null ? listaPedidos.size() : 0;
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {

        TextView txtCliente, txtDireccion, txtDistancia;
        Button btnAceptar, btnDetalle;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCliente = itemView.findViewById(R.id.txtCliente);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtDistancia = itemView.findViewById(R.id.txtDistancia);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnDetalle = itemView.findViewById(R.id.btnDetalle);
        }
    }
}
