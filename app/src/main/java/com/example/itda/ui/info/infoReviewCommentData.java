package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class infoReviewCommentData implements Parcelable {
    private int reviewCommentId;            // 리뷰 댓글 고유 아이디
    private int reviewId;                   // 리뷰 고유 아이디
    private int userId;                     // 유저 고유 아이디
    private int storeId;                    // 가게 고유 아이디
    private String reviewCommentContent;    // 리뷰 댓글 내용
    private String reviewCommentRegDate;           // 리뷰 댓글 작성 일자
    private String userName;                // 유저 명
    private String userProfilePath;         // 유저 프로필 이미지

    public infoReviewCommentData(int reviewCommentId
            , int reviewId
            , int userId
            , int storeId
            , String reviewCommentContent
            , String reviewCommentRegDate
            , String userName
            , String userProfilePath) {
        this.reviewCommentId = reviewCommentId;
        this.reviewId = reviewId;
        this.userId = userId;
        this.storeId = storeId;
        this.reviewCommentContent = reviewCommentContent;
        this.reviewCommentRegDate = reviewCommentRegDate;
        this.userName = userName;
        this.userProfilePath = userProfilePath;
    }

    // Parcelable interface Constructor
    protected infoReviewCommentData(Parcel in) {
        reviewCommentId = in.readInt();
        reviewId = in.readInt();
        userId = in.readInt();
        storeId = in.readInt();
        reviewCommentContent = in.readString();
        reviewCommentRegDate = in.readString();
        userName = in.readString();
        userProfilePath = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<infoReviewCommentData> CREATOR = new Creator<infoReviewCommentData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public infoReviewCommentData createFromParcel(Parcel in) {
            return new infoReviewCommentData(in);
        }

        @Override
        public infoReviewCommentData[] newArray(int size) {
            return new infoReviewCommentData[size];
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
        dest.writeInt(reviewCommentId);
        dest.writeInt(reviewId);
        dest.writeInt(userId);
        dest.writeInt(storeId);
        dest.writeString(reviewCommentContent);
        dest.writeString(reviewCommentRegDate);
        dest.writeString(userName);
        dest.writeString(userProfilePath);
    }
    //=======================================================================

    public int getReviewCommentId() {
        return reviewCommentId;
    }

    public void setReviewCommentId(int reviewCommentId) {
        this.reviewCommentId = reviewCommentId;
    }

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

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getReviewCommentContent() {
        return reviewCommentContent;
    }

    public void setReviewCommentContent(String reviewCommentContent) {
        this.reviewCommentContent = reviewCommentContent;
    }

    public String getReviewCommentRegDate() {
        return reviewCommentRegDate;
    }

    public void setReviewCommentRegDate(String reviewCommentRegDate) {
        this.reviewCommentRegDate = reviewCommentRegDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePath() {
        return userProfilePath;
    }

    public void setUserProfilePath(String userProfilePath) {
        this.userProfilePath = userProfilePath;
    }
}
