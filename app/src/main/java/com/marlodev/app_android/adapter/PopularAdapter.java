package com.marlodev.app_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemProductPopularBinding;
import com.marlodev.app_android.domain.Product;
import com.marlodev.app_android.model.ProductWebSocketEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter para mostrar productos populares en un RecyclerView horizontal.
 * También soporta actualizaciones en tiempo real mediante WebSocket.
 */
public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final List<Product> products = new ArrayList<>(); // Lista de productos
    private Context context;                                   // Contexto para Glide


    //#####
    private OnProductClickListener listener;

    // Interfaz para manejar click
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Setter del listener
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }
    // #######


    public PopularAdapter() {}

    // --------------------------
    // 🔹 Inflado de item
    // --------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        // Inflamos el layout item_product_popular.xml
        ItemProductPopularBinding binding = ItemProductPopularBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    // --------------------------
    // 🔹 Bind de datos al item
    // --------------------------
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        // ────────── Título del producto ──────────
        holder.binding.txtTitle.setText(
                product.getName() != null ? product.getName() : "Sin nombre"
        );

        // ────────── Imagen del producto ──────────
        List<String> images = product.getImageUrls();
        if (images != null && !images.isEmpty()) {
            Glide.with(context)
                    .load(images.get(0))                       // Carga la primera imagen
                    .placeholder(R.drawable.ic_image_placeholder) // Imagen mientras carga
                    .error(R.drawable.ic_image_placeholder)      // Imagen si falla
                    .centerCrop()
                    .into(holder.binding.pic);
        } else {
            holder.binding.pic.setImageResource(R.drawable.ic_image_placeholder);
        }

        // ────────── Indicador "Nuevo" ──────────
        holder.binding.txtNuevo.setVisibility(product.isNew() ? View.VISIBLE : View.GONE);

        // ────────── Precio ──────────
        holder.binding.txtPrece.setText("S/." + product.getPrice());
        holder.binding.txtOldPrece.setText("S/." + product.getOldPrice());

        // ────────── Rating y cantidad de reviews ──────────
        holder.binding.txtRanking.setText(String.valueOf(product.getRating()));
        holder.binding.txtReviews.setText("(" + product.getReviewsCount() + ")");

        //#########
        // En onBindViewHolder:
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(products.get(position));
            }
        });


        //#######
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // --------------------------
    // 🔹 Actualizar lista completa de productos
    // --------------------------
    public void setProducts(List<Product> newProducts) {
        products.clear();
        if (newProducts != null) products.addAll(newProducts);
        notifyDataSetChanged();
    }

    // --------------------------
    // 🔹 Métodos para manejar actualizaciones vía WebSocket
    // --------------------------

    // Agrega un nuevo producto al inicio de la lista
    public void addProduct(ProductWebSocketEvent event) {
        Product product = Product.fromWebSocketEvent(event);
        if (product.getImageUrls() == null) {
            product.setImageUrls(Collections.emptyList()); // Evita null
        }
        products.add(0, product);
        notifyItemInserted(0);
    }

    // Actualiza un producto existente según el evento WebSocket
    public void updateProduct(ProductWebSocketEvent event) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == event.id) {
                Product updated = Product.fromWebSocketEvent(event);
                if (updated.getImageUrls() == null) {
                    updated.setImageUrls(Collections.emptyList());
                }
                products.set(i, updated);
                notifyItemChanged(i);
                return;
            }
        }
    }

    // Elimina un producto por ID
    public void removeProduct(long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                products.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    // Actualiza solo las imágenes de un producto
    public void updateProductImages(long id, String imageUrl) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    products.get(i).setImageUrls(List.of(imageUrl));
                } else {
                    products.get(i).setImageUrls(Collections.emptyList());
                }
                notifyItemChanged(i);
                return;
            }
        }
    }

    // --------------------------
    // 🔹 ViewHolder
    // --------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductPopularBinding binding;

        public ViewHolder(@NonNull ItemProductPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
