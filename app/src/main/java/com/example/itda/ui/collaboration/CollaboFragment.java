package com.example.itda.ui.collaboration;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.mainBookmarkCollaboData;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;
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

public class CollaboFragment extends Fragment {

    private View root;  // Fragment root view


    // Layout
    public RecyclerView collaboRv;  // 협업 리사이클러뷰


    // Adapter
    public CollaboRvAdapter CollaboAdapter; // 협업 리사이클러뷰 어뎁터


    // Volley Library RequestQueue
    public static RequestQueue requestQueue;    // Volley Library 사용을 위한 RequestQueue


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성


    // Rest API
    private String HOST;        // Host 정보
    private String COLLABO_URL; // 협업 가게 정보 데이터 조회 Rest API
    private String BOOKMARK_STORE_PATH; // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
    private String STORE_URL;   // 가게 정보 데이터 조회 Rest API
    private String BOOKMARK_COLLABO_PATH;   // 유저 찜한 협업 목록 ( 간단 정보 ) 조회 Rest API
    private String DELETE_BOOKMARK_COLLABO_PATH;    // 유저 찜한 협업 목록 삭제 Rest API
    private String INSERT_BOOKMARK_COLLABO_PATH;    // 유저 찜한 협업 목록 추가 Rest API


    // Data
    public ArrayList<collaboData> Collabos = new ArrayList<>(); // 협업 정보 저장
    private ArrayList<mainBookmarkStoreData> BookmarkStore = new ArrayList<>(); // 유저 찜한 가게 목록
    private final ArrayList<mainBookmarkCollaboData> BookmarkCollabo = new ArrayList<>();   // 유저 찜한 협업 목록

    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )

    // Global Data
    private Location locCurrent;    // 현재 위치 객체
    private boolean gpsPossible = false;    // Gps 사용 가능 여부


    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            gpsPossible = true; // Gps 사용 가능 여부
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
                )
                .check();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_collabo, container, false);

        COLLABO_URL = ((globalMethod) requireActivity().getApplication()).getCollaboPath(); // 협업 정보 데이터 조회 Rest API
        BOOKMARK_STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainBookmarkStorePath();   // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
        STORE_URL = ((globalMethod) requireActivity().getApplicationContext()).getMainStorePath();  // 가게 상세 데이터 조회 Rest API
        BOOKMARK_COLLABO_PATH = ((globalMethod) requireActivity().getApplication()).getMainBookmarkCollaboPath();   // 유저 찜한 협업 목록 ( 간단 정보 ) 조회 Rest API
        DELETE_BOOKMARK_COLLABO_PATH = ((globalMethod) requireActivity().getApplication()).deleteBookmarkCollaboPath(); // 유저 찜한 협업 목록 삭제 Rest API
        INSERT_BOOKMARK_COLLABO_PATH = ((globalMethod) requireActivity().getApplication()).insertBookmarkCollaboPath(); // 유저 찜한 협업 목록 추가 Rest API
        HOST = ((globalMethod) requireActivity().getApplication()).getHost();   // Host 정보

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        // 유저 전역 변수 GET
        User = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 2000){   // resultCode가 2000로 넘어왔다면 InfoActivity에서 넘어온것
                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");    // 찜한 목록 데이터 GET
            }
        });

        // 위치 관리자 객체
        LocationManager lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions(); // Gps 권한 확인
        }else{
            // 현재 위치 좌표
            // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
            // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
            // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정 -> 추후 서비스시 GPS_PROVIDER로 변경
            //locCurrent = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locCurrent = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            gpsPossible = true; // Gps 활성화 체크
        }

        collaboRv = root.findViewById(R.id.collabo_rv); // 협업 리사이클러뷰

        getBookmarkStore();     // 찜한 가게 목록 GET
        getBookmarkCollabo();   // 찜한 협업 목록 GET

        getCollabo();   // 협업 데이터 GET

        return root;
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
                                object.getInt("bookmarkStoreId")    // 찜한 가게 테이블 고유 아이디
                                , object.getInt("storeId"));        // 가게 고유 아이디

                        BookmarkStore.add(bookmarkStore);   // 찜한 가게 데이터 저장
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

    // 협업 데이터 GET
    public void getCollabo(){
        StringRequest collaboRequest = new StringRequest(Request.Method.GET, HOST + COLLABO_URL, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");  // 객체에 collabo라는 Key를 가진 JSONArray 생성

                for(int i = 0; i < collaboArr.length(); i++){
                    JSONObject object = collaboArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    collaboData collaboData = new collaboData(
                            object.getInt("collaboId")      // 협업 고유 아이디
                            , object.getInt("prvStoreId")   // 앞 가게 고유 아이디
                            , object.getInt("postStoreId")  // 뒷 가게 고유 아이디
                            , object.getInt("prvDiscountCondition") // 앞 가게 할인 조건 금액
                            , object.getInt("postDiscountRate")     // 뒷 가게 할인 율
                            , object.getString("prvStoreName")      // 앞 가게 명
                            , object.getString("postStoreName")     // 뒷 가게 명
                            , HOST + object.getString("prvStoreImagePath")  // 앞 가게 썸네일 이미지
                            , HOST + object.getString("postStoreImagePath") // 뒷 가게 썸네일 이미지
                            , object.getString("collaboDistance")); // 가게 간 거리

                    Collabos.add(collaboData);
                }
                // LayoutManager 객체 생성
                collaboRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

                CollaboAdapter = new CollaboRvAdapter(getActivity(), Collabos, BookmarkCollabo);    // 리사이클러뷰 어뎁터 객체 생성
                collaboRv.setAdapter(CollaboAdapter);   // 리사이클러뷰 어뎁터 객체 지정

                setClickListener(); // Click Listener SET

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("getCollaboError", "onErrorResponse : " + error));

        collaboRequest.setShouldCache(false);   // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(collaboRequest);       // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 정보 Return
    private void getBookmarkCollabo(){
        // GET 방식 파라미터 설정
        // Param => userId : 로그인 유저 고유 아이디
        String bookmarkCollaboPath = BOOKMARK_COLLABO_PATH;
        bookmarkCollaboPath += String.format("?userId=%s", User.getInt("userId", 0));   // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkCollaboRequest = new StringRequest(Request.Method.GET, HOST + bookmarkCollaboPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray bookmarkCollaboArr = jsonObject.getJSONArray("bookmarkCollabo");  // 객체에 bookmarkCollabo라는 Key를 가진 JSONArray 생성

                if(bookmarkCollaboArr.length() > 0) {
                    for (int i = 0; i < bookmarkCollaboArr.length(); i++) {
                        JSONObject object = bookmarkCollaboArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        mainBookmarkCollaboData bookmarkCollabo = new mainBookmarkCollaboData(
                                object.getInt("bookmarkCollaboId")  // 찜한 협업 목록 고유 아이디
                                , object.getInt("collaboId"));      // 협업 고유 아이디

                        BookmarkCollabo.add(bookmarkCollabo);   // 협업 정보 저장
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getBookmarkCollaboError", "onErrorResponse : " + error);
        });

        bookmarkCollaboRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(bookmarkCollaboRequest);     // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 목록 삭제
    private void deleteBookmarkCollabo(int index){
        // POST 방식 파라미터 설정
        // Param => bmkCobId : 삭제할 찜한 협업 목록 고유 아이디
        Map<String, String> param = new HashMap<>();
        param.put("bmkCobId", String.valueOf(BookmarkCollabo.get(index).getBmkCollaboId()));   // 삭제할 찜한 협업 목록 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteBookmarkCollaboRequest = new StringRequest(Request.Method.POST, HOST + DELETE_BOOKMARK_COLLABO_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");   // success flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(root.getContext(), "삭제 성공!", R.style.blueToast).show();

                    BookmarkCollabo.remove(index);   // 데이터 삭제
                }else{
                    StyleableToast.makeText(root.getContext(), "삭제 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteBookmarkCollaboError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteBookmarkCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteBookmarkCollaboRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 목록 추가
    private void insertBookmarkCollabo(int userId, int collaboId){
        // POST 방식 파라미터 설정
        // Param => userId : 추가할 유저 고유 아이디
        //          collaboId : 추가할 찜한 협업 목록 고유 아이디
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("collaboId", String.valueOf(collaboId));   // 찜할 협업 목록 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertBookmarkCollaboRequest = new StringRequest(Request.Method.POST, HOST + INSERT_BOOKMARK_COLLABO_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");   // Success Flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(root.getContext(), "추가 성공!", R.style.blueToast).show();

                    // 데이터 추가
                    BookmarkCollabo.add(new mainBookmarkCollaboData(
                            Integer.parseInt(jsonObject.getString("bmkCobId"))  // 찜한 협업 목록 고유 아이디
                            , collaboId // 협업 고유 아이디
                    ));
                }else{
                    StyleableToast.makeText(root.getContext(), "추가 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertBookmarkCollaboError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertBookmarkCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertBookmarkCollaboRequest);      // RequestQueue에 요청 추가
    }

    // 리사이클러뷰 클릭 리스너 SET
    private void setClickListener(){
        // 협업 목록 리사이클러뷰 클릭 리스너
        CollaboAdapter.setonCollaboRvClickListener((v, position, flag) -> {
            if(flag.contains("Image")){ // 가게 이미지 클릭 시
                // 클릭한 가게 고유 아이디
                // GET 방식 파라미터 설정
                // Param => storeId : 가게 고유 아이디
                String storePath = STORE_URL + "?storeId=" + ( flag.equals("prvImage") ? Collabos.get(position).getPrvStoreId() : Collabos.get(position).getPostStoreId() );

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
                StringRequest storeRequest = new StringRequest(Request.Method.GET, HOST + storePath, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                        JSONObject object = mainStoreArr.getJSONObject(0);  // JSONObject 생성

                        float distance = 0; // 현위치에서 가게까지의 거리

                        // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                        if (gpsPossible) {
                            // 가게 위치 좌표
                            Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                            point.setLatitude(object.getDouble("storeLatitude"));    // 위도
                            point.setLongitude(object.getDouble("storeLongitude"));  // 경도

                            distance = locCurrent.distanceTo(point);    // 거리
                        }

                        mainStoreData storeData = new mainStoreData(
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
                                , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                                , object.getDouble("storeScore")        // 가게 별점
                                , object.getString("storeWorkingTime")  // 가게 운영 시간
                                , object.getString("storeHashTag")      // 가게 해시태그
                                , object.getInt("storeReviewCount")     // 가게 리뷰 개수
                                , distance / 1000); // 현위치에서 가게까지의 거리

                        Intent intent = new Intent(getActivity(), InfoActivity.class);  // 가게 상세 화면으로 이동하기 위한 Intent 객체 선언

                        // 데이터 송신을 위한 Parcelable interface 사용
                        // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                        intent.putExtra("Store", storeData);    // 가게 데이터
                        intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 유저 찜하 가게 목록
                        intent.putExtra("pageName", "CollaboFragment"); // 화면전환 페이지 명

                        activityResultLauncher.launch(intent);  // 새 Activity 인스턴스 시작

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("getStoreError", "onErrorResponse : " + error);
                });

                storeRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(storeRequest);     // RequestQueue에 요청 추가
            }else if(flag.equals("bookmarkDelete")){    // 찜 삭제 버튼 클릭 시

                // 찜한 가게 고유 아이디 찾기
                for(int i = 0; i < BookmarkCollabo.size(); i++){
                    if(BookmarkCollabo.get(i).getCollaboId() == Collabos.get(position).getCollaboId()){
                        deleteBookmarkCollabo(i);    // 찜 목록에서 제거
                        break;
                    }
                }

            }else if(flag.equals("bookmarkInsert")){    // 찜 추가 버튼 클릭 시
                insertBookmarkCollabo(User.getInt("userId", 0), Collabos.get(position).getCollaboId());  // 찜 목록에 추가
            }
        });
    }
}