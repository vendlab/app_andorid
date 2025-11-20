package com.marlodev.app_android.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemBannerSkeletonBinding;
import com.marlodev.app_android.databinding.ItemSliderBinding;
import com.marlodev.app_android.domain.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Banner> sliderItems = new ArrayList<>();
    private final Context context;
    private OnBannerClickListener clickListener;

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_SKELETON = 1;

    public BannerAdapter(Context context) {
        this.context = context;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.clickListener = listener;
    }

    public void setSliderItems(List<Banner> items) {
        sliderItems.clear();
        if (items != null) {
            sliderItems.addAll(items);
        }
        notifyDataSetChanged(); // Notificar al adaptador de los cambios
    }

    @Override
    public int getItemViewType(int position) {
        return sliderItems.get(position).isSkeleton() ? VIEW_TYPE_SKELETON : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_SKELETON) {
            ItemBannerSkeletonBinding skeletonBinding = ItemBannerSkeletonBinding.inflate(inflater, parent, false);
            return new SkeletonViewHolder(skeletonBinding);
        } else {
            ItemSliderBinding binding = ItemSliderBinding.inflate(inflater, parent, false);
            return new BannerViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            Banner banner = sliderItems.get(position);
            bannerViewHolder.bind(banner, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    // --- ViewHolders ---

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemSliderBinding binding;

        public BannerViewHolder(@NonNull ItemSliderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Banner banner, OnBannerClickListener listener) {
            Glide.with(itemView.getContext())
                    .load(banner.getUrl())
                    .centerCrop()
                    .placeholder(R.color.colorGrey400)
                    .error(R.color.colorGrey400)
                    .into(binding.imageSlide);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBannerClick(banner);
                }
            });
        }
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        public SkeletonViewHolder(ItemBannerSkeletonBinding binding) {
            super(binding.getRoot());
        }
    }

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
}
