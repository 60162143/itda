package com.example.itda.ui.info;

public class infoCollaboData {
    private int StoreId;                    // 가게 고유 아이디
    private String StoreName;               // 가게 명
    private String StoreAddress;            // 가게 주소
    private String StoreDetail;             // 가게 간단 제공 서비스
    private String StoreFacility;           // 가게 제공 시설 여부
    private double StoreLatitude;           // 가게 위도
    private double StoreLongitude;          // 가게 경도
    private String StoreNumber;             // 가게 번호
    private String StoreInfo;               // 가게 상세 정보
    private int StoreCategoryId;            // 가게의 카테고리 고유 아이디
    private String StoreThumbnailPath;      // 가게 썸네일 이미지 경로
    private double StoreScore;              // 가게 별점
    private String StoreWorkingTime;        // 가게 운영 시간
    private int CollaboId;                  // 협업 고유 아이디
    private int CollaboStoreId;             // 협업 뒷 가게 고유 아이디
    private int CollaboDiscountCondition;    // 앞 가게 할인 조건 ( 최소 금액 )
    private int CollaboDiscountRate;        // 뒷 가게 할인율 ( 정수 )

    // Constructor
    public infoCollaboData(int storeId
            , String storeName
            , String storeAddress
            , String storeDetail
            , String storeFacility
            , double storeLatitude
            , double storeLongitude
            , String storeNumber
            , String storeInfo
            , int storeCategoryId
            , String storeThumbnailPath
            , double storeScore
            , String storeWorkingTime
            , int collaboId
            , int collaboStoreId
            , int collaboDiscountCondition
            , int collaboDiscountRate) {

        StoreId = storeId;
        StoreName = storeName;
        StoreAddress = storeAddress;
        StoreDetail = storeDetail;
        StoreFacility = storeFacility;
        StoreLatitude = storeLatitude;
        StoreLongitude = storeLongitude;
        StoreNumber = storeNumber;
        StoreInfo = storeInfo;
        StoreCategoryId = storeCategoryId;
        StoreThumbnailPath = storeThumbnailPath;
        StoreScore = storeScore;
        StoreWorkingTime = storeWorkingTime;
        CollaboId = collaboId;
        CollaboStoreId = collaboStoreId;
        CollaboDiscountCondition = collaboDiscountCondition;
        CollaboDiscountRate = collaboDiscountRate;
    }

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

    public String getStoreDetail() {
        return StoreDetail;
    }

    public void setStoreDetail(String storeDetail) {
        StoreDetail = storeDetail;
    }

    public String getStoreFacility() {
        return StoreFacility;
    }

    public void setStoreFacility(String storeFacility) {
        StoreFacility = storeFacility;
    }

    public double getStoreLatitude() {
        return StoreLatitude;
    }

    public void setStoreLatitude(double storeLatitude) {
        StoreLatitude = storeLatitude;
    }

    public double getStoreLongitude() {
        return StoreLongitude;
    }

    public void setStoreLongitude(double storeLongitude) {
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

    public void setStoreScore(double storeScore) {
        StoreScore = storeScore;
    }

    public String getStoreWorkingTime() {
        return StoreWorkingTime;
    }

    public void setStoreWorkingTime(String storeWorkingTime) {
        StoreWorkingTime = storeWorkingTime;
    }

    public int getCollaboId() {
        return CollaboId;
    }

    public void setCollaboId(int collaboId) {
        CollaboId = collaboId;
    }

    public int getCollaboDiscountCondition() {
        return CollaboDiscountCondition;
    }

    public void setCollaboDiscountCondition(int collaboDiscountCondition) {
        CollaboDiscountCondition = collaboDiscountCondition;
    }

    public int getCollaboDiscountRate() {
        return CollaboDiscountRate;
    }

    public void setCollaboDiscountRate(int collaboDiscountRate) {
        CollaboDiscountRate = collaboDiscountRate;
    }

    public int getCollaboStoreId() {
        return CollaboStoreId;
    }

    public void setCollaboStoreId(int collaboStoreId) {
        CollaboStoreId = collaboStoreId;
    }
}
