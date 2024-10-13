package com.example.new4.api;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.example.new4.bean.Head;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ReadChat extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static void read(int chatId){
        new Thread(() -> {
            Head head = new Head();
            Gson gson= new Gson();
            // url路径
            String url = "https://api-store.openguet.cn/api/member/tran/chat/change?chatId="+chatId;

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
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, Response response) throws IOException {
                        // 处理结果
                        //TODO 请求成功处理
                        Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
                        // 获取响应体的json串
                        String body = response.body().string();
                        Log.d("info", body);
                        // 解析json串到自己封装的状态
                        ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
                        Log.d("info", dataResponseBody.toString());
                    }
                });
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

//    /**
//     * 回调
//     */
//    public final Callback callback = new Callback() {
//        @Override
//        public void onFailure(@NonNull Call call, IOException e) {
//            //TODO 请求失败处理
//            e.printStackTrace();
//        }
//        @Override
//        public void onResponse(@NonNull Call call, Response response) throws IOException {
//            //TODO 请求成功处理
//            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
//            // 获取响应体的json串
//            String body = response.body().string();
//            Log.d("info", body);
//            // 解析json串到自己封装的状态
//            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
//            Log.d("info", dataResponseBody.toString());
//        }
//    };

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