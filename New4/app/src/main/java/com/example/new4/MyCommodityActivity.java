package com.example.new4;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

    private ListView lvMyCommodity;
    List<MyCommodity> productList = new ArrayList<>();
    private Button btnSold,btnUnsold;
    private  MyCommodityAdapter adapter;

    int userId,goodsId;
    private final Gson gson = new Gson();
    Head head = new Head();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commodity1);

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

        //事件绑定
        lvMyCommodity = findViewById(R.id.lv_my_commodity);
        adapter= new MyCommodityAdapter(getApplicationContext());

        //获取传过来的userId用于查询
        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        getCommodity();

        //返回按钮
        TextView tvBack = findViewById(R.id.tv_back);
        //点击返回销毁当前界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });








        //为每一个item设置点击事件
        lvMyCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCommodity commodity = (MyCommodity) lvMyCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putInt("goodsId",commodity.getId());
                Intent intent = new Intent(MyCommodityActivity.this, CommodityDetailActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });


        //删除按钮的点击事件

        adapter.setOnItemClickListener(new MyCommodityAdapter.onItemClickListener() {
            @Override
            public void deleteClick(MyCommodity commodity, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommodityActivity.this);
                builder.setTitle("提示:").setMessage("确认删除此商品项吗?").setIcon(R.drawable.icon_user)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //根据点击的位置，获取点击位置的item的信MyCommodity commodity = (MyCommodity) adapterDelete.getItem(position);
                                MyCommodity commodity = (MyCommodity) adapter.getItem(position);
                                goodsId=commodity.getId();

                                deletePost(goodsId);

                                getCommodity();

                                Toast.makeText(MyCommodityActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();

                            }
                        }).show();


            }
        });





        //--------------------------------------------------------------------------------
    }


    private void getCommodity(){
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
//            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
//            Log.d("info", dataResponseBody.toString());


            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")){

                    productList =  dataResponseBody.getData().getRecords();

                    loadData(productList);

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


    public void loadData(List<MyCommodity> productList){
        adapter = new MyCommodityAdapter(MyCommodityActivity.this);
        adapter.setData(productList);
        lvMyCommodity.setAdapter(adapter);

    }


    private void deletePost(int goodsId){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/delete?goodsId="+goodsId+"&userId="+userId;

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
                client.newCall(request).enqueue(deleteCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback deleteCallback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<deleteResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            deleteResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{

                if (dataResponseBody.getCode()==200){
                    Toast.makeText(MyCommodityActivity.this,"商品删除成功",Toast.LENGTH_SHORT).show();
                    getCommodity();
                }
                else {
                    Toast.makeText(MyCommodityActivity.this,dataResponseBody.getMsg(),Toast.LENGTH_SHORT).show();
                }

            });


        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class deleteResponseBody <T> {

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

        public deleteResponseBody(){}

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

}
