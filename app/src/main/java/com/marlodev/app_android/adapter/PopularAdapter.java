package com.marlodev.app_android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ViewholderPopularBinding;
import com.marlodev.app_android.domain.Product;

import java.util.ArrayList;
import java.util.Locale;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    ArrayList<Product> products;
    Context context;

    public PopularAdapter(ArrayList<Product> products) {
        this.products = products;
    }


    @NonNull
    @Override
    public PopularAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return  new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.Viewholder holder, int position) {



        Product product = products.get(position);
        Log.d("PopularAdapter", "Producto: " + product.getTitle() + " | isNew: " + product.isNew());

        holder.binding.txtNuevo.setVisibility(product.isNew() ? View.VISIBLE : View.GONE);

        holder.binding.txtRanking.setText(String.valueOf(products.get(position).getRating()));
        holder.binding.txtReviews.setText("(" + String.valueOf(products.get(position).getReviewsCount()) + ")");
        holder.binding.txtTitle.setText(products.get(position).getTitle());
        holder.binding.txtPrece.setText("S/." + String.valueOf(products.get(position).getPrice()));
        holder.binding.txtOldPrece.setText("S/." + String.valueOf(products.get(position).getOldPrice()));


        RequestOptions options = new RequestOptions();
        options = options.transform(new CenterInside());


        Glide.with(context)
                .load(products.get(position).getImages().get(0).getUrl()) // 👈 aquí va el .getUrl()
                .apply(new RequestOptions().transform(new CenterInside()))
                .into(holder.binding.pic);




    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderPopularBinding binding;
        public Viewholder( ViewholderPopularBinding  binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
