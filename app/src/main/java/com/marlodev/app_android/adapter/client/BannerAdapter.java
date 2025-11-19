package com.marlodev.app_android.adapter.client;

import android.content.Context;
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

/**
 * Adapter para mostrar banners en un ViewPager2 con efecto 3D y sombra pronunciada en el centro.
 * Auto-scroll desactivado por defecto.
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.SliderViewHolder> {

    // Lista de banners
    private final List<Banner> sliderItems = new ArrayList<>();
    private final ViewPager2 viewPager2;
    private final Context context;

    // Listener para clicks en banners
    private OnBannerClickListener clickListener;

    /**
     * Constructor del adapter
     * @param context contexto de la activity/fragment
     * @param viewPager2 el ViewPager2 que mostrará los banners
     */
    public BannerAdapter(Context context, ViewPager2 viewPager2) {
        this.context = context;
        this.viewPager2 = viewPager2;

        // Configura ViewPager2 (peek effect, 3D y sombra)
        setupViewPager();
    }

    /**
     * Configuración de ViewPager2 para peek effect, escala y sombra pronunciada en el centro.
     */
    private void setupViewPager() {
        // Permitir que las páginas se muestren parcialmente
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);

        // Distancias en px según densidad
        final float offsetPx = 40 * context.getResources().getDisplayMetrics().density;

        // Transformación de página para efecto 3D y profundidad
        viewPager2.setPageTransformer((page, position) -> {
            // Movimiento horizontal
            page.setTranslationX(-offsetPx * position);

            // Escala vertical
            float scale = 0.85f + (1 - Math.abs(position)) * 0.15f;
            page.setScaleY(scale);

            // Opacidad
            page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f);

            // Profundidad Z
            page.setZ(-Math.abs(position));

            // Movimiento vertical para efecto detrás
            page.setTranslationY(Math.abs(position) * 30);

            // -------------------------------
            // Sombra pronunciada para la página central
            // -------------------------------
            if (Math.abs(position) < 0.5f) {
                page.setElevation(40f);       // sombra más pronunciada
                page.setTranslationZ(40f);
            } else {
                page.setElevation(0f);
                page.setTranslationZ(0f);
            }
        });
    }

    /**
     * Inflar layout de item
     */
    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    /**
     * Bind de cada banner
     */
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Banner banner = sliderItems.get(position);
        holder.setImage(banner);

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onBannerClick(banner);
        });
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    /**
     * Actualiza los banners y simula loop infinito duplicando la lista
     */
    public void setSliderItems(List<Banner> items) {
        sliderItems.clear();
        if (items != null && !items.isEmpty()) {
            sliderItems.addAll(items);
            sliderItems.addAll(items); // duplicamos para simular loop infinito
        }
        notifyDataSetChanged();

        // Iniciar en el medio para efecto loop
        if (!sliderItems.isEmpty()) {
            viewPager2.setCurrentItem(sliderItems.size() / 2, false);
        }
    }

    /**
     * Configura el listener para clicks en banners
     */
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

        /**
         * Carga la imagen del banner usando Glide
         */
        void setImage(Banner banner) {
            Glide.with(context)
                    .load(banner.getUrl())
                    .centerCrop()
//                    .placeholder(R.drawable.banner_placeholder) // placeholder opcional
//                    .error(R.drawable.banner_error) // error opcional
                    .into(imageView);
        }
    }

    /**
     * Interface para clicks en banners
     */
    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }
}
