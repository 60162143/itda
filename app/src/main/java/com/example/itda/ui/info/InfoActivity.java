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

    private RecyclerView infoCollaboRv;             // 협업 가게 리사이클러뷰

    private InfoCollaboRvAdapter infoCollaboAdapter;       // 협업 가게 리사이클러뷰 어뎁터

    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue

    final static private String COLLABO_PATH = "/collabo/getInfoCollabo.php"; // 가게 정보 데이터 조회 Rest API
    final static private String HOST = "http://no2955922.ivyro.net";        // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //텍스트뷰 정의
        //최상단 가게 이름
        TextView infoMainStoreName = findViewById(R.id.info_main_store_name);
        //가게 이름
        TextView infoStoreName = findViewById(R.id.info_store_name);
        //가게 별점
        TextView infoStarScore = findViewById(R.id.info_star_score);
        //가게 간단한 설명
        TextView infoInformation = findViewById(R.id.info_information);
        //가게 해시태그
        TextView infoHashtag = findViewById(R.id.info_hashtag);
        //가게 운영 시간
        TextView infoWorkingTime = findViewById(R.id.info_working_time);
        //가게 주차 가능 여부
        TextView infoParking = findViewById(R.id.info_parking);
        //가게 주소
        TextView infoAddress = findViewById(R.id.info_address);
        //사진 타이틀 가게 이름
        TextView infoPhotoTitle = findViewById(R.id.info_photo_title);
        //리뷰 타이틀 가게 이름
        TextView infoReviewTitle = findViewById(R.id.info_review_title);

        //버튼 정의
        //메뉴 더보기 버튼
        Button infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);
        //사진 더보기 버튼
        Button infoPhotoPlusBtn = findViewById(R.id.info_photo_plus_btn);
        //리뷰 쓰기 버튼
        TextView infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);
        //결제하기 버튼
        Button infoPaymentBtn = findViewById(R.id.info_payment_btn);

        //이미지 정의
        //가게 메인 이미지
        ImageView infoStoreImage = findViewById(R.id.info_store_image);

        //최상단 아이콘 정의
        //최상단 뒤로가기 버튼
        ImageButton infoBackIc = findViewById(R.id.info_back_ic);
        //최상단 전화하기 버튼
        ImageButton infoCallIc = findViewById(R.id.info_call_ic);
        //최상단 찜하기 버튼
        ImageButton infoBookmarkIc = findViewById(R.id.info_bookmark_ic);

        //메뉴 리스트 정의
        //메뉴 리스트뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        ListView infoMenuLv = findViewById(R.id.info_menu_lv);

        //리사이클러뷰 정의
        //협업 리사이클러뷰
        infoCollaboRv = findViewById(R.id.info_collabo_rv);
        //사진 리사이클러뷰
        RecyclerView infoPhotoRv = findViewById(R.id.info_photo_rv);
        //리뷰 리사이클러뷰
        RecyclerView infoReviewRv = findViewById(R.id.info_review_rv);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        Store = getIntent().getParcelableExtra("Store");

        //텍스트 설정
        infoMainStoreName.setText(Store.getStoreName());
        infoStoreName.setText(Store.getStoreName());
        infoStarScore.setText(String.valueOf(Store.getStoreScore()));
        infoInformation.setText(Store.getStoreInfo());
        infoWorkingTime.setText(Store.getStoreWorkingTime());
        if(TextUtils.isEmpty(Store.getStoreParking())){
            infoParking.setText("업데이트 예정");
        }else {
            infoParking.setText(Store.getStoreParking());
        }

        // 이미지 설정
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(infoStoreImage);     // 이미지를 보여줄 View를 지정

        //뒤로가기 버튼 클릭 리스너 입니다.
        infoBackIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getInfoCollabo();   // 협업 가게 데이터 GET
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
                            , object.getString("storeParking")              // 가게 주차 가능 여부
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

