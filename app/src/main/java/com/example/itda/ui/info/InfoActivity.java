package com.example.itda.ui.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.ui.home.HomeSearchActivity;
import com.example.itda.ui.home.mainStoreData;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoActivity extends Activity {

    private mainStoreData Store;    // 가게 데이터
    private ArrayList<collaboData> Collabo = new ArrayList<>();     // 협업 가게 데이터
    private ArrayList<menuData> Menu = new ArrayList<>();       // 메뉴 데이터

    // ---------------- 최상단 Section ---------------------------
    private TextView infoMainStoreName; // 최상단 가게 이름
    private ImageButton infoBackIc;     // 최상단 뒤로가기 버튼
    private ImageButton infoCallIc;     // 최상단 전화 버튼
    private ImageButton infoBookmarkIc; // 최상단 찜 버튼


    // ---------------- 가게 정보 Section ---------------------
    private TextView infoStoreName;     // 가게 이름
    private TextView infoStarScore;     // 가게 별점
    private TextView infoInformation;   // 가게 간단 설명
    private TextView infoHashtag;       // 가게 해시태그
    private ImageView infoStoreImage;   // 가게 썸네일 이미지


    // ---------------- 협업 Section ---------------------
    private TextView infoCollaboTxt;                    // 협업 가게 Text ( "이어진 가게" )
    private RecyclerView infoCollaboRv;                 // 협업 가게 리사이클러뷰
    private InfoCollaboRvAdapter infoCollaboAdapter;    // 협업 가게 리사이클러뷰 어뎁터


    // ---------------- 운영 정보 Section ---------------------
    private TextView infoWorkingTime;           // 가게 운영 시간
    private TextView infoDetail;                // 가게 간단 제공 서비스
    private TextView infoFacility;              // 가게 제공 시설 여부
    private ImageButton infoWorkingTimeDownIc;  // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
    private ImageButton infoWorkingTimeUpIc;    // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
    private String[] workingTimeArr;            // 운영 시간 텍스트를 구분자 "&&"으로 Split한 배열
    private LinearLayout infoWorkingTimeLayout; // 가게 운영시간 전체 레이아웃
    private LinearLayout infoDetailLayout;      // 가게 간단 제공 서비스 레이아웃
    private LinearLayout infoFacilityLayout;    // 가게 제공 시설 여부 레이아웃


    // ---------------- 메뉴 Section ---------------------
    private Button infoMenuPlusBtn;             // 메뉴 더보기 버튼
    private RecyclerView infoMenuRv;            // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
    private InfoMenuRvAdapter infoMenuAdapter;  // 메뉴 리사이클러뷰 어댑터
    private ImageView infoMenuIcon;             // 메뉴 아이콘


    // ---------------- 지도 Section ---------------------
    private TextView infoAddress; // 가게 주소
    private ViewGroup mapViewContainer;     // mapView를 포함시킬 View Container
    private MapView mapView;                // 카카오 지도 View

    private boolean firstDragFlag = true;   // mapView 드래그 모드인지 확인하는 Flag
    private boolean dragFlag = false;       // 현재 터치가 드래그인지 확인하는 Flag
    private float startXPosition = 0;       // 터치 이벤트의 시작점의 X(가로)위치
    private float startYPosition = 0;       // 터치 이벤트의 시작점의 Y(가로)위치


    // ---------------- 사진 Section ---------------------
    private TextView infoPhotoTitle;    // 사진 타이틀 가게 이름
    private Button infoPhotoPlusBtn;    // 사진 더보기 버튼
    private RecyclerView infoPhotoRv;   // 사진 리사이클러뷰


    // ---------------- 리뷰 Section ---------------------
    private TextView infoReviewTitle;   // 리뷰 타이틀 가게 이름
    private TextView infoReviewPlusBtn; // 리뷰 쓰기 버튼
    private RecyclerView infoReviewRv;  // 리뷰 리사이클러뷰

    // ---------------- 결제 Section ---------------------
    private Button infoPaymentBtn; // 결제하기 버튼


    // ---------------- Nested ScrollView ---------------------
    private NestedScrollView infoScrollView; // 스크롤 뷰


    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue
    final static private String COLLABO_PATH = "/info/getInfoCollabo.php";  // 협업 가게 정보 데이터 조회 Rest API
    final static private String MENU_PATH = "/info/getInfoMenu.php";        // 메뉴 정보 데이터 조회 Rest API
    final static private String HOST = "http://no2955922.ivyro.net";        // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initView();

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        Store = getIntent().getParcelableExtra("Store");

        // ---------------- 최상단 Section ---------------------------
        infoMainStoreName.setText(Store.getStoreName());    // 최상단 가게 이름
        // 최상단 뒤로가기 버튼 클릭 리스너
        infoBackIc.setOnClickListener(v -> finish());   // 버튼 클릭 시 Activity 종료

        // ---------------- 가게 정보 Section ---------------------
        infoStoreName.setText(Store.getStoreName());        // 가게 이름
        infoStarScore.setText(String.valueOf(Store.getStoreScore()));   // 가게 별점
        infoInformation.setText(Store.getStoreInfo());      // 가게 간단 정보

        // 가게 간단 소개
        if(!TextUtils.isEmpty(Store.getStoreInfo())){
            infoInformation.setText(Store.getStoreInfo());
        }else{
            infoInformation.setText("가게 소개가 등록되어 있지 않습니다.");
        }

        // 이미지 설정
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(infoStoreImage);     // 이미지를 보여줄 View를 지정

        // ---------------- 협업 Section ---------------------
        getInfoCollabo();   // 협업 가게 데이터 GET


        // ---------------- 운영 정보 Section ---------------------
        // 가게 운영 시간
        if(!TextUtils.isEmpty(Store.getStoreWorkingTime())){
            workingTimeArr = Store.getStoreWorkingTime().split("\\n");
            infoWorkingTime.setText(workingTimeArr[0]);
            if(workingTimeArr.length == 1){
                infoWorkingTimeDownIc.setVisibility(View.GONE);
            }
        }else{
            infoWorkingTimeLayout.setVisibility(View.GONE);
        }

        // 가게 간단 제공 서비스
        if(!TextUtils.isEmpty(Store.getStoreDetail())){
            infoDetail.setText(Store.getStoreDetail());
        }else{
            infoDetailLayout.setVisibility(View.GONE);
        }

        // 가게 제공 시설 여부
        if(!TextUtils.isEmpty(Store.getStoreFacility())){
            infoFacility.setText(Store.getStoreFacility());
        }else{
            infoFacilityLayout.setVisibility(View.GONE);
        }

        // 가게 운영시간 아래 화살표 ( 내용 늘이기 ) 버튼 클릭 리스너
        infoWorkingTimeDownIc.setOnClickListener(view -> {
            infoWorkingTime.setText(Store.getStoreWorkingTime());   // 구분자가 "\n"으로 되어있는 내용 모두 출력
            infoWorkingTimeDownIc.setVisibility(View.GONE);         // 아래 방향 화살표 버튼 아예 숨기기 ( 공간 조차 없어짐 )
            infoWorkingTimeUpIc.setVisibility(View.VISIBLE);        // 위 방향 화살표 버튼 보이기
        });

        infoWorkingTimeUpIc.setOnClickListener(view -> {
            workingTimeArr = Store.getStoreWorkingTime().split("\\n");    // 구분자가 "\n"으로 되어있는 내용 split
            infoWorkingTime.setText(workingTimeArr[0]);                         // 첫번째 배열에 들어있는 내용만 출력
            infoWorkingTimeUpIc.setVisibility(View.GONE);                       // 위 방향 화살표 버튼 아예 숨기기 ( 공간 조차 없어짐 )
            infoWorkingTimeDownIc.setVisibility(View.VISIBLE);                  // 아래 방향 화살표 버튼 보이기
        });

        // ---------------- 메뉴 Section ---------------------
        getInfoMenu();  // 메뉴 데이터 GET

        infoMenuPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, InfoMenuActivity.class);  // 메뉴 상세 화면 Activity로 이동하기 위한 Intent 객체 선언

                intent.putParcelableArrayListExtra("Menu", Menu);
                intent.putExtra("storeName", Store.getStoreName());
                startActivity(intent);
            }
        });

        // ---------------- 지도 Section ---------------------
        infoAddress.setText(Store.getStoreAddress());


        // ---------------- 사진 Section ---------------------


        // ---------------- 리뷰 Section ---------------------


        // ---------------- 결제 Section ---------------------

    }


    // Activity 이동간 mapView는 1개만 띄워져 있어야 하기 때문에
    // onCreate가 아닌 onResume에서 mapview 객체 생성
    //
    // ----------- 간단한 LifeCycle --------------
    // onCreate -> onResume -> ( 다른 Activity로 이동 ) -> onPause -> ( 현재 Activity로 이동 ) -> onResume
    // @SuppressLint("ClickableViewAccessibility") 어노테이션을 추가해 Lint의 Warning을 무시
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();

        mapView = new MapView(this);   // mapView 객체 생성

        mapViewContainer = (ViewGroup) findViewById(R.id.info_map_view);    // ViewGroup Container
        mapViewContainer.addView(mapView);                                  // mapView attach

        double latitude = Store.getStoreLatitude();  // 첫 번째로 검색된 가게의 위도
        double longitude = Store.getStoreLongitude(); // 첫 번째로 검색된 가게의 경도

        // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        mapView.setMapCenterPoint(mapPoint, true);         // 지도 화면의 중심점을 설정

        MapPOIItem marker = new MapPOIItem();                       // POI 객체 생성
        marker.setItemName(Store.getStoreName());     // POI Item 아이콘이 선택되면 나타나는 말풍선(Callout Balloon)에 POI Item 이름이 보여짐
        marker.setMapPoint(mapPoint);                           // POI Item의 지도상 좌표를 설정
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker); // 지도화면에 POI Item 아이콘(마커)를 추가

        // mapView에 모션이벤트가 생길때
        // mainScrollView.requestDisallowInterceptTouchEvent(true);
        //스크롤에 터치이벤트를 뺏기지 않는다는 코드
        mapView.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    infoScrollView.requestDisallowInterceptTouchEvent(true);
                    startXPosition = motionEvent.getX();  //첫번째 터치의 X(너비)를 저장
                    startYPosition = motionEvent.getY();  //첫번째 터치의 Y(너비)를 저장
                    break;
                case MotionEvent.ACTION_UP:
                    float endXPosition = motionEvent.getX();   // X 좌표
                    float endYPosition = motionEvent.getY();   // X 좌표
                    if(Math.abs(startXPosition - endXPosition) < 10 && Math.abs(startYPosition - endYPosition) < 10){
                        Intent intent = new Intent(InfoActivity.this, InfoMapActivity.class);  // 메뉴 상세 화면 Activity로 이동하기 위한 Intent 객체 선언

                        intent.putExtra("store", Store);
                        startActivity(intent);
                    }
                    infoScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        });
    }

    // 다른 Activity로 이동했을 경우 생성했던 mapView 객체 제거
    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeView(mapView);
    }

    private void initView(){
        // ---------------- 최상단 Section ---------------------------
        infoMainStoreName = findViewById(R.id.info_main_store_name);    // 최상단 가게 이름
        infoBackIc = findViewById(R.id.info_back_ic);                   // 최상단 뒤로 가기 버튼
        infoCallIc = findViewById(R.id.info_call_ic);                   // 최상단 전화 버튼
        infoBookmarkIc = findViewById(R.id.info_bookmark_ic);           // 최상단 찜 버튼

        // ---------------- 가게 정보 Section ---------------------
        infoStoreName = findViewById(R.id.info_store_name);     // 가게 이름
        infoStarScore = findViewById(R.id.info_star_score);     // 가게 별점
        infoInformation = findViewById(R.id.info_information);  // 가게 간단 설명
        infoHashtag = findViewById(R.id.info_hashtag);          // 가게 해시태그
        infoStoreImage = findViewById(R.id.info_store_image);   // 가게 썸네일 이미지

        // ---------------- 협업 Section ---------------------
        infoCollaboTxt = findViewById(R.id.collabo_tv);     // 협업 타이틀
        infoCollaboRv = findViewById(R.id.info_collabo_rv); // 협업 리사이클러뷰

        // ---------------- 운영 정보 Section ---------------------
        infoWorkingTime = findViewById(R.id.info_working_time); // 가게 운영 시간
        infoDetail = findViewById(R.id.info_detail);            // 가게 간단 제공 서비스
        infoFacility = findViewById(R.id.info_facility);        // 가게 제공 시설 여부
        infoWorkingTimeDownIc = findViewById(R.id.info_working_time_down_arrow_ic); // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
        infoWorkingTimeUpIc = findViewById(R.id.info_working_time_up_arrow_ic);     // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
        infoWorkingTimeLayout = findViewById(R.id.info_working_time_layout);    // 가게 운영 시간 전체 레이아웃
        infoDetailLayout = findViewById(R.id.info_detail_layout);               // 가게 간단 제공 서비스 전체 레이아웃
        infoFacilityLayout = findViewById(R.id.info_facility_layout);           // 가게 제공 시설 여부 전체 레이아웃

        // ---------------- 메뉴 Section ---------------------
        infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);    // 메뉴 더보기 버튼
        infoMenuRv = findViewById(R.id.info_menu_rv);               // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        infoMenuIcon = findViewById(R.id.info_menu_ic);             // 메뉴 아이콘

        // ---------------- 지도 Section ---------------------
        infoAddress = findViewById(R.id.info_address);  // 가게 주소

        // ---------------- 사진 Section ---------------------
        infoPhotoTitle = findViewById(R.id.info_photo_title);       // 사진 타이틀 가게 이름
        infoPhotoPlusBtn = findViewById(R.id.info_photo_plus_btn);  // 사진 더보기 버튼
        infoPhotoRv = findViewById(R.id.info_photo_rv);             // 사진 리사이클러뷰

        // ---------------- 리뷰 Section ---------------------
        infoReviewTitle = findViewById(R.id.info_review_title);         // 리뷰 타이틀 가게 이름
        infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);    // 리뷰 작성 버튼
        infoReviewRv = findViewById(R.id.info_review_rv);               // 리뷰 리사이클러뷰

        // ---------------- 결제 Section ---------------------
        infoPaymentBtn = findViewById(R.id.info_payment_btn);   // 결제 버튼

        // ---------------- Nested ScrollView ---------------------
        infoScrollView = findViewById(R.id.info_scroll_view); // 스크롤 뷰
    }

    // 협업 가게 데이터 GET
    private void getInfoCollabo(){
        // 수평, 수직으로 ViewHolder 표현하기 위한 Layout 관리 클래스
        LinearLayoutManager llm = new LinearLayoutManager(this);    // LayoutManager 객체 생성
        llm.setOrientation(LinearLayoutManager.HORIZONTAL); // 수평 레이아웃으로 설정
        infoCollaboRv.setHasFixedSize(true);    // 리사이클러뷰 높이, 너비 변경 제한
        infoCollaboRv.setLayoutManager(llm);    // 리사이클러뷰 Layout 설정

        // GET 방식 파라미터 설정
        String collaboPath = COLLABO_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest CollaboRequest = new StringRequest(Request.Method.GET, HOST + collaboPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                   // Response를 JsonObject 객체로 생성
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");  // 객체에 collabo라는 Key를 가진 JSONArray 생성

                if(collaboArr.length() > 0) {
                    for (int i = 0; i < collaboArr.length(); i++) {
                        JSONObject object = collaboArr.getJSONObject(i);        // 배열 원소 하나하나 꺼내서 JSONObject 생성
                        // 카테고리 데이터 생성 및 저장
                        collaboData collaboData = new collaboData(
                                object.getInt("storeId")                      // 가게 고유 아이디
                                , object.getString("storeName")                 // 가게 이름
                                , object.getString("storeAddress")              // 가게 주소
                                , object.getString("storeDetail")               // 가게 간단 제공 서비스
                                , object.getString("storeFacility")             // 가게 제공 시설 여부
                                , object.getDouble("storeLatitude")             // 가게 위도
                                , object.getDouble("storeLongitude")            // 가게 경도
                                , object.getString("storeNumber")               // 가게 번호
                                , object.getString("storeInfo")                 // 가게 간단 정보
                                , object.getInt("storeCategoryId")              // 가게가 속한 카테고리 고유 아이디
                                , HOST + object.getString("storeThumbnailPath") // 가게 썸네일 이미지 경로
                                , object.getDouble("storeScore")                // 가게 별점
                                , object.getString("storeWorkingTime")          // 가게 운영 시간
                                , object.getInt("collaboId")                    // 협업 고유 아이디
                                , object.getInt("collaboDiscountCondition")     // 앞 가게 할인 조건 ( 최소 금액 )
                                , object.getInt("collaboDiscountRate"));        // 뒷 가게 할인율 ( 정수 )
                        Collabo.add(collaboData); // 카테고리 정보 저장
                    }

                    infoCollaboAdapter = new InfoCollaboRvAdapter(this);  // 리사이클러뷰 어뎁터 객체 생성
                    infoCollaboAdapter.setCollabo(Collabo);    // 어뎁터 객체에 카테고리 정보 저장
                    infoCollaboRv.setAdapter(infoCollaboAdapter);     // 리사이클러뷰 어뎁터 객체 지정

                    infoCollaboTxt.setVisibility(View.VISIBLE);
                    infoCollaboRv.setVisibility(View.VISIBLE);
                }else{
                    infoCollaboTxt.setVisibility(View.GONE);
                    infoCollaboRv.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoCollaboError", "onErrorResponse : " + error);
        });

        CollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(CollaboRequest);      // RequestQueue에 요청 추가
    }

    // 메뉴 데이터 GET
    private void getInfoMenu(){
        // GET 방식 파라미터 설정
        String menuPath = MENU_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest MenuRequest = new StringRequest(Request.Method.GET, HOST + menuPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);             // Response를 JsonObject 객체로 생성
                JSONArray menuArr = jsonObject.getJSONArray("menu");    // 객체에 menu라는 Key를 가진 JSONArray 생성

                if(menuArr.length() > 0) {
                    for (int i = 0; i < menuArr.length(); i++) {
                        JSONObject object = menuArr.getJSONObject(i);        // 배열 원소 하나하나 꺼내서 JSONObject 생성
                        // 카테고리 데이터 생성 및 저장
                        menuData menuData = new menuData(
                                object.getInt("menuId")                      // 가게 고유 아이디
                                , object.getInt("storeId")                 // 가게 이름
                                , object.getString("menuName")              // 가게 주소
                                , object.getInt("menuPrice")               // 가게 간단 제공 서비스
                                , object.getInt("menuOrder"));             // 가게 제공 시설 여부
                        Menu.add(menuData); // 카테고리 정보 저장
                    }

                    infoMenuAdapter = new InfoMenuRvAdapter(this, Menu, false);  // 리사이클러뷰 어뎁터 객체 생성

                    infoMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                    infoMenuRv.setAdapter(infoMenuAdapter);

                    infoMenuPlusBtn.setVisibility(View.VISIBLE);
                    ConstraintLayout infoMenuBar = findViewById(R.id.info_menu_bar);
                    infoMenuBar.setVisibility(View.VISIBLE);
                    infoMenuRv.setVisibility(View.VISIBLE);
                    infoMenuIcon.setVisibility(View.VISIBLE);
                }else{
                    ConstraintLayout infoMenuBar = findViewById(R.id.info_menu_bar);
                    infoMenuBar.setVisibility(View.GONE);
                    infoMenuPlusBtn.setVisibility(View.GONE);
                    infoMenuRv.setVisibility(View.GONE);
                    infoMenuIcon.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoMenuError", "onErrorResponse : " + error);
        });

        MenuRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(MenuRequest);      // RequestQueue에 요청 추가
    }
}

