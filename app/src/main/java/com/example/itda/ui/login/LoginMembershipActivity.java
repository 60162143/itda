package com.example.itda.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.itda.library.mail.SendMail;
import com.example.itda.library.timer.TimerView;
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

public class LoginMembershipActivity extends AppCompatActivity {

    // Layout
    private ImageButton backIc;     // 상단 뒤로가기 버튼
    private EditText authEmail;     // 인증 받을 메일 입력
    private EditText authNumber;    // 인증 번호 입력
    private EditText newPassword;       // 새로운 비밀 번호 입력
    private EditText newPasswordOnce;   // 새로운 비밀 번호 재입력 입력
    private Button authDupBtn;      // 중복 확인 버튼
    private Button authEmailBtn;    // 인증 번호 요청 버튼
    private Button authNumberBtn;   // 인증 번호 입력 버튼
    private Button membershipBtn;   // 회원가입 버튼
    private TextView newPasswordTxt;        // 새로운 비밀 번호 입력 안내 텍스트
    private TextView newPasswordOnceTxt;    // 새로운 비밀 번호 재입력 입력 안내 텍스트
    private TimerView authNumberTimer;      // 인증 타이머
    private LinearLayout authNumberLayout;  // 인증 번호 입력 전체 레이아웃
    private LinearLayout PasswordLayout;    // 비밀 번호 입력 전체 레이아웃


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String GET_USER_ID_PATH;    // 이메일로 유저 고유 아이디 조회 Rest API
    private String INSERT_USER_PATH;    // 유저 회원가입 Rest API
    private String HOST;    // Host 정보


    // Thread Handler
    private final Handler handler = new Handler();


    // Global Data
    private String emailCode = "";  // 이메일 인증 번호
    private boolean newPasswordFlag = false;    // 비밀번호 입력 조건 만족 여부

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_membership);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보
        GET_USER_ID_PATH = ((globalMethod) getApplication()).getLoginUserIdPath();  // 이메일로 유저 고유 아이디 조회 Rest API
        INSERT_USER_PATH = ((globalMethod) getApplication()).insertLoginUserPath(); // 유저 회원가입 Rest API

        // Init View
        initView();

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> {
            // 타이머가 작동중이라면 종료 후 실행
            if(authNumberTimer.isCertification()){
                authNumberTimer.stopAnimator();
            }

            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(LoginMembershipActivity.this, LoginActivity.class);

            setResult(4000, intent);    // 결과 코드와 intent 값 전달
            finish();
        });

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000으로 넘어왔다면 회원가입 추가 정보 입력 완료
                // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                Intent intent = new Intent(LoginMembershipActivity.this, LoginActivity.class);

                setResult(1000, intent);    // 결과 코드와 intent 값 전달
                finish();
            }
        });

        // 여러 블로그에서 Nougat(Android N-OS)부터 Main Thread 에서 Network 액세스를 차단하기 때문에 아래와 같이 해결하라고 말하고 있는데요.
        // 정책변경 보다는 Thread 분리가 우선!!!! ( 추후에 쓰레드 분리하는법 공부하자! )

        // 아래와 같이 StrictMode 테스트를 위해 App에서 StrictMode 정책 설정을 하고 Main Thread에서 SharedPreferences를 설정하는 코드를 적용해보았습니다.

        // StrictMode 정책을 penaltyLog()로 설정하였을 때 위반로그를 확인 할 수 있습니다.
        // SharedPreferences 와 같이 Disk I/O 작업을 수반하는 동작은 Main Thread 에서 분리하여 개발해야겠습니다.
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        // 인증 받을 메일 입력 텍스트 감지 리스너
        authEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 인증 메일 발송 버튼 활성/비활성화
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우
                    authEmailBtn.setBackgroundResource(R.drawable.round_green_10dp);
                    authEmailBtn.setEnabled(true);
                }else{
                    authEmailBtn.setBackgroundResource(R.drawable.round_gray_10dp);
                    authEmailBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 인증번호 입력 텍스트 감지 리스너
        authNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 중복확인 버튼, 인증 번호 입력 버튼 활성/비활성화
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우
                    authDupBtn.setBackgroundResource(R.drawable.round_green_10dp);
                    authDupBtn.setEnabled(true);
                    authNumberBtn.setBackgroundResource(R.drawable.round_green_10dp);
                    authNumberBtn.setEnabled(true);
                }else{
                    authDupBtn.setBackgroundResource(R.drawable.round_gray_10dp);
                    authDupBtn.setEnabled(false);
                    authNumberBtn.setBackgroundResource(R.drawable.round_gray_10dp);
                    authNumberBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 메일 인증 요청 버튼 클릭 리스너
        authEmailBtn.setOnClickListener(view -> {
            authNumber.setText(""); // 인증 번호 입력 초기화

            Log.d("mailTransfer", "메일 전송중...");
            // 인증번호 입력 칸, 버튼 보이기
            authNumberLayout.setVisibility(View.VISIBLE);

            authEmailBtn.setText("인증번호 재요청");

            // 타이머가 작동중이라면 종료 후 실행
            if(authNumberTimer.isCertification()){
                authNumberTimer.stopAnimator();
            }

            authNumberTimer.start(180000);  // 3분 타이머 실행

            // 타이머 텍스트 변경 리스너
            authNumberTimer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                    // 텍스트가 입력되기 전에 Call back
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if(charSequence.toString().equals("00:00")){
                        StyleableToast.makeText(getApplicationContext(), "인증 시간이 만료되었습니다.", R.style.redToast).show();
                        authNumberLayout.setVisibility(View.GONE);  // 인증 번호 전체 레이아웃 숨김

                        authNumberTimer.stopAnimator(); // 타이머 종료
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    // 텍스트 입력이 모두 끝났을 때 Call back
                }
            });

            // UI 조작 완료후 3초 뒤에 메일 전송 함수 실행
            handler.postDelayed(() -> {
                sendEmail(authEmail.getText().toString());  // 인증 메일 전송
            }, 3000);
        });

        // 메일 중복 확인 버튼 클릭 리스너
        authDupBtn.setOnClickListener(view -> {

            if (TextUtils.isEmpty(authEmail.getText().toString())) {    // 이메일을 입력하지 않을 경우
                StyleableToast.makeText(getApplicationContext(), "이메일을 입력해주세요.", R.style.redToast).show();
            }else if(!isValidEmail(authEmail.getText().toString())){    // 이메일 형식이 올바르지 않은 경우
                StyleableToast.makeText(getApplicationContext(), "이메일의 형식이 올바른지 확인해주세요.", R.style.redToast).show();
            }else{
                getUserId();    // 인증 받을 이메일의 중복 확인
            }
        });

        // 메일 인증 확인 버튼 클릭 리스너
        authNumberBtn.setOnClickListener(view -> {
            // 타이머가 진행중이고 인증 코드를 정확이 입력했다면
            if(authNumberTimer.isCertification()){
                if(emailCode.equals(authNumber.getText().toString())){
                    PasswordLayout.setVisibility(View.VISIBLE); // 비밀 번호 전체 레이아웃 표시
                    authNumberTimer.stopAnimator(); // 타이머 종료
                    authNumberLayout.setVisibility(View.GONE);  // 인증 번호 전체 레이아웃 숨김

                    // 인증 번호 요청 비활성화
                    authEmailBtn.setEnabled(false);
                    authEmail.setEnabled(false);

                    // 키보드 숨기기
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(authNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }else{
                    StyleableToast.makeText(getApplicationContext(), "인증 번호를 확인해주세요.", R.style.redToast).show();
                }
            }
        });

        // 새로운 비밀번호 입력 텍스트 감지 리스너
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우

                    // 비밀번호 조건을 만족하는지, 현재 비밀번호를 제대로 입력했는지 확인
                    // 회원가입 버튼 활성화 여부 변경
                    if(pwdRegularExpressionChk(charSequence.toString())){   // 비밀번호 정규식에 부합 하는지
                        newPasswordFlag = true;  // 비밀번호 정규식 부합하는지 Flag
                        newPasswordTxt.setTextColor(Color.parseColor("#5E5E5E"));

                        if(charSequence.toString().equals(newPasswordOnce.getText().toString())){
                            newPasswordOnceTxt.setVisibility(View.GONE);    // 비밀번호 일치 안내 텍스트 숨김

                            // 회원가입 버튼 활성화
                            membershipBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            membershipBtn.setEnabled(true);
                        }else{
                            newPasswordOnceTxt.setVisibility(View.VISIBLE); // 비밀번호 일치 안내 텍스트 표시

                            // 회원가입 버튼 활성화
                            membershipBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            membershipBtn.setEnabled(false);
                        }

                    }else{
                        newPasswordOnceTxt.setVisibility(View.VISIBLE); // 비밀번호 일치 안내 텍스트 숨김


                        newPasswordTxt.setTextColor(Color.parseColor("#EF3560"));   // 비밀번호 정규식 부합 여부 빨간 텍스트

                        // 회원가입 버튼 비활성화
                        membershipBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        membershipBtn.setEnabled(false);

                        newPasswordFlag = false;    // 비밀번호 정규식 부합하는지 Flag
                    }
                }else{
                    newPasswordFlag = false;    // 비밀번호 정규식 부합하는지 Flag
                    newPasswordTxt.setTextColor(Color.parseColor("#EF3560"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 새로운 비밀번호 재입력 텍스트 감지 리스너
        newPasswordOnce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우

                    // 입력한 비밀번호와 동일한지 확인
                    // 변경 버튼 활성화 여부 변경
                    if(newPassword.getText().toString().equals(charSequence.toString())){   // 비밀번호 입력과 재입력 일치하는지 확인
                        newPasswordOnceTxt.setVisibility(View.GONE);    // 비밀번호 일치 안내 텍스트 숨김

                        if(newPasswordFlag){    // 비밀번호 입력이 정규식에 일치하는지

                            // 회원가입 버튼 활성화
                            membershipBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            membershipBtn.setEnabled(true);
                        }else{
                            // 회원가입 버튼 비활성화
                            membershipBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            membershipBtn.setEnabled(false);
                        }

                    }else{
                        newPasswordOnceTxt.setVisibility(View.VISIBLE); // 비밀번호 일치 안내 텍스트 표시

                        // 회원가입 버튼 비활성화
                        membershipBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        membershipBtn.setEnabled(false);
                    }
                }else{
                    newPasswordOnceTxt.setVisibility(View.GONE);    // 비밀번호 일치 안내 텍스트 숨김
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 회원가입 정보 insert
        membershipBtn.setOnClickListener(view -> {
            // POST 방식 파라미터 설정
            // Param => email : 회원가입 이메일
            //          password : 회원가입 비밀번호
            Map<String, String> param = new HashMap<>();
            param.put("email", authEmail.getText().toString()); // 회원가입 이메일
            param.put("password", getHash(newPassword.getText().toString()));   // 회원가입 비밀번호

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
            StringRequest insertUserRequest = new StringRequest(Request.Method.POST, HOST + INSERT_USER_PATH, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                    String success = jsonObject.getString("success");   // Success Flag

                    if(!TextUtils.isEmpty(success) && success.equals("1")) {
                        StyleableToast.makeText(getApplicationContext(), "회원가입 성공!", R.style.blueToast).show();

                        // 로그인 유저 고유 아이디
                        int userId = Integer.parseInt(jsonObject.getString("userId"));

                        Intent intent = new Intent(LoginMembershipActivity.this, LoginMembershipOptionActivity.class);
                        intent.putExtra("userId", userId);  // 회원가입된 유저 고유 아이디
                        intent.putExtra("loginFlag", "normal"); // 일반 회원가입 flag

                        activityResultLauncher.launch(intent);
                    }else{
                        StyleableToast.makeText(getApplicationContext(), "변경가입 실패...", R.style.redToast).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("insertUserError", "onErrorResponse : " + error);
            }) {
                @Override
                protected Map<String, String> getParams(){
                    // php로 설정값을 보낼 수 있음 ( POST )
                    return param;
                }
            };

            insertUserRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(insertUserRequest);      // RequestQueue에 요청 추가
        });
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            if(authNumberTimer.isCertification()){
                authNumberTimer.stopAnimator();
            }

            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(LoginMembershipActivity.this, LoginActivity.class);

            setResult(4000, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.login_membership_back_ic);       // 상단 뒤로가기 버튼
        authEmail = findViewById(R.id.login_membership_auth_email); // 인증 받을 메일 입력
        authNumber = findViewById(R.id.login_membership_auth_number);   // 인증 번호 입력
        newPassword = findViewById(R.id.login_membership_new_password); // 새로운 비밀 번호 입력
        newPasswordOnce = findViewById(R.id.login_membership_new_password_once);    // 새로운 비밀 번호 재입력
        authDupBtn = findViewById(R.id.login_membership_auth_mail_dup_btn);     // 메일 중복 확인 버튼
        authEmailBtn = findViewById(R.id.login_membership_auth_mail_btn);       // 인증 메일 요청 버튼
        authNumberBtn = findViewById(R.id.login_membership_auth_number_btn);    // 인증 번호 입력 버튼
        newPasswordTxt = findViewById(R.id.login_membership_new_password_txt);  // 새로운 비밀 번호 입력 안내 텍스트
        newPasswordOnceTxt = findViewById(R.id.login_membership_new_password_once_txt); // 새로운 비밀 번호 재입력 안내 텍스트
        authNumberTimer = findViewById(R.id.login_membership_auth_number_timer);    // 인증 타이머
        authNumberLayout = findViewById(R.id.login_membership_auth_number_layout);  // 인증 번호 입력 전체 레이아웃
        PasswordLayout = findViewById(R.id.login_membership_password_layout);       // 비밀 번호 입력 전체 레이아웃
        membershipBtn = findViewById(R.id.login_membership_btn);    // 회원가입 버튼
    }

    // 유저 이메일이 DB에 있는지 확인
    private void getUserId() {
        // POST 방식 파라미터 설정
        // Param => userEmail : 입력한 유저 이메일
        Map<String, String> param = new HashMap<>();
        param.put("userEmail", authEmail.getText().toString()); // 입력한 유저 이메일

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest getUserIdRequest = new StringRequest(Request.Method.POST, HOST + GET_USER_ID_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성

                // 로그인 유저 고유 아이디
                int userId = Integer.parseInt(jsonObject.getJSONArray("userId").getJSONObject(0).getString("userId"));

                // 유저 고유 아이디가 0일 경우 로그인 정보가 없음
                if(userId == 0){
                    authDupBtn.setVisibility(View.GONE);
                    authEmail.setEnabled(false);
                    authEmailBtn.setVisibility(View.VISIBLE);
                    authEmailBtn.setText("인증번호 요청");

                    // 키보드 숨기기
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(authNumber.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    StyleableToast.makeText(getApplicationContext(), "사용 가능한 이메일 주소 입니다.", R.style.blueToast).show();
                }else{
                    StyleableToast.makeText(getApplicationContext(), "이미 가입된 이메일 주소 입니다.", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getUserIdError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams(){
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        getUserIdRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(getUserIdRequest);      // RequestQueue에 요청 추가
    }

    // 인증 메일 전송
    private void sendEmail(String email){
        SendMail mailServer = new SendMail();
        emailCode = mailServer.sendSecurityCode(getApplicationContext(), email);
    }

    /**
     * Comment  : 정상적인 이메일 인지 검증.
     */
    public boolean isValidEmail(String email) {
        boolean err = false;
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if(m.matches()) {
            err = true;
        }
        return err;
    }

    // *** 문자열 해시 암호화 코드 ***
    private static String getHash(String str) {
        String hashStr;
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
        boolean chk = false;    // 정규식 만족 여부

        // 특수문자, 영문, 숫자 조합 (8 ~ 20자리)
        match = Pattern.compile(pattern1).matcher(newPwd);

        if(match.find()) {
            chk = true;
        }

        return chk;
    }
}