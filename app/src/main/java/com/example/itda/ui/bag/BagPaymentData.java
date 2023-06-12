package com.example.itda.ui.bag;

import android.os.Parcel;
import android.os.Parcelable;

public class BagPaymentData implements Parcelable {
    private int paymentId;         // 결제 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private int userId;    // 유저 고유 아이디
    private int paymentPrice;      // 결제 금액
    private String paymentDate;      // 결제일
    private String storeName;      // 가게 명
    private String storeImagePath;      // 가게 썸네일 이미지

    // Constructor
    public BagPaymentData(int paymentId, int storeId, int userId, int paymentPrice, String paymentDate, String storeName, String storeImagePath) {
        this.paymentId = paymentId;
        this.storeId = storeId;
        this.userId = userId;
        this.paymentPrice = paymentPrice;
        this.paymentDate = paymentDate;
        this.storeName = storeName;
        this.storeImagePath = storeImagePath;
    }

    // Parcelable interface Constructor
    protected BagPaymentData(Parcel in) {
        paymentId = in.readInt();
        storeId = in.readInt();
        userId = in.readInt();
        paymentPrice = in.readInt();
        paymentDate = in.readString();
        storeName = in.readString();
        storeImagePath = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<BagPaymentData> CREATOR = new Creator<BagPaymentData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public BagPaymentData createFromParcel(Parcel in) {
            return new BagPaymentData(in);
        }

        @Override
        public BagPaymentData[] newArray(int size) {
            return new BagPaymentData[size];
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
        dest.writeInt(paymentId);
        dest.writeInt(storeId);
        dest.writeInt(userId);
        dest.writeInt(paymentPrice);
        dest.writeString(paymentDate);
        dest.writeString(storeName);
        dest.writeString(storeImagePath);
    }
    //=======================================================================

    // Getter, Setter
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
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

    public int getPaymentPrice() {
        return paymentPrice;
    }

    public void setPaymentPrice(int paymentPrice) {
        this.paymentPrice = paymentPrice;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
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
