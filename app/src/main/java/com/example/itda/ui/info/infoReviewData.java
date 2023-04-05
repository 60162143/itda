package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoReviewData implements Parcelable {
    private int reviewId;   // 리뷰 고유 아이디
    private int userId;     // 유저 고유 아이디
    private String userName;    // 유저 명
    private int storeId;        // 가게 고유 아이디
    private String userProfilePath; // 유저 프로필 이미지 경로
    private String reviewDetail;    // 리뷰 내용
    private int reviewScore;        // 리뷰 별점
    private int reviewHeartCount;   // 리뷰 좋아요 수
    private String reviewRegDate;   // 리뷰 작성 일자
    private int reviewCommentCount; // 리뷰 댓글 수

    // Constructor
    public infoReviewData(int reviewId
            , int userId
            , String userName
            , int storeId
            , String userProfilePath
            , String reviewDetail
            , int reviewScore
            , int reviewHeartCount
            , String reviewRegDate
            , int reviewCommentCount) {

        this.reviewId = reviewId;
        this.userId = userId;
        this.userName = userName;
        this.storeId = storeId;
        this.userProfilePath = userProfilePath;
        this.reviewDetail = reviewDetail;
        this.reviewScore = reviewScore;
        this.reviewHeartCount = reviewHeartCount;
        this.reviewRegDate = reviewRegDate;
        this.reviewCommentCount = reviewCommentCount;
    }

    // Parcelable interface Constructor
    protected infoReviewData(Parcel in) {
        reviewId = in.readInt();
        userId = in.readInt();
        userName = in.readString();
        storeId = in.readInt();
        userProfilePath = in.readString();
        reviewDetail = in.readString();
        reviewScore = in.readInt();
        reviewHeartCount = in.readInt();
        reviewRegDate = in.readString();
        reviewCommentCount = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoReviewData> CREATOR = new Creator<infoReviewData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoReviewData createFromParcel(Parcel in) {
            return new infoReviewData(in);
        }

        @Override
        public infoReviewData[] newArray(int size) {
            return new infoReviewData[size];
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
        dest.writeInt(reviewId);
        dest.writeInt(userId);
        dest.writeString(userName);
        dest.writeInt(storeId);
        dest.writeString(userProfilePath);
        dest.writeString(reviewDetail);
        dest.writeInt(reviewScore);
        dest.writeInt(reviewHeartCount);
        dest.writeString(reviewRegDate);
        dest.writeInt(reviewCommentCount);
    }
    //=======================================================================


    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getUserProfilePath() {
        return userProfilePath;
    }

    public void setUserProfilePath(String userProfilePath) {
        this.userProfilePath = userProfilePath;
    }

    public String getReviewDetail() {
        return reviewDetail;
    }

    public void setReviewDetail(String reviewDetail) {
        this.reviewDetail = reviewDetail;
    }

    public int getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
    }

    public int getReviewHeartCount() {
        return reviewHeartCount;
    }

    public void setReviewHeartCount(int reviewHeartCount) {
        this.reviewHeartCount = reviewHeartCount;
    }

    public String getReviewRegDate() {
        return reviewRegDate;
    }

    public void setReviewRegDate(String reviewRegDate) {
        this.reviewRegDate = reviewRegDate;
    }

    public int getReviewCommentCount() {
        return reviewCommentCount;
    }

    public void setReviewCommentCount(int reviewCommentCount) {
        this.reviewCommentCount = reviewCommentCount;
    }
}
