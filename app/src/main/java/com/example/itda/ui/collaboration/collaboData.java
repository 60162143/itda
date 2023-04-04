package com.example.itda.ui.collaboration;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.itda.ui.home.mainStoreData;

public class collaboData implements Parcelable {
    private int collaboId;
    private int prvStoreId;
    private int postStoreId;
    private int prvDiscountCondition;
    private int postDiscountRate;
    private String prvStoreName;
    private String postStoreName;
    private String prvStoreImagePath;
    private String postStoreImagePath;
    private float collaboDistance;

    public collaboData(int collaboId
            , int prvStoreId
            , int postStoreId
            , int prvDiscountCondition
            , int postDiscountRate
            , String prvStoreName
            , String postStoreName
            , String prvStoreImagePath
            , String postStoreImagePath
            , float collaboDistance) {
        this.collaboId = collaboId;
        this.prvStoreId = prvStoreId;
        this.postStoreId = postStoreId;
        this.prvDiscountCondition = prvDiscountCondition;
        this.postDiscountRate = postDiscountRate;
        this.prvStoreName = prvStoreName;
        this.postStoreName = postStoreName;
        this.prvStoreImagePath = prvStoreImagePath;
        this.postStoreImagePath = postStoreImagePath;
        this.collaboDistance = collaboDistance;
    }

    // Parcelable interface Constructor
    protected collaboData(Parcel in) {
        collaboId = in.readInt();
        prvStoreId = in.readInt();
        postStoreId = in.readInt();
        prvDiscountCondition = in.readInt();
        postDiscountRate = in.readInt();
        prvStoreName = in.readString();
        postStoreName = in.readString();
        prvStoreImagePath = in.readString();
        postStoreImagePath = in.readString();
        collaboDistance = in.readFloat();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<collaboData> CREATOR = new Creator<collaboData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public collaboData createFromParcel(Parcel in) {
            return new collaboData(in);
        }

        @Override
        public collaboData[] newArray(int size) {
            return new collaboData[size];
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
        dest.writeInt(collaboId);
        dest.writeInt(prvStoreId);
        dest.writeInt(postStoreId);
        dest.writeInt(prvDiscountCondition);
        dest.writeInt(postDiscountRate);
        dest.writeString(prvStoreName);
        dest.writeString(postStoreName);
        dest.writeString(prvStoreImagePath);
        dest.writeString(postStoreImagePath);
        dest.writeFloat(collaboDistance);
    }
    //=======================================================================

    public int getCollaboId() {
        return collaboId;
    }

    public void setCollaboId(int collaboId) {
        this.collaboId = collaboId;
    }

    public int getPrvStoreId() {
        return prvStoreId;
    }

    public void setPrvStoreId(int prvStoreId) {
        this.prvStoreId = prvStoreId;
    }

    public int getPostStoreId() {
        return postStoreId;
    }

    public void setPostStoreId(int postStoreId) {
        this.postStoreId = postStoreId;
    }

    public int getPrvDiscountCondition() {
        return prvDiscountCondition;
    }

    public void setPrvDiscountCondition(int prvDiscountCondition) {
        this.prvDiscountCondition = prvDiscountCondition;
    }

    public int getPostDiscountRate() {
        return postDiscountRate;
    }

    public void setPostDiscountRate(int postDiscountRate) {
        this.postDiscountRate = postDiscountRate;
    }

    public String getPrvStoreName() {
        return prvStoreName;
    }

    public void setPrvStoreName(String prvStoreName) {
        this.prvStoreName = prvStoreName;
    }

    public String getPostStoreName() {
        return postStoreName;
    }

    public void setPostStoreName(String postStoreName) {
        this.postStoreName = postStoreName;
    }

    public String getPrvStoreImagePath() {
        return prvStoreImagePath;
    }

    public void setPrvStoreImagePath(String prvStoreImagePath) {
        this.prvStoreImagePath = prvStoreImagePath;
    }

    public String getPostStoreImagePath() {
        return postStoreImagePath;
    }

    public void setPostStoreImagePath(String postStoreImagePath) {
        this.postStoreImagePath = postStoreImagePath;
    }

    public float getCollaboDistance() {
        return collaboDistance;
    }

    public void setCollaboDistance(float collaboDistance) {
        this.collaboDistance = collaboDistance;
    }
}
