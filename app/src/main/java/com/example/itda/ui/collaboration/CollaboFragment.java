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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class CollaboFragment extends Fragment {

    public RecyclerView collaboRv;  // 협업 리사이클러뷰
    public LinearLayoutManager llm; // 협업 정보 저장
    public ArrayList<collaboData> Collabos = new ArrayList<>();  // 협업 정보 저장

    public CollaboRvAdapter CollaboAdapter;

    public static RequestQueue requestQueue;

    private String HOST;        // Host 정보
    private String COLLABO_URL; // 협업 가게 정보 데이터 조회 Rest API

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_collabo, container, false);

        COLLABO_URL = ((globalVariable) requireActivity().getApplication()).getCollaboPath(); // 협업 정보 데이터 조회 Rest API
        HOST = ((globalVariable) requireActivity().getApplication()).getHost();    // Host 정보

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        collaboRv = root.findViewById(R.id.collabo_rv);

        getCollabo();

        return root;
    }

    public void getCollabo(){
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);     // 수직 레이아웃으로 설정

        collaboRv.setHasFixedSize(true);
        collaboRv.setLayoutManager(llm);

        StringRequest collaboRequest = new StringRequest(Request.Method.GET, HOST + COLLABO_URL, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray collaboArr = jsonObject.getJSONArray("collabo");
                for(int i = 0; i < collaboArr.length(); i++){
                    JSONObject object = collaboArr.getJSONObject(i);

                    collaboData collaboData = new collaboData(object.getInt("collaboId") // 협업 고유 아이디
                            , object.getInt("prvStoreId")                                // 앞 가게 고유 아이디
                            , object.getInt("postStoreId")                               // 뒷 가게 고유 아이디
                            , object.getInt("prvDiscountCondition")                      // 앞 가게 할인 조건 금액
                            , object.getInt("postDiscountRate")                          // 뒷 가게 할인 율
                            , object.getString("prvStoreName")                           // 앞 가게 명
                            , object.getString("postStoreName")                          // 뒷 가게 명
                            , HOST + object.getString("prvStoreImagePath")               // 앞 가게 썸네일 이미지
                            , HOST + object.getString("postStoreImagePath")              // 뒷 가게 썸네일 이미지
                            , Float.valueOf(String.format("%.2f", object.getDouble("collaboDistance"))));      // 가게 간 거리
                    Collabos.add(collaboData);
                }

                CollaboAdapter = new CollaboRvAdapter(getActivity(), Collabos);
                collaboRv.setAdapter(CollaboAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.d("getCollaboError", "onErrorResponse : " + error));
        collaboRequest.setShouldCache(false);
        requestQueue.add(collaboRequest);
    }
}