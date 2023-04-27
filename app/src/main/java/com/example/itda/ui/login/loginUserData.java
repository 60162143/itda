package com.example.itda.ui.login;

import android.os.Parcel;
import android.os.Parcelable;

public class loginUserData implements Parcelable {
    private int userId; // 유저 고유 아이디
    private String userEmail;   // 유저 이메일
    private String userPassword;    // 유저 비밀번호
    private String userProfileImage;    // 유저 프로필 이미지
    private String userNumber;  // 유저 번호
    private String userName;    // 유저 명
    private String userBirthday;    // 유저 생일

    // Constructor
    public loginUserData(int userId
            , String userEmail
            , String userPassword
            , String userProfileImage
            , String userNumber
            , String userName
            , String userBirthday) {

        this.userId = userId;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userProfileImage = userProfileImage;
        this.userNumber = userNumber;
        this.userName = userName;
        this.userBirthday = userBirthday;
    }

    // Parcelable interface Constructor
    protected loginUserData(Parcel in) {
        userId = in.readInt();
        userEmail = in.readString();
        userPassword = in.readString();
        userProfileImage = in.readString();
        userNumber = in.readString();
        userName = in.readString();
        userBirthday = in.readString();
    }

    // ===========Activity간 데이터를 한꺼번에 전달받기 위해 사용한 Interface==============
    // 새롭게 생성된 Activity 에서 이 객체를 추출해 낼 떄 호출
    public static final Creator<loginUserData> CREATOR = new Creator<loginUserData>() {
        // writeToParcel() 메소드에서 썼던 순서대로 읽어 옴
        @Override
        public loginUserData createFromParcel(Parcel in) {
            return new loginUserData(in);
        }

        @Override
        public loginUserData[] newArray(int size) {
            return new loginUserData[size];
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
        dest.writeInt(userId);
        dest.writeString(userEmail);
        dest.writeString(userPassword);
        dest.writeString(userProfileImage);
        dest.writeString(userNumber);
        dest.writeString(userName);
        dest.writeString(userBirthday);
    }
    //=======================================================================

    // Getter and Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }
}
