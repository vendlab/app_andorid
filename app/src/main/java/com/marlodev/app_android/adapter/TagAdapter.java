package com.marlodev.app_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import com.marlodev.app_android.R;
import com.marlodev.app_android.databinding.ViewholderTagBinding;
import com.marlodev.app_android.domain.TagsModel;

import java.util.ArrayList;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.viewHolder> {

    private ArrayList<TagsModel> items;
    private Context context;
    private int selectedPosition = -1;
    private  int lastSelectedPosition = -1;

    public TagAdapter(ArrayList<TagsModel> items) {
        this.items = items;
    }



      @NonNull
    @Override
    public TagAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderTagBinding binding = ViewholderTagBinding.inflate(LayoutInflater.from(context), parent    , false);
        return new viewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.viewHolder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getTitle());
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);
            }
        });

           if(selectedPosition == position){
            holder.binding.titleTxt.setBackgroundResource(R.drawable.bg_btn_green_prmary_select);
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else {
            holder.binding.titleTxt.setBackgroundResource(R.drawable.bg_btn_white_50_opacity);
            holder.binding.titleTxt.setTextColor(ContextCompat.getColor(context, R.color.colorGrey800));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ViewholderTagBinding binding;
        public viewHolder(ViewholderTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
