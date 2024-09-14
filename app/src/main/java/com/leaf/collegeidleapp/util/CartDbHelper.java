package com.leaf.collegeidleapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leaf.collegeidleapp.bean.Cart;
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;

import java.util.ArrayList;
import java.util.List;

public class CartDbHelper extends SQLiteOpenHelper {


    //定义数据库表名
    public static final String DB_NAME = "tb_cart";
    /** 创建收藏信息表 **/
    private static final String CART_DB = "create table tb_cart (" +
            "Id integer primary key ," +
            "commodityId INTEGER UNIQUE,"+
            "stuId text not NUll)";


    private Context context;
    public CartDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CART_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public SQLiteDatabase getSecondDatabaseConnection(Context context) {
        CommodityDbHelper db2Helper = new CommodityDbHelper(context, "tb_cart.db", null, 1);
        return db2Helper.getReadableDatabase();
    }



    /**
     * 添加我的收藏商品
     * @param cart 收藏对象
     */
    public void addMyCart(Cart cart) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("commodityId",cart.getCommodityId());
        values.put("stuId",cart.getStuId());
        db.insert(DB_NAME,null,values);
        values.clear();
    }




    /**
     * 通过学号获取我的收藏商品信息的id
     * @param stuId 学号
     * @return 收藏的所有商品的id
     */
    public List<Cart> readMyCartCommodityId(String stuId) {
        List<Cart> carts = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_cart where stuId=?",new String[]{stuId});
        if(cursor.moveToFirst()) {
            do {
                int commodityIdIndex=cursor.getColumnIndex("commodityId");


                int commodityId = cursor.getInt(commodityIdIndex);

                Cart cart = new Cart();
                cart.setCommodityId(commodityId);
                carts.add(cart);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return carts;
    }




    /**
     * 通过学号获取我的购物车商品信息
     * @param stuId 学号
     * @return 收藏的商品信息
     */



    public List<Commodity> readMyCarts(String stuId) {
        List<Commodity> commoditys = new ArrayList<>();  //这个是要传回去的数据
        SQLiteDatabase db = this.getWritableDatabase();

        CommodityDbHelper commodityDbHelper = new CommodityDbHelper(context, "tb_commodity", null, 1);
        SQLiteDatabase commodityDb = commodityDbHelper.getReadableDatabase();

        if (stuId!=null){
            Cursor cursor = db.rawQuery("select * from tb_cart where stuId=?",new String[]{stuId});if(cursor.moveToFirst()) {
                do {
                    Cursor cursor1 = commodityDb.rawQuery("select * from tb_commodity",null);
                    if (cursor1.moveToFirst()){
                        do {
                            int cartCommodityIdIndex=cursor.getColumnIndex("commodityId");
                            int commodityIdIndex=cursor1.getColumnIndex("commodityId");
                            int titleIndex = cursor1.getColumnIndex("title");
                            int priceIndex = cursor1.getColumnIndex("phone");
                            int phoneIndex = cursor1.getColumnIndex("phone");
                            int descriptionIndex=cursor1.getColumnIndex("description");
                            int pictureIndex = cursor1.getColumnIndex("picture");
                            int cartCountIndex = cursor1.getColumnIndex("cartCount");


                            int collctionCommodityId=cursor.getInt(cartCommodityIdIndex);
                            int commodityId=cursor1.getInt(commodityIdIndex);

                            if (collctionCommodityId==commodityId){

                                int cartCount = cursor1.getInt(cartCountIndex);
                                String title = cursor1.getString(titleIndex);
                                float price = cursor1.getFloat(priceIndex);
                                String phone = cursor1.getString(phoneIndex);
                                String description = cursor1.getString(descriptionIndex);
                                byte[] picture = cursor1.getBlob(pictureIndex);
                                Commodity commodity1 = new Commodity();
                                commodity1.setPicture(picture);
                                commodity1.setTitle(title);
                                commodity1.setDescription(description);
                                commodity1.setCommodityId(commodityId);
                                commodity1.setPrice(price);
                                commodity1.setPhone(phone);
                                commodity1.setCartCount(cartCount);

                                commoditys.add(commodity1);
                                break;
                            }
                        }while (cursor1.moveToNext());
                    }
                    cursor1.close();
                }while (cursor.moveToNext());
            }
            cursor.close();
        }else {
            throw new IllegalArgumentException("Invalid stuId value: Cannot be NULL");
        }
        return commoditys;
    }



}
