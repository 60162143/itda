package com.example.itda.ui.collaboration;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;
import com.example.itda.ui.home.mainBookmarkCollaboData;
import com.example.itda.ui.home.mainBookmarkStoreData;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class CollaboRvAdapter extends RecyclerView.Adapter<CollaboRvAdapter.CustomCollaboViewHolder>{
    private final ArrayList<collaboData> Collaboes;   // 협업 데이터
    private ArrayList<mainBookmarkCollaboData> BookmarkCollabos;    // 찜한 협업 목록 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // 리사이클러뷰 클릭 리스너 인터페이스
    private static onCollaboRvClickListener rvClickListener = null;

    // Constructor
    public CollaboRvAdapter(Context context
            , ArrayList<collaboData> collabos
            , ArrayList<mainBookmarkCollaboData> bookmarkCollabos){
        this.mContext = context;
        this.Collaboes = collabos;
        this.BookmarkCollabos = bookmarkCollabos;
    }


    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public CollaboRvAdapter.CustomCollaboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_collabo, parent, false);

        return new CustomCollaboViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull CollaboRvAdapter.CustomCollaboViewHolder holder, int position) {
        collaboData collabo = Collaboes.get(position);  // 현재 Position 협업 데이터

        // 앞가게
        holder.prv_store_name.setText(collabo.getPrvStoreName());   // 앞 가게 명

        // 앞 가게 썸네일 이미지
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPrvStoreImagePath()))    // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.prv_store_image);      // 이미지를 보여줄 View를 지정

        // 앞 가게 할인 조건
        String discountCondition = collabo.getPrvDiscountCondition() + "원 이상 결제";
        holder.prv_store_discount_condition.setText(discountCondition);

        // 뒷가게
        holder.post_store_name.setText(collabo.getPostStoreName()); // 뒷 가게 명

        // 뒷 가게 썸네일 이미지
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPostStoreImagePath()))
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)
                .fallback(R.drawable.ic_fallback)
                .into(holder.post_store_image);

        // 뒷 가게 할인율
        String discountRate = collabo.getPostDiscountRate() + "% 할인";
        holder.post_store_discount_rate.setText(discountRate);

        // 가게 간 거리
        String distanceStr = collabo.getCollaboDistance() + " km";
        holder.distance.setText(distanceStr);

        // 로그인이 되어있을 경우 찜 버튼 보이기
        if(((globalMethod) mContext.getApplicationContext()).loginChecked()){
            holder.bookmarkBtn.setVisibility(View.VISIBLE);

            // 찜한 협업 목록이 있을 경우 찜 버튼 활성화
            if(BookmarkCollabos != null && BookmarkCollabos.size() > 0){
                for(int i = 0; i < BookmarkCollabos.size(); i++){
                    if(Collaboes.get(position).getCollaboId() == BookmarkCollabos.get(i).getCollaboId()){
                        holder.bookmarkBtn.setSelected(true);

                        break;
                    }
                }
            }
        }else{
            holder.bookmarkBtn.setVisibility(View.GONE);
        }
    }

    // 리스너 설정
    public void setonCollaboRvClickListener(onCollaboRvClickListener rvClickListener) {
        CollaboRvAdapter.rvClickListener = rvClickListener;
    }

//    // 찜한 가게 목록 설정
//    public void setbookmarkCollabos(ArrayList<mainBookmarkStoreData> bookmarkStores) {
//        BookmarkStores = bookmarkStores;
//    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Collaboes.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomCollaboViewHolder extends RecyclerView.ViewHolder {
        ImageButton prv_store_image;            // 앞 가게 썸네일 이미지
        ImageButton post_store_image;           // 뒷 가게 썸네일 이미지
        TextView prv_store_name;                // 앞 가게 명
        TextView post_store_name;               // 뒷 가게 명
        TextView prv_store_discount_condition;  // 앞 가게 할인 조건
        TextView post_store_discount_rate;      // 뒷 가게 할인 율
        TextView distance;                      // 가게 간 거리

        Button bookmarkBtn;                     // 협업 목록 찜 버튼

        public CustomCollaboViewHolder(@NonNull View itemView) {
            super(itemView);

            prv_store_image = itemView.findViewById(R.id.collabo_prv_store_image);
            post_store_image = itemView.findViewById(R.id.collabo_post_store_image);
            prv_store_name = itemView.findViewById(R.id.collabo_prv_store_name);
            post_store_name = itemView.findViewById(R.id.collabo_post_store_name);
            prv_store_discount_condition = itemView.findViewById(R.id.collabo_prv_store_discount_condition);
            post_store_discount_rate = itemView.findViewById(R.id.collabo_post_store_discount_rate);
            distance = itemView.findViewById(R.id.collabo_distance);
            bookmarkBtn = itemView.findViewById(R.id.collabo_bookmark);

            // 리사이클러뷰 앞가게 이미지 클릭 이벤트 인터페이스 구현
            prv_store_image.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onCollaboRvClick(view, getAbsoluteAdapterPosition(), "prvImage");
                }
            });

            // 리사이클러뷰 뒷가게 이미지 클릭 이벤트 인터페이스 구현
            post_store_image.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onCollaboRvClick(view, getAbsoluteAdapterPosition(), "postImage");
                }
            });

            // 리사이클러뷰 찜 버튼 클릭 이벤트 인터페이스 구현
            bookmarkBtn.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    if(bookmarkBtn.isSelected()){
                        bookmarkBtn.setSelected(false);
                        rvClickListener.onCollaboRvClick(view, getAbsoluteAdapterPosition(), "bookmarkDelete");
                    }else{
                        bookmarkBtn.setSelected(true);
                        rvClickListener.onCollaboRvClick(view, getAbsoluteAdapterPosition(), "bookmarkInsert");
                    }
                }
            });
        }
    }
}
