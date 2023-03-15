package com.example.itda.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.example.itda.MainActivity;
import com.example.itda.R;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 xml의 내용을 파싱하여 뷰를 생성 및 속성을 설정
        setContentView(R.layout.activity_splash);

        // Handlr : 메시지 수신메시지 수신 시 그 처리를 담당
        // Handler는 Looper가 가진 메시지 큐(Message Queue)를 다룰 수 있기 때문에
        // 새로운 메시지를 보내거나 수신된 메시지에 대한 처리를 담당하는 주체가 되는 것
        Handler hd = new Handler();
        hd.postDelayed(new splashHandler(), 3000);  // 1초 후에 hd handler 실행, 3000ms = 3초
    }

    private class splashHandler implements Runnable{
        @Override
        public void run() { // 시간 지난 후 실행할 코딩
            // 메인 화면으로 이동
            startActivity(new Intent(getApplication(), MainActivity.class));

            SplashActivity.this.finish();   // 로딩페이지 Activity Stack에서 제거
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈 때 뒤로가기 버튼 못누르게 함
    }
}
