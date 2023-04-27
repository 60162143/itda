package com.example.itda.ui.global;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.itda.R;

public class globalMethod extends Application {
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

    // 로그인 체크
    public boolean loginChecked() {
        SharedPreferences auto = this.getSharedPreferences("user", Activity.MODE_PRIVATE);

        int userId = auto.getInt("userId", 0);  // 유저 아이디

        return userId != 0; // 유저 아이디가 있으면 true, 없으면 false Return
    }

    // FTP 호스트 정보
    public String getFTPHost() {
        return "------";
    }

    // FTP 호스트 아이디
    public String getFTPUserId() {
        return "------";
    }

    // FTP 호스트 비밀번호
    public String getFTPUserPassword() {
        return "------";
    }
}
