package com.example.new4.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.new4.R;
import com.example.new4.TypeCommodityActivity;
import com.example.new4.bean.AllCommodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TypeCommodityAdapter extends BaseAdapter {



    private LayoutInflater layoutInflater;
    private Context context;
    private List<AllCommodity> commodities = new ArrayList<>();
    HashMap<Integer, View> location = new HashMap<>();

    public TypeCommodityAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);

    }

    public void setData(List<AllCommodity> commodities) {
        this.commodities = commodities;


        notifyDataSetChanged();
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

        ViewHolder holder;
        convertView = layoutInflater.inflate(R.layout.layout_all_commodity, null);
        holder = new ViewHolder(convertView, (AllCommodity) getItem(position), context);
        convertView.setTag(holder);
        return convertView;

    }

    static class ViewHolder {
        ImageView ivCommodity;
        TextView tvTitle, tvPrice;

        public ViewHolder(View itemView, AllCommodity commodity, Context context) {

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
