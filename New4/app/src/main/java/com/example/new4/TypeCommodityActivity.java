package com.example.new4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.new4.adapter.AllCommodityAdapter;
import com.example.new4.adapter.TypeCommodityAdapter;
import com.example.new4.bean.AllCommodity;
import com.example.new4.bean.Head;
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

public class TypeCommodityActivity extends AppCompatActivity {

    GridView lvTypeCommodity;
    TextView tvType;
    List<AllCommodity> productList = new ArrayList<>();

    Head head = new Head();
    private final Gson gson = new Gson();

    public int userId,typeId;
    public String typeName,userHead;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_type);


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

        lvTypeCommodity = findViewById(R.id.list_commodity);


        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        typeName=bundle.getString("typeName");
        typeId=bundle.getInt("typeId");
        userHead=bundle.getString("userHead");


        tvType=findViewById(R.id.tv_type);
        tvType.setText(typeName);

        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        get();

        //为每一个item设置点击事件
        lvTypeCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllCommodity commodity = (AllCommodity) lvTypeCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putInt("goodsId",commodity.getId());
                bundle1.putInt("userId",userId);
                bundle1.putString("UserHead",userHead);
                Intent intent = new Intent(TypeCommodityActivity.this, CommodityDetailActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

    }

    private void get(){
        new Thread(() -> {

            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/all?size=100&userId="+ userId;

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
                client.newCall(request).enqueue(callback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取所有商品的回调
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
            Type jsonType = new TypeToken<MainActivity.ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("MainActivity", body);
            // 解析json串到自己封装的状态


            MainActivity.ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){

                    List<AllCommodity> allProductList = new ArrayList<>();

                    allProductList = dataResponseBody.getData().getRecords();
                    for (AllCommodity commodity : allProductList){
                        if (commodity.getTypeId()==typeId){
                            productList.add(commodity);
                        }
                    }
                    TypeCommodityAdapter adapter = new TypeCommodityAdapter(getApplicationContext());
                    adapter.setData(productList);
                    lvTypeCommodity.setAdapter(adapter);

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
        private MainActivity.Data data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public MainActivity.Data getData() {
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
        private List<AllCommodity> records;
        private int total;
        private int size;
        private int current;

        // Getters and Setters


        public List<AllCommodity> getRecords() {
            return records;
        }

        public void setRecords(List<AllCommodity> records) {
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
