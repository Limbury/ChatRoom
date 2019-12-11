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
import android.widget.Toast;

import com.hnu.hi.client.Client_ChatRoom;
import com.hnu.hi.data.ListInfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public
class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private ImageView send;
    //private ImageView back;
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
                case 0x123:
                    Log.d(TAG, "handleMessage: 0x123");
                    String text = (String) msg.obj;
                    Integer isrecc = msg.arg1;
                    if(isrecc == 1){
                        Msg msg2 =  new Msg(text,Msg.type_received);
                        msgList.add(msg2);
                        adapter.notifyItemInserted(msgList.size()- 1);
                        msgRecyclerView.scrollToPosition(msgList.size()- 1);
//将RecyclerView定位到最后一行
                        Log.d(TAG, "handleMessage: 0x123 更新消息");
                    }
                    else {
                        Msg msg2 =  new Msg(text,Msg.type_sent);
                        msgList.add(msg2);
                        adapter.notifyItemInserted(msgList.size()- 1);
                        msgRecyclerView.scrollToPosition(msgList.size()- 1);
                        Log.d(TAG, "handleMessage: 0想23 更新发送消息");
                    }
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
                        new MyThreadSave(sendHandler,Integer.parseInt(chat_id),chatText,"1").start();
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
        new MyThreadRead(sendHandler).start();
        inputText= (EditText) findViewById(R.id.input_text);
        send= (ImageView) findViewById(R.id.send_image);
        //back= (ImageView) findViewById(R.id.back);
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
                 new MyThreadSave(sendHandler,Integer.parseInt(chat_id),content,"0").start();
                }
            }
        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(MainActivity.this,ListViewChatActivity.class);
////                intent.putExtra("chat_id",chat_id);
////                intent.putExtra("chat_name",chat_name);
////                startActivity(intent);
//
//                finish();
//            }
//        });
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
            inputText.setText("");
            Log.d(TAG, "run: 发送消息聊天线程");
            client_chatRoom.sendMsg(Integer.parseInt(chat_id),content);
            Message message = new Message();
            message.what = 0x041;
            message.obj = content;
            handler.sendMessage(message);


            } catch (IOException e) {
             e.printStackTrace();
            }
            Log.d(TAG, "run: 发送信息成功，结束线程");
        }
    }
    class MyThreadRead extends Thread {
        private Handler handler;
        private String from;
        private String own;
        private String chatText_read;
        private String isrec;
        public MyThreadRead(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: 读取聊天记录");
            File dir1 = getDir("abc", MODE_PRIVATE);
            File file1 = new File(dir1, "mess_info.txt");
            
            try {
                InputStream instream = new FileInputStream(file1);
                if (instream != null) {
                    Log.d(TAG, "run: 不为空");
                    InputStreamReader inputreader
                            = new InputStreamReader(instream, "UTF-8");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = "";
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        StringTokenizer st= new StringTokenizer(line,"*");
                        if(st.hasMoreTokens()) {
                            own = st.nextToken();
                            Log.d(TAG, "run: from"+own);
                        }
                        if(st.hasMoreTokens()) {
                            from = st.nextToken();
                            Log.d(TAG, "run: from"+from);
                        }
                        if(st.hasMoreTokens()){
                            isrec = st.nextToken();
                            Log.d(TAG, "run: isrec="+isrec);
                        }
                        if(st.hasMoreTokens()){
                            chatText_read = st.nextToken();
                            Log.d(TAG, "run: chattext_read："+chatText_read);
                        }
                        if(own != null && from != null && isrec != null && chatText_read != null){
                            if(own.equals(client_chatRoom.getOwnJKNum().toString()) && from.equals(chat_id)){
                                Log.d(TAG, "run: 找到聊天记录");
                                    Message message = new Message();
                                    message.what = 0x123;
                                    message.obj = chatText_read;
                                    message.arg1 = Integer.parseInt(isrec);
                                    handler.sendMessage(message);
                            }
                        }

                    }
                    instream.close();//关闭输入流
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
            Log.d(TAG, "MyThreadSave stop run");
        }
    }
    class MyThreadSave extends Thread {
        private Handler handler;
        private Integer from;
        private String chatText;
        private String isrec;
        public MyThreadSave(Handler handler,Integer from,String chatText,String isrec) {
            this.handler = handler;
            this.from = from;
            this.chatText = chatText;
            this.isrec = isrec;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: 保存聊天记录");
            File dir1 = getDir("abc", MODE_PRIVATE);
            File file1 = new File(dir1, "mess_info.txt");

            Log.d(TAG, "getDir"+dir1.toString());
            try {
                if(!file1.exists()){
                    file1.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(file1, "rwd");
                raf.seek(file1.length());
                raf.write((client_chatRoom.getOwnJKNum().toString()+"*"+from.toString()+"*"+isrec+"*"+chatText+"\r\n").getBytes());
                raf.close();
//                    FileOutputStream out = openFileOutput("mess_info.txt", MODE_PRIVATE);
//                    out.write((from.toString()+"*"+"1"+"*"+chatText+"\r\n").getBytes());
//                    Log.d(TAG, "run: 写入成功");
//                    out.flush();
//                    out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG, "run: 文件不存在");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "run: IOE");
            }
            Log.d(TAG, "MyThreadSave stop run");
        }
    }
}
