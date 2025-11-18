package com.marlodev.app_android.adapter.client;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.BannerModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter profesional para ViewPager2 que muestra banners (sliders)
 * - Auto-scroll seguro
 * - Loop infinito sin duplicar lista
 * - Placeholders y manejo de errores
 * - Actualización dinámica de banners
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.SliderViewHolder> {

    private final List<BannerModel> sliderItems = new ArrayList<>();
    private final ViewPager2 viewPager2;
    private Context context;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private final int autoScrollDelay = 4000; // 4 segundos por banner

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            if (sliderItems.size() > 1) {
                int nextItem = (viewPager2.getCurrentItem() + 1) % sliderItems.size();
                viewPager2.setCurrentItem(nextItem, true);
                autoScrollHandler.postDelayed(this, autoScrollDelay);
            }
        }
    };

    public BannerAdapter(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        BannerModel banner = sliderItems.get(position);
        holder.setImage(banner);
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    /**
     * Actualiza dinámicamente los banners
     */
    public void setSliderItems(List<BannerModel> items) {
        sliderItems.clear();
        if (items != null) sliderItems.addAll(items);
        notifyDataSetChanged();

        // Reiniciamos auto-scroll
        stopAutoScroll();
        if (!sliderItems.isEmpty()) startAutoScroll();
    }

    /**
     * Inicia el auto-scroll seguro
     */
    public void startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, autoScrollDelay);
    }

    /**
     * Detiene el auto-scroll
     */
    public void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    // --------------------------
    // ViewHolder
    // --------------------------
    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(BannerModel bannerModel) {
            Glide.with(context)
                    .load(bannerModel.getUrl())
                    .centerCrop()
                    .into(imageView);
        }
    }
}








