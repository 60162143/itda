package com.example.itda.ui.bag;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.collaboration.CollaboRvAdapter;
import com.example.itda.ui.collaboration.collaboData;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.MainStoreRvAdapter;
import com.example.itda.ui.home.mainBookmarkCollaboData;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.info.InfoPaymentCouponSpinnerAdapter;
import com.example.itda.ui.info.infoPaymentCouponData;
import com.example.itda.ui.info.infoPaymentData;
import com.example.itda.ui.info.infoPaymentMenuData;
import com.example.itda.ui.mypage.MyPageBookmarkActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BagFragment extends Fragment {

    private View root;  // Fragment root view

    private ArrayList<mainStoreData> SelectStore = new ArrayList<>();  // 선택한 가게 정보 저장
    private ArrayList<mainBookmarkStoreData> BookmarkStore = new ArrayList<>();  // 유저 찜한 가게 목록
    public ArrayList<BagCouponData> Coupons = new ArrayList<>();  // 사용가능 쿠폰 데이터
    private ArrayList<BagPaymentData> Payments = new ArrayList<>();  // 결제 데이터
    private ArrayList<ArrayList<BagPaymentMenuData>> PaymentMenus = new ArrayList<>();  // 결제한 메뉴 데이터

    public Button couponBtn;    // 사용 가능 쿠폰 버튼
    public Button paymentBtn;    // 주문/결제 내역 버튼

    public TextView noDataTitle;    // 데이터 없을 경우 안내 텍스트

    public RecyclerView couponRv;  // 사용 가능 쿠폰 리사이클러뷰
    public RecyclerView paymentRv;  // 주문/결제 내역 리사이클러뷰

    public BagCouponRvAdapter couponAdapter; // 사용 가능 쿠폰 리사이클러뷰 어뎁터
    public BagPaymentRvAdapter paymentAdapter; // 주문/결제 내역 리사이클러뷰 어뎁터

    public static RequestQueue requestQueue;    // Volley Library 사용을 위한 RequestQueue

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성

    private String HOST;        // Host 정보
    private String GET_SELECT_STORE_PATH;      // 선택한 가게 정보 데이터 조회 Rest API
    private String GET_BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
    private String GET_COUPON_PATH; // 유저 사용 가능 쿠폰 목록 조회 Rest API
    private String GET_PAYMENT_PATH; // 유저 결제 목록 조회 Rest API

    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private int loginUserId = 0; // 로그인 유저 고유 아이디
    private int clickStoreId = 0; // 클릭한 가게 고유 아이디
    private int clickRvPosition = 0; // 클릭한 리사이클러뷰 Position
    private Location locCurrent;    // 현재 위치 객체
    private boolean gpsPossible = false;    // Gps 사용 가능 여부

    private String intentFlag; // 화면전환이 쿠폰에서인지 주문/결제에서 인지 확인 Flag

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_bag, container, false);

        GET_COUPON_PATH = ((globalMethod) requireActivity().getApplication()).getBagCouponPath();      // 유저 사용 가능 쿠폰 목록 조회 Rest API
        GET_PAYMENT_PATH = ((globalMethod) requireActivity().getApplication()).getBagPaymentPath();      // 유저 결제 목록 조회 Rest API
        GET_SELECT_STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainStorePath();      // 선택한 가게 정보 데이터 조회 Rest API
        GET_BOOKMARK_STORE_PATH = ((globalMethod) requireActivity().getApplication()).getMainBookmarkStorePath();      // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
        HOST = ((globalMethod) requireActivity().getApplication()).getHost();                 // Host 정보

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

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

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 2000) {   // resultCode가 2000로 넘어왔다면 infoActivity에서 넘어온것
                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");    // 찜한 목록 데이터 GET
            }else if (result.getResultCode() == 3000) {   // resultCode가 3000로 넘어왔다면 BagPaymentDetailActivity 넘어온것
                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");    // 찜한 목록 데이터 GET
            }
        });

        // 유저 전역 변수 GET
        User = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);

        loginUserId = User.getInt("userId", 0);  // 로그인 유저 고유 아이디

        initView(); // 뷰 생성

        getCoupon(); // 쿠폰 데이터 GET

        getPayment();   // 결제 데이터 GET

        // 찜한 가게 버튼 클릭 리스너
        couponBtn.setOnClickListener(view -> {
            // 버튼 활성화 / 비활성화
            couponBtn.setEnabled(false);
            couponBtn.setBackgroundResource(R.drawable.round_select_color_little);
            paymentBtn.setEnabled(true);
            paymentBtn.setBackgroundResource(R.drawable.round_unselect_color_little);

            // 리사이클러뷰 토글
            paymentRv.setVisibility(View.GONE);

            // 값이 있을때만 보이기
            if(Coupons.size() > 0){
                couponRv.setVisibility(View.VISIBLE);
                noDataTitle.setVisibility(View.GONE);
            }else{
                noDataTitle.setText("목록이 없습니다.");
                noDataTitle.setVisibility(View.VISIBLE);
            }
        });

        // 찜한 협업 리스트 버튼 클릭 리스너
        paymentBtn.setOnClickListener(view -> {
            // 버튼 활성화 / 비활성화
            couponBtn.setEnabled(true);
            couponBtn.setBackgroundResource(R.drawable.round_unselect_color_little);
            paymentBtn.setEnabled(false);
            paymentBtn.setBackgroundResource(R.drawable.round_select_color_little);

            // 리사이클러뷰 토글
            couponRv.setVisibility(View.GONE);

            // 값이 있을때만 보이기
            if(Payments.size() > 0){
                paymentRv.setVisibility(View.VISIBLE);
                noDataTitle.setVisibility(View.GONE);
            }else{
                noDataTitle.setText("목록이 없습니다.");
                noDataTitle.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    // 뷰 생성
    private void initView() {
        couponBtn = root.findViewById(R.id.bag_coupon_btn); // 사용 가능 쿠폰 버튼
        paymentBtn = root.findViewById(R.id.bag_payment_btn); // 주문/결제 내역 버튼
        noDataTitle = root.findViewById(R.id.bag_no_data_title);  // 데이터 없을 경우 안내 텍스트
        couponRv = root.findViewById(R.id.bag_coupon_rv);  // 사용 가능 쿠폰 리사이클러뷰
        paymentRv = root.findViewById(R.id.bag_payment_rv);  // 주문/결제 내역 리사이클러뷰

        if (loginUserId == 0) {
            couponBtn.setVisibility(View.GONE);
            paymentBtn.setVisibility(View.GONE);
            couponRv.setVisibility(View.GONE);
            paymentRv.setVisibility(View.GONE);
            noDataTitle.setVisibility(View.VISIBLE);
            noDataTitle.setText("로그인 후 이용 가능합니다.");
        }
    }

    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            gpsPossible = true;         // Gps 사용 가능 여부
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(root.getContext(), "권한 허용을 하지 않으면 위치 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
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

    // 가게 데이터 GET
    private void getStore(int storeId) {
        String getSelectStorePath = GET_SELECT_STORE_PATH + String.format("?storeId=%s", storeId);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest getSelectStoreRequest = new StringRequest(Request.Method.GET, HOST + getSelectStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray selectStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                SelectStore = new ArrayList<>();    // 가게 데이터 초기화

                for (int i = 0; i < selectStoreArr.length(); i++) {
                    JSONObject object = selectStoreArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

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

                    SelectStore.add(mainStore);  // 선택한 가게 정보 저장
                }

                getBookmarkStore(); // 유저 찜한 가게 목록 데이터 GET

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getSelectStoreError", "onErrorResponse : " + error);
        });

        getSelectStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(getSelectStoreRequest);     // RequestQueue에 요청 추가
    }

    // 유저 찜한 가게 정보 Return
    private void getBookmarkStore(){
        // GET 방식 파라미터 설정
        String bookmarkStorePath = GET_BOOKMARK_STORE_PATH;
        bookmarkStorePath += String.format("?userId=%s", User.getInt("userId", 0));     // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkStoreRequest = new StringRequest(Request.Method.GET, HOST + bookmarkStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray bookmarkStoreArr = jsonObject.getJSONArray("bookmarkStore");  // 객체에 store라는 Key를 가진 JSONArray 생성

                BookmarkStore = new ArrayList<>();  // 찜한 가게 정보 초기화

                if(bookmarkStoreArr.length() > 0) {
                    for (int i = 0; i < bookmarkStoreArr.length(); i++) {
                        JSONObject object = bookmarkStoreArr.getJSONObject(i);          // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        mainBookmarkStoreData bookmarkStore = new mainBookmarkStoreData(
                                object.getInt("bookmarkStoreId")                        // 가게 고유 아이디
                                , object.getInt("storeId")); // 현위치에서 가게까지의 거리

                        BookmarkStore.add(bookmarkStore);  // 찜한 가게 정보 저장
                    }
                }

                if(intentFlag.equals("InfoActivity")){    // InfoActivity로 화면전환

                    Intent intent = new Intent(getActivity(), InfoActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putExtra("Store", SelectStore.get(0));
                    intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                    intent.putExtra("pageName", "BagFragment");

                    // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                    activityResultLauncher.launch(intent);

                }else if(intentFlag.equals("BagPaymentDetailActivity")){    // BagPaymentDetailActivity로 화면전환

                    Intent intent = new Intent(getActivity(), BagPaymentDetailActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putExtra("store", SelectStore.get(0));
                    intent.putExtra("payment", Payments.get(clickRvPosition));
                    intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                    intent.putParcelableArrayListExtra("paymentMenu", PaymentMenus.get(clickRvPosition));

                    // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
                    activityResultLauncher.launch(intent);
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

    // 쿠폰 데이터 GET
    private void getCoupon(){
        // GET 방식 파라미터 설정
        String couponPath = GET_COUPON_PATH + String.format("?userId=%s", loginUserId);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest getCouponRequest = new StringRequest(Request.Method.GET, HOST + couponPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray couponArr = jsonObject.getJSONArray("coupon");  // 객체에 coupon이라는 Key를 가진 JSONArray 생성

                if(couponArr.length() > 0) {
                    for (int i = 0; i < couponArr.length(); i++) {
                        JSONObject object = couponArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 쿠폰 데이터 생성 및 저장
                        BagCouponData bagCouponData = new BagCouponData(
                                object.getInt("couponId")       // 쿠폰 고유 아이디
                                , object.getInt("storeId")  // 가게 고유 아이디
                                , object.getInt("userId")       // 유저 고유 아이디
                                , object.getInt("discountRate") // 쿠폰 할인율
                                , object.getString("expDate")   // 쿠폰 만료일
                                , object.getString("storeName") // 해당 쿠폰 가게 명
                                , HOST + object.getString("storeImage"));   // 해당 쿠폰 가게 이미지

                        Coupons.add(bagCouponData); // 쿠폰 데이터 저장
                    }

                    // LayoutManager 객체 생성
                    couponRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

                    // 어뎁터 객체 생성
                    couponAdapter = new BagCouponRvAdapter(getActivity(), Coupons);
                    couponRv.setAdapter(couponAdapter);   // 어뎁터 객체 지정

                    // 쿠폰 리사이클러뷰 클릭 리스너
                    couponAdapter.setonBagCouponRvClickListener((v, position, flag) -> {
                        if(flag.equals("image")){   // 가게 이미지 클릭 시

                            clickStoreId = Coupons.get(position).getStoreId();

                            intentFlag = "InfoActivity"; // 결제에서 화면전환 Flag SET
                            getStore(clickStoreId);     // 클릭한 가게 데이터 GET
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getCouponError", "onErrorResponse : " + error);
        });

        getCouponRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(getCouponRequest);      // RequestQueue에 요청 추가
    }

    // 결제 데이터 GET
    private void getPayment(){
        // GET 방식 파라미터 설정
        String paymentPath = GET_PAYMENT_PATH + String.format("?userId=%s", loginUserId);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest getPaymentRequest = new StringRequest(Request.Method.GET, HOST + paymentPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray paymentArr = jsonObject.getJSONArray("payment");  // 객체에 payment이라는 Key를 가진 JSONArray 생성

                if(paymentArr.length() > 0) {
                    for (int i = 0; i < paymentArr.length(); i++) {
                        JSONObject object = paymentArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 결제 데이터 생성 및 저장
                        BagPaymentData bagPaymentData = new BagPaymentData(
                                object.getInt("paymentId")       // 결제 고유 아이디
                                , object.getInt("storeId")  // 가게 고유 아이디
                                , object.getInt("userId")       // 유저 고유 아이디
                                , object.getInt("paymentPrice") // 결제 금액
                                , object.getString("paymentDate")   // 결제 일자
                                , object.getString("storeName") // 결제 가게 명
                                , HOST + object.getString("storeImage"));   // 결제 가게 이미지

                        Payments.add(bagPaymentData); // 결제 데이터 저장

                        JSONArray paymentMenuArr = object.getJSONArray("paymentMenu");  // 객체에 paymentMenu이라는 Key를 가진 JSONArray 생성

                        PaymentMenus.add(new ArrayList<>());    // 결제 메뉴 데이터 초기화

                        // 결제한 메뉴 데이터 생성 및 저장
                        for(int j = 0; j < paymentMenuArr.length(); j++){
                            JSONObject menuObject = paymentMenuArr.getJSONObject(j);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                            // 결제 데이터 생성 및 저장
                            BagPaymentMenuData bagPaymentMenuData = new BagPaymentMenuData(
                                    menuObject.getInt("paymentMenuId")       // 결제한 메뉴 테이블 고유 아이디
                                    , menuObject.getInt("paymentId")  // 결제 고유 아이디
                                    , menuObject.getInt("menuId")       // 메뉴 고유 아이디
                                    , menuObject.getInt("menuCount") // 결제 메뉴 수량
                                    , menuObject.getString("menuName")  // 결제 메뉴 명
                                    , menuObject.getInt("menuPrice"));   // 결제 메뉴 가격

                            PaymentMenus.get(i).add(bagPaymentMenuData); // 결제 메뉴 데이터 저장
                        }
                    }
                }

                // LayoutManager 객체 생성
                paymentRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

                // 어뎁터 객체 생성
                paymentAdapter = new BagPaymentRvAdapter(getActivity(), Payments, PaymentMenus);
                paymentRv.setAdapter(paymentAdapter);   // 어뎁터 객체 지정

                // 결제 리사이클러뷰 클릭 리스너
                paymentAdapter.setonBagPaymentRvClickListener((v, position, flag) -> {
                    if(flag.equals("total")){   // 리사이클러뷰 클릭 시
                        clickStoreId = Payments.get(position).getStoreId();
                        clickRvPosition = position;
                        intentFlag = "BagPaymentDetailActivity"; // 결제에서 화면전환 Flag SET

                        getStore(clickStoreId);     // 클릭한 가게 데이터 GET
                    }else if(flag.equals("image")){   // 가게 이미지 클릭 시

                        clickStoreId = Payments.get(position).getStoreId();
                        intentFlag = "InfoActivity"; // 결제에서 화면전환 Flag SET

                        getStore(clickStoreId);     // 클릭한 가게 데이터 GET
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getPaymentError", "onErrorResponse : " + error);
        });

        getPaymentRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(getPaymentRequest);      // RequestQueue에 요청 추가
    }
}