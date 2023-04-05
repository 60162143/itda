package com.example.itda.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    private TextView LoginPasswordSearchBtn;// 비밀번호 찾기
    private TextView LoginMembershipBtn;    // 회원가입

    private EditText LoginEmailEt;          // 이메일 입력
    private EditText LoginPasswordEt;       // 비밀번호 입력

    private Button LoginBtn;           // 로그인 버튼

    private ImageButton LoginKakaoBtn;  // 카카오 로그인 버튼

    private Intent intent;  // 화면 이동을 위한 Intent 객체 선언

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        // 카카오 로그인 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(R.drawable.kakao_talk)     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(LoginKakaoBtn);     // 이미지를 보여줄 View를 지정

        // 로그인 버튼 클릭 리스너
        LoginBtn.setOnClickListener(v -> {
            String loginEmail = LoginEmailEt.getText().toString();                  // 입력 이메일
            String loginPassword = getHash(LoginPasswordEt.getText().toString());   // 입력 비밀번호


        });

        // 비밀번호 찾기 클릭 리스너
        LoginPasswordSearchBtn.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), PasswordActivity.class);
            startActivity(intent);
        });

        // 회원가입 클릭 리스너
        LoginMembershipBtn.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MembershipActivity.class);
            startActivity(intent);
        });
    }

    // 뷰 생성
    private void initView() {
        LoginPasswordSearchBtn = findViewById(R.id.login_password_search_bt);
        LoginMembershipBtn = findViewById(R.id.login_membership_bt);
        LoginEmailEt = findViewById(R.id.login_email_et);
        LoginPasswordEt = findViewById(R.id.login_password_et);
        LoginBtn = findViewById(R.id.login_enter_bt);
        LoginKakaoBtn = findViewById(R.id.login_kakao_logo_btn);
    }

    // *** 문자열 해시 암호화 코드 ***
    public static String getHash(String str) {
        String hashStr = "";
        try{
            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte byteData[] = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashStr = sb.toString();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace(); hashStr = null;
        }
        return hashStr;
    }
    // *** 스틱코드 등록 코드 ***
}
