package server;

import java.net.Socket;

public class ServerThread extends Thread {
	private Socket client;
	
	public ServerThread(Socket client) {
	        this.client = client;
	    }
}
