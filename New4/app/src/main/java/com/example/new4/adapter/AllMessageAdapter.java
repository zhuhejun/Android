package com.example.new4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.new4.AllMessageActivity;
import com.example.new4.R;

import java.util.ArrayList;
import java.util.List;

public class AllMessageAdapter extends BaseAdapter {

    private List<AllMessageActivity.MessageList> messageLists = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    private String tUserHead;

    public AllMessageAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<AllMessageActivity.MessageList> messageLists, String tUserHead){
        this.tUserHead = tUserHead;
        this.messageLists = messageLists;
        notifyDataSetChanged(); //通知适配器数据已经改变
    }

    @Override
    public int getCount() {
        return messageLists.size();
    }

    @Override
    public Object getItem(int position) {
        return messageLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convertView = layoutInflater.inflate(R.layout.layout_allmassege, null);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);

        // 获取 MessageList 对象
        AllMessageActivity.MessageList message = messageLists.get(position);
        holder.bindData(message, context, tUserHead); // 将数据绑定到 ViewHolder

        return convertView;
    }

    static class ViewHolder {
        ImageView iv_userHead;
        TextView tv_userName, tv_unReadNum;

        public ViewHolder(View itemView) {
            iv_userHead = itemView.findViewById(R.id.iv_tuserHead);
            tv_unReadNum = itemView.findViewById(R.id.tv_messageNum);
            tv_userName = itemView.findViewById(R.id.tv_tUserName);
        }

        public void bindData(AllMessageActivity.MessageList message, Context context, String tUserHead) {
            // 绑定数据
            tv_userName.setText(message.getUsername());
            if (message.getUnReadNum() != 0) {
                tv_unReadNum.setText(String.valueOf(message.getUnReadNum()));
                tv_unReadNum.setVisibility(View.VISIBLE);
            } else {
                tv_unReadNum.setVisibility(View.GONE);
            }

            if (tUserHead != null) {
                Glide.with(context)
                        .load(tUserHead)
                        .into(iv_userHead);
            } else {
                // 处理无图片数据时的逻辑，比如显示默认图片
                iv_userHead.setImageResource(R.drawable.icon_take_photo);
            }
        }
    }
}