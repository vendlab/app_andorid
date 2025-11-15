package com.marlodev.app_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.model.order.CartItemResponse;
import com.marlodev.app_android.model.order.OrderResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderResponse> orders;

    public OrderAdapter(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        if (orders != null) {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

            orders.sort((o1, o2) -> {
                try {
                    Date d1 = isoFormat.parse(o1.getCreatedAt());
                    Date d2 = isoFormat.parse(o2.getCreatedAt());
                    return d2.compareTo(d1); // DESCENDENTE: más reciente primero
                } catch (Exception e) {
                    return 0;
                }
            });
        }

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

        // Status y mensaje
        holder.tvStatus.setText(order.getStatus());
        holder.tvMessage.setText(order.getMessage());

        if (order.getCreatedAt() != null) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date date = isoFormat.parse(order.getCreatedAt());

                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM", new Locale("es", "PE"));
                holder.tvDate.setText(sdf.format(date));
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvDate.setText(order.getCreatedAt()); // fallback
            }
        } else {
            holder.tvDate.setText("");
        }


        // Última imagen del pedido
        List<CartItemResponse> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            CartItemResponse lastItem = items.get(items.size() - 1);
            List<String> images = lastItem.getProduct().getImageUrls();
            if (images != null && !images.isEmpty()) {
                String lastImageUrl = images.get(images.size() - 1);
                Glide.with(holder.itemView.getContext())
                        .load(lastImageUrl)
                        .into(holder.itemImage);
            } else {
                holder.itemImage.setImageResource(android.R.color.transparent); // vacío si no hay imagen
            }
        } else {
            holder.itemImage.setImageResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvStatus;
        TextView tvMessage;
        ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.text_date);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            tvMessage = itemView.findViewById(R.id.tv_order_message);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
