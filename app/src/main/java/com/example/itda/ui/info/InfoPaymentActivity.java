package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.http.GET;

public class InfoPaymentActivity extends AppCompatActivity implements onInfoPaymentSelectMenuRvClickListener {
    private ImageButton infoPaymentBackBtn;     // 상단 뒤로가기 버튼
    private Button infoPaymentStoreName;       // 상단 가게 이름
    private Button infoPaymentBtn;       // 결제 버튼
    private TextView infoPaymentTotalPrice;       // 쿠폰 할인 받기 전 결제 금액 ( 쿠폰 있을 경우 )
    private TextView infoPaymentFinalTotalPrice;       // 쿠폰 할인 받은 후 결제 금액 ( 쿠폰 있을 경우 )

    private RecyclerView infoPaymentSelectMenuRv;  // 선택한 메뉴 리사이클러뷰
    private Spinner infoPaymentCouponSpinner;  // 쿠폰 Spinner

    private InfoPaymentSelectMenuRvAdapter infoPaymentSelectMenuAdapter;    // 선택한 메뉴 리사이클러뷰 어뎁터
    private InfoPaymentCouponSpinnerAdapter infoPaymentCouponSpinnerAdapter;    // 쿠폰 Spinner 어뎁터

    private ArrayList<infoMenuData> selectMenu = new ArrayList<>();   // 선택한 메뉴 데이터
    private ArrayList<infoPaymentCouponData> Coupons = new ArrayList<>();   // 쿠폰 데이터

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue

    private String GET_PAYMENT_COUPON_PATH;      // 쿠폰 목록 조회 Rest API
    private String HOST;            // Host 정보

    private int storeId;    // 가게 고유 아이디
    private int userId;     // 유저 고유 아이디
    private String storeName;   // 가게 명
    private int totalPrice = 0; // 결제 총 금액

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_payment);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        GET_PAYMENT_COUPON_PATH = ((globalMethod) getApplication()).getInfoPaymentCouponPath();      // 쿠폰 목록 조회 Rest API
        HOST = ((globalMethod) getApplication()).getHost();                       // Host 정보

        initView(); // 뷰 생성

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoPaymentBackBtn.setOnClickListener(view -> finish());

        // Intent Data SET
        storeId = getIntent().getExtras().getInt("storeId");   // 가게 고유 아이디
        userId = getIntent().getExtras().getInt("userId");    // 유저 고유 아이디
        storeName = getIntent().getExtras().getString("storeName"); // 가게 명

        infoPaymentStoreName.setText(storeName);    // 가게 명 SET

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        selectMenu = getIntent().getParcelableArrayListExtra("selectMenu"); // 메뉴 데이터

        // 결제 총 금액 계산
        for(int i = 0; i < selectMenu.size(); i++){
            totalPrice += selectMenu.get(i).getMenuPrice() * selectMenu.get(i).getMenuCount();
        }
        // 숫자 형식 SET ( 콤마 추가 )
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        infoPaymentTotalPrice.setText(myFormatter.format(getTotalMenuPrice(selectMenu, 0)) + "원");  // 결제 총 금액 ( 쿠폰 적용 X )
        infoPaymentFinalTotalPrice.setText(myFormatter.format(getTotalMenuPrice(selectMenu, 0)) + "원");  // 결제 총 금액 ( 쿠폰 적용 O )


        // 선택한 메뉴 리사이클러뷰 SET
        // LayoutManager 객체 생성
        infoPaymentSelectMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        infoPaymentSelectMenuAdapter = new InfoPaymentSelectMenuRvAdapter(this, this, selectMenu);  // 리사이클러뷰 어뎁터 객체 생성
        infoPaymentSelectMenuRv.setAdapter(infoPaymentSelectMenuAdapter); // 리사이클러뷰 어뎁터 객체 지정

        getPaymentCoupon(); // 쿠폰 데이터 GET

        // 결제 버튼 클릭 리스너
        infoPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("결제 버튼 클릭!!");
            }
        });


        infoPaymentCouponSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // 결제 최종 금액 SET
                infoPaymentFinalTotalPrice.setText(myFormatter.format(getTotalMenuPrice(selectMenu, Coupons.get(position).getDiscountRate())) + "원");  // 결제 총 금액 ( 쿠폰 적용 O )
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("spinnerNothingSelected", "spinnerNothingSelected");
            }
        });
    }

    private void initView(){
        infoPaymentBackBtn = findViewById(R.id.info_payment_back_ic);  // 상단 뒤로가기 버튼
        infoPaymentStoreName = findViewById(R.id.info_payment_main_store_name);   // 상단 가게 이름
        infoPaymentBtn = findViewById(R.id.info_payment_btn);   // 결제 버튼
        infoPaymentSelectMenuRv = findViewById(R.id.info_payment_select_menu_rv);  // 선택한 메뉴 리사이클러뷰
        infoPaymentCouponSpinner = findViewById(R.id.info_payment_coupon_spinner);  // 쿠폰 Spinner
        infoPaymentTotalPrice = findViewById(R.id.info_payment_total_price);  // 쿠폰 할인 받기 전 결제 금액 ( 쿠폰 있을 경우 )
        infoPaymentFinalTotalPrice = findViewById(R.id.info_payment_final_total_price); // 쿠폰 할인 받은 후 결제 금액 ( 쿠폰 있을 경우 )
    }

    // 쿠폰 데이터 GET
    private void getPaymentCoupon(){
        // GET 방식 파라미터 설정
        String paymentCouponPath = GET_PAYMENT_COUPON_PATH + String.format("?storeId=%s", storeId);
        paymentCouponPath += String.format("&&userId=%s", userId);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest getPaymentCouponRequest = new StringRequest(Request.Method.GET, HOST + paymentCouponPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray couponArr = jsonObject.getJSONArray("coupon");  // 객체에 collabo라는 Key를 가진 JSONArray 생성

                // 쿠폰 없음 데이터 추가 ( 더미 데이터 )
                Coupons.add(new infoPaymentCouponData(0, 0, 0, 0,""));

                if(couponArr.length() > 0) {
                    for (int i = 0; i < couponArr.length(); i++) {
                        JSONObject object = couponArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 협업 데이터 생성 및 저장
                        infoPaymentCouponData infoCollaboData = new infoPaymentCouponData(
                                object.getInt("couponId")       // 쿠폰 고유 아이디
                                , object.getInt("storeId")  // 가게 고유 아이디
                                , object.getInt("userId")       // 유저 고유 아이디
                                , object.getInt("discountRate") // 쿠폰 할인율
                                , object.getString("expDate"));   // 쿠폰 만료일

                        Coupons.add(infoCollaboData); // 쿠폰 데이터 저장
                    }

                }

                // 쿠폰 Spinner 어뎁터 객체 생성
                infoPaymentCouponSpinnerAdapter = new InfoPaymentCouponSpinnerAdapter(this, Coupons);
                infoPaymentCouponSpinner.setAdapter(infoPaymentCouponSpinnerAdapter);   // Spinner 어뎁터 객체 지정

                // droplist를 spinner와 간격을 두고 나오게 해줌
                infoPaymentCouponSpinner.setDropDownVerticalOffset(ConvertDPtoPX(50));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getPaymentCouponError", "onErrorResponse : " + error);
        });

        getPaymentCouponRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(getPaymentCouponRequest);      // RequestQueue에 요청 추가
    }

    // 선택한 메뉴 리사이클러뷰 클릭 리스너
    @Override
    public void onInfoPaymentSelectMenuRvClick(View v, int position, String flag) {
        if(flag.equals("minus")){
            // 개수가 1개 이상을 경우만 -1
            if(selectMenu.get(position).getMenuCount() > 0){
                selectMenu.get(position).setMenuCount(selectMenu.get(position).getMenuCount() - 1);
                infoPaymentSelectMenuAdapter.setMenu(selectMenu);
                infoPaymentSelectMenuAdapter.notifyItemChanged(position);
            }
        }else if(flag.equals("plus")){
            // 개수 +1
            selectMenu.get(position).setMenuCount(selectMenu.get(position).getMenuCount() + 1);
            infoPaymentSelectMenuAdapter.setMenu(selectMenu);
            infoPaymentSelectMenuAdapter.notifyItemChanged(position);
        }

        // 숫자 형식 SET ( 콤마 추가 )
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        infoPaymentTotalPrice.setText(myFormatter.format(getTotalMenuPrice(selectMenu, 0)) + "원");  // 결제 총 금액 ( 쿠폰 적용 X )
        infoPaymentFinalTotalPrice.setText(myFormatter.format(getTotalMenuPrice(selectMenu, Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()).getDiscountRate())) + "원");  // 결제 총 금액 ( 쿠폰 적용 O )
    }

    // 결제 총 금액 Return
    public int getTotalMenuPrice(ArrayList<infoMenuData> menus, int discountRate){
        double totalPrice = 0;

        if(menus != null && menus.size() > 0){
            for(int i = 0; i < menus.size(); i++){
                totalPrice += menus.get(i).getMenuPrice() * menus.get(i).getMenuCount();
            }
        }

        // 쿠폰 적용
        return (int) (Math.round((double) (totalPrice * ( 1.0 - discountRate / 100.0))));
    }

    // dp to pixel 변환
    public int ConvertDPtoPX(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}

