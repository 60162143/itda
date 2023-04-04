package com.example.itda.ui.map;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalVariable;
import com.example.itda.ui.home.mainStoreData;
import com.example.itda.ui.info.InfoActivity;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// implement 받는 interface 종류
// CurrentLocationEventListener : 현위치 트래킹 이벤트를 통보 받을 수 있음
// MapViewEventListener : 지도 이동/확대/축소, 지도 화면 터치 (Single Tap / Double Tap / Long Press) 이벤트를 통보받을 수 있음
// POIItemEventListener : POI 관련 이벤트를 통보 받을 수 있음
// POI = Point Of Interest, 지도 내 마커를 의미
public class MapFragment extends Fragment implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, MapView.POIItemEventListener {

    private View root;                      // Fragment root view
    private ViewGroup mapViewContainer;     // mapView를 포함시킬 View Container
    private MapView mapView;                // 카카오 지도 View

    private LocationManager lm;          // 위치관리자 객체
    private LinearLayoutManager llm;     // 수평, 수직으로 ViewHolder 표현하기 위한 Layout 관리 클래스
    private InputMethodManager imm;      // 화면에 나오는 soft 키보드를 제어하는 클래스
    private Intent intent;               // 상세 화면 전환을 위한 변수

    private RecyclerView mapStoreRv;        // 가게 정보 리사이클러뷰
    private MapRvAdapter mapStoreAdapter;   // 가게 정보 리사이클러뷰 어뎁터

    private ArrayList<MapStoreData> map_store = new ArrayList<>();      // 지도 내 상점 정보
    private ArrayList<mainStoreData> select_store = new ArrayList<>();  // 선택한 가게의 Detail 정보

    private static RequestQueue requestQueue;    // Volley Library 사용을 위한 RequestQueue

    private String MAPSTORE_PATH;   // 지도 내 가게 데이터 조회 Rest API
    private String MAINSTORE_PATH; // 가게 세부 데이터 조회 Rest API
    private String HOST;        // Host 정보

    private boolean isTrackingMode = false;     // 현재 트래킹 모드인지 확인하는 Flag
    private int lastTag = -1;   // 상세화면 전환시 해당 태그 번호 저장 ( 현재 위치를 마지막 위치로 기억하기 위해 )

    // =========== 리사이클러뷰 addOnItemTouchListener 내 전역 변수 ================
    private boolean firstDragFlag = true;   // 리사이클러뷰 드래그 모드인지 확인하는 Flag
    private boolean dragFlag = false;       // 현재 터치가 드래그인지 확인하는 Flag
    private float startXPosition = 0;       // 터치 이벤트의 시작점의 X(가로)위치

    // =========================================================================

    //지도 권한 허용 확인
    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            initView();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(root.getContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    //지도 권한 허용 확인
    private void checkPermissions() {
        // 마시멜로(안드로이드 6.0) 이상 권한 체크
        TedPermission.with(root.getContext())
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                .setPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                        //android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 기기, 사진, 미디어, 파일 엑세스 권한
                )
                .check();
    }

    // 뷰 생성
    private void initView() {
        // ---------------- Rest API 전역변수 SET---------------------------
        MAPSTORE_PATH = ((globalVariable) requireActivity().getApplication()).getMapStorePath();   // 지도 내 가게 데이터 조회 Rest API
        MAINSTORE_PATH = ((globalVariable) requireActivity().getApplication()).getMainStorePath(); // 가게 정보 데이터 조회 Rest API
        HOST = ((globalVariable) requireActivity().getApplication()).getHost();    // Host 정보

        ImageButton mapGPSBtn = (ImageButton) root.findViewById((R.id.gps_button));         // GPS 버튼
        EditText mapSchText = (EditText) root.findViewById((R.id.search_map_store));        // 검색어 입력창
        ImageButton mapRefreshBtn = (ImageButton) root.findViewById((R.id.refresh_button)); // 새로고침 버튼

        mapStoreRv = root.findViewById(R.id.map_store_rv);  // 지도 내 가게 정보 리사이클러뷰

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireActivity());
        }

        // 시스템 - 레벨 서비스
        // 시스템에서 제공하는 디바이스나 안드로이드 프레임워크내 기능을 다른 어플리케이션과 공유하고자 시스템으로부터 객체를 얻을 떄 사용
        // INPUT_METHOD_SERVICE : 입력 방법을 관리하는 InputMethodManager
        imm = (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);

        // ======== 리사이클러뷰 Touch 이벤트 리스너 ===================
        mapStoreRv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                isTrackingMode = false;     // 현재 위치 트래킹 모드 해제
                // 현위치 트랙킹 모드 및 나침반 모드 Off
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

                // 하드웨어 X축 길이 구하기 위해 사용
                Display display  = requireActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();   // 좌표 각채 생성
                display.getSize(size);      // size 객체에 디바이스 실제 size 저장
                int display_width = size.x; // 디바이스 가로 길이 저장

                switch(e.getAction()){
                    // 누르고 움직였을 때
                    case MotionEvent.ACTION_MOVE:{
                        dragFlag = true;    // 드래그 중
                        if(firstDragFlag){  // 터치후 계속 드래그 하고 있다면 ACTION_MOVE가 계속 일어날 것임으로 무브를 시작한 첫번째 터치만 값을 저장
                            startXPosition = e.getX();  //첫번째 터치의 X(너비)를 저장
                            firstDragFlag = false;      //두번째 MOVE가 실행되지 못하도록 플래그 변경
                        }
                        break;

                    }
                    // 누른걸 땠을 때
                    case MotionEvent.ACTION_UP : {
                        float endPosition = e.getX();   // X 좌표
                        firstDragFlag = true;           // 드래그 모드 플래그

                        double sel_lat;     // 현재 Position의 위도
                        double sel_lon;     // 현재 Position의 경도

                        if(dragFlag){
                            // 슬라이드 범위 계산, 중간 이상으로 슬라이스 시 다음 또는 이전 Position 으로 이동
                            if((startXPosition < endPosition) && (endPosition - startXPosition) > 10 && endPosition > (float)display_width / 2){        // 왼쪽으로 슬라이드 ( 다음 Position 이동 )
                                mapStoreRv.smoothScrollToPosition(llm.findFirstVisibleItemPosition());  // 현재 뷰에서 최상단에 보이는 아이템의 위치로 이동

                                sel_lat = map_store.get(llm.findFirstVisibleItemPosition()).getMapStoreLatitude();  // 현재 뷰에서 최상단에 보이는 아이템의 위치의 위도
                                sel_lon = map_store.get(llm.findFirstVisibleItemPosition()).getMapStoreLongitude(); // 현재 뷰에서 최상단에 보이는 아이템의 위치의 경도

                                // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                                MapPoint selectMapPoint = MapPoint.mapPointWithGeoCoord(sel_lat, sel_lon);  // 선택한 가게의 좌표

                                mapView.setMapCenterPoint(selectMapPoint, true);    // 지도 화면의 중심점 설정

                                mapView.selectPOIItem(mapView.getPOIItems()[llm.findFirstVisibleItemPosition()], true); // 선택한 가게의 마커 선택
                            }else if((startXPosition > endPosition) && (startXPosition - endPosition) > 10 && endPosition < (float)display_width / 2){  // 오른쪽으로 슬라이드 ( 이전 Position 이동 )
                                mapStoreRv.smoothScrollToPosition(llm.findLastVisibleItemPosition());   // 현재 뷰에서 최 하단에 보이는 아이템의 위치로 이동

                                sel_lat = map_store.get(llm.findLastVisibleItemPosition()).getMapStoreLatitude();   // 현재 뷰에서 최하단에 보이는 아이템의 위치의 위도
                                sel_lon = map_store.get(llm.findLastVisibleItemPosition()).getMapStoreLongitude();  // 현재 뷰에서 최하단에 보이는 아이템의 위치의 경도

                                // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                                MapPoint selectMapPoint = MapPoint.mapPointWithGeoCoord(sel_lat, sel_lon);  // 선택한 가게의 좌표

                                mapView.setMapCenterPoint(selectMapPoint, true);    // 지도 화면의 중심점 설정

                                mapView.selectPOIItem(mapView.getPOIItems()[llm.findLastVisibleItemPosition()], true);  // 선택한 가게의 마커 선택
                            }else if((startXPosition < endPosition) && (endPosition - startXPosition) > 10){            // 왼쪽으로 슬라이드 ( 이동 X )
                                mapStoreRv.smoothScrollToPosition(llm.findLastVisibleItemPosition());   // 현재 뷰에서 최 하단에 보이는 아이템의 위치로 이동

                                sel_lat = map_store.get(llm.findLastVisibleItemPosition()).getMapStoreLatitude();   // 현재 뷰에서 최하단에 보이는 아이템의 위치의 위도
                                sel_lon = map_store.get(llm.findLastVisibleItemPosition()).getMapStoreLongitude();  // 현재 뷰에서 최하단에 보이는 아이템의 위치의 경도

                                // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                                MapPoint selectMapPoint = MapPoint.mapPointWithGeoCoord(sel_lat, sel_lon);  // 선택한 가게의 좌표

                                mapView.setMapCenterPoint(selectMapPoint, true);    // 지도 화면의 중심점 설정

                                mapView.selectPOIItem(mapView.getPOIItems()[llm.findLastVisibleItemPosition()], true);  // 선택한 가게의 마커 선택
                            }else if((startXPosition > endPosition) && (startXPosition - endPosition) > 10){        // 오른쪽으로 슬라이드 ( 이동 X )
                                mapStoreRv.smoothScrollToPosition(llm.findFirstVisibleItemPosition());  // 현재 뷰에서 최상단에 보이는 아이템의 위치로 이동

                                sel_lat = map_store.get(llm.findFirstVisibleItemPosition()).getMapStoreLatitude();  // 현재 뷰에서 최상단에 보이는 아이템의 위치의 위도
                                sel_lon = map_store.get(llm.findFirstVisibleItemPosition()).getMapStoreLongitude(); // 현재 뷰에서 최상단에 보이는 아이템의 위치의 경도

                                // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                                MapPoint selectMapPoint = MapPoint.mapPointWithGeoCoord(sel_lat, sel_lon);  // 선택한 가게의 좌표

                                mapView.setMapCenterPoint(selectMapPoint, true);    // 지도 화면의 중심점 설정

                                mapView.selectPOIItem(mapView.getPOIItems()[llm.findFirstVisibleItemPosition()], true); // 선택한 가게의 마커 선택
                            }else{      // 단순 터치 일 경우 ( 슬라이드 X )
                                View child = rv.findChildViewUnder(e.getX(), e.getY()); // 지정된 점 위의 view를 찾아주는 메서드
                                assert child != null : "RecyclerView Child is Null";    // View가 null일 경우 Exception 발생

                                int position = rv.getChildAdapterPosition(child);       // 어댑터에서 지정된 view에 해당하는 위치를 반환하는 메서드

                                // GET 방식 파라미터 설정, 파라미터 : 선택한 가게 고유 아이디
                                String selectStoreURL = String.format(HOST + MAINSTORE_PATH + "?storeId=%s",map_store.get(position).getMapStoreId());

                                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
                                StringRequest selectStoreRequest = new StringRequest(Request.Method.GET, selectStoreURL, response -> {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);                     // Response를 JsonObject 객체로 생성
                                        JSONArray mainStoreArr = jsonObject.getJSONArray("store");      // 객체에 store라는 Key를 가진 JSONArray 생성

                                        JSONObject object = mainStoreArr.getJSONObject(0);              // 첫번째 원소의 값으로 JSONObject 생성

                                        mainStoreData selectStore = new mainStoreData(
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
                                                    , object.getInt("storeReviewCount")            // 가게 리뷰 개수
                                                    , 0); // // 현위치에서 가게까지의 거리

                                        lastTag = position; // 상세화면으로 이동할 리사이클러뷰의 태그 번호 ( 인덱스 번호 ) 저장

                                        intent = new Intent(getActivity(), InfoActivity.class);             // 상세화면으로 이동하기 위한 Intent 객체 선언

                                        // 데이터 송신을 위한 Parcelable interface 사용
                                        // Java에서 제공해주는 Serializable보다 안드로에드에서 훨씬 빠른 속도를 보임
                                        intent.putExtra("Store", (Parcelable) selectStore);

                                        startActivity(intent);  // 새 Activity 인스턴스 시작

                                    } catch (JSONException error) {
                                        error.printStackTrace();
                                    }
                                }, error -> {
                                    // 통신 에러시 로그 출력
                                    Log.d("Error", "onErrorResponse : " + error);
                                });

                                selectStoreRequest.setShouldCache(false);   // 이전 결과가 있어도 새로 요청하여 출력
                                requestQueue.add(selectStoreRequest);       // RequestQueue에 요청 추가
                            }
                        }
                        startXPosition = 0.0f;  // 시작 위치 초기화
                        dragFlag = false;       // 드래그 플래그 초기화

                        break;
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
        // =================================================================

        // GPS 버튼 클릭 리스너
        mapGPSBtn.setOnClickListener(view -> {
            // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
            if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(root.getContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            //트래킹 모드가 아닌 단순 현재위치 업데이트일 경우, 한번만 위치 업데이트하고 트래킹을 중단
            if (!isTrackingMode) {
                // 현위치 트랙킹 모드 On + 나침반 모드 On, 단말의 위치에 따라 지도 중심이 이동하며 단말의 방향에 따라 지도가 회전
                lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

                // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
                // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
                // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
                //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Location loc_Current = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                double cur_lat = loc_Current.getLatitude();     // 현재 위치의 위도
                double cur_lon = loc_Current.getLongitude();    // 현재 위치의 경도

                // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                MapPoint currentMapPoint = MapPoint.mapPointWithGeoCoord(cur_lat, cur_lon);
                //이 좌표로 지도 중심 이동
                mapView.setMapCenterPoint(currentMapPoint, true);

                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                isTrackingMode = true;
            }else{
                // 현위치 트랙킹 모드 및 나침반 모드 Off
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                isTrackingMode = false;
            }
        });

        // 조회 EditText Key Press Listener
        mapSchText.setOnKeyListener((view, keyCode, keyEvent) -> {
            // 터치 이벤트가 Down, Up 2번 인식 되므로 Down 조건 추가
            // 엔터키 입력 Event
            if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                mapSchText.setSelection(mapSchText.length());   // 커서위치 설정
                // 지도 로드 완료 후 리사이클러뷰 데이터 받아오기
                Map<String, String> param = new HashMap<>();
                param.put("schText", mapSchText.getText().toString());  // 파라미터 설정
                getMapStoreData(param);     // 가게 데이터 GET
                return true;    // 받은 터치를 없어겠다는 의미
            }
            return false;       // 받은 터치를 다른곳에서도 사용할 수 있게 남겨두겠다는 의미
        });

        // 검색 EditText 입력시 키보드 내려가게
        mapSchText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            String schText = textView.getText().toString();

            // 검색어 입력이 없을 시
            if(schText.isEmpty()){
                Toast.makeText(getContext(), "정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
                return true;
            }

            // 키보드 검색어 버튼 클릭 시
            if(EditorInfo.IME_ACTION_SEARCH == actionId){
                imm.hideSoftInputFromWindow(mapSchText.getWindowToken(),0); // 키보드 내려줌
                return false;
            }
            return true;
        });

        // 새로고침 버튼 클릭 리스너
        mapRefreshBtn.setOnClickListener(view -> {
            mapSchText.setText(""); // 검색어 초기화
            getMapStoreData(new HashMap<>());
            imm.hideSoftInputFromWindow(mapSchText.getWindowToken(),0); // 키보드 내려줌
        });
    }

    // 가게 데이터 가져오는곳
    public void getMapStoreData(Map<String, String> param){
        llm = new LinearLayoutManager(getActivity());       // LayoutManager 객체 생성
        llm.setOrientation(LinearLayoutManager.HORIZONTAL); // 수평 레이아웃으로 설정
        mapStoreRv.setHasFixedSize(true);                   // 리사이클러뷰 높이, 너비 변경 제한
        mapStoreRv.setLayoutManager(llm);                   // 리사이클러뷰 Layout 설정

        mapView.removeAllPOIItems();    // 지도 화면에 추가된 모든 POI Item들을 제거
        map_store = new ArrayList<>();  // 지도 내 가게 정보 객체 생성

        lm = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);    // 위치관리자 객체 생성

        // ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION 퍼미션 체크
        if (ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(root.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(root.getContext(), "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // LocattionMananger.GPS_PROVIDER : GPS들로부터 현재 위치 확인, 정확도 높음, 실내 사용 불가
        // LocationManager.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인, 정확도 낮음, 실내 사용 가능
        // 실내에서 테스트 하기 위해 NETWORK_PROVIDER로 설정
        //Location loc_Current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null ? lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) : lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location loc_Current = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        double cur_lat = loc_Current.getLatitude();     // 현재 위치의 위도
        double cur_lon = loc_Current.getLongitude();    // 현재 위치의 경도

        Location cur_loc = new Location("cur_loc"); // Location 객체 생성
        cur_loc.setLatitude(cur_lat);   // 현재 위도 SET
        cur_loc.setLongitude(cur_lon);  // 현재 경도 SET

        // GET 방식 파라미터 설정
        String mapStorePath = MAPSTORE_PATH;
        mapStorePath += String.format("?latitude=%s", cur_lat);     // 위도 파라미터 설정
        mapStorePath += String.format("&&longitude=%s", cur_lon);   // 경도 파라미터 설정

        if(!param.isEmpty()){   // 검색어 입력되었을 경우 파라미터 입력
            mapStorePath += String.format("&&schText=%s",param.get("schText"));
        }

        // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( GET 방식 )
        StringRequest MapStoreRequest = new StringRequest(Request.Method.GET, HOST + mapStorePath, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);                 // Response를 JsonObject 객체로 생성
                JSONArray mapStoreArr = jsonObject.getJSONArray("store");   // 객체에 store라는 Key를 가진 JSONArray 생성

                int index = lastTag != -1 ? lastTag : 0;    // 마지막으로 보여진 리사이클러뷰로 이동하기 위한 index

                if(mapStoreArr.length() > 0){
                    for(int i = 0; i < mapStoreArr.length(); i++){
                        JSONObject object = mapStoreArr.getJSONObject(i);   // 배열 원소 하나하나 꺼내서 JSONObject 생성

                        // 현재 위치에서 가게 까지의 거리 계산을 위한 좌표
                        Location point = new Location(object.getString("storeName"));   // 가게 위치 Location 객체 생성
                        point.setLatitude(object.getDouble("storeLatitude"));
                        point.setLongitude(object.getDouble("storeLongitude"));

                        MapStoreData mapStore = new MapStoreData(object.getInt("storeId")   // 가게 고유 아이디
                                , object.getString("storeName")                             // 가게 이름
                                , !object.isNull("storeThumbnailPath") ? HOST + object.getString("storeThumbnailPath") : HOST + "/ftpFileStorage/noImage.png"   // 가게 썸네일 이미지 경로
                                , Float.parseFloat(object.getString("storeScore"))          // 가게 별점
                                , object.getDouble("storeLatitude")                         // 가게 위도
                                , object.getDouble("storeLongitude")                        // 가게 경도
                                , cur_loc.distanceTo(point) / 1000                                // 현재 위치에서 떨어진 거리, 단위 : m
                                , object.getString("storeInfo")                             // 가게 간단 정보
                                , object.getString("storeHashTag"));                        // 가게 해시태그

                        map_store.add(mapStore);    // 가게 데이터 추가

                        // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                        MapPoint MARKER_POINT = MapPoint.mapPointWithGeoCoord(map_store.get(i).getMapStoreLatitude(), map_store.get(i).getMapStoreLongitude());

                        MapPOIItem marker = new MapPOIItem();                       // POI 객체 생성
                        marker.setItemName(map_store.get(i).getMapStoreName());     // POI Item 아이콘이 선택되면 나타나는 말풍선(Callout Balloon)에 POI Item 이름이 보여짐
                        marker.setTag(i);                                           // MapView 객체에 등록된 POI Item들 중 특정 POI Item을 찾기 위한 식별자로 사용
                        marker.setMapPoint(MARKER_POINT);                           // POI Item의 지도상 좌표를 설정
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

                        mapView.addPOIItem(marker); // 지도화면에 POI Item 아이콘(마커)를 추가
                    }

                    double last_lat = map_store.get(index).getMapStoreLatitude();  // 마지막으로 검색된 가게의 위도
                    double last_lon = map_store.get(index).getMapStoreLongitude(); // 마지막으로 검색된 가게의 경도

                    // 위경도 좌표 시스템(WGS84)의 좌표값으로 MapPoint 객체를 생성
                    MapPoint lastMapPoint = MapPoint.mapPointWithGeoCoord(last_lat, last_lon);

                    mapView.setMapCenterPoint(lastMapPoint, true);         // 지도 화면의 중심점을 설정

                    mapView.selectPOIItem(mapView.getPOIItems()[index], true);  // 선택한 가게의 마커 선택

                }else{
                    // 검색 결과가 없을 시 Toast 메시지 출력
                    Toast.makeText(getContext(), "검색 결과가 없습니다.",Toast.LENGTH_SHORT).show();
                }
                mapStoreAdapter = new MapRvAdapter(getActivity());  // 리사이클러뷰 어뎁터 객체 생성
                mapStoreAdapter.setStores(map_store);               // 어뎁터 객체에 가게 정보 저장
                mapStoreAdapter.setMapView(mapView);                // 어뎁터 객체에 지도 정보 저장
                mapStoreRv.setAdapter(mapStoreAdapter);             // 리사이클러뷰 어뎁터 객체 지정

                mapStoreRv.scrollToPosition(index);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 통신 에러시 로그 출력
            Log.d("getMapStoreDataError", "onErrorResponse : " + error);
        });

        MapStoreRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
        requestQueue.add(MapStoreRequest);      // RequestQueue에 요청 추가
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);

        checkPermissions(); //지도 권한 허용 확인

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Activity 이동간 mapView는 1개만 띄워져 있어야 하기 때문에
    // onCreate가 아닌 onResume에서 mapview 객체 생성
    //
    // ----------- 간단한 LifeCycle --------------
    // onCreate -> onResume -> ( 다른 Activity로 이동 ) -> onPause -> ( 현재 Activity로 이동 ) -> onResume
    // @SuppressLint("ClickableViewAccessibility") 어노테이션을 추가해 Lint의 Warning을 무시
    @Override
    public void onResume() {
        super.onResume();

        mapView = new MapView(root.getContext());   // mapView 객체 생성

        mapViewContainer = (ViewGroup) root.findViewById(R.id.map_view);    // ViewGroup Container
        mapViewContainer.addView(mapView);                                  // mapView attach

        mapView.setMapViewEventListener(this);          // 지도 이동/확대/축소, 지도 화면 터치 (Single Tap / Double Tap / Long Press) 이벤트를 통보받을 수 있음
        mapView.setCurrentLocationEventListener(this);  // 현위치 트래킹 이벤트를 통보 받을 수 있음
        mapView.setPOIItemEventListener(this);          // POI 관련 이벤트를 통보 받을 수 있음

        mapView.setZoomLevel(1, true);  // 지도 화면의 확대/축소 레벨을 설정
        mapView.zoomIn(true);   // 지도 화면을 한단계 확대

        // 지도 로드 완료 후 리사이클러뷰 데이터 받아오기
        getMapStoreData(new HashMap<>());
    }

    @Override
    public void onPause() {
        super.onPause();
        mapViewContainer.removeAllViews();      // ViewGroup에서 모든 자식 뷰를 제거
        mapStoreRv.removeAllViewsInLayout();    // 리사이클러뷰 레이아웃 제거
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCurrentLocationUpdate(MapView mmapView, MapPoint mmapPoint, float v) { }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) { }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) { }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) { }

    @Override
    public void onMapViewInitialized(MapView mapView) { }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) { }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) { }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) { }

    // mapView POI Click Event
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        // 선택한 마커 Position의 리사이클러뷰로 이동
        mapStoreRv.scrollToPosition(mapPOIItem.getTag());
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) { }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) { }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) { }
}
