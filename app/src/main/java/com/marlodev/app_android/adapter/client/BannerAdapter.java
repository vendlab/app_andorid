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
import com.marlodev.app_android.domain.Banner;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.SliderViewHolder> {

    private final List<Banner> sliderItems = new ArrayList<>();
    private final ViewPager2 viewPager2;
    private final Context context;

    private final Handler autoScrollHandler = new Handler(Looper.getMainLooper());
    private final int autoScrollDelay = 10000; // 4 segundos
    private Runnable autoScrollRunnable;

    private OnBannerClickListener clickListener;

    public BannerAdapter(Context context, ViewPager2 viewPager2) {
        this.context = context;
        this.viewPager2 = viewPager2;
        setupViewPager();
        initAutoScrollRunnable();
    }

    /** Configura ViewPager2: peek effect + 3D + loop */
    private void setupViewPager() {
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);


        final float pageMarginPx = 20 * context.getResources().getDisplayMetrics().density;
        final float offsetPx = 40 * context.getResources().getDisplayMetrics().density;


        viewPager2.setPageTransformer((page, position) -> {
            page.setTranslationX(-offsetPx * position);
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);
            page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f);

            // Profundidad
            page.setZ(-Math.abs(position));

            // Nueva línea para dar efecto “detrás” a laterales
            page.setTranslationY(Math.abs(position) * 30);
        });
    }

    /** Auto-scroll seguro */
    private void initAutoScrollRunnable() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (sliderItems.size() > 1) {
                    int nextItem = (viewPager2.getCurrentItem() + 1) % sliderItems.size();
                    viewPager2.setCurrentItem(nextItem, true);
                    autoScrollHandler.postDelayed(this, autoScrollDelay);
                }
            }
        };
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
        holder.setImage(banner);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onBannerClick(banner);
        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    /** Actualiza banners con loop infinito simulado */
    public void setSliderItems(List<Banner> items) {
        sliderItems.clear();
        if (items != null && !items.isEmpty()) {
            sliderItems.addAll(items);
            sliderItems.addAll(items); // duplicamos para simular loop infinito
        }
        notifyDataSetChanged();

        if (!sliderItems.isEmpty()) {
            viewPager2.setCurrentItem(sliderItems.size() / 2, false); // empezar en el medio
        }

        stopAutoScroll();
        if (!sliderItems.isEmpty()) startAutoScroll();
    }

    public void startAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, autoScrollDelay);
    }

    public void stopAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    public void destroy() {
        stopAutoScroll();
        autoScrollRunnable = null;
    }

    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.clickListener = listener;
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

        void setImage(Banner banner) {
            Glide.with(context)
                    .load(banner.getUrl())
                    .centerCrop()
//                    .placeholder(R.drawable.banner_placeholder)
//                    .error(R.drawable.banner_error)
                    .into(imageView);
        }
    }

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
}
