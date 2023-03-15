package com.example.itda.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.itda.R;

public class LoginActivity extends Activity {
    TextView LoginPasswordSearchBtn;    //비밀번호를 잊어버리셨습니까 버튼
    TextView LoginMembershipBtn;        //아직 회원이 아니신가요 버튼

    EditText LoginEmailEt;              //이메일 입력 칸
    EditText LoginPasswordEt;           //비밀번호 입력 칸

    Button LoginEnterBtn;               //로그인 버튼

    ImageButton LoginKakaoLogoBtn;      //카카오 로그인 버튼
    ImageButton LoginFacebookLogoBtn;   //페이스북 로그인 버튼
    ImageButton LoginNaverLogoBtn;      //네이버 로그인 버튼

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginPasswordSearchBtn = findViewById(R.id.login_password_search_bt);
        LoginMembershipBtn = findViewById(R.id.login_membership_bt);

        LoginEmailEt = findViewById(R.id.login_email_et);
        LoginPasswordEt = findViewById(R.id.login_password_et);

        LoginEnterBtn = findViewById(R.id.login_enter_bt);
        LoginKakaoLogoBtn = findViewById(R.id.login_kakao_logo_btn);
        LoginFacebookLogoBtn = findViewById(R.id.login_facebook_logo_btn);
        LoginNaverLogoBtn = findViewById(R.id.login_naver_logo_btn);

        LoginPasswordSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), PasswordActivity.class);
                startActivity(intent);
            }
        });

        LoginMembershipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), MembershipActivity.class);
                startActivity(intent);
            }
        });
    }
}
