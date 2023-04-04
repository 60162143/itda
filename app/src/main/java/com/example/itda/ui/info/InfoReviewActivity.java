package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


// 여기부터 수정합시다!!!
public class InfoReviewActivity extends Activity implements onInfoReviewPhotoRvClickListener{

    private ImageButton infoReviewBackIc;       // 상단 뒤로가기 버튼
    private ImageButton infoReviewUserProfile;  // 유저 프로필 사진

    private Button infoReviewStoreName;         // 상단 가게 이름
    private Button infoReviewHeartBtn;          // 리뷰 좋아요 버튼
    private Button infoReviewCommentBtn;        // 리뷰 댓글 등록 버튼

    private TextView infoReviewUserName;        // 유저 명
    private TextView infoReviewHeartCount;      // 리뷰 좋아요 수
    private TextView infoReviewCommentCount;    // 리뷰 댓글 수
    private TextView infoReviewScore;           // 리뷰 별점
    private TextView infoReviewRegDate;         // 리뷰 작성 일자
    private TextView infoReviewContent;         // 리뷰 내용

    private RecyclerView infoReviewPhotoRv;     // 리뷰 사진 리사이클러뷰
    private RecyclerView infoReviewCommentRv;   // 리뷰 댓글 리사이클러뷰

    private EditText infoReviewComment;         // 리뷰 댓글 내용

    private String storeName;   // 가게 이름
    private int reviewId;       // 리뷰 고유 아이디
    private reviewData Review; // 리뷰 데이터
    private ArrayList<photoData> Photos; // 리뷰 사진 데이터
    private ArrayList<reviewCommentData> ReviewComments = new ArrayList<>(); // 리뷰 댓글 데이터

    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue
    private String REVIEW_COMMENT_PATH;    // 리뷰 댓글 정보 데이터 조회 Rest API
    private String HOST;        // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_review);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        REVIEW_COMMENT_PATH = ((globalVariable) getApplication()).getInfoReviewCommentPath();   // 리뷰 댓글 정보 데이터 조회 Rest API
        HOST = ((globalVariable) getApplication()).getHost();                               // Host 정보

        initView();

        storeName = getIntent().getExtras().getString("storeName");     // 가게 명
        infoReviewStoreName.setText(storeName);

        reviewId = getIntent().getIntExtra("reviewId", 0);  // 선택한 리뷰 고유 아이디

        Review = getIntent().getParcelableExtra("review");            // 리뷰 데이터

        // 유저 프로필 사진
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Review.getUserProfilePath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(infoReviewUserProfile);           // 이미지를 보여줄 View를 지정

        infoReviewUserName.setText(Review.getUserName());                               // 유저 명
        infoReviewHeartCount.setText(String.valueOf(Review.getReviewHeartCount()));     // 리뷰 좋아요 수
        infoReviewCommentCount.setText(String.valueOf(Review.getReviewCommentCount())); // 리뷰 댓글 수
        infoReviewScore.setText(String.valueOf(Review.getReviewScore()));               // 리뷰 별점
        infoReviewRegDate.setText(Review.getReviewRegDate());                           // 리뷰 작성 일자
        infoReviewContent.setText(Review.getReviewDetail());                            // 리뷰 내용


        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        Photos = getIntent().getParcelableArrayListExtra("Photo");  // 사진 데이터

        InfoReviewPhotoRvAdapter InfoReviewPhotoRvAdapter = new InfoReviewPhotoRvAdapter(this, this, Photos);  // 리사이클러뷰 어뎁터 객체 생성

        infoReviewPhotoRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        infoReviewPhotoRv.setAdapter(InfoReviewPhotoRvAdapter);

        // 리뷰 댓글 데이터 GET
        getInfoReviewComment();
    }

    private void initView(){
        infoReviewBackIc = findViewById(R.id.info_review_detail_back_ic);               // 상단 뒤로가기 버튼
        infoReviewUserProfile = findViewById(R.id.info_review_detail_user_profile);     // 유저 프로필 사진
        infoReviewStoreName = findViewById(R.id.info_review_detail_main_store_name);    // 상단 가게 이름
        infoReviewHeartBtn = findViewById(R.id.info_review_detail_heart_btn);           // 리뷰 좋아요 버튼
        infoReviewCommentBtn = findViewById(R.id.info_review_detail_comment_btn);       // 리뷰 댓글 버튼
        infoReviewUserName = findViewById(R.id.info_review_detail_user_name);           // 유저 명
        infoReviewHeartCount = findViewById(R.id.info_review_detail_heart_count);       // 리뷰 좋아요 수
        infoReviewCommentCount = findViewById(R.id.info_review_detail_comment_count);   // 리뷰 댓글 수
        infoReviewScore = findViewById(R.id.info_review_detail_score);                  // 리뷰 별점
        infoReviewRegDate = findViewById(R.id.info_review_detail_regdate);              // 리뷰 작성 일자
        infoReviewContent = findViewById(R.id.info_review_detail_content);              // 리뷰 내용
        infoReviewPhotoRv = findViewById(R.id.info_review_detail_photo);                // 리뷰 사진 리사이클러뷰
        infoReviewCommentRv = findViewById(R.id.info_review_detail_comment_rv);         // 리뷰 댓글 리사이클러뷰
        infoReviewComment = findViewById(R.id.info_review_detail_comment);              // 리뷰 댓글

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoReviewBackIc.setOnClickListener(view -> finish());
    }

    // 리뷰 댓글 데이터 GET
    private void getInfoReviewComment(){
        // GET 방식 파라미터 설정
        String reviewCommentPath = REVIEW_COMMENT_PATH + String.format("?reviewId=%s", reviewId);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest ReviewCommentRequest = new StringRequest(Request.Method.GET, HOST + reviewCommentPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);               // Response를 JsonObject 객체로 생성
                JSONArray reviewCommentArr = jsonObject.getJSONArray("comment"); // 객체에 comment라는 Key를 가진 JSONArray 생성

                if(reviewCommentArr.length() > 0) {
                    for (int i = 0; i < reviewCommentArr.length(); i++) {
                        JSONObject object = reviewCommentArr.getJSONObject(i);        // 배열 원소 하나하나 꺼내서 JSONObject 생성
                        // 카테고리 데이터 생성 및 저장
                        reviewCommentData reviewCommentData = new reviewCommentData(
                                object.getInt("reviewCommentId")                        // 리뷰 댓글 고유 아이디
                                , object.getInt("reviewId")                             // 리뷰 고유 아이디
                                , object.getInt("userId")                               // 유저 고유 아이디
                                , object.getInt("storeId")                              // 가게 고유 아이디
                                ,  object.getString("reviewCommentDetail")              // 리뷰 내용
                                , object.getString("reviewRegDate")                     // 리뷰 작성 일자
                                , object.getString("userName")                          // 유저 명
                                , HOST + object.getString("userProfilePath")); // 유저 프로필 사진
                        ReviewComments.add(reviewCommentData); // 리뷰 정보 저장
                    }

                    InfoReviewCommentRvAdapter infoReviewCommentRvAdapter= new InfoReviewCommentRvAdapter(this, ReviewComments);  // 리사이클러뷰 어뎁터 객체 생성

                    infoReviewCommentRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
                    infoReviewCommentRv.setAdapter(infoReviewCommentRvAdapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoReviewCommentError", "onErrorResponse : " + error);
        });

        ReviewCommentRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(ReviewCommentRequest);      // RequestQueue에 요청 추가
    }

    // 사진 리사이클러뷰 클릭 이벤트 구현
    @Override
    public void onInfoReviewPhotoRvClick(View v, int position) {
        // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        Intent intent = new Intent(InfoReviewActivity.this, InfoPhotoActivity.class);
        intent.putParcelableArrayListExtra("Photo", Photos);
        intent.putExtra("Position", position);
        intent.putExtra("storeName", storeName);
        startActivity(intent);
    }
}

