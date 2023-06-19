package com.example.itda.ui.map;

public class mapStoreData {
    private int mapStoreId; // 가게 고유 아이디
    private String mapStoreName;        // 가게 명
    private String mapStoreImagePath;   // 가게 썸네일 경로
    private float mapStoreScore;        // 가게 별점
    private double mapStoreLatitude;    // 가게 위도
    private double mapStoreLongitude;   // 가게 경도
    private float mapStoreDistance;     // 현재 위치에서 가게까지의 거리
    private String mapStoreInfo;        // 가게 간단 정보
    private String mapStoreHashTag;     // 가게 해시태그

    // Constructor
    public mapStoreData(int mapStoreId
            , String mapStoreName
            , String mapStoreImagePath
            , float mapStoreScore
            , double mapStoreLatitude
            , double mapStoreLongitude
            , float mapStoreDistance
            , String mapStoreInfo
            , String mapStoreHashTag) {

        this.mapStoreId = mapStoreId;
        this.mapStoreName = mapStoreName;
        this.mapStoreImagePath = mapStoreImagePath;
        this.mapStoreScore = mapStoreScore;
        this.mapStoreLatitude = mapStoreLatitude;
        this.mapStoreLongitude = mapStoreLongitude;
        this.mapStoreDistance = mapStoreDistance;
        this.mapStoreInfo = mapStoreInfo;
        this.mapStoreHashTag = mapStoreHashTag;
    }

    // Getter & Setter
    public int getMapStoreId() {
        return mapStoreId;
    }

    public void setMapStoreId(int mapStoreId) {
        this.mapStoreId = mapStoreId;
    }

    public String getMapStoreName() {
        return mapStoreName;
    }

    public void setMapStoreName(String mapStoreName) {
        this.mapStoreName = mapStoreName;
    }

    public String getMapStoreImagePath() {
        return mapStoreImagePath;
    }

    public void setMapStoreImagePath(String mapStoreImagePath) {
        this.mapStoreImagePath = mapStoreImagePath;
    }

    public float getMapStoreScore() {
        return mapStoreScore;
    }

    public void setMapStoreScore(float mapStoreScore) {
        this.mapStoreScore = mapStoreScore;
    }

    public double getMapStoreLatitude() {
        return mapStoreLatitude;
    }

    public void setMapStoreLatitude(double mapStoreLatitude) {
        this.mapStoreLatitude = mapStoreLatitude;
    }

    public double getMapStoreLongitude() {
        return mapStoreLongitude;
    }

    public void setMapStoreLongitude(double mapStoreLongitude) {
        this.mapStoreLongitude = mapStoreLongitude;
    }

    public float getMapStoreDistance() {
        return mapStoreDistance;
    }

    public void setMapStoreDistance(float mapStoreDistance) {
        this.mapStoreDistance = mapStoreDistance;
    }

    public String getMapStoreInfo() {
        return mapStoreInfo;
    }

    public void setMapStoreInfo(String mapStoreInfo) {
        this.mapStoreInfo = mapStoreInfo;
    }

    public String getMapStoreHashTag() {
        return mapStoreHashTag;
    }

    public void setMapStoreHashTag(String mapStoreHashTag) {
        this.mapStoreHashTag = mapStoreHashTag;
    }
}
