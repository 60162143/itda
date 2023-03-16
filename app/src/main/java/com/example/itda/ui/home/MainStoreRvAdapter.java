package com.example.itda.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.info.InfoActivity;

import java.util.ArrayList;

// ViewHolder 패턴은, 각 뷰의 객체를 ViewHolder에 보관함으로써 뷰의 내용을 업데이트 하기 위한
// findViewById() 메소드 호출을 줄여 효과적으로 퍼포먼스 개선을 할 수 있는 패턴이다.
// ViewHolder 패턴을 사용하면, 한 번 생성하여 저장했던 뷰는 다시 findViewById() 를 통해 뷰를 불러올 필요가 사라지게 된다.
public class MainStoreRvAdapter extends RecyclerView.Adapter<MainStoreRvAdapter.CustomMainCategoryViewHolder>{

    private ArrayList<mainStoreData> Stores = new ArrayList<>();    // 가게 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;
    private Intent intent;  // 상세 페이지로 전환을 위한 객체

    // Constructor
    public MainStoreRvAdapter(Context context){
        this.mContext = context;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public MainStoreRvAdapter.CustomMainCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_store, parent, false);
        return new CustomMainCategoryViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull MainStoreRvAdapter.CustomMainCategoryViewHolder holder, int position) {
        mainStoreData store = Stores.get(position);     // 현재 position의 가게 정보

        // 안드로이드에서 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
        // 이미지를 빠르고 부드럽게 스크롤 하는 것을 목적
        Glide.with(holder.itemView)                 // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(store.getStoreThumbnailPath()))     // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .error(R.drawable.ic_error)         // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.ic_fallback)   // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.main_store_image);     // 이미지를 보여줄 View를 지정

        holder.main_store_text.setText(store.getStoreName());   // 가게 이름 textView set

        //메인 화면 이미지 클릭 시 버튼 리스너
        holder.main_store_image.setOnClickListener(v -> {
            intent = new Intent(mContext, InfoActivity.class);  // 상세화면으로 이동하기 위한 Intent 객체 선언

            // 데이터 송신을 위한 Parcelable interface 사용
            // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
            intent.putExtra("Store", (Parcelable) store);

            mContext.startActivity(intent); // 새 Activity 인스턴스 시작
        });
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Stores.size();
    }

    // 가게 정보 Setter
    public void setStores(ArrayList<mainStoreData> stores){
        this.Stores = stores;
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomMainCategoryViewHolder extends RecyclerView.ViewHolder {
        ImageButton main_store_image;   // 가게 썸네일
        TextView main_store_text;       // 가게 이름

        public CustomMainCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            main_store_image = itemView.findViewById(R.id.store_image);
            main_store_text = itemView.findViewById(R.id.store_name);
        }
    }

}
