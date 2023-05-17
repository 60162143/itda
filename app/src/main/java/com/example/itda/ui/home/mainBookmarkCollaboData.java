package com.example.itda.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class mainBookmarkCollaboData implements Parcelable {
    private int bmkCollaboId;     // 찜한 협업 목록 고유 아이디
    private int collaboId;  // 협업 고유 아이디

    // Constructor
    public mainBookmarkCollaboData(int bmkCollaboId, int collaboId) {
        this.bmkCollaboId = bmkCollaboId;
        this.collaboId = collaboId;
    }

    // Parcelable interface Constructor
    protected mainBookmarkCollaboData(Parcel in) {
        bmkCollaboId = in.readInt();
        collaboId = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<mainBookmarkCollaboData> CREATOR = new Creator<mainBookmarkCollaboData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public mainBookmarkCollaboData createFromParcel(Parcel in) {
            return new mainBookmarkCollaboData(in);
        }

        @Override
        public mainBookmarkCollaboData[] newArray(int size) {
            return new mainBookmarkCollaboData[size];
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
        dest.writeInt(bmkCollaboId);
        dest.writeInt(collaboId);
    }
    //=======================================================================

    // Getter, Setter
    public int getBmkCollaboId() {
        return bmkCollaboId;
    }

    public void setBmkCollaboId(int bmkCollaboId) {
        this.bmkCollaboId = bmkCollaboId;
    }

    public int getCollaboId() {
        return collaboId;
    }

    public void setCollaboId(int collaboId) {
        this.collaboId = collaboId;
    }
}
