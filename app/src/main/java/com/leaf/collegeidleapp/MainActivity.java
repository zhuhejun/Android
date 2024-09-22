package com.leaf.collegeidleapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.adapter.AllCommodityAdapter1;
import com.leaf.collegeidleapp.bean.AllCommodity1;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.bean.Head;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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


/**
 * 主界面活动类
 * @author: autumn_leaf
 */
public class MainActivity extends AppCompatActivity {

    GridView lvAllCommodity;
    List<Commodity> allCommodities = new ArrayList<>();
    ImageButton ibLearning,ibElectronic,ibDaily,ibSports;
    TextView tv_name;
    private final Gson gson = new Gson();

    CommodityDbHelper dbHelper;
    AllCommodityAdapter adapter;
    int userId;
    List<AllCommodity1> productList = new ArrayList<>();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //数据展示栏代码
        //listview对象lvAllCommodity容器，用来展示所有商品
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        //弄了个管理器dbHelper和适配器adapter
        dbHelper = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
        adapter = new AllCommodityAdapter(getApplicationContext());
        //通过管理器dbHelper给allCommodities赋予数据
        allCommodities = dbHelper.readAllCommodities();

        //使用adapter给allCommodities里的数据存到adapter里
        adapter.setData(allCommodities);
        //抽取adapter里的数据进行展示
        lvAllCommodity.setAdapter(adapter);
        //接受来自loginActivity里的bundle传过来的username用于展示

        get();




        final Bundle bundle = this.getIntent().getExtras();
        String stuNum = bundle.getString("username");
        userId = bundle.getInt("userId");

        Toast.makeText(MainActivity.this, "userId:"+userId, Toast.LENGTH_SHORT).show();


        tv_name=findViewById(R.id.tv_userName);
        tv_name.setText(stuNum);

        //发布闲置的按钮的事件处理
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        //跳转到添加物品界面
        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到addCommodityActivity里

                    bundle.putString("user_id", stuNum);
                    bundle.putInt("userId",userId);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });


        //进入购物车的按钮的事件处理
        ImageButton IbCartProduct = findViewById(R.id.ib_cart);
        //跳转到购物车
        IbCartProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到cartActivity里
                    bundle.putString("user_id", stuNum);
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
                    bundle.putString("username1", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });




        //刷新界面按钮事件处理
        ImageView tvRefresh = findViewById(R.id.iv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommodities = dbHelper.readAllCommodities();
                adapter.setData(allCommodities);
                lvAllCommodity.setAdapter(adapter);
            }
        });


        //为每一个item设置点击事件
        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllCommodity1 commodity = (AllCommodity1) lvAllCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);

                bundle1.putString("stuId",stuNum);
                bundle1.putInt("commodityId",commodity.getId());

                Intent intent = new Intent(MainActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });



        //点击不同的类别,显示不同的商品信息
        ibLearning = findViewById(R.id.ib_learning_use);
        ibElectronic = findViewById(R.id.ib_electric_product);
        ibDaily = findViewById(R.id.ib_daily_use);
        ibSports = findViewById(R.id.ib_sports_good);
        final Bundle bundle2 = new Bundle();
        //学习用品
        ibLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",1);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //电子用品
        ibElectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",2);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //生活用品
        ibDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",3);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //体育用品
        ibSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",4);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
    }

    public void loadData(){
        allCommodities = dbHelper.readAllCommodities();
        adapter.setData(allCommodities);
        lvAllCommodity.setAdapter(adapter);
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
                    Toast.makeText(MainActivity.this,"成功获取商品数据",Toast.LENGTH_SHORT).show();
                    productList = dataResponseBody.getData().getRecords();
                    AllCommodityAdapter1 adapter1 = new AllCommodityAdapter1(getApplicationContext());
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
        private List<AllCommodity1> records;
        private int total;
        private int size;
        private int current;

        // Getters and Setters


        public List<AllCommodity1> getRecords() {
            return records;
        }

        public void setRecords(List<AllCommodity1> records) {
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