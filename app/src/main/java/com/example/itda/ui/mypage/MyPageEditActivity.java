package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallerKt;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.home.MainStoreRvAdapter;
import com.example.itda.ui.home.mainStoreData;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;
import it.sauronsoftware.ftp4j.FTPClient;

public class MyPageEditActivity extends AppCompatActivity{
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private ImageButton userProfile;    // 유저 프로필 이미지
    private Button userNameBtn;    // 유저 명 변경 버튼
    private Button userNumberBtn;  // 유저 번호 변경 버튼
    private Button userPasswordBtn;    // 유저 비밀번호 변경 버튼
    private Button userBirthdayBtn;    // 유저 생일 변경 버튼

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private boolean galleryPossible = false;    // 갤러리 접근 권한 가능 여부

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit);

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000){ // resultCode가 1000으로 넘어왔다면 이름 변경
                userNameBtn.setText(User.getString("userName", "")); // 유저 명
            }else if(result.getResultCode() == 2000){ // resultCode가 2000으로 넘어왔다면 번호 변경
                userNumberBtn.setText(User.getString("userNumber", "-")); // 유저 번호
            }else if(result.getResultCode() == 3000){ // resultCode가 3000으로 넘어왔다면 생일 변경
                userBirthdayBtn.setText(User.getString("userBirthday", "-")); // 유저 생일
            }else if(result.getResultCode() == RESULT_OK){
                Intent intent = result.getData();
                Uri uri = intent.getData();

                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                //String path = cursor.getString(cursor.getColumnIndex("_data"));
                String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String size = cursor.getString(cursor.getColumnIndexOrThrow("_size"));
                File file = new File(path);

                //uploadFile(file);

                System.out.println("path : " + path);
                System.out.println("size : " + size);
                cursor.close();


                userProfile.setImageURI(uri);
            }
        });

        // 유저 프로필 이미지
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(User.getString("userProfileImage", "")))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(userProfile);      // 이미지를 보여줄 View를 지정

        userNameBtn.setText(User.getString("userName", "")); // 유저 명
        userNumberBtn.setText(User.getString("userNumber", "-")); // 유저 번호
        userPasswordBtn.setText("********"); // 유저 비밀번호
        userBirthdayBtn.setText(User.getString("userBirthday", "-")); // 유저 생일

        // 유저 프로필 이미지 변경 버튼 클릭 리스너
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });

        // 유저 명 클릭 변경 버튼 리스너
        userNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageEditActivity.this, MyPageEditNameActivity.class);

                activityResultLauncher.launch(intent);
            }
        });

        // 유저 번호 변경 버튼 클릭 리스너
        userNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageEditActivity.this, MyPageEditNumberActivity.class);

                activityResultLauncher.launch(intent);
            }
        });

        // 유저 비밀번호 변경 버튼 클릭 리스너
        userPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("userPasswordBtn");
            }
        });

        // 유저 생일 변경 버튼 클릭 리스너
        userBirthdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageEditActivity.this, MyPageEditBirthdayActivity.class);

                activityResultLauncher.launch(intent);
            }
        });

    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_back_ic); // 상단 뒤로가기 버튼
        userProfile = findViewById(R.id.mypage_edit_user_profile);  // 유저 프로필 이미지
        userNameBtn = findViewById(R.id.mypage_edit_user_name_btn);  // 유저 명 변경 버튼
        userNumberBtn = findViewById(R.id.mypage_edit_user_number_btn);  // 유저 번호 변경 버튼
        userPasswordBtn = findViewById(R.id.mypage_edit_user_password_btn);  // 유저 비밀번호 변경 버튼
        userBirthdayBtn = findViewById(R.id.mypage_edit_user_birthday_btn);  // 유저 생일 변경 버튼
    }

    // 갤러리 접근 권한 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    // 갤러리 접근 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            StyleableToast.makeText(getApplicationContext(), "권한 확인!", R.style.blueToast).show();

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction(Intent.ACTION_PICK);
            activityResultLauncher.launch(intent);
            galleryPossible = true;
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            StyleableToast.makeText(getApplicationContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", R.style.redToast).show();
        }
    };
//
//    public void uploadFile(File fileName){
//
//        FTPClient client = new FTPClient();
//
//        try {
//            client.connect(FTP_HOST,21);//ftp 서버와 연결, 호스트와 포트를 기입
//            client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
//            client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
//            client.changeDirectory("uploadtest/");//서버에서 넣고 싶은 파일 경로를 기입
//
//            client.upload(fileName, new MyTransferListener());//업로드 시작
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        } catch (Exception e) {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            e.printStackTrace();
//            try {
//                client.disconnect(true);
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//
//    }
//
//    /*******  Used to file upload and show progress  **********/
//
//    public class MyTransferListener implements FTPDataTransferListener {
//
//        public void started() {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn.setVisibility(View.GONE);
//                    // Transfer started
//                    Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
//                    //System.out.println(" Upload Started ...");
//                }
//            });
//        }
//
//        public void transferred(int length) {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // Yet other length bytes has been transferred since the last time this
//                    // method was called
//                    Toast.makeText(getBaseContext(), " transferred ...", Toast.LENGTH_SHORT).show();
//                    //System.out.println(" transferred ..." + length);
//                }
//            });
//        }
//
//        public void completed() {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn.setVisibility(View.VISIBLE);
//                    // Transfer completed
//
//                    Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
//                    //System.out.println(" completed ..." );
//                }
//            });
//        }
//
//        public void aborted() {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn.setVisibility(View.VISIBLE);
//                    // Transfer aborted
//                    Toast.makeText(getBaseContext(), "transfer aborted ,please try again...", Toast.LENGTH_SHORT).show();
//                    //System.out.println(" aborted ..." );
//                }
//            });
//        }
//
//        public void failed() {
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    btn.setVisibility(View.VISIBLE);
//                    // Transfer failed
//                    System.out.println(" failed ...");
//                }
//            });
//        }
//    }
}

