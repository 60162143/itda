package com.example.itda.ui.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.util.ArrayList;

public class InfoMenuRvAdapter extends RecyclerView.Adapter<InfoMenuRvAdapter.CustomInfoMenuViewHolder> {
    private ArrayList<infoMenuData> Menus;    // 메뉴 데이터
    private boolean menuPlusBtn;          // 메뉴 더보기 버튼을 클릭했는지 여부

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    public InfoMenuRvAdapter(Context context, ArrayList<infoMenuData> menus, boolean menuPlusBtn){
        this.Menus = menus;
        this.mContext = context;
        this.menuPlusBtn = menuPlusBtn;
    }

    @NonNull
    @Override
    public InfoMenuRvAdapter.CustomInfoMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_menu, parent, false);
        return new InfoMenuRvAdapter.CustomInfoMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoMenuRvAdapter.CustomInfoMenuViewHolder holder, int position) {
        infoMenuData menu = Menus.get(position);     // 현재 position의 가게 정보
        holder.infoMenuName.setText(menu.getMenuName());   // 협업 할인율 textView set
        if(menu.getMenuPrice() != 0){
            holder.infoMenuPrice.setText(String.valueOf(menu.getMenuPrice()) + " 원");   // 협업 할인율 textView set
        }else{
            holder.infoMenuPrice.setText("가격 미등록");   // 협업 할인율 textView set
        }
    }

    @Override
    public int getItemCount() {
        return menuPlusBtn ? Menus.size() : Math.min(Menus.size(), 3);
    }

    public static class CustomInfoMenuViewHolder extends RecyclerView.ViewHolder {
        TextView infoMenuDot;       // 협업 가게 할인 율
        TextView infoMenuName;       // 협업 가게 할인 율
        TextView infoMenuPrice;          // 협업 가게 이름

        public CustomInfoMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            infoMenuDot = (TextView) itemView.findViewById(R.id.info_menu_center_dot);
            infoMenuName = (TextView) itemView.findViewById(R.id.info_menu_name);
            infoMenuPrice = (TextView) itemView.findViewById(R.id.info_menu_price);
        }
    }
}
