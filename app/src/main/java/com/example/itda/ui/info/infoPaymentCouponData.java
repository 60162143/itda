package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoPaymentCouponData implements Parcelable {
    private int couponId;         // 쿠폰 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private int userId;    // 유저 고유 아이디
    private int discountRate;      // 쿠폰 할인율
    private String expDate;      // 쿠폰 만료일

    // Constructor
    public infoPaymentCouponData(int couponId, int storeId, int userId, int discountRate, String expDate) {
        this.couponId = couponId;
        this.storeId = storeId;
        this.userId = userId;
        this.discountRate = discountRate;
        this.expDate = expDate;
    }

    // Parcelable interface Constructor
    protected infoPaymentCouponData(Parcel in) {
        couponId = in.readInt();
        storeId = in.readInt();
        userId = in.readInt();
        discountRate = in.readInt();
        expDate = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoPaymentCouponData> CREATOR = new Creator<infoPaymentCouponData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoPaymentCouponData createFromParcel(Parcel in) {
            return new infoPaymentCouponData(in);
        }

        @Override
        public infoPaymentCouponData[] newArray(int size) {
            return new infoPaymentCouponData[size];
        }
    };

    // Parcel 하려는 오브젝트의 종류를 정의
    @Override
    public int describeContents() {
        return 0;
    }

    // 객체를 intent에 담을 때 자동으로 호출
    // Parcel 객체에 객체복원을 위해 필요한 정보 담기, 멤버 변수 모두 저장
    // 각 오브젝트의 각 엘리먼트를 각각 Parcel 해줘야 함
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(couponId);
        dest.writeInt(storeId);
        dest.writeInt(userId);
        dest.writeInt(discountRate);
        dest.writeString(expDate);
    }
    //=======================================================================

    // Getter, Setter

    public int getCouponId() {
        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
