package com.example.new4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.new4.R;
import com.example.new4.bean.MyBuyCommodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyBuyCommodityAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<MyBuyCommodity> commodities = new ArrayList<>();

    HashMap<Integer,View> location = new HashMap<>();

    public MyBuyCommodityAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<MyBuyCommodity> commodities) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        // 获取当前商品对象
        MyBuyCommodity commodity = (MyBuyCommodity) getItem(position);


        ViewHolder holder;
        convertView = layoutInflater.inflate(R.layout.layout_mybuy_commodity, null);
        holder = new ViewHolder(convertView,(MyBuyCommodity) getItem(position),context);
        convertView.setTag(holder);

        // 加载商品图片
        if (!commodity.getImageUrlList().isEmpty()) {
            Glide.with(context)
                    .load(commodity.getImageUrlList().get(0))
                    .into(holder.ivCommodity);
        } else {
            holder.ivCommodity.setImageResource(R.drawable.icon_take_photo);
        }
        return convertView;
    }


    static class ViewHolder{
        ImageView tuserHead,ivCommodity;
        TextView tvTitle, tvPrice,tuserName;

        public ViewHolder(View itemView, MyBuyCommodity commodity, Context context){


            tvTitle = itemView.findViewById(R.id.tv_content);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tuserHead = itemView.findViewById(R.id.iv_tuserhead);
            tuserName=itemView.findViewById(R.id.tv_tusername);
            ivCommodity = itemView.findViewById(R.id.iv_commodity);


            String content = commodity.getGoodsDescription();
            if (content.length() > 7) {
                content = content.substring(0, 7) + "...";
            }
            tvTitle.setText(content);
            tvPrice.setText("¥" + String.valueOf(commodity.getPrice()));
            tuserName.setText(commodity.getSellerName());



            if (commodity.getSellerAvatar() != null && !commodity.getSellerAvatar().isEmpty()) {
                Glide.with(context)
                        .load(commodity.getSellerAvatar())
                        .into(tuserHead);
            } else {
                // 处理无图片数据时的逻辑，比如显示默认图片
                tuserHead.setImageResource(R.drawable.icon_take_photo);
            }

//            if (commodity.getImageUrlList() != null && !commodity.getImageUrlList().isEmpty()) {
//                Glide.with(context)
//                        .load(commodity.getImageUrlList().get(0))
//                        .into(ivCommodity);
//            } else {
//                // 处理无图片数据时的逻辑，比如显示默认图片
//                ivCommodity.setImageResource(R.drawable.icon_take_photo);
//            }
//


        }



    }


}
