package com.marlodev.app_android.adapter;

// Importaciones necesarias
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemTagBinding;
import com.marlodev.app_android.domain.TagsModel;

import java.util.ArrayList;

// Adapter para mostrar los tags (categorías, etiquetas, etc.) en un RecyclerView
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.viewHolder> {

    private ArrayList<TagsModel> items; // Lista de items (tags) a mostrar
    private Context context; // Contexto de la Activity/Fragment
    private int selectedPosition = -1; // Posición actualmente seleccionada
    private int lastSelectedPosition = -1; // Posición previamente seleccionada

    // Constructor del adapter recibe la lista de tags
    public TagAdapter(ArrayList<TagsModel> items) {
        this.items = items;
    }

    // Inflar el layout de cada item y crear un ViewHolder
    @NonNull
    @Override
    public TagAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext(); // Obtener contexto del parent
        // Inflamos el layout usando ViewBinding
        ItemTagBinding binding = ItemTagBinding.inflate(LayoutInflater.from(context), parent, false);
        return new viewHolder(binding); // Devolvemos el ViewHolder
    }

    // Vincular los datos a cada ViewHolder
    @Override
    public void onBindViewHolder(@NonNull TagAdapter.viewHolder holder, int position) {
        // Seteamos el título del tag en el TextView
        holder.binding.titleTxt.setText(items.get(position).getTitle());

        // Listener para el click en el tag
        holder.binding.getRoot().setOnClickListener(v -> {
            // Obtenemos la posición actual de forma segura
            int currentPosition = holder.getBindingAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return; // Salir si el ViewHolder no tiene posición

            // Actualizamos las posiciones seleccionadas
            lastSelectedPosition = selectedPosition; // Guardamos la anterior
            selectedPosition = currentPosition; // Actualizamos con la nueva
            // Notificamos al RecyclerView que estas posiciones han cambiado para que se redibujen
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        // Cambiar apariencia del tag según si está seleccionado o no
        if(selectedPosition == position){
            // Tag seleccionado: fondo verde y texto blanco
            holder.binding.titleTxt.setBackgroundResource(R.drawable.bg_btn_green_prmary_select);
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else {
            // Tag no seleccionado: fondo blanco semitransparente y texto gris
            holder.binding.titleTxt.setBackgroundResource(R.drawable.bg_btn_white_50_opacity);
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorGrey800));
        }
    }

    // Retornar el tamaño de la lista
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Clase interna ViewHolder que contiene las referencias de ViewBinding para cada item
    public class viewHolder extends RecyclerView.ViewHolder {
        ItemTagBinding binding; // ViewBinding del item
        public viewHolder(ItemTagBinding binding) {
            super(binding.getRoot()); // Pasamos la root view al constructor del ViewHolder
            this.binding = binding; // Guardamos el binding para usarlo en onBindViewHolder
        }
    }
}

