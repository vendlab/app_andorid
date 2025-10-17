package com.marlodev.app_android.adapter;

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
import com.marlodev.app_android.domain.BannerModel;

import java.util.ArrayList;

/**
 * Adapter para manejar un ViewPager2 que muestra banners (sliders) en la app.
 * Permite deslizar horizontalmente entre imágenes y hacer un loop infinito.
 */
public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private ArrayList<BannerModel> sliderItems; // Lista de banners a mostrar
    private ViewPager2 viewPager2;              // ViewPager2 que contiene este adapter
    private Context context;                     // Contexto para inflar layouts y cargar imágenes



    // Runnable que permite repetir el slider (loop infinito)
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Duplica la lista de banners para continuar el loop
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged(); // Notifica al adapter que la lista cambió
        }
    };

    // Constructor del adapter
    public SliderAdapter(ArrayList<BannerModel> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    // --------------------------
    // 🔹 Inflado del item del slider
    // --------------------------
    @NonNull
    @Override
    public SliderAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Guardamos contexto para Glide
        // Inflamos el layout item_slider.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    // --------------------------
    // 🔹 Bind de datos al item
    // --------------------------
    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewHolder holder, int position) {
        // Seteamos la imagen del banner actual
        holder.setImage(sliderItems.get(position));

        // Si estamos cerca del final, ejecutamos el runnable para duplicar la lista y simular loop infinito
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    // --------------------------
    // 🔹 ViewHolder del slider
    // --------------------------
    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Obtenemos referencia a ImageView dentro del layout item_slider.xml
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        /**
         * Setea la imagen del banner usando Glide.
         * @param bannerModel Banner a mostrar
         */
        void setImage(BannerModel bannerModel) {
            Glide.with(context)
                    .load(bannerModel.getUrl()) // Carga la URL de la imagen
                    .into(imageView);          // Inserta la imagen en el ImageView
        }
    }
}
















