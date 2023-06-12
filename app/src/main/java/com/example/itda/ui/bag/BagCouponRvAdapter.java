package com.example.itda.ui.bag;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.home.onMainStoreRvClickListener;
import com.example.itda.ui.info.infoPaymentCouponData;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class BagCouponRvAdapter extends RecyclerView.Adapter<BagCouponRvAdapter.CustomMainCategoryViewHolder>{
    private ArrayList<BagCouponData> Coupons;  // 사용가능 쿠폰 데이터

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onBagCouponRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    // 프래그먼트에서 생성, 리스너는 따로 SET
    public BagCouponRvAdapter(Context context
            , ArrayList<BagCouponData> coupons){
        this.mContext = context;
        this.Coupons = coupons;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public BagCouponRvAdapter.CustomMainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bag_coupon, parent, false);

        return new CustomMainCategoryViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull BagCouponRvAdapter.CustomMainCategoryViewHolder holder, int position) {
        BagCouponData coupon = Coupons.get(position);     // 현재 position의 가게 정보

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(coupon.getStoreImagePath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.storeImage);       // 이미지를 보여줄 View를 지정

        holder.storeName.setText(coupon.getStoreName()); // 가게 이름

        // 쿠폰 할인율 글자 크기 수정 ( 한 글자만 수정 ) -> SpannableString 사용
        // setSpan (속성, 시작 위치, 끝 위치, 플래그)
        // ForegroundColorSpan : 글자 색상 지정. (BackgroundColorSpan : 배경색 지정)
        // StyleSpan : 글자의 스타일 지정 (BOLD, ITALIC 등)
        // RelativeSizeSpan : 글자의 상대적 크기 지정 (1.3f는 1.3배)
        String couponDiscountRateTxt = coupon.getDiscountRate() + "% 할인";
        SpannableString spannableCouponDiscountRateTxt = new SpannableString(couponDiscountRateTxt);

        // 할인율에 따른 색, 길이 달라짐
        if(coupon.getDiscountRate() < 10){
            spannableCouponDiscountRateTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#5492D3")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableCouponDiscountRateTxt.setSpan(new RelativeSizeSpan(1.5f), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            spannableCouponDiscountRateTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#4AD5A7")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableCouponDiscountRateTxt.setSpan(new RelativeSizeSpan(1.5f), 0, 2, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.couponDiscountRate.setText(spannableCouponDiscountRateTxt); // 쿠폰 할인 율

        holder.couponExpireDate.setText("사용 가능 기한 : " + coupon.getExpDate() + " 까지"); // 쿠폰 만료일
    }

    // 리스너 설정
    public void setonBagCouponRvClickListener(onBagCouponRvClickListener rvClickListener) {
        BagCouponRvAdapter.rvClickListener = rvClickListener;
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Coupons.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomMainCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageButton storeImage;     // 가게 썸네일
        TextView storeName;         // 가게 명
        TextView couponDiscountRate;     // 쿠폰 할인 율
        TextView couponExpireDate;        // 쿠폰 만료일

        public CustomMainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.bag_coupon_store_image);
            storeName = itemView.findViewById(R.id.bag_coupon_store_name);
            couponDiscountRate = itemView.findViewById(R.id.bag_coupon_discount_rate);
            couponExpireDate = itemView.findViewById(R.id.bag_coupon_expire_date);

            // 리사이클러뷰 이미지 클릭 이벤트 인터페이스 구현
            storeImage.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onBagCouponRvClick(view, getAbsoluteAdapterPosition(), "image");
                }
            });
        }
    }
}
