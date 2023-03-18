package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.ui.home.CategoryRvAdapter;
import com.example.itda.ui.home.HomeSearchActivity;
import com.example.itda.ui.home.mainCategoryData;
import com.example.itda.ui.home.mainStoreData;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoActivity extends Activity {

    private mainStoreData Store;
    private ArrayList<collaboData> Collabo = new ArrayList<>();

    private TextView infoMainStoreName; // 최상단 가게 이름
    private TextView infoStoreName; // 가게 이름
    private TextView infoStarScore; // 가게 별점
    private TextView infoInformation; // 가게 간단한 설명
    private TextView infoHashtag; // 가게 해시태그
    private TextView infoWorkingTime; // 가게 운영 시간
    private TextView infoDetail; // 가게 간단 제공 서비스
    private TextView infoFacility; // 가게 제공 시설 여부
    private TextView infoAddress; // 가게 주소
    private TextView infoPhotoTitle; // 사진 타이틀 가게 이름
    private TextView infoReviewTitle; // 리뷰 타이틀 가게 이름
    private Button infoMenuPlusBtn; // 메뉴 더보기 버튼
    private Button infoPhotoPlusBtn; // 사진 더보기 버튼
    private TextView infoReviewPlusBtn; // 리뷰 쓰기 버튼
    private Button infoPaymentBtn; // 결제하기 버튼
    private ImageView infoStoreImage; // 가게 메인 이미지
    private ImageButton infoBackIc; // 최상단 뒤로가기 버튼
    private ImageButton infoCallIc; // 최상단 전화하기 버튼
    private ImageButton infoBookmarkIc; // 최상단 찜하기 버튼
    private ImageButton infoWorkingTimeDownIc; // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
    private ImageButton infoWorkingTimeUpIc; // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
    private ListView infoMenuLv; // 메뉴 리스트뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
    private RecyclerView infoCollaboRv;             // 협업 가게 리사이클러뷰
    private RecyclerView infoPhotoRv; // 사진 리사이클러뷰
    private RecyclerView infoReviewRv; // 리뷰 리사이클러뷰
    private InfoCollaboRvAdapter infoCollaboAdapter;       // 협업 가게 리사이클러뷰 어뎁터

    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue
    final static private String COLLABO_PATH = "/collabo/getInfoCollabo.php"; // 가게 정보 데이터 조회 Rest API
    final static private String HOST = "http://no2955922.ivyro.net";        // Host 정보

    private String[] workingTimeArr;    // 운영 시간 텍스트를 구분자 "&&"으로 Split한 배열
    private boolean isWorkingTimeMore = false;  // 가게 운영 시간 더보기 버튼이 눌러졌는지 확인
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

        //텍스트 설정
        infoMainStoreName.setText(Store.getStoreName());    // 최상단 가게 이름
        infoStoreName.setText(Store.getStoreName());        // 가게 이름
        infoStarScore.setText(String.valueOf(Store.getStoreScore()));   // 가게 별점
        infoInformation.setText(Store.getStoreInfo());      // 가게 간단 정보

        // 가게 운영 시간
        if(!TextUtils.isEmpty(Store.getStoreWorkingTime())){
            workingTimeArr = Store.getStoreWorkingTime().split("\\n");
            infoWorkingTime.setText(workingTimeArr[0]);
        }else{
            infoWorkingTime.setText("등록되어 있지 않습니다.");
            infoWorkingTimeDownIc.setVisibility(View.GONE);
        }

        // 이미지 설정
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(infoStoreImage);     // 이미지를 보여줄 View를 지정

        // 최상단 뒤로가기 버튼 클릭 리스너
        infoBackIc.setOnClickListener(v -> finish());   // 버튼 클릭 시 Activity 종료

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

        getInfoCollabo();   // 협업 가게 데이터 GET
    }

    private void initView(){
        //텍스트뷰 정의
        //최상단 가게 이름
        infoMainStoreName = findViewById(R.id.info_main_store_name);
        //가게 이름
        infoStoreName = findViewById(R.id.info_store_name);
        //가게 별점
        infoStarScore = findViewById(R.id.info_star_score);
        //가게 간단한 설명
        infoInformation = findViewById(R.id.info_information);
        //가게 해시태그
        infoHashtag = findViewById(R.id.info_hashtag);
        //가게 운영 시간
        infoWorkingTime = findViewById(R.id.info_working_time);
        //가게 간단 제공 서비스
        infoDetail = findViewById(R.id.info_detail);
        //가게 제공 시설 여부
        infoFacility = findViewById(R.id.info_facility);
        //가게 주소
        infoAddress = findViewById(R.id.info_address);
        //사진 타이틀 가게 이름
        infoPhotoTitle = findViewById(R.id.info_photo_title);
        //리뷰 타이틀 가게 이름
        infoReviewTitle = findViewById(R.id.info_review_title);

        //버튼 정의
        //메뉴 더보기 버튼
        infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);
        //사진 더보기 버튼
        infoPhotoPlusBtn = findViewById(R.id.info_photo_plus_btn);
        //리뷰 쓰기 버튼
        infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);
        //결제하기 버튼
        infoPaymentBtn = findViewById(R.id.info_payment_btn);

        //이미지 정의
        //가게 메인 이미지
        infoStoreImage = findViewById(R.id.info_store_image);

        //최상단 아이콘 정의
        //최상단 뒤로가기 버튼
        infoBackIc = findViewById(R.id.info_back_ic);
        //최상단 전화하기 버튼
        infoCallIc = findViewById(R.id.info_call_ic);
        //최상단 찜하기 버튼
        infoBookmarkIc = findViewById(R.id.info_bookmark_ic);
        // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
        infoWorkingTimeDownIc = findViewById(R.id.info_working_time_down_arrow_ic);
        // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
        infoWorkingTimeUpIc = findViewById(R.id.info_working_time_up_arrow_ic);

        //메뉴 리스트 정의
        //메뉴 리스트뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        infoMenuLv = findViewById(R.id.info_menu_lv);

        //리사이클러뷰 정의
        //협업 리사이클러뷰
        infoCollaboRv = findViewById(R.id.info_collabo_rv);
        //사진 리사이클러뷰
        infoPhotoRv = findViewById(R.id.info_photo_rv);
        //리뷰 리사이클러뷰
        infoReviewRv = findViewById(R.id.info_review_rv);
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
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");  // 객체에 category라는 Key를 가진 JSONArray 생성

                for(int i = 0; i < collaboArr.length(); i++){
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getCategoryError", "onErrorResponse : " + error);
        });

        CollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(CollaboRequest);      // RequestQueue에 요청 추가
    }
}

