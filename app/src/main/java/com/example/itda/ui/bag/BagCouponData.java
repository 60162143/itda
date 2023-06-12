package com.example.itda.ui.bag;

import android.os.Parcel;
import android.os.Parcelable;

public class BagCouponData implements Parcelable {
    private int couponId;         // 쿠폰 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private int userId;    // 유저 고유 아이디
    private int discountRate;      // 쿠폰 할인율
    private String expDate;      // 쿠폰 만료일
    private String storeName;      // 가게 명
    private String storeImagePath;      // 가게 썸네일 이미지

    // Constructor
    public BagCouponData(int couponId, int storeId, int userId, int discountRate, String expDate, String storeName, String storeImagePath) {
        this.couponId = couponId;
        this.storeId = storeId;
        this.userId = userId;
        this.discountRate = discountRate;
        this.expDate = expDate;
        this.storeName = storeName;
        this.storeImagePath = storeImagePath;
    }

    // Parcelable interface Constructor
    protected BagCouponData(Parcel in) {
        couponId = in.readInt();
        storeId = in.readInt();
        userId = in.readInt();
        discountRate = in.readInt();
        expDate = in.readString();
        storeName = in.readString();
        storeImagePath = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<BagCouponData> CREATOR = new Creator<BagCouponData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public BagCouponData createFromParcel(Parcel in) {
            return new BagCouponData(in);
        }

        @Override
        public BagCouponData[] newArray(int size) {
            return new BagCouponData[size];
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
        dest.writeString(storeName);
        dest.writeString(storeImagePath);
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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImagePath() {
        return storeImagePath;
    }

    public void setStoreImagePath(String storeImagePath) {
        this.storeImagePath = storeImagePath;
    }
}
