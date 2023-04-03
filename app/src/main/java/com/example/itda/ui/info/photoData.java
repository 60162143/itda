package com.example.itda.ui.info;

import android.os.Parcel;
import android.os.Parcelable;

public class photoData implements Parcelable {
    private int photoId;            // 리뷰 사진 고유 아이디
    private int userId;             // 리뷰 작성자 고유 아이디
    private int reviewId;           // 리뷰 고유 아이디
    private String userName;        // 리뷰 작성자 명
    private String photoPath;       // 사진 경로
    private String reviewDetail;    // 리뷰 내용
    private int reviewScore;        // 리뷰 점수

    // Constructor
    public photoData(int photoId, int userId, int reviewId, String userName, String photoPath, String reviewDetail, int reviewScore) {
        this.photoId = photoId;
        this.userId = userId;
        this.reviewId = reviewId;
        this.userName = userName;
        this.photoPath = photoPath;
        this.reviewDetail = reviewDetail;
        this.reviewScore = reviewScore;
    }

    // Parcelable interface Constructor
    protected photoData(Parcel in) {
        photoId = in.readInt();
        userId = in.readInt();
        reviewId = in.readInt();
        userName = in.readString();
        photoPath = in.readString();
        reviewDetail = in.readString();
        reviewScore = in.readInt();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<photoData> CREATOR = new Creator<photoData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public photoData createFromParcel(Parcel in) {
            return new photoData(in);
        }

        @Override
        public photoData[] newArray(int size) {
            return new photoData[size];
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
        dest.writeInt(photoId);
        dest.writeInt(userId);
        dest.writeInt(reviewId);
        dest.writeString(userName);
        dest.writeString(photoPath);
        dest.writeString(reviewDetail);
        dest.writeInt(reviewScore);
    }
    //=======================================================================

    // Getter and Setter
    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
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
}
