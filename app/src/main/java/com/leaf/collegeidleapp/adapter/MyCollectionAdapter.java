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
import com.leaf.collegeidleapp.bean.Collection;
import com.leaf.collegeidleapp.bean.Commodity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 我的收藏适配器Adapter类
 * @author autumn_leaf
 */
public class MyCollectionAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private List<Commodity> commodities = new ArrayList<>();

    HashMap<Integer,View> location = new HashMap<>();

    public MyCollectionAdapter(Context context) {
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
            convertView = layoutInflater.inflate(R.layout.layout_my_collection1,null);
            Commodity collection = (Commodity) getItem(position);
            holder = new ViewHolder(convertView,collection,position);
            location.put(position,convertView);
            convertView.setTag(holder);
        }else{
            convertView = location.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    //定义静态类,包含每一个item的所有元素
    static class ViewHolder {
        ImageView ivCommodity,ivDelete;
        TextView tvTitle,tvDescription,tvPrice;
        int position;

        public ViewHolder(View itemView,Commodity commodity,int position) {
            this.position=position;
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
                public void onClick(View view) {
                    if(onItemClickListener!=null){
                        onItemClickListener.deleteClick(commodity,position);
                    }
                }
            });


        }




    }


    //创造点击事件对象
    public interface  onItemClickListener{
        void deleteClick(Commodity commodity , int position);  //删除的点击事件
    }


    private static MyCollectionAdapter.onItemClickListener onItemClickListener;
    public static MyCollectionAdapter.onItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public static void setOnItemClickListener(MyCollectionAdapter.onItemClickListener onItemClickListener) {
        MyCollectionAdapter.onItemClickListener = onItemClickListener;
    }


}
