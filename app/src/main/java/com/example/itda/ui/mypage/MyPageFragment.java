package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.login.LoginActivity;
import com.example.itda.R;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageFragment extends Fragment {
    private View root;                      // Fragment root view
    private Button myPageSetBtn;                // 설정 버튼
    private Button myPageLoginBtn;              // 로그인 버튼
    private Button myPageLogoutBtn;             // 로그아웃 버튼
    private Button myPageEditBtn;               // 유저 정보 수정 버튼
    private ImageButton myPageUserProfile;      // 유저 프로필 이미지
    private TextView myPageUserName;            // 유저 명
    private ImageButton myPageBookmarkBtn;      // 찜 목록 버튼
    private ImageButton myPageReviewBtn;        // 리뷰 목록 버튼
    private ImageButton myPagePhotoBtn;         // 사진 목록 버튼
    private LinearLayout linearLayoutBefore;    // 로그인 전 레이아웃 전체
    private LinearLayout linearLayoutAfter;     // 로그인 후 레이아웃 전체

    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성
    private int display_width = 0;
    private int display_height = 0;

    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )

    private boolean isLoginFlag = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mypage, container, false);

        // 하드웨어 X축, Y축 길이 구하기 위해 사용
        Display display  = requireActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();   // 좌표 각채 생성
        display.getSize(size);  // size 객체에 디바이스 실제 size 저장

        display_width = size.x;     // 디바이스 가로 길이 저장
        display_height = size.y;    // 디바이스 세로 길이 저장

        isLoginFlag = ((globalMethod) requireActivity().getApplicationContext()).loginChecked();    // 로그인 여부

        initView();

        // 유저 전역 변수 GET
        User = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 로그인 유무에 따른 UI Visible
        if(((globalMethod) requireActivity().getApplication()).loginChecked()){

            loginDataSet(); // 로그인 정보 SET

            linearLayoutBefore.setVisibility(View.GONE);
            linearLayoutAfter.setVisibility(View.VISIBLE);
        }else{
            linearLayoutBefore.setVisibility(View.VISIBLE);
            linearLayoutAfter.setVisibility(View.GONE);
        }

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 9001){ // resultCode가 9001로 넘어왔다면 로그인 완료 후 넘어옴
                loginDataSet(); // 로그인 정보 SET

                linearLayoutBefore.setVisibility(View.GONE);
                linearLayoutAfter.setVisibility(View.VISIBLE);
            }else if(result.getResultCode() == 9002){ // resultCode가 9002로 넘어왔다면 프로필 변경 후 넘어옴
                loginDataSet(); // 로그인 정보 SET
            }else if(result.getResultCode() == 1001){ // resultCode가 1001로 넘어왔다면 찜 목록 화면에서 넘어옴

            }else if(result.getResultCode() == 1002){ // resultCode가 1001로 넘어왔다면 리뷰 목록 화면에서 넘어옴

            }
        });

        // 로그인 버튼 클릭 리스너
        myPageLoginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            // startActivityForResult가 아닌 ActivityResultLauncher의 launch 메서드로 intent 실행
            activityResultLauncher.launch(intent);
        });

        // 로그아웃 버튼 클릭 리스너
        myPageLogoutBtn.setOnClickListener(v -> {
            // SharedPreferences를 가져와서 거기서 Editor 객체를 가져와 put 메소드를 사용
            SharedPreferences.Editor autoLoginEdit = User.edit();

            autoLoginEdit.clear();  // 저장되어 있는 데이터 제거

            autoLoginEdit.apply();  // 데이터를 저장
            linearLayoutBefore.setVisibility(View.VISIBLE);
            linearLayoutAfter.setVisibility(View.GONE);
        });

        // 내 정보 수정 버튼 클릭 리스너
        myPageEditBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPageEditActivity.class);

            activityResultLauncher.launch(intent);
        });

        // 내 정보 찜 목록 버튼 클릭 리스너
        myPageBookmarkBtn.setOnClickListener(v -> {
            if(isLoginFlag){
                Intent intent = new Intent(getActivity(), MyPageBookmarkActivity.class);

                activityResultLauncher.launch(intent);
            }else{
                StyleableToast.makeText(root.getContext(), "로그인 후 이용해주세요!", R.style.orangeToast).show();
            }
        });

        // 내 정보 리뷰 목록 버튼 클릭 리스너
        myPageReviewBtn.setOnClickListener(v -> {
            if(isLoginFlag){
                Intent intent = new Intent(getActivity(), MyPageReviewActivity.class);

                activityResultLauncher.launch(intent);
            }else{
                StyleableToast.makeText(root.getContext(), "로그인 후 이용해주세요!", R.style.orangeToast).show();
            }
        });

        // 내 정보 사진 목록 버튼 클릭 리스너
        myPagePhotoBtn.setOnClickListener(v -> {
            if(isLoginFlag){
                Intent intent = new Intent(getActivity(), MyPagePhotoActivity.class);

                activityResultLauncher.launch(intent);
            }else{
                StyleableToast.makeText(root.getContext(), "로그인 후 이용해주세요!", R.style.orangeToast).show();
            }
        });

        return root;
    }

    // 뷰 생성
    private void initView() {
        myPageSetBtn = root.findViewById(R.id.mypage_set_btn);
        myPageLoginBtn = root.findViewById(R.id.mypage_login_btn);
        myPageLogoutBtn = root.findViewById(R.id.mypage_logout_btn);
        myPageEditBtn = root.findViewById(R.id.mypage_edit_btn);
        myPageUserProfile = root.findViewById(R.id.mypage_user_profile);
        myPageUserName = root.findViewById(R.id.mypage_user_name);
        myPageBookmarkBtn = root.findViewById(R.id.mypage_bookmark_btn);
        myPageReviewBtn = root.findViewById(R.id.mypage_review_btn);
        myPagePhotoBtn = root.findViewById(R.id.mypage_photo_btn);

        linearLayoutBefore = root.findViewById(R.id.mypage_login_before_layout);
        linearLayoutBefore.setLayoutParams(new LinearLayout.LayoutParams(display_width, display_height/2 - 200));

        linearLayoutAfter = root.findViewById(R.id.mypage_login_after_layout);
        linearLayoutAfter.setLayoutParams(new LinearLayout.LayoutParams(display_width, display_height/2 - 200));
    }

    private void loginDataSet(){
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(this)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(User.getString("userProfileImage", "")))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(myPageUserProfile);      // 이미지를 보여줄 View를 지정

        myPageUserName.setText(User.getString("userName", ""));
    }
}