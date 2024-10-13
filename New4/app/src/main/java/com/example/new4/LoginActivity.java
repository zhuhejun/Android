package com.example.new4;


import static com.example.new4.SplashActivity.sharedPreferences;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.new4.api.JavaPOSTRequest;
import com.example.new4.bean.Head;
import com.example.new4.bean.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.Headers;
import okhttp3.MediaType;

public class LoginActivity extends AppCompatActivity {

    private final Gson gson = new Gson();
    EditText EtStuNumber, EtStuPwd;
    ImageView iv_visible;

    private String username, password;
    private boolean loginIsClickable = true; // 控制登录按钮可点击状态

    public CheckBox cb_RememberMe, cb_autoLogin; // 声明 CheckBox

    int clearFlag=0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);



        // 不显示顶部状态栏
        if (Build.VERSION.SDK_INT >= 21) {
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

        TextView tvRegister = findViewById(R.id.tv_register);
        // 跳转到注册界面
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        EtStuNumber = findViewById(R.id.et_username);
        EtStuPwd = findViewById(R.id.et_password);
        iv_visible = findViewById(R.id.visible);
        cb_RememberMe = findViewById(R.id.cb_remember_me);
        cb_autoLogin = findViewById(R.id.cb_autoLogin);
        final Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            clearFlag = bundle.getInt("clearFlag");
        }



//
//        if (flag==0){
//            //不再显示LoginActivity
//
//            loadSavedLoginInfo();
//        }else {
//            EtStuNumber.setText("");
//            EtStuPwd.setText("");
//            //取消勾选保存密码和自动登录
//            cb_RememberMe.setChecked(false); // 清空“保存密码”
//            cb_autoLogin.setChecked(false);   // 清空“自动登录”
//        }


        if (clearFlag==1){
            EtStuNumber.setText("");
            EtStuPwd.setText("");
            //取消勾选保存密码和自动登录
            cb_RememberMe.setChecked(false); // 清空“保存密码”
            cb_autoLogin.setChecked(false);   // 清空“自动登录”
            logout();
        }else if(sharedPreferences.getBoolean("rem_code",false)&&sharedPreferences.getString("username",null)!=null&&sharedPreferences.getString("password",null)!=null){
            EtStuNumber.setText( sharedPreferences.getString("username", ""));
            EtStuPwd.setText(sharedPreferences.getString("password", ""));
            cb_RememberMe.setChecked(true);
        }


        //登录按钮
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInput()) {
                    if (loginIsClickable) {
                        //登录时放数据
                        SharedPreferences.Editor editor = sharedPreferences.edit();


                        if (cb_RememberMe.isChecked()){
                            editor.putString("password",EtStuNumber.getText().toString());
                            editor.putString("password",EtStuPwd.getText().toString());
                        }else{
                            editor.remove("username"); // 仅删除用户名
                            editor.remove("password"); // 仅删除密码

                        }
                        editor.putBoolean("rem_code",cb_RememberMe.isChecked());
                        editor.putBoolean("auto_login",cb_autoLogin.isChecked());
                        editor.apply();

                        post(); // 开始登录请求

                    }



                    loginIsClickable = false;

                    // 使用 Handler 延迟 5 秒后使按钮可点击
                    new Handler().postDelayed(() -> loginIsClickable = true, 5000); // 5000 毫秒，即 5 秒
                } else {
                    Toast.makeText(LoginActivity.this, "输入不符合要求，请检查", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //密码显示和隐藏
        iv_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility();
            }
        });

        //自动登录和记住密码逻辑
        cb_autoLogin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cb_RememberMe.setChecked(true); // 自动勾选保存密码
            }
        });
        cb_RememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                cb_autoLogin.setChecked(false); // 取消勾选自动登录
            }
        });

    }

//    // 加载保存的登录信息,自动登录功能
//    private void loadSavedLoginInfo() {
//        String savedUsername = sharedPreferences.getString("username", "");
//        String savedPassword = sharedPreferences.getString("password", "");
//        EtStuNumber.setText(savedUsername);
//        EtStuPwd.setText(savedPassword);
//        cb_RememberMe.setChecked(!savedUsername.isEmpty() && !savedPassword.isEmpty());
//        cb_autoLogin.setChecked(sharedPreferences.getBoolean("auto_login", false));
//        boolean isAutoLoginChecked = sharedPreferences.getBoolean("auto_login", false);
//
//        if (isAutoLoginChecked) {
//            cb_RememberMe.setChecked(true);
//        }
//        // 自动登录，直接调用登录
//        if (cb_autoLogin.isChecked() && !savedUsername.isEmpty() && !savedPassword.isEmpty()) {
//            post();
//        }
//    }

    //用户退出登陆时，清空数据
    public static void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username"); // 仅删除用户名
        editor.remove("password"); // 仅删除密码
        editor.putBoolean("auto_login", false);
        editor.putBoolean("rem_code", false);
        editor.apply();
    }

    // 检查输入是否符合要求
    public boolean CheckInput() {
        String StuNumber = EtStuNumber.getText().toString();
        String StuPwd = EtStuPwd.getText().toString();
        if (StuNumber.trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "学号不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (StuPwd.trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 执行登录请求
    private void post() {
        String StuNumber = EtStuNumber.getText().toString();
        String StuPwd = EtStuPwd.getText().toString();
        Head head = new Head();

        new Thread(() -> {
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/user/login";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", head.getAppId())
                    .add("appSecret", head.getAppSecret())
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("username", StuNumber);
            bodyMap.put("password", StuPwd);
            String body = gson.toJson(bodyMap); // 将Map转换为字符串类型加入请求体中

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            // 请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                // 发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            } catch (NetworkOnMainThreadException ex) {
                ex.printStackTrace();
            }

            // 自动登录选项的处理
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("auto_login", cb_autoLogin.isChecked()); // 保存自动登录状态
            editor.apply();

            // 记住密码逻辑
            if (cb_RememberMe.isChecked()) {
                editor.putString("username", StuNumber);
                editor.putString("password", StuPwd);
            } else {
                editor.remove("username"); // 删除以往保存的账号
                editor.remove("password"); // 删除以往保存的密码
            }
            editor.apply(); // 提交数据
        }).start();
    }


    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            e.printStackTrace(); // TODO: 请求失败的处理
        }

        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            Type jsonType = new TypeToken<JavaPOSTRequest.ResponseBody<Object>>(){}.getType();
            JavaPOSTRequest.ResponseBody<Object> dataResponseBody = gson.fromJson(body, jsonType);
            Log.d("info", dataResponseBody.toString());

            runOnUiThread(() -> {
                if (dataResponseBody.getMsg().equals("登录成功")) {
                    Toast.makeText(LoginActivity.this, "恭喜你登录成功!", Toast.LENGTH_SHORT).show();
                    navigateToMain(dataResponseBody.getData());
                } else {
                    // 其他提示
                    handleLoginError(dataResponseBody.getMsg());
                }
            });
        }
    };

    // 切换密码可见性
    private void togglePasswordVisibility() {
        if (iv_visible.getTag() == null || iv_visible.getTag().equals("0")) {
            iv_visible.setTag("1");
            iv_visible.setImageResource(R.drawable.baseline_visibility_24);
            EtStuPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            iv_visible.setTag("0");
            iv_visible.setImageResource(R.drawable.baseline_visibility_off_24);
            EtStuPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        EtStuPwd.setSelection(EtStuPwd.getText().length()); // 强制光标移动到文本末尾
    }

    // 导航到 MainActivity
    public void navigateToMain(UserData userData) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();
        username = EtStuNumber.getText().toString();
        password = EtStuPwd.getText().toString();
        Integer userId = userData.getId();
        String userHead = userData.getAvatar();
        bundle.putString("password", password);
        bundle.putString("username", username);
        bundle.putInt("userId", userId);
        bundle.putString("userHeadImage", userHead);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    // 处理登录错误提示
    private void handleLoginError(String errorMsg) {
        if (errorMsg.equals("密码错误")) {
            Toast.makeText(LoginActivity.this, "密码错误!", Toast.LENGTH_SHORT).show();
        } else if (errorMsg.equals("当前登录用户不存在")) {
            Toast.makeText(LoginActivity.this, "当前登录用户不存在!", Toast.LENGTH_SHORT).show();
        }
        // 可以在这里增加更多的错误处理逻辑
    }

}