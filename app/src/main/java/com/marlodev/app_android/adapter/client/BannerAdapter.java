package com.marlodev.app_android.adapter.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.Banner;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.SliderViewHolder> {

    private final List<Banner> sliderItems = new ArrayList<>();
    private final Context context;
    private final ViewPager2 viewPager2;
    private OnBannerClickListener clickListener;

    public BannerAdapter(Context context, ViewPager2 viewPager2) {
        this.context = context;
        this.viewPager2 = viewPager2;
        setupViewPager();
    }

    private void setupViewPager() {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);

        // Configurar RecyclerView interno
        viewPager2.post(() -> {
            if (viewPager2.getChildCount() > 0) {
                View child = viewPager2.getChildAt(0);
                if (child instanceof RecyclerView) {
                    RecyclerView recyclerView = (RecyclerView) child;
                    recyclerView.setClipToPadding(false);
                    recyclerView.setClipChildren(false);
                    recyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                    recyclerView.setPadding(0, 0, 0, 0);
                }
            }
        });

        CompositePageTransformer transformer = new CompositePageTransformer();

        // OPCIÓN 1: Margen MÍNIMO (2-4dp)
        transformer.addTransformer(new MarginPageTransformer(2));

        transformer.addTransformer((page, position) -> {
            float absPos = Math.abs(position);

            float scaleX = 0.85f + (1 - absPos) * 0.15f;
            float scaleY = 0.90f + (1 - absPos) * 0.10f;
            page.setScaleX(scaleX);
            page.setScaleY(scaleY);

            float alpha = 0.65f + (1 - absPos) * 0.35f;
            page.setAlpha(alpha);

            float elevation = (1 - absPos) * 100f;
            page.setTranslationZ(elevation);

            View cardContainer = page.findViewById(R.id.cardContainer);
            if (cardContainer instanceof MaterialCardView) {
                MaterialCardView card = (MaterialCardView) cardContainer;
                float cardElevation = 6f + (1 - absPos) * 6f;
                card.setCardElevation(cardElevation);
            }
        });

        viewPager2.setPageTransformer(transformer);
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Banner banner = sliderItems.get(position);
        holder.bind(banner);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onBannerClick(banner);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public void setSliderItems(List<Banner> items) {
        sliderItems.clear();
        if (items != null) {
            sliderItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.clickListener = listener;
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void bind(Banner banner) {
            Glide.with(itemView.getContext())
                    .load(banner.getUrl())
                    .centerCrop()
                    .placeholder(R.color.colorGrey400)
                    .error(R.color.colorGrey400)
                    .into(imageView);
        }
    }

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
}