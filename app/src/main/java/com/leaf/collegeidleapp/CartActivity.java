package com.leaf.collegeidleapp;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.leaf.collegeidleapp.adapter.AllCommodityAdapter;
import com.leaf.collegeidleapp.adapter.CartAdapter;
import com.leaf.collegeidleapp.bean.Cart;
import com.leaf.collegeidleapp.bean.Commodity;
import com.leaf.collegeidleapp.util.CartDbHelper;
import com.leaf.collegeidleapp.util.CommodityDbHelper;

import java.util.ArrayList;
import java.util.List;

public class CartActivity  extends AppCompatActivity {

    ListView lvMyCart;
    List<Commodity> MyCartCommodities = new ArrayList<>();

    CartDbHelper dbHelper;
    CartAdapter adapter;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        String stuId = this.getIntent().getStringExtra("user_id");

        //数据展示栏代码
        //listview对象lvAllCommodity容器，用来展示所有商品
        lvMyCart = findViewById(R.id.lv_cartList);
        //弄了个管理器dbHelper和适配器adapter
        dbHelper = new CartDbHelper(getApplicationContext(), CartDbHelper.DB_NAME, null, 1);
        adapter = new CartAdapter(getApplicationContext());
        //通过管理器dbHelper给allCommodities赋予数据
        MyCartCommodities = dbHelper.readMyCarts(stuId);
        //使用adapter给allCommodities里的数据存到adapter里
        adapter.setData(MyCartCommodities);
        //抽取adapter里的数据进行展示
        lvMyCart.setAdapter(adapter);
        //接受来自loginActivity里的bundle传过来的username用于展示
        final Bundle bundle = this.getIntent().getExtras();
        String stuNum = bundle.getString("username");
        TextView totalTxt = findViewById(R.id.totalTxt);
        setTotalTxt(totalTxt);



        //为每一个item设置点击事件
        lvMyCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commodity commodity = (Commodity) lvMyCart.getAdapter().getItem(position);
                Bundle bundle1 = new Bundle();
                bundle1.putInt("position", position);
                bundle1.putByteArray("picture", commodity.getPicture());
                bundle1.putString("title", commodity.getTitle());
                bundle1.putString("description", commodity.getDescription());
                bundle1.putFloat("price", commodity.getPrice());
                bundle1.putString("phone", commodity.getPhone());
                bundle1.putString("stuId", stuNum);
                bundle1.putInt("commodityId", commodity.getCommodityId());
                Intent intent = new Intent(CartActivity.this, ReviewCommodityActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });


        adapter.setOnItemClickListener(new CartAdapter.onItemClickListener() {
            @Override
            public void onPlusClick(Commodity commodity , int position) {
                //加

                CommodityDbHelper db3 = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
                db3.upCartCount1(stuNum,commodity);
                db3.close();
                loadData();
                setTotalTxt(totalTxt);
            }

            @Override
            public void onSubTractClick(Commodity commodity,int position) {
                //减
                CommodityDbHelper db3 = new CommodityDbHelper(getApplicationContext(), CommodityDbHelper.DB_NAME, null, 1);
                db3.upCartCount2(stuNum,commodity);
                db3.close();
                loadData();
                setTotalTxt(totalTxt);
            }


        });







    }
    public void setTotalTxt(TextView totalTxt) {

        final Bundle bundle1 = this.getIntent().getExtras();
        String stuNum1 = bundle1.getString("username");
        List<Commodity> cartTotal;
        cartTotal=dbHelper.readMyCarts(stuNum1);
        float totalPrice=0;
        for(int i =0;i< cartTotal.size();i++){
            totalPrice=totalPrice+cartTotal.get(i).getPrice()*cartTotal.get(i).getCartCount();
        }

        totalTxt.setText("¥"+totalPrice+"0");

    }
    public void loadData(){
        final Bundle bundle = this.getIntent().getExtras();
        String stuNum = bundle.getString("username");
        CartDbHelper db2= new CartDbHelper(getApplicationContext(), CartDbHelper.DB_NAME, null, 1);
        List<Commodity> cart2 = new ArrayList<>();
        cart2 = db2.readMyCarts(stuNum);
        adapter.setData(cart2);


    }

}
