package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import dataBase.Figures;
import dataBase.ListInfo;
import msg.MsgAddFriendResp;
import msg.MsgChatText;
import msg.MsgHead;
import tools.DialogTool;
import tools.PackageTool;
import tools.ParseTool;

public class Client_ChatRoom extends Thread {
	private String serverip;
	private int port;
	private Socket client;
	
	private InputStream receive;
	private OutputStream send;
	
	public Client_ChatRoom(String serverip, int port) {
		super();
		this.serverip = serverip;
		this.port = port;
	}
	/*
	 * 链接服务器
	*/	
	public boolean Connect() {
		try {
			client = new Socket(serverip, port);
			System.out.println("服务器已连接");
			receive= client.getInputStream();
			send = client.getOutputStream();// 获取该连接的输入输出流
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return false;
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
				JOptionPane.showMessageDialog(null, "与服务器断开连接", "ERROR", JOptionPane.ERROR_MESSAGE);
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
			DialogTool.ShowMessage(from, Msg);
		}
		else if(MsgType == 0x03){//更新好友列表
			System.out.println("Refresh list");
			ListInfo list = packlist(recMsg);
			Figures.list.Refresh_List(list);
		}
		if (MsgType == 0x55){
//			System.out.println("Here");
			MsgAddFriendResp mafr = (MsgAddFriendResp) recMsg;
			byte result = mafr.getState();
			System.out.println("Add Friend Result "+result);
			if(Figures.afu != null){
//				System.out.println("To show Result");
				Figures.afu.showResult(result);
			}
		}
	}

}
