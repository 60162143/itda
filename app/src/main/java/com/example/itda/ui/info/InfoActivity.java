package com.example.itda.ui.info;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.itda.ui.home.mainStoreData;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

public class InfoActivity extends Activity {

    private mainStoreData Store;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //텍스트뷰 정의
        //최상단 가게 이름
        TextView infoMainStoreName = findViewById(R.id.info_main_store_name);
        //가게 이름
        TextView infoStoreName = findViewById(R.id.info_store_name);
        //가게 별점
        TextView infoStarScore = findViewById(R.id.info_star_score);
        //가게 간단한 설명
        TextView infoInformation = findViewById(R.id.info_information);
        //가게 해시태그
        TextView infoHashtag = findViewById(R.id.info_hashtag);
        //가게 운영 시간
        TextView infoWorkingTime = findViewById(R.id.info_working_time);
        //가게 주차 가능 여부
        TextView infoParking = findViewById(R.id.info_parking);
        //가게 주소
        TextView infoAddress = findViewById(R.id.info_address);
        //사진 타이틀 가게 이름
        TextView infoPhotoTitle = findViewById(R.id.info_photo_title);
        //리뷰 타이틀 가게 이름
        TextView infoReviewTitle = findViewById(R.id.info_review_title);

        //버튼 정의
        //메뉴 더보기 버튼
        Button infoMenuPlusBtn = findViewById(R.id.info_menu_plus_btn);
        //사진 더보기 버튼
        Button infoPhotoPlusBtn = findViewById(R.id.info_photo_plus_btn);
        //리뷰 쓰기 버튼
        TextView infoReviewPlusBtn = findViewById(R.id.info_review_plus_btn);
        //결제하기 버튼
        Button infoPaymentBtn = findViewById(R.id.info_payment_btn);

        //이미지 정의
        //가게 메인 이미지
        ImageView infoStoreImage = findViewById(R.id.info_store_image);

        //최상단 아이콘 정의
        //최상단 뒤로가기 버튼
        ImageButton infoBackIc = findViewById(R.id.info_back_ic);
        //최상단 전화하기 버튼
        ImageButton infoCallIc = findViewById(R.id.info_call_ic);
        //최상단 찜하기 버튼
        ImageButton infoBookmarkIc = findViewById(R.id.info_bookmark_ic);

        //메뉴 리스트 정의
        //메뉴 리스트뷰(최대 3개까지만 보여짐), 나머지는 더보기 버튼을 누른 후
        ListView infoMenuLv = findViewById(R.id.info_menu_lv);

        //리사이클러뷰 정의
        //협업 리사이클러뷰
        RecyclerView infoCollaboRv = findViewById(R.id.info_collabo_rv);
        //사진 리사이클러뷰
        RecyclerView infoPhotoRv = findViewById(R.id.info_photo_rv);
        //리뷰 리사이클러뷰
        RecyclerView infoReviewRv = findViewById(R.id.info_review_rv);

        Store = getIntent().getParcelableExtra("Store");

        //텍스트 설정
        infoMainStoreName.setText(Store.getStoreName());
        infoStoreName.setText(Store.getStoreName());
        infoStarScore.setText(String.valueOf(Store.getStoreScore()));
        infoInformation.setText(Store.getStoreInfo());
        infoWorkingTime.setText(Store.getStoreWorkingTime());
        if(TextUtils.isEmpty(Store.getStoreParking())){
            infoParking.setText("업데이트 예정");
        }else {
            infoParking.setText(Store.getStoreParking());
        }

        //뒤로가기 버튼 클릭 리스너 입니다.
        infoBackIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

