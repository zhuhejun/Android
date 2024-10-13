package com.example.new4.api;

import androidx.annotation.NonNull;

import com.example.new4.bean.Head;
import com.example.new4.bean.UserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 提示: 需要添加httpcomponents中相关jar包，或者依赖
 *       https://mvnrepository.com/artifact/org.apache.httpcomponents
 */
public class JavaPOSTRequest {
    private final static Gson gson = new Gson();
    /**
     * 发送 POST 请求
     * @return 返回响应字符串
     */
    public static UserData LoginPost(String username, String password){
        OkHttpClient client = new OkHttpClient();

        Head head=new Head();
        // 用户修改请求路径 ↓↓↓↓↓↓↓↓↓↓↓
        String url = "https://api-store.openguet.cn/api/member/tran/user/login";

        // 请求头
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("appId", head.getAppId())
                .addHeader("appSecret", head.getAppSecret())
                .addHeader("Accept", "application/json, text/plain, */*")
                .post(createRequestBody(username, password));

        Request request = requestBuilder.build();

        String resp = null;
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            resp = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO 用户异常处理
        }

        Type jsonType = new TypeToken<ResponseBody<Object>>() {}.getType();
        JavaPOSTRequest.ResponseBody<Object> dataResponseBody = gson.fromJson(resp, jsonType);

        return dataResponseBody.data;
    }

    private static RequestBody createRequestBody(String username, String password) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("username", username);
        bodyMap.put("password", password);
        String json = gson.toJson(bodyMap); // Convert map to JSON string
        return RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
    }
    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {
        private int code;
        private String msg;
        private UserData data;

        public ResponseBody() {}

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public UserData getData() {
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