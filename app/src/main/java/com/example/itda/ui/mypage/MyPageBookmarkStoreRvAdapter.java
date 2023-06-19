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
import java.util.Locale;

public class MyPageBookmarkStoreRvAdapter extends RecyclerView.Adapter<MyPageBookmarkStoreRvAdapter.CustomStoreViewHolder> {

    private final ArrayList<myPageBookmarkStoreData> Stores; // 가게 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    private static onMyPageBookmarkStoreRvClickListener rvClickListener = null;

    // Constructor
    public MyPageBookmarkStoreRvAdapter(Context context, onMyPageBookmarkStoreRvClickListener clickListener, ArrayList<myPageBookmarkStoreData> stores){
        this.mContext = context;
        rvClickListener = clickListener;
        this.Stores = stores;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_mypage_bookmark_store, parent, false);

        return new CustomStoreViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomStoreViewHolder holder, int position) {
        myPageBookmarkStoreData store = Stores.get(position);  // 현재 position의 가게 정보

        // 가게 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(store.getStoreImagePath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.bookmarkStoreImage);        // 이미지를 보여줄 View를 지정

        // 가게 이름 SET
        holder.bookmarkStoreName.setText(store.getStoreName());

        // 가게 거리 SET
        // 거리가 10m 이상인 경우만 거리 표시
        String bookmarkStoreDistanceTxt;
        if(store.getStoreDistance() <= 0.01){
            bookmarkStoreDistanceTxt = "10m 이내";
        }else{
            bookmarkStoreDistanceTxt = String.format(Locale.getDefault(), "%.2f", store.getStoreDistance()) + "km";
        }
        holder.bookmarkStoreDistance.setText(bookmarkStoreDistanceTxt);

        // 가게 별점 SET
        String bookmarkStoreStarScoreTxt = String.format(Locale.getDefault(), "%.1f", store.getStoreScore());
        holder.bookmarkStoreStarScore.setText(bookmarkStoreStarScoreTxt);

        // 가게 간단 정보 SET
        holder.bookmarkStoreInfo.setText(store.getStoreInfo());

        // 해시태그 SET
        holder.bookmarkStoreHashTag.setText(store.getStoreHashTag());
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Stores.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomStoreViewHolder extends RecyclerView.ViewHolder {
        ImageView delBtn;   // 찜한 목록 삭제 버튼
        ImageView bookmarkStoreImage;   // 가게 썸네일
        TextView bookmarkStoreName;     // 가게 명
        TextView bookmarkStoreDistance;     // 현재 위치에서 가게까지의 거리
        TextView bookmarkStoreStarScore;    // 가게 별점
        TextView bookmarkStoreInfo;     // 가게 간단 정보
        TextView bookmarkStoreHashTag;  // 가게 해시태그

        public CustomStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            delBtn = itemView.findViewById(R.id.mypage_bookmark_store_del_btn);
            bookmarkStoreImage = itemView.findViewById(R.id.bookmark_store_image);
            bookmarkStoreName = itemView.findViewById(R.id.bookmark_store_name);
            bookmarkStoreDistance = itemView.findViewById(R.id.bookmark_store_distance);
            bookmarkStoreStarScore = itemView.findViewById(R.id.bookmark_star_score);
            bookmarkStoreInfo = itemView.findViewById(R.id.bookmark_store_info);
            bookmarkStoreHashTag = itemView.findViewById(R.id.bookmark_store_hashTag);

            // 찜버튼 클릭 리스너
            delBtn.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onMyPageBookmarkStoreRvClick(view, getAbsoluteAdapterPosition(), "delete");
                }
            });

            // 가게 이미지 클릭 리스너
            bookmarkStoreImage.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onMyPageBookmarkStoreRvClick(view, getAbsoluteAdapterPosition(), "storeImage");
                }
            });
        }
    }
}