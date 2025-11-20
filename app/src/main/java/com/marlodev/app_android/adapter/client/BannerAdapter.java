package com.marlodev.app_android.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
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

    /**
     * Constructor que encapsula la lógica de configuración del ViewPager.
     * Esta arquitectura previene condiciones de carrera en el layout inicial.
     * @param context Contexto de la aplicación.
     * @param viewPager2 La instancia de ViewPager2 a configurar.
     */
    public BannerAdapter(Context context, ViewPager2 viewPager2) {
        this.context = context;
        setupViewPager(viewPager2);
    }

    private void setupViewPager(ViewPager2 viewPager2) {
        // Configuración que se aplica al ViewPager ANTES de que se le asigne el adaptador.
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(dpToPx(4)));
        transformer.addTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setScaleX(0.85f + (1 - absPos) * 0.15f);
            page.setScaleY(0.90f + (1 - absPos) * 0.10f);
            page.setAlpha(0.65f + (1 - absPos) * 0.35f);
            page.setTranslationZ((1 - absPos) * 100f);

            View cardContainer = page.findViewById(R.id.cardContainer);
            if (cardContainer instanceof MaterialCardView) {
                ((MaterialCardView) cardContainer).setCardElevation(6f + (1 - absPos) * 6f);
            }
        });
        viewPager2.setPageTransformer(transformer);

        // Corrección post-layout para el RecyclerView interno.
        viewPager2.post(() -> {
            if (viewPager2.getChildCount() > 0) {
                View child = viewPager2.getChildAt(0);
                if (child instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) child;
                    recyclerView.setClipToPadding(false);
                    recyclerView.setClipChildren(false);
                    recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                    recyclerView.setPadding(0, 0, 0, 0); // Clave para evitar el "doble padding"
                }
            }
        });
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.clickListener = listener;
    }

    public void setSliderItems(List<Banner> items) {
        sliderItems.clear();
        if (items != null) {
            sliderItems.addAll(items);
        }
        notifyDataSetChanged();
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
            return new SkeletonViewHolder(ItemBannerSkeletonBinding.inflate(inflater, parent, false));
        } else {
            return new BannerViewHolder(ItemSliderBinding.inflate(inflater, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            ((BannerViewHolder) holder).bind(sliderItems.get(position), clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
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
                if (listener != null && !banner.isSkeleton()) {
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
