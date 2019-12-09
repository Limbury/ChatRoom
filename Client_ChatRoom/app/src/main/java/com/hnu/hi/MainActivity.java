package com.hnu.hi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.ListInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public
class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private ImageView send;
    private ImageView back;
    private TextView nameview;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private String man_name;
    private String chat_id;
    private String chat_name;
    private Client_ChatRoom client_chatRoom = Client_ChatRoom.getClient_chatRoom();
    private List<Msg> msgList= new ArrayList<>();
    private static final String TAG = "MainActivity";

    private Handler sendHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x041:
                    Msg msg1 =  new Msg((String) msg.obj,Msg.type_sent);
                    msgList.add(msg1);
                    adapter.notifyItemInserted(msgList.size()- 1);
//当有新消息时，刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size()- 1);
//将RecyclerView定位到最后一行
                    break;
            }
        }
    };
    private Handler recHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x04:

                    String chatText = (String) msg.obj;
                    Integer from_uid = msg.arg1;
                    Log.d(TAG, "handleMessage: "+chatText+"from "+from_uid);
                    Msg msg1=  new Msg(chatText,Msg.type_received);
                    if(from_uid.toString().equals(chat_id)){
                        msgList.add(msg1);
//                        adapter=  new MsgAdapter(msgList);
//                        msgRecyclerView.setAdapter(adapter);
                        adapter.notifyItemInserted(msgList.size()- 1);
//当有新消息时，刷新RecyclerView中的显示
                        msgRecyclerView.scrollToPosition(msgList.size()- 1);
                    }

                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
         //man_name = intent.getStringExtra("name");
        chat_id = intent.getStringExtra("chat_id");
        chat_name = intent.getStringExtra("chat_name");
        man_name = chat_id+"("+chat_name+")";
        Log.d(TAG, "onCreate: man_name="+man_name);
        Log.d(TAG, "onCreate: Talking");

        new MyRecThread(recHandler).start();

        inputText= (EditText) findViewById(R.id.input_text);
        send= (ImageView) findViewById(R.id.send_image);
        back= (ImageView) findViewById(R.id.back);
        nameview = (TextView) findViewById(R.id.linkman_name);
        nameview.setText(man_name);//设置联系人名字
        //initMsgs();        //初始化消息数据，应该改成同步消息
        msgRecyclerView= (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=  new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
//点击事件获取EditText中的内容
        @Override
        public void onClick(View v) {
            String content=inputText.getText().toString();
                if (!"".equals(content)){


//清空输入框的内容
                 new MySendThread(sendHandler).start();

                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,ListViewChatActivity.class);
//                intent.putExtra("chat_id",chat_id);
//                intent.putExtra("chat_name",chat_name);
//                startActivity(intent);
                finish();
            }
        });
    }
//    private void initMsgs() {
//        Msg msg1=  new Msg("喵喵清",Msg.type_received);
//        msgList.add(msg1);
//        Msg msg2= new Msg("我是喵喵清喵喵喵喵",Msg.type_sent);
//        msgList.add(msg2);
//        Msg msg3= new Msg("余生请多指教",Msg.type_received);
//        msgList.add(msg3);
//
//    }

    class MyRecThread extends Thread {
        private Handler handler;

        public MyRecThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            //try {
            Log.d(TAG, "run: 接受消息聊天线程");
            client_chatRoom.setHandler(handler);
            //client_chatRoom.run();
            // } catch (IOException e) {
            // e.printStackTrace();
            //}
            Log.d(TAG, "MyThread stop run");
        }
    }
    class MySendThread extends Thread {
        private Handler handler;

        public MySendThread(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
            String content=inputText.getText().toString();
            Log.d(TAG, "run: 发送消息聊天线程");
            client_chatRoom.sendMsg(Integer.parseInt(chat_id),content);
            Message message = new Message();
            message.what = 0x041;
            message.obj = content;
            handler.sendMessage(message);
            inputText.setText("");

            } catch (IOException e) {
             e.printStackTrace();
            }
            Log.d(TAG, "run: 发送信息成功，结束线程");
        }
    }
}
