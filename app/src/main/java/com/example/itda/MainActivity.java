package com.example.itda;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends FragmentActivity {
    // 화면의 최상위에 고정되어 있는 버튼
    // 메인 화면 홈버튼
    public FloatingActionButton fab_main;

    private long pressedTime = 0;   // 뒤로가기 버튼 입력시간이 담길 long 객체

    // 리스너 생성
    public interface OnBackPressedListener{
        void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener){
        mBackListener = listener;
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {
        // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
        if(mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
        } else {
            Log.e("!!!", "Listener is null");
            if ( pressedTime == 0 ) {
                StyleableToast.makeText(this, "한 번 더 누르면 종료됩니다.", R.style.orangeToast).show();
                //Toast.makeText(this, " 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            }
            else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if ( seconds > 2000 ) {
                    StyleableToast.makeText(this, "한 번 더 누르면 종료됩니다.", R.style.orangeToast).show();
                    //Toast.makeText(this, " 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                    pressedTime = 0 ;
                }
                else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess");
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 레이아웃 xml의 내용을 파싱하여 뷰를 생성 및 속성을 설정
        setContentView(R.layout.activity_main);

        // 인터넷 연결 상태를 확인하기 위한 객체
        // 인터넷에 실제로 연결되어 있는지, 연결되어 있다면 어떤 유형의 연결인지 확인 가능
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            StyleableToast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", R.style.redToast).show();
            //Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            finish();   // Activity 종료
        }

        // 안드로이드 하단 바
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // NavHost에서 App Navigation을 관리하는 객체
        // NavController는 사용자가 앱 내에서 이동할 때 NavHost에서 대상 콘텐츠의 전환을 조종하는 역활
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // 네비게이션 Toolbar 지정
        NavigationUI.setupWithNavController(navView, navController);

        fab_main = findViewById(R.id.main_fab);  // 홈버튼

        // 홈버튼 클릭 이벤트 리스너
        fab_main.setOnClickListener(v -> navController.navigate(R.id.navigation_home));
    }
}