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

public class InfoMenuRvAdapter extends RecyclerView.Adapter<InfoMenuRvAdapter.CustomInfoMenuViewHolder> {
    private final ArrayList<infoMenuData> Menus;    // 메뉴 데이터
    private final boolean menuPlusBtn;  // 메뉴 더보기 버튼을 클릭했는지 여부

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    // Constructor
    public InfoMenuRvAdapter(Context context, ArrayList<infoMenuData> menus, boolean menuPlusBtn){
        this.Menus = menus;
        this.mContext = context;
        this.menuPlusBtn = menuPlusBtn;
    }

    // ViewHolder를 새로 만들어야 할 때 호출
    // 각 아이템을 위한 XML 레이아웃을 활용한 뷰 객체를 생성하고 이를 뷰 홀더 객체에 담아 리턴
    @NonNull
    @Override
    public InfoMenuRvAdapter.CustomInfoMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // layoutInflater로 xml객체화. viewHolder 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_info_menu, parent, false);

        return new InfoMenuRvAdapter.CustomInfoMenuViewHolder(view);
    }

    // ViewHolder를 어떠한 데이터와 연결할 때 호출
    // 뷰 홀더 객체들의 레이아웃을 채움
    // position 이라는 파라미터를 활용하여 데이터의 순서에 맞게 아이템 레이아웃을 바인딩 가능
    @Override
    public void onBindViewHolder(@NonNull InfoMenuRvAdapter.CustomInfoMenuViewHolder holder, int position) {
        infoMenuData menu = Menus.get(position);    // 현재 position의 가게 정보

        // 메뉴 명 SET
        holder.infoMenuName.setText(menu.getMenuName());

        // 메뉴 가격 SET
        String infoMenuPriceTxt;
        if(menu.getMenuPrice() != 0){   // 메뉴 가격이 있을 경우
            // 숫자 형식 SET ( 콤마 추가 )
            DecimalFormat numberFormatter = new DecimalFormat("###,###");   // 문자열 형식 변경 Formatter ( 숫자 + 콤마 )
            infoMenuPriceTxt = numberFormatter.format(menu.getMenuPrice()) + "원";
        }else{  // 메뉴 가격이 없을 경우
            infoMenuPriceTxt = "가격 미등록";
        }
        holder.infoMenuPrice.setText(infoMenuPriceTxt);
    }

    // RecyclerView Adapter에서 관리하는 아이템의 개수를 반환
    @Override
    public int getItemCount() {
        // 가게 상세화면에서는 메뉴 최대 3개 return
        // 메뉴 상세화면에서 메뉴 전체 return
        return menuPlusBtn ? Menus.size() : Math.min(Menus.size(), 3);
    }

    // adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
    // itemView를 저장하는 custom viewHolder 생성
    // findViewById & 각종 event 작업
    public static class CustomInfoMenuViewHolder extends RecyclerView.ViewHolder {
        TextView infoMenuName;  // 메뉴 명
        TextView infoMenuPrice; // 메뉴 가격

        public CustomInfoMenuViewHolder(@NonNull View itemView) {
            super(itemView);

            infoMenuName = itemView.findViewById(R.id.info_menu_name);
            infoMenuPrice = itemView.findViewById(R.id.info_menu_price);
        }
    }
}
