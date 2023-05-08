package com.example.itda.ui.mypage;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.util.ArrayList;

public class MyPageBookmarkCollaboRvAdapter extends RecyclerView.Adapter<MyPageBookmarkCollaboRvAdapter.CustomBookmarkCollaboViewHolder> {

    private ArrayList<MyPageBookmarkCollaboData> Collabos; // 협업 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private Context mContext;

    // Constructor
    public MyPageBookmarkCollaboRvAdapter(Context context, ArrayList<MyPageBookmarkCollaboData> collabos){
        this.mContext = context;
        this.Collabos = collabos;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomBookmarkCollaboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bookmark_collabo, parent, false);

        return new CustomBookmarkCollaboViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomBookmarkCollaboViewHolder holder, int position) {
        MyPageBookmarkCollaboData collabo = Collabos.get(position);  // 현재 position의 가게 정보

        // 앞 가게 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(collabo.getPrvStoreImagePath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.prvStoreImage);        // 이미지를 보여줄 View를 지정

        // 뒷 가게 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(collabo.getPostStoreImagePath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.postStoreImage);        // 이미지를 보여줄 View를 지정

        holder.prvStoreName.setText(collabo.getPrvStoreName());   // 앞 가게 이름 SET
        holder.postStoreName.setText(collabo.getPostStoreName());   // 뒷 가게 이름 SET

        holder.prvStoreDiscountCondition.setText(collabo.getPrvDiscountCondition() + "원 이상 결제");   // 앞 가게 할인 조건
        holder.postStoreDiscountRate.setText(collabo.getPostDiscountRate() + "% 할인");   // 뒷 가게 할인 율

        // 거리가 10m 이상인 경우만 거리 표시
        if(collabo.getDistance() <= 0.01){
            holder.distance.setText("10m 이내");
        }else{
            holder.distance.setText(String.format("%.2f", collabo.getDistance()) + "km");
        }
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Collabos.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomBookmarkCollaboViewHolder extends RecyclerView.ViewHolder {
        ImageView prvStoreImage;    // 앞 가게 이미지
        ImageView postStoreImage;    // 뒷 가게 이미지
        TextView prvStoreName;      // 앞 가게 명
        TextView postStoreName;  // 뒷 가게 명
        TextView prvStoreDiscountCondition; // 앞 가게 할인 조건
        TextView postStoreDiscountRate;      // 뒷 가게 할인 율
        TextView distance;      // 가게 간 거리

        public CustomBookmarkCollaboViewHolder(@NonNull View itemView) {
            super(itemView);
            prvStoreImage = itemView.findViewById(R.id.mypage_bookmark_collabo_prv_store_image);
            postStoreImage = itemView.findViewById(R.id.mypage_bookmark_collabo_post_store_image);
            prvStoreName = itemView.findViewById(R.id.mypage_bookmark_collabo_prv_store_name);
            postStoreName = itemView.findViewById(R.id.mypage_bookmark_collabo_post_store_name);
            prvStoreDiscountCondition = itemView.findViewById(R.id.mypage_bookmark_collabo_prv_store_condition);
            postStoreDiscountRate = itemView.findViewById(R.id.mypage_bookmark_collabo_post_store_discount);
            distance = itemView.findViewById(R.id.mypage_bookmark_collabo_distance);
        }
    }
}