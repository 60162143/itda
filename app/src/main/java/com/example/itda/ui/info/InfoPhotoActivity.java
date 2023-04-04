package com.example.itda.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.itda.R;

import java.util.ArrayList;

public class InfoPhotoActivity extends Activity {

    private ImageButton infoPhotoBackIc;    // 상단 뒤로가기 버튼
    private Button infoPhotoStoreName;      // 상단 가게 이름
    private ViewPager2 infoPhotoDetailSlider;   // 사진 슬라이드 뷰페이저

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_photo);

        initView();

        String storeName = getIntent().getExtras().getString("storeName");  // 가게 명
        infoPhotoStoreName.setText(storeName);

        int position = getIntent().getIntExtra("Position", 0);  // 선택한 사진 position

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        ArrayList<infoPhotoData> photo = getIntent().getParcelableArrayListExtra("Photo");  // 사진 데이터

        InfoPhotoDetailRvAdapter infoPhotoDetailAdapter = new InfoPhotoDetailRvAdapter(this, photo);  // 리사이클러뷰 어뎁터 객체 생성

        infoPhotoDetailSlider.setOffscreenPageLimit(1);
        infoPhotoDetailSlider.setAdapter(infoPhotoDetailAdapter);

        infoPhotoDetailSlider.setCurrentItem(position);
    }

    private void initView(){
        infoPhotoBackIc = findViewById(R.id.info_photo_back_ic);            // 상단 뒤로가기 버튼
        infoPhotoStoreName = findViewById(R.id.info_photo_main_store_name); // 상단 가게 이름
        infoPhotoDetailSlider = findViewById(R.id.info_photo_detail_slider);  // 사진 슬라이더 뷰페이저

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoPhotoBackIc.setOnClickListener(view -> finish());
    }
}

