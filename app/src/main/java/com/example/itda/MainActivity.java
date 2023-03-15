package com.example.itda;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends FragmentActivity {
    // 화면의 최상위에 고정되어 있는 버튼
    // 메인 화면 홈버튼
    public FloatingActionButton fab_main;

    Context context;

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
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
            finish();   // Activity 종료
        }

        // 안드로이드 하단 바
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // NavHost에서 App Navigation을 관리하는 객체
        // NavController는 사용자가 앱 내에서 이동할 때 NavHost에서 대상 콘텐츠의 전환을 조종하는 역활
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // 네비게이션 Toolbar 지정
        NavigationUI.setupWithNavController(navView, navController);

        fab_main = (FloatingActionButton) findViewById(R.id.main_fab);  // 홈버튼

        // 홈버튼 클릭 이벤트 리스너
        fab_main.setOnClickListener(v -> navController.navigate(R.id.navigation_home));
    }
}