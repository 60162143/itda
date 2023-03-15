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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollaboFragment extends Fragment {

    public RecyclerView CollaboRv;
    public LinearLayoutManager llm;
    public ArrayList<collaboData> collabo = new ArrayList<>();

    public CollaboRvAdapter CollaboAdapter;

    public static RequestQueue requestQueue;

    final static private String MAIN_URL = "127.0.0.1";
    final static private String COLLABORATION_URL = "127.0.0.1/collaboStore2.php";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_collabo, container, false);

        CollaboRv = root.findViewById(R.id.collabo_rv);

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getActivity());
        }

        makeCollabo();

        return root;
    }

    public void makeCollabo(){
        llm = new LinearLayoutManager(getActivity());

        CollaboRv.setHasFixedSize(true);
        CollaboRv.setLayoutManager(llm);

        StringRequest CollaboRequest = new StringRequest(Request.Method.GET, COLLABORATION_URL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray collaboArr = jsonObject.getJSONArray("collabo");
                    for(int i = 0; i < collaboArr.length(); i++){
                        JSONObject objectInArray = collaboArr.getJSONObject(i);
                        collaboData collaboData = new collaboData(objectInArray.getInt("CollaboId"), objectInArray.getInt("FrontStoreId"), objectInArray.getInt("BackStoreId"),objectInArray.getString("FrontStoreName"), objectInArray.getString("BackStoreName"), MAIN_URL + objectInArray.getString("FrontStoreThumbnail"), MAIN_URL + objectInArray.getString("BackStoreThumbnail"), objectInArray.getString("DiscountConditional"), objectInArray.getString("Discount"));
                        collabo.add(collaboData);
                    }
                    CollaboAdapter = new CollaboRvAdapter();
                    CollaboAdapter.setCollaboes(collabo);
                    CollaboRv.setAdapter(CollaboAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse : " + String.valueOf(error));
            }
        });
        CollaboRequest.setShouldCache(false);
        requestQueue.add(CollaboRequest);
    }
}