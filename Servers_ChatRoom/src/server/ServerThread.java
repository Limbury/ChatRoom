package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import db.MsgPool;
import db.RecordInfo;
import db.RecordModi;
import db.Server_id;
import db.ThreadPool;
import db.UserInfo;
import db.UserModi;
import msg.MsgAddFriend;
import msg.MsgAddFriendResp;
import msg.MsgChatText;
import msg.MsgHead;
import msg.MsgLogin;
import msg.MsgLoginResp;
import msg.MsgReg;
import msg.MsgRegResp;
import msg.MsgTeamList;
import tools.ChatTool;
import tools.ParseTool;
import tools.ThreadRegDelTool;

public class ServerThread extends Thread {
	public boolean isSending = false;	//
    private Socket client;	//����ӿ�	
    private OutputStream ous;	//�����
    private int userid;		//Ψһ��ʶ
    private boolean isOnline = false;	//�Ƿ�����
	
	public ServerThread(Socket client) {
	        this.client = client;
	}
	
	public int getUserid() {
        return userid;
    }
	/*
	 * �����߳�
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
        while (!isOnline) { // ���߳��пͻ���δ��½
            try {
                processLogin();
            } catch (Exception e) {

				/*
                 * �ͻ��˶Ͽ�����
				 */
                System.out.println(client.getRemoteSocketAddress() + "�ѶϿ�");
                isOnline = false;

                try {
                    client.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
        while (isOnline) { // ���߳��пͻ����ѵ�½
            //��ʼ�����б�
            try {
                processChat();
            } catch (Exception e) {
				/*
				 * �ͻ��˶Ͽ�����
				 */
                System.out.println(client.getRemoteSocketAddress() + "断开");
                ThreadRegDelTool.DelThread(userid);// ���߳����ݿ��м�ɾ��������Ϣ
                isOnline = false;
                try {
                    broadcastState();
                } catch (SQLException | IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
                try {
                    client.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }
        }
    }
	
	/*
	 * �ͻ��˵�¼����
	 */
	private void processLogin() throws Exception {
        //connect to DataBase
        UserModi model = new UserModi();

        ous = client.getOutputStream();
        InputStream ins = client.getInputStream();
        DataInputStream dis = new DataInputStream(ins);

        MsgHead msg = MsgHead.readMessageFromStream(dis);

		/*
		 * ��������Բ�ͬ����Ϣ���д���
		 */

        // �������������ע����Ϣ
        if (msg.getType() == 0x01) {
            MsgReg mr = (MsgReg) msg;

            // ע���û�

            UserInfo newUser = model.createUser(mr.getPwd(), mr.getNikeName(), 1);
            int newuid = newUser.getUID();

			/*
			 * ������׼��������Ϣ
			 */
            byte state = 0;
            MsgRegResp mrr = new MsgRegResp(newuid, state);
            mrr.send(ous);
        }

        // ����������ǵ�½��Ϣ
        else if (msg.getType() == 0x02) {
            MsgLogin ml = (MsgLogin) msg;

            byte checkmsg;// ��������״̬��Ϣ
            if (ThreadPool.threadpool.containsKey(String.valueOf(ml.getSrc()))) {//�Ѿ�������
                checkmsg = 2;
            } else if (model.userAuthorization(ml.getSrc(), ml.getPwd())) {// �����֤���û�����
                checkmsg = 0;
            } else {
                checkmsg = 1;
            }

			/*
			 * ������׼��������Ϣ
			 */
            MsgLoginResp mlr = new MsgLoginResp(checkmsg);
            mlr.send(ous);

			/*
			 * �����½������ɣ� ���ͺ����б�
			 */
            if (checkmsg == 0) {
                userid = ml.getSrc();
                ThreadRegDelTool.RegThread(this); // ���߳����ݿ���ע������߳�
                sendFriendList();
                sendUnacessMsg();
                isOnline = true;// �����ѵ�¼�ͻ���
                broadcastState();
            }

        }
    }
	/*
	 * ���º����б��б���״̬
	 */
	private void broadcastState() throws SQLException, IOException {
        UserModi model = new UserModi();
        UserInfo user = model.getUserByUID(userid);
        for (int i = 0; i < user.getCollectionCount(); i++) {
            for (int j = 0; j < user.getBodyCount()[i]; j++) {
                ServerThread st = ThreadPool.threadpool.get(String.valueOf(user.getBodyNum()[i][j]));
                if (st != null) {
                    st.sendFriendList();
                }
            }
        }
    }
	
	private void sendFriendList() throws IOException, SQLException {
        System.out.println("发送好友列表");

       
        UserModi model = new UserModi();
        UserInfo user = model.getUserByUID(userid);
        MsgTeamList mtl = new MsgTeamList(user);
        mtl.send(ous);
    }
	
	private void sendUnacessMsg() throws IOException, SQLException {
        System.out.println("上线拉取未接受消息");
        for(int i=0;i<MsgPool.msgpool.size();i++) {
        	MsgChatText mct=MsgPool.msgpool.get(i);
        	if(mct.getDest() == userid) {
        		mct.send(ous);
        		mct.setDest(-1);
        	}
        }
        UserModi model = new UserModi();
        UserInfo user = model.getUserByUID(userid);
        MsgTeamList mtl = new MsgTeamList(user);
        mtl.send(ous);
    }
	
	public void processChat() throws Exception {
        InputStream ins = client.getInputStream();
        DataInputStream dis = new DataInputStream(ins);

        int totalLen = dis.readInt();
        byte[] data = new byte[totalLen - 4];
        dis.readFully(data);
        MsgHead msg = ParseTool.parseMsg(data);// �������Ϣ

		/*
		 * ��������Բ�ͬ����Ϣ���д���
		 */

        if (msg.getType() == 0x04) {//����յ����Ƿ�����Ϣ����
            MsgChatText mct = (MsgChatText) msg;
            int from = mct.getSrc();
            int to = mct.getDest();
            String msgText = mct.getMsgText();
//			System.out.println("Sending Test!!");
//			System.out.println("From "+from+" To "+to+" Text "+msgText);
            RecordModi tmp = new RecordModi();
            tmp.inserthistry(from, to, msgText);
            tmp.inserthistry(to, from, msgText);
            if (!ChatTool.sendMsg(from, to, msgText)) {
                System.out.println("SaveOnServer");

                //���浽��������
                ChatTool.saveOnServer(from, to, msgText);
            }
        } else if (msg.getType() == 0x05) {//����ܵ���Ӻ��ѵ�����
            System.out.println("Add friend request");
            MsgAddFriend maf = (MsgAddFriend) msg;
            int own_jk = maf.getSrc();
            int add_jk = maf.getAdd_ID();
            String list_name = maf.getList_name();
            UserModi model = new UserModi();
            int result = model.addFriend(add_jk, own_jk, list_name);
            System.out.println("Add finish " + result);
            MsgAddFriendResp mafr = new MsgAddFriendResp();
            mafr.setSrc(Server_id.ServerID);
            mafr.setDest(own_jk);
            mafr.setTotalLen(14);
            mafr.setType((byte) 0x55);
            if (result == 0) {//success
                model.addFriend(own_jk, add_jk, "newfriend");
                //send add_jk new list
                mafr.setState((byte) 0);
                //send own_jk new list
            } else if (result == 1) {//�����������
                mafr.setState((byte) 1);
            } else if (result == 2) {//����Ѿ������������
                mafr.setState((byte) 2);
            } else if (result == 3) {//�����б�ʧ��
                mafr.setState((byte) 3);
            }
            mafr.send(ous);

            sendFriendList();

            //send Add_JK Friend list
            //model.addFriend(own_jk, add_jk, list_name);
            //��������߸����б�
            ServerThread st = ThreadPool.threadpool.get(String.valueOf(add_jk));
            if (st != null) {
                st.sendFriendList();
            }
        }
        else if(msg.getType() == 0x06) {
        	int uid1=msg.getSrc();
        	int uid2=msg.getDest();
        	sendhistory(uid1,uid2);
        }
    }
	
	public void sendMsg(int from, String msg) throws IOException {
        MsgChatText mct = new MsgChatText(from, userid, msg);
        mct.send(ous);
    }
	
	public void sendhistory(int uid1,int uid2){ //requst from uid1, he is looking uid2
		RecordModi tmp = new RecordModi();
		List<RecordInfo> a;
		try {
			a = tmp.getRecordByUID(uid1, uid2);
			for(int i=0;i<a.size();i++) {
				int from = a.get(i).getUid1();
	            int to = a.get(i).getUid2();
	            String msgText = a.get(i).getText();
	            MsgChatText mct = new MsgChatText(from, to, msgText);
	            mct.setType((byte) 0x66);
	            mct.send(ous);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("no more message");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("send erro");
		}
	}
}
