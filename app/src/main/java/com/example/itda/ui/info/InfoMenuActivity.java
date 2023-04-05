package com.example.itda.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class InfoMenuActivity extends Activity {
    private ImageButton infoMenuBackIc;     // 상단 뒤로가기 버튼
    private Button infoMenuStoreName;       // 상단 가게 이름
    private RecyclerView infoMenuDetailRv;  // 상세 메뉴 리사이클러뷰

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_menu);

        initView(); // 뷰 생성

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoMenuBackIc.setOnClickListener(view -> finish());

        // 가게 명
        String storeName = getIntent().getExtras().getString("storeName");
        infoMenuStoreName.setText(storeName);

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        ArrayList<infoMenuData> Menu = getIntent().getParcelableArrayListExtra("Menu"); // 메뉴 데이터

        // LayoutManager 객체 생성
        infoMenuDetailRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        InfoMenuRvAdapter infoMenuDetailAdapter = new InfoMenuRvAdapter(this, Menu, true);  // 리사이클러뷰 어뎁터 객체 생성
        infoMenuDetailRv.setAdapter(infoMenuDetailAdapter); // 리사이클러뷰 어뎁터 객체 지정
    }

    private void initView(){
        infoMenuBackIc = findViewById(R.id.info_menu_back_ic);  // 상단 뒤로가기 버튼
        infoMenuStoreName = findViewById(R.id.info_menu_main_store_name);   // 상단 가게 이름
        infoMenuDetailRv = findViewById(R.id.info_menu_detail_rv);  // 상세 메뉴 리사이클러뷰
    }
}

