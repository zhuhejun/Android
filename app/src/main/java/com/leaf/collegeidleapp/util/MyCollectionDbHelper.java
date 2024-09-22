package com.leaf.collegeidleapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leaf.collegeidleapp.bean.Commodity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 我的收藏数据库连接类
 * @author autumn_leaf
 */
public class MyCollectionDbHelper extends SQLiteOpenHelper {



    //定义数据库表名
    public static final String DB_NAME = "tb_collection";
    /** 创建收藏信息表 **/
    private static final String CREATE_COLLECTION_DB = "create table tb_collection (" +
            "Id integer primary key ," +
            "commodityId integer,"+
            "stuId text)";
    private Context context;
    public MyCollectionDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COLLECTION_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public SQLiteDatabase getSecondDatabaseConnection(Context context) {
        CommodityDbHelper db2Helper = new CommodityDbHelper(context, "tb_commodity.db", null, 1);
        return db2Helper.getReadableDatabase();
    }




    /**
     * 添加我的收藏商品
     * @param collection 收藏对象
     */
    public void addMyCollection(Collection collection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("commodityId",collection.getCommodityId());
        values.put("stuId",collection.getStuId());
        db.insert(DB_NAME,null,values);
        values.clear();
    }

    /**
     * 通过学号获取我的收藏商品信息
     * @param stuId 学号
     * @return 收藏的商品信息
     */



    public List<Commodity> readMyCollections(String stuId) {
        List<Commodity> commoditys = new ArrayList<>();  //这个是要传回去的数据
        SQLiteDatabase db = this.getWritableDatabase();

        CommodityDbHelper commodityDbHelper = new CommodityDbHelper(context, "tb_commodity", null, 1);
        SQLiteDatabase commodityDb = commodityDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from tb_collection where stuId=?",new String[]{stuId});
        if(cursor.moveToFirst()) {
            do {
                Cursor cursor1 = commodityDb.rawQuery("select * from tb_commodity",null);
                if (cursor1.moveToFirst()){
                    do {
                        int collectionCommodityIdIndex=cursor.getColumnIndex("commodityId");
                        int commodityIdIndex=cursor1.getColumnIndex("commodityId");

                        int categoryIndex = cursor1.getColumnIndex("category");
                        int titleIndex = cursor1.getColumnIndex("title");
                        int priceIndex = cursor1.getColumnIndex("price");
                        int phoneIndex = cursor1.getColumnIndex("phone");
                        int descriptionIndex=cursor1.getColumnIndex("description");
                        int pictureIndex = cursor1.getColumnIndex("picture");
                        int collectionNumIndex=cursor1.getColumnIndex("collectionNum");
                        int reviewNumIndex=cursor1.getColumnIndex("reviewNum");



                        int collectionCommodityId=cursor.getInt(collectionCommodityIdIndex);
                        int commodityId=cursor1.getInt(commodityIdIndex);
                        int collectionNum=cursor1.getInt(collectionNumIndex);
                        int reviewNum=cursor1.getInt(reviewNumIndex);

                        if (collectionCommodityId==commodityId){

                            String category = cursor1.getString(categoryIndex);
                            String title = cursor1.getString(titleIndex);
                            float price = cursor1.getFloat(priceIndex);
                            String phone = cursor1.getString(phoneIndex);
                            String description = cursor1.getString(descriptionIndex);
                            byte[] picture = cursor1.getBlob(pictureIndex);
                            Commodity commodity1 = new Commodity();
                            commodity1.setPicture(picture);
                            commodity1.setTitle(title);
                            commodity1.setCategory(category);
                            commodity1.setDescription(description);
                            commodity1.setPrice(price);
                            commodity1.setPhone(phone);
                            commodity1.setCommodityId(commodityId);
                            commodity1.setCollectionNum(collectionNum);
                            commodity1.setReviewNum(reviewNum);

                            commoditys.add(commodity1);
                            break;
                        }
                    }while (cursor1.moveToNext());
                }
                cursor1.close();
            }while (cursor.moveToNext());
        }
        cursor.close();
        return commoditys;
    }
    /**
     * 通过学号获取我的收藏商品信息的id
     * @param stuId 学号
     * @return 收藏的所有商品的id
     */
    public List<Collection> readMyCollectionsCommodityId(String stuId) {
        List<Collection> collections = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_collection where stuId=?",new String[]{stuId});
        if(cursor.moveToFirst()) {
            do {
                int commodityIdIndex=cursor.getColumnIndex("commodityId");


                int commodityId = cursor.getInt(commodityIdIndex);
                Collection collection = new Collection();
                collection.setCommodityId(commodityId);
                collections.add(collection);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return collections;
    }

    /**
     * 删除收藏的商品项
     * @param commodityId 商品单号
     */
    public void deleteMyCollection(int commodityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db.isOpen()) {
            db.delete(DB_NAME,"commodityId=?",new String[]{String.valueOf(commodityId)});
            db.close();
        }
    }

}
