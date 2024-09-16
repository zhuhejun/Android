package com.example.new4;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.new4.bean.Head;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个人中心主界面Activity类
 */
public class PersonalCenterActivity extends AppCompatActivity {

    TextView TvStuNumber;
    int userId;
    String userHeadImage;
    ImageView userHead;
    private static final int PICK_IMAGE_REQUEST = 1;
    Head head = new Head();
    private final Gson gson = new Gson();
    List<File> imaFile = new ArrayList<>();
    private Uri imageUri;
    private List<String> imageUrlList1; // 对应 imageUrlList

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        final Bundle bundle = this.getIntent().getExtras();

        userId = bundle.getInt("userId");
        userHeadImage = bundle.getString("userHeadImage");
        userHead=findViewById(R.id.iv_user_photo);
        if (userHeadImage != null) {
            Glide.with(PersonalCenterActivity.this)
                    .load(userHeadImage)
                    .into(userHead);
        } else {
            // 处理无图片数据时的逻辑，比如显示默认图片
            userHead.setImageResource(R.drawable.icon_user_photo);
        }
        //取出登录时的登录名
        TvStuNumber = findViewById(R.id.tv_student_number);
        String StuNumber = this.getIntent().getStringExtra("username1");//mainActivity传的
        TvStuNumber.setText(StuNumber);

        //返回主界面
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //修改头像按钮
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开图片选择器
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // 创建选择图片的Intent
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 启动选择图片的Intent


            }
        });


//
//        //点击修改密码按钮
//        final Button btnModifyPwd = findViewById(R.id.btn_modify_password);
//        btnModifyPwd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),ModifyPwdActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("stu_number",TvStuNumber.getText().toString());
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
//
//

        //点击查看我的发布按钮
        Button btnMyGoods = findViewById(R.id.btn_my_goods);
        btnMyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyCommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("userId",userId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


//
//        //点击个人信息按钮
//        Button btnUserInfo = findViewById(R.id.btn_user_info);
//        btnUserInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("stu_number1",TvStuNumber.getText().toString());
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
//


        //退出登录按钮点击事件
        Button btnLogOut = findViewById(R.id.btn_logout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalCenterActivity.this);
                builder.setTitle("提示:").setMessage("确认退出系统吗?").setIcon(R.drawable.icon_user).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //跳转到登录界面
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
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
            Type jsonType = new TypeToken<PersonalCenterActivity.ResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            assert response.body() != null;
            String body = response.body().string();
            Log.d("info1", body);
            // 解析json串到自己封装的状态
            PersonalCenterActivity.ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info1", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")) {
                    Toast.makeText(PersonalCenterActivity.this, "上传图片成功!", Toast.LENGTH_SHORT).show();
                    Data data = dataResponseBody.getData();
                    imageUrlList1 = data.getImageUrlList();//imageUrlList1要传去修改个人信息

                    post2();
                }


            });

        }
    };



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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // 从返回的数据中获取图片的URI
            imageUri = data.getData();
            userHead.setImageURI(imageUri);
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
        private Data data;

        public ResponseBody() {
        }

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

        private String imageCode;
        private List<String> imageUrlList; // 对应 imageUrlList

        public String getImageCode() {
            return imageCode;
        }

        public void setImageCode(String imageCode) {
            this.imageCode = imageCode;
        }

        public List<String> getImageUrlList() {
            return imageUrlList;
        }

        public void setImageUrlList(List<String> imageUrlList) {
            this.imageUrlList = imageUrlList;
        }
    }

    //修改头像用
    private void post2(){
        new Thread(() -> {

            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/user/update";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", imageUrlList1.get(0));
            bodyMap.put("userId", userId);
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

                if(dataResponseBody.getMsg().equals("成功"));
                {
                    Toast.makeText(PersonalCenterActivity.this,"头像更新成功",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
