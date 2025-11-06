package com.marlodev.app_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemProductPopularBinding;
import com.marlodev.app_android.domain.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar productos populares en un RecyclerView horizontal.
 * También soporta actualizaciones en tiempo real mediante WebSocket.
 */
public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private final List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    public PopularAdapter() {}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductPopularBinding binding = ItemProductPopularBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        ItemProductPopularBinding b = holder.binding;

        b.txtTitle.setText(product.getName() != null ? product.getName() : "Sin nombre");

        List<String> images = product.getImageUrls();
        if (images != null && !images.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(images.get(0))
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(b.pic);
        } else {
            b.pic.setImageResource(R.drawable.ic_image_placeholder);
        }

        b.txtNuevo.setVisibility(product.isNewProduct() ? View.VISIBLE : View.GONE);
        b.txtPrece.setText("S/." + product.getPrice());
        b.txtOldPrece.setText("S/." + product.getOldPrice());
        b.txtRanking.setText(String.valueOf(product.getRating()));
        b.txtReviews.setText("(" + product.getReviewsCount() + ")");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // -----------------------------
    // 🔹 Actualización con DiffUtil
    // -----------------------------
    public void setProducts(List<Product> newProducts) {
        if (newProducts == null) newProducts = new ArrayList<>();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.products, newProducts));
        this.products.clear();
        this.products.addAll(newProducts);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addProduct(Product product) {
        products.add(0, product);
        notifyItemInserted(0);
    }

    public void updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (product.getId() != null && product.getId().equals(products.get(i).getId())) {
                products.set(i, product);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public void removeProduct(long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() != null && products.get(i).getId() == id) {
                products.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void updateProductImages(long id, String imageUrl) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getId() != null && p.getId() == id) {
                p.setImageUrls(imageUrl != null ? List.of(imageUrl) : new ArrayList<>());
                notifyItemChanged(i);
                return;
            }
        }
    }

    // -----------------------------
    // 🔹 ViewHolder
    // -----------------------------
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductPopularBinding binding;

        public ViewHolder(@NonNull ItemProductPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // -----------------------------
    // 🔹 DiffUtil.Callback
    // -----------------------------
    private static class DiffCallback extends DiffUtil.Callback {
        private final List<Product> oldList;
        private final List<Product> newList;

        public DiffCallback(List<Product> oldList, List<Product> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }

        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Long oldId = oldList.get(oldItemPosition).getId();
            Long newId = newList.get(newItemPosition).getId();
            return oldId != null && oldId.equals(newId);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Product oldP = oldList.get(oldItemPosition);
            Product newP = newList.get(newItemPosition);
            return oldP.getName().equals(newP.getName()) &&
                    oldP.getPrice() == newP.getPrice() &&
                    oldP.getOldPrice() == newP.getOldPrice() &&
                    oldP.getImageUrls().equals(newP.getImageUrls()) &&
                    oldP.isNewProduct() == newP.isNewProduct();
        }

    }
}
