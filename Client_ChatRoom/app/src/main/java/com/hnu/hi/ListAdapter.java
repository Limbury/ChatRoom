package com.hnu.hi;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<ManList> mList;
    long firstClickTime = 0;//修复双击打开两个同样的一对一聊天界面
    private static final String TAG = "ListAdapter";
    static class ViewHolder extends RecyclerView.ViewHolder{
        View listView;
        ImageView listImage;
        TextView listName;

        public ViewHolder(View view) {
            super(view);
            listView = view;
            listName = (TextView) view.findViewById(R.id.list_name);
            listImage = (ImageView) view.findViewById(R.id.list_image);
        }
    }

    public ListAdapter(List<ManList> list){
        mList = list;
        firstClickTime = 0;//双击后退出重置时间，防止双击后再点击进不去
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                ////修复双击打开两个同样的一对一聊天界面
                //Boolean flag_double = Boolean.TRUE;

                if(firstClickTime > 0){
                    long secondClickTime = SystemClock.uptimeMillis();//距离上次开机时间
                    long dtime = secondClickTime - firstClickTime;
                    if(300 < dtime && dtime <1000){//双击不做操作 大于1000是从一对一界面返回了
                        //flag_double = Boolean.FALSE;
                        Log.d(TAG, "onClick: 双击");
                        Toast.makeText(context, "请勿双击", Toast.LENGTH_SHORT).show();
                        firstClickTime = 0;//双击后退出重置时间，防止双击后再点击进不去
                        return;
                    }
//                    else if(dtime > 1000){
//                        firstClickTime = 0;
//                        return;
//                    }
                }
                firstClickTime = SystemClock.uptimeMillis();
                int position = holder.getAdapterPosition();
                ManList manList = mList.get(position);
                String name = manList.getIdName();
                Toast.makeText(context,"即将和"+name+"聊天",Toast.LENGTH_SHORT).show();
//                Message message = new Message();
//                message.what = 0x554;//添加消息列表
//                message.obj = manList;

                Intent intent = new Intent(context,MainActivity.class);
                //intent.putExtra("name",name);
                intent.putExtra("chat_id",manList.getId().toString());
                intent.putExtra("chat_name",manList.getName());
                Log.d(TAG, "onClick: 启动聊天界面");
                context.startActivity(intent);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManList list = mList.get(position);
        holder.listImage.setImageResource(list.getImageId());
        holder.listName.setText(list.getIdName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
