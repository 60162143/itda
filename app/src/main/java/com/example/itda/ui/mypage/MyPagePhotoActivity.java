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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.info.InfoPhotoActivity;
import com.example.itda.ui.info.infoPhotoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPagePhotoActivity extends Activity implements onMyPagePhotoRvClickListener {

    // Layout
    private ImageButton photoBackIc;    // 상단 뒤로가기 버튼
    private Button photoTitle;      // 상단 타이틀
    private TextView photoNoTitle;  // 리사이클러뷰 값 없음 표시 텍스트
    private RecyclerView photoRv;   // 사진 전체 리사이클러뷰
    private Dialog photoDeleteDialog;   // 업로드한 사진 삭제 다이얼로그

    // Adapter
    private MyPagePhotoRvAdapter photoAdapter;   // 사진 전체 리사이클러뷰 어뎁터


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String PHOTO_PATH;  // 사진 정보 데이터 조회 Rest API
    private String DELETE_PHOTO_PATH;   // 업로드 한 사진 삭제 Rest API
    private String HOST;    // Host 정보


    // Data
    private final ArrayList<infoPhotoData> Photo  = new ArrayList<>();  // 사진 데이터

    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_photo_total);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보
        PHOTO_PATH = ((globalMethod) getApplication()).getInfoPhotoPath();  // 사진 정보 데이터 조회 Rest API
        DELETE_PHOTO_PATH = ((globalMethod) getApplication()).deletePhotoPath();    // 업로드 한 사진 삭제 Rest API

        User = getSharedPreferences("user", Activity.MODE_PRIVATE); // 로그인 유저 데이터 GET

        // Init View
        initView();

        // 타이틀 SET
        photoTitle.setText("내가 업로드한 사진");

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        photoBackIc.setOnClickListener(view -> finish());

        // ************* 데이터 가져오기 *******************
        getInfoPhoto();     // 사진 데이터 GET
    }

    // 뷰 생성
    private void initView(){
        photoBackIc = findViewById(R.id.info_photo_total_back_ic);  // 상단 뒤로가기 버튼
        photoTitle = findViewById(R.id.info_photo_total_main_store_name);   // 상단 타이틀
        photoNoTitle = findViewById(R.id.mypage_photo_no_title);    // 리사이클러뷰 값 없음 표시 텍스트
        photoRv = findViewById(R.id.info_photo_total_rv);   // 사진 전체 리사이클러뷰

        // 프로필 변경 Dialog 팝업
        photoDeleteDialog = new Dialog(MyPagePhotoActivity.this);   // Dialog 초기화
        photoDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);   // 타이틀 제거
        photoDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        photoDeleteDialog.setContentView(R.layout.dl_delete);   // xml 레이아웃 파일과 연결
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
                    // 리사이클러뷰 데이터 SET
                    photoAdapter = new MyPagePhotoRvAdapter(this,this , Photo);  // 리사이클러뷰 어뎁터 객체 생성

                    photoRv.setLayoutManager(new GridLayoutManager(this, 3));
                    photoRv.setAdapter(photoAdapter);
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

    // 사진 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onMyPagePhotoRvClick(View v, int position, String flag) {
        if(flag.equals("image")){   // 이미지 클릭 시
            // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
            Intent intent = new Intent(MyPagePhotoActivity.this, InfoPhotoActivity.class);

            intent.putParcelableArrayListExtra("Photo", Photo); // 사진 데이터
            intent.putExtra("Position", position);  // 현재 position
            intent.putExtra("storeName", photoTitle.getText()); // 상단 타이틀

            startActivity(intent);  // 새 Activity 인스턴스 시작
        }else if(flag.equals("delete")){    // 삭제 버튼 클릭 시
            photoDeleteDialog.show();

            // 뷰 정의
            TextView dlTitle = photoDeleteDialog.findViewById(R.id.dl_title);   // 다이얼로그 타이틀
            Button dlConfirmBtn = photoDeleteDialog.findViewById(R.id.dl_confirm_btn);  // 다이얼로그 확인 버튼
            Button dlCloseBtn = photoDeleteDialog.findViewById(R.id.dl_close_btn);  // 다이얼로그 닫기 버튼

            // 데이터 SET
            dlTitle.setText("업로드한 사진을 삭제하시겠습니까?");
            dlConfirmBtn.setText("삭제");

            // 삭제 버튼 클릭 리스너
            dlConfirmBtn.setOnClickListener(view -> {
                // POST 방식 파라미터 설정
                // Param => rvPhotoId : 삭제할 리뷰 고유 아이디
                Map<String, String> param = new HashMap<>();
                param.put("rvPhotoId", String.valueOf(Photo.get(position).getPhotoId()));   // 삭제할 리뷰 고유 아이디

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest deleteReviewRequest = new StringRequest(Request.Method.POST, HOST + DELETE_PHOTO_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");   // Success Flag

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "삭제 성공!", R.style.blueToast).show();

                            photoDeleteDialog.dismiss();    // 다이얼로그 닫기

                            Photo.remove(position); // 데이터 삭제
                            photoAdapter.notifyItemRemoved(position);   // 리사이클러뷰 데이터 삭제

                            // 데이터가 없을 경우 없다는 텍스트 표시
                            if(Photo.size() == 0){
                                photoRv.setVisibility(View.GONE);
                                photoNoTitle.setVisibility(View.VISIBLE);
                            }
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "삭제 실패...", R.style.redToast).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("deleteMyPagePhotoError", "onErrorResponse : " + error);
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
            dlCloseBtn.setOnClickListener(view1 -> photoDeleteDialog.dismiss());
        }
    }
}