package com.example.itda.ui.info;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


// 리사이클러뷰 여백 주는 클래스
// 나중에 참고하자
public class InfoPhotoTotalRvDeco extends RecyclerView.ItemDecoration {
    private int size10;
    private int size5;
    private int size1;
    private int size2;

    public InfoPhotoTotalRvDeco(Context context){
        this.size10 = dpToPx(context, 10);
        this.size5 = dpToPx(context, 5);
        this.size1 = dpToPx(context, 1);
        this.size2 = dpToPx(context, 2);
    }

    // dp -> pixel 단위로 변경
    private int dpToPx(Context context, int dp) {

        return (int) TypedValue.applyDimension
                (TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        System.out.println("실행?");
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

//        //상하 설정
//        if(position == 0 || position == 1 || position == 2) {
//            // 첫번 째 줄 아이템
//            outRect.top = size10;
//            outRect.bottom = size10;
//        } else {
//            outRect.bottom = size10;
//        }

        // spanIndex = 0 -> 첫번째 열 아이템
        // spanIndex = 1 -> 두번째 열 아이템
        // spanIndex = 2 -> 세번째 열 아이템
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = lp.getSpanIndex();

//        if(spanIndex == 0) {
//            // 첫번째 열 아이템
//            outRect.left = size10;
//            outRect.right = size5;
//            outRect.bottom = size10;
//
//        } else {
//            // 두번째 열 아이템
//            outRect.left = size5;
//            outRect.right = size10;
//            outRect.bottom = size10;
//
//        }

//        if(spanIndex == 0) {
//            // 첫번째 열 아이템
//            outRect.left = size5;
//            outRect.right = size5;
//
//        } else if(spanIndex == 1) {
//            // 두번째 열 아이템
//            outRect.left = size5;
//            outRect.right = size5;
//
//        } else if(spanIndex == 2) {
//            // 세번째 열 아이템
//            outRect.left = size5;
//            outRect.right = size5;
//        }
    }
}