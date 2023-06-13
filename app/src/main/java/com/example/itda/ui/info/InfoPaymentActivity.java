package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.BuildConfig;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;
import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.BootpayAnalytics;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

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

    private ArrayList<InfoMenuData> selectMenu = new ArrayList<>();   // 선택한 메뉴 데이터
    private ArrayList<infoPaymentCouponData> Coupons = new ArrayList<>();   // 쿠폰 데이터

    private InfoPaymentData Payment;   // 결제 데이터

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue

    private String GET_PAYMENT_COUPON_PATH;      // 쿠폰 목록 조회 Rest API
    private String INSERT_PAYMENT_PATH;      // 결제 Insert Rest API
    private String INSERT_PAYMENT_COUPON_PATH;      // 결제 후 쿠폰 Insert Rest API
    private String HOST;            // Host 정보

    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private int storeId;    // 가게 고유 아이디
    private int userId;     // 유저 고유 아이디
    private String storeName;   // 가게 명

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_payment);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        GET_PAYMENT_COUPON_PATH = ((globalMethod) getApplication()).getInfoPaymentCouponPath();      // 쿠폰 목록 조회 Rest API
        INSERT_PAYMENT_PATH = ((globalMethod) getApplication()).insertInfoPaymentPath();      // 결제 Insert Rest API
        INSERT_PAYMENT_COUPON_PATH = ((globalMethod) getApplication()).insertInfoPaymentCouponPath();      // 결제 후 쿠폰 Insert Rest API
        HOST = ((globalMethod) getApplication()).getHost();                       // Host 정보

        // 초기설정 - 해당 프로젝트(안드로이드)의 application id 값을 설정합니다. 결제와 통계를 위해 꼭 필요합니다.
        BootpayAnalytics.init(this, BuildConfig.BOOTPAY_APPLICATION_KEY);

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000) { // resultCode가 1000으로 넘어왔다면 결제 완료
                // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                Intent intent = new Intent(InfoPaymentActivity.this, InfoOrderActivity.class);

                setResult(1000, intent);    // 결과 코드와 intent 값 전달
                finish();
            }
        });

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
                PaymentTest(view);
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

    // 결제 데이터 Insert
    private void insertInfoPayment() throws JSONException {
        Map<String, String> param = new HashMap<>();
        param.put("storeId", String.valueOf(storeId));   // 가게 고유 아이디
        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("couponId", String.valueOf(Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()).getCouponId()));   // 쿠폰 고유 아이디
        param.put("paymentPrice", String.valueOf(getTotalMenuPrice(selectMenu, Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()).getDiscountRate())));   // 결제 총 금액

        // 결제한 메뉴 배열 to JSONArray
        JSONArray jMenuArray = new JSONArray(); //배열

        for (int i = 0; i < selectMenu.size(); i++) {
            JSONObject jMenuObject = new JSONObject();  //배열 내에 들어갈 json

            jMenuObject.put("menuId", selectMenu.get(i).getMenuId());
            jMenuObject.put("menuCount", selectMenu.get(i).getMenuCount());
            jMenuArray.put(jMenuObject);
        }

        param.put("paymentMenus", jMenuArray.toString());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertPaymentRequest = new StringRequest(Request.Method.POST, HOST + INSERT_PAYMENT_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(getApplicationContext(), "결제 완료", R.style.blueToast).show();

                    JSONObject payment = jsonObject.getJSONArray("payment").getJSONObject(0);  // 객체에 collabo라는 Key를 가진 JSONArray 생성

                    Payment = new InfoPaymentData(
                            payment.getInt("paymentId")       // 결제 고유 아이디
                            , payment.getInt("storeId")  // 가게 고유 아이디
                            , payment.getInt("userId")       // 유저 고유 아이디
                            , payment.getInt("paymentPrice") // 결제 금액
                            , payment.getString("paymentDate")  // 결제 일자
                            , payment.getString("expireDate")   // 결제 상품 만료 일자
                            , payment.getString("storeName")    // 결제 가게 명
                            , HOST + payment.getString("storeImage")   // 결제 가게 이미지
                    );

                    insertInfoPaymentCoupon();  // 쿠폰 데이터 Insert
                }else{
                    StyleableToast.makeText(getApplicationContext(), "결제 insert 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertPaymentError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertPaymentRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertPaymentRequest);      // RequestQueue에 요청 추가
    }

    // 결제 후 쿠폰 데이터 Insert
    private void insertInfoPaymentCoupon() {
        Map<String, String> param = new HashMap<>();
        param.put("storeId", String.valueOf(storeId));   // 가게 고유 아이디
        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("paymentPrice", String.valueOf(Payment.getPaymentPrice()));   // 총 결제 금액

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertPaymentCouponRequest = new StringRequest(Request.Method.POST, HOST + INSERT_PAYMENT_COUPON_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                    Intent intent = new Intent(InfoPaymentActivity.this, InfoPaymentCompleteActivity.class);

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putExtra("payment", Payment);   // 결제 데이터
                    intent.putExtra("coupon", Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()));   // 쿠폰 데이터
                    intent.putParcelableArrayListExtra("paymentMenu", selectMenu);   // 결제한 메뉴 데이터

                    activityResultLauncher.launch(intent);
                }else{
                    StyleableToast.makeText(getApplicationContext(), "쿠폰 insert 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertPaymentCouponError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertPaymentCouponRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertPaymentCouponRequest);      // RequestQueue에 요청 추가
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
    public int getTotalMenuPrice(ArrayList<InfoMenuData> menus, int discountRate){
        double totalPrice = 0;

        // 각 메뉴에 쿠폰 할인율 적용
        if(menus != null && menus.size() > 0){
            for(int i = 0; i < menus.size(); i++){
                totalPrice += getMenuPriceWithCoupon(menus.get(i), discountRate) * menus.get(i).getMenuCount();
            }
        }

        return (int) totalPrice;
    }

    // 쿠폰이 적용된 결제한 메뉴 금액 Return
    public int getMenuPriceWithCoupon(InfoMenuData menu, int discountRate){

        // 쿠폰 적용
        return (int) (Math.round((menu.getMenuPrice() * ( 1.0 - discountRate / 100.0))));
    }

    // dp to pixel 변환
    public int ConvertDPtoPX(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public void PaymentTest(View v) {
        BootUser user = new BootUser().setPhone(User.getString("userNumber", "")); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)


        List items = new ArrayList<>();

        for(int i = 0; i < selectMenu.size(); i++){
            BootItem item = new BootItem()
                    .setName(selectMenu.get(i).getMenuName())   // 상품 명
                    .setId(String.valueOf(selectMenu.get(i).getMenuId()))   // 상품 아이디
                    .setQty(selectMenu.get(i).getMenuCount())   // 상품 수량
                    .setPrice((double) getMenuPriceWithCoupon(selectMenu.get(i), Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()).getDiscountRate()));   // 상품 가격

            items.add(item);
        }

        Payload payload = new Payload();
        payload.setApplicationId(BuildConfig.BOOTPAY_APPLICATION_KEY)   // 해당 프로젝트(안드로이드)의 application id 값
                .setOrderName("itda 결제 테스트")
                .setPg("이니시스")  // 결제할 PG 사
                .setMethod("")  // 결제수단
                .setOrderId("1234")
                .setPrice((double) getTotalMenuPrice(selectMenu, Coupons.get(infoPaymentCouponSpinner.getSelectedItemPosition()).getDiscountRate()))
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        StyleableToast.makeText(getApplicationContext(), "결제가 취소되었습니다.", R.style.redToast).show();
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        StyleableToast.makeText(getApplicationContext(), "결제 진행 중 오류가 발생했습니다. 관리자에게 문의해주세요.", R.style.redToast).show();
                        Log.d("bootpay", "error: " + data);
                    }

                    @Override
                    public void onClose() {
                        Log.d("bootpay", "close: ");
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        StyleableToast.makeText(getApplicationContext(), "가상 계좌 입금 번호가 발급되었습니다.", R.style.orangeToast).show();
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        try {
                            insertInfoPayment();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).requestPayment();
    }
}

