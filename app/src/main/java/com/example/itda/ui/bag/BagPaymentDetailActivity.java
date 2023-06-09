package com.example.itda.ui.bag;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BagPaymentDetailActivity extends AppCompatActivity {
    // Data
    private mainStoreData Store;    // 결제한 가게 정보 저장
    private ArrayList<mainBookmarkStoreData> BookmarkStore = new ArrayList<>(); // 유저 찜한 가게 목록
    private bagPaymentData Payment; // 결제 데이터
    private ArrayList<bagPaymentMenuData> PaymentMenu = new ArrayList<>();  // 결제한 메뉴 데이터


    // Layout
    private ImageButton backIc;     // 상단 뒤로가기 버튼
    private ImageButton storeImage; // 결제한 가게 이미지
    private TextView storeName;     // 결제한 가게 명
    private TextView paymentId;     // 결제 번호
    private TextView paymentDate;   // 결제 일자
    private TextView expireDate;    // 결제 상품 만료 일자
    private TextView paymentUsedStatus; // 결제 상품 사용 가능 상태
    private TextView orderPrice;        // 주문 금액
    private TextView couponPrice;       // 쿠폰 할인 금액
    private TextView totalPrice;        // 총 결제 금액
    private LinearLayout couponPriceLayout; // 쿠폰 할인 금액 전체 레이아웃
    public RecyclerView paymentMenuRv;      // 결제한 메뉴 리사이클러뷰


    // Adapter
    public BagPaymentDetailMenuRvAdapter paymentMenuAdapter; // 결제한 메뉴 리사이클러뷰 어뎁터


    // intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bag_payment_detail);

        Store = getIntent().getParcelableExtra("store");        // 결제한 가게 정보 저장
        Payment = getIntent().getParcelableExtra("payment");    // 결제 데이터
        BookmarkStore = getIntent().getParcelableArrayListExtra("bookmarkStore");   // 유저 찜한 가게 목록
        PaymentMenu = getIntent().getParcelableArrayListExtra("paymentMenu");       // 결제한 메뉴 데이터

        initView(); // 뷰 생성

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 2000) { // resultCode가 2000으로 넘어왔다면 InfoActivity에서 넘어온것
                assert result.getData() != null;
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");  // 찜한 목록 데이터
            }
        });

        // 가게 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Payment.getStoreImagePath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(storeImage);       // 이미지를 보여줄 View를 지정

        // 가게 이름 SET
        storeName.setText(Payment.getStoreName());

        // 주문 번호 SET
        String paymentIdTxt = "주문 번호 : " + Payment.getPaymentId();
        paymentId.setText(paymentIdTxt);

        // 결제 일자 SET
        String paymentDateTxt = "결제 일자 : " + Payment.getPaymentPayDate();
        paymentDate.setText(paymentDateTxt);

        // 결제 상품 만료 일자 SET
        String expireDateTxt = "만료 일자 : " + Payment.getPaymentExpDate();
        expireDate.setText(expireDateTxt);

        // 결제 상품 사용 가능 상태 SET
        paymentUsedStatus.setText(Payment.getPaymentUsedStatus());

        // 사용 가능 상태 따른 글자 색 변경
        switch (Payment.getPaymentUsedStatus()){
            case "사용 가능" :
                paymentUsedStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.green05, getApplicationContext().getTheme()));
                break;
            case "사용 완료" :
                paymentUsedStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.orange05, getApplicationContext().getTheme()));
                break;
            case "기간 만료" :
                paymentUsedStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.red04, getApplicationContext().getTheme()));
                break;
            default :
                paymentUsedStatus.setTextColor(getApplicationContext().getResources().getColor(R.color.gray03, getApplicationContext().getTheme()));
                break;
        }

        DecimalFormat numberFormatter = new DecimalFormat("###,###");   // 문자열 형식 변경 Formatter ( 숫자 + 콤마 )

        // 주문 금액 SET
        String orderPriceTxt = numberFormatter.format(getTotalMenuPrice(PaymentMenu, 0)) + "원";
        orderPrice.setText(orderPriceTxt);

        // 쿠폰 할인 금액 SET
        if(Payment.getPaymentUsedCouponDisRate() != 0){ // 쿠폰을 선택한 경우
            couponPriceLayout.setVisibility(View.VISIBLE);

            String couponPriceTxt = numberFormatter.format(getTotalMenuPrice(PaymentMenu, Payment.getPaymentUsedCouponDisRate()) - getTotalMenuPrice(PaymentMenu, 0)) + "원";
            couponPrice.setText(couponPriceTxt);
        }

        // 최종 결제 금액
        String totalPriceTxt = numberFormatter.format(getTotalMenuPrice(PaymentMenu, Payment.getPaymentUsedCouponDisRate())) + "원";
        totalPrice.setText(totalPriceTxt);

        // 결제한 메뉴 리사이클러뷰 어뎁터 객체 생성
        paymentMenuAdapter = new BagPaymentDetailMenuRvAdapter(this, PaymentMenu);

        // 사진 리사이클러뷰 어뎁터 객체 생성
        paymentMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        paymentMenuRv.setAdapter(paymentMenuAdapter);   // 리사이클러뷰 어뎁터 객체 지정

        // 뒤로 가기 버튼 클릭 리스너
        backIc.setOnClickListener(view -> {
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(BagPaymentDetailActivity.this, MainActivity.class);

            intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 가게 찜한 목록 데이터
            setResult(3000, intent);    // 결과 코드와 intent 값 전달
            finish();
        });

        // 가게 이미지 클릭 리스너
        storeImage.setOnClickListener(view -> {
            Intent intent = new Intent(BagPaymentDetailActivity.this, InfoActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

            // 데이터 송신을 위한 Parcelable interface 사용
            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
            intent.putExtra("Store", Store);   // 가게 데이터
            intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);   // 유저 찜한 목록 데이터
            intent.putExtra("pageName", "BagPaymentDetailActivity");        // 화면전환 페이지 명

            activityResultLauncher.launch(intent);  // 새 Activity 인스턴스 시작
        });
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.bag_payment_detail_back_ic); // 상단 뒤로가기 버튼
        storeImage = findViewById(R.id.bag_payment_detail_store_image); // 결제한 가게 이미지
        storeName = findViewById(R.id.bag_payment_detail_store_name);   // 결제한 가게 명
        paymentId = findViewById(R.id.bag_payment_detail_pay_id);       // 결제 번호
        paymentDate = findViewById(R.id.bag_payment_detail_pay_date);   // 결제한 일자
        expireDate = findViewById(R.id.bag_payment_detail_exp_date);    // 결제 상품 만료 일자
        paymentUsedStatus = findViewById(R.id.bag_payment_detail_used_status);  // 결제 상품 사용 가능 상태
        orderPrice = findViewById(R.id.bag_payment_detail_order_price);     // 주문 금액
        couponPrice = findViewById(R.id.bag_payment_detail_coupon_price);   // 쿠폰 할인 금액
        totalPrice = findViewById(R.id.bag_payment_detail_total_price);     // 총 결제 금액
        couponPriceLayout = findViewById(R.id.bag_payment_detail_coupon_layout);    // 쿠폰 할인 금액 전체 레이아웃
        paymentMenuRv = findViewById(R.id.bag_payment_detail_menu_rv);  // 결제한 메뉴 리사이클러뷰
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(BagPaymentDetailActivity.this, MainActivity.class);

            intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 유저 찜한 목록 데이터
            setResult(3000, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
    }

    // 결제 총 금액 Return
    // Param => menus : 결제한 메뉴 데이터
    //          discountRate : 쿠폰 할인 율 ( 쿠폰 적용 안 되었을 경우 0 )
    public int getTotalMenuPrice(ArrayList<bagPaymentMenuData> menus, int discountRate){
        double totalPrice = 0;  // 최종 금액

        // 각 메뉴에 쿠폰 할인율 적용
        if(menus != null && menus.size() > 0){
            for(int i = 0; i < menus.size(); i++){
                totalPrice += getMenuPriceWithCoupon(menus.get(i), discountRate) * menus.get(i).getMenuCount();
            }
        }

        return (int) totalPrice;
    }

    // 쿠폰이 적용된 결제한 메뉴 금액 Return
    // Param => menus : 결제한 메뉴 데이터
    //          discountRate : 쿠폰 할인 율 ( 쿠폰 적용 안 되었을 경우 0 )
    public int getMenuPriceWithCoupon(bagPaymentMenuData menu, int discountRate){

        // 쿠폰 적용
        return (int) (Math.round((menu.getMenuPrice() * ( 1.0 - discountRate / 100.0))));
    }
}

