package com.marlodev.app_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.model.order.OrderResponse;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderResponse> orders;

    public OrderAdapter(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);
//        holder.tvId.setText("Pedido #" + order.getId());
        holder.tvStatus.setText(order.getStatus());
        // aquí puedes llenar más campos
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
//            tvId = itemView.findViewById(R.id.tv_order_id);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }

}