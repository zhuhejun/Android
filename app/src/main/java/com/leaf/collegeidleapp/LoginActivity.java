package com.leaf.collegeidleapp;

import android.app.Service;
import android.content.Intent;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

import com.google.gson.reflect.TypeToken;
import com.leaf.collegeidleapp.bean.Head;
import com.leaf.collegeidleapp.bean.User;
import com.leaf.collegeidleapp.util.HttpService;


import java.util.HashMap;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;



/**
 * 登录界面Activity类
 * @author : autumn_leaf
 */
public class LoginActivity extends AppCompatActivity {

    private final Gson gson = new Gson();
    EditText EtStuNumber,EtStuPwd;
    private String username;

    LinkedList<User> users = new LinkedList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView tvRegister = findViewById(R.id.tv_register);
        //跳转到注册界面
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        EtStuNumber = findViewById(R.id.et_username);
        EtStuPwd = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
//                login();
//                boolean flag = false;
//                if(CheckInput()) {
//                    UserDbHelper dbHelper = new UserDbHelper(getApplicationContext(),UserDbHelper.DB_NAME,null,1);
//                    users = dbHelper.readUsers();
//                    //User user是一个单独的用户对象，而users是一个用户数据群体
//                    for(User user : users) {
//                        //如果可以找到,则输出登录成功,并跳转到主界面
//                        if(user.getUsername().equals(EtStuNumber.getText().toString()) && user.getPassword().equals(EtStuPwd.getText().toString()) ) {
//                            flag = true;
//                            Toast.makeText(LoginActivity.this,"恭喜你登录成功!",Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                            Bundle bundle = new Bundle();
//                            username = EtStuNumber.getText().toString();
//                            bundle.putString("username",username);
//                            //和main里的final Bundle bundle = this.getIntent().getExtras();相呼应
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                    }
//                    //否则提示登录失败,需要重新输入
//                    if (!flag) {
//                        Toast.makeText(LoginActivity.this,"学号或密码输入错误!",Toast.LENGTH_SHORT).show();
//                    }
//                }
            }
        });
    }

//    private void login() {
//        HttpService httpService = new HttpService();
//        httpService.login(EtStuNumber.getText().toString(),EtStuPwd.getText().toString(),"https://api-store.openguet.cn/api/member/tran/user/login");
//    }

    //检查输入是否符合要求
    public boolean CheckInput() {
        String StuNumber = EtStuNumber.getText().toString();
        String StuPwd = EtStuPwd.getText().toString();
        if(StuNumber.trim().equals("")) {
            Toast.makeText(LoginActivity.this,"学号不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(StuPwd.trim().equals("")) {
            Toast.makeText(LoginActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void post(){
        String StuNumber = EtStuNumber.getText().toString();
        String StuPwd = EtStuPwd.getText().toString();
        Head head = new Head();
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/user/login";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("password", StuNumber);
            bodyMap.put("username", StuPwd);
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
                if (dataResponseBody.getMsg().equals("登录成功")) {
                    Toast.makeText(LoginActivity.this, "恭喜你登录成功!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    username = EtStuNumber.getText().toString();
                    UserData userData = dataResponseBody.getData();
                    Integer userId;
                    userId = userData.getId();
                    bundle.putString("username",username);
                    bundle.putInt("userId", userId);

                    //和main里的final Bundle bundle = this.getIntent().getExtras();相呼应
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(dataResponseBody.getMsg().equals("密码错误")) {
                    Toast.makeText(LoginActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();

                }
                else if(dataResponseBody.getMsg().equals("当前登录用户不存在")) {
                    Toast.makeText(LoginActivity.this, "当前登录用户不存在!", Toast.LENGTH_SHORT).show();

                }
            });




        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {

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
        private UserData data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public UserData getData() {
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
    public class UserData {
        private Integer id;
        private String appKey;
        private String username;
        private int money;
        private String avatar;
        private String password;

        // 添加相应的getter方法


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
