package com.example.itda.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.itda.R;
import com.example.itda.ui.home.mainStoreData;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class InfoMapActivity extends Activity {
    private mainStoreData Store;        // 가게 데이터
    private ViewGroup mapViewContainer; // mapView를 포함시킬 View Container
    private MapView mapView;    // 카카오 지도 View
    private ImageButton infoMapBackIc;  // 상단 뒤로가기 버튼
    private Button infoMapStoreName;    // 상단 가게 명

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_map);

        Store = getIntent().getParcelableExtra("store");    // 가게 데이터

        initView(); // 뷰 생성

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoMapBackIc.setOnClickListener(view -> finish());

        infoMapStoreName.setText(Store.getStoreName()); // 상단 가게 명
    }

    // Activity 이동간 mapView는 1개만 띄워져 있어야 하기 때문에
    // onCreate가 아닌 onResume에서 mapview 객체 생성
    //
    // ----------- 간단한 LifeCycle --------------
    // onCreate -> onResume -> ( 다른 Activity로 이동 ) -> onPause -> ( 현재 Activity로 이동 ) -> onResume
    @Override
    protected void onResume() {
        super.onResume();

        mapView = new MapView(this);    // mapView 객체 생성

        mapViewContainer.addView(mapView);     // mapView attach

        double latitude = Store.getStoreLatitude();     // 첫 번째로 검색된 가게의 위도
        double longitude = Store.getStoreLongitude();   // 첫 번째로 검색된 가게의 경도

        // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        mapView.setMapCenterPoint(mapPoint, true);  // 지도 화면의 중심점을 설정

        MapPOIItem marker = new MapPOIItem();       // POI 객체 생성
        marker.setItemName(Store.getStoreName());   // POI Item 아이콘이 선택되면 나타나는 말풍선(Callout Balloon)에 POI Item 이름이 보여짐
        marker.setMapPoint(mapPoint);   // POI Item의 지도상 좌표를 설정
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker); // 지도화면에 POI Item 아이콘(마커)를 추가

    }

    // 다른 Activity로 이동했을 경우 생성했던 mapView 객체 제거
    @Override
    protected void onPause() {
        super.onPause();

        mapViewContainer.removeView(mapView);
    }

    private void initView(){
        infoMapBackIc = findViewById(R.id.info_map_back_ic);    // 상단 뒤로가기 버튼
        infoMapStoreName = findViewById(R.id.info_map_main_store_name);     // 상단 가게 명
        mapViewContainer = (ViewGroup) findViewById(R.id.info_map_detail_view); // ViewGroup Container
    }
}

