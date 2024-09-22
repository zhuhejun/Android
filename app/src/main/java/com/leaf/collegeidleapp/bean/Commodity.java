package com.leaf.collegeidleapp.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * 商品实体类
 * @author : autumn_leaf
 */
public class Commodity {

    //编号
    private Integer commodityId;
    //标题
    private String title;
    //类别
    private String category;
    //价格
    private float price;
    //联系方式
    private String phone;
    //商品描述
    private String description;
    //商品图片,以二进制字节存储
    private byte[] picture;
    //用户学号
    private String stuId;

    private Integer cartCount;

    private Integer collectionNum;

    private Integer reviewNum;


    public Integer getCollectionNum() {
        return collectionNum;
    }

    public void setCollectionNum(Integer collectionNum) {
        this.collectionNum = collectionNum;
    }

    public Integer getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(Integer reviewNum) {
        this.reviewNum = reviewNum;
    }

    public Integer getCartCount() {
        return cartCount;
    }

    public void setCartCount(Integer cartCount) {
        this.cartCount = cartCount;
    }



    public Integer getCommodityId() {
        return this.commodityId;
    }

    public void setCommodityId(Integer commodityId) {
        this.commodityId = commodityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getPicture() {
        // 假设picture是原始的大型图片数据，您需要对其进行压缩处理
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);

        // 压缩图片为字节数组
        Bitmap compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth()/2, originalBitmap.getHeight()/2, true); // 将图片缩小为原尺寸的一半
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); // 压缩质量为50%

        return stream.toByteArray();
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }
}
