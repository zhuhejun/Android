package com.leaf.collegeidleapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leaf.collegeidleapp.bean.Review;

import java.util.LinkedList;

/**
 * 评论/留言数据库连接类
 * @author autumn_leaf
 */
public class ReviewDbHelper extends SQLiteOpenHelper {

    //定义数据库表名
    public static final String DB_NAME = "tb_review";
    /** 创建评论信息表 **/
    private static final String CREATE_REVIEW_DB = "create table tb_review (" +
            "id integer primary key autoincrement," +
            "stuId text," +
            "currentTime text," +
            "commodityId integer,"+
            "content text," +
            "position integer )";

    public ReviewDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REVIEW_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 添加评论
     * @param review 评论对象
     */
    public void addReview(Review review) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stuId",review.getStuId());
        values.put("currentTime",review.getCurrentTime());
        values.put("content",review.getContent());
        values.put("position",review.getPosition());
        values.put("commodityId",review.getCommodityId());
        db.insert(DB_NAME,null,values);
        values.clear();
    }

    /**
     * 根据商品项编号读取相应的评论信息
     * @return 评论对象数组
     */
    public LinkedList<Review> readReviews(int  commodityId) {
        LinkedList<Review> reviews = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from tb_review where commodityId=?",new String[]{String.valueOf(commodityId)});
        int stuIdIndex, currentTimeIndex, contentIndex;

        if (cursor.moveToFirst()) {
            // 获取列索引
            stuIdIndex = cursor.getColumnIndex("stuId");
            currentTimeIndex = cursor.getColumnIndex("currentTime");
            contentIndex = cursor.getColumnIndex("content");

            do {
                if (stuIdIndex >= 0 && currentTimeIndex >= 0 && contentIndex >= 0) {
                    String stuId = cursor.getString(stuIdIndex);
                    String currentTime = cursor.getString(currentTimeIndex);
                    String content = cursor.getString(contentIndex);

                    Review review = new Review();
                    review.setStuId(stuId);
                    review.setCurrentTime(currentTime);
                    review.setContent(content);

                    reviews.add(review);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reviews;
    }

}
