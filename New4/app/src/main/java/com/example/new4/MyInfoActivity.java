package com.example.new4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.new4.bean.Head;
import com.example.new4.bean.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyInfoActivity extends AppCompatActivity {

    TextView tvUserName,tvUserId,tvUserMoney,tvMoneyIn,tvMoneyOut;
    ImageView userHead;
    Button recharge;
    private final Gson gson = new Gson();
    public  int userId,money,totalIn,totalOut,amount;
    public  String username,password,userHeadImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        //不显示顶部状态栏
        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        //获取bundle数据
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        username = bundle.getString("username");
        password = bundle.getString("password");


        Button btnBack = findViewById(R.id.btn_back);
        //返回点击事件,销毁当前界面
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvUserId = findViewById(R.id.tv_userId);
        tvUserName = findViewById(R.id.tv_userName);
        tvUserMoney = findViewById(R.id.tv_userMoney);
        tvMoneyIn = findViewById(R.id.tv_moneyIn);
        tvMoneyOut = findViewById(R.id.tv_moneyOut);
        userHead = findViewById(R.id.iv_head);

        show();

        //充值按钮的点击事件
        recharge = findViewById(R.id.btn_recharge);
        recharge.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyInfoActivity.this);
                builder.setTitle("输入充值金额");

                // 创建输入文本框
                final EditText input = new EditText(MyInfoActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);

                // 设置确定和取消按钮
                builder.setPositiveButton("确定", (dialog, which) -> {
                    String value = input.getText().toString();
                    if (!value.isEmpty()&&value.length()<10) {

                        amount = Integer.parseInt(value);
                        post2();//发送充值请求
                    }else if (value.length()>10){
                        Toast.makeText(MyInfoActivity.this, "充值金额过大", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MyInfoActivity.this, "请输入充值金额", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", (dialog, which) -> dialog.cancel()); // 取消按钮
                // 显示对话框
                builder.show();

            }
        });

        //点击头像按钮
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyInfoActivity.this,PictureActivity.class);
                Bundle bundle=new Bundle();

                bundle.putString("bigImage",userHeadImage);

                intent.putExtras(bundle);

                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


    }




    //获取用户数据
    private void post1(){
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
            bodyMap.put("password", password);
            bodyMap.put("username", username);
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
            Type jsonType = new TypeToken<ResponseBody1<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody1<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(() -> {
                if (dataResponseBody.getMsg().equals("登录成功")) {
                    UserData userData = dataResponseBody.getData();

                    userHeadImage = userData.getAvatar();
                    username = userData.getUsername();
                    money = userData.getMoney();
                    loadData();

                }else {
                    // 处理用户数据为空的情况
                    Toast.makeText(MyInfoActivity.this, "用户数据为空", Toast.LENGTH_SHORT).show();
                }
            });




        }
    };


    //获取用户中收入和总支出
    private void get(){
        new Thread(() -> {
            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/trading/allMoney?userId="+userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();
            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback1);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback1 = new Callback() {
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

            runOnUiThread(()->{
                Data data=dataResponseBody.getData();
                totalIn = data.getTotalRevenue();
                totalOut = data.totalSpending;

                loadData();

            });




        }
    };


    //充值
    private void post2(){
        new Thread(() -> {

            Head head =new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/recharge?tranMoney=" + amount + "&userId=" + userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback2);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback2 = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody2<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("MyInfoActivity", body);
            // 解析json串到自己封装的状态
            ResponseBody2<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){
                    Toast.makeText(MyInfoActivity.this,"充值成功",Toast.LENGTH_SHORT).show();

                    show();
                }

            });
        }
    };



    //post1的ResponseBody
    public static class ResponseBody1 <T> {

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

        public ResponseBody1(){}

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
        private Data data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public Data getData() {
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

    public static class ResponseBody2 <T> {

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
        private T data;

        public ResponseBody2(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public T getData() {
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


    public class Data {
        private int totalRevenue;   // 根据返回的数据显示，使用int
        private int totalSpending;  // 根据返回的数据显示，使用int


        public int getTotalSpending() {
            return totalSpending;
        }

        public void setTotalSpending(int totalSpending) {
            this.totalSpending = totalSpending;
        }

        public int getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(int totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }

    public  void show(){
        post1();
        get();


    }

    public void loadData(){
        tvUserId.setText(userId+"");
        tvUserName.setText(username);
        tvUserMoney.setText(money+"");


        tvMoneyIn.setText(totalIn+"");
        tvMoneyOut.setText(totalOut+"");

        if (isFinishing()) {
            //不加载
        } else if(userHeadImage != null) {
            Glide.with(MyInfoActivity.this)
                    .load(userHeadImage)
                    .into(userHead);
        }else{
            // 处理无图片数据时的逻辑，比如显示默认图片
            userHead.setImageResource(R.drawable.icon_user_photo);
        }
    }

}
