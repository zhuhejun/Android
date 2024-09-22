package com.leaf.collegeidleapp.util;

import android.util.Log;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpService {

    private OkHttpClient client = new OkHttpClient();

    public void login(String username, String password,String url) {
        // 构建请求体
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Headers headers = new Headers.Builder()
                .add("appId", "dae70c305a9b430cb7b5ef0f1ee89c96")
                .add("appSecret","1792531b4a5b20c1c44f6b9347eb324bea377")
                .build();

        // 创建请求
        Request request = new Request.Builder()
                .url(url) // 替换为您的登录接口地址
                .headers(headers)
                .post(formBody)
                .build();

        // 发起请求
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("LoginiResult",responseBody);
                } else {
                    Log.d("failure","login");
                }
            }
        });
    }
}
