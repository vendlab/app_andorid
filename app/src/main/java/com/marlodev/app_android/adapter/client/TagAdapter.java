package com.marlodev.app_android.adapter.client;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marlodev.app_android.R;
import com.marlodev.app_android.domain.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter profesional para RecyclerView de Tags.
 * - Maneja clics
 * - Maneja lista dinámica de Tags
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private final List<Tag> tagList = new ArrayList<>();
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        this.listener = listener;
    }

    // -----------------------------
    // Adapter Methods
    // -----------------------------
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.bind(tag);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    // -----------------------------
    // Actualización de lista
    // -----------------------------
    public void setTags(List<Tag> tags) {
        tagList.clear();
        if (tags != null) tagList.addAll(tags);
        notifyDataSetChanged();
    }

    public Tag getTagAt(int position) {
        return tagList.get(position);
    }

    // -----------------------------
    // ViewHolder
    // -----------------------------
    class TagViewHolder extends RecyclerView.ViewHolder {

        private final TextView textTagName;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            textTagName = itemView.findViewById(R.id.tagTitle);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTagClick(tagList.get(getAdapterPosition()));
                }
            });
        }

        void bind(Tag tag) {
            textTagName.setText(tag.getName());
        }
    }
}
