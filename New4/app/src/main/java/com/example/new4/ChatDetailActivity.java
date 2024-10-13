package com.example.new4;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.new4.adapter.MessageDetailAdapter;

import com.example.new4.api.ReadChat;
import com.example.new4.bean.Head;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatDetailActivity extends AppCompatActivity {

    int tUserId,userId;
    String tUserName,tUserHead,UserHead,sentMessage;

    Gson gson =new Gson();
    Head head=new Head();

    private ListView lv_message;
    TextView tvUserName;
    ImageView btn_back;
    EditText et_sent;
    Button btn_sent;
    private ScheduledExecutorService scheduler;


    List<messageRecord> records = new ArrayList<>();
    List<messageRecord> oldRecords = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagedetail);

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


        Bundle bundle = this.getIntent().getExtras();
        if( bundle != null) {
            tUserId=bundle.getInt("tUserId");
            userId=bundle.getInt("userId");
            tUserName = bundle.getString("tUserName");
            tUserHead = bundle.getString("tUserHead");
            UserHead = bundle.getString("userHead");
        }

        tvUserName = findViewById(R.id.tUserName);
        tvUserName.setText(tUserName);
        et_sent=findViewById(R.id.et_message);
        btn_sent=findViewById(R.id.btn_send);
        lv_message=findViewById(R.id.lv_massage);
        btn_back = findViewById(R.id.iv_back);


        scheduler = Executors.newScheduledThreadPool(1);
        startRefreshing();



        //发送信息按钮
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentMessage=et_sent.getText().toString();
                if (checkInput(sentMessage)){

                    sentMessage();
                    et_sent.setText("");
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public boolean checkInput(String sentMessage){

        if (sentMessage.trim().equals("")){
            return false;
        }
        return true;
    }

    public void loadData( List<messageRecord> records){
        MessageDetailAdapter adapter=new MessageDetailAdapter(ChatDetailActivity.this);
        adapter.setData(records,tUserHead,UserHead,userId);
        lv_message.setAdapter(adapter);
        // 滚动到底部
        lv_message.setSelection(adapter.getCount() - 1);
    }



    private void getMessage(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/chat/message?fromUserId="+tUserId+ "&size=100&userId="+userId;

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
                client.newCall(request).enqueue(getcallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback getcallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<getmessageResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            getmessageResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                records = dataResponseBody.getData().getRecords();


                for (messageRecord message : records) {
                    // 这里可以调用设置已读的方法

                    if (userId!=Integer.parseInt(message.getFromUserId()))
                    {
                        ReadChat.read(message.getId());
                    }

                }

                Collections.reverse(records);

                //判断页面是否需要刷新
                if (oldRecords.isEmpty()){
                    oldRecords.addAll(records);
                    loadData(records);
                }
                if (oldRecords.size()!=records.size()){
                    oldRecords.clear();
                    oldRecords.addAll(records);
                    loadData(records);

                }


            });


        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class getmessageResponseBody <T> {

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
        private getMessageData data;

        public getmessageResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public getMessageData getData() {
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

    // 主类 Data
    public class getMessageData {
        private List<messageRecord> records; // 记录列表
        private int total;             // 总记录数
        private int size;              // 每页大小
        private int current;           // 当前页

        public List<messageRecord> getRecords() {
            return records;
        }

        public void setRecords(List<messageRecord> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

    }

    // 嵌套类 Record
    public static class messageRecord {
        private int id;           // 记录 ID
        private String fromUserId;   // 发送者 ID
        private String fromUsername;  // 发送者用户名
        private String content;       // 内容
        private String userId;        // 接收者 ID
        private String username;      // 接收者用户名
        private boolean status;       // 状态
        private String createTime;    // 创建时间

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getFromUsername() {
            return fromUsername;
        }

        public void setFromUsername(String fromUsername) {
            this.fromUsername = fromUsername;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }



    private void sentMessage(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/chat";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content",sentMessage);
            bodyMap.put("toUserId",tUserId );
            bodyMap.put("userId", userId);
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
                client.newCall(request).enqueue(sentMessagecallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback sentMessagecallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<sentMessageResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            sentMessageResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){
                    Toast.makeText(ChatDetailActivity.this,"消息发送成功",Toast.LENGTH_SHORT).show();
                    getMessage();
                }
                else {
                    Toast.makeText(ChatDetailActivity.this,dataResponseBody.getMsg(),Toast.LENGTH_SHORT).show();
                }

            });
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class sentMessageResponseBody <T> {

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

        public sentMessageResponseBody(){}

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

    public void startRefreshing() {
        // 安排在每 10 秒后执行 refreshData 方法
        scheduler.scheduleAtFixedRate(() -> {
            runOnUiThread(this::getMessage); // 在主线程中更新 UI
        }, 0, 2, TimeUnit.SECONDS);
    }


}
