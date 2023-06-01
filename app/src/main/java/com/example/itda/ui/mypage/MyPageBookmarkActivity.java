package com.example.itda.ui.mypage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.HomeSearchActivity;
import com.example.itda.ui.home.MainStoreRvAdapter;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.info.onInfoCollaboRvClickListener;
import com.example.itda.ui.map.MapRvAdapter;
import com.example.itda.ui.map.MapStoreData;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageBookmarkActivity extends AppCompatActivity implements onMyPageBookmarkStoreRvClickListener, onMyPageBookmarkCollaboRvClickListener {
    private ImageButton backIc; // 상단 뒤로가기 버튼

    private Button bookmarkStoreBtn;  // 찜한 가게 버튼
    private Button bookmarkCollaboBtn; // 찜한 협업 리스트 버튼

    private TextView bookmarkNoTitle;   // 리사이클러뷰 값 없음 표시 텍스트
    private RecyclerView bookmarkStoreRv;    // 찜한 가게 리사이클러뷰
    private RecyclerView bookmarkCollaboRv;    // 찜한 협업 리스트 리사이클러뷰

    private MyPageBookmarkStoreRvAdapter bookmarkStoreAdapter;   // 찜한 가게 리사이클러뷰 어뎁터
    private MyPageBookmarkCollaboRvAdapter bookmarkCollaboAdapter;   // 찜한 협업 목록 리사이클러뷰 어뎁터

    private Dialog bookmarkDeleteDialog;   // 찜한 협업 목록 삭제 다이얼로그

    private final ArrayList<mainStoreData> MainStore = new ArrayList<>();  // 가게 정보 저장
    private ArrayList<MyPageBookmarkStoreData> bookmarkStoreData = new ArrayList<>();      // 유저 찜한 가게 데이터
    private ArrayList<mainBookmarkStoreData> infoBookmarkStoreData = new ArrayList<>();      // 유저 찜한 가게 간단 데이터 ( intent용 )
    private ArrayList<MyPageBookmarkCollaboData> bookmarkCollaboData = new ArrayList<>();      // 유저 찜한 협업 목록 데이터

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )
    private String STORE_PATH;      // 가게 정보 데이터 조회 Rest API
    private String BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 조회 Rest API
    private String BOOKMARK_COLLABO_PATH;      // 유저 찜한 협업 목록 조회 Rest API
    private String BOOKMARK_STORE_DELETE_PATH;      // 유저 찜한 가게 목록 삭제 Rest API
    private String BOOKMARK_COLLABO_DELETE_PATH;      // 유저 찜한 협업 목록 삭제 Rest API
    private String HOST;            // Host 정보

    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            StyleableToast.makeText(getApplicationContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", R.style.redToast).show();
        }
    };

    // Gps 권한 허용 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                        //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_bookmark);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        STORE_PATH = ((globalMethod) getApplication()).getMainStorePath();      // 가게 정보 데이터 조회 Rest API
        BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).getBookmarkStorePath();    // 유저 찜한 가게 목록 조회 Rest API
        BOOKMARK_COLLABO_PATH = ((globalMethod) getApplication()).getBookmarkCollaboPath();    // 유저 찜한 협업 목록 조회 Rest API
        BOOKMARK_STORE_DELETE_PATH = ((globalMethod) getApplication()).deleteBookmarkStorePath();    // 유저 찜한 가게 목록 삭제 Rest API
        BOOKMARK_COLLABO_DELETE_PATH = ((globalMethod) getApplication()).deleteBookmarkCollaboPath();    // 유저 찜한 협업 목록 삭제 Rest API

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 2000){   // resultCode가 2000로 넘어왔다면 InfoActivity에서 넘어온것
                infoBookmarkStoreData = result.getData().getParcelableArrayListExtra("bookmarkStore");    // 찜한 목록 데이터 GET

                int storeId = result.getData().getIntExtra("storeId", 0);   // 가게 고유 아이디

                // 찜 목록에서 제거 되었는지 확인
                if(infoBookmarkStoreData != null && infoBookmarkStoreData.size() > 0){
                    if(infoBookmarkStoreData.size() != bookmarkStoreData.size()){
                        for(int i = 0; i < bookmarkStoreData.size(); i++){
                            if(bookmarkStoreData.get(i).getStoreId() == storeId){
                                bookmarkStoreData.remove(i);
                                bookmarkStoreAdapter.notifyItemRemoved(i);  // 리사이클러뷰 데이터 삭제

                                break;
                            }
                        }
                    }
                }
            }
        });

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // 찜한 가게 버튼 클릭 리스너
        bookmarkStoreBtn.setOnClickListener(view -> {
            // 버튼 활성화 / 비활성화
            bookmarkStoreBtn.setEnabled(false);
            bookmarkStoreBtn.setBackgroundResource(R.drawable.round_select_color_little);
            bookmarkCollaboBtn.setEnabled(true);
            bookmarkCollaboBtn.setBackgroundResource(R.drawable.round_unselect_color_little);

            // 리사이클러뷰 토글
            bookmarkCollaboRv.setVisibility(View.GONE);

            // 값이 있을때만 보이기
            if(bookmarkStoreData.size() > 0){
                bookmarkStoreRv.setVisibility(View.VISIBLE);
            }else{
                bookmarkNoTitle.setVisibility(View.VISIBLE);
            }

        });

        // 찜한 협업 리스트 버튼 클릭 리스너
        bookmarkCollaboBtn.setOnClickListener(view -> {
            // 버튼 활성화 / 비활성화
            bookmarkStoreBtn.setEnabled(true);
            bookmarkStoreBtn.setBackgroundResource(R.drawable.round_unselect_color_little);
            bookmarkCollaboBtn.setEnabled(false);
            bookmarkCollaboBtn.setBackgroundResource(R.drawable.round_select_color_little);

            // 리사이클러뷰 토글
            bookmarkStoreRv.setVisibility(View.GONE);

            // 값이 있을때만 보이기
            if(bookmarkCollaboData.size() > 0){
                bookmarkCollaboRv.setVisibility(View.VISIBLE);
            }else{
                bookmarkNoTitle.setVisibility(View.VISIBLE);
            }
        });

        // ************* 데이터 가져오기 *******************
        getBookmarkStoreData(); // 찜한 가게 목록 가져오기
        getBookmarkCollaboData(); // 찜한 협업 목록 가져오기
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_bookmark_back_ic); // 상단 뒤로가기 버튼
        bookmarkNoTitle = findViewById(R.id.mypage_bookmark_no_title); // 리사이클러뷰 값 없음 표시 텍스트
        bookmarkStoreBtn = findViewById(R.id.mypage_bookmark_store_btn);  // 찜한 가게 버튼
        bookmarkCollaboBtn = findViewById(R.id.mypage_bookmark_collabo_btn);  // 찜한 협업 리스트 버튼
        bookmarkStoreRv = findViewById(R.id.mypage_bookmark_store_rv);  // 찜한 가게 리사이클러뷰
        bookmarkCollaboRv = findViewById(R.id.mypage_bookmark_collabo_rv);  // 찜한 협업 리스트 리사이클러뷰

        // 프로필 변경 Dialog 팝업
        bookmarkDeleteDialog = new Dialog(MyPageBookmarkActivity.this);  // Dialog 초기화
        bookmarkDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        bookmarkDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        bookmarkDeleteDialog.setContentView(R.layout.dl_delete); // xml 레이아웃 파일과 연결
    }

    // 찜한 가게 목록 GET
    private void getBookmarkStoreData(){
        // LayoutManager 객체 생성
        LinearLayoutManager llm = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        bookmarkStoreRv.setLayoutManager(llm);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            checkPermissions();
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
                                , object.getInt("bookmarkStoreId"));                        // 찜한 가게 목록 고유 아이디

                        bookmarkStoreData.add(bookmarkStore);    // 가게 데이터 추가
                    }

                    bookmarkStoreAdapter = new MyPageBookmarkStoreRvAdapter(this, this, bookmarkStoreData);  // 리사이클러뷰 어뎁터 객체 생성
                    bookmarkStoreRv.setAdapter(bookmarkStoreAdapter); // 리사이클러뷰 어뎁터 객체 지정
                }else{
                    // 값 없음 표시 텍스트 보이기
                    bookmarkStoreRv.setVisibility(View.GONE);
                    bookmarkNoTitle.setVisibility(View.VISIBLE);

                }
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

    // 찜한 협업 목록 GET
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
                    }

                    bookmarkCollaboAdapter = new MyPageBookmarkCollaboRvAdapter(this, this, bookmarkCollaboData);  // 리사이클러뷰 어뎁터 객체 생성
                    bookmarkCollaboRv.setAdapter(bookmarkCollaboAdapter); // 리사이클러뷰 어뎁터 객체 지정
                }else{
                    // 검색 결과가 없을 시 Toast 메시지 출력
                    //Toast.makeText(this, "찜한 협업 목록이 없습니다.",Toast.LENGTH_SHORT).show();
                }
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

    // 가게 리사이클러뷰 클릭 리스너
    @Override
    public void onMyPageBookmarkStoreRvClick(View v, int position, String flag) {
        if(flag.equals("delete")){  // 찜 목록 삭제 버튼 클릭
            bookmarkDeleteDialog.show();

            // 뷰 정의
            TextView dlTitle = bookmarkDeleteDialog.findViewById(R.id.dl_title);  // 다이얼로그 타이틀
            Button dlConfirmBtn = bookmarkDeleteDialog.findViewById(R.id.dl_confirm_btn);  // 다이얼로그 확인 버튼
            Button dlCloseBtn = bookmarkDeleteDialog.findViewById(R.id.dl_close_btn);  // 다이얼로그 닫기 버튼

            // 데이터 SET
            dlTitle.setText("찜한 가게 목록을 삭제하시겠습니까?");
            dlConfirmBtn.setText("삭제");

            // 삭제 버튼 클릭 리스너
            dlConfirmBtn.setOnClickListener(view -> {
                Map<String, String> param = new HashMap<>();
                param.put("bmkStId", String.valueOf(bookmarkStoreData.get(position).getBookmarkStoreId()));   // 변경할 찜한 가게 목록 고유 아이디

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest deleteStoreRequest = new StringRequest(Request.Method.POST, HOST + BOOKMARK_STORE_DELETE_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "삭제 성공!", R.style.blueToast).show();

                            bookmarkDeleteDialog.dismiss();  // 다이얼로그 닫기

                            bookmarkStoreData.remove(position);   // 데이터 삭제
                            bookmarkStoreAdapter.notifyItemRemoved(position); // 리사이클러뷰 데이터 삭제

                            // 데이터가 없을 경우 없다는 텍스트 표시
                            if(bookmarkStoreData.size() == 0){
                                bookmarkStoreRv.setVisibility(View.GONE);
                                bookmarkNoTitle.setVisibility(View.VISIBLE);
                            }
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "삭제 실패...", R.style.redToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("deleteMyPageBookmarkStoreError", "onErrorResponse : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // php로 설정값을 보낼 수 있음 ( POST )
                        return param;
                    }
                };

                deleteStoreRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(deleteStoreRequest);      // RequestQueue에 요청 추가
            });

            // 닫기 버튼 클릭 리스너
            dlCloseBtn.setOnClickListener(view1 -> bookmarkDeleteDialog.dismiss());
        }else if(flag.equals("storeImage")) { // 앞 가게 이미지 클릭
            getStoreData(bookmarkStoreData.get(position).getStoreId());
        }
    }

    // 협업 리사이클러뷰 클릭 리스너
    @Override
    public void onMyPageBookmarkCollaboRvClick(View v, int position, String flag) {
        if(flag.equals("delete")){  // 찜 목록 삭제 버튼 클릭
            bookmarkDeleteDialog.show();

            // 뷰 정의
            TextView dlTitle = bookmarkDeleteDialog.findViewById(R.id.dl_title);  // 다이얼로그 타이틀
            Button dlConfirmBtn = bookmarkDeleteDialog.findViewById(R.id.dl_confirm_btn);  // 다이얼로그 확인 버튼
            Button dlCloseBtn = bookmarkDeleteDialog.findViewById(R.id.dl_close_btn);  // 다이얼로그 닫기 버튼

            // 데이터 SET
            dlTitle.setText("찜한 협업 목록을 삭제하시겠습니까?");
            dlConfirmBtn.setText("삭제");

            // 삭제 버튼 클릭 리스너
            dlConfirmBtn.setOnClickListener(view -> {
                Map<String, String> param = new HashMap<>();
                param.put("bmkCobId", String.valueOf(bookmarkCollaboData.get(position).getBookmarkCollaboId()));   // 변경할 찜한 협업 목록 고유 아이디

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest deleteCollaboRequest = new StringRequest(Request.Method.POST, HOST + BOOKMARK_COLLABO_DELETE_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "삭제 성공!", R.style.blueToast).show();

                            bookmarkDeleteDialog.dismiss();  // 다이얼로그 닫기

                            bookmarkCollaboData.remove(position);   // 데이터 삭제
                            bookmarkCollaboAdapter.notifyItemRemoved(position); // 리사이클러뷰 데이터 삭제

                            // 데이터가 없을 경우 없다는 텍스트 표시
                            if(bookmarkCollaboData.size() == 0){
                                bookmarkCollaboRv.setVisibility(View.GONE);
                                bookmarkNoTitle.setVisibility(View.VISIBLE);
                            }
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "삭제 실패...", R.style.redToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("deleteMyPageBookmarkCollaboError", "onErrorResponse : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // php로 설정값을 보낼 수 있음 ( POST )
                        return param;
                    }
                };

                deleteCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(deleteCollaboRequest);      // RequestQueue에 요청 추가
            });

            // 닫기 버튼 클릭 리스너
            dlCloseBtn.setOnClickListener(view1 -> bookmarkDeleteDialog.dismiss());
        }else if(flag.equals("prvImage")) { // 앞 가게 이미지 클릭
            getStoreData(bookmarkCollaboData.get(position).getPrvStoreId());
        }
        else if(flag.equals("postImage")) { // 뒷 가게 이미지 클릭
            getStoreData(bookmarkCollaboData.get(position).getPostStoreId());
        }
    }

    public void getStoreData(int storeId){
        // GET 방식 파라미터 설정
        String storePath = STORE_PATH;
        storePath += String.format("?storeId=%s", storeId);     // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest mainStoreRequest = new StringRequest(Request.Method.GET, HOST + storePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                JSONObject object = mainStoreArr.getJSONObject(0);  // JSONObject 생성

                float distance = 0;   // 현위치에서 가게까지의 거리

                //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions(); // Gps 권한 확인
                }else{
                    // 위치 정보 매니저
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

                    // 현재 위치 좌표
                    // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
                    // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
                    // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
                    Location locCurrent = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                    Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                    point.setLatitude(object.getDouble("storeLatitude"));
                    point.setLongitude(object.getDouble("storeLongitude"));

                    distance = locCurrent.distanceTo(point);
                }

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
                        , HOST + object.getString("storeThumbnailPath")   // 가게 썸네일 이미지 경로
                        , object.getDouble("storeScore")                // 가게 별점
                        , object.getString("storeWorkingTime")          // 가게 운영 시간
                        , object.getString("storeHashTag")              // 가게 해시태그
                        , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                        , distance / 1000); // 현위치에서 가게까지의 거리

                infoBookmarkStoreData = new ArrayList<>();      // 유저 찜한 가게 간단 데이터 ( intent를 위한 데이터 )

                // intent를 위한 찜 목록 간단 데이터 SET
                if(bookmarkStoreData != null && bookmarkStoreData.size() > 0){
                    for(int i = 0; i < bookmarkStoreData.size(); i++){
                        infoBookmarkStoreData.add(new mainBookmarkStoreData(
                                bookmarkStoreData.get(i).getBookmarkStoreId()
                                , bookmarkStoreData.get(i).getStoreId()
                        ));
                    }
                }

                Intent intent = new Intent(MyPageBookmarkActivity.this, InfoActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

                // 데이터 송신을 위한 Parcelable interface 사용
                // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                intent.putExtra("Store", (Parcelable) mainStore);
                intent.putParcelableArrayListExtra("bookmarkStore", infoBookmarkStoreData);
                intent.putExtra("pageName", "MyPageBookmarkActivity");

                // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                activityResultLauncher.launch(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getMainStoreError", "onErrorResponse : " + error);
        })/* {  // Post 예제 ( 추후에 참고용 )
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                if(param.isEmpty()){
                    map.put("schText", "");
                }else{
                    map.put("schText", param.get("schText"));
                }

                System.out.println("############");
                System.out.println(map.get("schText"));
                //php로 설정값을 보낼 수 있음
                return map;
            }
        }*/;

        mainStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(mainStoreRequest);     // RequestQueue에 요청 추가
    }
}

