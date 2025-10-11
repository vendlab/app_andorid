//package com.marlodev.app_android.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.CenterCrop;
//import com.bumptech.glide.request.RequestOptions;
//import com.marlodev.app_android.R;
//import com.marlodev.app_android.databinding.ViewholderPopularBinding;
//import com.marlodev.app_android.domain.Product;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
//
//    private final List<Product> products = new ArrayList<>();
//    private Context context;
//
//    public PopularAdapter() {
//        // Constructor vacío
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        context = parent.getContext();
//        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
//                LayoutInflater.from(context), parent, false
//        );
//        return new ViewHolder(binding);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Product product = products.get(position);
//
//        // 1. Nombre del producto
//        holder.binding.txtTitle.setText(product.getName() != null ? product.getName() : "Sin nombre");
//
//
//
//
//        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty() && product.getImageUrls().get(0) != null) {
//            Glide.with(context)
//                    .load(product.getImageUrls().get(0))
//                    .placeholder(R.drawable.ic_image_placeholder)
//                    .error(R.drawable.ic_image_placeholder)
//                    .centerCrop()
//                    .into(holder.binding.pic);
//        } else {
//            holder.binding.pic.setImageResource(R.drawable.ic_image_placeholder);
//        }
//
//
//        // 3. Etiqueta "Nuevo"
//        holder.binding.txtNuevo.setVisibility(product.isNew() ? View.VISIBLE : View.GONE);
//
//        // 4. Precio y precio antiguo
//        holder.binding.txtPrece.setText("S/." + product.getPrice());
//        holder.binding.txtOldPrece.setText("S/." + product.getOldPrice());
//
//        // 5. Opcional: Rating y reviews
//        holder.binding.txtRanking.setText(String.valueOf(product.getRating()));
//        holder.binding.txtReviews.setText("(" + product.getReviewsCount() + ")");
//    }
//
//    @Override
//    public int getItemCount() {
//        return products.size();
//    }
//
//    // ✅ Método para actualizar la lista de productos
//    public void setProducts(List<Product> newProducts) {
//        products.clear();
//        if (newProducts != null) products.addAll(newProducts);
//        notifyDataSetChanged();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private final ViewholderPopularBinding binding;
//
//        public ViewHolder(@NonNull ViewholderPopularBinding binding) {
//            super(binding.getRoot());
//            this.binding = binding;
//        }
//    }


package com.marlodev.app_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ViewholderPopularBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final List<Product> products = new ArrayList<>();
    private Context context;

    public PopularAdapter() {}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.binding.txtTitle.setText(product.getName() != null ? product.getName() : "Sin nombre");

        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty() && product.getImageUrls().get(0) != null) {
            Glide.with(context)
                    .load(product.getImageUrls().get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(holder.binding.pic);
        } else {
            holder.binding.pic.setImageResource(R.drawable.ic_image_placeholder);
        }

        holder.binding.txtNuevo.setVisibility(product.isNew() ? View.VISIBLE : View.GONE);
        holder.binding.txtPrece.setText("S/." + product.getPrice());
        holder.binding.txtOldPrece.setText("S/." + product.getOldPrice());
        holder.binding.txtRanking.setText(String.valueOf(product.getRating()));
        holder.binding.txtReviews.setText("(" + product.getReviewsCount() + ")");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // ────────── LISTA COMPLETA ──────────
    public void setProducts(List<Product> newProducts) {
        products.clear();
        if (newProducts != null) products.addAll(newProducts);
        notifyDataSetChanged();
    }

    // ────────── MÉTODOS PARA WEBSOCKET ──────────
    public void addProduct(ProductWebSocketEvent event) {
        products.add(0, Product.fromWebSocketEvent(event)); // agregar al inicio
        notifyItemInserted(0);
    }

    public void updateProduct(ProductWebSocketEvent event) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == event.id) {
                products.set(i, Product.fromWebSocketEvent(event));
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeProduct(long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                products.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void updateProductImages(long id, String imageUrl) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                products.get(i).setImageUrls(List.of(imageUrl));
                notifyItemChanged(i);
                return;
            }
        }
    }

    // ────────── VIEW HOLDER ──────────
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderPopularBinding binding;

        public ViewHolder(@NonNull ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
