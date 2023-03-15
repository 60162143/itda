package com.example.itda.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class HomeSearchActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        ImageButton infoBackIc = findViewById(R.id.home_search_back);   // 상단 뒤로가기 버튼

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoBackIc.setOnClickListener(view -> finish());

        RecyclerView mainStoreRv = findViewById(R.id.search_main_store_rv); // 가게 데이터 리사이클러뷰

        // GridLayoutManager 객체를 2개의 ViewHolder로 생성
        GridLayoutManager glm = new GridLayoutManager(this, 2);

        mainStoreRv.setHasFixedSize(true);      // 리사이클러뷰 높이, 너비 변경 제한
        mainStoreRv.setLayoutManager(glm);      //리사이클러뷰 Layout 설정

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        ArrayList<mainStoreData> Store = getIntent().getParcelableArrayListExtra("Store");

        MainStoreRvAdapter MainStoreAdapter = new MainStoreRvAdapter(this);   // 리사이클러뷰 어뎁터 객체 생성
        MainStoreAdapter.setStores(Store);          // 어뎁터 객체에 가게 정보 저장
        mainStoreRv.setAdapter(MainStoreAdapter);   // 리사이클러뷰 어뎁터 객체 지정
    }
}

