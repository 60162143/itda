package com.example.itda.ui.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.itda.R;
import com.example.itda.ui.global.globalMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class MyPageEditNameActivity extends Activity {
    private ImageButton backIc; // 상단 뒤로가기 버튼
    private EditText userName;  // 유저 명 변경 입력
    private Button userNameBtn; // 유저 명 변경 버튼

    private static RequestQueue requestQueue;   // Volley Library 사용을 위한 RequestQueue
    private SharedPreferences User;    // 로그인 데이터 ( 전역 변수 )
    private String UPDATE_NAME_PATH;      // 유저 이름 변경 Rest API
    private String HOST;            // Host 정보

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_edit_name);

        // RequestQueue 객체 생성 ( 초기에만 생성 )
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

        HOST = ((globalMethod) getApplication()).getHost();   // Host 정보
        UPDATE_NAME_PATH = ((globalMethod) getApplication()).getUpdateUserNamePath();    // 유저 이름 변경 Rest API

        initView(); // 뷰 생성

        // 유저 전역 변수 GET
        User = getSharedPreferences("user", Activity.MODE_PRIVATE);

        // 뒤로 가기 버튼 클릭 시 Activity 종료
        backIc.setOnClickListener(view -> finish());

        userName.setText(User.getString("userName", "")); // 유저 명

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // 텍스트가 입력되기 전에 Call back
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // 텍스트가 변경 될때마다 Call back
                if(!User.getString("userName", "").equals(charSequence.toString()) && !charSequence.toString().isEmpty()){
                    userNameBtn.setEnabled(true);
                    userNameBtn.setBackgroundResource(R.drawable.round_color);
                }else{
                    userNameBtn.setEnabled(false);
                    userNameBtn.setBackgroundResource(R.drawable.round_gray);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 텍스트 입력이 모두 끝났을 때 Call back
            }
        });

        userNameBtn.setOnClickListener(view -> {
            Map<String, String> param = new HashMap<>();
            param.put("userId", String.valueOf(User.getInt("userId", 0)));   // 변경할 유저 고유 아이디
            param.put("name", userName.getText().toString());   // 변경할 유저 명

            // StringRequest 객체 생성을 통해 RequestQueue로 Volley Http 통신 ( POST 방식 )
            StringRequest updateNameRequest = new StringRequest(Request.Method.POST, HOST + UPDATE_NAME_PATH, response -> {
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

                        autoLoginEdit.putString("userName", userName.getText().toString());  // 유저 명

                        autoLoginEdit.apply();  // 데이터를 저장

                        // ResultCode와 데이터 값 전달을 위한 intent객체 생성
                        Intent intent = new Intent(MyPageEditNameActivity.this, MyPageEditActivity.class);

                        setResult(1000, intent);    // 결과 코드와 intent 값 전달

                        finish();
                    }else{
                        StyleableToast.makeText(getApplicationContext(), "변경 실패...", R.style.redToast).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                // 통신 에러시 로그 출력
                Log.d("updateNameError", "onErrorResponse : " + error);
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    // php로 설정값을 보낼 수 있음 ( POST )
                    return param;
                }
            };

            updateNameRequest.setShouldCache(false);  // 이전 결과가 있어도 새로 요청하여 출력
            requestQueue.add(updateNameRequest);      // RequestQueue에 요청 추가
        });
    }

    // 뷰 생성
    private void initView(){
        backIc = findViewById(R.id.mypage_edit_name_back_ic); // 상단 뒤로가기 버튼
        userName = findViewById(R.id.mypage_edit_name);  // 유저 명 변경 입력
        userNameBtn = findViewById(R.id.mypage_edit_name_btn);  // 유저 명 변경 버튼
    }
}

