package com.leaf.collegeidleapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.leaf.collegeidleapp.R;
import com.leaf.collegeidleapp.bean.AllCommodity1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllCommodityAdapter1 extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<AllCommodity1> commodities = new ArrayList<>();
    HashMap<Integer, View> location = new HashMap<>();

    public AllCommodityAdapter1(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<AllCommodity1> commodities) {
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

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_all_commodity1, null);
            holder = new ViewHolder(convertView, (AllCommodity1) getItem(position), context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivCommodity;
        TextView tvTitle, tvPrice, tv_collectionNum, tv_reviewNum;

        public ViewHolder(View itemView, AllCommodity1 commodity, Context context) {
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            ivCommodity = itemView.findViewById(R.id.iv_commodity);

            tv_reviewNum = itemView.findViewById(R.id.tv_reviewNum);

            tvTitle.setText(commodity.getContent());
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