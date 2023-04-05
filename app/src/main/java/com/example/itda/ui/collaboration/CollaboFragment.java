package com.example.itda.ui.collaboration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollaboFragment extends Fragment {

    public RecyclerView collaboRv;  // 협업 리사이클러뷰
    public ArrayList<collaboData> Collabos = new ArrayList<>();  // 협업 정보 저장

    public CollaboRvAdapter CollaboAdapter; // 협업 리사이클러뷰 어뎁터

    public static RequestQueue requestQueue;    // Volley Library 사용을 위한 RequestQueue

    private String HOST;        // Host 정보
    private String COLLABO_URL; // 협업 가게 정보 데이터 조회 Rest API

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_collabo, container, false);

        COLLABO_URL = ((globalVariable) requireActivity().getApplication()).getCollaboPath();   // 협업 정보 데이터 조회 Rest API
        HOST = ((globalVariable) requireActivity().getApplication()).getHost();                 // Host 정보

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        collaboRv = root.findViewById(R.id.collabo_rv);

        getCollabo();   // 협업 데이터 GET

        return root;
    }

    // 협업 데이터 GET
    public void getCollabo(){
        StringRequest collaboRequest = new StringRequest(Request.Method.GET, HOST + COLLABO_URL, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                   // Response를 JsonObject 객체로 생성
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");    // 객체에 collabo라는 Key를 가진 JSONArray 생성
                for(int i = 0; i < collaboArr.length(); i++){
                    JSONObject object = collaboArr.getJSONObject(i);    // 배열 원소 하나하나 꺼내서 JSONObject 생성

                    collaboData collaboData = new collaboData(object.getInt("collaboId") // 협업 고유 아이디
                            , object.getInt("prvStoreId")                       // 앞 가게 고유 아이디
                            , object.getInt("postStoreId")                      // 뒷 가게 고유 아이디
                            , object.getInt("prvDiscountCondition")             // 앞 가게 할인 조건 금액
                            , object.getInt("postDiscountRate")                 // 뒷 가게 할인 율
                            , object.getString("prvStoreName")                  // 앞 가게 명
                            , object.getString("postStoreName")                 // 뒷 가게 명
                            , HOST + object.getString("prvStoreImagePath")      // 앞 가게 썸네일 이미지
                            , HOST + object.getString("postStoreImagePath")     // 뒷 가게 썸네일 이미지
                            , object.getString("collaboDistance"));             // 가게 간 거리

                    Collabos.add(collaboData);
                }
                // LayoutManager 객체 생성
                collaboRv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

                CollaboAdapter = new CollaboRvAdapter(getActivity(), Collabos); // 리사이클러뷰 어뎁터 객체 생성
                collaboRv.setAdapter(CollaboAdapter);   // 리사이클러뷰 어뎁터 객체 지정
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("getCollaboError", "onErrorResponse : " + error));

        collaboRequest.setShouldCache(false);   // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(collaboRequest);       // RequestQueue에 요청 추가
    }
}