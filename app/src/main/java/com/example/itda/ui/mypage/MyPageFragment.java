package com.example.itda.ui.mypage;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.ui.global.globalVariable;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.example.itda.ui.login.LoginActivity;
import com.example.itda.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyPageFragment extends Fragment {
    private View root;                      // Fragment root view

    private Button myPageSetBtn;                // 설정 버튼
    private Button myPageLoginBtn;              // 로그인 버튼
    private Button myPageLogoutBtn;             // 로그아웃 버튼
    private Button myPageEditBtn;               // 유저 정보 수정 버튼
    private ImageButton myPageUserProfile;      // 유저 프로필 이미지
    private ImageButton myPageBookmarkBtn;      // 찜 목록 버튼
    private ImageButton myPageHeartBtn;         // 좋아요 목록 버튼
    private ImageButton myPageReviewBtn;        // 리뷰 목록 버튼
    private ImageButton myPagePhotoBtn;         // 사진 목록 버튼
    private LinearLayout linearLayoutBefore;    // 로그인 전 레이아웃 전체
    private LinearLayout linearLayoutAfter;     // 로그인 후 레이아웃 전체
    Intent intent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mypage, container, false);

        initView();

        // --------------------------------로그인 되어 있는지 확인후에 레이아웃 길이 설정하자!!!--------------------------------------------

//        // 하드웨어 X축, Y축 길이 구하기 위해 사용
//        Display display  = requireActivity().getWindowManager().getDefaultDisplay();
//        Point size = new Point();   // 좌표 각채 생성
//        display.getSize(size);      // size 객체에 디바이스 실제 size 저장
//        int display_width = size.x; // 디바이스 가로 길이 저장
//        int display_height = size.y; // 디바이스 세로 길이 저장
//
//        linearLayoutBefore = root.findViewById(R.id.mypage_login_before_layout);
//        linearLayoutBefore.setLayoutParams(new LinearLayout.LayoutParams(display_width, display_height/2 - 100));
//
//        linearLayoutAfter = root.findViewById(R.id.mypage_login_after_layout);
//        linearLayoutAfter.setLayoutParams(new LinearLayout.LayoutParams(display_width, display_height/2 - 100));

        // -------------------------------------------------------------------------------------------------------------------------

        // 로그인 버튼 클릭 리스너
        myPageLoginBtn.setOnClickListener(v -> {
            intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
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
        myPageBookmarkBtn = root.findViewById(R.id.mypage_bookmark_btn);
        myPageHeartBtn = root.findViewById(R.id.mypage_heart_btn);
        myPageReviewBtn = root.findViewById(R.id.mypage_review_btn);
        myPagePhotoBtn = root.findViewById(R.id.mypage_photo_btn);
        linearLayoutBefore = root.findViewById(R.id.mypage_login_before_layout);
        linearLayoutAfter = root.findViewById(R.id.mypage_login_after_layout);
    }
}