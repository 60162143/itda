package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoMenuData implements Parcelable {
    private int menuId;         // 메뉴 고유 아이디
    private int storeId;        // 가게 고유 아이디
    private String menuName;    // 메뉴 이름
    private int menuPrice;      // 메뉴 가격
    private int menuOrder;      // 메뉴 정렬 순서

    // Constructor
    public infoMenuData(int menuId, int storeId, String menuName, int menuPrice, int menuOrder) {
        this.menuId = menuId;
        this.storeId = storeId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuOrder = menuOrder;
    }

    // Parcelable interface Constructor
    protected infoMenuData(Parcel in) {
        menuId = in.readInt();
        storeId = in.readInt();
        menuName = in.readString();
        menuPrice = in.readInt();
        menuOrder = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoMenuData> CREATOR = new Creator<infoMenuData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoMenuData createFromParcel(Parcel in) {
            return new infoMenuData(in);
        }

        @Override
        public infoMenuData[] newArray(int size) {
            return new infoMenuData[size];
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
        dest.writeInt(menuId);
        dest.writeInt(storeId);
        dest.writeString(menuName);
        dest.writeInt(menuPrice);
        dest.writeInt(menuOrder);
    }
    //=======================================================================

    // Getter, Setter
    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
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

    public int getMenuOrder() {
        return menuOrder;
    }

    public void setMenuOrder(int menuOrder) {
        this.menuOrder = menuOrder;
    }
}
