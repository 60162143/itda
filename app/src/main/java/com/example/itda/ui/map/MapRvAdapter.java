package com.example.itda.ui.map;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.mainBookmarkStoreData;

import java.util.ArrayList;
import java.util.Locale;

public class MapRvAdapter extends RecyclerView.Adapter<MapRvAdapter.CustomStoreViewHolder> {

    private final ArrayList<mapStoreData> Stores;   // 가게 데이터
    private ArrayList<mainBookmarkStoreData> BookmarkStores;    // 찜한 가게 데이터

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onMapStoreRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public MapRvAdapter(Context context
            , ArrayList<mapStoreData> stores
            , ArrayList<mainBookmarkStoreData> bookmarkStore){
        this.mContext = context;
        this.Stores = stores;
        this.BookmarkStores = bookmarkStore;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public MapRvAdapter.CustomStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_map_store, parent, false);

        return new CustomStoreViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull MapRvAdapter.CustomStoreViewHolder holder, int position) {
        mapStoreData store = Stores.get(position);  // 현재 position의 가게 정보

        // 로그인이 되어있을 경우 찜 버튼 보이기
        if(((globalMethod) mContext.getApplicationContext()).loginChecked()){
            holder.mapStoreBookmark.setVisibility(View.VISIBLE);

            // 찜한 가게 목록이 있을 경우 찜 버튼 활성화
            if(BookmarkStores != null && BookmarkStores.size() > 0){
                for(int i = 0; i < BookmarkStores.size(); i++){
                    if(store.getMapStoreId() == BookmarkStores.get(i).getStoreId()){
                        holder.mapStoreBookmark.setSelected(true);

                        break;
                    }
                }
            }
        }else{
            holder.mapStoreBookmark.setVisibility(View.GONE);
        }

        // 가게 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(store.getMapStoreImagePath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.mapStoreImage);        // 이미지를 보여줄 View를 지정

        // 가게 이름 SET
        holder.mapStoreName.setText(store.getMapStoreName());

        // 거리 SET
        // 거리가 10m 이상인 경우만 거리 표시
        String mapStoreDistanceTxt;
        if(store.getMapStoreDistance() <= 0.01){
            mapStoreDistanceTxt = "10m 이내";
        }else{
            mapStoreDistanceTxt = String.format(Locale.getDefault(), "%.2f", store.getMapStoreDistance()) + "km";
        }
        holder.mapStoreDistance.setText(mapStoreDistanceTxt);

        // 가게 별점 SET
        String mapStoreStarScoreTxt = String.format(Locale.getDefault(), "%.1f", store.getMapStoreScore());
        holder.mapStoreStarScore.setText(mapStoreStarScoreTxt);

        // 가게 간단 정보 SET
        holder.mapStoreInfo.setText(store.getMapStoreInfo());

        // 가게 해시태그 SET
        holder.mapStoreHashTag.setText(store.getMapStoreHashTag());
    }

    // 리스너 설정
    public void setonMapStoreRvClickListener(onMapStoreRvClickListener rvClickListener) {
        MapRvAdapter.rvClickListener = rvClickListener;
    }

    // 찜한 가게 목록 SET
    public void setbookmarkStores(ArrayList<mainBookmarkStoreData> bookmarkStores) {
        BookmarkStores = bookmarkStores;
    }

    // 가게 별점 SET
    public void setStoreScore(int index, double score) {
        Stores.get(index).setMapStoreScore((float) score);
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
        ImageView mapStoreImage;    // 가게 썸네일
        TextView mapStoreName;      // 가게 명
        TextView mapStoreDistance;  // 현재 위치에서 가게까지의 거리
        TextView mapStoreStarScore; // 가게 별점
        TextView mapStoreInfo;      // 가게 간단 정보
        TextView mapStoreHashTag;   // 가게 해시태그
        Button mapStoreBookmark;   // 가게 찜 버튼

        public CustomStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            mapStoreImage = itemView.findViewById(R.id.map_store_image);
            mapStoreName = itemView.findViewById(R.id.map_store_name);
            mapStoreDistance = itemView.findViewById(R.id.map_store_distance);
            mapStoreStarScore = itemView.findViewById(R.id.map_star_score);
            mapStoreInfo = itemView.findViewById(R.id.map_store_info);
            mapStoreHashTag = itemView.findViewById(R.id.map_store_hashTag);
            mapStoreBookmark = itemView.findViewById(R.id.map_store_bookmark);

            // 리사이클러뷰 찜 버튼 클릭 이벤트 인터페이스 구현
            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onMapStoreRvClick(view, getAbsoluteAdapterPosition(), "total");
                }
            });

            // 리사이클러뷰 찜 버튼 클릭 이벤트 인터페이스 구현
            mapStoreBookmark.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    if(mapStoreBookmark.isSelected()){
                        mapStoreBookmark.setSelected(false);
                        rvClickListener.onMapStoreRvClick(view, getAbsoluteAdapterPosition(), "bookmarkDelete");
                    }else{
                        mapStoreBookmark.setSelected(true);
                        rvClickListener.onMapStoreRvClick(view, getAbsoluteAdapterPosition(), "bookmarkInsert");
                    }
                }
            });
        }
    }
}