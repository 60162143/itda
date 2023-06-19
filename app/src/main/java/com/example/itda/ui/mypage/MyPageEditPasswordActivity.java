package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageEditPasswordActivity extends AppCompatActivity {

    // Layout
    private ImageButton backIc;     // 상단 뒤로가기 버튼
    private EditText oldPassword;   // 이전 비밀번호 입력
    private EditText newPassword;   // 새로운 비밀번호 입력
    private TextView oldPasswordTxt;    // 이전 비밀번호 입력 안내 텍스트
    private TextView newPasswordTxt;    // 새로운 비밀번호 입력 안내 텍스트
    private TextView lostPasswordBtn;   // 비밀번호 재설정 버튼
    private Button PasswordBtn; // 비밀번호 변경 버튼


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성


    // Rest API
    private String UPDATE_PASSWORD_PATH;    // 비밀번호 변경 Rest API
    private String HOST;    // Host 정보


    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )

    // Global Data
    private boolean oldPasswordFlag = false;    // 현재 비밀번호를 정확히 입력했는지 여부
    private boolean newPasswordFlag = false;    // 변경할 비밀번호를 정확히 입력했는지 여부

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit_password);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보
        UPDATE_PASSWORD_PATH = ((globalMethod) getApplication()).getUpdateUserPasswordPath();   // 비밀번호 변경 Rest API

        // Init View
        initView();

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // 현재 비밀번호 입력 텍스트 감지 리스너
        oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우
                    oldPasswordTxt.setVisibility(View.VISIBLE); // 이전 비밀번호 입력 안내 텍스트 표시

                    // 비밀번호가 암호화된 문자열로 저장되어 있기 때문에
                    // 해시 함수를 이용하여 입력된 문자열이 같은지 확인
                    // 암호화는 가능하지만 복호화가 불가능하기 때문
                    if(User.getString("userPassword", "").equals(getHash(charSequence.toString()))){
                        oldPasswordFlag = true; // 현재 비밀번호를 정확히 입력했는지 여부
                        oldPasswordTxt.setVisibility(View.GONE);    // 현재 비밀번호 입력 안내 텍스트 숨김

                        // 변경할 비밀번호가 제대로 입력되었을 경우 변경 버튼 활성화
                        if(newPasswordFlag){
                            PasswordBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            PasswordBtn.setEnabled(true);
                        }else{
                            PasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            PasswordBtn.setEnabled(false);
                        }
                    }else{
                        PasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        PasswordBtn.setEnabled(false);
                        oldPasswordFlag = false;    // 현재 비밀번호를 정확히 입력했는지 여부
                    }
                }else{
                    oldPasswordTxt.setVisibility(View.GONE);    // 현재 비밀번호 입력 안내 텍스트 숨김
                    oldPasswordFlag = false;    // 현재 비밀번호를 정확히 입력했는지 여부
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 변경할 비밀번호 입력 텍스트 감지 리스너
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우

                    // 비밀번호 조건을 만족하는지, 현재 비밀번호를 제대로 입력했는지 확인
                    // 변경 버튼 활성화 여부 변경
                    if(pwdRegularExpressionChk(charSequence.toString())){
                        newPasswordFlag = true;

                        if(oldPasswordFlag){
                            PasswordBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            PasswordBtn.setEnabled(true);
                        }else{
                            PasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            PasswordBtn.setEnabled(false);
                        }
                    }else{
                        PasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        PasswordBtn.setEnabled(false);
                        newPasswordFlag = false;
                    }
                }else{
                    newPasswordFlag = false;
                    newPasswordTxt.setTextColor(Color.parseColor("#5E5E5E"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 비밀번호 변경 버튼 리스너
        PasswordBtn.setOnClickListener(view -> {
            // POST 방식 파라미터 설정
            // Param => userId : 변경할 유저 고유 아이디
            //          password : 변경할 패스워드
            Map<String, String> param = new HashMap<>();
            param.put("userId", String.valueOf(User.getInt("userId", 0)));  // 변경할 유저 고유 아이디
            param.put("password", getHash(newPassword.getText().toString()));    // 변경할 패스워드

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
            StringRequest updatePasswordRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_PASSWORD_PATH, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                    String success = jsonObject.getString("success");   // Success Flag

                    if(!TextUtils.isEmpty(success) && success.equals("1")) {
                        StyleableToast.makeText(getApplicationContext(), "변경 성공!", R.style.blueToast).show();

                        // 해당 앱의 파일에 저장되는 데이터를 다룰 수 있는 인터페이스이다.
                        // 즉, 자동로그인 기능에서 뿐만 아니라 앱을 종료하고 새로 시작했을 때 똑같이 가지고 있어야 하는 데이터가 있을 경우 넣어서 사용할 수 있다.
                        // 데이터는 해당 앱을 삭제하거나 해당 앱의 데이터를 삭제하지 않는 이상 계속 유지된다.
                        // key, value와 같은 Map 형태로 값을 넣을 수 있으며 넣을 수 있는 값의 타입은 Boolean, Float, Int, Long, String, StringSet이 있다.
                        // Activity.MODE_PRIVATE : 기본 모드로 이렇게 설정할 경우 해당 데이터는 해당 앱에서만 사용
                        SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);

                        // SharedPreferences를 가져와서 거기서 Editor 객체를 가져와 put 메소드를 사용
                        SharedPreferences.Editor autoLoginEdit = auto.edit();

                        autoLoginEdit.putString("userPassword", getHash(newPassword.getText().toString()));  // 유저 비밀번호

                        autoLoginEdit.apply();  // 데이터를 저장

                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(MyPageEditPasswordActivity.this, MyPageEditActivity.class);

                        setResult(4000, intent);    // 결과 코드와 intent 값 전달
                        finish();
                    }else{
                        StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("updatePasswordError", "onErrorResponse : " + error);
            }) {
                @Override
                protected Map<String, String> getParams(){
                    // php로 설정값을 보낼 수 있음 ( POST )
                    return param;
                }
            };

            updatePasswordRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(updatePasswordRequest);      // RequestQueue에 요청 추가
        });

        // 비밀번호 재설정 버튼 클릭 리스너
        lostPasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyPageEditPasswordActivity.this, MyPageEditLostPasswordActivity.class);

            activityResultLauncher.launch(intent);
        });

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000으로 넘어왔다면 비밀번호 변경 완료
                // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                Intent intent = new Intent(MyPageEditPasswordActivity.this, MyPageEditActivity.class);

                setResult(4000, intent);    // 결과 코드와 intent 값 전달

                finish();
            }else if(result.getResultCode() == 2000){ // resultCode가 2000으로 넘어왔다면 뒤로가기 버튼으로 넘어옴
                Log.d("msg", "MyPageEditLostPasswordActivity close!");
            }
        });
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_password_back_ic);   // 상단 뒤로가기 버튼
        oldPassword = findViewById(R.id.mypage_edit_old_password);  // 이전 비밀번호 입력
        newPassword = findViewById(R.id.mypage_edit_new_password);  // 새로운 비밀번호 입력
        oldPasswordTxt = findViewById(R.id.mypage_edit_old_password_txt);   // 이전 비밀번호 입력 텍스트
        newPasswordTxt = findViewById(R.id.mypage_edit_new_password_txt);   // 새로운 비밀번호 입력 텍스트
        lostPasswordBtn = findViewById(R.id.mypage_edit_password_lost_btn); // 비밀번호 재설정 버튼
        PasswordBtn = findViewById(R.id.mypage_edit_password_btn);  // 비밀번호 변경 버튼
    }

    // *** 문자열 해시 암호화 코드 ***
    private static String getHash(String str) {
        String hashStr; // 해시 암호화된 문자열
        try{
            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256"); // SHA-256 해시함수를 사용
            sh.update(str.getBytes()); // str의 문자열을 해싱하여 sh에 저장
            byte[] data = sh.digest(); // sh 객체의 다이제스트를 얻는다.

            //얻은 결과를 string으로 변환
            StringBuilder sb = new StringBuilder();

            for (byte byteData : data) {
                sb.append(Integer.toString((byteData & 0xff) + 0x100, 16).substring(1));
            }

            hashStr = sb.toString();
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace(); hashStr = null;
        }

        return hashStr;
    }
    // *** 스틱코드 등록 코드 ***

    /** 비밀번호 정규식 체크 **/
    public boolean pwdRegularExpressionChk(String newPwd) {
        final String pattern1 = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$"; // 영문, 숫자, 특수문자

        Matcher match;
        boolean chk = false;    // 정규실 만족 여부

        // 특수문자, 영문, 숫자 조합 (8 ~ 20자리)
        match = Pattern.compile(pattern1).matcher(newPwd);

        if(match.find()) {
            chk = true;
            newPasswordTxt.setTextColor(Color.parseColor("#5E5E5E"));
        }else{
            newPasswordTxt.setTextColor(Color.parseColor("#EF3560"));
        }

        return chk;
    }
}

