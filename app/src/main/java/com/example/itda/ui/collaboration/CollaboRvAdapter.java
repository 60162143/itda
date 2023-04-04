package com.example.itda.ui.collaboration;

import android.content.Context;
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

    private ArrayList<collaboData> Collaboes = new ArrayList<>();   // 협업 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    public CollaboRvAdapter(Context context, ArrayList<collaboData> collabos){
        this.mContext = context;
        this.Collaboes = collabos;
    }

    @NonNull
    @Override
    public CollaboRvAdapter.CustomCollaboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_collabo, parent, false);
        return new CustomCollaboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaboRvAdapter.CustomCollaboViewHolder holder, int position) {
        collaboData collabo = Collaboes.get(position);
        //앞가게
        holder.prv_store_name.setText(collabo.getPrvStoreName());
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPrvStoreImagePath()))
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)
                .fallback(R.drawable.ic_fallback)
                .into(holder.prv_store_image);
        holder.prv_store_discount_condition.setText(collabo.getPrvDiscountCondition() + "원 이상 결제");

        //뒷가게
        holder.post_store_name.setText(collabo.getPostStoreName());
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPostStoreImagePath()))
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)
                .fallback(R.drawable.ic_fallback)
                .into(holder.post_store_image);
        holder.post_store_discount_rate.setText(collabo.getPostDiscountRate() + "% 할인");

        // 가게 간 거리
        holder.distance.setText(collabo.getCollaboDistance() + " km");
    }

    @Override
    public int getItemCount() {
        return Collaboes.size();
    }

    public class CustomCollaboViewHolder extends RecyclerView.ViewHolder {
        ImageButton prv_store_image;
        ImageButton post_store_image;
        TextView prv_store_name;
        TextView post_store_name;
        TextView prv_store_discount_condition;
        TextView post_store_discount_rate;
        TextView distance;

        public CustomCollaboViewHolder(@NonNull View itemView) {
            super(itemView);
            prv_store_image = itemView.findViewById(R.id.collabo_prv_store_image);
            post_store_image = itemView.findViewById(R.id.collabo_post_store_image);
            prv_store_name = itemView.findViewById(R.id.collabo_prv_store_name);
            post_store_name = itemView.findViewById(R.id.collabo_post_store_name);
            prv_store_discount_condition = itemView.findViewById(R.id.collabo_prv_store_discount_condition);
            post_store_discount_rate = itemView.findViewById(R.id.collabo_post_store_discount_rate);
            distance = itemView.findViewById(R.id.collabo_distance);
        }
    }
}
