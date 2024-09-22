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
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CommodityDbHelper;
import com.leaf.collegeidleapp.util.MyCollectionDbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 我的收藏Activity类
 * @author autumn_leaf
 */
public class MyCollectionActivity extends AppCompatActivity {

    ListView lvMyCollection;
    List<Commodity> myCollections = new ArrayList<>();
    TextView tvStuId;
    private static final Logger LOGGER = Logger.getLogger(MyCollectionActivity.class.getName());

    MyCollectionDbHelper dbHelper;
    MyCollectionAdapter adapter;
    CommodityDbHelper dbHelper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String StuId = this.getIntent().getStringExtra("stuId");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);
        //返回
        TextView tvBack = findViewById(R.id.tv_back);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvStuId = findViewById(R.id.tv_stuId);
        tvStuId.setText(this.getIntent().getStringExtra("stuId"));

        lvMyCollection = findViewById(R.id.lv_my_collection);
        dbHelper = new MyCollectionDbHelper(getApplicationContext(),MyCollectionDbHelper.DB_NAME,null,1);

        dbHelper2 = new CommodityDbHelper(getApplicationContext(),CommodityDbHelper.DB_NAME,null,1);

        myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
        adapter = new MyCollectionAdapter(getApplicationContext());
        adapter.setData(myCollections);
        lvMyCollection.setAdapter(adapter);


        //为每一个item设置点击事件
        lvMyCollection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Commodity commodity = (Commodity) lvMyCollection.getAdapter().getItem(position);
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
                Intent intent = new Intent(MyCollectionActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });

        adapter.setOnItemClickListener(new MyCollectionAdapter.onItemClickListener() {
            @Override
            public void deleteClick(Commodity commodity, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyCollectionActivity.this);
                builder.setTitle("提示:").setMessage("确定删除此收藏商品吗?").setIcon(R.drawable.icon_user).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Commodity collection1 = (Commodity) adapter.getItem(position);
                        //删除收藏商品项
                        dbHelper.deleteMyCollection(collection1.getCommodityId());
                        dbHelper2.upCollectionNum2(commodity);

                        Toast.makeText(MyCollectionActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();



                        loadData();
                    }
                }).show();

            }
        });




//
////        设置长按删除事件
//        lvMyCollection.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MyCollectionActivity.this);
//                builder.setTitle("提示:").setMessage("确定删除此收藏商品吗?").setIcon(R.drawable.icon_user).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Commodity collection1 = (Commodity) adapter.getItem(position);
//                        //删除收藏商品项
//                        dbHelper.deleteMyCollection(collection1.getCommodityId());
//
//
//                        Toast.makeText(MyCollectionActivity.this,"删除成功!",Toast.LENGTH_SHORT).show();
//                        loadData();
//                    }
//                }).show();
//                return false;
//            }
//        });
//
//


//
//        //页面刷新
//        TextView tvRefresh = findViewById(R.id.tv_refresh);
//        tvRefresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
//                adapter.setData(myCollections);
//                lvMyCollection.setAdapter(adapter);
//            }
//        });
//



    }
    public void loadData(){
        myCollections = dbHelper.readMyCollections(tvStuId.getText().toString());
        adapter.setData(myCollections);
        lvMyCollection.setAdapter(adapter);
    }
}
