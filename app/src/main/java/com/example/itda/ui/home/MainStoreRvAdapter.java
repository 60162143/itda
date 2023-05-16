package com.example.itda.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.info.onInfoCollaboRvClickListener;

import java.util.ArrayList;
import java.util.List;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class MainStoreRvAdapter extends RecyclerView.Adapter<MainStoreRvAdapter.CustomMainCategoryViewHolder>{
    private ArrayList<mainStoreData> Stores;    // 가게 데이터
    private ArrayList<mainBookmarkStoreData> BookmarkStores;    // 찜한 가게 데이터

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onMainStoreRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    // 프래그먼트에서 생성, 리스너는 따로 SET
    public MainStoreRvAdapter(Context context
            , ArrayList<mainStoreData> store
            , ArrayList<mainBookmarkStoreData> bookmarkStore){
        this.mContext = context;
        this.Stores = store;
        this.BookmarkStores = bookmarkStore;
    }

    // 액티비티에서 생성
    public MainStoreRvAdapter(Context context
            , onMainStoreRvClickListener clickListener
            , ArrayList<mainStoreData> store
            , ArrayList<mainBookmarkStoreData> bookmarkStore){
        this.mContext = context;
        rvClickListener = clickListener;
        this.Stores = store;
        this.BookmarkStores = bookmarkStore;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public MainStoreRvAdapter.CustomMainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_main_store, parent, false);

        return new CustomMainCategoryViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull MainStoreRvAdapter.CustomMainCategoryViewHolder holder, int position) {
        mainStoreData store = Stores.get(position);     // 현재 position의 가게 정보

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.mainStoreImage);       // 이미지를 보여줄 View를 지정

        // 로그인이 되어있을 경우 찜 버튼 보이기
        if(((globalMethod) mContext.getApplicationContext()).loginChecked()){
            holder.mainStoreBookmark.setVisibility(View.VISIBLE);

            // 찜한 가게 목록이 있을 경우 찜 버튼 활성화
            if(BookmarkStores != null && BookmarkStores.size() > 0){
                for(int i = 0; i < BookmarkStores.size(); i++){
                    if(store.getStoreId() == BookmarkStores.get(i).getStoreId()){
                        holder.mainStoreBookmark.setSelected(true);

                        break;
                    }
                }
            }
        }

        holder.mainStoreName.setText(store.getStoreName()); // 가게 이름

        holder.mainStoreScore.setText(String.format("%.1f", store.getStoreScore()));   // 가게 별점

        // 거리가 10m 이상인 경우만 거리 표시
        if(store.getStoreDistance() <= 0.01){
            holder.mainStoreDistance.setText("10m 이내");
        }else{
            holder.mainStoreDistance.setText(String.format("%.2f", store.getStoreDistance()) + "km");
        }

        // 가게 리뷰 수
        String reviewCount = " (" + store.getStoreReviewCount() + ")";
        holder.mainStoreReviewCount.setText(reviewCount);

        holder.mainStoreHashTag.setText(store.getStoreHashTag());   // 가게 해시태그
    }

    // 리스너 설정
    public void setonMainStoreRvClickListener(onMainStoreRvClickListener rvClickListener) {
        MainStoreRvAdapter.rvClickListener = rvClickListener;
    }

    // 찜한 가게 목록 설정
    public void setbookmarkStores(ArrayList<mainBookmarkStoreData> bookmarkStores) {
        BookmarkStores = bookmarkStores;
    }

    // 찜한 가게 목록 설정
    public void setStores(ArrayList<mainStoreData> stores) {
        Stores = stores;
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Stores.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomMainCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageButton mainStoreImage;     // 가게 썸네일
        Button mainStoreBookmark; // 가게 찜 버튼
        TextView mainStoreName;         // 가게 이름
        TextView mainStoreDistance;     // 현 위치에서 가게까지의 거리
        TextView mainStoreScore;        // 가게 별점
        TextView mainStoreReviewCount;  // 가게 리뷰 수
        TextView mainStoreHashTag;      // 가게 해시태그

        public CustomMainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            mainStoreImage = itemView.findViewById(R.id.main_store_image);
            mainStoreBookmark = itemView.findViewById(R.id.main_store_bookmark);

            mainStoreName = itemView.findViewById(R.id.main_store_name);
            mainStoreDistance = itemView.findViewById(R.id.main_store_distance);
            mainStoreScore = itemView.findViewById(R.id.main_store_score);
            mainStoreReviewCount = itemView.findViewById(R.id.main_store_review_count);
            mainStoreHashTag = itemView.findViewById(R.id.main_store_hashtag);

            // 리사이클러뷰 이미지 클릭 이벤트 인터페이스 구현
            mainStoreImage.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onMainStoreRvClick(view, getAbsoluteAdapterPosition(), "image");
                }
            });

            // 리사이클러뷰 찜 버튼 클릭 이벤트 인터페이스 구현
            mainStoreBookmark.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    if(mainStoreBookmark.isSelected()){
                        mainStoreBookmark.setSelected(false);
                        rvClickListener.onMainStoreRvClick(view, getAbsoluteAdapterPosition(), "bookmarkDelete");
                    }else{
                        mainStoreBookmark.setSelected(true);
                        rvClickListener.onMainStoreRvClick(view, getAbsoluteAdapterPosition(), "bookmarkInsert");
                    }
                }
            });
        }
    }
}
