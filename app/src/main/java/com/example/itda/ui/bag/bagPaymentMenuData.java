package com.example.itda.ui.bag;

import android.os.Parcel;
import android.os.Parcelable;

public class bagPaymentMenuData implements Parcelable {
    private int paymentMenuId;  // 결제 메뉴 테이블 고유 아이디
    private int paymentId;  // 결제 고유 아이디
    private int menuId;     // 메뉴 고유 아이디
    private int menuCount;      // 메뉴 수량
    private String menuName;    // 메뉴 이름
    private int menuPrice;      // 메뉴 가격

    // Constructor
    public bagPaymentMenuData(int paymentMenuId, int paymentId, int menuId, int menuCount, String menuName, int menuPrice) {
        this.paymentMenuId = paymentMenuId;
        this.paymentId = paymentId;
        this.menuId = menuId;
        this.menuCount = menuCount;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }

    // Parcelable interface Constructor
    protected bagPaymentMenuData(Parcel in) {
        paymentMenuId = in.readInt();
        paymentId = in.readInt();
        menuId = in.readInt();
        menuCount = in.readInt();
        menuName = in.readString();
        menuPrice = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<bagPaymentMenuData> CREATOR = new Creator<bagPaymentMenuData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public bagPaymentMenuData createFromParcel(Parcel in) {
            return new bagPaymentMenuData(in);
        }

        @Override
        public bagPaymentMenuData[] newArray(int size) {
            return new bagPaymentMenuData[size];
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
        dest.writeString(menuName);
        dest.writeInt(menuPrice);
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

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(int menuPrice) {
        this.menuPrice = menuPrice;
    }
}
