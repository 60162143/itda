package com.example.itda.ui.info;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class InfoReviewCommentRvAdapter extends RecyclerView.Adapter<InfoReviewCommentRvAdapter.CustomInfoReviewCommentViewHolder>{

    private ArrayList<infoReviewCommentData> ReviewComments;  // 리뷰 댓글 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoReviewCommentRvAdapter(Context context, ArrayList<infoReviewCommentData> reviewComments){
        this.mContext = context;
        this.ReviewComments = reviewComments;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomInfoReviewCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_review_comment, parent, false);

        return new InfoReviewCommentRvAdapter.CustomInfoReviewCommentViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomInfoReviewCommentViewHolder holder, int position) {
        infoReviewCommentData reviewComment = ReviewComments.get(position); // 현재 position의 리뷰 댓글 정보

        // 유저 프로필 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView) // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(reviewComment.getUserProfilePath()))    // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)   // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)     // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.userProfile);  // 이미지를 보여줄 View를 지정

        holder.userName.setText(reviewComment.getUserName());
        holder.reviewCommentRegDate.setText(reviewComment.getReviewCommentRegDate());
        holder.reviewCommentContent.setText(reviewComment.getReviewCommentContent());
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return ReviewComments.size();
    }


    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoReviewCommentViewHolder extends RecyclerView.ViewHolder {
        ImageButton userProfile;    // 유저 프로필 이미지
        TextView userName;  // 유저 명
        TextView reviewCommentRegDate;  // 리뷰 댓글 작성 일자
        TextView reviewCommentContent;  // 리뷰 댓글 내용

        public CustomInfoReviewCommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.info_review_comment_user_image);
            userName = itemView.findViewById(R.id.info_review_comment_user_name);
            reviewCommentRegDate = itemView.findViewById(R.id.info_review_comment_regdate);
            reviewCommentContent = itemView.findViewById(R.id.info_review_comment_detail);
        }
    }
}
