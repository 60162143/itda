package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class InfoPaymentCouponSpinnerAdapter extends BaseAdapter {
    private final ArrayList<infoPaymentCouponData> Coupons;    // 쿠폰 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoPaymentCouponSpinnerAdapter(Context context
            , ArrayList<infoPaymentCouponData> coupons){
        this.mContext = context;
        this.Coupons = coupons;
    }

    @Override
    public int getCount() {
        return Coupons != null ? Coupons.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return Coupons.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Coupons.get(i).getCouponId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View rootView = view;

        if(view == null){
            rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sp_info_payment_coupon, viewGroup, false);
        }

        TextView couponTxt = rootView.findViewById(R.id.sp_info_payment_coupon_content);

        if(i == 0){
            couponTxt.setText("쿠폰 없음");
        }else{
            couponTxt.setText(Coupons.get(i).getDiscountRate() + "% 할인 쿠폰( ~ " + Coupons.get(i).getExpDate() + " )");
        }

        return rootView;
    }
}
