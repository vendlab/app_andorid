package com.marlodev.app_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.delivery.Pedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Pedido> pedidoList;

    public PedidoAdapter(List<Pedido> pedidoList) {
        this.pedidoList = pedidoList;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);
        holder.txtCliente.setText("Frape Mocha Grande - " + pedido.getNombreCliente());
        holder.txtDireccion.setText(pedido.getDireccion());
        holder.txtDistancia.setText(pedido.getDistanciaKm() + " km / " + pedido.getTiempoEstimado());
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
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