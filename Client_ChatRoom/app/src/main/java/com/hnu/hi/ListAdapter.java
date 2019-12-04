package com.hnu.hi;

import android.content.Context;
import android.content.Intent;
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
    }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                ManList manList = mList.get(position);
                String name = manList.getName();
                Context context = v.getContext();
                Toast.makeText(context,"即将和"+name+"聊天",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("name",name);
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManList list = mList.get(position);
        holder.listImage.setImageResource(list.getImageId());
        holder.listName.setText(list.getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
