package com.leaf.collegeidleapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.bean.Head;
import com.leaf.collegeidleapp.bean.ImageData;
import com.leaf.collegeidleapp.bean.addCommodity;

import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

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
 * 物品发布界面Activity类
 * @author : autumn_leaf
 */
public class AddCommodityActivity extends AppCompatActivity {

    TextView tvStuId;
    ImageButton ivPhoto;
    EditText etTitle, etPrice, etPhone, etDescription, etAddr;
    Spinner spType;
    private String[] typeOptions = {"手机", "奢品", "潮品", "美妆", "数码", "潮玩", "游戏", "图书", "美食", "文玩", "母婴", "家居", "乐器", "其他"};
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;


    int randomId = ThreadLocalRandom.current().nextInt(100000, 1000000);

    private final Gson gson = new Gson();

    Head head = new Head();
    Integer userId;
    List<File> imaFile = new ArrayList<>();

    addCommodity addCommodity = new addCommodity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commodity);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        tvStuId = findViewById(R.id.tv_student_id);
        tvStuId.setText(this.getIntent().getStringExtra("user_id"));

        userId = this.getIntent().getIntExtra("userId",0);
        Button btnBack = findViewById(R.id.btn_back);
        //返回按钮点击事件
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivPhoto = findViewById(R.id.iv_photo);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 打开图片选择器
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // 创建选择图片的Intent
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 启动选择图片的Intent
            }
        });


        etTitle = findViewById(R.id.et_title);
        etPrice = findViewById(R.id.et_price);
        etPhone = findViewById(R.id.et_phone);
        etDescription = findViewById(R.id.et_description);
        spType = findViewById(R.id.spn_type);
        etAddr = findViewById(R.id.et_addr);

        spType.setAdapter(adapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 当选项被选择时，获取对应的 typeId
                addCommodity.setTypeId(position + 1); // typeId 应该从 1 开始
                addCommodity.setTypeName(typeOptions[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 当没有选项被选择时的处理
            }
        });
        Button btnPublish = findViewById(R.id.btn_publish);
        //发布按钮点击事件
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先检查合法性
                if (CheckInput()) {
                    post2();
                }
            }
        });

    }

    /**
     * 检查输入是否合法
     */
    public boolean CheckInput() {
        String title = etTitle.getText().toString();
        String price = etPrice.getText().toString();
        String type = spType.getSelectedItem().toString();
        String phone = etPhone.getText().toString();
        String description = etDescription.getText().toString();
        String address = etAddr.getText().toString();
        if (title.trim().equals("")) {
            Toast.makeText(this, "商品标题不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (price.trim().equals("")) {
            Toast.makeText(this, "商品价格不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (type.trim().equals("请选择类别")) {
            Toast.makeText(this, "商品类别未选择!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phone.trim().equals("")) {
            Toast.makeText(this, "手机号码不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.trim().equals("")) {
            Toast.makeText(this, "商品描述不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (address.trim().equals("")) {
            Toast.makeText(this, "地址不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void post1() {
        OkHttpClient client = new OkHttpClient();  // 创建OkHttpClient实例
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/image/upload";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

           MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
           imaFile.add(new File(Objects.requireNonNull(getImagePathFromUri(imageUri))));
            for (File file : imaFile){

               requestBodyBuilder.addFormDataPart(
                       "fileList",
                       file.getName(),
                       RequestBody.create(MediaType.parse("image/jpge"),file)
               );
           }

            // 创建请求体
            RequestBody requestBody =  requestBodyBuilder.build();


            // 创建请求
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(requestBody)
                    .build();

            try {
                // 发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback1);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void post2() {
        new Thread(() -> {
            String title = etTitle.getText().toString();
            String price = etPrice.getText().toString();
            String phone = etPhone.getText().toString();
            String description = etDescription.getText().toString();
            String address = etAddr.getText().toString();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/goods/add";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Content-Type", "application/json")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("price", price);
            bodyMap.put("imageCode", addCommodity.getImageCode());
            bodyMap.put("typeName", addCommodity.getTypeName());
            bodyMap.put("typeId", addCommodity.getTypeId());

            bodyMap.put("addr", address);
            bodyMap.put("userId", userId);
            bodyMap.put("content", title);
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
                client.newCall(request).enqueue(callback2);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback1 = new Callback() { //商品图片uil的回调
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            assert response.body() != null;
            String body = response.body().string();
            Log.d("info1", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info1", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")) {
                    Toast.makeText(AddCommodityActivity.this, "上传图片成功!", Toast.LENGTH_SHORT).show();

                    ImageData imaData = dataResponseBody.getData();
                    addCommodity.setImageCode(imaData.getImageCode());

                }
//                else if(dataResponseBody.getMsg().equals("密码错误")) {
//                    Toast.makeText(AddCommodityActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();
//
//                }
//                else if(dataResponseBody.getMsg().equals("当前登录用户不存在")) {
//                    Toast.makeText(AddCommodityActivity.this, "当前登录用户不存在!", Toast.LENGTH_SHORT).show();
//
//                }



            });

        }
    };

    private final Callback callback2 = new Callback() { //商品回调
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")) {
                    Toast.makeText(AddCommodityActivity.this, "新增商品成功!", Toast.LENGTH_SHORT).show();
                    finish();
                }


            });
        }
    };

    /**
     * http响应体的封装协议
     *
     * @param <T> 泛型
     */
    public static class ResponseBody<T> {

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
        private ImageData data;

        public ResponseBody() {
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public ImageData getData() {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 从返回的数据中获取图片的URI
            imageUri = data.getData();
            ivPhoto.setImageURI(imageUri);
            // 获取图片路径并上传


            uploadImage();  // 调用上传图片方法
        }
    }


    private void uploadImage() {
        if (imageUri != null) {
            // 将URI转换为File路径并上传
            String imagePath = getImagePathFromUri(imageUri);
            if (imagePath != null) {

                post1();  // 调用上传到服务器的方法
            }
        }
    }

    private String getImagePathFromUri(Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        return imagePath;
    }
}