package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoPaymentData implements Parcelable {
    private int paymentId;         // 결제 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private int userId;    // 유저 고유 아이디
    private int paymentPrice;         // 결제 총 금액
    private String usedYN;      // 결제 상품 사용 여부
    private String regDate;      // 결제 일
    private String expDate;      // 결제 상품 사용 만료일

    // Constructor
    public infoPaymentData(int paymentId, int storeId, int userId, int paymentPrice, String usedYN, String regDate, String expDate) {
        this.paymentId = paymentId;
        this.storeId = storeId;
        this.userId = userId;
        this.paymentPrice = paymentPrice;
        this.usedYN = usedYN;
        this.regDate = regDate;
        this.expDate = expDate;
    }

    // Parcelable interface Constructor
    protected infoPaymentData(Parcel in) {
        paymentId = in.readInt();
        storeId = in.readInt();
        userId = in.readInt();
        paymentPrice = in.readInt();
        usedYN = in.readString();
        regDate = in.readString();
        expDate = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoPaymentData> CREATOR = new Creator<infoPaymentData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoPaymentData createFromParcel(Parcel in) {
            return new infoPaymentData(in);
        }

        @Override
        public infoPaymentData[] newArray(int size) {
            return new infoPaymentData[size];
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
        dest.writeString(usedYN);
        dest.writeString(regDate);
        dest.writeString(expDate);
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

    public String getUsedYN() {
        return usedYN;
    }

    public void setUsedYN(String usedYN) {
        this.usedYN = usedYN;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
