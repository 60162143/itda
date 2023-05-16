package com.example.itda.ui.info;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
public class InfoReviewInsertPhotoRvAdapter extends RecyclerView.Adapter<InfoReviewInsertPhotoRvAdapter.CustomInfoPhotoViewHolder>{

    //private final ArrayList<infoPhotoData> Photos;  // 사진 데이터
    private ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onInfoReviewInsertPhotoRvClickListener rvClickListener = null;

    private boolean existDataFlag = false;

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoReviewInsertPhotoRvAdapter(Context context
            , onInfoReviewInsertPhotoRvClickListener clickListener
            , ArrayList<Uri> uriList){

        this.mContext = context;
        rvClickListener = clickListener;
        this.uriList = uriList;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CustomInfoPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_review_insert_photo, parent, false);

        return new InfoReviewInsertPhotoRvAdapter.CustomInfoPhotoViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CustomInfoPhotoViewHolder holder, int position) {
        Uri photoUri = uriList.get(position); // 현재 position의 사진 정보

        // 사진 추가 이미지일 경우 이미지 삭제 버튼 숨기기
        if(uriList.size() - 1 == position){
            holder.photoImageDelBtn.setVisibility(View.GONE);
        }else{
            holder.photoImageDelBtn.setVisibility(View.VISIBLE);
        }

        // 사진 이미지
        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(photoUri)     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);           // 이미지를 보여줄 View를 지정

//        if(uriList.size() == 1){
//            // 사진 이미지
//            // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
//            // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
//            Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
//                    .load(photoUri)     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
//                    .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
//                    .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
//                    .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
//                    .into(holder.photoImage);           // 이미지를 보여줄 View를 지정
//        }else{
//            // 사진 이미지
//            // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
//            // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
//            Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
//                    .load(photoUri)     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
//                    .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
//                    .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
//                    .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
//                    .into(holder.photoImage);           // 이미지를 보여줄 View를 지정
//        }
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        // 사진 이미지 최대 10개 return
        return uriList.size();
    }

    public void setUriList(ArrayList<Uri> uriList){
        this.uriList = uriList;
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;   // 사진 이미지
        ImageButton photoImageDelBtn;   // 사진 이미지 삭제 버튼

        public CustomInfoPhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImage = itemView.findViewById(R.id.info_review_insert_photo_image);
            photoImageDelBtn = itemView.findViewById(R.id.info_review_insert_photo_del_btn);

            // 리사이클러뷰 사진 이미지 클릭 이벤트 인터페이스 구현
            photoImage.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onInfoReviewInsertPhotoRvClick(view, getAbsoluteAdapterPosition(), "image");
                }
            });

            // 리사이클러뷰 사진 제거 버튼 클릭 이벤트 인터페이스 구현
            photoImageDelBtn.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onInfoReviewInsertPhotoRvClick(view, getAbsoluteAdapterPosition(), "delete");
                }
            });
        }
    }
}
