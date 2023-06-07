package com.example.itda.ui.mypage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallerKt;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.BuildConfig;
import com.example.itda.MainActivity;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.MainStoreRvAdapter;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.login.LoginActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class MyPageEditActivity extends AppCompatActivity{
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private ImageButton userProfile;    // 유저 프로필 이미지
    private Button userEmailBtn;    // 유저 이메일 버튼 ( 클릭 X )
    private Button userNameBtn;    // 유저 명 변경 버튼
    private Button userNumberBtn;  // 유저 번호 변경 버튼
    private Button userPasswordBtn;    // 유저 비밀번호 변경 버튼
    private Button userBirthdayBtn;    // 유저 생일 변경 버튼

    private Dialog userProfileDialog;   // 유저 프로필 변경 팝업 다이얼로그

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue

    private String UPDATE_PROFILE_PATH; // 유저 프로필 변경 Rest API
    private String DELETE_PROFILE_PATH; // 유저 프로필 기본 이미지 변경 Rest API
    private String NO_PROFILE_PATH; // 기본 프로필 이미지 경로

    private String HOST;    // Host 정보

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        UPDATE_PROFILE_PATH = ((globalMethod) getApplication()).getUpdateUserProfilePath();    // 유저 프로필 변경 Rest API
        DELETE_PROFILE_PATH = ((globalMethod) getApplication()).getDeleteUserProfilePath();    // 유저 프로필 기본 이미지 변경 Rest API
        NO_PROFILE_PATH = ((globalMethod) getApplication()).getNoProfilePath();    // 기본 프로필 이미지 경로

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> {
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(MyPageEditActivity.this, MainActivity.class);

            setResult(9002, intent);    // 결과 코드와 intent 값 전달
            finish();
        });

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000으로 넘어왔다면 이름 변경
                userNameBtn.setText(User.getString("userName", "")); // 유저 명
            }else if(result.getResultCode() == 2000){ // resultCode가 2000으로 넘어왔다면 번호 변경
                userNumberBtn.setText(User.getString("userNumber", "-")); // 유저 번호
            }else if(result.getResultCode() == 3000){ // resultCode가 3000으로 넘어왔다면 생일 변경
                userBirthdayBtn.setText(User.getString("userBirthday", "-")); // 유저 생일
            }else if(result.getResultCode() == 4000){ // resultCode가 4000으로 넘어왔다면 비밀번호 변경

            }else if(result.getResultCode() == RESULT_OK){  // 갤러리에서 프로필 변경
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

        // 유저 프로필 이미지
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(User.getString("userProfileImage", "")))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(userProfile);      // 이미지를 보여줄 View를 지정

        // Data SET
        // ---------- 일반 로그인과 카카오 로그인 분리 ----------

        // 유저 이메일
        if(User.getInt("userLoginFlag", 0) == 0){
            userEmailBtn.setText(User.getString("userEmail", ""));
            userPasswordBtn.setText("********"); // 유저 비밀번호
        }else if(User.getInt("userLoginFlag", 0) == 1){
            userEmailBtn.setText("-"); // 유저 이메일
            userPasswordBtn.setText("-"); // 유저 비밀번호
            userPasswordBtn.setEnabled(false);  // 유저 비밀번호 버튼 비활성화
        }

        userNameBtn.setText(User.getString("userName", "")); // 유저 명
        userBirthdayBtn.setText(User.getString("userBirthday", "-")); // 유저 생일
        // 유저 번호
        if(User.getString("userNumber", "-").equals("")){
            userNumberBtn.setText("-");
        }else{
            userNumberBtn.setText(User.getString("userNumber", "-"));
        }



        // 유저 프로필 이미지 변경 버튼 클릭 리스너
        userProfile.setOnClickListener(view -> checkPermissions());

        // 유저 명 클릭 변경 버튼 리스너
        userNameBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyPageEditActivity.this, MyPageEditNameActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 유저 번호 변경 버튼 클릭 리스너
        userNumberBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyPageEditActivity.this, MyPageEditNumberActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 유저 비밀번호 변경 버튼 클릭 리스너
        userPasswordBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyPageEditActivity.this, MyPageEditPasswordActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 유저 생일 변경 버튼 클릭 리스너
        userBirthdayBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MyPageEditActivity.this, MyPageEditBirthdayActivity.class);

            activityResultLauncher.launch(intent);
        });

    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
            Intent intent = new Intent(MyPageEditActivity.this, MainActivity.class);

            setResult(9002, intent);    // 결과 코드와 intent 값 전달
            finish();

            return true;
        }
        return false;
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_back_ic); // 상단 뒤로가기 버튼
        userProfile = findViewById(R.id.mypage_edit_user_profile);  // 유저 프로필 이미지
        userEmailBtn = findViewById(R.id.mypage_edit_user_email_btn);  // 유저 이메일 ( 클릭 X )
        userNameBtn = findViewById(R.id.mypage_edit_user_name_btn);  // 유저 명 변경 버튼
        userNumberBtn = findViewById(R.id.mypage_edit_user_number_btn);  // 유저 번호 변경 버튼
        userPasswordBtn = findViewById(R.id.mypage_edit_user_password_btn);  // 유저 비밀번호 변경 버튼
        userBirthdayBtn = findViewById(R.id.mypage_edit_user_birthday_btn);  // 유저 생일 변경 버튼

        // 프로필 변경 Dialog 팝업
        userProfileDialog = new Dialog(MyPageEditActivity.this);  // Dialog 초기화
        userProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        userProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        userProfileDialog.getWindow().setGravity(Gravity.BOTTOM);   // Dialog 하단 위치
        userProfileDialog.setContentView(R.layout.dl_mypage_profile); // xml 레이아웃 파일과 연결
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
        param.put("userId", String.valueOf(User.getInt("userId", 0)));   // 변경할 유저 고유 아이디
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

                    // 해당 앱의 파일에 저장되는 데이터를 다룰 수 있는 인터페이스이다.
                    // 즉, 자동로그인 기능에서 뿐만 아니라 앱을 종료하고 새로 시작했을 때 똑같이 가지고 있어야 하는 데이터가 있을 경우 넣어서 사용할 수 있다.
                    // 데이터는 해당 앱을 삭제하거나 해당 앱의 데이터를 삭제하지 않는 이상 계속 유지된다.
                    // key, value와 같은 Map 형태로 값을 넣을 수 있으며 넣을 수 있는 값의 타입은 Boolean, Float, Int, Long, String, StringSet이 있다.
                    // Activity.MODE_PRIVATE : 기본 모드로 이렇게 설정할 경우 해당 데이터는 해당 앱에서만 사용
                    SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);

                    // SharedPreferences를 가져와서 거기서 Editor 객체를 가져와 put 메소드를 사용
                    SharedPreferences.Editor autoLoginEdit = auto.edit();

                    autoLoginEdit.putString("userProfileImage", HOST + "/ftpFileStorage/" + name + "." + ets);  // 유저 프로필 이미지

                    autoLoginEdit.apply();  // 데이터를 저장
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
        param.put("userId", String.valueOf(User.getInt("userId", 0)));   // 변경할 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteProfileRequest = new StringRequest(Request.Method.POST, HOST + DELETE_PROFILE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

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

                    autoLoginEdit.putString("userProfileImage", HOST + jsonObject.getString("noProfilePath"));  // 기본 프로필 이미지

                    autoLoginEdit.apply();  // 데이터를 저장

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
}

