package com.example.itda.ui.mypage;

public class MyPageBookmarkStoreData {
    private int storeId;             // 가게 고유 아이디
    private String storeName;        // 가게 명
    private String storeImagePath;  // 가게 썸네일 경로
    private float storeScore;        // 가게 별점
    private double storeLatitude;    // 가게 위도
    private double storeLongitude;   // 가게 경도
    private float storeDistance;     // 현재 위치에서 가게까지의 거리
    private String storeInfo;        // 가게 간단 정보
    private String storeHashTag;     // 가게 해시태그
    private int bookmarkStoreId;     // 찜한 가게 목록 고유 아이디

    // Constructor
    public MyPageBookmarkStoreData(int storeId
            , String storeName
            , String storeImagePath
            , float storeScore
            , double storeLatitude
            , double storeLongitude
            , float storeDistance
            , String storeInfo
            , String storeHashTag
            , int bookmarkStoreId) {

        this.storeId = storeId;
        this.storeName = storeName;
        this.storeImagePath = storeImagePath;
        this.storeScore = storeScore;
        this.storeLatitude = storeLatitude;
        this.storeLongitude = storeLongitude;
        this.storeDistance = storeDistance;
        this.storeInfo = storeInfo;
        this.storeHashTag = storeHashTag;
        this.bookmarkStoreId = bookmarkStoreId;
    }

    // Getter & Setter
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
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

    public float getStoreScore() {
        return storeScore;
    }

    public void setStoreScore(float storeScore) {
        this.storeScore = storeScore;
    }

    public double getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(double storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public double getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(double storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public float getStoreDistance() {
        return storeDistance;
    }

    public void setStoreDistance(float storeDistance) {
        this.storeDistance = storeDistance;
    }

    public String getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(String storeInfo) {
        this.storeInfo = storeInfo;
    }

    public String getStoreHashTag() {
        return storeHashTag;
    }

    public void setStoreHashTag(String storeHashTag) {
        this.storeHashTag = storeHashTag;
    }

    public int getBookmarkStoreId() {
        return bookmarkStoreId;
    }

    public void setBookmarkStoreId(int bookmarkStoreId) {
        this.bookmarkStoreId = bookmarkStoreId;
    }
}
