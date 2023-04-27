package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageEditBirthdayActivity extends AppCompatActivity {
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private NumberPicker userYear;  // 유저 생일 년 입력
    private NumberPicker userMonth;  // 유저 생일 월 입력
    private NumberPicker userDay;  // 유저 생일 일 입력
    private Button userBirthdayBtn; // 유저 생일 변경 버튼

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )
    private String UPDATE_BIRTHDAY_PATH;      // 유저 생일 변경 Rest API
    private String HOST;            // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit_birthday);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        UPDATE_BIRTHDAY_PATH = ((globalMethod) getApplication()).getUpdateUserBirthdayPath();    // 유저 생일 변경 Rest API

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        // 연, 월, 일 최대, 최소값 설정
        userYear.setMinValue(1940);
        userYear.setMaxValue(2023);

        userMonth.setMinValue(1);
        userMonth.setMaxValue(12);

        userDay.setMinValue(1);
        userDay.setMaxValue(31);

        // 날짜의 디폴트 년도는 1900년
        // 1900년도 이상이라는 뜻은 값이 들어있다는 의미
        if(Integer.parseInt(User.getString("userBirthday", "").substring(0, 4)) > 1900){
            String[] birth = User.getString("userBirthday", "").split("\\.");
            userYear.setValue(Integer.parseInt(birth[0]));  // 년
            userMonth.setValue(Integer.parseInt(birth[1])); // 월
            userDay.setValue(Integer.parseInt(birth[2]));   // 일

            Calendar cal = Calendar.getInstance();  // Calendar 인스턴스를 생성
            cal.set(userYear.getValue(),userMonth.getValue() - 1,1);   // 월 부분은 -1

            userDay.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));   // getActualMaximum 함수를 호출하면 기준이된 월의 말일 계산
        }else{
            userYear.setValue(2000);    // 년
            userMonth.setValue(1);      // 월
            userDay.setValue(1);        // 일
        }

        // 연도 변경 리스너 ( 말일 계산 )
        userYear.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            Calendar cal = Calendar.getInstance();  // Calendar 인스턴스를 생성
            cal.set(newValue,userMonth.getValue() - 1,1);   // 월 부분은 -1

            userDay.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));   // getActualMaximum 함수를 호출하면 기준이된 월의 말일 계산
        });

        // 월 변경 리스너 ( 말일 계산 )
        userMonth.setOnValueChangedListener((numberPicker, oldValue, newValue) -> {
            Calendar cal = Calendar.getInstance();  // Calendar 인스턴스를 생성
            cal.set(userYear.getValue(),newValue - 1,1);    // 월 부분은 -1

            userDay.setMaxValue(cal.getActualMaximum(Calendar.DAY_OF_MONTH));   // getActualMaximum 함수를 호출하면 기준이된 월의 말일 계산
        });

        userBirthdayBtn.setOnClickListener(view -> {
            String birthDay = userYear.getValue() + "."
                    + (userMonth.getValue() < 10 ? "0" + userMonth.getValue() : userMonth.getValue()) + "."
                    + (userDay.getValue() < 10 ? "0" + userDay.getValue() : userDay.getValue());

            // 날짜가 변경되었을 경우 변경
            if(!birthDay.equals(User.getString("userBirthday", ""))){
                Map<String, String> param = new HashMap<>();
                param.put("userId", String.valueOf(User.getInt("userId", 0)));   // 변경할 유저 고유 아이디
                param.put("birthday", userYear.getValue() + "-" + userMonth.getValue() + "-" + userDay.getValue());   // 변경할 유저 생일

                // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
                StringRequest updateBirthdayRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_BIRTHDAY_PATH, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);   // Response를 JsonObject 객체로 생성
                        String success = jsonObject.getString("success");

                        if(!TextUtils.isEmpty(success) && success.equals("1")) {
                            StyleableToast.makeText(getApplicationContext(), "변경 성공!", R.style.blueToast).show();

                            // 해당 앱의 파일에 저장되는 데이터를 다룰 수 있는 인터페이스이다.
                            // 즉, 자동로그인 기능에서 뿐만 아니라 앱을 종료하고 새로 시작했을 때 똑같이 가지고 있어야 하는 데이터가 있을 경우 넣어서 사용할 수 있다.
                            // 데이터는 해당 앱을 삭제하거나 해당 앱의 데이터를 삭제하지 않는 이상 계속 유지된다.
                            // key, value와 같은 Map 형태로 값을 넣을 수 있으며 넣을 수 있는 값의 타입은 Boolean, Float, Int, Long, String, StringSet이 있다.
                            // Activity.MODE_PRIVATE : 기본 모드로 이렇게 설정할 경우 해당 데이터는 해당 앱에서만 사용
                            SharedPreferences auto = getSharedPreferences("user", Activity.MODE_PRIVATE);

                            // SharedPreferences를 가져와서 거기서 Editor 객체를 가져와 put 메소드를 사용
                            SharedPreferences.Editor autoLoginEdit = auto.edit();

                            autoLoginEdit.putString("userBirthday", birthDay);  // 유저 번호

                            autoLoginEdit.apply();  // 데이터를 저장

                            // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                            Intent intent = new Intent(MyPageEditBirthdayActivity.this, MyPageEditActivity.class);

                            setResult(3000, intent);    // 결과 코드와 intent 값 전달

                            finish();
                        }else{
                            StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    // 통신 에러시 로그 출력
                    Log.d("updateNumberError", "onErrorResponse : " + error);
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // php로 설정값을 보낼 수 있음 ( POST )
                        return param;
                    }
                };

                updateBirthdayRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
                requestQueue.add(updateBirthdayRequest);      // RequestQueue에 요청 추가

            }else{
                StyleableToast.makeText(getApplicationContext(), "날짜를 변경해 주세요.", R.style.redToast).show();
            }
        });
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_birthday_back_ic); // 상단 뒤로가기 버튼
        userYear = findViewById(R.id.mypage_edit_year);  // 유저 생일 년 입력
        userMonth = findViewById(R.id.mypage_edit_month);  // 유저 생일 월 입력
        userDay = findViewById(R.id.mypage_edit_day);  // 유저 생일 일 입력
        userBirthdayBtn = findViewById(R.id.mypage_edit_birthday_btn);  // 유저 생일 변경 버튼
    }
}

