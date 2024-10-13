package com.example.new4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.state.ToggleableState;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.new4.adapter.AllMessageAdapter;
import com.example.new4.bean.AllCommodity;
import com.example.new4.bean.Head;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AllMessageActivity extends AppCompatActivity {

    ListView lv_allMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScheduledExecutorService scheduler;
    ImageView btn_back;

    Head head = new Head();
    Gson gson = new Gson();
    int userId;
    String userHead;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allmessage);

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


        lv_allMessage=findViewById(R.id.lv_allMessage);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        btn_back=findViewById(R.id.iv_back);

        Bundle bundle = this.getIntent().getExtras();
        userId=bundle.getInt("userId");
        userHead=bundle.getString("userHead");

        // 初始化调度器,实现2秒刷新
        scheduler = Executors.newScheduledThreadPool(1);
        startRefreshing();

        //每一个信息的点击事件
        lv_allMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MessageList messageLists = (MessageList) lv_allMessage.getAdapter().getItem(position);
                Bundle bundle = new Bundle();
                bundle.putInt("tUserId", Integer.parseInt(messageLists.getFromUserId()));
                bundle.putInt("userId",userId);
                bundle.putString("tUserName",messageLists.getUsername());
                bundle.putString("userHead",userHead);
                bundle.putString("tUserHead","tUserHead");         //---------------------------
                Intent intent = new Intent(AllMessageActivity.this, ChatDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 模拟加载新数据
            new Handler().postDelayed(() -> {
                refreshData(); // 刷新数据
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }, 2000); // 延迟2秒
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    //重写系统返回
    @Override
    public void onBackPressed() {
        super.onBackPressed(); // 保持系统返回逻辑
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // 添加动画
    }

    // 刷新数据的方法
    private void refreshData() {
        getAllMessage();
    }

    private void getAllMessage(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/chat/user?userId="+userId;

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
                client.newCall(request).enqueue(allMessagecallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback allMessagecallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<allMessageResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            allMessageResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{

//                if (dataResponseBody.getMsg().equals("成功"))


                List<MessageList> messageList  = dataResponseBody.getData();
//                Toast.makeText(AllMessageActivity.this,messageList.get(1).getUnReadNum()+"",Toast.LENGTH_SHORT).show();

                AllMessageAdapter adapter = new AllMessageAdapter(AllMessageActivity.this);
                adapter.setData(messageList,"131321");
                lv_allMessage.setAdapter(adapter);
            });

        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class allMessageResponseBody <T> {

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
        private List<MessageList>  data;

        public allMessageResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public List<MessageList> getData() { // 返回类型改为 List<MessageList>
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


    public class MessageList{

        private String fromUserId;
        private String username;
        private int unReadNum;

        public String getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getUnReadNum() {
            return unReadNum;
        }

        public void setUnReadNum(int unReadNum) {
            this.unReadNum = unReadNum;
        }
    }


    public void startRefreshing() {
        // 安排在每 0.5 秒（500 毫秒）后执行 refreshData 方法
        scheduler.scheduleAtFixedRate(() -> {
            runOnUiThread(this::refreshData); // 在主线程中更新 UI
        }, 0, 500, TimeUnit.MILLISECONDS); // 设置为 500 毫秒
    }
}
