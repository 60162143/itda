package com.example.itda.ui.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InfoOrderMenuRvAdapter extends RecyclerView.Adapter<InfoOrderMenuRvAdapter.CustomInfoMenuViewHolder> {
    private final ArrayList<InfoMenuData> Menus;    // 메뉴 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    private static onInfoOrderMenuRvClickListener rvClickListener = null;

    // Constructor
    public InfoOrderMenuRvAdapter(Context context
            , onInfoOrderMenuRvClickListener clickListener
            , ArrayList<InfoMenuData> menus){
        this.mContext = context;
        rvClickListener = clickListener;
        this.Menus = menus;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public InfoOrderMenuRvAdapter.CustomInfoMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_order_menu, parent, false);

        return new InfoOrderMenuRvAdapter.CustomInfoMenuViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull InfoOrderMenuRvAdapter.CustomInfoMenuViewHolder holder, int position) {
        InfoMenuData menu = Menus.get(position);    // 현재 position의 가게 정보

        holder.infoMenuName.setText(menu.getMenuName());   // 메뉴 명

        if(menu.getMenuPrice() != 0){
            // 메뉴 가격
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            holder.infoMenuPrice.setText(myFormatter.format(menu.getMenuPrice()) + "원");    // 메뉴 가격
        }else{  // 메뉴 가격이 없을 경우
            holder.infoMenuPrice.setText("가격 미등록");
        }
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        // 가게 상세화면에서는 메뉴 최대 3개 return
        // 메뉴 상세화면에서 메뉴 전체 return
        return Menus.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoMenuViewHolder extends RecyclerView.ViewHolder {
        TextView infoMenuName;  // 메뉴명
        TextView infoMenuPrice; // 메뉴 가격

        public CustomInfoMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            infoMenuName = itemView.findViewById(R.id.info_order_menu_name);
            infoMenuPrice = itemView.findViewById(R.id.info_order_menu_price);

            // 리사이클러뷰 클릭 이벤트 인터페이스 구현
            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition(); // 현재 Position

                // 리스너 객체를 가진 Activity에 오버라이딩 된 클릭 함수 호출
                if(pos != RecyclerView.NO_POSITION){
                    rvClickListener.onInfoOrderMenuRvClick(view, getAbsoluteAdapterPosition(), "total");
                }
            });
        }
    }
}
