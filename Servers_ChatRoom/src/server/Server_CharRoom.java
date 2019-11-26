package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_CharRoom {
	ServerSocket server_chat;
	
	public void setupServer(int port) {
		try {
			server_chat = new ServerSocket(port);
			System.out.println("服务器启动成功");
			while (true) {
				Socket client = server_chat.accept();
				System.out.println("Incoming" + client.getRemoteSocketAddress());
				ServerThread st = new ServerThread(client);
				st.start();
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeServer() {
		try {
			server_chat.close();
			System.out.println("服务器关闭成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("服务器测试");
		Server_CharRoom tmp = new Server_CharRoom();
		tmp.setupServer(6666);
		tmp.closeServer();
	}
}
