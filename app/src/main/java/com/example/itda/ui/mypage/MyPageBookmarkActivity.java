package com.example.itda.ui.mypage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.map.MapRvAdapter;
import com.example.itda.ui.map.MapStoreData;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageBookmarkActivity extends Activity {
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private Button bookmarkStoreBtn;  // 찜한 가게 버튼
    private Button bookmarkCollaboBtn; // 찜한 협업 리스트 버튼

    private RecyclerView bookmarkStoreRv;    // 찜한 가게 리사이클러뷰
    private RecyclerView bookmarkCollaboRv;    // 찜한 협업 리스트 리사이클러뷰

    private MyPageBookmarkStoreRvAdapter bookmarkStoreAdapter;   // 찜한 가게 리사이클러뷰 어뎁터
    private MyPageBookmarkCollaboRvAdapter bookmarkCollaboAdapter;   // 찜한 협업 목록 리사이클러뷰 어뎁터

    private ArrayList<MyPageBookmarkStoreData> bookmarkStoreData = new ArrayList<>();      // 유저 찜한 가게 데이터
    private ArrayList<MyPageBookmarkCollaboData> bookmarkCollaboData = new ArrayList<>();      // 유저 찜한 협업 목록 데이터

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )
    private String BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 조회 Rest API
    private String BOOKMARK_COLLABO_PATH;      // 유저 찜한 협업 목록 조회 Rest API
    private String HOST;            // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_bookmark);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).getBookmarkStorePath();    // 유저 찜한 가게 목록 조회 Rest API
        BOOKMARK_COLLABO_PATH = ((globalMethod) getApplication()).getBookmarkCollaboPath();    // 유저 찜한 협업 목록 조회 Rest API

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // 찝한 협업 리스트 버튼 클릭 리스너
        bookmarkStoreBtn.setOnClickListener(view -> {
            bookmarkStoreBtn.setEnabled(false);
            bookmarkStoreBtn.setBackgroundResource(R.drawable.round_select_color_little);
            bookmarkCollaboBtn.setEnabled(true);
            bookmarkCollaboBtn.setBackgroundResource(R.drawable.round_unselect_color_little);

            bookmarkStoreRv.setVisibility(View.VISIBLE);
            bookmarkCollaboRv.setVisibility(View.GONE);
        });

        // 찝한 협업 리스트 버튼 클릭 리스너
        bookmarkCollaboBtn.setOnClickListener(view -> {
            bookmarkStoreBtn.setEnabled(true);
            bookmarkStoreBtn.setBackgroundResource(R.drawable.round_unselect_color_little);
            bookmarkCollaboBtn.setEnabled(false);
            bookmarkCollaboBtn.setBackgroundResource(R.drawable.round_select_color_little);

            bookmarkStoreRv.setVisibility(View.GONE);
            bookmarkCollaboRv.setVisibility(View.VISIBLE);
        });

        // ************* 데이터 가져오기 *******************
        getBookmarkStoreData(); // 찜한 가게 목록 가져오기
        getBookmarkCollaboData(); // 찜한 협업 목록 가져오기
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_bookmark_back_ic); // 상단 뒤로가기 버튼
        bookmarkStoreBtn = findViewById(R.id.mypage_bookmark_store_btn);  // 찜한 가게 버튼
        bookmarkCollaboBtn = findViewById(R.id.mypage_bookmark_collabo_btn);  // 찜한 협업 리스트 버튼
        bookmarkStoreRv = findViewById(R.id.mypage_bookmark_store_rv);  // 찜한 가게 리사이클러뷰
        bookmarkCollaboRv = findViewById(R.id.mypage_bookmark_collabo_rv);  // 찜한 협업 리스트 리사이클러뷰
    }

    private void getBookmarkStoreData(){
        // LayoutManager 객체 생성
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        bookmarkStoreRv.setLayoutManager(llm);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
        // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
        // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
        //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location loc_Current = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double cur_lat = loc_Current.getLatitude();     // 현재 위치의 위도
        double cur_lon = loc_Current.getLongitude();    // 현재 위치의 경도

        // GET 방식 파라미터 설정
        String bookmarkStorePath = BOOKMARK_STORE_PATH;
        bookmarkStorePath += String.format("?latitude=%s", cur_lat);     // 위도 파라미터 설정
        bookmarkStorePath += String.format("&&longitude=%s", cur_lon);   // 경도 파라미터 설정
        bookmarkStorePath += String.format("&&userId=%s", User.getInt("userId", 0));    // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkStoreRequest = new StringRequest(Request.Method.GET, HOST + bookmarkStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray bookmarkStoreArr = jsonObject.getJSONArray("store");   // 객체에 store라는 Key를 가진 JSONArray 생성

                if(bookmarkStoreArr.length() > 0){
                    for(int i = 0; i < bookmarkStoreArr.length(); i++){
                        JSONObject object = bookmarkStoreArr.getJSONObject(i);   // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 현재 위치에서 가게 까지의 거리 계산을 위한 좌표
                        Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                        point.setLatitude(object.getDouble("storeLatitude"));       // 가게 위도
                        point.setLongitude(object.getDouble("storeLongitude"));     // 가게 경도

                        MyPageBookmarkStoreData bookmarkStore = new MyPageBookmarkStoreData(object.getInt("storeId")   // 가게 고유 아이디
                                , object.getString("storeName")                             // 가게 이름
                                , HOST + object.getString("storeThumbnailPath")             // 가게 썸네일 이미지 경로
                                , Float.parseFloat(object.getString("storeScore"))          // 가게 별점
                                , object.getDouble("storeLatitude")                         // 가게 위도
                                , object.getDouble("storeLongitude")                        // 가게 경도
                                , loc_Current.distanceTo(point) / 1000                            // 현재 위치에서 떨어진 거리, 단위 : km
                                , object.getString("storeInfo")                             // 가게 간단 정보
                                , object.getString("storeHashTag")                          // 가게 해시태그
                                , object.getString("bookmarkStoreId"));                     // 찜한 가게 목록 고유 아이디

                        bookmarkStoreData.add(bookmarkStore);    // 가게 데이터 추가
                    }
                }else{
                    // 검색 결과가 없을 시 Toast 메시지 출력
                    Toast.makeText(this, "찜한 가게 목록이 없습니다.",Toast.LENGTH_SHORT).show();
                }

                bookmarkStoreAdapter = new MyPageBookmarkStoreRvAdapter(this, bookmarkStoreData);  // 리사이클러뷰 어뎁터 객체 생성
                bookmarkStoreRv.setAdapter(bookmarkStoreAdapter); // 리사이클러뷰 어뎁터 객체 지정
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getBookmarkStoreDataError", "onErrorResponse : " + error);
        });

        bookmarkStoreRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(bookmarkStoreRequest);      // RequestQueue에 요청 추가
    }

    private void getBookmarkCollaboData(){
        // LayoutManager 객체 생성
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        bookmarkCollaboRv.setLayoutManager(llm);

        // GET 방식 파라미터 설정
        String bookmarkCollaboPath = BOOKMARK_COLLABO_PATH;
        bookmarkCollaboPath += String.format("?userId=%s", User.getInt("userId", 0));     // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkCollaboRequest = new StringRequest(Request.Method.GET, HOST + bookmarkCollaboPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray bookmarkCollaboArr = jsonObject.getJSONArray("collabo");   // 객체에 store라는 Key를 가진 JSONArray 생성

                if(bookmarkCollaboArr.length() > 0){
                    for(int i = 0; i < bookmarkCollaboArr.length(); i++){
                        JSONObject object = bookmarkCollaboArr.getJSONObject(i);   // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        MyPageBookmarkCollaboData bookmarkCollabo = new MyPageBookmarkCollaboData(object.getInt("bookmarkCollaboId")   // 찜한 협업 리스트 고유 아이디
                                , object.getInt("prvStoreId")       // 앞 가게 고유 아이디
                                , object.getString("prvStoreName")  // 앞 가게 명
                                , HOST + object.getString("prvStoreImagePath")  // 앞 가게 이미지 경로
                                , object.getInt("prvDiscountCondition") // 앞 가게 할인 조건
                                , object.getInt("postStoreId")          // 뒷 가게 고유 아이디
                                , object.getString("postStoreName")     // 뒷 가게 명
                                , HOST + object.getString("postStoreImagePath") // 뒷 가게 할인 율
                                , object.getInt("postDiscountRate") // 가게 간 거리, 단위 : km
                                , Float.parseFloat(object.getString("distance"))); // 찜한 가게 목록 고유 아이디

                        bookmarkCollaboData.add(bookmarkCollabo);    // 협업 데이터 추가

                        System.out.println("distance : " + object.getString("distance"));
                    }
                }else{
                    // 검색 결과가 없을 시 Toast 메시지 출력
                    Toast.makeText(this, "찜한 협업 목록이 없습니다.",Toast.LENGTH_SHORT).show();
                }

                bookmarkCollaboAdapter = new MyPageBookmarkCollaboRvAdapter(this, bookmarkCollaboData);  // 리사이클러뷰 어뎁터 객체 생성
                bookmarkCollaboRv.setAdapter(bookmarkCollaboAdapter); // 리사이클러뷰 어뎁터 객체 지정
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getBookmarkCollaboDataError", "onErrorResponse : " + error);
        });

        bookmarkCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(bookmarkCollaboRequest);      // RequestQueue에 요청 추가
    }
}

