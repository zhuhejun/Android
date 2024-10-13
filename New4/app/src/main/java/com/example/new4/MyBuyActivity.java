package com.example.new4;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.new4.adapter.MyBuyCommodityAdapter;
import com.example.new4.adapter.MyCommodityAdapter;
import com.example.new4.bean.Head;
import com.example.new4.bean.MyBuyCommodity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyBuyActivity extends AppCompatActivity {

    public int userId;
    private final Gson gson = new Gson();
    private ListView lvMyCommodity;
    List<MyBuyCommodity> commodityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybuy);

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


        //获取传过来的userId用于查询
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");

        //返回按钮
        TextView tvBack = findViewById(R.id.tv_back);
        //点击返回销毁当前界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvMyCommodity=findViewById(R.id.lv_my_commodity);

        get();

//        //为每一个item设置点击事件
//        lvMyCommodity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//
//            }
//        });





    }


    private void get(){
        new Thread(() -> {

            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/trading/buy?size=1000&userId="+userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .add("Content-Type","application/json")
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

            runOnUiThread(()->{
                if(dataResponseBody.getMsg().equals("成功")){


                    commodityList= dataResponseBody.getData().getRecords();
                    if (dataResponseBody.getData().getTotal()==0){
                        Toast.makeText(MyBuyActivity.this,"你还没购买商品",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyBuyActivity.this,"数据获取成功",Toast.LENGTH_SHORT).show();


                        MyBuyCommodityAdapter adapter = new MyBuyCommodityAdapter(MyBuyActivity.this);
                        adapter.setData(commodityList);
                        lvMyCommodity.setAdapter(adapter);

                    }
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


    public class Data{
        private List<MyBuyCommodity> records; // 存储 Record 对象的列表
        private int total;             // 总记录数
        private int size;              // 每页数量
        private int current;           // 当前页码

        public List<MyBuyCommodity> getRecords() {
            return records;
        }

        public void setRecords(List<MyBuyCommodity> records) {
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
}
