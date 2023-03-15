package com.example.itda.ui.map;

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

import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class MapRvAdapter extends RecyclerView.Adapter<MapRvAdapter.CustomStoreViewHolder> {

    private ArrayList<MapStoreData> Stores = new ArrayList<>(); // 가게 데이터

    private MapView mapView;    // 지도 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private Context mContext;

    // Constructor
    public MapRvAdapter(Context context){
        this.mContext = context;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public MapRvAdapter.CustomStoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_store, parent, false);

        return new CustomStoreViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull MapRvAdapter.CustomStoreViewHolder holder, int position) {
        MapStoreData store = Stores.get(position);  // 현재 position의 가게 정보

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(store.getMapStoreIamagePath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.mapStoreImage);        // 이미지를 보여줄 View를 지정

        holder.mapStoreName.setText(store.getMapStoreName());   // 가게 이름 SET

        // 거리가 10m 이상인 경우만 거리 표시
        if(store.getMapStoreDistance() <= 0.01){
            holder.mapStoreDistance.setText("10m 이내");
        }else{
            holder.mapStoreDistance.setText(String.format("%.2f", store.getMapStoreDistance()) + "km");
        }

        holder.mapStoreStarScore.setText(String.format("%.1f", store.getMapStoreScore()));  // 가게 별점 SET
        holder.mapStoreInfo.setText(store.getMapStoreInfo());   // 가게 간단 정보 SET

        //holder.mapStoreHashTag.setText(store.getMapStoreHashTag());   // 추후 추가
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Stores.size();
    }

    // 가게 정보 Setter
    public void setStores(ArrayList<MapStoreData> stores){
        this.Stores = stores;
    }

    // 지도 정보 Getter & Setter
    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
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

        public CustomStoreViewHolder(@NonNull View itemView) {
            super(itemView);
            mapStoreImage = itemView.findViewById(R.id.map_store_image);
            mapStoreName = itemView.findViewById(R.id.map_store_name);
            mapStoreDistance = itemView.findViewById(R.id.map_store_distance);
            mapStoreStarScore = itemView.findViewById(R.id.map_star_score);
            mapStoreInfo = itemView.findViewById(R.id.map_store_info);
            mapStoreHashTag = itemView.findViewById(R.id.map_store_hashTag);
        }
    }
}