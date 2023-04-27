package com.example.itda.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends Activity {
    private loginUserData User;    // 가게 데이터

    private ScrollView LoginTotalScrollView;    // 전체 스크롤뷰 레이아웃
    private TextView LoginPasswordSearchBtn;// 비밀번호 찾기
    private TextView LoginMembershipBtn;    // 회원가입

    private EditText LoginEmailEt;          // 이메일 입력
    private EditText LoginPasswordEt;       // 비밀번호 입력

    private Button LoginBtn;           // 로그인 버튼

    private ImageButton LoginKakaoBtn;  // 카카오 로그인 버튼

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private String LOGIN_PATH;      // 로그인 정보 데이터 조회 Rest API
    private String HOST;            // Host 정보

    private Intent intent;  // 화면 이동을 위한 Intent 객체 선언

    boolean isKeyboardShowing = false;
    int keypadBaseHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        LOGIN_PATH = ((globalMethod) getApplication()).getLoginPath();    // 로그인 정보 데이터 조회 Rest API

        initView(); // 뷰 생성

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
            String loginPassword = getHash(LoginPasswordEt.getText().toString());   // 입력 비밀번호 ( SHA-256 해시 암호화 알고리즘 사용 )

            Map<String, String> param = new HashMap<>();
            param.put("email", loginEmail);
            param.put("password", loginPassword);

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
            StringRequest loginRequest = new StringRequest(Request.Method.POST, HOST + LOGIN_PATH, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                    JSONArray userArr = jsonObject.getJSONArray("user");  // 객체에 user라는 Key를 가진 JSONArray 생성

                    if(userArr.length() > 0) {
                        JSONObject object = userArr.getJSONObject(0);    // JSONObject 생성

                        // 해당 앱의 파일에 저장되는 데이터를 다룰 수 있는 인터페이스이다.
                        // 즉, 자동로그인 기능에서 뿐만 아니라 앱을 종료하고 새로 시작했을 때 똑같이 가지고 있어야 하는 데이터가 있을 경우 넣어서 사용할 수 있다.
                        // 데이터는 해당 앱을 삭제하거나 해당 앱의 데이터를 삭제하지 않는 이상 계속 유지된다.
                        // key, value와 같은 Map 형태로 값을 넣을 수 있으며 넣을 수 있는 값의 타입은 Boolean, Float, Int, Long, String, StringSet이 있다.
                        // Activity.MODE_PRIVATE : 기본 모드로 이렇게 설정할 경우 해당 데이터는 해당 앱에서만 사용
                        SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);

                        // SharedPreferences를 가져와서 거기서 Editor 객체를 가져와 put 메소드를 사용
                        SharedPreferences.Editor autoLoginEdit = auto.edit();

                        // 유저 정보 데이터 저장
                        autoLoginEdit.putInt("userId", object.getInt("userId"));    // 유저 고유 아이디
                        autoLoginEdit.putString("userEmail", object.getString("userEmail"));    // 유저 이메일
                        autoLoginEdit.putString("userPassword", object.getString("userPassword"));  // 유저 비밀번호
                        autoLoginEdit.putString("userProfileImage", HOST + object.getString("userProfileImage"));   // 유저 프로필 이미지
                        autoLoginEdit.putString("userNumber", object.getString("userNumber"));  // 유저 번호
                        autoLoginEdit.putString("userName", object.getString("userName"));  // 유저 이름
                        autoLoginEdit.putString("userBirthday", object.getString("userBirthday"));  // 유저 생일
                        autoLoginEdit.putString("userLoginFlag", object.getString("userLoginFlag"));  // 유저 로그인 방식

                        autoLoginEdit.apply();  // 데이터를 저장

                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        setResult(9001, intent);    // 결과 코드와 intent 값 전달

                        finish();   // Activity 종료
                    }else{
                        StyleableToast.makeText(this, "이메일 또는 비밀번호가 일치하지 않습니다.", R.style.redToast).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("getLoginUserError", "onErrorResponse : " + error);
            }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

            loginRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(loginRequest);      // RequestQueue에 요청 추가
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

        // 이메일 EditText 엔터키 입력 리스너
        LoginEmailEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            switch(actionId){
                case EditorInfo.IME_ACTION_NEXT:
                    // 이메일 입력이 비어있는지 확인
                    if(!TextUtils.isEmpty(LoginEmailEt.getText().toString())){
                        LoginPasswordEt.requestFocus();    // 비밀번호 입력 포커스
                    }else{
                        StyleableToast.makeText(this, "이메일을 입력해 주세요.", R.style.orangeToast).show();
                    }
                    break;
            }
            return true;
        });

        // 비밀번호 EditText 엔터키 입력 리스너
        LoginPasswordEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            switch(actionId){
                case EditorInfo.IME_ACTION_DONE:
                    // 이메일과 비밀번호 입력이 비어있는지 확인
                    if(!TextUtils.isEmpty(LoginEmailEt.getText().toString()) && !TextUtils.isEmpty(LoginPasswordEt.getText().toString())){
                        LoginBtn.callOnClick(); // 로그인 버튼 클릭
                    }else{
                        StyleableToast.makeText(this, "이메일 또는 비밀번호를 입력해 주세요.", R.style.orangeToast).show();
                    }
                    break;
            }
            return true;
        });
    }

    // 뷰 생성
    private void initView() {
        LoginTotalScrollView = findViewById(R.id.login_total_scroll);
        LoginPasswordSearchBtn = findViewById(R.id.login_password_search_bt);
        LoginMembershipBtn = findViewById(R.id.login_membership_bt);
        LoginEmailEt = findViewById(R.id.login_email_et);
        LoginEmailEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        LoginPasswordEt = findViewById(R.id.login_password_et);
        LoginBtn = findViewById(R.id.login_enter_bt);
        LoginKakaoBtn = findViewById(R.id.login_kakao_logo_btn);
    }

    // *** 문자열 해시 암호화 코드 ***
    private static String getHash(String str) {
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
