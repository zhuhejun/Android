package com.leaf.collegeidleapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;



/**
 * 不同类型商品信息的活动类
 * @author autumn_leaf
 */
public class CommodityTypeActivity extends AppCompatActivity {

    TextView tvCommodityType;
    GridView lvCommodityType;  //用于展示商品数据的容器
    List<Commodity> commodities = new LinkedList<>();  //商品数据

    CommodityDbHelper dbHelper;
    AllCommodityAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        final Bundle bundle = this.getIntent().getExtras();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_type);
        //返回按钮事件处理
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tvCommodityType = findViewById(R.id.tv_type);  //TextView **用品框绑定
        lvCommodityType = findViewById(R.id.list_commodity);  //商品栏ListView绑定
        //创建管理器dbHelper和适配器adapter用于获取数据后展示在ListView lvCommodityType里
        dbHelper = new CommodityDbHelper(getApplicationContext(),CommodityDbHelper.DB_NAME,null,1);
        adapter = new AllCommodityAdapter(getApplicationContext());

        //根据不同的状态显示不同的界面
        int status = this.getIntent().getIntExtra("status",0); //获取从MainActivity里传过来bundle2里的status的值
        if(status == 1) {
            tvCommodityType.setText("学习用品");
        }else if(status == 2) {
            tvCommodityType.setText("电子用品");
        }else if(status == 3) {
            tvCommodityType.setText("生活用品");
        }else if(status == 4) {
            tvCommodityType.setText("体育用品");
        }
        //根据不同类别显示不同的商品信息//获取通过管理器dbHelper的方法readCommodityType获取商品数据放到 List<Commodity> commodities里
        commodities = dbHelper.readCommodityType(tvCommodityType.getText().toString());
        adapter.setData(commodities);
        lvCommodityType.setAdapter(adapter);//在ListView lvCommodityType中展示商品



        //为每一个item设置点击事件
        lvCommodityType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                String stuNum = bundle.getString("stuId");

                Commodity commodity = (Commodity) lvCommodityType.getAdapter().getItem(position);
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
                Intent intent = new Intent(CommodityTypeActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });





    }






}
