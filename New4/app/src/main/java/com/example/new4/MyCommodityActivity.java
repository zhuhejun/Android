package com.example.new4;

import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;

import com.example.new4.adapter.AllCommodityAdapter;
import com.example.new4.adapter.MyCommodityAdapter;
import com.example.new4.bean.AllCommodity;
import com.example.new4.bean.Head;
import com.example.new4.bean.MyCommodity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyCommodityActivity extends AppCompatActivity {

    private ListView lvMyCommodity1,lvMyCommodity2;
    List<MyCommodity> productList = new ArrayList<>();
    private Button btnSold,btnUnsold;


    int userId;
    private final Gson gson = new Gson();
    Head head = new Head();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commodity1);

        //获取传过来的userId用于查询
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        get();

        //返回按钮
        TextView tvBack = findViewById(R.id.tv_back);
        //点击返回销毁当前界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //事件绑定
        lvMyCommodity1 = findViewById(R.id.lv_my_commodity1);
        lvMyCommodity2 = findViewById(R.id.lv_my_commodity2);
        btnSold = findViewById(R.id.btn_sold);
        btnUnsold = findViewById(R.id.btn_unsold);

        // 点击“已经出售”按钮显示第一个 ListView，隐藏第二个 ListView
        btnSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMyCommodity1.setVisibility(View.VISIBLE);
                lvMyCommodity2.setVisibility(View.GONE);
                get();
            }
        });

        // 点击“未出售”按钮显示第二个 ListView，隐藏第一个 ListView
        btnUnsold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvMyCommodity1.setVisibility(View.GONE);
                lvMyCommodity2.setVisibility(View.VISIBLE);
            }
        });


        //为每一个item设置点击事件
        lvMyCommodity1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCommodity commodity = (MyCommodity) lvMyCommodity1.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putInt("goodsId",commodity.getId());
                Intent intent = new Intent(MyCommodityActivity.this, CommodityDetailActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });




        //--------------------------------------------------------------------------------
    }


    private void get(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/myself?userId="+userId;

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
                if (dataResponseBody.getMsg().equals("成功")){





                    productList =  dataResponseBody.getData().getRecords();
                    MyCommodityAdapter adapter = new MyCommodityAdapter(MyCommodityActivity.this);
                    Toast.makeText(MyCommodityActivity.this,productList.get(1).getContent(),Toast.LENGTH_SHORT).show();

                    adapter.setData(productList);
                    lvMyCommodity1.setAdapter(adapter);

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

    public class Data {
        private List<MyCommodity> records;
        private int total;
        private int size;
        private int current;

        public List<MyCommodity> getRecords() {
            return records;
        }

        public void setRecords(List<MyCommodity> records) {
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
