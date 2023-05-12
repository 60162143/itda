package com.example.itda.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class mainBookmarkStoreData implements Parcelable {
    private int bmkStoreId;     // 찜한 가게 목록 고유 아이디
    private int storeId;  // 가게 고유 아이디

    // Constructor
    public mainBookmarkStoreData(int bmkStoreId, int storeId) {
        this.bmkStoreId = bmkStoreId;
        this.storeId = storeId;
    }

    // Parcelable interface Constructor
    protected mainBookmarkStoreData(Parcel in) {
        bmkStoreId = in.readInt();
        storeId = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<mainBookmarkStoreData> CREATOR = new Creator<mainBookmarkStoreData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public mainBookmarkStoreData createFromParcel(Parcel in) {
            return new mainBookmarkStoreData(in);
        }

        @Override
        public mainBookmarkStoreData[] newArray(int size) {
            return new mainBookmarkStoreData[size];
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
        dest.writeInt(bmkStoreId);
        dest.writeInt(storeId);
    }
    //=======================================================================

    // Getter, Setter
    public int getBmkStoreId() {
        return bmkStoreId;
    }

    public void setBmkStoreId(int bmkStoreId) {
        this.bmkStoreId = bmkStoreId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
