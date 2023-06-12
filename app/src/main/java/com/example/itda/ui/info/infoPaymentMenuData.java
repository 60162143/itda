package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoPaymentMenuData implements Parcelable {
    private int paymentMenuId;         // 결제한 메뉴 테이블 고유 아이디
    private int paymentId;        // 결제 고유 아이디
    private int menuId;    // 결제한 메뉴 고유 아이디
    private int menuCount;         // 결제한 메뉴 수량


    // Constructor
    public infoPaymentMenuData(int paymentMenuId, int paymentId, int menuId, int menuCount) {
        this.paymentMenuId = paymentMenuId;
        this.paymentId = paymentId;
        this.menuId = menuId;
        this.menuCount = menuCount;
    }

    // Parcelable interface Constructor
    protected infoPaymentMenuData(Parcel in) {
        paymentMenuId = in.readInt();
        paymentId = in.readInt();
        menuId = in.readInt();
        menuCount = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoPaymentMenuData> CREATOR = new Creator<infoPaymentMenuData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoPaymentMenuData createFromParcel(Parcel in) {
            return new infoPaymentMenuData(in);
        }

        @Override
        public infoPaymentMenuData[] newArray(int size) {
            return new infoPaymentMenuData[size];
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
        dest.writeInt(paymentMenuId);
        dest.writeInt(paymentId);
        dest.writeInt(menuId);
        dest.writeInt(menuCount);
    }
    //=======================================================================

    // Getter, Setter
    public int getPaymentMenuId() {
        return paymentMenuId;
    }

    public void setPaymentMenuId(int paymentMenuId) {
        this.paymentMenuId = paymentMenuId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getMenuCount() {
        return menuCount;
    }

    public void setMenuCount(int menuCount) {
        this.menuCount = menuCount;
    }
}
