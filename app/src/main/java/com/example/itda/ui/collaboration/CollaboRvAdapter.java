package com.example.itda.ui.collaboration;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.util.ArrayList;

public class CollaboRvAdapter extends RecyclerView.Adapter<CollaboRvAdapter.CustomCollaboViewHolder>{

    public ArrayList<collaboData> collaboes = new ArrayList<>();

    @NonNull
    @Override
    public CollaboRvAdapter.CustomCollaboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collabo, parent, false);
        return new CustomCollaboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaboRvAdapter.CustomCollaboViewHolder holder, int position) {
        collaboData collabo = collaboes.get(position);
        //앞가게
        holder.front_store_name.setText(collabo.getFrontStoreName());
        Glide.with(holder.itemView).load(Uri.parse(collabo.getFrontStoreThumbnail())).error(R.drawable.ic_error).fallback(R.drawable.ic_fallback).into(holder.front_store_image);
        holder.discount_conditional.setText(collabo.getDiscountConditional());

        //뒷가게
        holder.back_store_name.setText(collabo.getBackStoreName());
        Glide.with(holder.itemView).load(Uri.parse(collabo.getBackStoreThumbnail())).error(R.drawable.ic_error).fallback(R.drawable.ic_fallback).into(holder.back_store_image);
        holder.discount.setText(collabo.getDiscount());
    }

    @Override
    public int getItemCount() {
        return collaboes.size();
    }

    public void setCollaboes(ArrayList<collaboData> collaboes){
        this.collaboes = collaboes;
    }

    public class CustomCollaboViewHolder extends RecyclerView.ViewHolder {
        ImageButton front_store_image;
        ImageButton back_store_image;
        TextView front_store_name;
        TextView back_store_name;
        TextView discount_conditional;
        TextView discount;

        public CustomCollaboViewHolder(@NonNull View itemView) {
            super(itemView);
            front_store_image = itemView.findViewById(R.id.front_store_image);
            back_store_image = itemView.findViewById(R.id.back_store_image);
            front_store_name = itemView.findViewById(R.id.front_store_name);
            back_store_name = itemView.findViewById(R.id.back_store_name);
            discount_conditional = itemView.findViewById(R.id.discount_conditional);
            discount = itemView.findViewById(R.id.discount);
        }
    }
}
