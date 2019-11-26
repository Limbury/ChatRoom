package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

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

}
