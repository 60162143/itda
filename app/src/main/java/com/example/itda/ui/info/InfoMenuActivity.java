package com.example.itda.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;
import com.example.itda.ui.home.mainStoreData;

import java.util.ArrayList;

public class InfoMenuActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_menu);

        ImageButton infoMenuBackIc = findViewById(R.id.info_menu_back_ic);   // 상단 뒤로가기 버튼
        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoMenuBackIc.setOnClickListener(view -> finish());


        Button infoMenuStoreName = findViewById(R.id.info_menu_main_store_name);    // 상단 가게 이름

        String storeName = getIntent().getExtras().getString("storeName");
        infoMenuStoreName.setText(storeName);

        RecyclerView infoMenuDetailRv = findViewById(R.id.info_menu_detail_rv);

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        ArrayList<menuData> Menu = getIntent().getParcelableArrayListExtra("Menu");

        InfoMenuRvAdapter infoMenuDetailAdapter = new InfoMenuRvAdapter(this, Menu, true);  // 리사이클러뷰 어뎁터 객체 생성

        infoMenuDetailRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        infoMenuDetailRv.setAdapter(infoMenuDetailAdapter);
        initView();

    }

    private void initView(){

    }
}

