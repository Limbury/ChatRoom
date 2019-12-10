package tools;

import java.io.IOException;

import db.MsgPool;
import db.ThreadPool;
import msg.MsgChatText;
import server.ServerThread;

public class ChatTool {
	/*
	 * �������������UID���û���������ΪMsg����Ϣ
	 * 
	 * @return �Ƿ�ɹ�����
	 */
	public static boolean sendMsg(int from,int to, String msg) {
		/*
		 * Check User Online
		 */
		
		
		/*
		 * ���û�������Ϣ
		 */
		ServerThread st = ThreadPool.threadpool.get(String.valueOf(to));
		
		if(st == null ) {
			System.out.println("目标不在线");
			return false;
		}
		
		try {
			st.sendMsg(from, msg);
//			System.out.println("Finish Sendding");
			return true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * �����������JKNum���û������� ������JKNum���û���������ΪMsg����Ϣ
	 */
	public static void saveOnServer(int from,int to, String Msg) {
		MsgChatText mct = new MsgChatText(from, to, Msg);
		MsgPool.msgpool.add(mct);
	}
}
