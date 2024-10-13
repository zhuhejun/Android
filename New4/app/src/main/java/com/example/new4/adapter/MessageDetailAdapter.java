package com.example.new4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.new4.ChatDetailActivity;
import com.example.new4.R;
import com.example.new4.bean.MyBuyCommodity;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflater;
    private List<ChatDetailActivity.messageRecord> message = new ArrayList<>();
    String tUserHead,UserHead;
    int userId;

    public MessageDetailAdapter (Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<ChatDetailActivity.messageRecord> message,String tUserHead,String UserHead,int userId) {
        this.message = message;
        this.tUserHead=tUserHead;
        this.UserHead=UserHead;
        this.userId=userId;
        notifyDataSetChanged();  //通知适配器数据已经改变，需要刷新界面以反映最新的商品数据。
    }



    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Object getItem(int position) {
        return message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ChatDetailActivity.messageRecord msg = (ChatDetailActivity.messageRecord) getItem(position);

//        MessageDetailAdapter.ViewHolder1 holder;
//        convertView = layoutInflater.inflate(R.layout.layout_tdialoguemassege, null);
//        holder = new MessageDetailAdapter.ViewHolder1(convertView,(ChatDetailActivity.messageRecord) getItem(position),context,tUserHead);
//        convertView.setTag(holder);

        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (msg.getFromUserId().equals(String.valueOf(userId))) {

            convertView = layoutInflater.inflate(R.layout.layout_mydialoguemassege, null);
            holder2 = new MessageDetailAdapter.ViewHolder2(convertView,(ChatDetailActivity.messageRecord) getItem(position),context,UserHead);
            convertView.setTag(holder2);
        } else {

            convertView = layoutInflater.inflate(R.layout.layout_tdialoguemassege, null);
            holder1 = new MessageDetailAdapter.ViewHolder1(convertView,(ChatDetailActivity.messageRecord) getItem(position),context,tUserHead);
            convertView.setTag(holder1);
        }

        return convertView;
    }


    static class ViewHolder1{
        ImageView iv_tuserHead;
        TextView content;

        public ViewHolder1(View itemView, ChatDetailActivity.messageRecord message, Context context,String tUserHead){

            iv_tuserHead = itemView.findViewById(R.id.iv_tuserHead);
            content=itemView.findViewById(R.id.tv_tUserMessage);

            content.setText(message.getContent());


            if (tUserHead != null ) {
                Glide.with(context)
                        .load(tUserHead)
                        .into(iv_tuserHead);
            } else {
                // 处理无图片数据时的逻辑，比如显示默认图片
                iv_tuserHead.setImageResource(R.drawable.icon_take_photo);
            }


        }



    }

    static class ViewHolder2{
        ImageView iv_userHead;
        TextView content;

        public ViewHolder2(View itemView, ChatDetailActivity.messageRecord message, Context context,String UserHead){

            iv_userHead = itemView.findViewById(R.id.iv_userHead);
            content=itemView.findViewById(R.id.tv_myUserMessage);

            content.setText(message.getContent());


            if (UserHead != null ) {
                Glide.with(context)
                        .load(UserHead)
                        .into(iv_userHead);
            } else {
                // 处理无图片数据时的逻辑，比如显示默认图片
                iv_userHead.setImageResource(R.drawable.icon_take_photo);
            }


        }

    }


}
