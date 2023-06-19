package com.example.itda.ui.mypage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import com.example.itda.ui.info.infoPhotoData;
import com.example.itda.ui.info.infoReviewData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageReviewActivity extends Activity implements onMyPageReviewRvClickListener {

    // Layout
    private ImageButton backIc;     // 상단 뒤로가기 버튼
    private TextView reviewNoTitle; // 리사이클러뷰 값 없음 표시 텍스트
    private RecyclerView reviewRv;      // 작성한 리뷰 리스트 리사이클러뷰
    private Dialog reviewDeleteDialog;  // 작성한 리뷰 목록 삭제 다이얼로그


    // Adapter
    private MyPageReviewRvAdapter reviewAdapter;    // 작성한 리뷰 리사이클러뷰 어뎁터


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String PHOTO_PATH;  // 사진 정보 데이터 조회 Rest API
    private String REVIEW_PATH; // 리뷰 정보 데이터 조회 Rest API
    private String DELETE_REVIEW_PATH;  // 작성 리뷰 삭제 Rest API
    private String HOST;    // Host 정보


    // Data
    private final ArrayList<infoReviewData> Review = new ArrayList<>(); // 리뷰 데이터
    private final ArrayList<infoPhotoData> Photo = new ArrayList<>();   // 사진 데이터

    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_review);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보
        PHOTO_PATH = ((globalMethod) getApplication()).getInfoPhotoPath();      // 사진 정보 데이터 조회 Rest API
        REVIEW_PATH = ((globalMethod) getApplication()).getInfoReviewPath();    // 리뷰 정보 데이터 조회 Rest API
        DELETE_REVIEW_PATH = ((globalMethod) getApplication()).deleteReviewPath();  // 작성 리뷰 삭제 Rest API

        // Init View
        initView();

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // ************* 데이터 가져오기 *******************
        getInfoPhoto();     // 사진 데이터 GET
        getInfoReview();    // 리뷰 데이터 GET
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_review_back_ic);  // 상단 뒤로가기 버튼
        reviewNoTitle = findViewById(R.id.mypage_review_no_title);  // 리사이클러뷰 값 없음 표시 텍스트
        reviewRv = findViewById(R.id.mypage_review_rv); // 작성한 리뷰 리스트 리사이클러뷰

        // 프로필 변경 Dialog 팝업
        reviewDeleteDialog = new Dialog(MyPageReviewActivity.this); // Dialog 초기화
        reviewDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);  // 타이틀 제거
        reviewDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // 배경 색 제거
        reviewDeleteDialog.setContentView(R.layout.dl_delete);  // xml 레이아웃 파일과 연결
    }

    // 사진 데이터 GET
    private void getInfoPhoto(){
        // GET 방식 파라미터 설정
        // Param => userId : 유저 고유 아이디
        String photoPath = PHOTO_PATH + String.format("?userId=%s", User.getInt("userId", 0));  // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest PhotoRequest = new StringRequest(Request.Method.GET, HOST + photoPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray photoArr = jsonObject.getJSONArray("photo");  // 객체에 photo라는 Key를 가진 JSONArray 생성

                if(photoArr.length() > 0) {
                    for (int i = 0; i < photoArr.length(); i++) {
                        JSONObject object = photoArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 사진 데이터 생성 및 저장
                        infoPhotoData infoPhotoData = new infoPhotoData(
                                object.getInt("photoId")        // 사진 고유 아이디
                                , object.getInt("userId")       // 유저 고유 아이디
                                , object.getInt("reviewId")     // 리뷰 고유 아이디
                                , object.getString("userName")  // 유저 명
                                , HOST + object.getString("photoImagePath") // 사진 이미지 경로
                                , object.getString("reviewDetail")  // 리뷰 내용
                                , object.getInt("reviewScore"));    // 리뷰 별점

                        Photo.add(infoPhotoData); // 사진 정보 저장
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getMyPageInfoPhotoError", "onErrorResponse : " + error);
        });

        PhotoRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(PhotoRequest);      // RequestQueue에 요청 추가
    }

    // 리뷰 데이터 GET
    private void getInfoReview(){
        // GET 방식 파라미터 설정
        // Param => userId : 유저 고유 아이디
        String reviewPath = REVIEW_PATH + String.format("?userId=%s", User.getInt("userId", 0));

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest ReviewRequest = new StringRequest(Request.Method.GET, HOST + reviewPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray reviewArr = jsonObject.getJSONArray("review");    // 객체에 review라는 Key를 가진 JSONArray 생성

                if(reviewArr.length() > 0) {
                    for (int i = 0; i < reviewArr.length(); i++) {
                        JSONObject object = reviewArr.getJSONObject(i); // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 리뷰 데이터 생성 및 저장
                        infoReviewData infoReviewData = new infoReviewData(
                                object.getInt("reviewId")       // 리뷰 고유 아이디
                                , object.getInt("userId")       // 유저 고유 아이디
                                , object.getString("userName")  // 유저 명
                                , object.getInt("storeId")      // 가게 고유 아이디
                                , HOST + object.getString("userProfilePath")    // 유저 프로필 경로
                                , object.getString("reviewDetail")  // 리뷰 내용
                                , object.getInt("reviewScore")      // 리뷰 별점
                                , object.getInt("reviewHeartCount") // 리뷰 좋아요 수
                                , object.getString("reviewRegDate") // 리뷰 작성 일자
                                , object.getInt("reviewCommentCount")   // 리뷰 댓글 수
                                , object.getInt("reviewHeartIsClick")); // 로그인 했을 경우 좋아요 눌렀는지 확인 Flag ( 누른 경우 : 1, 안눌렀거나 비로그인 시 : 0 )

                        Review.add(0, infoReviewData); // 리뷰 정보 저장
                    }
                    // LayoutManager 객체 생성
                    reviewRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

                    reviewAdapter = new MyPageReviewRvAdapter(this, Review, Photo, "내가 쓴 리뷰", this);  // 리사이클러뷰 어뎁터 객체 생성
                    reviewRv.setAdapter(reviewAdapter); // 리사이클러뷰 어뎁터 객체 지정

                }else {
                    reviewRv.setVisibility(View.GONE);
                    reviewNoTitle.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getMyPageReviewError", "onErrorResponse : " + error);
        });

        ReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(ReviewRequest);      // RequestQueue에 요청 추가
    }

    @Override
    public void onMyPageReviewRvClick(View v, int position, String flag) {
        if(flag.equals("total")){   // 리사이클러뷰 전체 클릭
            infoReviewData review = Review.get(position);       // 리뷰 데이터
            ArrayList<infoPhotoData> photo = new ArrayList<>(); // 리뷰 사진 데이터

            // 현재 리뷰에 속한 시진 데이터만 저장
            for(int i = 0; i < Photo.size(); i++){
                if(review.getReviewId() == Photo.get(i).getReviewId()){
                    photo.add(Photo.get(i));
                }
            }

            // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
            Intent intent = new Intent(MyPageReviewActivity.this, MyPageReviewDetailActivity.class);

            intent.putParcelableArrayListExtra("Photo", photo); // 사진 데이터
            intent.putExtra("reviewId", review.getReviewId());  // 리뷰 고유 아이디
            intent.putExtra("storeName", "내가 쓴 리뷰");   // 타이틀
            intent.putExtra("review", review);  // 리뷰 데이터

            startActivity(intent);  // 새 Activity 인스턴스 시작
        }else if(flag.equals("delete")){    // 리사이클러뷰 삭제 버튼 클릭
            reviewDeleteDialog.show();

            // 뷰 정의
            TextView dlTitle = reviewDeleteDialog.findViewById(R.id.dl_title);  // 다이얼로그 타이틀
            Button dlConfirmBtn = reviewDeleteDialog.findViewById(R.id.dl_confirm_btn); // 다이얼로그 확인 버튼
            Button dlCloseBtn = reviewDeleteDialog.findViewById(R.id.dl_close_btn); // 다이얼로그 닫기 버튼

            // 데이터 SET
            dlTitle.setText("작성한 리뷰를 삭제하시겠습니까?");
            dlConfirmBtn.setText("삭제");

            // 삭제 버튼 클릭 리스너
            dlConfirmBtn.setOnClickListener(view -> {
                // POST 방식 파라미터 설정
                // Param => reviewId : 삭제할 리뷰 고유 아이디
                Map<String, String> param = new HashMap<>();
                param.put("reviewId", String.valueOf(Review.get(position).getReviewId()));  // 삭제할 리뷰 고유 아이디

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest deleteReviewRequest = new StringRequest(Request.Method.POST, HOST + DELETE_REVIEW_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");   // Success Flag

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "삭제 성공!", R.style.blueToast).show();

                            reviewDeleteDialog.dismiss();   // 다이얼로그 닫기

                            Review.remove(position);    // 데이터 삭제
                            reviewAdapter.notifyItemRemoved(position);  // 리사이클러뷰 데이터 삭제

                            // 데이터가 없을 경우 없다는 텍스트 표시
                            if(Review.size() == 0){
                                reviewRv.setVisibility(View.GONE);
                                reviewNoTitle.setVisibility(View.VISIBLE);
                            }
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "삭제 실패...", R.style.redToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("deleteMyPageReviewError", "onErrorResponse : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams(){
                        // php로 설정값을 보낼 수 있음 ( POST )
                        return param;
                    }
                };

                deleteReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(deleteReviewRequest);      // RequestQueue에 요청 추가
            });

            // 닫기 버튼 클릭 리스너
            dlCloseBtn.setOnClickListener(view1 -> reviewDeleteDialog.dismiss());
        }
    }
}