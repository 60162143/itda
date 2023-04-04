package com.example.itda.ui.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class InfoPhotoTotalActivity extends Activity implements onInfoPhotoTotalRvClickListener{

    ArrayList<infoPhotoData> photo;     // 사진 데이터

    private ImageButton infoPhotoBackIc;    // 상단 뒤로가기 버튼
    private Button infoPhotoStoreName;      // 상단 가게 이름
    private RecyclerView infoMenuTotalRv;   // 사진 전체 리사이클러뷰

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_photo_total);

        initView();

        String storeName = getIntent().getExtras().getString("storeName");  // 가게 명
        infoPhotoStoreName.setText(storeName);

        int position = getIntent().getIntExtra("Position", 0);  // 선택한 사진 position

        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        photo = getIntent().getParcelableArrayListExtra("Photo");  // 사진 데이터

        InfoPhotoTotalRvAdapter infoPhotoTotalAdapter = new InfoPhotoTotalRvAdapter(this,this , photo);  // 리사이클러뷰 어뎁터 객체 생성

        infoMenuTotalRv.setLayoutManager(new GridLayoutManager(this, 3));
        infoMenuTotalRv.setAdapter(infoPhotoTotalAdapter);

    }

    private void initView(){
        infoPhotoBackIc = findViewById(R.id.info_photo_total_back_ic);            // 상단 뒤로가기 버튼
        infoPhotoStoreName = findViewById(R.id.info_photo_total_main_store_name); // 상단 가게 이름
        infoMenuTotalRv = findViewById(R.id.info_photo_total_rv);  // 사진 슬라이더 뷰페이저

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoPhotoBackIc.setOnClickListener(view -> finish());
    }

    @Override
    public void onInfoPhotoTotalRvClick(View v, int position) {
        Intent intent = new Intent(InfoPhotoTotalActivity.this, InfoPhotoActivity.class); // 사진 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        intent.putParcelableArrayListExtra("Photo", photo);
        intent.putExtra("Position", position);
        intent.putExtra("storeName", infoPhotoStoreName.getText());
        startActivity(intent);
    }
}