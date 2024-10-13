package com.example.new4.bean;

import java.util.List;

public class AllCommodity {
    private Integer id;              // 商品ID
    private String appKey;          // 应用Key
    private String tUserId;         // 用户ID
    private String imageCode;       // 图片编号
    private String content;         // 内容
    private int price;              // 价格
    private String addr;            // 地址
    private int typeId;             // 类型ID
    private String typeName;        // 类型名称
    private int status;             // 状态
    private String createTime;      // 创建时间
    private String username;        // 用户名
    private String avatar;          // 头像
    private List<String> imageUrlList; // 图片URL列表
    private int appIsShare;         // 应用分享状态
    private String tuserId;         // 用户ID


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String gettUserId() {
        return tUserId;
    }

    public void settUserId(String tUserId) {
        this.tUserId = tUserId;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setImageUrlList(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public int getAppIsShare() {
        return appIsShare;
    }

    public void setAppIsShare(int appIsShare) {
        this.appIsShare = appIsShare;
    }

    public String getTuserId() {
        return tuserId;
    }

    public void setTuserId(String tuserId) {
        this.tuserId = tuserId;
    }
}
