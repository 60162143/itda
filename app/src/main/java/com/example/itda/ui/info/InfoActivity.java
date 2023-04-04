package com.example.itda.ui.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.ui.global.globalVariable;
import com.example.itda.ui.home.MainStoreRvAdapter;
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

public class InfoActivity extends Activity implements onInfoCollaboRvClickListener, onInfoPhotoRvClickListener, onInfoReviewRvClickListener {

    private mainStoreData Store;    // 가게 데이터
    private ArrayList<collaboData> Collabo = new ArrayList<>(); // 협업 가게 데이터
    private ArrayList<menuData> Menu = new ArrayList<>();       // 메뉴 데이터
    private ArrayList<photoData> Photo = new ArrayList<>();     // 사진 데이터
    private ArrayList<reviewData> Review = new ArrayList<>();   // 리뷰 데이터

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
    private TextView infoCollaboTitle;                  // 협업 타이틀
    private RecyclerView infoCollaboRv;                 // 협업 가게 리사이클러뷰
    private InfoCollaboRvAdapter infoCollaboAdapter;    // 협업 가게 리사이클러뷰 어뎁터
    private Dialog infoCollaboDialog;                   // 협업 팝업 다이얼로그
    private LinearLayout infoCollaboLayout;             // 헙업 전체 레이아웃


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
    private LinearLayout infoServiceLayout;     // 운영 정보 전체 레이아웃

    // ---------------- 메뉴 Section ---------------------
    private Button infoMenuPlusBtn;             // 메뉴 더보기 버튼
    private RecyclerView infoMenuRv;            // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
    private InfoMenuRvAdapter infoMenuAdapter;  // 메뉴 리사이클러뷰 어댑터
    private LinearLayout infoMenuLayout;        // 메뉴 전체 레이아웃


    // ---------------- 지도 Section ---------------------
    private TextView infoAddress; // 가게 주소
    private ViewGroup mapViewContainer;     // mapView를 포함시킬 View Container
    private MapView mapView;                // 카카오 지도 View

    private float startXPosition = 0;       // 터치 이벤트의 시작점의 X(가로)위치
    private float startYPosition = 0;       // 터치 이벤트의 시작점의 Y(가로)위치


    // ---------------- 사진 Section ---------------------
    private RecyclerView infoPhotoRv;               // 사진 리사이클러뷰
    private LinearLayout infoPhotoLayout;           // 사진 전체 레이아웃
    private InfoPhotoRvAdapter infoPhotoAdapter;    // 사진 리사이클러뷰 어댑터


    // ---------------- 리뷰 Section ---------------------
    private Button infoReviewPlusBtn; // 리뷰 작성 버튼
    private RecyclerView infoReviewRv;  // 리뷰 리사이클러뷰
    private InfoReviewRvAdapter infoReviewAdapter;  // 리뷰 리사이클러뷰 어댑터

    // ---------------- 결제 Section ---------------------
    private Button infoPaymentBtn; // 결제하기 버튼


    // ---------------- Nested ScrollView ---------------------
    private NestedScrollView infoScrollView; // 스크롤 뷰


    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue
    private String MAINSTORE_PATH;  // 가게 데이터 조회 Rest API
    private String COLLABO_PATH;    // 협업 가게 정보 데이터 조회 Rest API
    private String MENU_PATH;       // 메뉴 정보 데이터 조회 Rest API
    private String PHOTO_PATH;      // 사진 정보 데이터 조회 Rest API
    private String REVIEW_PATH;     // 리뷰 정보 데이터 조회 Rest API
    private String HOST;            // Host 정보

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

        // ---------------- Rest API 전역변수 SET---------------------------
        MAINSTORE_PATH = ((globalVariable) getApplication()).getMainStorePath();    // 가게 데이터 조회 Rest API
        COLLABO_PATH = ((globalVariable) getApplication()).getCollaboPath();        // 협업 가게 정보 데이터 조회 Rest API
        MENU_PATH = ((globalVariable) getApplication()).getMenuPath();              // 메뉴 정보 데이터 조회 Rest API
        PHOTO_PATH = ((globalVariable) getApplication()).getPhotoPath();            // 사진 정보 데이터 조회 Rest API
        REVIEW_PATH = ((globalVariable) getApplication()).getReviewPath();          // 리뷰 정보 데이터 조회 Rest API
        HOST = ((globalVariable) getApplication()).getHost();                       // Host 정보

        // ---------------- 최상단 Section ---------------------------
        infoMainStoreName.setText(Store.getStoreName());    // 최상단 가게 이름
        // 최상단 뒤로가기 버튼 클릭 리스너
        infoBackIc.setOnClickListener(v -> finish());   // 버튼 클릭 시 Activity 종료

        if(!TextUtils.isEmpty(Store.getStoreNumber())){
            infoCallIc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String telNum = "tel:" + Store.getStoreNumber();
                    Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telNum));
                    startActivity(mIntent);
                }
            });
        }else{
            infoCallIc.setVisibility(View.GONE);
        }



        // ---------------- 가게 정보 Section ---------------------
        infoStoreName.setText(Store.getStoreName());        // 가게 이름
        infoStarScore.setText(String.valueOf(Store.getStoreScore()));   // 가게 별점
        infoInformation.setText(Store.getStoreInfo());      // 가게 간단 정보
        infoHashtag.setText(Store.getStoreHashTag());       // 가게 해시태그

        // 가게 간단 소개
        if(!TextUtils.isEmpty(Store.getStoreInfo())){
            infoInformation.setText(Store.getStoreInfo());
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
        boolean serviceExistFalg = false;   // 운영정보가 하나라도 존재하는지 여부를 나타내는 Flag
        // 가게 운영 시간
        if(!TextUtils.isEmpty(Store.getStoreWorkingTime())){
            serviceExistFalg = true;
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
            serviceExistFalg = true;
            infoDetail.setText(Store.getStoreDetail());
        }else{
            infoDetailLayout.setVisibility(View.GONE);
        }

        // 가게 제공 시설 여부
        if(!TextUtils.isEmpty(Store.getStoreFacility())){
            serviceExistFalg = true;
            infoFacility.setText(Store.getStoreFacility());
        }else{
            infoFacilityLayout.setVisibility(View.GONE);
        }

        if(serviceExistFalg){
            infoServiceLayout.setPadding(0 , 10, 0, 20);
        }else{
            infoServiceLayout.setVisibility(View.GONE);
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
        getInfoPhoto();     // 사진 데이터 GET


        // ---------------- 리뷰 Section ---------------------
        getInfoReview();    // 리뷰 데이터 GET


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
        infoCollaboLayout = findViewById(R.id.info_collabo_layout); // 협업 전체 레이아웃
        infoCollaboRv = findViewById(R.id.info_collabo_rv);         // 협업 리사이클러뷰
        infoCollaboTitle = findViewById(R.id.info_collabo_title);   // 협업 타이틀

        infoCollaboDialog = new Dialog(InfoActivity.this);          // Dialog 초기화
        infoCollaboDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        infoCollaboDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoCollaboDialog.setContentView(R.layout.dl_info_collabo); // xml 레이아웃 파일과 연결

        // ---------------- 운영 정보 Section ---------------------
        infoWorkingTime = findViewById(R.id.info_working_time); // 가게 운영 시간
        infoDetail = findViewById(R.id.info_detail);            // 가게 간단 제공 서비스
        infoFacility = findViewById(R.id.info_facility);        // 가게 제공 시설 여부
        infoWorkingTimeDownIc = findViewById(R.id.info_working_time_down_arrow_ic); // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
        infoWorkingTimeUpIc = findViewById(R.id.info_working_time_up_arrow_ic);     // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
        infoWorkingTimeLayout = findViewById(R.id.info_working_time_layout);    // 가게 운영 시간 전체 레이아웃
        infoDetailLayout = findViewById(R.id.info_detail_layout);               // 가게 간단 제공 서비스 전체 레이아웃
        infoFacilityLayout = findViewById(R.id.info_facility_layout);           // 가게 제공 시설 여부 전체 레이아웃
        infoServiceLayout = findViewById(R.id.info_service_layout);             // 운영 정보 전체 레이아웃

        // ---------------- 메뉴 Section ---------------------
        infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);    // 메뉴 더보기 버튼
        infoMenuRv = findViewById(R.id.info_menu_rv);               // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        infoMenuLayout = findViewById(R.id.info_menu_layout);       // 메뉴 전체 레이아웃

        // ---------------- 지도 Section ---------------------
        infoAddress = findViewById(R.id.info_address);  // 가게 주소

        // ---------------- 사진 Section ---------------------
        infoPhotoRv = findViewById(R.id.info_photo_rv);             // 사진 리사이클러뷰
        infoPhotoLayout = findViewById(R.id.info_photo_layout);

        // ---------------- 리뷰 Section ---------------------
        infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);    // 리뷰 작성 버튼
        infoReviewRv = findViewById(R.id.info_review_rv);               // 리뷰 리사이클러뷰

        // ---------------- 결제 Section ---------------------
        infoPaymentBtn = findViewById(R.id.info_payment_btn);   // 결제 버튼

        // ---------------- Nested ScrollView ---------------------
        infoScrollView = findViewById(R.id.info_scroll_view); // 스크롤 뷰

        // ---------------- Collabo Dialog Section ---------------------
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
                        // 협업 데이터 생성 및 저장
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
                                , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                                , object.getDouble("storeScore")                // 가게 별점
                                , object.getString("storeWorkingTime")          // 가게 운영 시간
                                , object.getInt("collaboId")                    // 협업 고유 아이디
                                , object.getInt("collaboStoreId")               // 협업 뒷 가게 고유 아이디
                                , object.getInt("collaboDiscountCondition")     // 앞 가게 할인 조건 ( 최소 금액 )
                                , object.getInt("collaboDiscountRate"));        // 뒷 가게 할인율 ( 정수 )
                        Collabo.add(collaboData); // 협업 정보 저장
                    }

                    infoCollaboAdapter = new InfoCollaboRvAdapter(this, this);  // 리사이클러뷰 어뎁터 객체 생성
                    infoCollaboAdapter.setCollabo(Collabo);    // 어뎁터 객체에 카테고리 정보 저장
                    infoCollaboRv.setAdapter(infoCollaboAdapter);     // 리사이클러뷰 어뎁터 객체 지정

                    infoCollaboLayout.setVisibility(View.VISIBLE);
                    infoCollaboLayout.setPadding(0, 10, 0, 10);
                }else{
                    infoCollaboLayout.setVisibility(View.GONE);
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
                        // 메뉴 데이터 생성 및 저장
                        menuData menuData = new menuData(
                                object.getInt("menuId")         // 메뉴 고유 아이디
                                , object.getInt("storeId")      // 가게 고유 아이디
                                , object.getString("menuName")  // 메뉴 명
                                , object.getInt("menuPrice")    // 메뉴 가격
                                , object.getInt("menuOrder"));  // 메뉴 정렬 순서
                        Menu.add(menuData); // 메뉴 정보 저장
                    }

                    // 어뎁터 생성 및 Set
                    infoMenuAdapter = new InfoMenuRvAdapter(this, Menu, false);  // 리사이클러뷰 어뎁터 객체 생성

                    infoMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                    infoMenuRv.setAdapter(infoMenuAdapter);

                    // 메뉴 레이아웃 보이기
                    infoMenuLayout.setVisibility(View.VISIBLE);

                    // 상하좌우 여백 추가
                    infoMenuLayout.setPadding(0, 10, 0, 10);
                }else{
                    // 데이터 없을 시 메뉴 상단 바 감추기
                    infoMenuLayout.setVisibility(View.GONE);

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

    // 사진 데이터 GET
    private void getInfoPhoto(){
        // GET 방식 파라미터 설정
        String photoPath = PHOTO_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest PhotoRequest = new StringRequest(Request.Method.GET, HOST + photoPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);             // Response를 JsonObject 객체로 생성
                JSONArray photoArr = jsonObject.getJSONArray("photo");    // 객체에 menu라는 Key를 가진 JSONArray 생성

                if(photoArr.length() > 0) {
                    for (int i = 0; i < photoArr.length(); i++) {
                        JSONObject object = photoArr.getJSONObject(i);        // 배열 원소 하나하나 꺼내서 JSONObject 생성
                        // 카테고리 데이터 생성 및 저장
                        photoData photoData = new photoData(
                                object.getInt("photoId")                    // 사진 고유 아이디
                                , object.getInt("userId")                   // 유저 고유 아이디
                                , object.getInt("reviewId")                 // 리뷰 고유 아이디
                                , object.getString("userName")              // 유저 명
                                , HOST + object.getString("photoImagePath") // 사진 이미지 경로
                                , object.getString("reviewDetail")          // 리뷰 내용
                                , object.getInt("reviewScore"));            // 리뷰 별점
                        Photo.add(photoData); // 사진 정보 저장
                    }

                    infoPhotoAdapter = new InfoPhotoRvAdapter(this, this, Photo);  // 리사이클러뷰 어뎁터 객체 생성

                    infoPhotoRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                    infoPhotoRv.setAdapter(infoPhotoAdapter);


                    // 사진 레이아웃 보이기
                    infoPhotoLayout.setVisibility(View.VISIBLE);

                    // 상하좌우 여백 추가
                    infoPhotoLayout.setPadding(0, 10, 0, 10);
                }else{
                    // 사진 레이아웃 숨기기
                    infoPhotoLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoPhotoError", "onErrorResponse : " + error);
        });

        PhotoRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(PhotoRequest);      // RequestQueue에 요청 추가
    }

    // 리뷰 데이터 GET
    private void getInfoReview(){
        // GET 방식 파라미터 설정
        String reviewPath = REVIEW_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest ReviewRequest = new StringRequest(Request.Method.GET, HOST + reviewPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);             // Response를 JsonObject 객체로 생성
                JSONArray reviewArr = jsonObject.getJSONArray("review"); // 객체에 menu라는 Key를 가진 JSONArray 생성

                if(reviewArr.length() > 0) {
                    for (int i = 0; i < reviewArr.length(); i++) {
                        JSONObject object = reviewArr.getJSONObject(i);        // 배열 원소 하나하나 꺼내서 JSONObject 생성
                        // 카테고리 데이터 생성 및 저장
                        reviewData reviewData = new reviewData(
                                object.getInt("reviewId")                       // 리뷰 고유 아이디
                                , object.getInt("userId")                       // 유저 고유 아이디
                                , object.getString("userName")                  // 유저 명
                                , object.getInt("storeId")                      // 가게 고유 아이디
                                , HOST + object.getString("userProfilePath")    // 유저 프로필 경로
                                , object.getString("reviewDetail")              // 리뷰 내용
                                , object.getInt("reviewScore")                  // 리뷰 별점
                                , object.getInt("reviewHeartCount")             // 리뷰 좋아요 수
                                , object.getString("reviewRegDate")             // 리뷰 작성 일자
                                , object.getInt("reviewCommentCount"));         // 리뷰 댓글 수
                        Review.add(reviewData); // 리뷰 정보 저장
                    }

                    infoReviewAdapter = new InfoReviewRvAdapter(this, Review, Photo, Store.getStoreName(), this);  // 리사이클러뷰 어뎁터 객체 생성

                    infoReviewRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                    infoReviewRv.setAdapter(infoReviewAdapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoReviewError", "onErrorResponse : " + error);
        });

        ReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(ReviewRequest);      // RequestQueue에 요청 추가
    }

    // 협업 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoCollaboRvClick(View v, int position) {
        infoCollaboDialog.show();

        // 뷰 정의
        TextView preStoreName = infoCollaboDialog.findViewById(R.id.info_collabo_previous_store_name);  // 앞가게 명
        TextView postStoreName = infoCollaboDialog.findViewById(R.id.info_collabo_post_store_name);     // 뒷가게 명

        ImageButton preStoreImage = infoCollaboDialog.findViewById(R.id.info_collabo_previous_store_image);  // 앞가게 썸네일
        ImageButton postStoreImage = infoCollaboDialog.findViewById(R.id.info_collabo_post_store_image);    // 앞가게 썸네일

        TextView distance = infoCollaboDialog.findViewById(R.id.info_collabo_distance);  // 두 가게 사이 거리
        TextView discount_info = infoCollaboDialog.findViewById(R.id.info_collabo_coupon);  // 할인 정보

        // 값 세팅
        collaboData collabo = Collabo.get(position);    // 현재 Position의 협업 데이터

        preStoreName.setText(Store.getStoreName());     // 앞가게 명
        postStoreName.setText(collabo.getStoreName());    // 뒷가게 명

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적

        // 앞가게 이미지
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(preStoreImage);     // 이미지를 보여줄 View를 지정

        // 뒷가게 이미지
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(collabo.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(postStoreImage);     // 이미지를 보여줄 View를 지정

        // 뒷가게 이미지 클릭 리스너
        postStoreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mainStorePath = MAINSTORE_PATH + String.format("?storeId=%s", collabo.getStoreId());
                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
                StringRequest MainStoreRequest = new StringRequest(Request.Method.GET, HOST + mainStorePath, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                        JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                        JSONObject object = mainStoreArr.getJSONObject(0);

                        mainStoreData mainStore = new mainStoreData(
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
                                , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                                , object.getDouble("storeScore")                // 가게 별점
                                , object.getString("storeWorkingTime")          // 가게 운영 시간
                                , object.getString("storeHashTag")              // 가게 해시태그
                                , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                                , 0); // 현위치에서 가게까지의 거리

                        Intent intent = new Intent(InfoActivity.this, InfoActivity.class); // 가게 상세화면 Activity로 이동하기 위한 Intent 객체 선언
                        intent.putExtra("Store", mainStore);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("getMainStoreError", "onErrorResponse : " + error);
                });

                MainStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(MainStoreRequest);     // RequestQueue에 요청 추가
            }
        });

        // 가게 간 거리 계산을 위한 좌표
        Location prePoint = new Location(Store.getStoreName());   // 앞 가게 위치 Location 객체 생성
        prePoint.setLatitude(Store.getStoreLatitude());
        prePoint.setLongitude(Store.getStoreLongitude());

        // 가게 간 거리 계산을 위한 좌표
        Location postPoint = new Location(collabo.getStoreName());   // 뒷 가게 위치 Location 객체 생성
        postPoint.setLatitude(collabo.getStoreLatitude());
        postPoint.setLongitude(collabo.getStoreLongitude());

        distance.setText(String.format("%.2f Km", prePoint.distanceTo(postPoint) / 1000));

        String dis_info = Store.getStoreName() + "에서 "
                + collabo.getCollaboDiscountCondition() +"원 이상 결제 시 "
                + collabo.getStoreName() + "에서 "
                + collabo.getCollaboDiscountRate() + "% 할인";

        discount_info.setText(dis_info);
    }

    // 사진 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoPhotoRvClick(View v, int position) {
        // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        // position이 4보다 작을 경우 사진 상세 화면으로 이동
        // position이 4보다 클 경우 사진 전체 화면으로 이동
        Intent intent = new Intent(InfoActivity.this, position < 4 ? InfoPhotoActivity.class : InfoPhotoTotalActivity.class);
        intent.putParcelableArrayListExtra("Photo", Photo);
        intent.putExtra("Position", position);
        intent.putExtra("storeName", Store.getStoreName());
        startActivity(intent);
    }

    // 리뷰 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoReviewRvClick(View v, int position) {
        reviewData review = Review.get(position);
        ArrayList<photoData> photo = new ArrayList<>();

        for(int i = 0; i < Photo.size(); i++){
            if(review.getReviewId() == Photo.get(i).getReviewId()){
                photo.add(Photo.get(i));
            }
        }
        // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        Intent intent = new Intent(InfoActivity.this, InfoReviewActivity.class);
        intent.putParcelableArrayListExtra("Photo", photo);
        intent.putExtra("reviewId", review.getReviewId());
        intent.putExtra("storeName", Store.getStoreName());
        intent.putExtra("review", review);
        startActivity(intent);
    }
}

