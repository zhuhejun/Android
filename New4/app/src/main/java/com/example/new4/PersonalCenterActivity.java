package com.example.new4;

import static com.example.new4.LoginActivity.logout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    String userHeadImage,username,password;
    ImageView userHead;
    private static final int PICK_IMAGE_REQUEST = 1;
    Head head = new Head();
    private final Gson gson = new Gson();
    List<File> imaFile = new ArrayList<>();
    private Uri imageUri;
    public List<String> imageUrlList1; // 对应 imageUrlList
    public String backHeadImg;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);

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


        final Bundle bundle = this.getIntent().getExtras();
        userId = bundle.getInt("userId");
        userHeadImage = bundle.getString("userHeadImage");
        userHead=findViewById(R.id.iv_user_photo);
        username = bundle.getString("username");
        password = bundle.getString("password");




        if (isFinishing()) {
            //不加载
        } else if(userHeadImage != null) {
            Glide.with(PersonalCenterActivity.this)
                    .load(userHeadImage)
                    .into(userHead);
        }else{
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
                if(backHeadImg!=null){
                    Intent intent = new Intent(PersonalCenterActivity.this, MainActivity.class);
                    Bundle bundle2=new Bundle();
                    bundle2.putString("userHeadImage", backHeadImg); // 将字符串传入 Intent
                    bundle2.putInt("userId",userId);
                    bundle2.putString("username",username);
                    bundle2.putString("password",password);
                    intent.putExtras(bundle2);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    finishAffinity();
                }else {
                    finish();
                }

            }
        });


        //修改头像按钮
        Button btn_changHead=findViewById(R.id.btn_modify_head);
        btn_changHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 打开图片选择器
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // 创建选择图片的Intent
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 启动选择图片的Intent
            }
        });


        //点击头像按钮
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalCenterActivity.this,PictureActivity.class);
                Bundle bundle=new Bundle();
                if (backHeadImg!=null){
                    bundle.putString("bigImage",backHeadImg);
                }
                else {
                    bundle.putString("bigImage",userHeadImage);
                }

                intent.putExtras(bundle);

                startActivity(intent);

                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        //点击个人信息按钮
        Button btnUserInfo = findViewById(R.id.btn_user_info);
        btnUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("username",username);
                bundle.putString("password",password);
                bundle.putInt("userId",userId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        //点击查看我的购买按钮
        Button myBuy = findViewById(R.id.btn_mybuy);
        myBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyBuyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("userId",userId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });






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
                        Bundle bundleLogin=new Bundle();
                        bundleLogin.putInt("clearFlag",1);
                        intent.putExtras(bundleLogin);

                        //清除登录时的数据

                        startActivity(intent);
                        finishAffinity(); // 结束当前 activity 及其所有兄弟 activity
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

    //重写了系统退回而不是按案件退回的bug
    @Override
    public void onBackPressed() {
        if (backHeadImg != null) {
            // 创建 Intent 去启动 MainActivity
            Intent intent = new Intent(PersonalCenterActivity.this, MainActivity.class);
            Bundle bundle2 = new Bundle();
            bundle2.putString("userHeadImage", backHeadImg); // 传入字符串
            bundle2.putInt("userId", userId);
            bundle2.putString("username", username);
            bundle2.putString("password", password);


            intent.putExtras(bundle2);

            // 启动 MainActivity 并添加动画
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            // 结束当前 Activity
            finishAffinity();
        } else {
            // 如果 backHeadImg 为 null 直接退出当前 Activity
            super.onBackPressed(); // 调用父类的 onBackPressed 方法
        }
    }


    //上传文件
    private void uploadFile() {
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
            Type jsonType = new TypeToken<PersonalCenterActivity.imageUploadResponseBody<Object>>() {
            }.getType();
            // 获取响应体的json串
            assert response.body() != null;
            String body = response.body().string();
            Log.d("info1", body);
            // 解析json串到自己封装的状态
            PersonalCenterActivity.imageUploadResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info1", dataResponseBody.toString());

            runOnUiThread(()->{
                if (dataResponseBody.getMsg().equals("成功")) {
                    Toast.makeText(PersonalCenterActivity.this, "上传图片成功!", Toast.LENGTH_SHORT).show();
                    imageData data = dataResponseBody.getData();//imageUrlList1要传去修改个人信息
                    imageUrlList1= data.getImageUrlList();
                    backHeadImg=imageUrlList1.get(0);
                    changeProfilePicture();
                }
                else{
                    Toast.makeText(PersonalCenterActivity.this, dataResponseBody.getMsg(), Toast.LENGTH_SHORT).show();
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
                uploadFile();  // 调用上传到服务器的方法
            }
        }
    }

    /**
     * http响应体的封装协议
     *
     * @param <T> 泛型
     */
    public static class imageUploadResponseBody<T> {

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
        private imageData data;

        public imageUploadResponseBody() {
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public imageData getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "imageUploadResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
    public class imageData {

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
    private void changeProfilePicture(){
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
            Type jsonType = new TypeToken<imageUploadResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            imageUploadResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(()->{
                Toast.makeText(PersonalCenterActivity.this,dataResponseBody.getMsg(),Toast.LENGTH_SHORT).show();
            });
        }
    };

    public class changeData {
        private String id;
        private String appKey;
        private String username;
        private int money;
        private String avatar;
        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }




}
