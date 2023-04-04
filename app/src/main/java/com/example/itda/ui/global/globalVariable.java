package com.example.itda.ui.global;

import android.app.Application;

public class globalVariable extends Application {
    final private String host = "http://no2955922.ivyro.net";        // Host 정보
    final private String categoryPath = "/store/getCategory.php";   // 카테고리 데이터 조회 Rest API
    final private String mainStorePath = "/store/getMainStore.php"; // 가게 정보 데이터 조회 Rest API
    final private String mapStorePath = "/store/getMapStore.php";   // 지도 내 가게 데이터 조회 Rest API
    final private String collaboPath = "/info/getInfoCollabo.php";  // 협업 가게 정보 데이터 조회 Rest API
    final private String menuPath = "/info/getInfoMenu.php";        // 메뉴 정보 데이터 조회 Rest API
    final private String photoPath = "/info/getInfoPhoto.php";      // 사진 정보 데이터 조회 Rest API
    final private String reviewPath = "/info/getInfoReview.php";    // 리뷰 정보 데이터 조회 Rest API
    final private String reviewCommentPath = "/info/getInfoReviewComment.php";    // 리뷰 댓글 정보 데이터 조회 Rest API

    public String getHost() {
        return host;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public String getMainStorePath() {
        return mainStorePath;
    }

    public String getMapStorePath() {
        return mapStorePath;
    }

    public String getCollaboPath() {
        return collaboPath;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getReviewPath() {
        return reviewPath;
    }

    public String getReviewCommentPath() {
        return reviewCommentPath;
    }
}
