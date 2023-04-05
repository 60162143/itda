package com.example.itda.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class HomeSearchActivity extends Activity {
    private ImageButton homeSearchBackIc;   // 상단 뒤로가기 버튼
    private RecyclerView mainStoreRv;       // 가게 데이터 리사이클러뷰

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_search);

        initView(); // 뷰 생성

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        homeSearchBackIc.setOnClickListener(view -> finish());

        // GridLayoutManager 객체를 2개의 ViewHolder로 생성
        GridLayoutManager glm = new GridLayoutManager(this, 2);

        mainStoreRv.setHasFixedSize(true);  // 리사이클러뷰 높이, 너비 변경 제한
        mainStoreRv.setLayoutManager(glm);  //리사이클러뷰 Layout 설정

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        ArrayList<mainStoreData> Store = getIntent().getParcelableArrayListExtra("Store");

        MainStoreRvAdapter MainStoreAdapter = new MainStoreRvAdapter(this, Store);  // 리사이클러뷰 어뎁터 객체 생성
        mainStoreRv.setAdapter(MainStoreAdapter);   // 리사이클러뷰 어뎁터 객체 지정
    }

    // 뷰 생성
    private void initView(){
        homeSearchBackIc = findViewById(R.id.home_search_back); // 상단 뒤로가기 버튼
        mainStoreRv = findViewById(R.id.search_main_store_rv);  // 가게 데이터 리사이클러뷰
    }
}

