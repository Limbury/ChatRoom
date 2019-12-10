package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import data.ListInfo;
import data.Figures;
import msg.MsgAddFriend;
import msg.MsgAddFriendResp;
import msg.MsgChatText;
import msg.MsgHead;
import msg.MsgLogin;
import msg.MsgLoginResp;
import msg.MsgReg;
import msg.MsgRegResp;
import msg.MsgTeamList;
import tools.PackageTool;
import tools.ParseTool;

public class Client_ChatRoom extends Thread {
	private String serverip;
	private int port;
	private Socket client;
	
	private static int OwnJKNum;// 当登陆成功后，就该ChatClient的唯一JK号
	private InputStream ins;
	private OutputStream ous;
	
	public Client_ChatRoom(String serverip, int port) {
		super();
		this.serverip = serverip;
		this.port = port;
	}
	/*
	 * 链接服务器
	*/	
	public boolean ConnectServer() {
		try {
			client = new Socket(serverip, port);
			System.out.println("服务器已连接");
			ins= client.getInputStream();
			ous = client.getOutputStream();// 获取该连接的输入输出流
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return false;
	}
	
	public void offline() {
		try {
			client.close();
		}catch (IOException e) {
			// e.printStackTrace();
		}
	}
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
				//JOptionPane.showMessageDialog(null, "与服务器断开连接", "ERROR", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
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
	}
	/*
	 * 读消息
	 */
	public byte[] receiveMsg() throws IOException {
		DataInputStream dis = new DataInputStream(ins);
		int totalLen = dis.readInt();
		System.out.println("TotalLen"+totalLen);
		// 读取totalLen长度的数据
		byte[] data = new byte[totalLen - 4];
		dis.readFully(data);
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
		if (recMsg.getType() != 0x03) {
			System.out.println("通讯协议错误");
			System.exit(0);
		}
		return packlist(recMsg);
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
		}
		else if(MsgType == 0x03){//更新好友列表
			System.out.println("Refresh list");
			ListInfo list = packlist(recMsg);
			//Figures.list.Refresh_List(list);
		}
		else if (MsgType == 0x55){
//			System.out.println("Here");
			MsgAddFriendResp mafr = (MsgAddFriendResp) recMsg;
			byte result = mafr.getState();
			System.out.println("Add Friend Result "+result);
			/*if(Figures.afu != null){
//				System.out.println("To show Result");
				Figures.afu.showResult(result);
			}*/
		}
		else if (MsgType == 0x66) {
			// is history record
			MsgChatText mct = (MsgChatText) recMsg;
			int from = mct.getSrc();
			int to = mct.getDest();
			String Msg = mct.getMsgText();
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
	public boolean Reg(String NikeName, String PassWord) {
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
				return false;
			}

			MsgRegResp mrr = (MsgRegResp) recMsg;
			// System.out.println("TestHere"+recMsg.getDest());
			if (mrr.getState() == 0) {
				/*
				 * 注册成功
				 */
				// System.out.println("注册的JK号为" + mrr.getDest());
				//JOptionPane.showMessageDialog(null, "注册成功\nJK码为" + mrr.getDest());
				return true;
			} else {
				/*
				 * 注册失败
				 */
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("与服务器断开连接");
		return false;
	}

	/**
	 * Login 向服务器发送登陆请求
	 * 
	 * @param id
	 * @param pwd
	 * @return 能否登陆
	 */
	public int Login(int id, String pwd) {
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
				return 5;
			}
			MsgLoginResp mlr = (MsgLoginResp) recMsg;
			byte resp = mlr.getState();
			if (resp == 0) {
				System.out.println("登陆成功");
				OwnJKNum = id;
				return 0;
			} else if (resp == 1) {
				System.out.println("ID号或密码错误");
				return 1;
			} else if(resp == 2){
				System.out.println("这个账号已经登陆");
				return 2;
			} else {
				System.out.println("未知错误");
				return 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("与服务器断开连接");
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
		ous.flush();
	}
	/*
	 * uid 1 is looking the record with uid 2
	 */
	public void GetRecord(int uid1, int uid2) {
		MsgHead mh = new MsgHead();
		int Totalen = 13;
		byte type = 0x06;
		mh.setTotalLen(Totalen);
		mh.setType(type);
		mh.setDest(uid2);
		mh.setSrc(uid1);
	}

}
