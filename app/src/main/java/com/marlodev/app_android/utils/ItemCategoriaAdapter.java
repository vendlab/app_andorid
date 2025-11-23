package com.marlodev.app_android.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.viewmodel.ItemCategoriaViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemCategoriaAdapter extends RecyclerView.Adapter<ItemCategoriaAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<ItemCategoriaViewModel> itemList;
    private List<ItemCategoriaViewModel> itemBusqueda;

    public ItemCategoriaAdapter(Context context, List<ItemCategoriaViewModel> itemList) {
        this.context = context;
        this.itemList = itemList;


        this.itemBusqueda = new ArrayList<>(itemList);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView titleText;
        TextView descriptionText;
        TextView statusText;
        TextView productsCount;

        TextView dateText;

        CardView editButton;
        CardView deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImage);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            statusText = itemView.findViewById(R.id.statusText);
            productsCount = itemView.findViewById(R.id.productsCount);

            dateText = itemView.findViewById(R.id.dateText);

            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);


        }

    }
    @Override
    public ItemCategoriaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_admin_categorias, null);
        return new ItemCategoriaAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemCategoriaAdapter.ViewHolder holder, int position) {
        ItemCategoriaViewModel item = itemList.get(position);

        holder.iconImage.setImageResource(item.getIcon());
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(item.getDescription());
        holder.statusText.setText(item.getStatus());
        holder.productsCount.setText(item.getStatus());
        holder.dateText.setText(item.getCreateAt());
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para editar
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para eliminar
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();

    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                List<ItemCategoriaViewModel> itemBusqueda = new ArrayList<>();


                if (charString.isEmpty()) {
                    itemBusqueda.addAll(itemList) ;
                } else {

                    for (ItemCategoriaViewModel row : itemBusqueda) {

                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase())) {
                           itemBusqueda.add(row);
                        }
                    }

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = itemBusqueda;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemBusqueda = (List<ItemCategoriaViewModel>) results.values;
                notifyDataSetChanged();

            }
        };
    }
}
