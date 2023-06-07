package com.example.itda.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
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
import com.example.itda.ui.mypage.MyPageEditBirthdayActivity;
import com.example.itda.ui.mypage.MyPageEditNameActivity;
import com.example.itda.ui.mypage.MyPageEditNumberActivity;
import com.example.itda.ui.mypage.MyPageEditPasswordActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class LoginMembershipOptionActivity extends AppCompatActivity{
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private ImageButton userProfile;    // 유저 프로필 이미지

    private EditText userName;    // 유저 명 입력
    private EditText userNumber;  // 유저 번호 입력

    private NumberPicker userBirthdayYear;  // 유저 생일 년 입력
    private NumberPicker userBirthdayMonth;  // 유저 생일 월 입력
    private NumberPicker userBirthdayDay;  // 유저 생일 일 입력

    private Button membershipOptionBtn;    // 회원가입 정보 입력 완료 버튼

    private Dialog userProfileDialog;   // 유저 프로필 변경 팝업 다이얼로그
    private Dialog optionInputCancelDialog;   // 회원가입 추가정보 입력 취소 팝업 다이얼로그

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue

    private String UPDATE_PROFILE_PATH; // 유저 프로필 변경 Rest API
    private String DELETE_PROFILE_PATH; // 유저 프로필 기본 이미지 변경 Rest API
    private String NO_PROFILE_PATH; // 기본 프로필 이미지 경로
    private String UPDATE_LOGIN_USER_OPTION; // 유저 회원가입 추가 정보 Update Rest API

    private String HOST;    // Host 정보

    private final Handler handler = new Handler();

    private int userId = 0;
    private String userEmail = "";
    private String loginFlag = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_membership_option);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        UPDATE_PROFILE_PATH = ((globalMethod) getApplication()).getUpdateUserProfilePath();    // 유저 프로필 변경 Rest API
        DELETE_PROFILE_PATH = ((globalMethod) getApplication()).getDeleteUserProfilePath();    // 유저 프로필 기본 이미지 변경 Rest API
        NO_PROFILE_PATH = ((globalMethod) getApplication()).getNoProfilePath();    // 기본 프로필 이미지 경로
        UPDATE_LOGIN_USER_OPTION = ((globalMethod) getApplication()).updateLoginUserOptionPath();    // 유저 회원가입 추가 정보 update Rest API

        userId = getIntent().getExtras().getInt("userId");    // 회원가입된 유저 고유 아이디
        loginFlag = getIntent().getExtras().getString("loginFlag"); // 회원가입 방법 ( 일반 회원가입 : normal, 카카오 회원가입 : kakao )

        initView(); // 뷰 생성

        // --------------- Data SET ---------------------

        // 연, 월, 일 최대, 최소값 설정
        userBirthdayYear.setMinValue(1940);
        userBirthdayYear.setMaxValue(2020);
        userBirthdayYear.setValue(2000);

        userBirthdayMonth.setMinValue(1);
        userBirthdayMonth.setMaxValue(12);
        userBirthdayMonth.setValue(1);

        userBirthdayDay.setMinValue(1);
        userBirthdayDay.setMaxValue(31);
        userBirthdayDay.setValue(1);

        // 유저 프로필 이미지 ( 기본 이미지로 초기화 )
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(HOST + NO_PROFILE_PATH))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(userProfile);      // 이미지를 보여줄 View를 지정

        // 회원가입이 카카오로 되었을 경우 정보 SET
        if(loginFlag.equals("kakao")){
            userEmail = getIntent().getExtras().getString("email"); // 유저 이메일

            userName.setText(getIntent().getExtras().getString("name"));    // 유저 명
            // 생일 월, 일
            if(!getIntent().getExtras().getString("birthday").isEmpty()){
                userBirthdayMonth.setValue(Integer.parseInt(getIntent().getExtras().getString("birthday").split("-")[1]));
                userBirthdayDay.setValue(Integer.parseInt(getIntent().getExtras().getString("birthday").split("-")[2]));
            }

            // 유저 프로필
            if(!getIntent().getExtras().getString("userProfileURL").isEmpty()){
                // 유저 프로필 이미지 기본 이미지로 변경
                Glide.with(getApplicationContext())                 // View, Fragment 혹은 Activity로부터 Context를 GET
                        .load(getIntent().getExtras().getString("userProfileURL"))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                        .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                        .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                        .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                        .into(userProfile);      // 이미지를 보여줄 View를 지정
            }
        }

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> {
            WindowManager.LayoutParams params = optionInputCancelDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            optionInputCancelDialog.getWindow().setAttributes(params);

            optionInputCancelDialog.show();

            // 뷰 정의
            TextView dialogTitle = optionInputCancelDialog.findViewById(R.id.dl_title);  // 다이얼로그 안내 텍스트
            Button inputCancelConfirmBtn = optionInputCancelDialog.findViewById(R.id.dl_confirm_btn);  // 확인 버튼
            Button inputCancelCloseBtn = optionInputCancelDialog.findViewById(R.id.dl_close_btn);  // 닫기 버튼

            dialogTitle.setText("추가정보를 입력하지 않고 종료하겠습니까?");

            // 확인 버튼 클릭 리스너
            inputCancelConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionInputCancelDialog.dismiss();
                    updateUserOption(userName.getText().toString()
                            , ""
                            , "1900-01-01");
                }
            });

            // 닫기 버튼
            inputCancelCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionInputCancelDialog.dismiss();
                }
            });
        });

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){  // 갤러리에서 프로필 변경
                Intent intent = result.getData();
                Uri uri = intent.getData(); // 선택한 갤러리 URI 정보

                // 커서란?
                // ContentResolver.query() 클라이언트 메서드는 언제나 쿼리 선택 기준과 일치하는 행에 대해 쿼리 프로젝션이 지정한 열을 포함하는 Cursor를 반환
                // 데이터베이스 쿼리에서 반환된 결과 테이블의 행들을 가르키는 것
                // 이 인터페이스는 데이터베이스 쿼리에서 반환된 결과 집합에 대한 임의의 읽기-쓰기 액세스를 제공
                Cursor cursor = getContentResolver().query(
                        uri     // Uri : 찾고자하는 데이터의 Uri, 접근할 앱에서 정의, 내 앱에서 만들고 싶다면 manifest에서 만들 수 있음
                        , null  // Projection : 일반적인 DB의 column와 같음, 결과로 받고 싶은 데이터의 종류를 알려줌
                        , null  // Selection : DB의 where 키워드와 같음, 어떤 조건으로 필터링된 결과를 받을 때 사용
                        , null  // Selection args : Selection과 함께 사용됨, SELECT 절에 있는 ? 자리표시자를 대체
                        , null  // SortOrder : 쿼리 결과 데이터를 sorting할 때 사용( 반환된 Cursor 내에 행이 나타나는 순서를 지정 )
                );

                cursor.moveToNext();    // Cursor를 다음 행(Row)으로 이동

                String filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));  // 파일의 경로
                String fileSize = cursor.getString(cursor.getColumnIndexOrThrow("_size"));  // 파일 크기 ( 단위 : 바이트 )

                // 파일명 + 확장자
                String[] fileNameEts = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)).split("\\.");

                String fileName = fileNameEts[0]; // 파일 명
                String fileEts = fileNameEts.length > 1 ? fileNameEts[1] : "";  // 파일 확장자

                File file = new File(filePath);

                // 안드로이드에서 네트워크와 관련된 작업을 할 때,
                // 반드시 메인 Thread가 아닌 별도의 작업 Thread를 생성하여 작업해야 함
                NThread nThread = new NThread(file, fileName, fileEts, Double.parseDouble(fileSize) / 1024);
                nThread.start();    // Thread 실행

                cursor.close(); // 커서 종료

                userProfile.setImageURI(uri);   // 프로필 이미지 지정

                userProfileDialog.dismiss();    // Dialog 닫기
            }
        });

        // 유저 휴대폰 번호 입력 EditText 엔터키 입력 리스너
        userNumber.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            switch(actionId){
                case EditorInfo.IME_ACTION_DONE:
                    // 키보드 내리기
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
            }
            return true;
        });

        // 연도 변경 리스너 ( 말일 계산 )
        userBirthdayYear.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            Calendar cal = Calendar.getInstance();  // Calendar 인스턴스를 생성
            cal.set(newValue,userBirthdayMonth.getValue() - 1,1);   // 월 부분은 -1

            userBirthdayDay.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));   // getActualMaximum 함수를 호출하면 기준이된 월의 말일 계산
        });

        // 월 변경 리스너 ( 말일 계산 )
        userBirthdayMonth.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            Calendar cal = Calendar.getInstance();  // Calendar 인스턴스를 생성
            cal.set(userBirthdayYear.getValue(),newValue - 1,1);    // 월 부분은 -1

            userBirthdayDay.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));   // getActualMaximum 함수를 호출하면 기준이된 월의 말일 계산
        });

        // 유저 프로필 이미지 변경 버튼 클릭 리스너
        userProfile.setOnClickListener(view -> checkPermissions());

        // 회원가입 추가 정보 입력 완료 버튼 클릭 리스너
        membershipOptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(userName.getText().toString())){
                    StyleableToast.makeText(getApplicationContext(), "이름을 입력해 주세요.", R.style.redToast).show();
                }else if(TextUtils.isEmpty(userNumber.getText().toString())){
                    StyleableToast.makeText(getApplicationContext(), "휴대폰 번호를 입력해 주세요.", R.style.redToast).show();
                }else{
                    updateUserOption(userName.getText().toString()  // 유저 명
                            , userNumber.getText().toString()   // 유저 휴대폰 번호
                            , userBirthdayYear.getValue() + "-" + userBirthdayMonth.getValue() + "-" + userBirthdayDay.getValue()); // 유저 생일
                }
            }
        });
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            WindowManager.LayoutParams params = optionInputCancelDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            optionInputCancelDialog.getWindow().setAttributes(params);

            optionInputCancelDialog.show();

            // 뷰 정의
            TextView dialogTitle = optionInputCancelDialog.findViewById(R.id.dl_title);  // 다이얼로그 안내 텍스트
            Button inputCancelConfirmBtn = optionInputCancelDialog.findViewById(R.id.dl_confirm_btn);  // 확인 버튼
            Button inputCancelCloseBtn = optionInputCancelDialog.findViewById(R.id.dl_close_btn);  // 닫기 버튼

            dialogTitle.setText("추가정보를 입력하지 않고 종료하겠습니까?");

            // 확인 버튼 클릭 리스너
            inputCancelConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionInputCancelDialog.dismiss();
                    updateUserOption(userName.getText().toString()
                            , ""
                            , "1900-01-01");
                }
            });

            // 닫기 버튼
            inputCancelCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    optionInputCancelDialog.dismiss();
                }
            });

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.login_membership_option_back_ic); // 상단 뒤로가기 버튼
        userProfile = findViewById(R.id.login_membership_option_user_profile);  // 유저 프로필 이미지
        userName = findViewById(R.id.login_membership_option_user_name);  // 유저 명 입력
        userNumber = findViewById(R.id.login_membership_option_user_number);  // 유저 번호 입력
        userBirthdayYear = findViewById(R.id.login_membership_option_birthday_year);  // 유저 생일 년 입력
        userBirthdayMonth = findViewById(R.id.login_membership_option_birthday_month);  // 유저 생일 월 입력
        userBirthdayDay = findViewById(R.id.login_membership_option_birthday_day);  // 유저 생일 일 입력
        membershipOptionBtn = findViewById(R.id.login_membership_option_btn);  // 회원가입 정보 입력 완료 버튼

        userNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // 프로필 변경 Dialog 팝업
        userProfileDialog = new Dialog(LoginMembershipOptionActivity.this);  // Dialog 초기화
        userProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        userProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        userProfileDialog.getWindow().setGravity(Gravity.BOTTOM);   // Dialog 하단 위치
        userProfileDialog.setContentView(R.layout.dl_mypage_profile); // xml 레이아웃 파일과 연결

        // 회원가입 추가정보 입력 취소 팝업
        optionInputCancelDialog = new Dialog(LoginMembershipOptionActivity.this);  // Dialog 초기화
        optionInputCancelDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        optionInputCancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        optionInputCancelDialog.setContentView(R.layout.dl_delete); // xml 레이아웃 파일과 연결
    }

    // 갤러리 접근 권한 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    // 갤러리 접근 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Log.d("uploadStart", "------------------ Authentication Success! ------------------");

            WindowManager.LayoutParams params = userProfileDialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            userProfileDialog.getWindow().setAttributes(params);

            userProfileDialog.show();

            // 뷰 정의
            Button profileGalleryBtn = userProfileDialog.findViewById(R.id.mypage_edit_user_profile_gallery_btn);  // 갤러리 변경 버튼
            Button profileBasicBtn = userProfileDialog.findViewById(R.id.mypage_edit_user_profile_basic_btn);  // 기본 이미지 변경 버튼
            Button profileCloseBtn = userProfileDialog.findViewById(R.id.mypage_edit_user_profile_close_btn);  // 닫기 버튼

            // 갤러리 변경 버튼 클릭 리스너
            profileGalleryBtn.setOnClickListener(view -> {
                Intent intent = new Intent(Intent.ACTION_PICK);

                // 갤러리로 화면 전환
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.setAction(Intent.ACTION_PICK);
                activityResultLauncher.launch(intent);
            });

            // 기본 이미지 변경 버튼
            profileBasicBtn.setOnClickListener(view -> {
                deleteProfile();    // 프로필 삭제

                // 유저 프로필 이미지 기본 이미지로 변경
                Glide.with(getApplicationContext())                 // View, Fragment 혹은 Activity로부터 Context를 GET
                        .load(Uri.parse(HOST + NO_PROFILE_PATH))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                        .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                        .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                        .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                        .into(userProfile);      // 이미지를 보여줄 View를 지정

                userProfileDialog.dismiss();    // Dialog 닫기
            });

            // 닫기 버튼
            profileCloseBtn.setOnClickListener(view -> userProfileDialog.dismiss());
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            StyleableToast.makeText(getApplicationContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", R.style.redToast).show();
        }
    };

    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        File filePath;  // 파일 내용
        String fileName;    // 파일 명
        String fileEts;     // 파일 확장자
        Double fileSize;    // 파일 크기
        public NThread(File file, String name, String ets, double size) {
            filePath = file;
            fileName = name;
            fileEts = ets;
            fileSize = size;
        }
        @Override

        public void run() {
            upload(filePath, fileName, fileEts, fileSize);
        }

        public void upload(File file, String name, String ets, double size){
            // Upload file
            uploadFile(file, name, ets, size);
        }
    }

    public void uploadFile(File file, String name, String ets, double size){

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

            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateProfile(name, ets, size);
                    //StyleableToast.makeText(getApplicationContext(), "변경 완료", R.style.blueToast).show();
                }
            });

        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StyleableToast.makeText(getApplicationContext(), "변경 실패", R.style.blueToast).show();
                }
            });

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
    public void updateProfile(String name, String ets, double size){
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 변경할 유저 고유 아이디
        param.put("fileName", name);    // 파일 명
        param.put("fileEts", ets);      // 파일 확장자
        param.put("fileSize", String.valueOf(size));    // 파일 크기

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest updateProfileRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_PROFILE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(getApplicationContext(), "변경 성공!", R.style.blueToast).show();

                }else{
                    StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("updateUserProfileError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        updateProfileRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(updateProfileRequest);      // RequestQueue에 요청 추가
    }

    // 프로필 이미지 기본 이미지로 변경
    public void deleteProfile(){
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 변경할 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteProfileRequest = new StringRequest(Request.Method.POST, HOST + DELETE_PROFILE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(getApplicationContext(), "변경 성공!", R.style.blueToast).show();

                }else{
                    StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteUserProfileError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteProfileRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteProfileRequest);      // RequestQueue에 요청 추가
    }

    // 회원가입 추가 정보 입력 Update
    public void updateUserOption(String userName, String userNumber, String userBirthday){
        Map<String, String> param = new HashMap<>();
        param.put("userId", String.valueOf(userId));   // 변경할 유저 고유 아이디
        param.put("userName", userName);    // 유저 이름
        param.put("userNumber", userNumber);      // 유저 휴대폰 번호
        param.put("userBirthday", userBirthday);   // 유저 생일

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest updateUserOptionRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_LOGIN_USER_OPTION, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(getApplicationContext(), "회원가입 완료!", R.style.blueToast).show();

                    if(loginFlag.equals("normal")){ // 일반 회원가입
                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(LoginMembershipOptionActivity.this, LoginMembershipActivity.class);

                        setResult(1000, intent);    // 결과 코드와 intent 값 전달
                        finish();
                    }else{  // 카카오 회원가입
                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(LoginMembershipOptionActivity.this, LoginActivity.class);

                        intent.putExtra("email", userEmail);
                        setResult(2000, intent);    // 결과 코드와 intent 값 전달
                        finish();
                    }

                }else{
                    StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("updateUserOptionError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        updateUserOptionRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(updateUserOptionRequest);      // RequestQueue에 요청 추가
    }
}

