package com.example.itda.ui.info;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.BuildConfig;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.mypage.MyPageEditActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;

public class InfoReviewInsertActivity extends AppCompatActivity implements onInfoReviewInsertPhotoRvClickListener{
    private ImageButton infoReviewInsertBackIc;       // 상단 뒤로가기 버튼

    private ImageButton infoReviewInsertStar01;  // 별점 1점 버튼
    private ImageButton infoReviewInsertStar02;  // 별점 2점 버튼
    private ImageButton infoReviewInsertStar03;  // 별점 3점 버튼
    private ImageButton infoReviewInsertStar04;  // 별점 4점 버튼
    private ImageButton infoReviewInsertStar05;  // 별점 5점 버튼

    private TextView infoReviewInsertPhotoCnt;   // 사진 추가 개수
    private EditText infoReviewContent; // 리뷰 내용
    private RecyclerView infoReviewPhotoRv;   // 리뷰 사진 리사이클러뷰
    private InfoReviewInsertPhotoRvAdapter infoReviewPhotoAdapter;    // 사진 리사이클러뷰 어댑터

    private Button infoReviewInsertBtn;  // 작성 완료 버튼

    private ArrayList<infoPhotoData> Photos; // 리뷰 사진 데이터
    private ArrayList<infoReviewPhotoFileData> PhotosFiles = new ArrayList<>(); // 리뷰 사진 파일 데이터 ( FTP에 업로드 하기 위해 )
    private ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    private int StoreId;    // 가게 고유 아이디
    private int UserId;    // 유저 고유 아이디
    private int ReviewId;    // 리뷰 고유 아이디

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private String INSERT_REVIEW_PATH; // 리뷰 추가 Rest API
    private String INSERT_REVIEW_PHOTO_PATH; // 리뷰 사진 추가 Rest API
    private String IMAGE_PLUS;  // 추가할 이미지 기본 화면 정보
    private String HOST;    // Host 정보

    private final Handler handler = new Handler();  // 쓰레드 핸들러
    private int starScore = 1;  // 현재 별점

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_review_insert);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        INSERT_REVIEW_PATH = ((globalMethod) getApplication()).insertInfoReviewPath();   // 리뷰 추가 Rest API
        INSERT_REVIEW_PHOTO_PATH = ((globalMethod) getApplication()).insertInfoReviewPhotoPath();   // 리뷰 사진 추가 Rest API
        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        IMAGE_PLUS = ((globalMethod) getApplication()).getImagePlusPath();   // 추가할 이미지 기본 화면 정보 ( 회색 화면에 가운데 플러스 아이콘 있는 화면 )

        StoreId = getIntent().getExtras().getInt("storeId");;    // 가게 고유 아이디
        UserId = getIntent().getExtras().getInt("userId");;    // 유저 고유 아이디

        initView();

        uriList.add(Uri.parse(HOST + IMAGE_PLUS));  // 리사이클러뷰 추가하는 버튼 이미지를 위해 더미 데이터 추가

        infoReviewPhotoAdapter = new InfoReviewInsertPhotoRvAdapter(this, this, uriList);
        infoReviewPhotoRv.setAdapter(infoReviewPhotoAdapter);
        infoReviewPhotoRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == RESULT_OK){ // 사진 선택 완료
                Intent data = result.getData();

                if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                    StyleableToast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", R.style.redToast).show();
                }
                else{   // 이미지를 하나라도 선택한 경우
                    if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                        Log.e("single choice: ", String.valueOf(data.getData()));
                        Uri imageUri = data.getData();
                        uriList.add((uriList.size() - 1), imageUri);
                        infoReviewPhotoAdapter.setUriList(uriList);
                        infoReviewPhotoAdapter.notifyDataSetChanged();    // 리사이클러뷰 데이터 변경

                        infoReviewInsertPhotoCnt.setText("( " + (uriList.size() - 1) + " / 10 )");  // 사진 개수
                        //infoReviewPhotoAdapter.notifyItemInserted(uriList.size() - 1);

                    }
                    else{      // 이미지를 여러장 선택한 경우
                        ClipData clipData = data.getClipData();
                        Log.e("clipData", String.valueOf(clipData.getItemCount()));

                        if(clipData.getItemCount() + uriList.size() - 1 > 10){   // 선택한 이미지가 11장 이상인 경우
                            StyleableToast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", R.style.redToast).show();
                        } else{   // 선택한 이미지가 1장 이상 10장 이하인 경우
                            Log.e("multiple choice: ", "multiple choice");

                            for (int i = 0; i < clipData.getItemCount(); i++){
                                Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                                try {
                                    uriList.add((uriList.size() - 1), imageUri);  //uri를 list에 담는다.

                                } catch (Exception e) {
                                    Log.e("error", "File select error", e);
                                }
                            }

                            infoReviewPhotoAdapter.setUriList(uriList);
                            infoReviewPhotoAdapter.notifyDataSetChanged();    // 리사이클러뷰 데이터 변경

                            infoReviewInsertPhotoCnt.setText("( " + (uriList.size() - 1) + " / 10 )");  // 사진 개수
                        }
                    }
                }
            }
        });


        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoReviewInsertBackIc.setOnClickListener(view -> finish());

        // 첫번째 별점 클릭 리스너
        infoReviewInsertStar01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starScore = 1;  // 현재 별점

                infoReviewInsertStar01.setSelected(true);
                infoReviewInsertStar02.setSelected(false);
                infoReviewInsertStar03.setSelected(false);
                infoReviewInsertStar04.setSelected(false);
                infoReviewInsertStar05.setSelected(false);
            }
        });

        // 두번째 별점 클릭 리스너
        infoReviewInsertStar02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starScore = 2;  // 현재 별점

                infoReviewInsertStar01.setSelected(true);
                infoReviewInsertStar02.setSelected(true);
                infoReviewInsertStar03.setSelected(false);
                infoReviewInsertStar04.setSelected(false);
                infoReviewInsertStar05.setSelected(false);
            }
        });

        // 세번째 별점 클릭 리스너
        infoReviewInsertStar03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starScore = 3;  // 현재 별점

                infoReviewInsertStar01.setSelected(true);
                infoReviewInsertStar02.setSelected(true);
                infoReviewInsertStar03.setSelected(true);
                infoReviewInsertStar04.setSelected(false);
                infoReviewInsertStar05.setSelected(false);
            }
        });

        // 네번째 별점 클릭 리스너
        infoReviewInsertStar04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starScore = 4;  // 현재 별점

                infoReviewInsertStar01.setSelected(true);
                infoReviewInsertStar02.setSelected(true);
                infoReviewInsertStar03.setSelected(true);
                infoReviewInsertStar04.setSelected(true);
                infoReviewInsertStar05.setSelected(false);
            }
        });

        // 다섯번째 별점 클릭 리스너
        infoReviewInsertStar05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starScore = 5;  // 현재 별점

                infoReviewInsertStar01.setSelected(true);
                infoReviewInsertStar02.setSelected(true);
                infoReviewInsertStar03.setSelected(true);
                infoReviewInsertStar04.setSelected(true);
                infoReviewInsertStar05.setSelected(true);
            }
        });

        // 작성 완료 버튼 클릭 리스너
        infoReviewInsertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(infoReviewContent.getText().toString())){
                    insertInfoReview(StoreId, UserId);

                }else{
                    StyleableToast.makeText(getApplicationContext(), "리뷰 내용을 입력해주세요!", R.style.redToast).show();
                }
            }
        });
    }

    private void initView(){
        infoReviewInsertBackIc = findViewById(R.id.info_review_insert_back_ic);               // 상단 뒤로가기 버튼
        infoReviewInsertStar01 = findViewById(R.id.info_review_star_ic_01);     // 별점 1점 버튼
        infoReviewInsertStar02 = findViewById(R.id.info_review_star_ic_02);    // 별점 2점 버튼
        infoReviewInsertStar03 = findViewById(R.id.info_review_star_ic_03);           // 별점 3점 버튼
        infoReviewInsertStar04 = findViewById(R.id.info_review_star_ic_04);       // 별점 4점 버튼
        infoReviewInsertStar05 = findViewById(R.id.info_review_star_ic_05);         // 별점 5점 버튼
        infoReviewContent = findViewById(R.id.info_review_insert_content);       // 리뷰 내용
        infoReviewInsertPhotoCnt = findViewById(R.id.info_review_insert_photo_count);       // 사진 추가 개수
        infoReviewPhotoRv = findViewById(R.id.info_review_insert_photo_rv);   // 리뷰 사진 리사이클러뷰
        infoReviewInsertBtn = findViewById(R.id.info_review_insert_btn);   // 작성 완료 버튼

        infoReviewInsertStar01.setSelected(true);   // 별점 1점 SET
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
            Log.d("uploadStart", "------------------ Authentication Success! ------------------");
            if(uriList.size() - 1 < 10){   // 사진은 10장 이하만 가능
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }else{
                StyleableToast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", R.style.redToast).show();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            StyleableToast.makeText(getApplicationContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", R.style.redToast).show();
        }
    };

    @Override
    public void onInfoReviewInsertPhotoRvClick(View v, int position, String flag) {
        if(position == uriList.size() - 1){ // 사진 추가 이벤트
            checkPermissions(); // 갤러리 권한 확인
        }else{  // 추가한 사진 클릭 이벤트
            if(flag.equals("image")){   // 사진 이미지 클릭

            }else if(flag.equals("delete")){    // 사진 삭제 버튼 클릭

                uriList.remove(position);
                infoReviewPhotoAdapter.setUriList(uriList);
                infoReviewPhotoAdapter.notifyItemRemoved(position);    // 리사이클러뷰 데이터 변경

                infoReviewInsertPhotoCnt.setText("( " + (uriList.size() - 1) + " / 10 )");  // 사진 개수
            }
        }
    }

    // 리뷰 데이터 Insert
    private void insertInfoReview(int storeId, int userId){
        Map<String, String> param = new HashMap<>();
        param.put("storeId", String.valueOf(storeId));   // 가게 고유 아이디
        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("reviewContent", infoReviewContent.getText().toString());   // 리뷰 내용
        param.put("reviewScore", String.valueOf(starScore));   // 리뷰 별점

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertReviewRequest = new StringRequest(Request.Method.POST, HOST + INSERT_REVIEW_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    ReviewId = jsonObject.getInt("reviewId");   // 추가한 리뷰 아이디

                    // 사진 데이터가 있는 경우
                    if(uriList.size() > 1) {
                        for(int i = 0; i < uriList.size() - 1; i++){

                            if (uriList.get(i).getScheme().equals("content")) {
                                // 커서란?
                                // ContentResolver.query() 클라이언트 메서드는 언제나 쿼리 선택 기준과 일치하는 행에 대해 쿼리 프로젝션이 지정한 열을 포함하는 Cursor를 반환
                                // 데이터베이스 쿼리에서 반환된 결과 테이블의 행들을 가르키는 것
                                // 이 인터페이스는 데이터베이스 쿼리에서 반환된 결과 집합에 대한 임의의 읽기-쓰기 액세스를 제공
                                Cursor cursor = getContentResolver().query(uriList.get(i)   // Uri : 찾고자하는 데이터의 Uri, 접근할 앱에서 정의, 내 앱에서 만들고 싶다면 manifest에서 만들 수 있음
                                        , null  // Projection : 일반적인 DB의 column와 같음, 결과로 받고 싶은 데이터의 종류를 알려줌
                                        , null  // Selection : DB의 where 키워드와 같음, 어떤 조건으로 필터링된 결과를 받을 때 사용
                                        , null  // Selection args : Selection과 함께 사용됨, SELECT 절에 있는 ? 자리표시자를 대체
                                        , null  // SortOrder : 쿼리 결과 데이터를 sorting할 때 사용( 반환된 Cursor 내에 행이 나타나는 순서를 지정 )
                                );

                                try {
                                    if (cursor != null && cursor.moveToFirst()) {
                                        // 파일명 + 확장자
                                        String[] fileNameEts = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)).split("\\.");
                                        String fileName = fileNameEts[0]; // 파일 명
                                        String fileEts = fileNameEts.length > 1 ? fileNameEts[1] : "";  // 파일 확장자

                                        String fileSize = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)); // 파일 크기 ( 단위 : 바이트 )
                                        String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)); // 파일의 경로

                                        File file = new File(filePath);
                                        PhotosFiles.add(new infoReviewPhotoFileData(file, ReviewId, fileName, fileEts, Double.parseDouble(fileSize) / 1024));
                                    }
                                } finally {
                                    if (cursor != null) {
                                        cursor.close();
                                    }
                                }
                            }
                        }

                        // 안드로이드에서 네트워크와 관련된 작업을 할 때,
                        // 반드시 메인 Thread가 아닌 별도의 작업 Thread를 생성하여 작업해야 함
                        NThread nThread = new NThread(PhotosFiles);
                        nThread.start();    // Thread 실행

                        nThread.join();    // Thread 실행
                    }

                    StyleableToast.makeText(getApplicationContext(), "리뷰 작성 성공!", R.style.blueToast).show();

                    // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                    Intent intent = new Intent(InfoReviewInsertActivity.this, InfoActivity.class);

                    setResult(1000, intent);    // 결과 코드와 intent 값 전달

                    finish();
                }else{
                    StyleableToast.makeText(getApplicationContext(), "리뷰 작성 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertReviewError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertReviewRequest);      // RequestQueue에 요청 추가
    }

    // 리뷰 사진 데이터 Insert
    public void insertInfoReviewPhoto(int reviewId, String name, String ets, double size){
        Map<String, String> param = new HashMap<>();
        param.put("reviewId", String.valueOf(reviewId));   // 리뷰 고유 아이디
        param.put("userId", String.valueOf(UserId));   // 유저 고유 아이디
        param.put("fileName", name);    // 파일 명
        param.put("fileEts", ets);      // 파일 확장자
        param.put("fileSize", String.valueOf(size));    // 파일 크기

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertReviewPhotoRequest = new StringRequest(Request.Method.POST, HOST + INSERT_REVIEW_PHOTO_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    Log.d("insertReviewPhotoSuccess", "추가 성공!");
                    //StyleableToast.makeText(getApplicationContext(), "변경 성공!", R.style.blueToast).show();

                }else{
                    Log.d("insertReviewPhotoFail", "추가 실패..");
                    //StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertReviewPhotoError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertReviewPhotoRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertReviewPhotoRequest);      // RequestQueue에 요청 추가
    }


    //안드로이드 최근 버전에서는 네크워크 통신시에 반드시 스레드를 요구한다.
    class NThread extends Thread{
        ArrayList<infoReviewPhotoFileData> photosFiles;

        public NThread(ArrayList<infoReviewPhotoFileData> PhotosFiles) {
            photosFiles = PhotosFiles;
        }

        @Override
        public void run() {
            upload(photosFiles);
        }

        public void upload(ArrayList<infoReviewPhotoFileData> files){
            // Upload file
            for(int i = 0; i < files.size(); i++){
                infoReviewPhotoFileData file = files.get(i);
                uploadFile(file.getFile(), file.getReviewId(), file.getFileName(), file.getFileEts(), file.getFileSize());
            }
        }
    }

    public void uploadFile(File file, int reviewId, String name, String ets, double size){

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
                    insertInfoReviewPhoto(reviewId, name, ets, size);
                    //StyleableToast.makeText(getApplicationContext(), "변경 완료", R.style.blueToast).show();
                }
            });

        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    StyleableToast.makeText(getApplicationContext(), "사진 저장 실패", R.style.blueToast).show();
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

}

