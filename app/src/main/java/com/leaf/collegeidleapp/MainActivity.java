package com.leaf.collegeidleapp;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 主界面活动类
 * @author: autumn_leaf
 */
public class MainActivity extends AppCompatActivity {

    GridView lvAllCommodity;
    List<Commodity> allCommodities = new ArrayList<>();
    ImageButton ibLearning,ibElectronic,ibDaily,ibSports;
    TextView tv_name;


    CommodityDbHelper dbHelper;
    AllCommodityAdapter adapter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //数据展示栏代码
        //listview对象lvAllCommodity容器，用来展示所有商品
        lvAllCommodity = findViewById(R.id.lv_all_commodity);
        //弄了个管理器dbHelper和适配器adapter
        dbHelper = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
        adapter = new AllCommodityAdapter(getApplicationContext());
        //通过管理器dbHelper给allCommodities赋予数据
        allCommodities = dbHelper.readAllCommodities();
        //使用adapter给allCommodities里的数据存到adapter里
        adapter.setData(allCommodities);
        //抽取adapter里的数据进行展示
        lvAllCommodity.setAdapter(adapter);
        //接受来自loginActivity里的bundle传过来的username用于展示


        final Bundle bundle = this.getIntent().getExtras();
        String stuNum = bundle.getString("username");

        tv_name=findViewById(R.id.tv_userName);
        tv_name.setText(stuNum);

        //发布闲置的按钮的事件处理
        ImageButton IbAddProduct = findViewById(R.id.ib_add_product);
        //跳转到添加物品界面
        IbAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCommodityActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到addCommodityActivity里

                    bundle.putString("user_id", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });


        //进入购物车的按钮的事件处理
        ImageButton IbCartProduct = findViewById(R.id.ib_cart);
        //跳转到购物车
        IbCartProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到cartActivity里
                    bundle.putString("user_id", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });


        //个人中心按钮的事件处理
        ImageButton IbPersonalCenter = findViewById(R.id.ib_personal_center);
        //跳转到个人中心界面
        IbPersonalCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PersonalCenterActivity.class);
                if (bundle != null) {
                    //把学生号打包到bundle里传递到PersonalCenterActivity里
                    bundle.putString("username1", stuNum);
                    intent.putExtras(bundle);
                }
                startActivity(intent);
            }
        });




        //刷新界面按钮事件处理
        ImageView tvRefresh = findViewById(R.id.iv_refresh);
        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommodities = dbHelper.readAllCommodities();
                adapter.setData(allCommodities);
                lvAllCommodity.setAdapter(adapter);
            }
        });


        //为每一个item设置点击事件
        lvAllCommodity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commodity commodity = (Commodity) lvAllCommodity.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position",position);
                bundle1.putByteArray("picture",commodity.getPicture());
                bundle1.putString("title",commodity.getTitle());
                bundle1.putString("description",commodity.getDescription());
                bundle1.putFloat("price",commodity.getPrice());
                bundle1.putString("phone",commodity.getPhone());
                bundle1.putString("stuId",stuNum);
                bundle1.putInt("commodityId",commodity.getCommodityId());
                bundle1.putInt("collectionNum",commodity.getCollectionNum());
                bundle1.putInt("reviewNum",commodity.getReviewNum());
                Intent intent = new Intent(MainActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });



        //点击不同的类别,显示不同的商品信息
        ibLearning = findViewById(R.id.ib_learning_use);
        ibElectronic = findViewById(R.id.ib_electric_product);
        ibDaily = findViewById(R.id.ib_daily_use);
        ibSports = findViewById(R.id.ib_sports_good);
        final Bundle bundle2 = new Bundle();
        //学习用品
        ibLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",1);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //电子用品
        ibElectronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",2);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //生活用品
        ibDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",3);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
        //体育用品
        ibSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle2.putInt("status",4);
                bundle2.putString("stuId",stuNum);
                Intent intent = new Intent(MainActivity.this,CommodityTypeActivity.class);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
    }

    public void loadData(){
        allCommodities = dbHelper.readAllCommodities();
        adapter.setData(allCommodities);
        lvAllCommodity.setAdapter(adapter);
    }


}