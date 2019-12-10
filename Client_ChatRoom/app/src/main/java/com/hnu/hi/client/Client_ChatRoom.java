package com.hnu.hi.client;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


//import javax.swing.JOptionPane;

import com.hnu.hi.ListViewChatActivity;
import com.hnu.hi.Msg;
import com.hnu.hi.data.ListInfo;
import com.hnu.hi.data.Figures;
import com.hnu.hi.data.LoginDataSource;
import com.hnu.hi.msg.MsgAddFriend;
import com.hnu.hi.msg.MsgAddFriendResp;
import com.hnu.hi.msg.MsgChatText;
import com.hnu.hi.msg.MsgHead;
import com.hnu.hi.msg.MsgLogin;
import com.hnu.hi.msg.MsgLoginResp;
import com.hnu.hi.msg.MsgReg;
import com.hnu.hi.msg.MsgRegResp;
import com.hnu.hi.msg.MsgTeamList;
import com.hnu.hi.tools.PackageTool;
import com.hnu.hi.tools.ParseTool;

public class Client_ChatRoom extends Thread {
    private String serverip;
    private int port;
    private Socket client;
    private Handler handler;
    private static int OwnJKNum;// 当登陆成功后，就该ChatClient的唯一JK号
    private InputStream ins;
    private OutputStream ous;
    private static final String TAG = "Client_ChatRoom";
    private ListInfo listInfo;

    //完美单例模式
    private volatile static Client_ChatRoom client_chatRoom;

    private Client_ChatRoom(String serverIp, int port) {
        super();
        this.serverip = serverIp;
        this.port = port;
    }
//            要从Android模拟器访问PC localhost，请使用10.0.2.2而不是127.0.0.1。 localhost或127.0.0.1是指模拟设备本身，而不是运行模拟器的主机。
//            参考：http://developer.android.com/tools/devices/emulator.html#networkaddresses
//
//            对于Genymotion使用：10.0.3.2而不是10.0.2.2

    public static Client_ChatRoom getClient_chatRoom(){
        if(client_chatRoom == null){
            synchronized (Client_ChatRoom.class){
                if(client_chatRoom == null)
                    //client_chatRoom = new Client_ChatRoom("10.0.2.2",6666);
                    //client_chatRoom = new Client_ChatRoom("175.10.207.234",6666);
                    client_chatRoom = new Client_ChatRoom("192.168.43.233",6666);
                    //client_chatRoom.start();
            }
        }
        return client_chatRoom;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setListInfo(ListInfo listInfo){
        this.listInfo = listInfo;
    }

    public Integer getOwnJKNum(){return OwnJKNum;}

    public ListInfo getListInfo(){return listInfo;}

    /*
     * 链接服务器
     */
    public boolean ConnectServer() {
        try {
            client = new Socket(serverip, port);
            System.out.println("服务器已连接");
            Log.d(TAG, "ConnectServer: 服务器已连接");
            ins= client.getInputStream();
            ous = client.getOutputStream();// 获取该连接的输入输出流
            return true;
        } catch (IOException e) {
             e.printStackTrace();
            Log.d(TAG, "ConnectServer: "+e);
        }
        Log.d(TAG, "ConnectServer: 服务器连接失败");
        return false;
    }
    public void disConnectServer(){

        try {
            ous.close();
            ins.close();
            client.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public Socket getClient(){return client;}

    /*
     * 接受服务器消息
     */
    public void run() {
        while (true) {
            try {
                processMsg();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("与服务器断开连接");
                Log.d(TAG, "run: 与服务器断开连接");
                //JOptionPane.showMessageDialog(null, "与服务器断开连接", "ERROR", JOptionPane.ERROR_MESSAGE);
                //System.exit(0);
                break;
            }
        }
    }
    public void runWithException() throws IOException {
        while (true) {
                processMsg();
        }
    }
    /**
     * sendMsg 发送信息
     *
     * @param to
     * @param Msg
     * @throws IOException
     */
    public void sendMsg(int to, String Msg) throws IOException {
        MsgChatText mct = new MsgChatText();
        byte data[] = Msg.getBytes();
        int TotalLen = 13;
        TotalLen += data.length;
        byte type = 0x04;
        mct.setTotalLen(TotalLen);
        mct.setType(type);
        mct.setDest(to);
        mct.setSrc(OwnJKNum);
        mct.setMsgText(Msg);

        byte[] sendMsg = PackageTool.packMsg(mct);
        ous.write(sendMsg);
        ous.flush();
        Log.d(TAG, "sendMsg: send to"+to+" "+Msg);
    }
    /*
     * 读消息
     */
    public byte[] receiveMsg() throws IOException {
        DataInputStream dis = new DataInputStream(ins);
        Log.d(TAG, "receiveMsg: dis.readInt()为空报错");
        int totalLen = dis.readInt();
        System.out.println("TotalLen"+totalLen);
        //if(totalLen == 67109364)totalLen = 20;
        Log.d(TAG, "receiveMsg: TotalLen="+totalLen);
        // 读取totalLen长度的数据
        byte[] data = new byte[totalLen - 4];
        dis.readFully(data);
        Log.d(TAG, "receiveMsg: 数据读取成功");
        return data;
    }

    public ListInfo packlist(MsgHead recMsg){
        ListInfo list = new ListInfo();
        MsgTeamList mtl = (MsgTeamList) recMsg;
        list.setNickName(mtl.getUserName());
        list.setJKNum(mtl.getDest());
        list.setPic(mtl.getPic());
        list.setListCount(mtl.getListCount());
        list.setListName(mtl.getListName());
        list.setBodyCount(mtl.getBodyCount());
        list.setBodyNum(mtl.getBodyNum());
        list.setBodypic(mtl.getBodyPic());
        list.setNikeName(mtl.getNikeName());
        list.setBodyState(mtl.getBodyState());
        return list;
    }


    public ListInfo getlist() throws IOException {
        byte[] data = receiveMsg();
        MsgHead recMsg = ParseTool.parseMsg(data);
        Log.d(TAG, "getlist: 接受好友列表信息");
        if (recMsg.getType() != 0x03) {
            System.out.println("通讯协议错误");
            //System.exit(0);
        }
        return packlist(recMsg);
    }
    public MsgRegResp getRegResp() throws IOException {
        byte[] data = receiveMsg();
        MsgHead recMsg = ParseTool.parseMsg(data);

        Log.d(TAG, "getlist: 接受好友列表信息");
        if (recMsg.getType() != 0x11) {
            System.out.println("通讯协议错误");
            //System.exit(0);
        }
        MsgRegResp mrr = (MsgRegResp) recMsg;
        return mrr;
    }
    /**
     * processMsg 接受服务器传来的消息
     *
     * @throws IOException
     */
    public void processMsg() throws IOException {
        byte[] data = receiveMsg();
        // 将数组转换为类
        MsgHead recMsg = ParseTool.parseMsg(data);
        byte MsgType = recMsg.getType();

        // 根据不同的信息进行处理
        if (MsgType == 0x04) {
            MsgChatText mct = (MsgChatText) recMsg;
            int from = mct.getSrc();
            String Msg = mct.getMsgText();
            Log.d(TAG, "processMsg: 0x04 "+from+"  "+Msg);
            Message msg = new Message();
            msg.what = 0x04;
            msg.obj = Msg;
            msg.arg1 = from;
            handler.sendMessage(msg);
        }
        else if(MsgType == 0x03){//更新好友列表
            System.out.println("Refresh list");
            Log.d(TAG, "processMsg: 更新好友列表");
            ListInfo list = packlist(recMsg);
            Message msg = new Message();
            msg.what = 0x03;
            msg.obj = list;
            handler.sendMessage(msg);
            //Figures.list.Refresh_List(list);
        }
        if (MsgType == 0x55){
//			System.out.println("Here");
            MsgAddFriendResp mafr = (MsgAddFriendResp) recMsg;
            byte result = mafr.getState();
            System.out.println("Add Friend Result "+result);
            Log.d(TAG, "processMsg: Add Friend Result:"+result);
			/*if(Figures.afu != null){
//				System.out.println("To show Result");
				Figures.afu.showResult(result);
			}*/
            Message msg = new Message();
            msg.what = 0x55;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    /**
     * Register 注册用户
     *
     * @param NikeName
     *            昵称
     * @param PassWord
     *            密码
     * @return 注册状态
     */
    public int Reg(String NikeName, String PassWord) {
        try {
            MsgReg mr = new MsgReg();
            int len = 33; // MsgReg的长度为固定的33
            byte type = 0x01; // MsgReg类型为0x01

            // 设置MsgReg的参数
            mr.setTotalLen(len);
            mr.setType(type);
            mr.setDest(Figures.ServerJK); // 服务器的JK号
            mr.setSrc(Figures.LoginJK);
            mr.setNikeName(NikeName);
            mr.setPwd(PassWord);

            // 打包MsgReg
            byte[] sendMsg = PackageTool.packMsg(mr);
            ous.write(sendMsg);

            // 接收服务器的反馈信息
            byte[] data = receiveMsg();

            // 将数组转换为类
            MsgHead recMsg = ParseTool.parseMsg(data);

            if (recMsg.getType() != 0x11) {// 不是回应注册消息
                System.out.println("通讯协议错误");
                Log.d(TAG, "Reg: 通讯协议错误");
                return 0;
            }

            MsgRegResp mrr = (MsgRegResp) recMsg;
            // System.out.println("TestHere"+recMsg.getDest());
            if (mrr.getState() == 0) {
                /*
                 * 注册成功
                 */
                // System.out.println("注册的JK号为" + mrr.getDest());
                Log.d(TAG, "Reg: 注册的JK号为" + mrr.getDest());
                //JOptionPane.showMessageDialog(null, "注册成功\nJK码为" + mrr.getDest());
                return mrr.getDest();
            } else {
                /*
                 * 注册失败
                 */
                Log.d(TAG, "Reg: 未知错误");
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("与服务器断开连接");
        return 0;
    }

    /**
     * Login 向服务器发送登陆请求
     *
     * @param id
     * @param pwd
     * @return 能否登陆
     */
    public int Login(int id, String pwd)
    {
        try {
            MsgLogin ml = new MsgLogin();
            int len = 23;
            byte type = 0x02;

            // 设置MsgLogin的各种东西
            ml.setTotalLen(len);
            ml.setType(type);
            ml.setDest(Figures.ServerJK);
            ml.setSrc(id);
            ml.setPwd(pwd);
            // 打包MsgLogin
            byte[] sendmsg = PackageTool.packMsg(ml);
            ous.write(sendmsg);
            // 接收服务器的反馈信息
            byte[] data = receiveMsg();
            // 将数组转换为类
            MsgHead recMsg = ParseTool.parseMsg(data);
            if (recMsg.getType() != 0x22) {// 不是登陆反馈信息
                System.out.println("通讯协议错误");
                Log.d(TAG, "Login: 通讯协议错误");
                return 5;
            }
            MsgLoginResp mlr = (MsgLoginResp) recMsg;
            //Log.d(TAG, "Login: 208");
            byte resp = mlr.getState();
            Log.d(TAG, "Login: resp");
            if (resp == 0) {
                System.out.println("登陆成功");
                Log.d(TAG, "Login: 登陆成功");
                OwnJKNum = id;
                return 0;
            } else if (resp == 1) {
                System.out.println("JK号或密码错误");
                Log.d(TAG, "Login: JK号或密码错误");
                return 1;
            } else if(resp == 2){
                System.out.println("这个账号已经登陆");
                Log.d(TAG, "Login: 这个账号已经登陆");
                return 2;
            } else {
                System.out.println("未知错误");
                Log.d(TAG, "Login: 未知错误");
                return 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Login: "+e);
        }

        System.out.println("与服务器断开连接");
        Log.d(TAG, "Login: 与服务器断开连接");
        return 4;
    }

    public void SendaddFriend(int add_id, String list_name) throws IOException {
        MsgAddFriend maf = new MsgAddFriend();
        byte data[] = list_name.getBytes();
        int TotalLen = 17;
        TotalLen += data.length;
        byte type = 0x05;
        maf.setTotalLen(TotalLen);
        maf.setType(type);
        maf.setDest(Figures.ServerJK);
        maf.setSrc(OwnJKNum);
        maf.setAdd_ID(add_id);
        maf.setList_name(list_name);
        byte[] sendMsg = PackageTool.packMsg(maf);
        ous.write(sendMsg);
        Log.d(TAG, "SendaddFriend: ffa");
        ous.flush();
    }

}
