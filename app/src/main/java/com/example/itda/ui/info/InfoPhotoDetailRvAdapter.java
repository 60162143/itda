package com.example.itda.ui.info;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

    private final ArrayList<infoPhotoData> Photos;  // 사진 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoPhotoDetailRvAdapter(Context context, ArrayList<infoPhotoData> photos){
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
        infoPhotoData photo = Photos.get(position); // 현재 position의 사진 정보

        // 유저 명 SET
        holder.photoReviewUserName.setText(photo.getUserName());

        // 리뷰 별점 SET
        holder.photoReviewScore.setText(String.valueOf(photo.getReviewScore()));

        // 리뷰 내용 SET
        holder.photoReview.setText(photo.getReviewDetail());

        // 유저 프로필 이미지 SET
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(photo.getPhotoPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error_black_36dp)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback_black_36dp)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);           // 이미지를 보여줄 View를 지정


        // 리뷰 내용 TextView가 Ellipsis 상태인지 아닌지 확인
        // holder.textView.onPredraw()로 뷰가 다 그려졌는지 확인 후 holder.textViewr.getLayout()이 null이 아닌 상태에서만 가능
        holder.photoReview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int ellipseCount = holder.photoReview.getLayout().getEllipsisCount(holder.photoReview.getLayout().getLineCount()-1);

                // Ellipsis 상태이면 0 이상이 나옴
                if (ellipseCount > 0) { // 말줄임이 사용된 상태

                }else{  // 말줄임이 사용되지 않은 상태

                    holder.photoReviewMore.setVisibility(View.GONE);
                }

                holder.photoReview.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });
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
        ImageView photoImage;   // 사진 이미지
        TextView photoReviewUserName;   // 리뷰 작성 유저 명
        TextView photoReviewScore;  // 리뷰 별점
        TextView photoReview;       // 리뷰 내용
        Button photoReviewMore;     // 리뷰 내용 더보기
        public CustomInfoPhotoDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImage = itemView.findViewById(R.id.info_photo_detail_image);
            photoReviewUserName = itemView.findViewById(R.id.info_photo_detail_user_name);
            photoReviewScore = itemView.findViewById(R.id.info_photo_detail_review_score);
            photoReview = itemView.findViewById(R.id.info_photo_detail_review);
            photoReviewMore = itemView.findViewById(R.id.info_photo_detail_plus_btn);

            // 리뷰 더보기 클릭 리스너
            photoReviewMore.setOnClickListener(view -> {
                // 더보기일 경우 텍스트 전문 보이기
                if(photoReviewMore.getText() == "더보기"){
                    photoReview.setMaxLines(100);
                    photoReviewMore.setText("접기");
                }else{  // 접기일 경우 텍스트 1줄만 보이기
                    photoReview.setMaxLines(1);
                    photoReviewMore.setText("더보기");
                }
            });
        }
    }
}
