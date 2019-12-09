package com.hnu.hi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.ListInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewChatActivity extends AppCompatActivity {
    private List<ManList> listList = new ArrayList<>();
    private List<ManList> list_mess = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private static final String TAG = "ListViewChatActivity";
    TextView hostname;
    ImageView add_man;
    public Button right;
    public Button cancel;

    public  TextView mess;
    public  TextView friendList;
    public EditText nickname_edit;
    private String nickname;
    private String chat_id;
    private String chat_name;
    private Client_ChatRoom client_chatRoom = Client_ChatRoom.getClient_chatRoom();
    private ListInfo listInfo;
    private ExecutorService mThreadPool;
    public final int MSG_DOWN_FAIL = 1;
    public final int MSG_DOWN_SUCCESS = 2;
    public final int MSG_DOWN_START = 3;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x03:
                    listInfo = (ListInfo) msg.obj;
                    client_chatRoom.setListInfo(listInfo);
                    String man_name1 = listInfo.getNickName().toString();
                    Integer man_uid2 = listInfo.getJKNum();
                    hostname = (TextView) findViewById(R.id.list_host_name);
                    hostname.setText("Hi  "+man_uid2+"("+man_name1+")");
                    flushManList();
                    break;
                case 0x554:
                    addMess((ManList) msg.obj);
                    break;
                case 0x55:
                    byte result = (byte)msg.obj;
                    if(result == 0){
                        Toast.makeText(ListViewChatActivity.this,"添加好友成功",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(ListViewChatActivity.this,"添加好友失败",Toast.LENGTH_LONG).show();
                    }
                    break;
                case MSG_DOWN_SUCCESS:
                    String man_name = listInfo.getNickName().toString();
                    Integer man_uid = listInfo.getJKNum();
                    hostname = (TextView) findViewById(R.id.list_host_name);
                    hostname.setText("Hi  "+man_uid+"("+man_name+")");
                    break;
//                case MSG_DOWN_START:
//                    //mTipTv.setText("download start");
//                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .penaltyLog().build());

        //mThreadPool = Executors.newCachedThreadPool();//初始化线程

        //获取信息
        Intent intent = getIntent();
        chat_id = intent.getStringExtra("chat_id");
        chat_name = intent.getStringExtra("chat_name");



        //initLists();//初始化联系人列表
        new MyThread(mHandler).start();
        if(listInfo == null){
            Log.d(TAG, "onCreate: listInfo == null");
            ListInfo listInfo2 = client_chatRoom.getListInfo();
            if(listInfo2 != null){
                listInfo = listInfo2;
                String man_name1 = listInfo.getNickName().toString();
                Integer man_uid2 = listInfo.getJKNum();
                hostname = (TextView) findViewById(R.id.list_host_name);
                hostname.setText("Hi  "+man_uid2+"("+man_name1+")");
                Log.d(TAG, "onCreate: client_chatRoom.getListInfo()不为空");
            }
            //flushManList();
        }



        //initLists();
        //flushManList();
        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        add_man = (ImageView) findViewById(R.id.add_man);
        mess = (TextView) findViewById(R.id.xiaoxi);
        friendList = (TextView) findViewById(R.id.haoyou);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListAdapter(listList);
        recyclerView.setAdapter(adapter);

        add_man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                AlertDialog.Builder dialog = new AlertDialog.Builder(ListViewChatActivity.this);
                final View dialogView = LayoutInflater.from(ListViewChatActivity.this).inflate(R.layout.activity_add_man,null);
                //dialog.setTitle("2222");
                dialog.setView(dialogView);
                right = (Button)dialogView.findViewById(R.id.add_man_right_button);
                cancel = (Button) dialogView.findViewById(R.id.add_man_cancel_button);
                nickname_edit = (EditText)dialogView.findViewById(R.id.add_man_editText);
                dialog.show();
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nickname = nickname_edit.getText().toString();
                        int add_id = Integer.parseInt(nickname);  //注意输入不是数字的时候
                        String list_name = "111";
                        try {
                            client_chatRoom.SendaddFriend(add_id, list_name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ListViewChatActivity.this,nickname,Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO
                        nickname_edit.setText("");
                        Toast.makeText(ListViewChatActivity.this,"cancel",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        friendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flushManList();
                getManList();
                friendList.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mess.setBackgroundColor(Color.parseColor("#000000"));
            }
        });

        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMess();
                mess.setBackgroundColor(Color.parseColor("#FFFFFF"));
                friendList.setBackgroundColor(Color.parseColor("#000000"));
            }
        });


        Log.d(TAG, "onCreate: list");
    }

    private void initLists(){
        listInfo = client_chatRoom.getListInfo();
        Log.d(TAG, "initLists: 初始化好友列表");
        //flushManList();
        Log.d(TAG, "initLists: 初始化完成");
    }


    private void flushManList(){

        int list_size = listList.size();
        listList.clear();
        adapter.notifyItemRangeRemoved(0,list_size);
        byte listCount = listInfo.getListCount();// 保存有多少组好友
        String ListName[] = listInfo.getListName();// 保存每个分组的名称
        byte[] bodyCount = listInfo.getBodyCount();// 每组有多少个人
        int bodyNum[][] = listInfo.getBodyNum();// 每个好友的JK号
        int bodypic[][] = listInfo.getBodypic();//好友头像
        String nikeName[][] = listInfo.getNikeName();// 每个好友的昵称
        for(int i = 0;i < bodyNum.length;i++){
            for(int j = 0;j < bodyNum[i].length;j++){//不同分组
                ManList manList1=  new ManList(bodyNum[i][j],nikeName[i][j],R.drawable.ic_launcher);
                listList.add(manList1);
            }

        }
        list_size = listList.size();
        //ListAdapter adapter = new ListAdapter(listList);
        //recyclerView.setAdapter(adapter);
        adapter.notifyItemRangeInserted(0,list_size);
    }
    private void getManList(){
        ListAdapter adapter = new ListAdapter(listList);
        recyclerView.setAdapter(adapter);
    }
    private void getMess(){
        //listList.clear();
        Log.d(TAG, "getMess: 消息列表长度："+list_mess.size());
        ListAdapter adapter = new ListAdapter(list_mess);
        recyclerView.setAdapter(adapter);
//        adapter.notifyItemInserted(list_mess.size()- 1);
//当有新消息时，刷新RecyclerView中的显示
        //recyclerView.scrollToPosition(list_mess.size()- 1);
    }
    private void addMess(ManList manList1){
        list_mess.add(manList1);
        Log.d(TAG, "addMess: 添加消息列表");
    }

//    public void setChatIdAndName(String chat_id,String chat_name)
//    {
//        this.chat_id = chat_id;
//        this.chat_name = chat_name;
//    }


    class MyThread extends Thread {
        private  Handler handler;
        public MyThread(Handler handler){
            this.handler = handler;
        }
        @Override
        public void run() {
            //try {
                Log.d(TAG, "run: 获取好友列表");

//                listInfo = client_chatRoom.getlist();
//                Message msg = new Message();
//                msg.what = MSG_DOWN_SUCCESS;

                client_chatRoom.setHandler(handler);
                client_chatRoom.start();

           // } catch (IOException e) {
               // e.printStackTrace();
            //}
            Log.d(TAG,"MyThread stop run");
        }
    }

//        private void initLists(){
//        ManList manList520=  new ManList(1314520,"韩逸清大美女",R.drawable.emotion_aixin);
//        listList.add(manList520);
//        ManList manList1=  new ManList(1314520,"我爱你",R.drawable.emotion_aixin);
//        listList.add(manList1);
//        ManList manList2=  new ManList(1314520,"一生",R.drawable.emotion_aixin);
//        listList.add(manList2);
//        ManList manList3=  new ManList(1314520,"一世",R.drawable.emotion_aixin);
//        listList.add(manList3);
//        ManList manList4=  new ManList(1314520,"执子",R.drawable.emotion_aixin);
//        listList.add(manList4);
//        ManList manList5=  new ManList(1314520,"之手",R.drawable.emotion_aixin);
//        listList.add(manList5);
//        ManList manList6=  new ManList(1314520,"与子",R.drawable.emotion_aixin);
//        listList.add(manList6);
//        ManList manList7=  new ManList(1314520,"偕老",R.drawable.emotion_aixin);
//        listList.add(manList7);
//
//    }

}
