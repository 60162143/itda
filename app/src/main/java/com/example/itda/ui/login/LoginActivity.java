package com.example.itda.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
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
import com.bumptech.glide.Glide;
import com.example.itda.BuildConfig;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {

    // Layout
    private TextView LoginPasswordSearchBtn;    // 비밀번호 찾기
    private TextView LoginMembershipBtn;    // 회원가입
    private EditText LoginEmailEt;      // 이메일 입력
    private EditText LoginPasswordEt;   // 비밀번호 입력
    private Button LoginBtn;    // 로그인 버튼
    private ImageButton LoginKakaoBtn;  // 카카오 로그인 버튼


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성


    // Volley Library RequestQueue
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue


    // Rest API
    private String LOGIN_PATH;  // 로그인 정보 데이터 조회 Rest API
    private String GET_USER_ID_PATH;    // 이메일로 유저 고유 아이디 조회 Rest API
    private String INSERT_KAKAO_USER_PATH;  // 유저 카카오 회원가입 Rest API
    private String UPDATE_PROFILE_PATH; // 유저 프로필 변경 Rest API
    private String HOST;    // Host 정보


    // Thread Handler
    private final Handler handler = new Handler();


    // Global Data
    private static final String TAG = "KakaoLogin";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        // BuildConfig
        // 변수 선언 -> build.gradle의 defaultConfig { 안에 } 사용
        // gradle 설정값을 java 코드로 접근할 수 있게 하는 클래스
        // build.gradle 변경 후 sync시에도 BuildConfig에 반영되지 않을 경우 Rebuild Project 한 번 수행 후 재확인

        // 카카오 sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_LOGIN_API_KEY);

        HOST = ((globalMethod) getApplication()).getHost(); // Host 정보
        LOGIN_PATH = ((globalMethod) getApplication()).getLoginPath();  // 로그인 정보 데이터 조회 Rest API
        GET_USER_ID_PATH = ((globalMethod) getApplication()).getLoginUserIdPath();  // 이메일로 유저 고유 아이디 조회 Rest API
        INSERT_KAKAO_USER_PATH = ((globalMethod) getApplication()).insertKakaoLoginUserPath();  // 유저 카카오 회원가입 Rest API
        UPDATE_PROFILE_PATH = ((globalMethod) getApplication()).getUpdateUserProfilePath();     // 유저 프로필 변경 Rest API

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000으로 넘어왔다면 일반 회원가입 완료
                Log.d("msg", "normal membership Success!!");
            }else if(result.getResultCode() == 2000){ // resultCode가 1000으로 넘어왔다면 카카오 회원가입 완료
                Log.d("msg", "kakao membership Success!!");
                assert result.getData() != null;
                String userEmail = result.getData().getStringExtra("email");
                getLoginUser(userEmail, "", "kakao"); // 로그인 User GET
            }else if(result.getResultCode() == 3000){ // resultCode가 1000으로 넘어왔다면 비밀번호 변경 완료
                Log.d("msg", "Password Change Success!!");
            }else if(result.getResultCode() == 4000){ // resultCode가 1000으로 넘어왔다면 일반 종료
                Log.d("msg", "Close Page");
            }
        });

        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        // 콜백 메서드
        Function2<OAuthToken,Throwable, Unit> callback = (oAuthToken, throwable) -> {
            Log.e(TAG,"CallBack Method");
            //oAuthToken != null 이라면 로그인 성공
            if(oAuthToken!=null){
                // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                updateKakaoLoginUi();

            }else {
                //로그인 실패
                Log.e(TAG, "invoke: login fail" );
            }

            return null;
        };

        initView(); // 뷰 생성

        // 카카오 로그인 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)    // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(R.drawable.kakao_talk)    // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error_black_36dp)     // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(LoginKakaoBtn);   // 이미지를 보여줄 View를 지정

        // 로그인 버튼 클릭 리스너
        LoginBtn.setOnClickListener(v -> {
            String loginEmail = LoginEmailEt.getText().toString();  // 입력 이메일
            String loginPassword = getHash(LoginPasswordEt.getText().toString());   // 입력 비밀번호 ( SHA-256 해시 암호화 알고리즘 사용 )

            getLoginUser(loginEmail, loginPassword, "normal");  // 로그인 User GET
        });

        // 비밀번호 찾기 클릭 리스너
        LoginPasswordSearchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginLostPasswordActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 회원가입 클릭 리스너
        LoginMembershipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, LoginMembershipActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 이메일 EditText 엔터키 입력 리스너
        LoginEmailEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // 이메일 입력이 비어있는지 확인
                if (!TextUtils.isEmpty(LoginEmailEt.getText().toString())) {
                    LoginPasswordEt.requestFocus();    // 비밀번호 입력 포커스
                } else {
                    StyleableToast.makeText(this, "이메일을 입력해 주세요.", R.style.orangeToast).show();
                }
            }
            return true;
        });

        // 비밀번호 EditText 엔터키 입력 리스너
        LoginPasswordEt.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 이메일과 비밀번호 입력이 비어있는지 확인
                if (!TextUtils.isEmpty(LoginEmailEt.getText().toString()) && !TextUtils.isEmpty(LoginPasswordEt.getText().toString())) {
                    LoginBtn.callOnClick(); // 로그인 버튼 클릭
                } else {
                    StyleableToast.makeText(this, "이메일 또는 비밀번호를 입력해 주세요.", R.style.orangeToast).show();
                }
            }
            return true;
        });

        // 카카오 소셜 로그인 버튼 클릭 리스너
        LoginKakaoBtn.setOnClickListener(view -> {
            // 해당 기기에 카카오톡이 설치되어 있는 확인
            if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this)){
                UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this, callback);
            }else{
                // 카카오톡이 설치되어 있지 않다면

                System.out.println("여기 아닌가?");
                UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this, callback);
            }
        });
    }

    // 뷰 생성
    private void initView() {
        LoginPasswordSearchBtn = findViewById(R.id.login_password_search_bt);
        LoginMembershipBtn = findViewById(R.id.login_membership_bt);
        LoginEmailEt = findViewById(R.id.login_email_et);
        LoginEmailEt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        LoginPasswordEt = findViewById(R.id.login_password_et);
        LoginBtn = findViewById(R.id.login_enter_bt);
        LoginKakaoBtn = findViewById(R.id.login_kakao_logo_btn);
    }

    // 유저 이메일이 DB에 있는지 확인
    private void getLoginUser(String email, String password, String loginFlag) {
        // POST 방식 파라미터 설정
        // Param => email : 로그인 이메일
        //          password : 로그인 비밀번호
        //          loginFlag : 로그인 방법 ( 일반 로그인 : normal, 카카오 로그인 : kakao )
        Map<String, String> param = new HashMap<>();
        param.put("email", email);
        param.put("password", password);
        param.put("loginFlag", loginFlag);

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest loginRequest = new StringRequest(Request.Method.POST, HOST + LOGIN_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray userArr = jsonObject.getJSONArray("user");    // 객체에 user라는 Key를 가진 JSONArray 생성

                if(userArr.length() > 0) {
                    JSONObject object = userArr.getJSONObject(0);   // JSONObject 생성

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
                    autoLoginEdit.putString("userEmail", object.getString("userEmail"));        // 유저 이메일
                    autoLoginEdit.putString("userPassword", object.getString("userPassword"));  // 유저 비밀번호
                    autoLoginEdit.putString("userProfileImage", HOST + object.getString("userProfileImage"));   // 유저 프로필 이미지
                    autoLoginEdit.putString("userNumber", object.getString("userNumber"));  // 유저 번호
                    autoLoginEdit.putString("userName", object.getString("userName"));      // 유저 이름
                    autoLoginEdit.putString("userBirthday", object.getString("userBirthday"));  // 유저 생일
                    autoLoginEdit.putInt("userLoginFlag", object.getInt("userLoginFlag"));  // 유저 로그인 방식

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
            protected Map<String, String> getParams(){
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        loginRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(loginRequest);      // RequestQueue에 요청 추가
    }

    // *** 문자열 해시 암호화 코드 ***
    private static String getHash(String str) {
        String hashStr; // 해시 암호화 된 문자열
        try{
            //암호화
            MessageDigest sh = MessageDigest.getInstance("SHA-256");    // SHA-256 해시함수를 사용
            sh.update(str.getBytes());  // str의 문자열을 해싱하여 sh에 저장
            byte[] data = sh.digest();  // sh 객체의 다이제스트를 얻는다.

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

    // 카카오 로그인 데이터 GET
    private void updateKakaoLoginUi() {
        // 로그인 여부에 따른 UI 설정
        UserApiClient.getInstance().me((user, throwable) -> {
            if (user != null) {
                // 유저 고유 아이디 SET
                String userId = user.getId() != null ? String.valueOf(user.getId()) : "";

                // 유저 명 SET
                assert Objects.requireNonNull(user.getKakaoAccount()).getProfile() != null;
                String userName = user.getKakaoAccount().getProfile().getNickname() != null ? user.getKakaoAccount().getProfile().getNickname() : "";

                // 유저 생일 SET ( 연도는 값이 없기 때문에 2000년으로 초기화 )
                String userBirthday;
                if(user.getKakaoAccount().getBirthday() != null){
                    userBirthday = "2000-" + user.getKakaoAccount().getBirthday().substring(0, 2) + "-" + user.getKakaoAccount().getBirthday().substring(2);
                }else{
                    userBirthday = "2000-01-01";
                }

                String userProfileURL;   // 유저의 프로필 URL

                if(user.getKakaoAccount().getProfile().getThumbnailImageUrl() != null){ // 프로필 이미지 있을 경우
                    // 자바에서 람다식과 inner class에서는 final 변수 또는 effectively final 변수만 접근 가능
                    userProfileURL = user.getKakaoAccount().getProfile().getThumbnailImageUrl();
                    String finalUserProfileURL = userProfileURL;

                    // URL to FILE 변환
                    // EXTERNAL_STORAGE 사용
                    new Thread(() -> {
                        try {
                            InputStream inputStream = new URL(user.getKakaoAccount().getProfile().getThumbnailImageUrl()).openStream();

                            File file = new File(getFilesDir(), String.valueOf(user.getId()));

                            long bytes = 0; // 파일 크기

                            // SDK VERSION 에 따라서 path Library 사용 가능 여부 나뉨
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                OutputStream out;
                                out = Files.newOutputStream(file.toPath());
                                writeFile(inputStream, out);
                                out.close();

                                Path path = Paths.get(file.getPath());

                                bytes = Files.size(path);
                            }

                            // 카카오 회원가입이 이미 되어 있는지 확인
                            getUserId(userId, userName, userBirthday, finalUserProfileURL, file, file.getName(), "jpg", (double) bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                }else{  // 프로필 이미지 없는 경우
                    // 카카오 회원가입이 이미 되어 있는지 확인
                    getUserId(userId, userName, userBirthday, "", null, "", "", 0);
                }
            } else {
                Log.d(TAG, "아예 비었음...");
            }
            return null;
        });
    }

    // File Write
    public void writeFile(InputStream is, OutputStream os) throws IOException
    {
        int c;
        while((c = is.read()) != -1)
            os.write(c);
        os.flush();
    }

    // 카카오 로그인 회원가입
    private void insertKakaoUser(String id
            , String name
            , String birthday
            , String userProfileURL
            , File userProfile
            , String userProfileName
            , String userProfileEts
            , double userProfileSize){
        // POST 방식 파라미터 설정
        // Param => email : 회원가입 이메일 ( 카카오 고유 아이디로 insert )
        //          name : 회원가입 유저 이름
        //          birthday : 회원가입 유저 생일
        Map<String, String> param = new HashMap<>();
        param.put("email", id); // 회원가입 이메일 ( 카카오 고유 아이디로 insert )
        param.put("name", name);   // 회원가입 유저 이름
        param.put("birthday", birthday);  // 회원가입 유저 생일

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertKakaoUserRequest = new StringRequest(Request.Method.POST, HOST + INSERT_KAKAO_USER_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");   // Success Flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(getApplicationContext(), "회원가입 성공!", R.style.blueToast).show();

                    int userId = Integer.parseInt(jsonObject.getString("userId"));  // 로그인 유저 고유 아이디

                    // 프로필이 있을 경우 이미지 업로드
                    if(!userProfileName.equals("")){
                        // 안드로이드에서 네트워크와 관련된 작업을 할 때,
                        // 반드시 메인 Thread가 아닌 별도의 작업 Thread를 생성하여 작업해야 함
                        NThread nThread = new NThread(userId, userProfile, userProfileName, userProfileEts, userProfileSize / 1024);
                        nThread.start();    // Thread 실행
                    }

                    Intent intent = new Intent(LoginActivity.this, LoginMembershipOptionActivity.class);
                    intent.putExtra("userId", userId);  // 회원가입된 유저 고유 아이디
                    intent.putExtra("loginFlag", "kakao");  // 카카오 회원가입 flag
                    intent.putExtra("email", id);   // 카카오 회원가입 유저 이메일
                    intent.putExtra("name", name);  // 회원가입된 유저 명
                    intent.putExtra("birthday", birthday);  // 회원가입된 유저 생일
                    intent.putExtra("userProfileURL", userProfileURL);  // 회원가입된 유저 프로필 URL

                    activityResultLauncher.launch(intent);

                }else{
                    StyleableToast.makeText(getApplicationContext(), "회원가입 실패...", R.style.redToast).show();
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

        insertKakaoUserRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertKakaoUserRequest);      // RequestQueue에 요청 추가
    }


    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        int userId;     // 파일 업로드할 유저 고유 아이디
        File filePath;  // 파일 내용
        String fileName;    // 파일 명
        String fileEts;     // 파일 확장자
        Double fileSize;    // 파일 크기

        public NThread(int userId, File file, String name, String ets, double size) {
            this.userId = userId;
            filePath = file;
            fileName = name;
            fileEts = ets;
            fileSize = size;
        }
        @Override

        public void run() {
            upload(userId, filePath, fileName, fileEts, fileSize);
        }

        public void upload(int userId, File file, String name, String ets, double size){
            // Upload file
            uploadFile(userId, file, name, ets, size);
        }
    }

    public void uploadFile(int userId, File file, String name, String ets, double size){

        FTPClient client = new FTPClient(); // FTP 업로드를 위한 객체 생성

        try {
            // BuildConfig
            // 변수 선언 -> build.gradle의 defaultConfig { 안에 } 사용
            // gradle 설정값을 java 코드로 접근할 수 있게 하는 클래스
            // build.gradle 변경 후 sync시에도 BuildConfig에 반영되지 않을 경우 Rebuild Project 한 번 수행 후 재확인

            client.connect(BuildConfig.FTP_HOST,21);    // ftp 서버와 연결, 호스트와 포트를 기입
            client.login(BuildConfig.FTP_ID, BuildConfig.FTP_PASSWORD);  // 로그인을 위해 아이디와 패스워드 기입
            client.setType(FTPClient.TYPE_BINARY);  // 2진으로 변경
            client.changeDirectory("/public_html/ftpFileStorage/"); // FTP 파일 저장 경로 입력

            client.upload(file, new MyTransferListener());  // 업로드 시작

            handler.post(() -> updateProfile(userId, name, ets, size)); // 파일 데이터 Update

        } catch (Exception e) {
            handler.post(() -> StyleableToast.makeText(getApplicationContext(), "변경 실패", R.style.blueToast).show());

            e.printStackTrace();
            try {
                client.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /*******  Used to file upload and show progress  **********/
    public class MyTransferListener implements FTPDataTransferListener {

        public void started() {

            handler.post(() -> {
                // Transfer started
                Log.d("uploadStart", "------------------ Upload Started ... ------------------");
            });
        }

        public void transferred(int length) {

            handler.post(() -> {
                // Yet other length bytes has been transferred since the last time this
                // method was called
                Log.d("uploadTransferred", "------------------ transferred ... ------------------");
            });
        }

        public void completed() {

            handler.post(() -> {
                // Transfer completed
                Log.d("uploadCompleted", "------------------ completed ... ------------------");
            });
        }

        public void aborted() {

            handler.post(() -> {
                // Transfer aborted
                Log.d("uploadAborted", "------------------ aborted ... ------------------");
            });
        }

        public void failed() {

            handler.post(() -> {
                // Transfer failed
                Log.d("uploadFailed", "------------------ failed ... ------------------");
            });
        }
    }

    // 프로필 이미지 갤러리에서 변경
    public void updateProfile(int userId, String name, String ets, double size){
        // POST 방식 파라미터 설정
        // Param => userId : 유저 고유 아이디
        //          fileName : 파일 명
        //          fileEts : 파일 확장자
        //          fileSize : 파일 크기
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 변경할 유저 고유 아이디
        param.put("fileName", name);    // 파일 명
        param.put("fileEts", ets);      // 파일 확장자
        param.put("fileSize", String.valueOf(size));    // 파일 크기

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest updateProfileRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_PROFILE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success"); // Success Flag

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    Log.d("uploadProfile", "success!!");

                }else{
                    Log.d("uploadProfile", "fail!!");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("updateUserProfileError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams(){
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        updateProfileRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(updateProfileRequest);      // RequestQueue에 요청 추가
    }

    // 유저 이메일이 DB에 있는지 확인
    private void getUserId(String id
            , String name
            , String birthday
            , String userProfileURL
            , File userProfile
            , String userProfileName
            , String userProfileEts
            , double userProfileSize) {
        // POST 방식 파라미터 설정
        // Param => userEmail : 카카오 로그인 유저 이메일 ( 카카오 로그인은 카카오 거유 아이디로 SET )
        Map<String, String> param = new HashMap<>();
        param.put("userEmail", id);   // 카카오 로그인 유저 이메일 ( 카카오 로그인은 카카오 거유 아이디로 SET )

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest getUserIdRequest = new StringRequest(Request.Method.POST, HOST + GET_USER_ID_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                // 로그인 유저 고유 아이디
                int userId = Integer.parseInt(jsonObject.getJSONArray("userId").getJSONObject(0).getString("userId"));

                // 유저 고유 아이디가 0일 경우 로그인 정보가 없음
                if(userId == 0){
                    // 카카오 로그인 회원가입 Insert
                    insertKakaoUser(id, name, birthday, userProfileURL, userProfile, userProfileName, userProfileEts, userProfileSize);
                }else{
                    getLoginUser(id, "", "kakao"); // 로그인 User GET
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
}