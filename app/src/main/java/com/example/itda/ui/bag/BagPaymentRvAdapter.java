package com.example.itda.ui.bag;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class BagPaymentRvAdapter extends RecyclerView.Adapter<BagPaymentRvAdapter.CustomMainCategoryViewHolder>{
    private final ArrayList<bagPaymentData> Payments;  // 주문/결제 데이터
    private final ArrayList<ArrayList<bagPaymentMenuData>> PaymentMenus;  // 결제한 메뉴 데이터

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onBagPaymentRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    // 프래그먼트에서 생성, 리스너는 따로 SET
    public BagPaymentRvAdapter(Context context
            , ArrayList<bagPaymentData> payments
            , ArrayList<ArrayList<bagPaymentMenuData>> paymentMenus){
        this.mContext = context;
        this.Payments = payments;
        this.PaymentMenus = paymentMenus;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public BagPaymentRvAdapter.CustomMainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bag_payment, parent, false);

        return new CustomMainCategoryViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull BagPaymentRvAdapter.CustomMainCategoryViewHolder holder, int position) {
        bagPaymentData payment = Payments.get(position);    // 현재 position의 결제 정보
        ArrayList<bagPaymentMenuData> paymentMenus = PaymentMenus.get(position);    // 현재 position의 결제한 메뉴 정보

        // 결제 일자 SET
        String paymentDateTxt = "결제 일자 : " + payment.getPaymentPayDate();
        holder.paymentDate.setText(paymentDateTxt);

        // 결제 상품 만료 일자 SET
        String expireDateTxt = "만료 일자 : " + payment.getPaymentExpDate();
        holder.expireDate.setText(expireDateTxt);

        // 결제 상품 사용 가능 상태 SET
        String paymentUsedStatusTxt = "( " + payment.getPaymentUsedStatus() + " )";
        holder.paymentUsedStatus.setText(paymentUsedStatusTxt);

        // 결제 상푸 사용 가능 상태에 따른 글자색 변경
        switch (payment.getPaymentUsedStatus()){
            case "사용 가능" :
                holder.paymentUsedStatus.setTextColor(mContext.getResources().getColor(R.color.green05, mContext.getTheme()));
                break;
            case "사용 완료" :
                holder.paymentUsedStatus.setTextColor(mContext.getResources().getColor(R.color.orange05, mContext.getTheme()));
                break;
            case "기간 만료" :
                holder.paymentUsedStatus.setTextColor(mContext.getResources().getColor(R.color.red04, mContext.getTheme()));
                break;
            default :
                holder.paymentUsedStatus.setTextColor(mContext.getResources().getColor(R.color.gray03, mContext.getTheme()));
                break;
        }

        // 가게 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(payment.getStoreImagePath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.storeImage);       // 이미지를 보여줄 View를 지정

        // 가게 이름 SET
        holder.storeName.setText(payment.getStoreName());


        DecimalFormat myFormatter = new DecimalFormat("###,###");   // 문자열 형식 변경 Formatter ( 숫자 + 콤마 )

        // 결제한 금액 SET
        String paymentPriceTxt = "결제 금액 : " + myFormatter.format(payment.getPaymentPrice()) + "원";
        holder.paymentPrice.setText(paymentPriceTxt);

        // 결제한 메뉴 SET
        String paymentMenuTxt;
        if(paymentMenus.size() == 1){   // 결제 메뉴가 1개일 경우
            paymentMenuTxt = paymentMenus.get(0).getMenuName() + " "+ paymentMenus.get(0).getMenuCount() + "개";
        }else{  // 결제 메뉴가 여러개일 경우 외 몇개
            paymentMenuTxt = paymentMenus.get(0).getMenuName() + " "+ paymentMenus.get(0).getMenuCount() + "개 외" + (paymentMenus.size() - 1);
        }
        holder.paymentMenu.setText(paymentMenuTxt);

    }

    // 리사이클러뷰 리스너 SET
    public void setonBagPaymentRvClickListener(onBagPaymentRvClickListener rvClickListener) {
        BagPaymentRvAdapter.rvClickListener = rvClickListener;
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Payments.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomMainCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageButton storeImage; // 가게 썸네일
        TextView storeName;     // 가게 명
        TextView paymentMenu;       // 결제한 메뉴
        TextView paymentPrice;      // 결제 금액
        TextView paymentDate;       // 결제 일자
        TextView expireDate;        // 결제 상품 만료 일자
        TextView paymentUsedStatus; // 결제 상품 사용 가능 상태

        public CustomMainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.bag_payment_store_image);
            storeName = itemView.findViewById(R.id.bag_payment_store_name);
            paymentMenu = itemView.findViewById(R.id.bag_payment_menu);
            paymentPrice = itemView.findViewById(R.id.bag_payment_price);
            paymentDate = itemView.findViewById(R.id.bag_payment_pay_date);
            expireDate = itemView.findViewById(R.id.bag_payment_exp_date);
            paymentUsedStatus = itemView.findViewById(R.id.bag_payment_used_status);

            // 리사이클러뷰 전체 클릭 이벤트 인터페이스 구현
            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onBagPaymentRvClick(view, getAbsoluteAdapterPosition(), "total");
                }
            });

            // 리사이클러뷰 이미지 클릭 이벤트 인터페이스 구현
            storeImage.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onBagPaymentRvClick(view, getAbsoluteAdapterPosition(), "image");
                }
            });
        }
    }
}
