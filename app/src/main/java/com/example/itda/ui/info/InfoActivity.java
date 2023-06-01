package com.example.itda.ui.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.MainActivity;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.HomeSearchActivity;
import com.example.itda.ui.home.mainBookmarkCollaboData;
import com.example.itda.ui.home.mainBookmarkStoreData;
import com.example.itda.ui.home.mainStoreData;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;
import com.example.itda.ui.mypage.MyPageBookmarkActivity;
import com.example.itda.ui.mypage.MyPageEditActivity;
import com.example.itda.ui.mypage.MyPageEditNameActivity;
import com.example.itda.ui.mypage.MyPageReviewActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class InfoActivity extends AppCompatActivity implements onInfoCollaboRvClickListener, onInfoPhotoRvClickListener, onInfoReviewRvClickListener {

    private mainStoreData Store;    // 가게 데이터
    private final ArrayList<infoCollaboData> Collabo = new ArrayList<>(); // 협업 가게 데이터
    private final ArrayList<infoMenuData> Menu = new ArrayList<>();       // 메뉴 데이터
    private ArrayList<infoPhotoData> Photo = new ArrayList<>();     // 사진 데이터
    private ArrayList<infoReviewData> Review = new ArrayList<>();   // 리뷰 데이터


    private int storeId;   // 가게 고유 아이디
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    // ---------------- 최상단 Section ---------------------------
    private TextView infoMainStoreName; // 최상단 가게 이름
    private ImageButton infoBackIc;     // 최상단 뒤로가기 버튼
    private ImageButton infoCallIc;     // 최상단 전화 버튼
    private ImageButton infoBookmarkIc; // 최상단 찜 버튼

    private ArrayList<mainBookmarkStoreData> BookmarkStore = new ArrayList<>();  // 유저 찜한 가게 목록
    private ArrayList<mainBookmarkCollaboData> BookmarkCollabo = new ArrayList<>();  // 유저 찜한 협업 목록
    private int bookmarkStoreIndex = -1; // 현재 가게의 찜한 목록 데이터의 인덱스


    // ---------------- 가게 정보 Section ---------------------
    private TextView infoStoreName;     // 가게 이름
    private TextView infoStarScore;     // 가게 별점
    private TextView infoInformation;   // 가게 간단 설명
    private TextView infoHashtag;       // 가게 해시태그
    private ImageView infoStoreImage;   // 가게 썸네일 이미지


    // ---------------- 협업 Section ---------------------
    private RecyclerView infoCollaboRv;                 // 협업 가게 리사이클러뷰
    private InfoCollaboRvAdapter infoCollaboAdapter;    // 협업 가게 리사이클러뷰 어뎁터
    private Dialog infoCollaboDialog;                   // 협업 팝업 다이얼로그
    private LinearLayout infoCollaboLayout;             // 헙업 전체 레이아웃
    private int bookmarkCollaboIndex = -1; // 현재 찜한 협업 목록중 클릭한 협업 데이터의 인덱스

    // ---------------- 운영 정보 Section ---------------------
    private TextView infoWorkingTime;           // 가게 운영 시간
    private TextView infoDetail;                // 가게 간단 제공 서비스
    private TextView infoFacility;              // 가게 제공 시설 여부
    private ImageButton infoWorkingTimeDownIc;  // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
    private ImageButton infoWorkingTimeUpIc;    // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
    private String[] workingTimeArr;            // 운영 시간 텍스트를 구분자 "&&"으로 Split한 배열
    private LinearLayout infoWorkingTimeLayout; // 가게 운영시간 전체 레이아웃
    private LinearLayout infoDetailLayout;      // 가게 간단 제공 서비스 레이아웃
    private LinearLayout infoFacilityLayout;    // 가게 제공 시설 여부 레이아웃
    private LinearLayout infoServiceLayout;     // 운영 정보 전체 레이아웃

    boolean serviceExistFalg = false;   // 운영정보가 하나라도 존재하는지 여부를 나타내는 Flag

    // ---------------- 메뉴 Section ---------------------
    private Button infoMenuPlusBtn;             // 메뉴 더보기 버튼
    private RecyclerView infoMenuRv;            // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
    private InfoMenuRvAdapter infoMenuAdapter;  // 메뉴 리사이클러뷰 어댑터
    private LinearLayout infoMenuLayout;        // 메뉴 전체 레이아웃


    // ---------------- 지도 Section ---------------------
    private TextView infoAddress;       // 가게 주소
    private ViewGroup mapViewContainer; // mapView를 포함시킬 View Container
    private MapView mapView;            // 카카오 지도 View

    private float startXPosition = 0;   // 터치 이벤트의 시작점의 X(가로)위치
    private float startYPosition = 0;   // 터치 이벤트의 시작점의 Y(가로)위치


    // ---------------- 사진 Section ---------------------
    private RecyclerView infoPhotoRv;               // 사진 리사이클러뷰
    private LinearLayout infoPhotoLayout;           // 사진 전체 레이아웃
    private InfoPhotoRvAdapter infoPhotoAdapter;    // 사진 리사이클러뷰 어댑터


    // ---------------- 리뷰 Section ---------------------
    private LinearLayout infoReviewLayout;             // 리뷰 전체 레이아웃
    private Button infoReviewPlusBtn; // 리뷰 작성 버튼
    private RecyclerView infoReviewRv;  // 리뷰 리사이클러뷰
    private InfoReviewRvAdapter infoReviewAdapter;  // 리뷰 리사이클러뷰 어댑터
    private TextView infoReviewNoDataTxt;     // 리뷰 데이터 없음 표시 텍스트


    // ---------------- 결제 Section ---------------------
    private Button infoPaymentBtn; // 결제하기 버튼


    // ---------------- Nested ScrollView ---------------------
    private NestedScrollView infoScrollView; // 스크롤 뷰

    // ---------------- Dialog ---------------------
    private Dialog reviewDeleteDialog;   // 작성한 리뷰 목록 삭제 다이얼로그


    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private static RequestQueue requestQueue;        // Volley Library 사용을 위한 RequestQueue
    private String STORE_PATH;  // 가게 데이터 조회 Rest API
    private String COLLABO_PATH;    // 협업 가게 정보 데이터 조회 Rest API
    private String MENU_PATH;       // 메뉴 정보 데이터 조회 Rest API
    private String PHOTO_PATH;      // 사진 정보 데이터 조회 Rest API
    private String REVIEW_PATH;     // 리뷰 정보 데이터 조회 Rest API
    private String DELETE_REVIEW_PATH;     // 작성 리뷰 삭제 Rest API
    private String UPDATE_REVIEW_HEART_PATH;      // 리뷰 좋아요 갱신 Rest API
    private String DELETE_BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 삭제 Rest API
    private String INSERT_BOOKMARK_STORE_PATH;      // 유저 찜한 가게 목록 추가 Rest API
    private String BOOKMARK_COLLABO_PATH;      // 유저 찜한 협업 목록 ( 간단 정보 ) 조회 Rest API
    private String DELETE_BOOKMARK_COLLABO_PATH;      // 유저 찜한 협업 목록 삭제 Rest API
    private String INSERT_BOOKMARK_COLLABO_PATH;      // 유저 찜한 협업 목록 추가 Rest API
    private String HOST;            // Host 정보

    private boolean isLoginFlag = false;    // 로그인 여부
    private String intentPageName;  // intent된 페이지 명

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initView(); // 뷰 생성

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        Store = getIntent().getParcelableExtra("Store");    // 가게 데이터 GET
        storeId = getIntent().getExtras().getInt("storeId");    // 메인 가게 리사이클러뷰의 현재 가게 position
        BookmarkStore = getIntent().getParcelableArrayListExtra("bookmarkStore");    // 유저 찜한 가게 데이터 GET
        intentPageName = getIntent().getExtras().getString("pageName");    // intent된 페이지 명

        isLoginFlag = ((globalMethod) getApplication()).loginChecked(); // 로그인 유무

        // ---------------- Rest API 전역변수 SET---------------------------
        STORE_PATH = ((globalMethod) getApplication()).getMainStorePath();    // 가게 데이터 조회 Rest API
        COLLABO_PATH = ((globalMethod) getApplication()).getInfoCollaboPath();    // 협업 가게 정보 데이터 조회 Rest API
        MENU_PATH = ((globalMethod) getApplication()).getInfoMenuPath();          // 메뉴 정보 데이터 조회 Rest API
        PHOTO_PATH = ((globalMethod) getApplication()).getInfoPhotoPath();        // 사진 정보 데이터 조회 Rest API
        REVIEW_PATH = ((globalMethod) getApplication()).getInfoReviewPath();      // 리뷰 정보 데이터 조회 Rest API
        DELETE_REVIEW_PATH = ((globalMethod) getApplication()).deleteReviewPath();      // 작성 리뷰 삭제 Rest API
        UPDATE_REVIEW_HEART_PATH = ((globalMethod) getApplication()).updateInfoReviewHeartPath();      // 리뷰 좋아요 갱신 Rest API
        DELETE_BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).deleteBookmarkStorePath();      // 유저 찜한 가게 목록 삭제 Rest API
        INSERT_BOOKMARK_STORE_PATH = ((globalMethod) getApplication()).insertBookmarkStorePath();      // 유저 찜한 가게 목록 추가 Rest API
        BOOKMARK_COLLABO_PATH = ((globalMethod) getApplication()).getMainBookmarkCollaboPath();      // 유저 찜한 협업 목록 ( 간단 정보 ) 조회 Rest API
        DELETE_BOOKMARK_COLLABO_PATH = ((globalMethod) getApplication()).deleteBookmarkCollaboPath();      // 유저 찜한 협업 목록 삭제 Rest API
        INSERT_BOOKMARK_COLLABO_PATH = ((globalMethod) getApplication()).insertBookmarkCollaboPath();      // 유저 찜한 협업 목록 추가 Rest API
        HOST = ((globalMethod) getApplication()).getHost();                       // Host 정보

        // ---------------- 최상단 Section ---------------------------
        infoMainStoreName.setText(Store.getStoreName());    // 최상단 가게 이름

        // 최상단 뒤로가기 버튼 클릭 리스너
        infoBackIc.setOnClickListener(v -> {
            if(intentPageName.equals("HomeFragment")){  // 페이지 명 : HomeFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", storeId);
                intent.putExtra("reviewCount", Store.getStoreReviewCount());
                intent.putExtra("score", Store.getStoreScore());

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("HomeSearchActivity")){  // 페이지 명 : HomeSearchActivity
                Intent intent = new Intent(InfoActivity.this, HomeSearchActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("MapFragment")){  // 페이지 명 : MapFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", storeId);
                intent.putExtra("score", Store.getStoreScore());

                setResult(3000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("infoActivity")){  // 페이지 명 : infoActivity
                Intent intent = new Intent(InfoActivity.this, InfoActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(3000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("MyPageBookmarkActivity")){  // 페이지 명 : MyPageBookmarkActivity
                Intent intent = new Intent(InfoActivity.this, MyPageBookmarkActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", Store.getStoreId());

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("CollaboFragment")){  // 페이지 명 : CollaboFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }
        });

        // 전화 번호 있을 시 전화 걸기 화면으로 전환 클릭 리스너
        if(!TextUtils.isEmpty(Store.getStoreNumber())){
            infoCallIc.setOnClickListener(view -> {
                String telNum = "tel:" + Store.getStoreNumber();
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telNum)); // 전화 걸기 화면으로 화면 전환
                startActivity(mIntent);
            });
        }else{
            infoCallIc.setVisibility(View.GONE);    // 전화 아이콘 숨김
        }

        // 찜이 되어 있을경우 레이아웃 변경
        // 로그인이 되어 있는지 확인
        if(isLoginFlag && BookmarkStore != null && BookmarkStore.size() > 0){
            for(int i = 0; i < BookmarkStore.size(); i++){
                if(Store.getStoreId() == BookmarkStore.get(i).getStoreId()){
                    bookmarkStoreIndex = i;
                    infoBookmarkIc.setSelected(true);
                    break;
                }
            }
        }

        // 찜 버튼 클릭 리스너
        infoBookmarkIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoginFlag){  // 로그인이 되어있으면
                    if(infoBookmarkIc.isSelected()){    // 찜 버튼이 눌러져 있으면
                        infoBookmarkIc.setSelected(false);
                        deleteBookmarkStore(bookmarkStoreIndex);    // 찜 목록에서 제거
                    }else{
                        infoBookmarkIc.setSelected(true);
                        insertBookmarkStore(User.getInt("userId", 0), Store.getStoreId());  // 찜 목록에 추가
                    }
                }else{
                    StyleableToast.makeText(getApplicationContext(), "로그인 해야 이용하실 수 있습니다.", R.style.redToast).show();
                }
            }
        });

        // ---------------- 가게 정보 Section ---------------------
        infoStoreName.setText(Store.getStoreName());        // 가게 이름
        infoInformation.setText(Store.getStoreInfo());      // 가게 간단 정보
        infoHashtag.setText(Store.getStoreHashTag());       // 가게 해시태그

        // 가게 간단 소개
        if(!TextUtils.isEmpty(Store.getStoreInfo())){
            infoInformation.setText(Store.getStoreInfo());
        }

        // 가게 썸네일 이미지
        Glide.with(this)    // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(infoStoreImage);  // 이미지를 보여줄 View를 지정

        // ---------------- 협업 Section ---------------------
        getInfoCollabo();   // 협업 가게 데이터 GET

        getBookmarkCollabo();  // 유저 찜한 협업 목록 데이터 GET

        // ---------------- 운영 정보 Section ---------------------
        // 가게 운영 시간
        if(!TextUtils.isEmpty(Store.getStoreWorkingTime())){
            serviceExistFalg = true;    // 운영정보 존재 플래그

            workingTimeArr = Store.getStoreWorkingTime().split("\\n");  // '\n' 구분자로 Split

            infoWorkingTime.setText(workingTimeArr[0]); // 초기에 운영시간 1개만 보여짐

            // 운영시간 데이터가 1개일 경우 더보기 화살표 아이콘 숨김
            if(workingTimeArr.length == 1){
                infoWorkingTimeDownIc.setVisibility(View.GONE);
            }
        }else{  // 운영시간 데이터가 없을 경우 운영시간 레이아웃 전체 숨김
            infoWorkingTimeLayout.setVisibility(View.GONE);
        }

        // 가게 운영시간 아래 화살표 ( 내용 늘이기 ) 버튼 클릭 리스너
        infoWorkingTimeDownIc.setOnClickListener(view -> {
            infoWorkingTime.setText(Store.getStoreWorkingTime());   // 구분자가 "\n"으로 되어있는 내용 모두 출력
            infoWorkingTimeDownIc.setVisibility(View.GONE);     // 아래 방향 화살표 버튼 아예 숨기기 ( 공간 조차 없어짐 )
            infoWorkingTimeUpIc.setVisibility(View.VISIBLE);    // 위 방향 화살표 버튼 보이기
        });

        // 가게 운영시간 위 화살표 ( 내용 줄이기 ) 버튼 클릭 리스너
        infoWorkingTimeUpIc.setOnClickListener(view -> {
            workingTimeArr = Store.getStoreWorkingTime().split("\\n");    // 구분자가 "\n"으로 되어있는 내용 split
            infoWorkingTime.setText(workingTimeArr[0]);         // 첫번째 배열에 들어있는 내용만 출력
            infoWorkingTimeUpIc.setVisibility(View.GONE);       // 위 방향 화살표 버튼 아예 숨기기 ( 공간 조차 없어짐 )
            infoWorkingTimeDownIc.setVisibility(View.VISIBLE);  // 아래 방향 화살표 버튼 보이기
        });

        // 간단 제공 서비스
        if(!TextUtils.isEmpty(Store.getStoreDetail())){
            serviceExistFalg = true;    // 운영정보 존재 플래그

            infoDetail.setText(Store.getStoreDetail());
        }else{  // 간단 제공 서비스 데이터가 없을 경우 간단 제공 서비스 레이아웃 전체 숨김
            infoDetailLayout.setVisibility(View.GONE);
        }

        // 제공 시설 여부
        if(!TextUtils.isEmpty(Store.getStoreFacility())){
            serviceExistFalg = true;    // 운영정보 존재 플래그

            infoFacility.setText(Store.getStoreFacility());
        }else{  // 제공 시설 여부 데이터가 없을 경우 제공 시설 여부 레이아웃 전체 숨김
            infoFacilityLayout.setVisibility(View.GONE);
        }

        // 운영정보가 하나라도 존재할 경우 Padding 부여
        if(serviceExistFalg){
            infoServiceLayout.setPadding(0 , 10, 0, 20);
        }else{  // 운영정보 데이터가 없을 운영정보 레이아웃 전체 숨김
            infoServiceLayout.setVisibility(View.GONE);
        }

        // ---------------- 메뉴 Section ---------------------
        getInfoMenu();  // 메뉴 데이터 GET

        // 메뉴 더보기 버튼 클릭 리스너
        infoMenuPlusBtn.setOnClickListener(view -> {
            Intent intent = new Intent(InfoActivity.this, InfoMenuActivity.class);  // 메뉴 상세 화면 Activity로 이동하기 위한 Intent 객체 선언

            // 데이터 송신을 위한 Parcelable interface 사용
            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
            intent.putParcelableArrayListExtra("Menu", Menu);   // 메뉴 데이터
            intent.putExtra("storeName", Store.getStoreName()); // 가게 명

            startActivity(intent);  // 새 Activity 인스턴스 시작
        });

        // ---------------- 지도 Section ---------------------
        infoAddress.setText(Store.getStoreAddress());   // 주소


        // ---------------- 사진 Section ---------------------
        getInfoPhoto();     // 사진 데이터 GET


        // ---------------- 리뷰 Section ---------------------
        getInfoReview();    // 리뷰 데이터 GET

        // 리뷰 작성 버튼 클릭 리스너
        infoReviewPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoginFlag){
                    Intent intent = new Intent(InfoActivity.this, InfoReviewInsertActivity.class);

                    intent.putExtra("storeId", Store.getStoreId()); // 가게 고유 아이디
                    intent.putExtra("userId", User.getInt("userId", 0)); // 유저 고유 아이디

                    activityResultLauncher.launch(intent);
                }else{
                    StyleableToast.makeText(getApplicationContext(), "로그인 후 이용하실 수 있습니다.", R.style.redToast).show();
                }
            }
        });


        // ---------------- 결제 Section ---------------------


        // ---------------- 화면전환 Section ---------------------
        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000) { // resultCode가 1000으로 넘어왔다면 리뷰 작성 완료

                getInfoPhoto();     // 사진 데이터 GET
                //infoPhotoAdapter.notifyDataSetChanged();

                Review = new ArrayList<>(); // 리뷰 데이터 초기화
                getInfoReview();    // 리뷰 데이터 GET

                infoStarScore.setText(String.valueOf(Store.getStoreScore()));   // 가게 별점
            }else if(result.getResultCode() == 2000) { // resultCode가 2000으로 넘어왔다면 리뷰 상세 화면에서 넘어옴

                int position = result.getData().getIntExtra("position", 0); // 리뷰 리사이클러뷰 position
                infoReviewData review = result.getData().getParcelableExtra("review");  // 변경된 리뷰 데이터

                Review.set(position, review);   // 리뷰 데이터 변경

                // 리사이클러뷰 갱신
                infoReviewAdapter.setReviews(Review);
                infoReviewAdapter.notifyItemChanged(position);
            }else if(result.getResultCode() == 3000) { // resultCode가 2000으로 넘어왔다면 infoActivity에서 넘어옴
                BookmarkStore = result.getData().getParcelableArrayListExtra("bookmarkStore");  // 찜한 목록 데이터
            }
        });
    }

    // Activity 이동간 mapView는 1개만 띄워져 있어야 하기 때문에
    // onCreate가 아닌 onResume에서 mapview 객체 생성
    //
    // ----------- 간단한 LifeCycle --------------
    // onCreate -> onResume -> ( 다른 Activity로 이동 ) -> onPause -> ( 현재 Activity로 이동 ) -> onResume
    // @SuppressLint("ClickableViewAccessibility") 어노테이션을 추가해 Lint의 Warning을 무시
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();

        mapView = new MapView(this);   // mapView 객체 생성

        mapViewContainer = (ViewGroup) findViewById(R.id.info_map_view);    // ViewGroup Container
        mapViewContainer.addView(mapView);                                  // mapView attach

        double latitude = Store.getStoreLatitude();     // 첫 번째로 검색된 가게의 위도
        double longitude = Store.getStoreLongitude();   // 첫 번째로 검색된 가게의 경도

        // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);

        mapView.setMapCenterPoint(mapPoint, true);  // 지도 화면의 중심점을 설정

        MapPOIItem marker = new MapPOIItem();       // POI 객체 생성
        marker.setItemName(Store.getStoreName());   // POI Item 아이콘이 선택되면 나타나는 말풍선(Callout Balloon)에 POI Item 이름이 보여짐
        marker.setMapPoint(mapPoint);   // POI Item의 지도상 좌표를 설정
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker); // 지도화면에 POI Item 아이콘(마커)를 추가

        // mapView에 모션이벤트가 생길때
        // mainScrollView.requestDisallowInterceptTouchEvent(true);
        //스크롤에 터치이벤트를 뺏기지 않는다는 코드
        mapView.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    infoScrollView.requestDisallowInterceptTouchEvent(true);
                    startXPosition = motionEvent.getX();  //첫번째 터치의 X(너비)를 저장
                    startYPosition = motionEvent.getY();  //첫번째 터치의 Y(너비)를 저장
                    break;
                case MotionEvent.ACTION_UP:
                    float endXPosition = motionEvent.getX();   // X 좌표
                    float endYPosition = motionEvent.getY();   // X 좌표
                    if(Math.abs(startXPosition - endXPosition) < 10 && Math.abs(startYPosition - endYPosition) < 10){
                        Intent intent = new Intent(InfoActivity.this, InfoMapActivity.class);  // 지도 상세 화면 Activity로 이동하기 위한 Intent 객체 선언

                        intent.putExtra("store", Store);    // 가게 데이터

                        startActivity(intent);  // 새 Activity 인스턴스 시작
                    }
                    infoScrollView.requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        });
    }

    // 다른 Activity로 이동했을 경우 생성했던 mapView 객체 제거
    @Override
    protected void onPause() {
        super.onPause();

        mapViewContainer.removeView(mapView);
    }

    // 휴대폰 뒤로가기 버튼 클릭 이벤트
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(intentPageName.equals("HomeFragment")){  // 페이지 명 : HomeFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", storeId);
                intent.putExtra("reviewCount", Store.getStoreReviewCount());
                intent.putExtra("score", Store.getStoreScore());

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("HomeSearchActivity")){  // 페이지 명 : HomeSearchActivity
                Intent intent = new Intent(InfoActivity.this, HomeSearchActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("MapFragment")){  // 페이지 명 : MapFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", storeId);
                intent.putExtra("score", Store.getStoreScore());

                setResult(3000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("infoActivity")){  // 페이지 명 : infoActivity
                Intent intent = new Intent(InfoActivity.this, InfoActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(3000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("MyPageBookmarkActivity")){  // 페이지 명 : MyPageBookmarkActivity
                Intent intent = new Intent(InfoActivity.this, MyPageBookmarkActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);
                intent.putExtra("storeId", Store.getStoreId());

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }else if(intentPageName.equals("CollaboFragment")){  // 페이지 명 : CollaboFragment
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);

                intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore);

                setResult(2000, intent);    // 결과 코드와 intent 값 전달
                finish();   // 버튼 클릭 시 Activity 종료
            }

            return true;
        }
        return false;
    }

    private void initView(){
        // ---------------- 최상단 Section ---------------------------
        infoMainStoreName = findViewById(R.id.info_main_store_name);    // 최상단 가게 이름
        infoBackIc = findViewById(R.id.info_back_ic);   // 최상단 뒤로 가기 버튼
        infoCallIc = findViewById(R.id.info_call_ic);   // 최상단 전화 버튼
        infoBookmarkIc = findViewById(R.id.info_bookmark_ic);   // 최상단 찜 버튼


        // ---------------- 가게 정보 Section ---------------------
        infoStoreName = findViewById(R.id.info_store_name);     // 가게 이름
        infoStarScore = findViewById(R.id.info_star_score);     // 가게 별점
        infoInformation = findViewById(R.id.info_information);  // 가게 간단 설명
        infoHashtag = findViewById(R.id.info_hashtag);  // 가게 해시태그
        infoStoreImage = findViewById(R.id.info_store_image);   // 가게 썸네일 이미지


        // ---------------- 협업 Section ---------------------
        infoCollaboLayout = findViewById(R.id.info_collabo_layout); // 협업 전체 레이아웃
        infoCollaboRv = findViewById(R.id.info_collabo_rv); // 협업 리사이클러뷰

        infoCollaboDialog = new Dialog(InfoActivity.this);  // Dialog 초기화
        infoCollaboDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        infoCollaboDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        infoCollaboDialog.setContentView(R.layout.dl_info_collabo); // xml 레이아웃 파일과 연결


        // ---------------- 운영 정보 Section ---------------------
        infoWorkingTime = findViewById(R.id.info_working_time); // 가게 운영 시간
        infoDetail = findViewById(R.id.info_detail);        // 가게 간단 제공 서비스
        infoFacility = findViewById(R.id.info_facility);    // 가게 제공 시설 여부
        infoWorkingTimeDownIc = findViewById(R.id.info_working_time_down_arrow_ic); // 가게 운영 시간 아래 방향 버튼 ( 내용 늘리기 )
        infoWorkingTimeUpIc = findViewById(R.id.info_working_time_up_arrow_ic);     // 가게 운영 시간 위 방향 버튼 ( 내용 줄이기 )
        infoWorkingTimeLayout = findViewById(R.id.info_working_time_layout);        // 가게 운영 시간 전체 레이아웃
        infoDetailLayout = findViewById(R.id.info_detail_layout);       // 가게 간단 제공 서비스 전체 레이아웃
        infoFacilityLayout = findViewById(R.id.info_facility_layout);   // 가게 제공 시설 여부 전체 레이아웃
        infoServiceLayout = findViewById(R.id.info_service_layout);     // 운영 정보 전체 레이아웃


        // ---------------- 메뉴 Section ---------------------
        infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);    // 메뉴 더보기 버튼
        infoMenuRv = findViewById(R.id.info_menu_rv);   // 메뉴 리사이클러뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        infoMenuLayout = findViewById(R.id.info_menu_layout);       // 메뉴 전체 레이아웃


        // ---------------- 지도 Section ---------------------
        infoAddress = findViewById(R.id.info_address);  // 가게 주소


        // ---------------- 사진 Section ---------------------
        infoPhotoRv = findViewById(R.id.info_photo_rv); // 사진 리사이클러뷰
        infoPhotoLayout = findViewById(R.id.info_photo_layout); // 사진 전체 레이아웃


        // ---------------- 리뷰 Section ---------------------
        infoReviewLayout = findViewById(R.id.info_review_layout); // 리뷰 전체 레이아웃
        infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);    // 리뷰 작성 버튼
        infoReviewRv = findViewById(R.id.info_review_rv);   // 리뷰 리사이클러뷰
        infoReviewNoDataTxt = findViewById(R.id.info_review_nodata);   // 리뷰 데이터 없음 표시 텍스트

        // 리뷰 삭제 Dialog 팝업
        reviewDeleteDialog = new Dialog(InfoActivity.this);  // Dialog 초기화
        reviewDeleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);    // 타이틀 제거
        reviewDeleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 배경 색 제거
        reviewDeleteDialog.setContentView(R.layout.dl_delete); // xml 레이아웃 파일과 연결


        // ---------------- 결제 Section ---------------------
        infoPaymentBtn = findViewById(R.id.info_payment_btn);   // 결제 버튼


        // ---------------- Nested ScrollView ---------------------
        infoScrollView = findViewById(R.id.info_scroll_view);   // 스크롤 뷰
    }

    // ---------------- 협업 Section Start ---------------------


    // 협업 가게 데이터 GET
    private void getInfoCollabo(){
        // GET 방식 파라미터 설정
        String collaboPath = COLLABO_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest CollaboRequest = new StringRequest(Request.Method.GET, HOST + collaboPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");  // 객체에 collabo라는 Key를 가진 JSONArray 생성



                if(collaboArr.length() > 0) {
                    for (int i = 0; i < collaboArr.length(); i++) {
                        JSONObject object = collaboArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 협업 데이터 생성 및 저장
                        infoCollaboData infoCollaboData = new infoCollaboData(
                                object.getInt("storeId")                            // 가게 고유 아이디
                                , object.getString("storeName")                     // 가게 이름
                                , object.getString("storeAddress")                  // 가게 주소
                                , object.getString("storeDetail")                   // 가게 간단 제공 서비스
                                , object.getString("storeFacility")                 // 가게 제공 시설 여부
                                , object.getDouble("storeLatitude")                 // 가게 위도
                                , object.getDouble("storeLongitude")                // 가게 경도
                                , object.getString("storeNumber")                   // 가게 번호
                                , object.getString("storeInfo")                     // 가게 간단 정보
                                , object.getInt("storeCategoryId")                  // 가게가 속한 카테고리 고유 아이디
                                , HOST + object.getString("storeThumbnailPath")     // 가게 썸네일 이미지 경로
                                , object.getDouble("storeScore")                    // 가게 별점
                                , object.getString("storeWorkingTime")              // 가게 운영 시간
                                , object.getInt("collaboId")                        // 협업 고유 아이디
                                , object.getInt("collaboStoreId")                   // 협업 뒷 가게 고유 아이디
                                , object.getInt("collaboDiscountCondition")         // 앞 가게 할인 조건 ( 최소 금액 )
                                , object.getInt("collaboDiscountRate"));            // 뒷 가게 할인율 ( 정수 )

                        Collabo.add(infoCollaboData); // 협업 정보 저장
                    }

                    // LayoutManager 객체 생성
                    infoCollaboRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

                    infoCollaboAdapter = new InfoCollaboRvAdapter(this, this, Collabo);  // 리사이클러뷰 어뎁터 객체 생성
                    infoCollaboRv.setAdapter(infoCollaboAdapter);   // 리사이클러뷰 어뎁터 객체 지정

                    infoCollaboLayout.setPadding(0, 10, 0, 10); // 협업 레이아웃 전체 Padding 부여
                }else{  // 협업 데이터 없을 경우 전체 레이아웃 숨김
                    infoCollaboLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoCollaboError", "onErrorResponse : " + error);
        });

        CollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(CollaboRequest);      // RequestQueue에 요청 추가
    }

    // 협업 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoCollaboRvClick(View v, int position) {
        infoCollaboDialog.show();

        // 뷰 정의
        TextView preStoreName = infoCollaboDialog.findViewById(R.id.info_collabo_previous_store_name);  // 앞가게 명
        TextView postStoreName = infoCollaboDialog.findViewById(R.id.info_collabo_post_store_name);     // 뒷가게 명

        ImageButton preStoreImage = infoCollaboDialog.findViewById(R.id.info_collabo_previous_store_image); // 앞가게 썸네일
        ImageButton postStoreImage = infoCollaboDialog.findViewById(R.id.info_collabo_post_store_image);    // 뒷가게 썸네일

        TextView distance = infoCollaboDialog.findViewById(R.id.info_collabo_distance);     // 두 가게 사이 거리
        TextView discount_info = infoCollaboDialog.findViewById(R.id.info_collabo_coupon);  // 할인 정보

        ImageButton bookmarkBtn = infoCollaboDialog.findViewById(R.id.info_collabo_bookmark_btn);    // 협업 데이터 찜 버튼

        // 값 세팅
        infoCollaboData collabo = Collabo.get(position);    // 현재 Position의 협업 데이터

        preStoreName.setText(Store.getStoreName());     // 앞가게 명
        postStoreName.setText(collabo.getStoreName());  // 뒷가게 명

        // 찜이 되어 있을경우 레이아웃 변경
        // 로그인이 되어 있는지 확인
        if(isLoginFlag) {
            if(!BookmarkCollabo.isEmpty()){
                for(int i = 0; i < BookmarkCollabo.size(); i++){
                    if(collabo.getCollaboId() == BookmarkCollabo.get(i).getCollaboId()){
                        bookmarkCollaboIndex = i;
                        bookmarkBtn.setSelected(true);
                        break;
                    }
                }
            }
        }else{
            bookmarkBtn.setVisibility(View.GONE);
        }

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적

        // 앞가게 이미지
        Glide.with(this)    // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(Store.getStoreThumbnailPath())) // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(preStoreImage);   // 이미지를 보여줄 View를 지정

        // 뒷가게 이미지
        Glide.with(this)     // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(collabo.getStoreThumbnailPath()))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(postStoreImage);  // 이미지를 보여줄 View를 지정

        // 가게 간 거리 계산을 위한 좌표
        Location prePoint = new Location(Store.getStoreName());     // 앞 가게 위치 Location 객체 생성
        prePoint.setLatitude(Store.getStoreLatitude());     // 위도
        prePoint.setLongitude(Store.getStoreLongitude());   // 경도

        // 가게 간 거리 계산을 위한 좌표
        Location postPoint = new Location(collabo.getStoreName());  // 뒷 가게 위치 Location 객체 생성
        postPoint.setLatitude(collabo.getStoreLatitude());      // 위도
        postPoint.setLongitude(collabo.getStoreLongitude());    // 경도

        // 가게 간 거리
        String distanceStr = prePoint.distanceTo(postPoint) / 1000 + " km";
        distance.setText(distanceStr);

        // 할인 정보
        String dis_info = Store.getStoreName() + "에서 "
                + collabo.getCollaboDiscountCondition() +"원 이상 결제 시 "
                + collabo.getStoreName() + "에서 "
                + collabo.getCollaboDiscountRate() + "% 할인";

        discount_info.setText(dis_info);

        // 뒷가게 이미지 클릭 리스너
        postStoreImage.setOnClickListener(view -> {
            String mainStorePath = STORE_PATH + String.format("?storeId=%s", collabo.getStoreId());

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
            StringRequest postStoreRequest = new StringRequest(Request.Method.GET, HOST + mainStorePath, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                    JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                    JSONObject object = mainStoreArr.getJSONObject(0);

                    mainStoreData mainStore = new mainStoreData(
                            object.getInt("storeId")                        // 가게 고유 아이디
                            , object.getString("storeName")                 // 가게 이름
                            , object.getString("storeAddress")              // 가게 주소
                            , object.getString("storeDetail")               // 가게 간단 제공 서비스
                            , object.getString("storeFacility")             // 가게 제공 시설 여부
                            , object.getDouble("storeLatitude")             // 가게 위도
                            , object.getDouble("storeLongitude")            // 가게 경도
                            , object.getString("storeNumber")               // 가게 번호
                            , object.getString("storeInfo")                 // 가게 간단 정보
                            , object.getInt("storeCategoryId")              // 가게가 속한 카테고리 고유 아이디
                            , HOST + object.getString("storeThumbnailPath") // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")                // 가게 별점
                            , object.getString("storeWorkingTime")          // 가게 운영 시간
                            , object.getString("storeHashTag")              // 가게 해시태그
                            , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                            , 0);                                      // 현위치에서 가게까지의 거리

                    Intent intent = new Intent(InfoActivity.this, InfoActivity.class); // 가게 상세화면 Activity로 이동하기 위한 Intent 객체 선언

                    intent.putExtra("Store", mainStore);    // 가게 데이터
                    intent.putExtra("pageName", "infoActivity");    // 가게 데이터

                    intent.putParcelableArrayListExtra("bookmarkStore", BookmarkStore); // 찜한 가게 목록 데이터 ( Intent 종료시 반환하기 위한 데이터 )

                    activityResultLauncher.launch(intent);  // 새 Activity 인스턴스 시작
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("getInfoPostStoreError", "onErrorResponse : " + error);
            });

            postStoreRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(postStoreRequest);     // RequestQueue에 요청 추가
        });

        // 찜 버튼 클릭 리스너
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoginFlag){  // 로그인이 되어있으면
                    if(bookmarkBtn.isSelected()){    // 찜 버튼이 눌러져 있으면
                        bookmarkBtn.setSelected(false);
                        deleteBookmarkCollabo(bookmarkCollaboIndex);    // 찜 목록에서 제거
                    }else{
                        bookmarkBtn.setSelected(true);
                        insertBookmarkCollabo(User.getInt("userId", 0), collabo.getCollaboId());  // 찜 목록에 추가
                    }
                }else{
                    StyleableToast.makeText(getApplicationContext(), "로그인 해야 이용하실 수 있습니다.", R.style.redToast).show();
                }
            }
        });
    }

    // ---------------- 협업 Section End ---------------------



    // ---------------- 메뉴 Section Start ---------------------


    // 메뉴 데이터 GET
    private void getInfoMenu(){
        // GET 방식 파라미터 설정
        String menuPath = MENU_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest MenuRequest = new StringRequest(Request.Method.GET, HOST + menuPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray menuArr = jsonObject.getJSONArray("menu");    // 객체에 menu라는 Key를 가진 JSONArray 생성

                if(menuArr.length() > 0) {
                    for (int i = 0; i < menuArr.length(); i++) {
                        JSONObject object = menuArr.getJSONObject(i);   // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 메뉴 데이터 생성 및 저장
                        infoMenuData infoMenuData = new infoMenuData(
                                object.getInt("menuId")         // 메뉴 고유 아이디
                                , object.getInt("storeId")      // 가게 고유 아이디
                                , object.getString("menuName")  // 메뉴 명
                                , object.getInt("menuPrice")    // 메뉴 가격
                                , object.getInt("menuOrder"));  // 메뉴 정렬 순서

                        Menu.add(infoMenuData); // 메뉴 정보 저장
                    }
                    // LayoutManager 객체 생성
                    infoMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

                    infoMenuAdapter = new InfoMenuRvAdapter(this, Menu, false);  // 리사이클러뷰 어뎁터 객체 생성
                    infoMenuRv.setAdapter(infoMenuAdapter); // 리사이클러뷰 어뎁터 객체 지정

                    infoMenuLayout.setPadding(0, 10, 0, 10);    // 메뉴 레이아웃 전체 Padding 부여
                }else{  // 메뉴 데이터 없을 경우 전체 레이아웃 숨김
                    infoMenuLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoMenuError", "onErrorResponse : " + error);
        });

        MenuRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(MenuRequest);      // RequestQueue에 요청 추가
    }


    // ---------------- 메뉴 Section End ---------------------


    // ---------------- 사진 Section Start ---------------------


    // 사진 데이터 GET
    private void getInfoPhoto(){
        // GET 방식 파라미터 설정
        String photoPath = PHOTO_PATH + String.format("?storeId=%s", Store.getStoreId());

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest PhotoRequest = new StringRequest(Request.Method.GET, HOST + photoPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray photoArr = jsonObject.getJSONArray("photo");  // 객체에 photo라는 Key를 가진 JSONArray 생성

                if(photoArr.length() > 0) {
                    for (int i = 0; i < photoArr.length(); i++) {
                        JSONObject object = photoArr.getJSONObject(i);  // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 사진 데이터 생성 및 저장
                        infoPhotoData infoPhotoData = new infoPhotoData(
                                object.getInt("photoId")                    // 사진 고유 아이디
                                , object.getInt("userId")                   // 유저 고유 아이디
                                , object.getInt("reviewId")                 // 리뷰 고유 아이디
                                , object.getString("userName")              // 유저 명
                                , HOST + object.getString("photoImagePath") // 사진 이미지 경로
                                , object.getString("reviewDetail")          // 리뷰 내용
                                , object.getInt("reviewScore"));            // 리뷰 별점
                        Photo.add(infoPhotoData); // 사진 정보 저장
                    }
                    // LayoutManager 객체 생성
                    infoPhotoRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

                    infoPhotoAdapter = new InfoPhotoRvAdapter(this, this, Photo);  // 리사이클러뷰 어뎁터 객체 생성
                    infoPhotoRv.setAdapter(infoPhotoAdapter);   // 리사이클러뷰 어뎁터 객체 지정

                    infoPhotoLayout.setPadding(0, 10, 0, 10);   // 사진 레이아웃 전체 Padding 부여
                }else{  // 사진 데이터 없을 경우 전체 레이아웃 숨김
                    infoPhotoLayout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoPhotoError", "onErrorResponse : " + error);
        });

        PhotoRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(PhotoRequest);      // RequestQueue에 요청 추가
    }

    // 사진 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoPhotoRvClick(View v, int position) {
        // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        // position이 4보다 작을 경우 사진 상세 화면으로 이동
        // position이 4보다 클 경우 사진 전체 화면으로 이동
        Intent intent = new Intent(InfoActivity.this, position < 4 ? InfoPhotoActivity.class : InfoPhotoTotalActivity.class);

        intent.putParcelableArrayListExtra("Photo", Photo);     // 사진 데이터
        intent.putExtra("Position", position);                  // 현재 Position
        intent.putExtra("storeName", Store.getStoreName());     // 가게 명

        startActivity(intent);  // 새 Activity 인스턴스 시작
    }


    // ---------------- 사진 Section End ---------------------


    // ---------------- 리뷰 Section Start ---------------------

    // 리뷰 데이터 GET
    private void getInfoReview(){
        // GET 방식 파라미터 설정
        String reviewPath = REVIEW_PATH + String.format("?storeId=%s", Store.getStoreId());

        if(isLoginFlag){
            reviewPath += String.format("&&loginUserId=%s", User.getInt("userId", 0));   // 로그인 유저 고유 아이디
       }

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest ReviewRequest = new StringRequest(Request.Method.GET, HOST + reviewPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                JSONArray reviewArr = jsonObject.getJSONArray("review"); // 객체에 review라는 Key를 가진 JSONArray 생성

                double starScoreTotal = 0;  // 리뷰 총 별점

                if(reviewArr.length() > 0) {
                    for (int i = 0; i < reviewArr.length(); i++) {
                        JSONObject object = reviewArr.getJSONObject(i); // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 리뷰 데이터 생성 및 저장
                        infoReviewData infoReviewData = new infoReviewData(
                                object.getInt("reviewId")                       // 리뷰 고유 아이디
                                , object.getInt("userId")                       // 유저 고유 아이디
                                , object.getString("userName")                  // 유저 명
                                , object.getInt("storeId")                      // 가게 고유 아이디
                                , HOST + object.getString("userProfilePath")    // 유저 프로필 경로
                                , object.getString("reviewDetail")              // 리뷰 내용
                                , object.getInt("reviewScore")                  // 리뷰 별점
                                , object.getInt("reviewHeartCount")             // 리뷰 좋아요 수
                                , object.getString("reviewRegDate")             // 리뷰 작성 일자
                                , object.getInt("reviewCommentCount")           // 리뷰 댓글 수
                                , object.getInt("reviewHeartIsClick"));         // 로그인 했을 경우 좋아요 눌렀는지 확인 Flag ( 누른 경우 : 1, 안눌렀거나 비로그인 시 : 0 )

                        Review.add(0, infoReviewData); // 리뷰 정보 저장

                        starScoreTotal += object.getInt("reviewScore"); // 리뷰 총 별점
                    }
                    // LayoutManager 객체 생성
                    infoReviewRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

                    infoReviewAdapter = new InfoReviewRvAdapter(this, Review, Photo, Store.getStoreName(), this, isLoginFlag, User.getInt("userId", 0));  // 리사이클러뷰 어뎁터 객체 생성
                    infoReviewRv.setAdapter(infoReviewAdapter); // 리사이클러뷰 어뎁터 객체 지정

                    // 리뷰 별점 Set
                    Store.setStoreReviewCount(Review.size());   // 리뷰 수
                    Store.setStoreScore(Math.round(starScoreTotal / Review.size() * 10.0) / 10.0);  // 리뷰 별점
                    infoStarScore.setText(String.valueOf(Store.getStoreScore()));

                    infoReviewNoDataTxt.setVisibility(View.GONE);   // 리뷰 없음 표시 텍스트 숨기기
                }else{
                    infoStarScore.setText(String.valueOf(0.0));   // 별점 초기화
                    infoReviewNoDataTxt.setVisibility(View.VISIBLE);   // 리뷰 없음 표시 텍스트 보이기
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getInfoReviewError", "onErrorResponse : " + error);
        });

        ReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(ReviewRequest);      // RequestQueue에 요청 추가
    }

    // 리뷰 리사이클러뷰 클릭 이벤트 인터페이스 구현
    @Override
    public void onInfoReviewRvClick(View v, int position, String flag) {
        if(flag.equals("total")){
            infoReviewData review = Review.get(position);       // 리뷰 데이터
            ArrayList<infoPhotoData> photo = new ArrayList<>(); // 리뷰 사진 데이터

            // 현재 리뷰에 속한 사진 데이터만 저장
            for(int i = 0; i < Photo.size(); i++){
                if(review.getReviewId() == Photo.get(i).getReviewId()){
                    photo.add(Photo.get(i));
                }
            }

            // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
            Intent intent = new Intent(InfoActivity.this, InfoReviewActivity.class);

            intent.putParcelableArrayListExtra("Photo", photo); // 사진 데이터
            intent.putExtra("reviewId", review.getReviewId());  // 리뷰 고유 아이디
            intent.putExtra("storeName", Store.getStoreName()); // 가게 명
            intent.putExtra("review", review);                  // 리뷰 데이터
            intent.putExtra("position", position);              // 현재 리사이클러뷰 position
            intent.putExtra("isLoginFlag", isLoginFlag);        // 현재 로그인 여부

            activityResultLauncher.launch(intent);  // 새 Activity 인스턴스 시작
        }else if(flag.equals("delete")){
            reviewDeleteDialog.show();

            // 뷰 정의
            TextView dlTitle = reviewDeleteDialog.findViewById(R.id.dl_title);  // 다이얼로그 타이틀
            Button dlConfirmBtn = reviewDeleteDialog.findViewById(R.id.dl_confirm_btn);  // 다이얼로그 확인 버튼
            Button dlCloseBtn = reviewDeleteDialog.findViewById(R.id.dl_close_btn);  // 다이얼로그 닫기 버튼

            // 데이터 SET
            dlTitle.setText("작성한 리뷰를 삭제하시겠습니까?");
            dlConfirmBtn.setText("삭제");

            // 삭제 버튼 클릭 리스너
            dlConfirmBtn.setOnClickListener(view -> {
                Map<String, String> param = new HashMap<>();
                param.put("reviewId", String.valueOf(Review.get(position).getReviewId()));   // 삭제할 리뷰 고유 아이디

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest deleteReviewRequest = new StringRequest(Request.Method.POST, HOST + DELETE_REVIEW_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "삭제 성공!", R.style.blueToast).show();

                            reviewDeleteDialog.dismiss();  // 다이얼로그 닫기

                            Review.remove(position);   // 데이터 삭제
                            infoReviewAdapter.notifyItemRemoved(position); // 리사이클러뷰 데이터 삭제

                            // 데이터가 없을 경우 없다는 텍스트 표시
                            if(Review.size() == 0){
                                infoReviewRv.setVisibility(View.GONE);
                                infoReviewNoDataTxt.setVisibility(View.VISIBLE);   // 리뷰 없음 표시 텍스트 보이기
                            }else{
                                infoReviewRv.setVisibility(View.VISIBLE);
                                infoReviewNoDataTxt.setVisibility(View.GONE);   // 리뷰 없음 표시 텍스트 숨기기
                            }
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "삭제 실패...", R.style.redToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("deleteReviewError", "onErrorResponse : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // php로 설정값을 보낼 수 있음 ( POST )
                        return param;
                    }
                };

                deleteReviewRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(deleteReviewRequest);      // RequestQueue에 요청 추가
            });

            // 닫기 버튼 클릭 리스너
            dlCloseBtn.setOnClickListener(view1 -> reviewDeleteDialog.dismiss());

        }else if(flag.equals("heartInsert")){   // 리뷰 좋아요 추가
            if(isLoginFlag){    // 로그인 시 이용 가능
                infoReviewData review = Review.get(position);       // 리뷰 데이터
                updateReviewHeart(review.getReviewId(), 1, position);
            }else{
                StyleableToast.makeText(this, "로그인 후 이용해주세요!", R.style.orangeToast).show();
            }
        }else if(flag.equals("heartDelete")){   // 리뷰 좋아요 삭제
            if(isLoginFlag){    // 로그인 시 이용 가능
                infoReviewData review = Review.get(position);       // 리뷰 데이터
                updateReviewHeart(review.getReviewId(), -1, position);
            }else{
                StyleableToast.makeText(this, "로그인 후 이용해주세요!", R.style.orangeToast).show();
            }
        }
    }

    // 리뷰 좋아요 수정
    private void updateReviewHeart(int reviewId, int flag, int position){
        Map<String, String> param = new HashMap<>();

        param.put("reviewId", String.valueOf(reviewId));   // 수정할 리뷰 고유 아이디
        param.put("userId", String.valueOf(User.getInt("userId", 0)));   // 수정할 유저 고유 아이디
        param.put("flag", String.valueOf(flag));   // 증가 +1, 감소 -1

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest updateReviewHeartRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_REVIEW_HEART_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    if(flag == 1){  // 좋아요 추가
                        Review.get(position).setReviewHeartIsClick(1);  // 좋아요 클릭 여부 ( 누름 )
                        Review.get(position).setReviewHeartCount(Review.get(position).getReviewHeartCount() + 1);   // 좋아요 수

                        StyleableToast.makeText(this, "좋아요 추가 성공!", R.style.blueToast).show();
                    }else{  // 좋아요 취소
                        Review.get(position).setReviewHeartIsClick(0);  // 좋아요 클릭 여부 ( 누름 취소 )
                        Review.get(position).setReviewHeartCount(Review.get(position).getReviewHeartCount() - 1);   // 좋아요 수

                        StyleableToast.makeText(this, "좋아요 삭제 성공!", R.style.blueToast).show();
                    }

                    infoReviewAdapter.setReviews(Review);
                    infoReviewAdapter.notifyItemChanged(position);
                }else{
                    StyleableToast.makeText(this, "좋아요 갱신 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("updateReviewHeartError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        updateReviewHeartRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(updateReviewHeartRequest);      // RequestQueue에 요청 추가
    }


    // ---------------- 리뷰 Section End ---------------------


    // ---------------- 찜 Section Start ---------------------


    // 유저 찜한 가게 삭제
    private void deleteBookmarkStore(int index){
        Map<String, String> param = new HashMap<>();

        param.put("bmkStId", String.valueOf(BookmarkStore.get(index).getBmkStoreId()));   // 삭제할 찜한 가게 목록 고유 아이디
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteBookmarkRequest = new StringRequest(Request.Method.POST, HOST + DELETE_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "삭제 성공!", R.style.blueToast).show();

                    BookmarkStore.remove(index);   // 데이터 삭제

                    bookmarkStoreIndex = -1;  // 현재 가게의 찜한 목록 데이터의 인덱스 초기화

                }else{
                    StyleableToast.makeText(this, "삭제 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteBookmarkRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 가게 추가
    private void insertBookmarkStore(int userId, int storeId){
        Map<String, String> param = new HashMap<>();

        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("storeId", String.valueOf(storeId));   // 찜할 가게 고유 아이디
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertBookmarkRequest = new StringRequest(Request.Method.POST, HOST + INSERT_BOOKMARK_STORE_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "추가 성공!", R.style.blueToast).show();

                    // 데이터 추가
                    BookmarkStore.add(new mainBookmarkStoreData(
                            Integer.parseInt(jsonObject.getString("bmkStId"))
                            , storeId
                    ));

                    bookmarkStoreIndex = BookmarkStore.size() - 1;  // 현재 가게의 찜한 목록 데이터의 인덱스 SET
                }else{
                    StyleableToast.makeText(this, "추가 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertBookmarkStoreError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertBookmarkRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertBookmarkRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 정보 Return
    private void getBookmarkCollabo(){
        // GET 방식 파라미터 설정
        String bookmarkCollaboPath = BOOKMARK_COLLABO_PATH;
        bookmarkCollaboPath += String.format("?userId=%s", User.getInt("userId", 0));     // 유저 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest bookmarkCollaboRequest = new StringRequest(Request.Method.GET, HOST + bookmarkCollaboPath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray bookmarkCollaboArr = jsonObject.getJSONArray("bookmarkCollabo");  // 객체에 store라는 Key를 가진 JSONArray 생성

                if(bookmarkCollaboArr.length() > 0) {
                    for (int i = 0; i < bookmarkCollaboArr.length(); i++) {
                        JSONObject object = bookmarkCollaboArr.getJSONObject(i);          // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        mainBookmarkCollaboData bookmarkCollabo = new mainBookmarkCollaboData(
                                object.getInt("bookmarkCollaboId")                        // 찜한 협업 목록 고유 아이디
                                , object.getInt("collaboId")); // 협업 고유 아이디

                        BookmarkCollabo.add(bookmarkCollabo);  // 협업 정보 저장
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getBookmarkCollaboError", "onErrorResponse : " + error);
        });

        bookmarkCollaboRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(bookmarkCollaboRequest);     // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 목록 삭제
    private void deleteBookmarkCollabo(int index){
        Map<String, String> param = new HashMap<>();

        param.put("bmkCobId", String.valueOf(BookmarkCollabo.get(index).getBmkCollaboId()));   // 삭제할 찜한 협업 목록 고유 아이디

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest deleteBookmarkCollaboRequest = new StringRequest(Request.Method.POST, HOST + DELETE_BOOKMARK_COLLABO_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "삭제 성공!", R.style.blueToast).show();

                    BookmarkCollabo.remove(index);   // 데이터 삭제

                }else{
                    StyleableToast.makeText(this, "삭제 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("deleteBookmarkCollaboError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        deleteBookmarkCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(deleteBookmarkCollaboRequest);      // RequestQueue에 요청 추가
    }

    // 유저 찜한 협업 목록 추가
    private void insertBookmarkCollabo(int userId, int collaboId){
        Map<String, String> param = new HashMap<>();

        param.put("userId", String.valueOf(userId));   // 유저 고유 아이디
        param.put("collaboId", String.valueOf(collaboId));   // 찜할 협업 목록 고유 아이디
        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
        StringRequest insertBookmarkCollaboRequest = new StringRequest(Request.Method.POST, HOST + INSERT_BOOKMARK_COLLABO_PATH, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                String success = jsonObject.getString("success");

                if(!TextUtils.isEmpty(success) && success.equals("1")) {
                    StyleableToast.makeText(this, "추가 성공!", R.style.blueToast).show();

                    // 데이터 추가
                    BookmarkCollabo.add(new mainBookmarkCollaboData(
                            Integer.parseInt(jsonObject.getString("bmkCobId"))
                            , collaboId
                    ));

                }else{
                    StyleableToast.makeText(this, "추가 실패...", R.style.redToast).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("insertBookmarkCollaboError", "onErrorResponse : " + error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // php로 설정값을 보낼 수 있음 ( POST )
                return param;
            }
        };

        insertBookmarkCollaboRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(insertBookmarkCollaboRequest);      // RequestQueue에 요청 추가
    }

    // ---------------- 찜 Section End ---------------------
}

