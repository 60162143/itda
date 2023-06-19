package com.example.itda.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.itda.ui.info.InfoActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomeFragment extends Fragment{
    private View root;  // Fragment root view

    // Layout
    private EditText mainSchText;   // 검색어 입력
    private RecyclerView CategoryRv;    // 카테고리 리사이클러뷰
    private RecyclerView MainStoreRv;   // 가게 정보 리사이클러뷰


    // Adapter
    private CategoryRvAdapter CategoryAdapter;      // 카테고리 리사이클러뷰 어뎁터
    private MainStoreRvAdapter MainStoreAdapter;    // 가게 정보 리사이클러뷰 어뎁터


    // Data
    private final ArrayList<mainCategoryData> Category = new ArrayList<>(); // 카테고리 데이터
    private ArrayList<mainStoreData> MainStore;  // 가게 데이터
    private ArrayList<mainBookmarkStoreData> BookmarkStore = new ArrayList<>();  // 유저 찜한 가게 목록 데이터

    // Global Data
    private Location locCurrent;    // 현재 위치 객체
    private boolean gpsPossible = false;    // Gps 사용 가능 여부

    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private Intent intent;  // 상세 페이지로 전환을 위한 객체


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String CATEGORY_PATH;   // 카테고리 데이터 조회 Rest API
    private String STORE_PATH;      // 가게 정보 데이터 조회 Rest API
    private String BOOKMARK_STORE_PATH;  // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
    private String DELETE_BOOKMARK_STORE_PATH;  // 유저 찜한 가게 목록 삭제 Rest API
    private String INSERT_BOOKMARK_STORE_PATH;  // 유저 찜한 가게 목록 추가 Rest API
    private String HOST;    // Host 정보



    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            gpsPossible = true;
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            StyleableToast.makeText(root.getContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", R.style.redCenterToast).show();
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
                )
                .check();
    }

    // View 생성
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // View의 정의를 실제 View 객체로 만드는 역할
        root = inflater.inflate(R.layout.fragment_home, container, false);

        // 유저 전역 변수 GET
        User = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);

        // ---------------- Rest API 전역변수 SET---------------------------
        CATEGORY_PATH = ((globalMethod) requireActivity().getApplication()).getMainCategoryPath();  // 카테고리 데이터 조회 Rest API
        STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainStorePath();    // 가게 정보 데이터 조회 Rest API
        BOOKMARK_STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainBookmarkStorePath();   // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
        DELETE_BOOKMARK_STORE_PATH = ((globalMethod) requireActivity().getApplication()).deleteBookmarkStorePath(); // 유저 찜한 가게 목록 삭제 Rest API
        INSERT_BOOKMARK_STORE_PATH = ((globalMethod) requireActivity().getApplication()).insertBookmarkStorePath(); // 유저 찜한 가게 목록 추가 Rest API
        HOST = ((globalMethod) requireActivity().getApplication()).getHost();   // Host 정보


        // Init View
        CategoryRv = root.findViewById(R.id.main_category_rv);  // 카테고리 리사이클러뷰
        MainStoreRv = root.findViewById(R.id.main_store_rv);    // 상점 정보 리사이클러뷰
        mainSchText = root.findViewById((R.id.search_main_store));   // 검색어 입력


        // 위치 정보 매니저
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions(); // Gps 권한 확인
        }else{
            // 현재 위치 좌표
            // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
            // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
            // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
            //locCurrent = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locCurrent = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            gpsPossible = true; // Gps 활성화 체크
        }

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000로 넘어왔다면 HomeSearchActivity에서 넘어온것

                mainSchText.setText("");    // 검색어 초기화

                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");  // 찜한 목록 데이터 GET

                // 찜 목록 데이터 SET
                MainStoreAdapter.setBookmarkStores(BookmarkStore);

                // 가게 리사이클러뷰 클릭 리스너
                MainStoreAdapter.setonMainStoreRvClickListener((v, position, flag) -> {
                    switch (flag) {
                        case "image":   // 가게 이미지 클릭 시
                            intent = new Intent(getActivity(), InfoActivity.class); // 상세화면으로 이동하기 위한 Intent 객체 선언

                            // 데이터 송신을 위한 Parcelable interface 사용
                            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                            intent.putExtra("Store", MainStore.get(position)); // 가게 데이터
                            intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 유저 찜한 가게 목록
                            intent.putExtra("pageName", "HomeFragment");    // 화면전환 페이지 명

                            // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                            activityResultLauncher.launch(intent);
                            break;
                        case "bookmarkDelete":     // 찜 버튼 클릭 시
                            int index = 0;  // 찜한 가게 목록 데이터의 index

                            // 찜한 목록중 선택한 데이터의 가게 고유 아이디 구하기
                            for (int i = 0; i < BookmarkStore.size(); i++) {
                                if (BookmarkStore.get(i).getStoreId() == MainStore.get(position).getStoreId()) {
                                    index = i;
                                    break;
                                }
                            }

                            deleteBookmarkStore(index); // 찜한 가게 제거
                            break;
                        case "bookmarkInsert":
                            insertBookmarkStore(User.getInt("userId", 0), MainStore.get(position).getStoreId());    // 찜한 가게 추가

                            break;
                    }
                });

                MainStoreAdapter.notifyDataSetChanged();    // 리사이클러뷰 데이터 변경
            }else if(result.getResultCode() == 2000){   // resultCode가 2000로 넘어왔다면 InfoActivity에서 넘어온것
                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");    // 찜한 목록 데이터 GET

                // 찜 목록 데이터 SET
                MainStoreAdapter.setBookmarkStores(BookmarkStore);

                int storeId = result.getData().getIntExtra("storeId", 0);   // 가게 고유 아이디

                // 변경 데이터 SET
                for(int i = 0; i < MainStore.size(); i++){
                    if(MainStore.get(i).getStoreId() == storeId){
                        MainStoreAdapter.setReviewCount(i, result.getData().getIntExtra("reviewCount", 0)); // 리뷰 갯수 SET
                        MainStoreAdapter.setStoreScore(i, result.getData().getDoubleExtra("score", 0)); // 별점 SET
                        MainStoreAdapter.notifyItemChanged(i);  // 리사이클러뷰 데이터 변경

                        break;
                    }
                }
            }
        });

        // 조회 EditText Key Press Listener
        mainSchText.setOnKeyListener((view, keyCode, keyEvent) -> {
            // 터치 이벤트가 Down, Up 2번 인식 되므로 Down 조건 추가
            // 엔터키 입력 Event
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if(!TextUtils.isEmpty(mainSchText.getText().toString())){
                    Map<String, String> param = new HashMap<>();
                    param.put("schText", mainSchText.getText().toString());  // 파라미터 설정
                    getSearchMainStore(param);
                }else{
                    StyleableToast.makeText(requireActivity(), "검색어를 입력해주세요.", R.style.redCenterToast).show();
                }

                return true;    // 받은 터치를 없어겠다는 의미
            }
            return false;       // 받은 터치를 다른곳에서도 사용할 수 있게 남겨두겠다는 의미
        });

        // 로그인이 되어 있으면 찜한 가게 목록 GET
        if(((globalMethod) requireActivity().getApplication()).loginChecked()){
            getBookmarkStore();
        }

        getCategory();  // 카테고리 데이터 GET

        getMainStore(); // 가게 데이터 GET

        return root;
    }

    // 카테고리 데이터 GET
    private void getCategory() {
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest categoryRequest = new StringRequest(Request.Method.GET, HOST + CATEGORY_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray categoryArr = jsonObject.getJSONArray("category");    // 객체에 category라는 Key를 가진 JSONArray 생성

                for (int i = 0; i < categoryArr.length(); i++) {
                    JSONObject objectInArray = categoryArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성
                    // 카테고리 데이터 생성 및 저장
                    mainCategoryData mainCategory = new mainCategoryData(
                            objectInArray.getInt("categoryId")      // 카테고리 고유 아이디
                            , objectInArray.getString("categoryNm") // 카테고리 이름
                            , HOST + objectInArray.getString("imagePath")); // 카테고리 이미지 경로

                    Category.add(mainCategory); // 카테고리 데이터 저장
                }
                // LayoutManager 객체 생성
                CategoryRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

                CategoryAdapter = new CategoryRvAdapter(getActivity(), Category);   // 리사이클러뷰 어뎁터 객체 생성
                CategoryRv.setAdapter(CategoryAdapter); // 리사이클러뷰 어뎁터 객체 지정

                // 카테고리 리사이클러뷰 클릭 리스너
                CategoryAdapter.setonMainStoreRvClickListener((v, position, flag) -> {
                    if(flag.equals("image")){   // 카테고리 이미지 클릭 시
                        Map<String, String> param = new HashMap<>();
                        param.put("categoryId", String.valueOf(Category.get(position).getCategoryId()));    // 파라미터 설정

                        getSearchMainStore(param);  // 카테고리 고유 아이디로 조회
                    }
                });
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
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                MainStore = new ArrayList<>();  // 가게 데이터 초기화

                for (int i = 0; i < mainStoreArr.length(); i++) {
                    JSONObject object = mainStoreArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    float distance = 0; // 현위치에서 가게까지의 거리

                    // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                    if (gpsPossible) {
                        // 가게 위치 좌표
                        Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                        point.setLatitude(object.getDouble("storeLatitude"));
                        point.setLongitude(object.getDouble("storeLongitude"));

                        distance = locCurrent.distanceTo(point);
                    }

                    mainStoreData mainStore = new mainStoreData(
                              object.getInt("storeId")      // 가게 고유 아이디
                            , object.getString("storeName") // 가게 이름
                            , object.getString("storeAddress")      // 가게 주소
                            , object.getString("storeDetail")       // 가게 간단 제공 서비스
                            , object.getString("storeFacility")     // 가게 제공 시설 여부
                            , object.getDouble("storeLatitude")     // 가게 위도
                            , object.getDouble("storeLongitude")    // 가게 경도
                            , object.getString("storeNumber")   // 가게 번호
                            , object.getString("storeInfo")     // 가게 간단 정보
                            , object.getInt("storeCategoryId")  // 가게가 속한 카테고리 고유 아이디
                            , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")        // 가게 별점
                            , object.getString("storeWorkingTime")  // 가게 운영 시간
                            , object.getString("storeHashTag")      // 가게 해시태그
                            , object.getInt("storeReviewCount")     // 가게 리뷰 개수
                            , distance / 1000); // 현위치에서 가게까지의 거리

                    MainStore.add(mainStore);   // 가게 데이터 저장
                }

                // GridLayoutManager 객체 생성
                MainStoreRv.setLayoutManager(new GridLayoutManager(requireContext(), 2, RecyclerView.VERTICAL, false));

                MainStoreAdapter = new MainStoreRvAdapter(getActivity(), MainStore, BookmarkStore);    // 리사이클러뷰 어뎁터 객체 생성
                MainStoreRv.setAdapter(MainStoreAdapter);   // 리사이클러뷰 어뎁터 객체 지정

                // 가게 리사이클러뷰 클릭 리스너
                MainStoreAdapter.setonMainStoreRvClickListener((v, position, flag) -> {
                    switch (flag) {
                        case "image":    // 가게 이미지 클릭 시
                            intent = new Intent(getActivity(), InfoActivity.class); // 상세화면으로 이동하기 위한 Intent 객체 선언

                            // 데이터 송신을 위한 Parcelable interface 사용
                            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                            intent.putExtra("Store", MainStore.get(position));  // 가게 데이터
                            intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 유저 찜한 가게 목록
                            intent.putExtra("storeId", MainStore.get(position).getStoreId());   // 가게 고유 아이디
                            intent.putExtra("pageName", "HomeFragment");    // 화면전환 페이지 명

                            // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                            activityResultLauncher.launch(intent);
                            break;
                        case "bookmarkDelete":     // 찜 버튼 클릭 시
                            int index = 0;  // 찜한 가게 목록 데이터의 index

                            // 찜한 목록중 선택한 데이터의 가게 고유 아이디 구하기
                            for (int i = 0; i < BookmarkStore.size(); i++) {
                                if (BookmarkStore.get(i).getStoreId() == MainStore.get(position).getStoreId()) {
                                    index = i;
                                    break;
                                }
                            }

                            deleteBookmarkStore(index); // 찜한 가게 제거
                            break;
                        case "bookmarkInsert":
                            insertBookmarkStore(User.getInt("userId", 0), MainStore.get(position).getStoreId());    // 찜한 가게 추가

                            break;
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getMainStoreError", "onErrorResponse : " + error);
        });

        mainStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(mainStoreRequest);     // RequestQueue에 요청 추가
    }

    // 검색한 가게 정보 Return
    private void getSearchMainStore(Map<String, String> param){
        ArrayList<mainStoreData> searchStore = new ArrayList<>();   // 검색된 가게 데이터

        // GET 방식 파라미터 설정
        // Param => schText : 입력된 검색어
        //          categoryId : 카테고리 고유 아이디
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
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                for(int i = 0; i < mainStoreArr.length(); i++){
                    JSONObject object = mainStoreArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    mainStoreData mainStore = new mainStoreData(
                            object.getInt("storeId")        // 가게 고유 아이디
                            , object.getString("storeName") // 가게 이름
                            , object.getString("storeAddress")      // 가게 주소
                            , object.getString("storeDetail")       // 가게 간단 제공 서비스
                            , object.getString("storeFacility")     // 가게 제공 시설 여부
                            , object.getDouble("storeLatitude")     // 가게 위도
                            , object.getDouble("storeLongitude")    // 가게 경도
                            , object.getString("storeNumber")   // 가게 번호
                            , object.getString("storeInfo")     // 가게 간단 정보
                            , object.getInt("storeCategoryId")  // 가게가 속한 카테고리 고유 아이디
                            , HOST + object.getString("storeThumbnailPath") // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")        // 가게 별점
                            , object.getString("storeWorkingTime")  // 가게 운영 시간
                            , object.getString("storeHashTag")      // 가게 해시태그
                            , object.getInt("storeReviewCount")     // 가게 리뷰 개수
                            , 0); // 현위치에서 가게까지의 거리

                    searchStore.add(mainStore); // 가게 정보 저장
                }

                if(!searchStore.isEmpty()){
                    intent = new Intent(getContext(), HomeSearchActivity.class);  // 가게 검색 Activity 화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putParcelableArrayListExtra("Store", searchStore);   // 가게 데이터
                    intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 유저 찜한 가게 목록

                    // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                    activityResultLauncher.launch(intent);
                }else{
                    StyleableToast.makeText(requireActivity(), "검색 내용이 없습니다.", R.style.redCenterToast).show();

                    mainSchText.requestFocus();
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

    // 유저 찜한 가게 정보 Return
    private void getBookmarkStore(){
        // GET 방식 파라미터 설정
        // Param => userId : 로그인 유저 고유 아이디
        String bookmarkStorePath = BOOKMARK_STORE_PATH;
        bookmarkStorePath += String.format("?userId=%s", User.getInt("userId", 0)); // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkStoreRequest = new StringRequest(Request.Method.GET, HOST + bookmarkStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray bookmarkStoreArr = jsonObject.getJSONArray("bookmarkStore");  // 객체에 bookmarkStore라는 Key를 가진 JSONArray 생성

                if(bookmarkStoreArr.length() > 0) {
                    for (int i = 0; i < bookmarkStoreArr.length(); i++) {
                        JSONObject object = bookmarkStoreArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        mainBookmarkStoreData bookmarkStore = new mainBookmarkStoreData(
                                  object.getInt("bookmarkStoreId")  // 찜한 가게 테이블 고유 아이디
                                , object.getInt("storeId"));        // 가게 고유 아이디

                        BookmarkStore.add(bookmarkStore);  // 유저 찜한 가게 목록 저장
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getBookmarkStoreError", "onErrorResponse : " + error);
        });

        bookmarkStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(bookmarkStoreRequest);     // RequestQueue에 요청 추가
    }

    // 유저 찜한 가게 삭제
    private void deleteBookmarkStore(int index){
        // POST 방식 파라미터 설정
        // Param => bmkStId : 삭제할 찜한 가게 목록 고유 아이디
        Map<String, String> param = new HashMap<>();
        param.put("bmkStId", String.valueOf(BookmarkStore.get(index).getBmkStoreId()));   // 삭제할 찜한 가게 목록 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteBookmarkRequest = new StringRequest(Request.Method.POST, HOST + DELETE_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");   // Success Flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(requireActivity(), "삭제 성공!", R.style.blueToast).show();

                    BookmarkStore.remove(index);    // 데이터 삭제
                    MainStoreAdapter.setBookmarkStores(BookmarkStore);  // 데이터 SET
                }else{
                    StyleableToast.makeText(requireActivity(), "삭제 실패...", R.style.redToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams(){
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteBookmarkRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 가게 추가
    private void insertBookmarkStore(int userId, int storeId){
        // POST 방식 파라미터 설정
        // Param => userId : 추가할 유저 고유 아이디
        //          storeId : 추가할 가게 고유 아이디
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("storeId", String.valueOf(storeId));   // 찜할 가게 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertBookmarkRequest = new StringRequest(Request.Method.POST, HOST + INSERT_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");   // Success Flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(requireActivity(), "추가 성공!", R.style.blueToast).show();

                    // 데이터 추가
                    BookmarkStore.add(new mainBookmarkStoreData(
                            Integer.parseInt(jsonObject.getString("bmkStId"))   // 유저 찜한 가게 테이블 고유 아이디
                            , storeId   // 찜한 가게 고유 아이디
                    ));

                    MainStoreAdapter.setBookmarkStores(BookmarkStore);
                }else{
                    StyleableToast.makeText(requireActivity(), "추가 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams(){
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertBookmarkRequest);      // RequestQueue에 요청 추가
    }
}