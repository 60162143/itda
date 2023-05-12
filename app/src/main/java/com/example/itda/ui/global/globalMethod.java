package com.example.itda.ui.global;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.itda.R;

public class globalMethod extends Application {

    /******************************* Data ***********************************/
    // Host 정보
    public String getHost() {
        return "http://no2955922.ivyro.net";
    }

    // 기본 프로필 이미지 경로
    public String getNoProfilePath() {
        return "/ftpFileStorage/noUser.png";
    }

    // 기본 가게 이미지 경로
    public String getNoStoreImagePath() {
        return "/ftpFileStorage/noImage.png";
    }

    /********************************* API ***********************************/
    // 카테고리 데이터 조회 Rest API
    public String getMainCategoryPath() {
        return "/store/getCategory.php";
    }

    // 가게 정보 데이터 조회 Rest API
    public String getMainStorePath() {
        return "/store/getMainStore.php";
    }

    // 유저 찜한 가게 목록 ( 간단 정보 ) 조회 Rest API
    public String getMainBookmarkStorePath() {
        return "/store/getMainBookmarkStore.php";
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

    // 로그인 데이터 조회 Rest API
    public String getLoginPath() {
        return "/login/getLoginUser.php";
    }

    // 마이페이지 유저 이름 변경 Rest API
    public String getUpdateUserNamePath() {
        return "/mypage/updateUserName.php";
    }

    // 마이페이지 유저 번호 변경 Rest API
    public String getUpdateUserNumberPath() {
        return "/mypage/updateUserNumber.php";
    }

    // 마이페이지 유저 생일 변경 Rest API
    public String getUpdateUserBirthdayPath() {
        return "/mypage/updateUserBirthday.php";
    }

    // 유저 프로필 변경 Rest API
    public String getUpdateUserProfilePath() {
        return "/mypage/updateUserProfile.php";
    }

    // 유저 프로필 기본 이미지 변경 Rest API
    public String getDeleteUserProfilePath() {
        return "/mypage/deleteUserProfile.php";
    }

    // 유저 비밀번호 변경 Rest API
    public String getUpdateUserPasswordPath() {
        return "/mypage/updateUserPassword.php";
    }

    // 유저 찜한 가게 목록 조회 Rest API
    public String getBookmarkStorePath() {
        return "/mypage/getBookmarkStore.php";
    }

    // 유저 찜한 가게 목록 삭제 Rest API
    public String deleteBookmarkStorePath() {
        return "/mypage/deleteBookmarkStore.php";
    }

    // 유저 찜한 가게 목록 추가 Rest API
    public String insertBookmarkStorePath() {
        return "/mypage/insertBookmarkStore.php";
    }

    // 유저 찜한 협업 목록 조회 Rest API
    public String getBookmarkCollaboPath() {
        return "/mypage/getBookmarkCollabo.php";
    }

    // 유저 찜한 협업 목록 삭제 Rest API
    public String deleteBookmarkCollaboPath() {
        return "/mypage/deleteBookmarkCollabo.php";
    }

    // 유저 작성한 리뷰 삭제 Rest API
    public String deleteReviewPath() {
        return "/mypage/deleteReview.php";
    }

    // 유저 업로드한 사진 삭제 Rest API
    public String deletePhotoPath() {
        return "/mypage/deletePhoto.php";
    }

    /*********************************** Method *************************************/
    // 로그인 체크
    public boolean loginChecked() {
        SharedPreferences auto = this.getSharedPreferences("user", Activity.MODE_PRIVATE);

        int userId = auto.getInt("userId", 0);  // 유저 아이디

        return userId != 0; // 유저 아이디가 있으면 true, 없으면 false Return
    }
}
