package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.Nullable;

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

import io.github.muddz.styleabletoast.StyleableToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyPageEditLostPasswordActivity extends Activity {

    // Layout
    private ImageButton backIc;     // 상단 뒤로가기 버튼
    private EditText authEmail;     // 인증 받을 메일 입력
    private EditText authNumber;    // 인증 번호 입력
    private EditText changePassword;        // 변경할 비밀 번호 입력
    private EditText changePasswordOnce;    // 변경할 비밀 번호 재입력 입력
    private Button authEmailBtn;        // 인증 번호 요청 버튼
    private Button authNumberBtn;       // 인증 번호 입력 버튼
    private Button changePasswordBtn;   // 비밀번호 변경 버튼
    private TextView changePasswordTxt; // 변경할 비밀 번호 입력 안내 텍스트
    private TextView changePasswordOnceTxt; // 변경할 비밀 번호 재입력 입력 안내 텍스트
    private TimerView authNumberTimer;      // 인증 타이머
    private LinearLayout authNumberLayout;      // 인증 번호 입력 전체 레이아웃
    private LinearLayout changePasswordLayout;  // 비밀 번호 변경 전체 레이아웃


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String UPDATE_PASSWORD_PATH;    // 비밀번호 변경 Rest API
    private String HOST;    // Host 정보


    // Thread Handler
    private final Handler handler = new Handler();


    // Login Data
    private SharedPreferences User; // 로그인 데이터 ( 전역 변수 )

    // Global Data
    private String emailCode = "";  // 이메일 인증 번호
    private boolean changePasswordFlag = false; // 비밀번호 입력 조건 만족 여부


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit_lost_password);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        UPDATE_PASSWORD_PATH = ((globalMethod) getApplication()).getUpdateUserPasswordPath();    // 비밀번호 변경 Rest API

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // Init View
        initView();


        // 인증 받을 메일 SET
        authEmail.setText(User.getString("userEmail", ""));

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> {
            // 타이머가 작동중이라면 종료 후 실행
            if(authNumberTimer.isCertification()){
                authNumberTimer.stopAnimator();
            }

            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(MyPageEditLostPasswordActivity.this, MyPageEditPasswordActivity.class);

            setResult(2000, intent);    // 결과 코드와 intent 값 전달
            finish();
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
                // 인증 번호 요청 버튼 활/비활성화
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
                // 인증 번호 입력 버튼 활/비활성화
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우
                    authNumberBtn.setBackgroundResource(R.drawable.round_green_10dp);
                    authNumberBtn.setEnabled(true);
                }else{
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
            if(isValidEmail(authEmail.getText().toString())){
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
                    sendEmail(authEmail.getText().toString());    // 인증 메일 전송
                }, 3000);

            }else{
                StyleableToast.makeText(getApplicationContext(), "이메일의 형식이 올바른지 확인해주세요.", R.style.redToast).show();
            }
        });

        // 메일 인증 확인 버튼 클릭 리스너
        authNumberBtn.setOnClickListener(view -> {
            // 타이머가 진행중이고 인증 코드를 정확이 입력했다면
            if(authNumberTimer.isCertification()){
                if(emailCode.equals(authNumber.getText().toString())){
                    changePasswordLayout.setVisibility(View.VISIBLE);   // 비밀 번호 전체 레이아웃 표시
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

        // 변경할 비밀번호 입력 텍스트 감지 리스너
        changePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우

                    // 비밀번호 조건을 만족하는지, 현재 비밀번호를 제대로 입력했는지 확인
                    // 변경 버튼 활성화 여부 변경
                    if(pwdRegularExpressionChk(charSequence.toString())){   // 비밀번호 정규식에 부합 하는지
                        changePasswordFlag = true;  // 비밀번호 정규식 부합하는지 Flag
                        changePasswordTxt.setTextColor(Color.parseColor("#5E5E5E"));

                        if(charSequence.toString().equals(changePasswordOnce.getText().toString())){
                            changePasswordOnceTxt.setVisibility(View.GONE); // 비밀번호 일치 안내 텍스트 숨김

                            // 변경 버튼 활성화
                            changePasswordBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            changePasswordBtn.setEnabled(true);
                        }else{
                            changePasswordOnceTxt.setVisibility(View.VISIBLE);  // 비밀번호 일치 안내 텍스트 표시

                            // 변경 버튼 비활성화
                            changePasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            changePasswordBtn.setEnabled(false);
                        }

                    }else{
                        changePasswordOnceTxt.setVisibility(View.VISIBLE);  // 비밀번호 일치 안내 텍스트 숨김


                        changePasswordTxt.setTextColor(Color.parseColor("#EF3560"));    // 비밀번호 정규식 부합 여부 빨간 텍스트

                        // 변경 버튼 비활성화
                        changePasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        changePasswordBtn.setEnabled(false);

                        changePasswordFlag = false; // 비밀번호 정규식 부합하는지 Flag
                    }
                }else{
                    changePasswordFlag = false; // 비밀번호 정규식 부합하는지 Flag
                    changePasswordTxt.setTextColor(Color.parseColor("#EF3560"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 변경할 비밀번호 재입력 텍스트 감지 리스너
        changePasswordOnce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(charSequence.toString().length() > 0){   // 입력된 텍스트가 있을 경우

                    // 입력한 비밀번호와 동일한지 확인
                    // 변경 버튼 활성화 여부 변경
                    if(changePassword.getText().toString().equals(charSequence.toString())){    // 비밀번호 입력과 재입력 일치하는지 확인
                        changePasswordOnceTxt.setVisibility(View.GONE); // 비밀번호 일치 안내 텍스트 숨김

                        if(changePasswordFlag){ // 비밀번호 입력이 정규식에 일치하는지

                            // 변경 버튼 활성화
                            changePasswordBtn.setBackgroundResource(R.drawable.round_green_30dp);
                            changePasswordBtn.setEnabled(true);
                        }else{
                            // 변경 버튼 비활성화
                            changePasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                            changePasswordBtn.setEnabled(false);
                        }

                    }else{
                        changePasswordOnceTxt.setVisibility(View.VISIBLE);  // 비밀번호 일치 안내 텍스트 표시

                        // 변경 버튼 비활성화
                        changePasswordBtn.setBackgroundResource(R.drawable.round_gray_30dp);
                        changePasswordBtn.setEnabled(false);
                    }
                }else{
                    changePasswordOnceTxt.setVisibility(View.GONE); // 비밀번호 일치 안내 텍스트 숨김
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        // 비밀번호 변경 버튼 리스너
        changePasswordBtn.setOnClickListener(view -> {
            // POST 방식 파라미터 설정
            // Param => userId : 변경할 유저 고유 아이디
            //          password : 변경할 패스워드
            Map<String, String> param = new HashMap<>();
            param.put("userId", String.valueOf(User.getInt("userId", 0)));     // 변경할 유저 고유 아이디
            param.put("password", getHash(changePassword.getText().toString()));    // 변경할 패스워드

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

                        autoLoginEdit.putString("userPassword", getHash(changePassword.getText().toString()));  // 유저 비밀번호

                        autoLoginEdit.apply();  // 데이터를 저장

                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(MyPageEditLostPasswordActivity.this, MyPageEditPasswordActivity.class);

                        setResult(1000, intent);    // 결과 코드와 intent 값 전달
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
    }


    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // 타이머가 작동중이라면 종료 후 실행
            if(authNumberTimer.isCertification()){
                authNumberTimer.stopAnimator();
            }

            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(MyPageEditLostPasswordActivity.this, MyPageEditPasswordActivity.class);

            setResult(2000, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_lost_password_back_ic);  // 상단 뒤로가기 버튼
        authEmail = findViewById(R.id.mypage_edit_auth_email);      // 인증 받을 메일 입력
        authNumber = findViewById(R.id.mypage_edit_auth_number);    // 인증 번호 입력
        changePassword = findViewById(R.id.mypage_edit_change_password);    // 변경할 비밀 번호 입력
        changePasswordOnce = findViewById(R.id.mypage_edit_change_password_once);   // 변경할 비밀 번호 재입력
        authEmailBtn = findViewById(R.id.mypage_edit_auth_mail_btn);    // 인증 메일 요청 버튼
        authNumberBtn = findViewById(R.id.mypage_edit_auth_number_btn); // 인증 번호 입력 버튼
        changePasswordTxt = findViewById(R.id.mypage_edit_change_password_txt); // 변경할 비밀 번호 입력 안내 텍스트
        changePasswordOnceTxt = findViewById(R.id.mypage_edit_change_password_once_txt);    // 변경할 비밀 번호 재입력 안내 텍스트
        authNumberTimer = findViewById(R.id.mypage_edit_auth_number_timer);     // 인증 타이머
        authNumberLayout = findViewById(R.id.mypage_edit_auth_number_layout);   // 인증 번호 입력 전체 레이아웃
        changePasswordLayout = findViewById(R.id.mypage_edit_change_password_layout);   // 비밀 번호 변경 전체 레이아웃
        changePasswordBtn = findViewById(R.id.mypage_edit_change_password_btn); // 비밀 번호 변경 버튼
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
        String hashStr; // 해시 암호화 문자열
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
        boolean chk = false;    // 정규화 만족 여부

        // 특수문자, 영문, 숫자 조합 (8 ~ 20자리)
        match = Pattern.compile(pattern1).matcher(newPwd);

        if(match.find()) {
            chk = true;
        }

        return chk;
    }
}

