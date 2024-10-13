package com.example.new4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.new4.api.JavaPOSTRequest;
import com.example.new4.bean.UserData;

public class SplashActivity extends AppCompatActivity {

    private String username ,password;
    public static UserData response;
    public static SharedPreferences sharedPreferences; // 声明 SharedPreferences
    public int userId=0;
    public String userHead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 设置您的启动页面布局
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        username=sharedPreferences.getString("username",null);
        password=sharedPreferences.getString("password",null);

        // 使用 Handler 来延迟启动 LoginActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (username!=null && password!=null&&sharedPreferences.getBoolean("auto_login",false)){
                    new Thread(()->{
                        // 调用 LoginPost 方法
                        response = JavaPOSTRequest.LoginPost(username, password);

                        runOnUiThread(()->{
                            userId = response.getId();
                            userHead=response.getAvatar();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("password", password);
                            bundle.putString("username", username);
                            bundle.putInt("userId", userId);
                            bundle.putString("userHeadImage", userHead);
                            intent.putExtras(bundle);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        });

                    }).start();



                }
                else {
                    // 启动 LoginActivity
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }


            }
        }, 500); // 延迟0.5秒

    }

    //用户退出登陆时，清空数据


}