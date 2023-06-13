package com.example.itda.ui.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;
import com.example.itda.ui.bag.BagPaymentMenuData;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InfoPaymentCompleteMenuRvAdapter extends RecyclerView.Adapter<InfoPaymentCompleteMenuRvAdapter.CustomInfoMenuViewHolder> {
    private ArrayList<InfoMenuData> Menus;    // 메뉴 데이터

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoPaymentCompleteMenuRvAdapter(Context context
            , ArrayList<InfoMenuData> menus){
        this.mContext = context;
        this.Menus = menus;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public InfoPaymentCompleteMenuRvAdapter.CustomInfoMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bag_payment_detail_menu, parent, false);

        return new InfoPaymentCompleteMenuRvAdapter.CustomInfoMenuViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull InfoPaymentCompleteMenuRvAdapter.CustomInfoMenuViewHolder holder, int position) {
        InfoMenuData menu = Menus.get(position);    // 현재 position의 메뉴 정보

        holder.bagPaymentDetailMenuName.setText(menu.getMenuName() + " " + menu.getMenuCount() + "개");   // 선택한 메뉴 명 + 주문 수량

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        holder.bagPaymentDetailMenuPrice.setText(myFormatter.format((menu.getMenuPrice() * menu.getMenuCount())) + "원");   // 선택한 메뉴 가격
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        return Menus.size();
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoMenuViewHolder extends RecyclerView.ViewHolder {
        TextView bagPaymentDetailMenuName;       // 선택한 메뉴 명
        TextView bagPaymentDetailMenuPrice;          // 선택한 메뉴 가격

        public CustomInfoMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            bagPaymentDetailMenuName = itemView.findViewById(R.id.bag_payment_detail_menu_name);
            bagPaymentDetailMenuPrice = itemView.findViewById(R.id.bag_payment_detail_menu_price);
        }
    }
}
