package com.example.itda.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.itda.R;

public class MembershipActivity extends Activity {
    TextView MembershipPasswordAgainTv;     //입력한 비밀번호와 재입력한 비밀번호가 일치하는 지 체크해주는 텍스트

    EditText MembershipEmailEt;             //이메일 입력 칸
    EditText MembershipPasswordEt;          //비밀번호 입력 칸
    EditText MembershipPasswordAgainEt;     //비밀번호 재입력 칸
    EditText MembershipNicknameEt;          //닉네임 입력 칸

    Button MembershipEmailAutBtn;           //이메일 인증 버튼
    Button MembershipEnterBtn;              //회원가입 버튼

    ImageButton MembershipPasswordShowIc;     //입력한 비밀번호 보여주는 버튼
    ImageView MembershipPasswordCheckIc;    //입력한 비밀번호가 형식에 맞는지 체크해주는 아이콘
    ImageButton MembershipPasswordAgainShowIc;//재입력한 비밀번호 보여주는 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        MembershipPasswordAgainTv = findViewById(R.id.membership_password_again_tv);

        MembershipEmailEt = findViewById(R.id.membership_email_et);
        MembershipPasswordEt = findViewById(R.id.membership_password_et);
        MembershipPasswordAgainEt = findViewById(R.id.membership_password_again_et);
        MembershipNicknameEt = findViewById(R.id.membership_nickname_et);

        MembershipEmailAutBtn = findViewById(R.id.membership_email_aut_btn);
        MembershipEnterBtn = findViewById(R.id.membership_enter_btn);

        MembershipPasswordShowIc = findViewById(R.id.membership_password_show_ic);
        MembershipPasswordCheckIc = findViewById(R.id.membership_password_check_ic);
        MembershipPasswordAgainShowIc = findViewById(R.id.membership_password_again_show_ic);
    }
}
