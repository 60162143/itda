package com.example.itda.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.itda.R;

public class PasswordActivity extends Activity {
    TextView PasswordNewAgainTv;    //입력한 새로운 비밀번호와 재입력한 새로운 비밀번호가 일치하는지 체크해주는 텍스트

    EditText PasswordEmailEt;       //이메일 입력하는 칸
    EditText PasswordPresentEt;     //현재 비밀번호 입력하는 칸
    EditText PasswordNewEt;         //새로운 비밀번호 입력하는 칸
    EditText PasswordNewAgainEt;    //새로운 비밀번호 재입력하는 칸

    Button PasswordEmailAutBtn;     //이메일 인증하는 버튼
    Button PasswordChangeBtn;       //비밀번호 변경 버튼

    ImageView PasswordPresentShowIc;//현재 비밀번호 보여주는 버튼
    ImageView PasswordNewShowIc;    //새로운 비밀번호 보여주는 버튼
    ImageView PasswordNewCheckIc;   //새로 입력한 비밀번호가 형식에 맞는 지 체크해주는 아이콘
    ImageView PasswordNewAgainShowIc;//새로 재입력한 비밀번호 보여주는 버튼

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        PasswordNewAgainTv = findViewById(R.id.membership_password_again_tv);

        PasswordEmailEt = findViewById(R.id.password_email_et);
        PasswordPresentEt = findViewById(R.id.password_present_et);
        PasswordNewEt = findViewById(R.id.password_new_et);
        PasswordNewAgainEt = findViewById(R.id.password_new_again_et);

        PasswordEmailAutBtn = findViewById(R.id.password_email_aut_btn);
        PasswordChangeBtn = findViewById(R.id.password_change_btn);

        PasswordPresentShowIc = findViewById(R.id.password_present_show_ic);
        PasswordNewShowIc = findViewById(R.id.password_new_show_ic);
        PasswordNewCheckIc = findViewById(R.id.password_new_check_ic);
        PasswordNewAgainShowIc = findViewById(R.id.password_new_again_show_ic);
    }
}
