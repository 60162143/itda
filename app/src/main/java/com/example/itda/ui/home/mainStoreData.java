package com.example.itda.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

public class mainStoreData implements Parcelable {
    private int StoreId;            // 가게 고유 아이디
    private String StoreName;       // 가게 명
    private String StoreAddress;    // 가게 주소
    private String StoreParking;    // 가게 주차 가능 여부
    private double StoreLatitude;   // 가게 위도
    private double StoreLongitude;  // 가게 경도
    private String StoreNumber;     // 가게 번호
    private String StoreInfo;       // 가게 상세 정보
    private int StoreCategoryId;    // 가게의 카테고리 고유 아이디
    private String StoreThumbnailPath;  // 가게 썸네일 이미지 경로
    private double StoreScore;      // 가게 별점
    private String StoreWorkingTime;    // 가게 운영 시간

    // Constructor
    public mainStoreData(int storeId, String storeName, String storeAddress, String storeParking, double storeLatitude, double storeLongitude, String storeNumber, String storeInfo, int storeCategoryId, String storeThumbnailPath, double storeScore, String storeWorkingTime) {
        StoreId = storeId;
        StoreName = storeName;
        StoreAddress = storeAddress;
        StoreParking = storeParking;
        StoreLatitude = storeLatitude;
        StoreLongitude = storeLongitude;
        StoreNumber = storeNumber;
        StoreInfo = storeInfo;
        StoreCategoryId = storeCategoryId;
        StoreThumbnailPath = storeThumbnailPath;
        StoreScore = storeScore;
        StoreWorkingTime = storeWorkingTime;
    }

    // Parcelable interface Constructor
    protected mainStoreData(Parcel in) {
        StoreId = in.readInt();
        StoreName = in.readString();
        StoreAddress = in.readString();
        StoreParking = in.readString();
        StoreLatitude = in.readDouble();
        StoreLongitude = in.readDouble();
        StoreNumber = in.readString();
        StoreInfo = in.readString();
        StoreCategoryId = in.readInt();
        StoreThumbnailPath = in.readString();
        StoreScore = in.readDouble();
        StoreWorkingTime = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<mainStoreData> CREATOR = new Creator<mainStoreData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public mainStoreData createFromParcel(Parcel in) {
            return new mainStoreData(in);
        }

        @Override
        public mainStoreData[] newArray(int size) {
            return new mainStoreData[size];
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
        dest.writeInt(StoreId);
        dest.writeString(StoreName);
        dest.writeString(StoreAddress);
        dest.writeString(StoreParking);
        dest.writeDouble(StoreLatitude);
        dest.writeDouble(StoreLongitude);
        dest.writeString(StoreNumber);
        dest.writeString(StoreInfo);
        dest.writeInt(StoreCategoryId);
        dest.writeString(StoreThumbnailPath);
        dest.writeDouble(StoreScore);
        dest.writeString(StoreWorkingTime);

    }
    //=======================================================================

    // Getter and Setter
    public int getStoreId() {
        return StoreId;
    }

    public void setStoreId(int storeId) {
        StoreId = storeId;
    }

    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public String getStoreAddress() {
        return StoreAddress;
    }

    public void setStoreAddress(String storeAddress) {
        StoreAddress = storeAddress;
    }

    public String getStoreParking() {
        return StoreParking;
    }

    public void setStoreParking(String storeParking) {
        StoreParking = storeParking;
    }

    public double getStoreLatitude() {
        return StoreLatitude;
    }

    public void setStoreLatitude(Double storeLatitude) {
        StoreLatitude = storeLatitude;
    }

    public double getStoreLongitude() {
        return StoreLongitude;
    }

    public void setStoreLongitude(Double storeLongitude) {
        StoreLongitude = storeLongitude;
    }

    public String getStoreNumber() {
        return StoreNumber;
    }

    public void setStoreNumber(String storeNumber) {
        StoreNumber = storeNumber;
    }

    public String getStoreInfo() {
        return StoreInfo;
    }

    public void setStoreInfo(String storeInfo) {
        StoreInfo = storeInfo;
    }

    public int getStoreCategoryId() {
        return StoreCategoryId;
    }

    public void setStoreCategoryId(int storeCategoryId) {
        StoreCategoryId = storeCategoryId;
    }

    public String getStoreThumbnailPath() {
        return StoreThumbnailPath;
    }

    public void setStoreThumbnailPath(String storeThumbnailPath) {
        StoreThumbnailPath = storeThumbnailPath;
    }

    public double getStoreScore() {
        return StoreScore;
    }

    public void setStoreScore(Double storeScore) {
        StoreScore = storeScore;
    }

    public String getStoreWorkingTime() {
        return StoreWorkingTime;
    }

    public void setStoreWorkingTime(String storeWorkingTime) {
        StoreWorkingTime = storeWorkingTime;
    }
}
