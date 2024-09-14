package com.leaf.collegeidleapp;

import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leaf.collegeidleapp.adapter.MyCollectionAdapter;
import com.leaf.collegeidleapp.adapter.MyCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CartDbHelper;
import com.leaf.collegeidleapp.util.CommodityDbHelper;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 我的发布物品Activity类
 */
public class MyCommodityActivity extends AppCompatActivity {

    ListView lvMyCommodity;
    List<Commodity> myCommodities = new ArrayList<>();
    TextView tvStuId;

    CommodityDbHelper dbHelper;

    MyCommodityAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commodity);


        String StuId = this.getIntent().getStringExtra("stu_id");

        //返回按钮
        TextView tvBack = findViewById(R.id.tv_back);
        //点击返回销毁当前界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvStuId = findViewById(R.id.tv_stu_id);
        tvStuId.setText(this.getIntent().getStringExtra("stu_id"));

        //ListView lvMyCommodity是用于展示数据的容器
        lvMyCommodity = findViewById(R.id.lv_my_commodity);
        //创建适配器adapter和管理器dbHelper去获取数据库中的数据再展示到ListView lvMyCommodity里
        adapter = new MyCommodityAdapter(getApplicationContext());
        dbHelper = new CommodityDbHelper(getApplicationContext(),CommodityDbHelper.DB_NAME,null,1);
        //数据集list myCommodities储存dbHelper从数据库调来的数据，此处根据学号进行了筛选
        myCommodities = dbHelper.readMyCommodities(tvStuId.getText().toString());
        //将数据集list myCommodities储存的数据放在adpter里再展示到lvMyCommodity
        adapter.setData(myCommodities);
        lvMyCommodity.setAdapter(adapter);


        //delete点击事件处理
        adapter.setOnItemClickListener(new MyCommodityAdapter.onItemClickListener() {

            @Override
            public void deleteClick(Commodity commodity, int position) {
                //删除我的发布事件处理
                //注意,这里的content不能写getApplicationContent();
                //builder类似一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommodityActivity.this);
                builder.setTitle("提示:").setMessage("确认删除此商品项吗?").setIcon(R.drawable.icon_user)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //根据点击的位置，获取点击位置的item的信息
                                Commodity commodity = (Commodity) adapter.getItem(position);
                                //获取商品名称,商品描述和价格执行删除操作
                                dbHelper.deleteMyCommodity(commodity.getTitle(),commodity.getDescription(),commodity.getPrice());
                                //数据一样,可以直接用,关联删除
                                //dbHelper2.deleteMyCollection(commodity.getTitle(),commodity.getDescription(),commodity.getPrice());
                                Toast.makeText(MyCommodityActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        }).show();
            }
        });


//
//
//        //长按点击事件
//        lvMyCommodity.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                //注意,这里的content不能写getApplicationContent();
//                //builder类似一个对话框
//                AlertDialog.Builder builder = new AlertDialog.Builder(MyCommodityActivity.this);
//                builder.setTitle("提示:").setMessage("确认删除此商品项吗?").setIcon(R.drawable.icon_user)
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        //根据点击的位置，获取点击位置的item的信息
//                        Commodity commodity = (Commodity) adapter.getItem(position);
//                        //获取商品名称,商品描述和价格执行删除操作
//                        dbHelper.deleteMyCommodity(commodity.getTitle(),commodity.getDescription(),commodity.getPrice());
//                        //数据一样,可以直接用,关联删除
//                        //dbHelper2.deleteMyCollection(commodity.getTitle(),commodity.getDescription(),commodity.getPrice());
//                        Toast.makeText(MyCommodityActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
//                    }
//                }).show();
//                return false;
//            }
//        });

        //刷新界面点击事件
        TextView tvRefresh = findViewById(R.id.tv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new CommodityDbHelper(MyCommodityActivity.this,CommodityDbHelper.DB_NAME,null,1);
                adapter = new MyCommodityAdapter(MyCommodityActivity.this);
                myCommodities = dbHelper.readMyCommodities(tvStuId.getText().toString());
                adapter.setData(myCommodities);
                lvMyCommodity.setAdapter(adapter);
            }
        });



        //为每一个item设置点击事件
        lvMyCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Commodity commodity = (Commodity) lvMyCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putByteArray("picture",commodity.getPicture());
                bundle1.putString("title",commodity.getTitle());
                bundle1.putString("description",commodity.getDescription());
                bundle1.putFloat("price",commodity.getPrice());
                bundle1.putString("phone",commodity.getPhone());
                bundle1.putString("stuId",StuId);
                bundle1.putInt("commodityId",commodity.getCommodityId());
                bundle1.putInt("collectionNum",commodity.getCollectionNum());
                bundle1.putInt("reviewNum",commodity.getReviewNum());
                Intent intent = new Intent(MyCommodityActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });


    }


    public void loadData(){
        dbHelper = new CommodityDbHelper(MyCommodityActivity.this,CommodityDbHelper.DB_NAME,null,1);
        adapter = new MyCommodityAdapter(MyCommodityActivity.this);
        myCommodities = dbHelper.readMyCommodities(tvStuId.getText().toString());
        adapter.setData(myCommodities);
        lvMyCommodity.setAdapter(adapter);
    }
}
