package com.leaf.collegeidleapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.User;
import com.leaf.collegeidleapp.util.UserDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 注册界面Activity类
 * @author : autumn_leaf
 */
public class RegisterActivity extends AppCompatActivity {

    EditText tvStuNumber,tvStuPwd,tvStuConfirmPwd;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button btnCancel = findViewById(R.id.btn_cancel);
        //返回到登录界面
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuNumber = findViewById(R.id.et_username);
        tvStuPwd = findViewById(R.id.et_password);
        tvStuConfirmPwd = findViewById(R.id.et_confirm_password);
        //注册点击事件
        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInput()) {

//                    UserDbHelper dbHelper1 = new UserDbHelper(getApplicationContext(), UserDbHelper.DB_NAME, null, 1);
//                    List<User> users = new ArrayList<>();
//                    users = dbHelper1.readUsers();
//                    boolean flag = false;
//                    for (User user : users) {
//                        if (user.getUsername().equals(tvStuNumber.getText().toString())) {
//                            flag = true;
//                        }
//                    }
//
//                    if (flag == true) {
//                        Toast.makeText(RegisterActivity.this, "该用户已经存在", Toast.LENGTH_SHORT).show();
//                    } else {
//                            User user = new User();
//                            user.setUsername(tvStuNumber.getText().toString());
//                            user.setPassword(tvStuPwd.getText().toString());
//                            UserDbHelper dbHelper = new UserDbHelper(getApplicationContext(), UserDbHelper.DB_NAME, null, 1);
//                            dbHelper.addUser(user);
//                            Toast.makeText(RegisterActivity.this, "恭喜你注册成功!", Toast.LENGTH_SHORT).show();
//                            //销毁当前界面
//                            finish();
//
//                    }
                    post();



                }
            }
        });
    }

    //判断输入是否符合规范
    public boolean CheckInput() {
        String username = tvStuNumber.getText().toString();
        String password = tvStuPwd.getText().toString();
        String confirm_password = tvStuConfirmPwd.getText().toString();
        if(username.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"用户名不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(confirm_password.trim().equals("")) {
            Toast.makeText(RegisterActivity.this,"确认密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.trim().equals(confirm_password.trim())) {
            Toast.makeText(RegisterActivity.this,"两次密码输入不一致!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void post(){
        new Thread(() -> {
            String username=tvStuNumber.getText().toString();
            String password = tvStuPwd.getText().toString();

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/user/register";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", "dae70c305a9b430cb7b5ef0f1ee89c96")
                    .add("appSecret", "1792531b4a5b20c1c44f6b9347eb324bea377")
                    .add("Content-Type", "application/json")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("password", password);
            bodyMap.put("username", username );
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);

            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }


    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(() -> {
                if (dataResponseBody.getMsg().equals("成功")) {
                    Toast.makeText(RegisterActivity.this, "恭喜你注册成功!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if(dataResponseBody.getMsg().equals("用户名已存在")) {
                    Toast.makeText(RegisterActivity.this, "用户名已存在!", Toast.LENGTH_SHORT).show();

                }else {
                    // 数据解析失败或者 dataResponseBody 为 null 的处理
                    Log.e("info", "数据解析失败或者 dataResponseBody 为 null");
                }
            });



        }
    };

    /**
     * http响应体的封装协议
     * @param <B> 泛型
     */
    public static class ResponseBody <B> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private B data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public B getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
