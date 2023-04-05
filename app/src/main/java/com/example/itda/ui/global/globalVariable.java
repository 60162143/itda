package com.example.itda.ui.global;

import android.app.Application;

public class globalVariable extends Application {

    // Host 정보
    public String getHost() {
        return "http://no2955922.ivyro.net";
    }

    // 카테고리 데이터 조회 Rest API
    public String getMainCategoryPath() {
        return "/store/getCategory.php";
    }

    // 가게 정보 데이터 조회 Rest API
    public String getMainStorePath() {
        return "/store/getMainStore.php";
    }

    // 지도 내 가게 데이터 조회 Rest API
    public String getMapStorePath() {
        return "/map/getMapStore.php";
    }

    // 협업 가게 정보 데이터 조회 Rest API
    public String getInfoCollaboPath() {
        return "/info/getInfoCollabo.php";
    }

    // 메뉴 정보 데이터 조회 Rest API
    public String getInfoMenuPath() {
        return "/info/getInfoMenu.php";
    }

    // 사진 정보 데이터 조회 Rest API
    public String getInfoPhotoPath() {
        return "/info/getInfoPhoto.php";
    }

    // 리뷰 정보 데이터 조회 Rest API
    public String getInfoReviewPath() {
        return "/info/getInfoReview.php";
    }

    // 리뷰 댓글 정보 데이터 조회 Rest API
    public String getInfoReviewCommentPath() {
        return "/info/getInfoReviewComment.php";
    }

    // 협업 데이터 조회 Rest API
    public String getCollaboPath() {
        return "/collabo/getCollabo.php";
    }
}
