package com.example.itda.ui.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itda.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class InfoOrderActivity extends AppCompatActivity implements onInfoOrderMenuRvClickListener, onInfoOrderSelectMenuRvClickListener {

    // Layout
    private ImageButton infoOrderBackBtn;   // 상단 뒤로가기 버튼
    private Button infoOrderStoreName;  // 상단 가게 이름
    private Button infoOrderBtn;        // 주문하기 버튼
    private TextView infoOrderSelectMenuNoItemTitle;    // 선택한 메뉴 없을 시 보여주는 안내 텍스트
    private RecyclerView infoOrderMenuRv;       // 가게 메뉴 리사이클러뷰
    private RecyclerView infoOrderSelectMenuRv; // 선택한 메뉴 리사이클러뷰


    // Adapter
    private InfoOrderSelectMenuRvAdapter infoOrderSelectMenuAdapter;    // 선택한 메뉴 리사이클러뷰 어뎁터


    // Intent activityResultLauncher
    private ActivityResultLauncher<Intent> activityResultLauncher;  // Intent형 activityResultLauncher 객체 생성


    // Data
    private ArrayList<infoMenuData> Menu = new ArrayList<>();   // 전체 메뉴 데이터
    private final ArrayList<infoMenuData> selectMenu = new ArrayList<>(); // 선택한 메뉴 데이터

    // Global Data
    private int storeId;    // 가게 고유 아이디
    private int userId;     // 유저 고유 아이디
    private String storeName;   // 가게 명

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_order);

        // Init View
        initView();

        // Intent Data SET
        storeId = getIntent().getExtras().getInt("storeId");   // 가게 고유 아이디
        userId = getIntent().getExtras().getInt("userId");    // 유저 고유 아이디
        storeName = getIntent().getExtras().getString("storeName"); // 가게 명
        // ArrayList를 받아올때 사용
        // putParcelableArrayListExtra로 넘긴 데이터를 받아올때 사용
        Menu = getIntent().getParcelableArrayListExtra("Menu"); // 메뉴 데이터


        // 가게 명 SET
        infoOrderStoreName.setText(storeName);

        // 가게 메뉴 리사이클러뷰 SET
        // LayoutManager 객체 생성
        infoOrderMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        // 가게 메뉴 리사이클러뷰 어뎁터
        InfoOrderMenuRvAdapter infoOrderMenuAdapter = new InfoOrderMenuRvAdapter(this, this, Menu); // 리사이클러뷰 어뎁터 객체 생성
        infoOrderMenuRv.setAdapter(infoOrderMenuAdapter);   // 리사이클러뷰 어뎁터 객체 지정

        // 선택한 메뉴 리사이클러뷰 SET
        // LayoutManager 객체 생성
        infoOrderSelectMenuRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        infoOrderSelectMenuAdapter = new InfoOrderSelectMenuRvAdapter(this, this, selectMenu);  // 리사이클러뷰 어뎁터 객체 생성
        infoOrderSelectMenuRv.setAdapter(infoOrderSelectMenuAdapter);   // 리사이클러뷰 어뎁터 객체 지정


        // 뒤로 가기 버튼 클릭 시 Activity 종료
        infoOrderBackBtn.setOnClickListener(view -> finish());

        // 주문 버튼 클릭 리스너
        infoOrderBtn.setOnClickListener(view -> {

            if(getTotalMenuPrice(selectMenu) == 0){   // 주문할 메뉴 수량이 없는 경우
                StyleableToast.makeText(getApplicationContext(), "주문할 메뉴를 선택해 주세요.", R.style.redCenterToast).show();
            }else{
                Intent intent = new Intent(InfoOrderActivity.this, InfoPaymentActivity.class);

                intent.putExtra("storeId", storeId);    // 가게 고유 아이디
                intent.putExtra("userId", userId);      // 유저 고유 아이디
                intent.putExtra("storeName", storeName);    // 가게 명

                // 메뉴 수량이 1개 이상인 메뉴만 담기
                ArrayList<infoMenuData> paymentSelectMenu = new ArrayList<>();

                for(int i = 0; i < selectMenu.size(); i++){
                    if(selectMenu.get(i).getMenuCount() > 0){
                        paymentSelectMenu.add(selectMenu.get(i));
                    }
                }

                // 데이터 송신을 위한 Parcelable interface 사용
                // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                intent.putParcelableArrayListExtra("selectMenu", paymentSelectMenu);   // 메뉴 데이터

                activityResultLauncher.launch(intent);
            }
        });

        // activityResultLauncher 초기화
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 1000) { // resultCode가 1000으로 넘어왔다면 결제 완료
                finish();
            }
        });
    }

    private void initView(){
        infoOrderBackBtn = findViewById(R.id.info_order_back_ic);   // 상단 뒤로가기 버튼
        infoOrderStoreName = findViewById(R.id.info_order_main_store_name); // 상단 가게 이름
        infoOrderBtn = findViewById(R.id.info_order_btn);   // 결제 버튼
        infoOrderMenuRv = findViewById(R.id.info_order_menu_rv);    // 가게 메뉴 리사이클러뷰
        infoOrderSelectMenuRv = findViewById(R.id.info_order_select_menu_rv);   // 선택한 메뉴 리사이클러뷰
        infoOrderSelectMenuNoItemTitle = findViewById(R.id.info_order_select_menu_no_item_title);   // 선택한 메뉴 없을 시 보여주는 안내 텍스트
    }

    // 가게 메뉴 리사이클러뷰 클릭 리스너
    @Override
    public void onInfoOrderMenuRvClick(View v, int position, String flag) {
        if(flag.equals("total")){
            boolean isMenuExist = false;    // 선택한 메뉴에 이미 메뉴가 있는지 확인 Flag
            int index = 0;  // 선택한 메뉴에 이미 메뉴가 있는 경우 선택한 메뉴 데이터의 index

            // 이미 선택한 메뉴 데이터에 있는지 확인
            if(selectMenu.size() > 0){
                for(int i = 0; i < selectMenu.size(); i++){
                    if(selectMenu.get(i).getMenuId() == Menu.get(position).getMenuId()){
                        isMenuExist = true;
                        index = i;
                        break;
                    }
                }
            }

            if(isMenuExist){    // 메뉴 데이터가 이미 있는 경우
                selectMenu.get(index).setMenuCount(selectMenu.get(index).getMenuCount() + 1);   // 동일 메뉴 수량 +1

                // 리사리아클러뷰 데이터 SET
                infoOrderSelectMenuAdapter.setMenu(selectMenu);
                infoOrderSelectMenuAdapter.notifyItemChanged(index);
            }else{  // 메뉴 데이터가 없었던 경우
                selectMenu.add(Menu.get(position)); // 메뉴 데이터 새로 추가

                // 리사이클러뷰 데이터 SET
                infoOrderSelectMenuAdapter.setMenu(selectMenu);
                infoOrderSelectMenuAdapter.notifyItemInserted(selectMenu.size() - 1);
            }
        }

        // 선택한 목록이 없을 경우 안내 텍스트 보이기
        if(selectMenu.size() == 0){
            infoOrderSelectMenuNoItemTitle.setVisibility(View.VISIBLE);
        }else{
            infoOrderSelectMenuNoItemTitle.setVisibility(View.GONE);
        }


        DecimalFormat myFormatter = new DecimalFormat("###,###");   // 문자열 형식 변경 Formatter ( 숫자 + 콤마 )

        // 결제 버튼 총 금액 SET
        String infoOrderBtnTxt = myFormatter.format(getTotalMenuPrice(selectMenu)) + "원 주문하기";
        infoOrderBtn.setText(infoOrderBtnTxt);
    }

    // 선택한 메뉴 리사이클러뷰 클릭 리스너
    @Override
    public void onInfoOrderSelectMenuRvClick(View v, int position, String flag) {
        switch (flag) {
            case "minus":    // - 버튼 클릭 시
                // 개수가 1개 이상일 경우만 -1
                if (selectMenu.get(position).getMenuCount() > 0) {
                    selectMenu.get(position).setMenuCount(selectMenu.get(position).getMenuCount() - 1); // 동일 메뉴 수량 -1

                    // 리사이클러뷰 데이터 SET
                    infoOrderSelectMenuAdapter.setMenu(selectMenu);
                    infoOrderSelectMenuAdapter.notifyItemChanged(position);
                }
                break;
            case "plus":   // + 버튼 클릭 시
                selectMenu.get(position).setMenuCount(selectMenu.get(position).getMenuCount() + 1); // 동일 메뉴 수량 +1

                // 리사이클러뷰 데이터 SET
                infoOrderSelectMenuAdapter.setMenu(selectMenu);
                infoOrderSelectMenuAdapter.notifyItemChanged(position);
                break;
            case "delete":
                selectMenu.remove(position);    // 메뉴 데이터 제거

                // 리사이클러뷰 데이터 SET
                infoOrderSelectMenuAdapter.setMenu(selectMenu);
                infoOrderSelectMenuAdapter.notifyItemRemoved(position);
                break;
        }

        // 선택한 목록이 없을 경우 안내 텍스트 보이기
        if(selectMenu.size() == 0){
            infoOrderSelectMenuNoItemTitle.setVisibility(View.VISIBLE);
        }else{
            infoOrderSelectMenuNoItemTitle.setVisibility(View.GONE);
        }

        // 숫자 형식 SET ( 콤마 추가 )
        DecimalFormat myFormatter = new DecimalFormat("###,###");   // 문자열 형식 변경 Formatter ( 숫자 + 콤마 )

        // 결제 버튼 총 금액 SET
        String infoOrderBtnTxt = myFormatter.format(getTotalMenuPrice(selectMenu)) + "원 주문하기";
        infoOrderBtn.setText(infoOrderBtnTxt);
    }

    // 주문 총 금액 Return
    public int getTotalMenuPrice(ArrayList<infoMenuData> menus){
        int totalPrice = 0; // 최종 금액

        // 메뉴 가격 * 메뉴 수량
        if(menus != null && menus.size() > 0){
            for(int i = 0; i < menus.size(); i++){
                totalPrice += menus.get(i).getMenuPrice() * menus.get(i).getMenuCount();
            }
        }

        return totalPrice;
    }
}