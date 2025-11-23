package com.marlodev.app_android.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marlodev.app_android.R;
import com.marlodev.app_android.viewmodel.ItemInventario;

import java.util.List;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<ItemInventario> inventarioList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onSolicitarReposicion(ItemInventario item);
        void onVerDetalles(ItemInventario item);
    }

    public InventarioAdapter(List<ItemInventario> inventarioList, OnItemClickListener listener) {
        this.inventarioList = inventarioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventario_card, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        ItemInventario item = inventarioList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return inventarioList.size();
    }

    static class InventarioViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconAlerta;
        private TextView tvNombre;
        private TextView tvUltimoReabastecimiento;
        private TextView tagAlerta;
        private TextView tvStockActual;
        private TextView tvStockMinimo;
        private ProgressBar progressBar;
        private Button btnSolicitarReposicion;
        private Button btnDetalles;

        public InventarioViewHolder(@NonNull View itemView) {
            super(itemView);
            iconAlerta = itemView.findViewById(R.id.icon_alerta);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvUltimoReabastecimiento = itemView.findViewById(R.id.tv_ultimo_reabastecimiento);
            tagAlerta = itemView.findViewById(R.id.tag_alerta);
            tvStockActual = itemView.findViewById(R.id.tv_stock_actual);
            tvStockMinimo = itemView.findViewById(R.id.tv_stock_minimo);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnSolicitarReposicion = itemView.findViewById(R.id.btn_solicitar_reposicion);
            btnDetalles = itemView.findViewById(R.id.btn_detalles);
        }

        public void bind(ItemInventario item, OnItemClickListener listener) {
            tvNombre.setText(item.getNombre());
            tvUltimoReabastecimiento.setText(item.getUltimoReabastecimiento());
            tvStockActual.setText(item.getStockActualFormateado());
            tvStockMinimo.setText(item.getStockMinimoFormateado());

            // Configurar progreso
            int porcentaje = item.getPorcentajeStock();
            progressBar.setProgress(porcentaje);

            // Configurar alerta según el tipo
            if (item.getTipoAlerta() == TipoAlerta.CRITICO) {
                iconAlerta.setColorFilter(itemView.getContext().getColor(R.color.colorCritico));
                tagAlerta.setText("Crítico");
                tagAlerta.setBackgroundColor(itemView.getContext().getColor(R.color.colorCritico));
                tagAlerta.setTextColor(itemView.getContext().getColor(android.R.color.white));
                progressBar.setProgressTintList(
                        itemView.getContext().getColorStateList(R.color.colorCritico)
                );
            } else if (item.getTipoAlerta() == TipoAlerta.ADVERTENCIA) {
                iconAlerta.setColorFilter(itemView.getContext().getColor(R.color.colorAdvertencia));
                tagAlerta.setText("Advertencia");
                tagAlerta.setBackgroundColor(itemView.getContext().getColor(R.color.colorAdvertencia));
                tagAlerta.setTextColor(itemView.getContext().getColor(R.color.colorBlack));
                progressBar.setProgressTintList(
                        itemView.getContext().getColorStateList(R.color.colorAdvertencia)
                );
            }

            // Listeners de botones
            btnSolicitarReposicion.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSolicitarReposicion(item);
                }
            });

            btnDetalles.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerDetalles(item);
                }
            });
        }
    }
}
