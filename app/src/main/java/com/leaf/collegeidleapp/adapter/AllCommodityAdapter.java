package com.leaf.collegeidleapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.leaf.collegeidleapp.R;
import com.leaf.collegeidleapp.bean.Commodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 主界面所有商品列表的适配器
 * @author autumn_leaf
 */
public class AllCommodityAdapter extends BaseAdapter {

    private Context context;  //上下文
    private LayoutInflater layoutInflater;  //渲染器

    private List<Commodity> commodities = new ArrayList<>();
    //对每一个item保存其位置
    HashMap<Integer,View> location = new HashMap<>();

    public AllCommodityAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Commodity> commodities) {
        this.commodities = commodities;
        notifyDataSetChanged();  //通知适配器数据已经改变，需要刷新界面以反映最新的商品数据。
    }

    @Override
    public int getCount() {
        return commodities.size();
    }

    @Override
    public Object getItem(int position) {
        return commodities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        //获得一个新的视图位置
        convertView = layoutInflater.inflate(R.layout.layout_all_commodity1, null);
        //获得一个商品数据
        Commodity commodity = (Commodity) getItem(position);
        //将商品数据传到ViewHolder里进行渲染
        holder = new ViewHolder(convertView, commodity);
        //保存view的位置position
        location.put(position, convertView);
        convertView.setTag(holder);

        return convertView;
        
    }

    //定义静态类,包含每一个item的所有元素
    static class ViewHolder {
        ImageView ivCommodity;
        TextView tvTitle,tvPrice,tv_collectionNum,tv_reviewNum;

        public ViewHolder(View itemView,Commodity commodity) {
            //绑定视图
            tvTitle = itemView.findViewById(R.id.tv_name);

            tvPrice = itemView.findViewById(R.id.tv_price);

            ivCommodity = itemView.findViewById(R.id.iv_commodity);
            tv_collectionNum=itemView.findViewById(R.id.tv_colectionNum);
            tv_reviewNum=itemView.findViewById(R.id.tv_reviewNum);

            //给视图赋值
            tvTitle.setText(commodity.getTitle());
            tvPrice.setText("¥"+String.valueOf(commodity.getPrice()));

            tv_collectionNum.setText(commodity.getCollectionNum()+"");
            tv_reviewNum.setText(commodity.getReviewNum()+"");



            byte[] picture = commodity.getPicture();
            //从字节数组中解码生成不可变的位图
            //public static Bitmap decodeByteArray(byte[] data, int offset, int length)
            Bitmap img = BitmapFactory.decodeByteArray(picture,0,picture.length);
            ivCommodity.setImageBitmap(img);
        }
    }
}
