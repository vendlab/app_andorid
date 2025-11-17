package com.marlodev.app_android.adapter.client;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ItemTagBinding;
import com.marlodev.app_android.domain.TagsModel;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<TagsModel> items;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnTagClickListener {
        void onTagClick(TagsModel tag, int position);
    }

    private OnTagClickListener listener;

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

    public TagAdapter(List<TagsModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTagBinding binding = ItemTagBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TagViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {

        private final ItemTagBinding binding;

        public TagViewHolder(@NonNull ItemTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TagsModel tag, int position) {

            binding.titleTxt.setText(tag.getTitle());

            boolean isSelected = (selectedPosition == position);

            // Asignar estilos una sola vez por bind
            binding.titleTxt.setBackgroundResource(
                    isSelected ? R.drawable.bg_btn_green_prmary_select
                            : R.drawable.bg_btn_white_50_opacity
            );

            binding.titleTxt.setTextColor(
                    ContextCompat.getColor(itemView.getContext(),
                            isSelected ? R.color.colorWhite : R.color.colorGrey800)
            );

            binding.getRoot().setOnClickListener(v -> {

                int adapterPos = getBindingAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) return;

                int oldPosition = selectedPosition;
                selectedPosition = adapterPos;

                // Optimización: solo refrescar si cambió realmente
                if (oldPosition != RecyclerView.NO_POSITION)
                    notifyItemChanged(oldPosition);

                notifyItemChanged(selectedPosition);

                if (listener != null) {
                    listener.onTagClick(tag, adapterPos);
                }

            });

        }
    }
}
