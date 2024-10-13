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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageButton ibLearning,ibElectronic,ibDaily,ibSports;
    private final Gson gson = new Gson();
    int userId;
    String userHeadImage,username,password;
    private AllCommodityAdapter adapter;

    TextView tv_username;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


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


        //数据展示栏代码
        //listview对象lvAllCommodity容器，用来展示所有商品
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);


        adapter = new AllCommodityAdapter(getApplicationContext());
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        userHeadImage = bundle.getString("userHeadImage");
        username=bundle.getString("username");
        password = bundle.getString("password");


        tv_username=findViewById(R.id.tv_userName);
        tv_username.setText(username);



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
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        //跳转聊天页面
        ImageButton ib_chat = findViewById(R.id.ib_chat);
        TextView tv_chat=findViewById(R.id.tv_cart);

        View.OnClickListener toChat=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this, AllMessageActivity.class);
                bundle.putInt("userId",userId);
                bundle.putString("userHead",userHeadImage);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        };
        tv_chat.setOnClickListener(toChat);
        ib_chat.setOnClickListener(toChat);


        //个人中心按钮的事件处理
        ImageButton IbPersonalCenter = findViewById(R.id.ib_personal_center);
        TextView tv_person=findViewById(R.id.tv_personal_center);
        //跳转到个人中心界面
        View.OnClickListener toPersonCenter=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到PersonalCenterActivity里
                    bundle.putInt("userId", userId);
                    bundle.putString("userHeadImage",userHeadImage);
                    bundle.putString("password",password);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        };
        IbPersonalCenter.setOnClickListener(toPersonCenter);
        tv_person.setOnClickListener(toPersonCenter);

        //为每一个item设置点击事件
        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllCommodity commodity = (AllCommodity) lvAllCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putInt("goodsId",commodity.getId());
                bundle1.putInt("userId",userId);
                bundle1.putString("UserHead",userHeadImage);
                Intent intent = new Intent(MainActivity.this, CommodityDetailActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        //分类点击设置
        ImageButton ibPhone,ibLuxury,ibTrendyProducts,ibMakeUp,ibDigital,ibTrendPlay,ibGame,ibBook,ibFood,ibPlay,ibMotherAndBaby,ibFurniture,ibMusicalInstruments,ibOther;
        ibPhone=findViewById(R.id.ib_phone);
        ibLuxury=findViewById(R.id.ib_luxury);
        ibTrendyProducts=findViewById(R.id.ib_trendyProducts);
        ibMakeUp=findViewById(R.id.ib_makeUp);
        ibDigital=findViewById(R.id.ib_Digital);
        ibTrendPlay=findViewById(R.id.ib_TrendyPlay);
        ibGame=findViewById(R.id.ib_Game);
        ibBook=findViewById(R.id.ib_book);
        ibFood=findViewById(R.id.ib_food);
        ibPlay=findViewById(R.id.ib_Play);
        ibMotherAndBaby=findViewById(R.id.ib_MotherAndBaby);
        ibFurniture=findViewById(R.id.ib_Furniture);
        ibMusicalInstruments=findViewById(R.id.ib_MusicalInstruments);
        ibOther=findViewById(R.id.ib_Other);



        // 为按钮设置点击事件
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int typeId=0;
                String typeName="";
                if (v.getTag() != null) {
                    String tagValue = v.getTag().toString();
                    if ("1".equals(tagValue)) {
                        // 点了手机
                        typeId=1;
                        typeName="手机";

                    } else if ("2".equals(tagValue)) {
                        // 点了奢品
                        typeId=2;
                        typeName="奢品";
                    }
                    else if ("3".equals(tagValue)) {
                        // 点了潮品
                        typeId=3;
                        typeName="潮玩";
                    }
                    else if ("4".equals(tagValue)) {
                        // 点了美妆
                        typeId=4;
                        typeName="美妆";
                    }
                    else if ("5".equals(tagValue)) {
                        // 点了数码
                        typeId=5;
                        typeName="数码";
                    }
                    else if ("6".equals(tagValue)) {
                        // 点了潮玩
                        typeId=6;
                        typeName="潮玩";
                    }
                    else if ("7".equals(tagValue)) {
                        // 点了游戏
                        typeId=7;
                        typeName="游戏";
                    }else if ("8".equals(tagValue)) {
                        // 点了图书
                        typeId=8;
                        typeName="图书";
                    }else if ("9".equals(tagValue)) {
                        // 点了美食
                        typeId=9;
                        typeName="美食";
                    }else if ("10".equals(tagValue)) {
                        // 点了文玩
                        typeId=10;
                        typeName="文玩";
                    }
                    else if ("11".equals(tagValue)) {
                        // 点了母爱
                        typeId=11;
                        typeName="母婴";
                    }
                    else if ("12".equals(tagValue)) {
                        // 点了家居
                        typeId=12;
                        typeName="家具";
                    }else if ("13".equals(tagValue)) {
                        // 点了乐器
                        typeId=13;
                        typeName="乐器";
                    }else if ("14".equals(tagValue)) {
                        // 点了其他
                        typeId=14;
                        typeName="其他";
                    }
                    else {
                        Toast.makeText(MainActivity.this, "错误", Toast.LENGTH_SHORT).show();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putInt("userId",userId);
                    bundle.putInt("typeId",typeId);
                    bundle.putString("typeName",typeName);
                    bundle.putString("userHead",userHeadImage);
                    Intent intent = new Intent(MainActivity.this, TypeCommodityActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);



                }
            }
        };

        ibPhone.setOnClickListener(buttonClickListener);
        ibLuxury.setOnClickListener(buttonClickListener);
        ibTrendyProducts.setOnClickListener(buttonClickListener);
        ibMakeUp.setOnClickListener(buttonClickListener);
        ibDigital.setOnClickListener(buttonClickListener);
        ibTrendPlay.setOnClickListener(buttonClickListener);
        ibGame.setOnClickListener(buttonClickListener);
        ibBook.setOnClickListener(buttonClickListener);
        ibFood.setOnClickListener(buttonClickListener);
        ibPlay.setOnClickListener(buttonClickListener);
        ibMotherAndBaby.setOnClickListener(buttonClickListener);
        ibFurniture.setOnClickListener(buttonClickListener);
        ibMusicalInstruments.setOnClickListener(buttonClickListener);
        ibGame.setOnClickListener(buttonClickListener);
        ibOther.setOnClickListener(buttonClickListener);


        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 模拟加载新数据
            new Handler().postDelayed(() -> {
                refreshData(); // 刷新数据
                swipeRefreshLayout.setRefreshing(false); // 停止刷新动画
            }, 1000); // 延迟1秒
        });


    }
    private void refreshData() {
        get();
        Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
    }






    private void get(){
        new Thread(() -> {

            Head head = new Head();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/all?size=10000&userId="+ userId;

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
            Log.d("MainActivity", body);
            // 解析json串到自己封装的状态


            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){

                    List<AllCommodity> allProductList = new ArrayList<>();
                    List<AllCommodity> productList = new ArrayList<>();

                    allProductList= dataResponseBody.getData().getRecords();

                    for (AllCommodity commodity : allProductList){
                        if (commodity.getImageUrlList().size()!=0){
                            productList.add(commodity);
                        }
                    }
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