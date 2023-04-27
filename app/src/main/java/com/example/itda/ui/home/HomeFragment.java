package com.example.itda.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment{
    private View root;  // Fragment root view

    private EditText mainSchText;   // 검색어 입력

    private RecyclerView CategoryRv;    // 카테고리 리사이클러뷰
    private RecyclerView MainStoreRv;   // 가게 정보 리사이클러뷰

    private final ArrayList<mainCategoryData> Category = new ArrayList<>(); // 카테고리 정보 저장
    private final ArrayList<mainStoreData> MainStore = new ArrayList<>();  // 가게 정보 저장

    private CategoryRvAdapter CategoryAdapter;      // 카테고리 리사이클러뷰 어뎁터
    private MainStoreRvAdapter MainStoreAdapter;    // 가게 정보 리사이클러뷰 어뎁터

    private Location locCurrent;    // 현재 위치 객체

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue

    private Intent intent;  // 상세 페이지로 전환을 위한 객체

    private String CATEGORY_PATH;   // 카테고리 데이터 조회 Rest API
    private String STORE_PATH;      // 가게 정보 데이터 조회 Rest API
    private String HOST;            // Host 정보

    private int touchPosition = -1; // 현재 터치한 Position, 음수로 초기화
    private float xPosition = 0;    // 현재 터치한 x 좌표

    private boolean gpsPossible = false;    // Gps 사용 가능 여부

    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            gpsPossible = true;
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(root.getContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    // Gps 권한 허용 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(root.getContext())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                        //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    // View 생성
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // View의 정의를 실제 View 객체로 만드는 역할
        root = inflater.inflate(R.layout.fragment_home, container, false);

        // ---------------- Rest API 전역변수 SET---------------------------
        CATEGORY_PATH = ((globalMethod) requireActivity().getApplication()).getMainCategoryPath();    // 카테고리 데이터 조회 Rest API
        STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainStorePath();      // 가게 정보 데이터 조회 Rest API
        HOST = ((globalMethod) requireActivity().getApplication()).getHost(); // Host 정보

        CategoryRv = root.findViewById(R.id.main_category_rv);  // 카테고리 리사이클러뷰
        MainStoreRv = root.findViewById(R.id.main_store_rv);    // 상점 정보 리사이클러뷰

        // 위치 정보 매니저
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions(); // Gps 권한 확인
        }else{
            // 현재 위치 좌표
            // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
            // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
            // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
            locCurrent = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            gpsPossible = true; // Gps 활성화 체크
        }

        mainSchText = (EditText) root.findViewById((R.id.search_main_store));   // 검색어 입력

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        // 조회 EditText Key Press Listener
        mainSchText.setOnKeyListener((view, keyCode, keyEvent) -> {
            // 터치 이벤트가 Down, Up 2번 인식 되므로 Down 조건 추가
            // 엔터키 입력 Event
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                Map<String, String> param = new HashMap<>();
                param.put("schText", mainSchText.getText().toString());  // 파라미터 설정
                getSearchMainStore(param);

                return true;    // 받은 터치를 없어겠다는 의미
            }
            return false;       // 받은 터치를 다른곳에서도 사용할 수 있게 남겨두겠다는 의미
        });

        CategoryRv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    // 눌렀을 때
                    case MotionEvent.ACTION_DOWN: {
                        xPosition = e.getX();   // 터치한 x 좌표 저장
                        // findChildViewUnder : 지정된 점 위의 view를 찾아주는 메서드
                        View child = CategoryRv.findChildViewUnder(e.getX(), e.getY());
                        if (child != null) {  // view가 있으면 현재 누른 리사이클러뷰 Position GET
                            touchPosition = CategoryRv.getChildAdapterPosition(child);
                        }
                        break;

                    }
                    // 누른걸 땠을 때
                    case MotionEvent.ACTION_UP: {
                        // findChildViewUnder : 지정된 점 위의 view를 찾아주는 메서드
                        View child = CategoryRv.findChildViewUnder(e.getX(), e.getY());

                        int postPosition = -2;  // 땠을 때 위치, 음수로 초기화

                        // view가 있으면 현재 누른걸 땐 리사이클러뷰 Position GET
                        if (child != null) {
                            postPosition = CategoryRv.getChildAdapterPosition(child);
                        }

                        // 누른 Position과 땐 Position이 같고 터치한 좌표의 오차 범위가 +-1 이면 터치로 인식
                        if (postPosition == touchPosition && xPosition >= e.getX() - 1 && xPosition <= e.getX() + 1) {
                            Map<String, String> param = new HashMap<>();
                            param.put("categoryId", String.valueOf(Category.get(touchPosition).getCategoryId()));  // 파라미터 설정
                            getSearchMainStore(param);
                        }
                        break;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        getCategory();  // 카테고리 데이터 GET

        getMainStore(); // 가게 데이터 GET

        return root;
    }

    // 카테고리 데이터 GET
    private void getCategory() {
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest categoryRequest = new StringRequest(Request.Method.GET, HOST + CATEGORY_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                   // Response를 JsonObject 객체로 생성
                JSONArray categoryArr = jsonObject.getJSONArray("category");  // 객체에 category라는 Key를 가진 JSONArray 생성

                for (int i = 0; i < categoryArr.length(); i++) {
                    JSONObject objectInArray = categoryArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성
                    // 카테고리 데이터 생성 및 저장
                    mainCategoryData mainCategory = new mainCategoryData(
                            objectInArray.getInt("categoryId")                          // 카테고리 고유 아이디
                            , objectInArray.getString("categoryNm")                     // 카테고리 이름
                            , HOST + objectInArray.getString("imagePath"));    // 카테고리 이미지 경로

                    Category.add(mainCategory); // 카테고리 정보 저장
                }
                // LayoutManager 객체 생성
                CategoryRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

                CategoryAdapter = new CategoryRvAdapter(Category);  // 리사이클러뷰 어뎁터 객체 생성
                CategoryRv.setAdapter(CategoryAdapter);             // 리사이클러뷰 어뎁터 객체 지정
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getCategoryError", "onErrorResponse : " + error);
        });

        categoryRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(categoryRequest);      // RequestQueue에 요청 추가
    }

    // 가게 데이터 GET
    private void getMainStore() {
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest mainStoreRequest = new StringRequest(Request.Method.GET, HOST + STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                for (int i = 0; i < mainStoreArr.length(); i++) {
                    JSONObject object = mainStoreArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    float distance = 0;   // 현위치에서 가게까지의 거리

                    // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                    if (gpsPossible) {
                        // 가게 위치 좌표
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
                            , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")                // 가게 별점
                            , object.getString("storeWorkingTime")          // 가게 운영 시간
                            , object.getString("storeHashTag")              // 가게 해시태그
                            , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                            , distance / 1000); // 현위치에서 가게까지의 거리

                    MainStore.add(mainStore);  // 가게 정보 저장
                }
                // GridLayoutManager 객체 생성
                MainStoreRv.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));

                MainStoreAdapter = new MainStoreRvAdapter(getActivity(), MainStore);    // 리사이클러뷰 어뎁터 객체 생성
                MainStoreRv.setAdapter(MainStoreAdapter);   // 리사이클러뷰 어뎁터 객체 지정
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

    // 검색한 가게 정보 Return
    private void getSearchMainStore(Map<String, String> param){
        ArrayList<mainStoreData> searchStore = new ArrayList<>();   // 검색된 가게 데이터

        String mainStorePath = STORE_PATH;

        // GET 방식 파라미터 설정
        if(!param.isEmpty() && param.get("schText") != null){   // 검색어 입력되었을 경우
            mainStorePath += String.format("?schText=%s", param.get("schText"));
        }else if(!param.isEmpty() && param.get("categoryId") != null){  // 카테고리를 클릭한 경우
            mainStorePath += String.format("?categoryId=%s", param.get("categoryId"));
        }

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest searchStoreRequest = new StringRequest(Request.Method.GET, HOST + mainStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                for(int i = 0; i < mainStoreArr.length(); i++){
                    JSONObject object = mainStoreArr.getJSONObject(i);          // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    mainStoreData mainStore = new mainStoreData(
                            object.getInt("storeId")                        // 가게 고유 아이디
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
                            , object.getString("storeHashTag")              // 가게 해시태그
                            , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                            , 0); // 현위치에서 가게까지의 거리

                    searchStore.add(mainStore);  // 가게 정보 저장
                }

                if(!searchStore.isEmpty()){
                    intent = new Intent(getContext(), HomeSearchActivity.class);  // 가게 검색 Activity 화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putParcelableArrayListExtra("Store", searchStore);

                    requireContext().startActivity(intent); // 새 Activity 인스턴스 시작
                }else{
                    Toast.makeText(getContext(), "검색 내용이 없습니다.",Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getSearchMainStoreError", "onErrorResponse : " + error);
        });

        searchStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(searchStoreRequest);     // RequestQueue에 요청 추가
    }
}