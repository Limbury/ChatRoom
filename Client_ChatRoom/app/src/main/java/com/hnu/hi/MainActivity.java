package com.hnu.hi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
    private List<Msg> msgList= new ArrayList<>();
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String man_name = intent.getStringExtra("name");
        Log.d(TAG, "onCreate: Talking");


        inputText= (EditText) findViewById(R.id.input_text);
        send= (ImageView) findViewById(R.id.send_image);
        back= (ImageView) findViewById(R.id.back);
        nameview = (TextView) findViewById(R.id.linkman_name);
        nameview.setText(man_name);//设置联系人名字
        initMsgs();        //初始化消息数据，应该改成同步消息
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
                    Msg msg =  new Msg(content,Msg.type_sent);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()- 1);
//当有新消息时，刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size()- 1);
//将RecyclerView定位到最后一行
                    inputText.setText("");
//清空输入框的内容
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this,ListViewChatActivity.class);
//                startActivity(intent);
                  finish();
            }
        });
    }
    private void initMsgs() {
        Msg msg1=  new Msg("你好.",Msg.type_received);
        msgList.add(msg1);
        Msg msg2= new Msg("你好，有什么事吗",Msg.type_sent);
        msgList.add(msg2);
        Msg msg3= new Msg("数据库写好了吗？",Msg.type_received);
        msgList.add(msg3);
    }
}
