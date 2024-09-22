package com.leaf.collegeidleapp.bean;

public class Head {
    private String appId ;
    private String appSecret;

    public Head() {
        this.appId = "dae70c305a9b430cb7b5ef0f1ee89c96";
        this.appSecret = "1792531b4a5b20c1c44f6b9347eb324bea377";
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
