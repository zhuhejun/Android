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

public class CartAdapter extends BaseAdapter {

    HashMap<Integer,View> location = new HashMap<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private List<Commodity> commodities = new ArrayList<>();



    
    
    
    
    

    public CartAdapter(Context context) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        CartAdapter.ViewHolder holder = null;
            convertView = layoutInflater.inflate(R.layout.layout_cart,null);
            Commodity commodity = (Commodity) getItem(position);
            holder =new ViewHolder(convertView,commodity,position);
            location.put(position,convertView);
            convertView.setTag(holder);

        return convertView;
    }

    //定义静态类,包含每一个item的所有元素
    static class ViewHolder {
        ImageView ivCommodity;
        TextView tvTitle,tvPrice;
        TextView btnPlus ;
        TextView btnMinus ;
        TextView itemNum ;
        TextView itemSum ;
        int position;

        public ViewHolder(View itemView, Commodity commodity, int position) {
            this.position = position;
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_ItemPrice);
            ivCommodity = itemView.findViewById(R.id.iv_picture);

            //为+和-设置绑定视图
            btnPlus = itemView.findViewById(R.id.plusCartBtn);
            btnMinus = itemView.findViewById(R.id.minusCartItem);
            itemNum = itemView.findViewById(R.id.numberItemTxt);
            itemSum = itemView.findViewById(R.id.totalEachItemPrice);



            itemNum.setText(commodity.getCartCount() + "");

            itemSum.setText("¥"+commodity.getCartCount() * commodity.getPrice() + "");
            itemNum.setText(String.valueOf(commodity.getCartCount()));
            tvTitle.setText(commodity.getTitle());
            tvPrice.setText("¥" + String.valueOf(commodity.getPrice()));
            byte[] picture = commodity.getPicture();
            Bitmap img = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            ivCommodity.setImageBitmap(img);


            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onPlusClick(commodity,position);

                    }

                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(onItemClickListener!=null){
                        onItemClickListener.onSubTractClick(commodity,position);

                    }

                }
            });


        }


    }



    private static onItemClickListener onItemClickListener;

    public CartAdapter.onItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(CartAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }




    public interface  onItemClickListener{
        void onPlusClick(Commodity commodity , int position);
        void onSubTractClick(Commodity commodity,int position);

    }





}
