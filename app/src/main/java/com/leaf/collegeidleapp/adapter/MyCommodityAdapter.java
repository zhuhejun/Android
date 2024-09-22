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
import java.util.LinkedList;
import java.util.List;

/**
 * 所有物品的适配器Adapter类
 * @author autumn_leaf
 */
public class MyCommodityAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<Commodity> commodities = new ArrayList<>();

    HashMap<Integer,View> location = new HashMap<>();

    public MyCommodityAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Commodity> commodities) {
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
        ViewHolder holder = null;
        if(location.get(position) == null){
            convertView = layoutInflater.inflate(R.layout.layout_my_commodity1,null);
            Commodity commodity = (Commodity) getItem(position);
            holder = new ViewHolder(convertView,commodity,position);
            location.put(position,convertView);
            convertView.setTag(holder);
        }else{
            convertView = location.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    //定义静态类,包含每一个item的所有元素
    static class ViewHolder {  //包住 public ViewHolder是为了使得ViewHolder仅被本适配器使用
        ImageView ivCommodity,ivDelete;
        TextView tvTitle,tvDescription,tvPrice;
        int position;

        public ViewHolder(View itemView,Commodity commodity,int position) {
            this.position = position;
            tvTitle = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDescription = itemView.findViewById(R.id.tv_description);
            ivCommodity = itemView.findViewById(R.id.iv_commodity);
            ivDelete=itemView.findViewById(R.id.iv_delete);
            tvTitle.setText(commodity.getTitle());
            tvDescription.setText(commodity.getDescription());
            tvPrice.setText("¥"+String.valueOf(commodity.getPrice()));
            byte[] picture = commodity.getPicture();
            Bitmap img = BitmapFactory.decodeByteArray(picture,0,picture.length);
            ivCommodity.setImageBitmap(img);

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.deleteClick(commodity,position);
                    }

                }
            });


        }




    }


    //创造点击事件对象
    public interface  onItemClickListener{
        void deleteClick(Commodity commodity , int position);
    }


    private static MyCommodityAdapter.onItemClickListener onItemClickListener;
    public static MyCommodityAdapter.onItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public static void setOnItemClickListener(MyCommodityAdapter.onItemClickListener onItemClickListener) {
        MyCommodityAdapter.onItemClickListener = onItemClickListener;
    }


}
