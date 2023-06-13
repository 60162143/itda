package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class InfoPaymentData implements Parcelable {
    private int paymentId;         // 결제 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private int userId;    // 유저 고유 아이디
    private int paymentPrice;         // 결제 총 금액
    private String paymentDate;      // 결제 일
    private String expireDate;      // 결제 상품 사용 만료일
    private String storeName;      // 결제 가게 명
    private String storeImage;      // 결제 가게 이미지

    // Constructor
    public InfoPaymentData(int paymentId, int storeId, int userId, int paymentPrice, String paymentDate, String expireDate, String storeName, String storeImage) {
        this.paymentId = paymentId;
        this.storeId = storeId;
        this.userId = userId;
        this.paymentPrice = paymentPrice;
        this.paymentDate = paymentDate;
        this.expireDate = expireDate;
        this.storeName = storeName;
        this.storeImage = storeImage;
    }

    // Parcelable interface Constructor
    protected InfoPaymentData(Parcel in) {
        paymentId = in.readInt();
        storeId = in.readInt();
        userId = in.readInt();
        paymentPrice = in.readInt();
        paymentDate = in.readString();
        expireDate = in.readString();
        storeName = in.readString();
        storeImage = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<InfoPaymentData> CREATOR = new Creator<InfoPaymentData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public InfoPaymentData createFromParcel(Parcel in) {
            return new InfoPaymentData(in);
        }

        @Override
        public InfoPaymentData[] newArray(int size) {
            return new InfoPaymentData[size];
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
        dest.writeString(expireDate);
        dest.writeString(storeName);
        dest.writeString(storeImage);
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

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        storeImage = storeImage;
    }
}
