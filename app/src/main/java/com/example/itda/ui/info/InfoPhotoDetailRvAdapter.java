package com.example.itda.ui.info;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class InfoPhotoDetailRvAdapter extends RecyclerView.Adapter<InfoPhotoDetailRvAdapter.CustomInfoPhotoDetailViewHolder>{

    private ArrayList<photoData> Photos = new ArrayList<>();    // 사진 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;
    private Intent intent;  // 상세 페이지로 전환을 위한 객체

    // Constructor
    public InfoPhotoDetailRvAdapter(Context context, ArrayList<photoData> photos){
        this.mContext = context;
        this.Photos = photos;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomInfoPhotoDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_photo_detail, parent, false);

        return new InfoPhotoDetailRvAdapter.CustomInfoPhotoDetailViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomInfoPhotoDetailViewHolder holder, int position) {
        photoData photo = Photos.get(position);     // 현재 position의 사진 정보

        holder.photoReviewUserName.setText(photo.getUserName());
        holder.photoReviewScore.setText(String.valueOf(photo.getReviewScore()));
        holder.photoReview.setText(photo.getReviewDetail());

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(photo.getPhotoPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);           // 이미지를 보여줄 View를 지정
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Photos.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoPhotoDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;           // 사진 이미지
        TextView photoReviewUserName;   // 리뷰 작성 유저 명
        TextView photoReviewScore;      // 리뷰 별점
        TextView photoReview;           // 리뷰 내용
        Button photoReviewMore;         // 리뷰 내용 더보기
        public CustomInfoPhotoDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImage = itemView.findViewById(R.id.info_photo_detail_image);
            photoReviewUserName = itemView.findViewById(R.id.info_photo_detail_user_name);
            photoReviewScore = itemView.findViewById(R.id.info_photo_detail_review_score);
            photoReview = itemView.findViewById(R.id.info_photo_detail_review);
            photoReviewMore = itemView.findViewById(R.id.info_photo_detail_plus_btn);

            // 리뷰 더보기 클릭 리스너
            photoReviewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 더보기일 경우 텍스트 전문 보이기
                    // 접기일 경우 텍스트 1줄만 보이기
                    if(photoReviewMore.getText() == "더보기"){
                        photoReview.setMaxLines(100);
                        photoReviewMore.setText("접기");
                    }else{
                        photoReview.setMaxLines(1);
                        photoReviewMore.setText("더보기");
                    }
                }
            });
        }
    }
}
