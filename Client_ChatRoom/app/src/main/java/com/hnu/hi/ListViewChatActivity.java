package com.hnu.hi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewChatActivity extends AppCompatActivity {
    private List<ManList> listList = new ArrayList<>();
    private static final String TAG = "ListViewChatActivity";
    private TextView hostname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        Intent intent = getIntent();
        String man_name = intent.getStringExtra("Displayname");
        hostname = (TextView) findViewById(R.id.list_host_name);
        hostname.setText("Hi  "+man_name);
        initLists();//初始化联系人列表
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ListAdapter adapter = new ListAdapter(listList);
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: list");
    }
    private void initLists(){
        ManList manList1=  new ManList("李益军",R.drawable.ic_launcher);
        listList.add(manList1);
        ManList manList2=  new ManList("符希健",R.drawable.ic_launcher);
        listList.add(manList2);
        ManList manList3=  new ManList("钱建刚",R.drawable.ic_launcher);
        listList.add(manList3);
        ManList manList4=  new ManList("甘延汉",R.drawable.ic_launcher);
        listList.add(manList4);
        ManList manList5=  new ManList("李恩晗",R.drawable.ic_launcher);
        listList.add(manList5);
        ManList manList6=  new ManList("李汉军",R.drawable.ic_launcher);
        listList.add(manList6);
        ManList manList7=  new ManList("冯姜瑶",R.drawable.ic_launcher);
        listList.add(manList7);
        ManList manList8=  new ManList("周元奇",R.drawable.ic_launcher);
        listList.add(manList8);
        ManList manList9=  new ManList("叶尔那尔·巴哈提",R.drawable.ic_launcher);
        listList.add(manList9);
        ManList manList10=  new ManList("进度款",R.drawable.ic_launcher);
        listList.add(manList10);
        ManList manList11=  new ManList("折戟沉沙",R.drawable.ic_launcher);
        listList.add(manList11);
        ManList manList12=  new ManList("时代峻峰的",R.drawable.ic_launcher);
        listList.add(manList12);
        ManList manList13=  new ManList("asdfs",R.drawable.ic_launcher);
        listList.add(manList13);
        ManList manList14=  new ManList("safddsf",R.drawable.ic_launcher);
        listList.add(manList14);

    }
}
