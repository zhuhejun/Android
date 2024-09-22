package com.leaf.collegeidleapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.adapter.MyCollectionAdapter;
import com.leaf.collegeidleapp.adapter.ReviewAdapter;
import com.leaf.collegeidleapp.bean.Cart;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.bean.Head;
import com.leaf.collegeidleapp.bean.Review;
import com.leaf.collegeidleapp.util.CartDbHelper;
import com.leaf.collegeidleapp.util.CommodityDbHelper;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;
import com.leaf.collegeidleapp.util.ReviewDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * 商品信息评论/留言类
 * @author autumn_leaf
 */
public class ReviewCommodityActivity extends AppCompatActivity {

    TextView title,price;
    ImageView ivCommodity;
    ListView lvReview;
    LinkedList<Review> reviews = new LinkedList<>();
    EditText etComment;
    int position,commodityId;
    byte[] picture;
    List<Collection> myCollections = new ArrayList<>();
    List<Cart> myCarts = new ArrayList<>();
    Gson gson = new Gson();
    Head head = new Head();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_comodity1);
        ivCommodity = findViewById(R.id.iv_commodity);
        title = findViewById(R.id.tv_title);
        price = findViewById(R.id.tv_price);
        Bundle b = getIntent().getExtras();
        if( b != null) {
            commodityId=b.getInt("commodityId");
            position = b.getInt("position");

        }
        Toast.makeText(ReviewCommodityActivity.this,commodityId,Toast.LENGTH_SHORT).show();

//        loadData2(commodityId);
        //返回
        ImageView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        //点击收藏按钮
//        String stuid=b.getString("stuId") ;
//        ImageView ibMyLove = findViewById(R.id.ib_my_love);
//        ibMyLove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isAlreadyCollected = false;
//                MyCollectionDbHelper dbHelper1 = new MyCollectionDbHelper(getApplicationContext(),MyCollectionDbHelper.DB_NAME,null,1);
//                myCollections = dbHelper1.readMyCollectionsCommodityId(stuid);
//                for (Collection collection : myCollections) {
//                    if (collection.getCommodityId() == commodityId) {
//                        isAlreadyCollected = true;
//                        break;
//                    }
//                }
//                if (isAlreadyCollected){
//                    Toast.makeText(getApplicationContext(),"已经收藏该商品!",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    //收藏至购物车
//                    MyCollectionDbHelper dbHelper = new MyCollectionDbHelper(getApplicationContext(),MyCollectionDbHelper.DB_NAME,null,1);
//                    Collection collection = new Collection();
//                    collection.setCommodityId(commodityId);
//                    String stuId = getIntent().getStringExtra("stuId");
//                    collection.setStuId(stuId);
//                    dbHelper.addMyCollection(collection);
//                    Toast.makeText(getApplicationContext(),"已添加至我的收藏!",Toast.LENGTH_SHORT).show();
//                    //修改该商品的收藏次数
//                    CommodityDbHelper db=new CommodityDbHelper(getApplicationContext(),CommodityDbHelper.DB_NAME,null,1);
//                    db.upCollectionNum1(commodityId);
//                    loadData2(commodityId);
//
//                }
//            }
//        });
//
//        //点击加入购物车按钮
//        ImageView ibMyCart = findViewById(R.id.ib_my_cart);
//        ibMyCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean isAlreadyCollected = false;
//                CartDbHelper dbHelper1 = new CartDbHelper(getApplicationContext(),CartDbHelper.DB_NAME,null,1);
//                myCarts = dbHelper1.readMyCartCommodityId(stuid);
//                for (Cart cart : myCarts) {
//                    if (cart.getCommodityId() == commodityId) {
//                        isAlreadyCollected = true;
//                        break;
//                    }
//                }
//                if (isAlreadyCollected){
//                    Toast.makeText(getApplicationContext(),"已经将该商品加入购物车!",Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    CartDbHelper dbHelper = new CartDbHelper(getApplicationContext(),CartDbHelper.DB_NAME,null,1);
//                    Cart cart = new Cart();
//                    cart.setCommodityId(commodityId);
//                    String stuId = getIntent().getStringExtra("stuId");
//                    cart.setStuId(stuId);
//                    dbHelper.addMyCart(cart);
//                    Toast.makeText(getApplicationContext(),"已添加至我的购物车!",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
//        //提交评论点击事件
//        Button btnReview = findViewById(R.id.btn_submit);
//        btnReview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //先检查是否为空
//                if(CheckInput()) {
//                    ReviewDbHelper dbHelper = new ReviewDbHelper(getApplicationContext(),ReviewDbHelper.DB_NAME,null,1);
//                    Review review = new Review();
//                    review.setContent(etComment.getText().toString());
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//                    //获取当前时间
//                    Date date = new Date(System.currentTimeMillis());
//                    review.setCurrentTime(simpleDateFormat.format(date));
//                    String stuId = getIntent().getStringExtra("stuId");
//                    review.setStuId(stuId);
//                    review.setPosition(position);
//                    review.setCommodityId(commodityId);
//                    dbHelper.addReview(review);
//                    //评论置为空
//                    etComment.setText("");
//                    CommodityDbHelper dbHelper3 = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
//                    Commodity commodity = dbHelper3.readCommodity(commodityId);
//                    dbHelper3.upReviewNum1(commodity);
//
//                    loadData1();
//                    loadData2(commodityId);
//                    Toast.makeText(getApplicationContext(),"评论成功!",Toast.LENGTH_SHORT).show();
//                }
//
//
//            }
//        });


        final ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
        final ReviewDbHelper dbHelper = new ReviewDbHelper(getApplicationContext(),ReviewDbHelper.DB_NAME,null,1);
        reviews = dbHelper.readReviews(commodityId);
        adapter.setData(reviews);
        //设置适配器
        lvReview.setAdapter(adapter);

    }

//    /**
//     * 检查输入评论是否为空
//     * @return true
//     */
//    public boolean CheckInput() {
//        String comment = etComment.getText().toString();
//        if (comment.trim().equals("")) {
//            Toast.makeText(this,"评论内容不能为空!",Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }



    public void loadData1(){
        ReviewDbHelper dbHelper = new ReviewDbHelper(getApplicationContext(),ReviewDbHelper.DB_NAME,null,1);
        ReviewAdapter adapter = new ReviewAdapter(getApplicationContext());
        reviews = dbHelper.readReviews(commodityId);
        adapter.setData(reviews);
        lvReview.setAdapter(adapter);
    }

    public void loadData2( int commodityId) {

        Commodity commodity;
        CommodityDbHelper dbHelper = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
        commodity = dbHelper.readCommodity(commodityId);
        title.setText(commodity.getTitle());
        price.setText("¥" + String.valueOf(commodity.getPrice()));
        picture = commodity.getPicture();
        Bitmap img = BitmapFactory.decodeByteArray(picture,0,picture.length);
        ivCommodity.setImageBitmap(img);

    }

    private void get(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/details?goodsId="+commodityId;

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
        private T data;

        public ResponseBody(){}

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
