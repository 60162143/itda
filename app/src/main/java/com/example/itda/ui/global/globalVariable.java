package com.example.itda.ui.global;

import android.app.Application;

public class globalVariable extends Application {
    final private String host = "http://no2955922.ivyro.net";        // Host 정보
    final private String mainCategoryPath = "/store/getCategory.php";   // 카테고리 데이터 조회 Rest API
    final private String mainStorePath = "/store/getMainStore.php"; // 가게 정보 데이터 조회 Rest API
    final private String mapStorePath = "/map/getMapStore.php";   // 지도 내 가게 데이터 조회 Rest API
    final private String infoCollaboPath = "/info/getInfoCollabo.php";  // 협업 가게 정보 데이터 조회 Rest API
    final private String infoMenuPath = "/info/getInfoMenu.php";        // 메뉴 정보 데이터 조회 Rest API
    final private String infoPhotoPath = "/info/getInfoPhoto.php";      // 사진 정보 데이터 조회 Rest API
    final private String infoReviewPath = "/info/getInfoReview.php";    // 리뷰 정보 데이터 조회 Rest API
    final private String infoReviewCommentPath = "/info/getInfoReviewComment.php";    // 리뷰 댓글 정보 데이터 조회 Rest API
    final private String collaboPath = "/collabo/getCollabo.php";    // 협업 데이터 조회 Rest API

    public String getHost() {
        return host;
    }

    public String getMainCategoryPath() {
        return mainCategoryPath;
    }

    public String getMainStorePath() {
        return mainStorePath;
    }

    public String getMapStorePath() {
        return mapStorePath;
    }

    public String getInfoCollaboPath() {
        return infoCollaboPath;
    }

    public String getInfoMenuPath() {
        return infoMenuPath;
    }

    public String getInfoPhotoPath() {
        return infoPhotoPath;
    }

    public String getInfoReviewPath() {
        return infoReviewPath;
    }

    public String getInfoReviewCommentPath() {
        return infoReviewCommentPath;
    }

    public String getCollaboPath() {
        return collaboPath;
    }
}
