package com.example.itda.ui.info;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class InfoReviewRvAdapter extends RecyclerView.Adapter<InfoReviewRvAdapter.CustomInfoReviewViewHolder>
        implements onInfoReviewPhotoRvClickListener{

    private final ArrayList<infoReviewData> Reviews;  // 리뷰 데이터
    private final ArrayList<infoPhotoData> Photos;    // 사진 데이터
    private final String storeName; // 가게 명
    private final int userId; // 로그인 유저 고유 아이디

    private final boolean isLoginFlag;  // 로그인 유무
    private static onInfoReviewRvClickListener rvClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoReviewRvAdapter(Context context
            , ArrayList<infoReviewData> reviews
            , ArrayList<infoPhotoData> Photos
            , String storeName
            , onInfoReviewRvClickListener clickListener
            , boolean isLoginFlag
            , int userId){

        this.mContext = context;
        this.Reviews = reviews;
        this.Photos = Photos;
        this.storeName = storeName;
        rvClickListener = clickListener;
        this.isLoginFlag = isLoginFlag;
        this.userId = userId;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomInfoReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_review, parent, false);

        return new InfoReviewRvAdapter.CustomInfoReviewViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomInfoReviewViewHolder holder, int position) {
        infoReviewData review = Reviews.get(position);  // 현재 position의 리뷰 정보

        // 로그인 유무에 따른 삭제버튼 활성화
        if(isLoginFlag && userId == review.getUserId()){
            holder.reviewDeleteBtn.setVisibility(View.VISIBLE);
        }else{
            holder.reviewDeleteBtn.setVisibility(View.GONE);
        }

        // 유저 프로필 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView) // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(review.getUserProfilePath()))   // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)   // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)     // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.userProfile);  // 이미지를 보여줄 View를 지정

        holder.userName.setText(review.getUserName());  // 유저 명
        holder.reviewRegDate.setText(review.getReviewRegDate());    // 리뷰 등록일
        holder.reviewHeartCnt.setText(String.valueOf(review.getReviewHeartCount()));    // 리뷰 좋아요 수
        holder.reviewScore.setText(String.valueOf(review.getReviewScore()));    // 리뷰 별점
        holder.reviewDetail.setText(review.getReviewDetail());  // 리뷰 내용
        holder.reviewCommentCnt.setText(String.valueOf(review.getReviewCommentCount()));    // 리뷰 댓글 수

        ArrayList<infoPhotoData> reviewPhoto = new ArrayList<>();   // 리뷰 사진 데이터

        // 리뷰에 속한 사진 데이터만 저장
        for(int i = 0; i < Photos.size(); i++){
            if(Photos.get(i).getReviewId() == review.getReviewId()){
                reviewPhoto.add(Photos.get(i));
            }
        }

        // LayoutManager 객체 생성
        holder.reviewPhotoRv.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false));

        // 리사이클러뷰 어뎁터 객체 생성
        InfoReviewPhotoRvAdapter infoReviewPhotoAdapter = new InfoReviewPhotoRvAdapter(mContext, this, reviewPhoto, review.getReviewId());
        holder.reviewPhotoRv.setAdapter(infoReviewPhotoAdapter);    // 리사이클러뷰 어뎁터 객체 지정
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Reviews.size();
    }

    // 사진 리사이클러뷰 클릭 이벤트 구현
    @Override
    public void onInfoReviewPhotoRvClick(View v, int position, int reviewId) {
        ArrayList<infoPhotoData> reviewPhoto = new ArrayList<>();   // 리뷰 사진 데이터

        // 리뷰에 속한 사진 데이터만 저장
        for(int i = 0; i < Photos.size(); i++){
            if(Photos.get(i).getReviewId() == reviewId){
                reviewPhoto.add(Photos.get(i));
            }
        }

        // 리뷰 상세 화면 Activity로 이동하기 위한 Intent 객체 선언
        Intent intent = new Intent(mContext, InfoPhotoActivity.class);

        intent.putParcelableArrayListExtra("Photo", reviewPhoto);    // 사진 데이터
        intent.putExtra("Position", position);      // 현재 position
        intent.putExtra("storeName", storeName);    // 가게 명

        mContext.startActivity(intent); // 새 Activity 인스턴스 시작
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoReviewViewHolder extends RecyclerView.ViewHolder {
        ImageButton userProfile;        // 유저 프로필 이미지
        ImageButton reviewHeartIc;      // 리뷰 좋아요 아이콘
        ImageButton reviewDeleteBtn;    // 리뷰 삭제 버튼
        TextView userName;  // 유저 명
        TextView reviewRegDate;     // 리뷰 작성 일자
        TextView reviewHeartCnt;    // 리뷰 좋아요 수
        TextView reviewScore;       // 리뷰 별점
        TextView reviewDetail;      // 리뷰 내용
        TextView reviewCommentCnt;  // 리뷰 댓글 수
        RecyclerView reviewPhotoRv; // 리뷰 사진

        public CustomInfoReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.info_review_user_image);
            reviewHeartIc = itemView.findViewById(R.id.info_review_heart_ic);
            reviewDeleteBtn = itemView.findViewById(R.id.info_review_delete_ic);
            userName = itemView.findViewById(R.id.info_review_user_name);
            reviewRegDate = itemView.findViewById(R.id.info_review_regdate);
            reviewHeartCnt = itemView.findViewById(R.id.info_review_heart_count);
            reviewScore = itemView.findViewById(R.id.info_review_score);
            reviewDetail = itemView.findViewById(R.id.info_review_detail);
            reviewCommentCnt = itemView.findViewById(R.id.info_review_comment_count);
            reviewPhotoRv = itemView.findViewById(R.id.info_review_photo);

            // 리사이클러뷰 클릭 이벤트 인터페이스 구현
            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onInfoReviewRvClick(view, getAbsoluteAdapterPosition(), "total");
                }
            });

            // 리사이클러뷰 삭제 버튼 클릭 이벤트 인터페이스 구현
            reviewDeleteBtn.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onInfoReviewRvClick(view, getAbsoluteAdapterPosition(), "delete");
                }
            });
        }
    }
}
