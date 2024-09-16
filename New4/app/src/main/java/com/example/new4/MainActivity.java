package com.example.new4;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.new4.adapter.AllCommodityAdapter;
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

public class MainActivity extends AppCompatActivity {

    GridView lvAllCommodity;
    ImageButton ibLearning,ibElectronic,ibDaily,ibSports;
    private final Gson gson = new Gson();
    int userId;
    String userHeadImage;
    List<AllCommodity> productList = new ArrayList<>();
    private AllCommodityAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //数据展示栏代码
        //listview对象lvAllCommodity容器，用来展示所有商品
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        adapter = new AllCommodityAdapter(getApplicationContext());
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        userHeadImage = bundle.getString("userHeadImage");

        get();


        //发布闲置的按钮的事件处理
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        //跳转到添加物品界面
        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到addCommodityActivity里
                    bundle.putInt("userId",userId);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });


        //个人中心按钮的事件处理
        ImageButton IbPersonalCenter = findViewById(R.id.ib_personal_center);
        //跳转到个人中心界面
        IbPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到PersonalCenterActivity里
                    bundle.putInt("userId", userId);
                    bundle.putString("userHeadImage",userHeadImage);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });


        //为每一个item设置点击事件
        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllCommodity commodity = (AllCommodity) lvAllCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putInt("goodsId",commodity.getId());
                Intent intent = new Intent(MainActivity.this, CommodityDetailActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });






    }




    private void get(){
        new Thread(() -> {

            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/all?userId="+ userId;

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
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态


            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){

                    productList = dataResponseBody.getData().getRecords();
                    AllCommodityAdapter adapter1 = new AllCommodityAdapter(getApplicationContext());
                    adapter1.setData(productList);
                    lvAllCommodity.setAdapter(adapter1);

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
        private Data  data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public Data  getData() {
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