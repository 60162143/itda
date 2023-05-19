package com.example.itda.ui.info;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class InfoReviewPhotoRvAdapter extends RecyclerView.Adapter<InfoReviewPhotoRvAdapter.CustomInfoReviewPhotoViewHolder>{

    private final ArrayList<infoPhotoData> Photos;    // 사진 데이터
    private int reviewId; // 리뷰 고유 아이디

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onInfoReviewPhotoRvClickListener rvClickListener = null;
    private static onInfoReviewDetailPhotoRvClickListener rvDetailClickListener = null;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    // InfoActivity 리뷰 내 사진 클릭 생성자
    public InfoReviewPhotoRvAdapter(Context context
            , onInfoReviewPhotoRvClickListener clickListener
            , ArrayList<infoPhotoData> photos
            , int reviewId){
        this.mContext = context;
        rvClickListener = clickListener;
        this.Photos = photos;
        this.reviewId = reviewId;
    }

    // ReviewActivity 리뷰 사진 클릭 생성자
    public InfoReviewPhotoRvAdapter(Context context
            , onInfoReviewDetailPhotoRvClickListener clickListener
            , ArrayList<infoPhotoData> photos
            , int reviewId){
        this.mContext = context;
        rvDetailClickListener = clickListener;
        this.Photos = photos;
        this.reviewId = reviewId;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomInfoReviewPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_review_photo, parent, false);

        return new InfoReviewPhotoRvAdapter.CustomInfoReviewPhotoViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomInfoReviewPhotoViewHolder holder, int position) {
        infoPhotoData photo = Photos.get(position);     // 현재 position의 사진 정보

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(photo.getPhotoPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);           // 이미지를 보여줄 View를 지정

        holder.photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rvClickListener == null){    // 리뷰 상세 화면에서 클릭
                    rvDetailClickListener.onInfoReviewDetailPhotoRvClick(view, holder.getAbsoluteAdapterPosition(), reviewId);
                }else{  // Info 화면에서 클릭
                    rvClickListener.onInfoReviewPhotoRvClick(view, holder.getAbsoluteAdapterPosition(), reviewId);
                }
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
    public static class CustomInfoReviewPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;   // 사진 이미지

        public CustomInfoReviewPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImage = itemView.findViewById(R.id.info_review_photo_image);
        }
    }
}
