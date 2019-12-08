package com.hnu.hi;

import android.content.DialogInterface;
import android.content.Intent;
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
    private static final String TAG = "ListViewChatActivity";
    TextView hostname;
    ImageView add_man;
    public Button right;
    public Button cancel;
    public EditText nickname_edit;
    private String nickname;
    private Client_ChatRoom client_chatRoom = Client_ChatRoom.getClient_chatRoom();
    private ListInfo listInfo;
    private ExecutorService mThreadPool;
    public final int MSG_DOWN_FAIL = 1;
    public final int MSG_DOWN_SUCCESS = 2;
    public final int MSG_DOWN_START = 3;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_DOWN_FAIL:
                    //mTipTv.setText("download fial");
                    break;
                case MSG_DOWN_SUCCESS:
                    String man_name = listInfo.getNickName().toString();
                    Integer man_uid = listInfo.getJKNum();
                    hostname = (TextView) findViewById(R.id.list_host_name);
                    hostname.setText("Hi  "+man_uid+"("+man_name+")");
                    break;
                case MSG_DOWN_START:
                    //mTipTv.setText("download start");
                    break;
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
        //Intent intent = getIntent();

        //String ClientJsonData = intent.getStringExtra("client");
        //String ListInfoJdonData = intent.getStringExtra("ListInfo");

        //listInfo = new Gson().fromJson(ListInfoJdonData,ListInfo.class);


//        let testString:String = "成都"
//        let utf8String = (testString as NSString).UTF8String
        new MyThread().start();




        initLists();//初始化联系人列表
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        add_man = (ImageView) findViewById(R.id.add_man);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ListAdapter adapter = new ListAdapter(listList);
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


        Log.d(TAG, "onCreate: list");
    }
    private void initLists(){
        ManList manList520=  new ManList("韩逸清",R.drawable.emotion_aixin);
        listList.add(manList520);
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

//    private Boolean getManList() throws IOException {
//        listInfo.getListCount();
//
//         return false;
//    }

    class MyThread extends Thread {
        @Override
        public void run() {
            try {
                Log.d(TAG, "run: 获取好友列表");

                listInfo = client_chatRoom.getlist();
                Message msg = new Message();
                msg.what = MSG_DOWN_SUCCESS;
                mHandler.sendMessage(msg);
                client_chatRoom.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"MyThread stop run");
        }
    }

}
