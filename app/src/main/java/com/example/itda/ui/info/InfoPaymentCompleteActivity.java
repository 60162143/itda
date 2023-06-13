package com.example.itda.ui.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
import com.example.itda.ui.bag.BagPaymentDetailMenuRvAdapter;
import com.example.itda.ui.mypage.MyPageEditActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InfoPaymentCompleteActivity extends AppCompatActivity {
    private InfoPaymentData Payment;  // 결제 데이터
    private infoPaymentCouponData Coupon;  // 쿠폰 데이터
    private ArrayList<InfoMenuData> PaymentMenu = new ArrayList<>();  // 결제한 메뉴 데이터

    private ImageButton storeImage; // 결제한 가게 이미지
    private TextView storeName;  // 결제한 가게 명
    private TextView paymentPrice;  // 결제한 금액
    private TextView paymentDate;  // 결제한 일자
    private TextView expireDate;  // 결제 상품 만료 일자
    private TextView orderPrice;  // 주문 금액
    private TextView couponPrice;  // 쿠폰 할인 금액
    private TextView totalPrice;  // 최종 결제 금액

    private Button confirmBtn;  // 확인 버튼

    private LinearLayout couponPriceLayout;  // 쿠폰 할인 금액 전체 레이아웃

    public RecyclerView paymentMenuRv;  // 결제한 메뉴 리사이클러뷰

    public InfoPaymentCompleteMenuRvAdapter paymentMenuAdapter; // 결제한 메뉴 리사이클러뷰 어뎁터

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_payment_complete);

        initView(); // 뷰 생성

        // ---------------- 화면전환 Section ---------------------
        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 2000) { // resultCode가 2000으로 넘어왔다면 InfoActivity에서 넘어온것

            }
        });

        Payment = getIntent().getParcelableExtra("payment"); // 결제 데이터
        Coupon = getIntent().getParcelableExtra("coupon"); // 쿠폰 데이터
        PaymentMenu = getIntent().getParcelableArrayListExtra("paymentMenu"); // 결제한 메뉴 데이터

        // 가게 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Payment.getStoreImage()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(storeImage);       // 이미지를 보여줄 View를 지정

        storeName.setText(Payment.getStoreName()); // 가게 이름

        // 결제한 금액
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        paymentPrice.setText("결제 금액 : " + myFormatter.format(Payment.getPaymentPrice()) + "원");

        paymentDate.setText("결제 날짜 : " + Payment.getPaymentDate()); // 결제 일자
        expireDate.setText("만료 날짜 : " + Payment.getExpireDate()); // 결제 상품 만료 일자

        // 결제한 메뉴 리사이클러뷰 어뎁터 객체 생성
        paymentMenuAdapter = new InfoPaymentCompleteMenuRvAdapter(this, PaymentMenu);

        // 결제한 메뉴 리사이클러뷰 어뎁터 객체 생성
        paymentMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        paymentMenuRv.setAdapter(paymentMenuAdapter); // 리사이클러뷰 어뎁터 객체 지정


        // 주문 금액
        orderPrice.setText(myFormatter.format(getTotalMenuPrice(PaymentMenu, 0)) + "원");

        // 쿠폰을 선택한 경우
        if(Coupon.getDiscountRate() != 0){
            couponPriceLayout.setVisibility(View.VISIBLE);
            // 쿠폰 할인 금액
            couponPrice.setText(myFormatter.format(getTotalMenuPrice(PaymentMenu, Coupon.getDiscountRate()) - getTotalMenuPrice(PaymentMenu, 0)) + "원");
        }

        // 최종 결제 금액
        totalPrice.setText(myFormatter.format(getTotalMenuPrice(PaymentMenu, Coupon.getDiscountRate())) + "원");


        // 확인 버튼 클릭 리스너
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                Intent intent = new Intent(InfoPaymentCompleteActivity.this, InfoPaymentActivity.class);

                setResult(1000, intent);    // 결과 코드와 intent 값 전달
                finish();
            }
        });
    }

    // 뷰 생성
    private void initView(){
        storeImage = findViewById(R.id.info_payment_complete_store_image);  // 결제한 가게 이미지
        storeName = findViewById(R.id.info_payment_complete_store_name);  // 결제한 가게 명
        paymentPrice = findViewById(R.id.info_payment_complete_price);  // 결제한 금액
        paymentDate = findViewById(R.id.info_payment_complete_pay_date);  // 결제한 일자
        expireDate = findViewById(R.id.info_payment_complete_exp_date);  // 결제 상품 만료 일자
        paymentMenuRv = findViewById(R.id.info_payment_complete_menu_rv);  // 결제한 메뉴 리사이클러뷰
        orderPrice = findViewById(R.id.info_payment_complete_order_price);  // 주문 금액
        couponPrice = findViewById(R.id.info_payment_complete_coupon_price);  // 쿠폰 할인 금액
        totalPrice = findViewById(R.id.info_payment_complete_total_price);  // 최종 결제 금액
        couponPriceLayout = findViewById(R.id.info_payment_complete_coupon_layout);  // 쿠폰 할인 금액 전체 레이아웃
        confirmBtn = findViewById(R.id.info_payment_complete_btn);  // 확인 버튼
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(InfoPaymentCompleteActivity.this, InfoPaymentActivity.class);

            setResult(1000, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
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
}

