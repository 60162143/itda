package com.example.itda.ui.collaboration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.itda.R;
import com.example.itda.ui.global.globalVariable;
import com.example.itda.ui.home.HomeSearchActivity;
import com.example.itda.ui.home.MainStoreRvAdapter;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CollaboRvAdapter extends RecyclerView.Adapter<CollaboRvAdapter.CustomCollaboViewHolder>{
    private ArrayList<collaboData> Collaboes;   // 협업 데이터
    private String HOST;        // Host 정보
    private String STORE_URL;   // 가게 정보 데이터 조회 Rest API
    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue

    private LocationManager lm;     // 위치 관리자 객체
    private Location locCurrent;   // 현재 위치 객체
    private boolean gpsPossible = false;    // Gps 사용 가능 여부

    // Activity Content
    // 어플리케이션의 현재 상태를 갖고 있음
    // 시스템이 관리하고 있는 액티비티, 어플리케이션의 정보를 얻기 위해 사용
    private final Context mContext;

    public CollaboRvAdapter(Context context, ArrayList<collaboData> collabos){
        this.mContext = context;
        this.Collaboes = collabos;
    }

    // Gps 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            gpsPossible = true;
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(mContext, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    // Gps 권한 허용 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(mContext)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                        //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    @NonNull
    @Override
    public CollaboRvAdapter.CustomCollaboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_collabo, parent, false);

        STORE_URL = ((globalVariable) mContext.getApplicationContext()).getMainStorePath(); // 가게 상세 데이터 조회 Rest API
        HOST = ((globalVariable) mContext.getApplicationContext()).getHost();               // Host 정보

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }

        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermissions(); // Gps 권한 확인
        }else{
            // 현재 위치 좌표
            // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
            // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
            // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
            locCurrent = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            gpsPossible = true; // Gps 활성화 체크
        }
        return new CustomCollaboViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaboRvAdapter.CustomCollaboViewHolder holder, int position) {
        collaboData collabo = Collaboes.get(position);

        // 앞가게
        holder.prv_store_name.setText(collabo.getPrvStoreName());   // 앞 가게 명

        // 앞 가게 썸네일 이미지
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPrvStoreImagePath()))
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)
                .fallback(R.drawable.ic_fallback)
                .into(holder.prv_store_image);
        holder.prv_store_discount_condition.setText(collabo.getPrvDiscountCondition() + "원 이상 결제");

        // 앞 가게 이미지 클릭 리스너
        holder.prv_store_image.setOnClickListener(view -> {
            String storePath = STORE_URL + "?storeId=" + collabo.getPrvStoreId();
            System.out.println(storePath);

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
            StringRequest storeRequest = new StringRequest(Request.Method.GET, HOST + storePath, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                    JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                    JSONObject object = mainStoreArr.getJSONObject(0);          // JSONObject 생성

                    float distance = 0;   // 현위치에서 가게까지의 거리

                    // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                    if (gpsPossible) {
                        // 가게 위치 좌표
                        Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                        point.setLatitude(object.getDouble("storeLatitude"));
                        point.setLongitude(object.getDouble("storeLongitude"));

                        distance = locCurrent.distanceTo(point);
                    }

                    mainStoreData prvStore = new mainStoreData(
                            object.getInt("storeId")                      // 가게 고유 아이디
                            , object.getString("storeName")                 // 가게 이름
                            , object.getString("storeAddress")              // 가게 주소
                            , object.getString("storeDetail")               // 가게 간단 제공 서비스
                            , object.getString("storeFacility")             // 가게 제공 시설 여부
                            , object.getDouble("storeLatitude")             // 가게 위도
                            , object.getDouble("storeLongitude")            // 가게 경도
                            , object.getString("storeNumber")               // 가게 번호
                            , object.getString("storeInfo")                 // 가게 간단 정보
                            , object.getInt("storeCategoryId")              // 가게가 속한 카테고리 고유 아이디
                            , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")                // 가게 별점
                            , object.getString("storeWorkingTime")          // 가게 운영 시간
                            , object.getString("storeHashTag")              // 가게 해시태그
                            , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                            , distance / 1000); // 현위치에서 가게까지의 거리

                    Intent intent = new Intent(mContext, InfoActivity.class);  // 가게 검색 Activity 화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putExtra("Store", prvStore);

                    mContext.startActivity(intent); // 새 Activity 인스턴스 시작

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("getPrvStoreError", "onErrorResponse : " + error);
            });

            storeRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(storeRequest);     // RequestQueue에 요청 추가
        });

        // 뒷가게
        holder.post_store_name.setText(collabo.getPostStoreName()); // 뒷 가게 명

        // 뒷 가게 썸네일 이미지
        Glide.with(holder.itemView)
                .load(Uri.parse(collabo.getPostStoreImagePath()))
                .placeholder(R.drawable.logo)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.ic_error)
                .fallback(R.drawable.ic_fallback)
                .into(holder.post_store_image);
        holder.post_store_discount_rate.setText(collabo.getPostDiscountRate() + "% 할인");

        // 뒷 가게 이미지 클릭 리스너
        holder.post_store_image.setOnClickListener(view -> {
            String storePath = STORE_URL + "?storeId=" + collabo.getPostStoreId();

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
            StringRequest storeRequest = new StringRequest(Request.Method.GET, HOST + storePath, response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                    JSONArray mainStoreArr = jsonObject.getJSONArray("store");  // 객체에 store라는 Key를 가진 JSONArray 생성

                    JSONObject object = mainStoreArr.getJSONObject(0);          // JSONObject 생성

                    float distance = 0;   // 현위치에서 가게까지의 거리

                    // Gps 권한 설정이 되어 있을 경우 현위치에서 가게까지의 거리 계산 및 설정
                    if (gpsPossible) {
                        // 가게 위치 좌표
                        Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                        point.setLatitude(object.getDouble("storeLatitude"));
                        point.setLongitude(object.getDouble("storeLongitude"));

                        distance = locCurrent.distanceTo(point);
                    }

                    mainStoreData postStore = new mainStoreData(
                            object.getInt("storeId")                      // 가게 고유 아이디
                            , object.getString("storeName")                 // 가게 이름
                            , object.getString("storeAddress")              // 가게 주소
                            , object.getString("storeDetail")               // 가게 간단 제공 서비스
                            , object.getString("storeFacility")             // 가게 제공 시설 여부
                            , object.getDouble("storeLatitude")             // 가게 위도
                            , object.getDouble("storeLongitude")            // 가게 경도
                            , object.getString("storeNumber")               // 가게 번호
                            , object.getString("storeInfo")                 // 가게 간단 정보
                            , object.getInt("storeCategoryId")              // 가게가 속한 카테고리 고유 아이디
                            , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                            , object.getDouble("storeScore")                // 가게 별점
                            , object.getString("storeWorkingTime")          // 가게 운영 시간
                            , object.getString("storeHashTag")              // 가게 해시태그
                            , object.getInt("storeReviewCount")             // 가게 리뷰 개수
                            , distance / 1000); // 현위치에서 가게까지의 거리

                    Intent intent = new Intent(mContext, InfoActivity.class);  // 가게 검색 Activity 화면으로 이동하기 위한 Intent 객체 선언

                    // 데이터 송신을 위한 Parcelable interface 사용
                    // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                    intent.putExtra("Store", postStore);

                    mContext.startActivity(intent); // 새 Activity 인스턴스 시작

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("getPostStoreError", "onErrorResponse : " + error);
            });

            storeRequest.setShouldCache(false); // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(storeRequest);     // RequestQueue에 요청 추가
        });

        // 가게 간 거리
        holder.distance.setText(collabo.getCollaboDistance() + " km");
    }

    @Override
    public int getItemCount() {
        return Collaboes.size();
    }

    public class CustomCollaboViewHolder extends RecyclerView.ViewHolder {
        ImageButton prv_store_image;
        ImageButton post_store_image;
        TextView prv_store_name;
        TextView post_store_name;
        TextView prv_store_discount_condition;
        TextView post_store_discount_rate;
        TextView distance;

        public CustomCollaboViewHolder(@NonNull View itemView) {
            super(itemView);
            prv_store_image = itemView.findViewById(R.id.collabo_prv_store_image);
            post_store_image = itemView.findViewById(R.id.collabo_post_store_image);
            prv_store_name = itemView.findViewById(R.id.collabo_prv_store_name);
            post_store_name = itemView.findViewById(R.id.collabo_post_store_name);
            prv_store_discount_condition = itemView.findViewById(R.id.collabo_prv_store_discount_condition);
            post_store_discount_rate = itemView.findViewById(R.id.collabo_post_store_discount_rate);
            distance = itemView.findViewById(R.id.collabo_distance);
        }
    }
}
