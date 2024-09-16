package com.example.new4.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.new4.R;
import com.example.new4.bean.AllCommodity;
import com.example.new4.bean.MyCommodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyCommodityAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<MyCommodity> commodities = new ArrayList<>();

    HashMap<Integer,View> location = new HashMap<>();

    public MyCommodityAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<MyCommodity> commodities) {
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
        ViewHolder holder;
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.layout_my_commodity, null);
            holder = new ViewHolder(convertView,(MyCommodity) getItem(position),context);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }


    static class ViewHolder{
        ImageView ivCommodity;
        TextView tvTitle, tvPrice;

        public ViewHolder(View itemView,MyCommodity commodity,Context context){


            tvTitle = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivCommodity = itemView.findViewById(R.id.iv_commodity);


            String content = commodity.getContent();
            if (content.length() > 7) {
                content = content.substring(0, 7) + "...";
            }
            tvTitle.setText(content);
            tvPrice.setText("¥" + String.valueOf(commodity.getPrice()));

            if (commodity.getImageUrlList() != null && !commodity.getImageUrlList().isEmpty()) {
                Glide.with(context)
                        .load(commodity.getImageUrlList().get(0))
                        .into(ivCommodity);
            } else {
                // 处理无图片数据时的逻辑，比如显示默认图片
                ivCommodity.setImageResource(R.drawable.icon_take_photo);
            }

        }



    }


}
