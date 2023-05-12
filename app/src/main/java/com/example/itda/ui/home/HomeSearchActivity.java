package com.example.itda.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.mypage.MyPageEditActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeSearchActivity extends Activity implements onMainStoreRvClickListener{
    private ImageButton homeSearchBackIc;   // 상단 뒤로가기 버튼
    private RecyclerView mainStoreRv;       // 가게 데이터 리사이클러뷰
    private MainStoreRvAdapter MainStoreAdapter;    // 리사이클러뷰 어뎁터 객체

    private ArrayList<mainStoreData> Store; // 가게 정보 저장
    private ArrayList<mainBookmarkStoreData> BookmarkStore; // 유저 찜한 가게 목록

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue

    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private String DELETE_BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 삭제 Rest API
    private String INSERT_BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 추가 Rest API
    private String HOST;            // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        DELETE_BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).deleteBookmarkStorePath();      // 유저 찜한 가게 목록 삭제 Rest API
        INSERT_BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).insertBookmarkStorePath();      // 유저 찜한 가게 목록 추가 Rest API
        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        initView(); // 뷰 생성

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        homeSearchBackIc.setOnClickListener(view -> {
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(HomeSearchActivity.this, MainActivity.class);

            intent.putParcelableArrayListExtra("BookmarkStore", BookmarkStore);
            setResult(1000, intent);    // 결과 코드와 intent 값 전달
            finish();
        });

        // GridLayoutManager 객체를 2개의 ViewHolder로 생성
        GridLayoutManager glm = new GridLayoutManager(this, 2);

        mainStoreRv.setHasFixedSize(true);  // 리사이클러뷰 높이, 너비 변경 제한
        mainStoreRv.setLayoutManager(glm);  //리사이클러뷰 Layout 설정

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        Store = getIntent().getParcelableArrayListExtra("Store");
        BookmarkStore = getIntent().getParcelableArrayListExtra("BookmarkStore");

        MainStoreAdapter = new MainStoreRvAdapter(this, this, Store, BookmarkStore);  // 리사이클러뷰 어뎁터 객체 생성
        mainStoreRv.setAdapter(MainStoreAdapter);   // 리사이클러뷰 어뎁터 객체 지정
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(HomeSearchActivity.this, MainActivity.class);

            intent.putParcelableArrayListExtra("BookmarkStore", BookmarkStore);
            setResult(1000, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        homeSearchBackIc = findViewById(R.id.home_search_back); // 상단 뒤로가기 버튼
        mainStoreRv = findViewById(R.id.search_main_store_rv);  // 가게 데이터 리사이클러뷰
    }

    // 리사이클러뷰 클릭 리스너
    @Override
    public void onMainStoreRvClick(View v, int position, String flag) {
        if(flag.equals("image")){
            Intent intent = new Intent(HomeSearchActivity.this, InfoActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

            // 데이터 송신을 위한 Parcelable interface 사용
            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
            intent.putExtra("Store", (Parcelable) Store.get(position));

            startActivity(intent); // 새 Activity 인스턴스 시작
        }else if(flag.equals("bookmarkDelete")){
            int index = 0;  // 찜한 가게 목록 데이터의 index

            // 찜한 목록중 선택한 데이터의 가게 고유 아이디 구하기
            for(int i = 0; i < BookmarkStore.size(); i++) {
                if(BookmarkStore.get(i).getStoreId() == Store.get(position).getStoreId()) {
                    index = i;
                    break;
                }
            }

            deleteBookmarkStore(index);
        }else if(flag.equals("bookmarkInsert")){
            insertBookmarkStore(User.getInt("userId", 0), Store.get(position).getStoreId());
        }
    }

    // 유저 찜한 가게 삭제
    private void deleteBookmarkStore(int index){
        Map<String, String> param = new HashMap<>();

        param.put("bmkStId", String.valueOf(BookmarkStore.get(index).getBmkStoreId()));   // 삭제할 찜한 가게 목록 고유 아이디
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteBookmarkRequest = new StringRequest(Request.Method.POST, HOST + DELETE_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "삭제 성공!", R.style.blueToast).show();

                    BookmarkStore.remove(index);   // 데이터 삭제

                    MainStoreAdapter.setbookmarkStores(BookmarkStore);

                }else{
                    StyleableToast.makeText(this, "삭제 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteBookmarkRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 가게 삭제
    private void insertBookmarkStore(int userId, int storeId){
        Map<String, String> param = new HashMap<>();

        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("storeId", String.valueOf(storeId));   // 찜할 가게 고유 아이디
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertBookmarkRequest = new StringRequest(Request.Method.POST, HOST + INSERT_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "추가 성공!", R.style.blueToast).show();

                    // 데이터 추가
                    BookmarkStore.add(new mainBookmarkStoreData(
                            Integer.parseInt(jsonObject.getString("bmkStId"))
                            , storeId
                    ));

                    MainStoreAdapter.setbookmarkStores(BookmarkStore);
                }else{
                    StyleableToast.makeText(this, "추가 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertBookmarkRequest);      // RequestQueue에 요청 추가
    }
}

