package com.example.itda.ui.home;

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

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class CategoryRvAdapter extends RecyclerView.Adapter<CategoryRvAdapter.CustomCategoryViewHolder> {

    private final ArrayList<mainCategoryData> Categories;   // 카테고리 데이터

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onMainStoreRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public CategoryRvAdapter(Context context
            , ArrayList<mainCategoryData> categories) {
        this.mContext = context;
        this.Categories = categories;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CategoryRvAdapter.CustomCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_main_category, parent, false);

        return new CustomCategoryViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CategoryRvAdapter.CustomCategoryViewHolder holder, int position) {
        mainCategoryData category = Categories.get(position);   // 현재 position의 카테고리 정보

        // 카테고리 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(category.getImagePath()))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.categoryImage);      // 이미지를 보여줄 View를 지정

        // 카테고리 명 SET
        holder.categoryName.setText(category.getCategoryNm());
    }

    // 리사이클러뷰 클릭 리스너 SET
    public void setonMainStoreRvClickListener(onMainStoreRvClickListener rvClickListener) {
        CategoryRvAdapter.rvClickListener = rvClickListener;
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Categories.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;    // 카테고리 이미지
        TextView categoryName;      // 카테고리 명

        public CustomCategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);

            // 리사이클러뷰 이미지 클릭 이벤트 인터페이스 구현
            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onMainStoreRvClick(view, getAbsoluteAdapterPosition(), "image");
                }
            });
        }
    }
}