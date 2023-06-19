package com.example.itda.ui.mypage;

public class myPageBookmarkCollaboData {
    private int bookmarkCollaboId;  // 찜한 협업 목록 고유 아이디
    private int prvStoreId; // 앞 가게 고유 아이디
    private String prvStoreName;        // 앞 가게 명
    private String prvStoreImagePath;   // 앞 가게 이미지 경로
    private int prvDiscountCondition;   // 앞 가게 할인 조건
    private int postStoreId;        // 뒷 가게 고유 아이디
    private String postStoreName;   // 뒷 가게 명
    private String postStoreImagePath;  // 뒷 가게 이미지 경로
    private int postDiscountRate;       // 뒷 가게 할인 율
    private float distance; // 가게 사이의 거리

    // Constructor
    public myPageBookmarkCollaboData(int bookmarkCollaboId
            , int prvStoreId
            , String prvStoreName
            , String prvStoreImagePath
            , int prvDiscountCondition
            , int postStoreId
            , String postStoreName
            , String postStoreImagePath
            , int postDiscountRate
            , float distance) {
        this.bookmarkCollaboId = bookmarkCollaboId;
        this.prvStoreId = prvStoreId;
        this.prvStoreName = prvStoreName;
        this.prvStoreImagePath = prvStoreImagePath;
        this.prvDiscountCondition = prvDiscountCondition;
        this.postStoreId = postStoreId;
        this.postStoreName = postStoreName;
        this.postStoreImagePath = postStoreImagePath;
        this.postDiscountRate = postDiscountRate;
        this.distance = distance;
    }

    // Getter and Setter

    public int getBookmarkCollaboId() {
        return bookmarkCollaboId;
    }

    public void setBookmarkCollaboId(int bookmarkCollaboId) {
        this.bookmarkCollaboId = bookmarkCollaboId;
    }

    public int getPrvStoreId() {
        return prvStoreId;
    }

    public void setPrvStoreId(int prvStoreId) {
        this.prvStoreId = prvStoreId;
    }

    public String getPrvStoreName() {
        return prvStoreName;
    }

    public void setPrvStoreName(String prvStoreName) {
        this.prvStoreName = prvStoreName;
    }

    public String getPrvStoreImagePath() {
        return prvStoreImagePath;
    }

    public void setPrvStoreImagePath(String prvStoreImagePath) {
        this.prvStoreImagePath = prvStoreImagePath;
    }

    public int getPrvDiscountCondition() {
        return prvDiscountCondition;
    }

    public void setPrvDiscountCondition(int prvDiscountCondition) {
        this.prvDiscountCondition = prvDiscountCondition;
    }

    public int getPostStoreId() {
        return postStoreId;
    }

    public void setPostStoreId(int postStoreId) {
        this.postStoreId = postStoreId;
    }

    public String getPostStoreName() {
        return postStoreName;
    }

    public void setPostStoreName(String postStoreName) {
        this.postStoreName = postStoreName;
    }

    public String getPostStoreImagePath() {
        return postStoreImagePath;
    }

    public void setPostStoreImagePath(String postStoreImagePath) {
        this.postStoreImagePath = postStoreImagePath;
    }

    public int getPostDiscountRate() {
        return postDiscountRate;
    }

    public void setPostDiscountRate(int postDiscountRate) {
        this.postDiscountRate = postDiscountRate;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
