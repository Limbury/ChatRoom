package tools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import msg.MsgAddFriend;
import msg.MsgAddFriendResp;
import msg.MsgChatText;
import msg.MsgHead;
import msg.MsgLogin;
import msg.MsgLoginResp;
import msg.MsgReg;
import msg.MsgRegResp;
import msg.MsgTeamList;

public class ParseTool {
	/*
	 * 从流中读取固定长度的字节，并将其转换为字符串
	 * 
	 * @param dins: 读取的流对象
	 * 
	 * @param len: 读取的字节长度
	 * 
	 * @return 转换后的字符串
	 */
	private static String readString(DataInputStream dins, int len) throws IOException {
		byte[] data = new byte[len];
		dins.readFully(data);
		return new String(data).trim();
	}

	/*
	 * 此方法用于解构消息
	 * 
	 * @param data 字节信息
	 * 
	 * @return 消息解构后赋值的MsgHead的子类
	 */
	public static MsgHead parseMsg(byte[] data) throws IOException {
		int totalLen = data.length + 4; // 之前已经读取了4个字节的长度信息
		ByteArrayInputStream bins = new ByteArrayInputStream(data);
		DataInputStream dins = new DataInputStream(bins);
		byte msgtype = dins.readByte();
		int dest = dins.readInt();
		int src = dins.readInt();
		if (msgtype == 0x01) {// 如果是注册信息
			String nikeName = readString(dins, 10);
			String pwd = readString(dins, 10);
			MsgReg mr = new MsgReg();
			mr.setTotalLen(totalLen);
			mr.setType(msgtype);
			mr.setDest(dest);
			mr.setSrc(src);
			mr.setNikeName(nikeName);
			mr.setPwd(pwd);
			return mr;
		}

		else if (msgtype == 0x11) { // 如果是注册返回信息
			byte state = dins.readByte();
			MsgRegResp mrr = new MsgRegResp();
			mrr.setTotalLen(totalLen);
			mrr.setType(msgtype);
			mrr.setDest(dest);
			mrr.setSrc(src);
			mrr.setState(state);
			return mrr;
		}

		else if (msgtype == 0x02) { // 如果是登陆信息
			String pwd = readString(dins, 10);
			MsgLogin mli = new MsgLogin();
			mli.setTotalLen(totalLen);
			mli.setType(msgtype);
			mli.setDest(dest);
			mli.setSrc(src);
			mli.setPwd(pwd);
			return mli;
		}

		else if (msgtype == 0x22) { // 如果是登陆返回信息
			byte state = dins.readByte();
			MsgLoginResp mlr = new MsgLoginResp();
			mlr.setTotalLen(totalLen);
			mlr.setType(msgtype);
			mlr.setDest(dest);
			mlr.setSrc(src);
			mlr.setState(state);
			return mlr;
		}

		else if (msgtype == 0x03) { // 如果是好友列表
			int i, j;

			String UserName = readString(dins, 10);// 读取用户名
			int pic = dins.readInt();
			byte listCount = dins.readByte();// 读取好友分组个数
			String listName[] = new String[listCount];
			byte bodyCount[] = new byte[listCount];

			int bodyNum[][];
			bodyNum = new int[listCount][];// 设置第一维长度
			
			int bodyPic[][];
			bodyPic = new int[listCount][];

			String nikeName[][];
			nikeName = new String[listCount][];// 设置第一维长度

			byte bodyState[][];
			bodyState = new byte[listCount][];// 设置第一维长度

			for (i = 0; i < listCount; i++) {
				listName[i] = readString(dins, 10);
				bodyCount[i] = dins.readByte();
				
				bodyNum[i] = new int[bodyCount[i]];// 设置第二维长度
				bodyPic[i] = new int[bodyCount[i]];
				nikeName[i] = new String[bodyCount[i]];// 设置第二维长度
				bodyState[i] = new byte[bodyCount[i]];// 设置第二维长度

				for (j = 0; j < bodyCount[i]; j++) {
					bodyNum[i][j] = dins.readInt();
					bodyPic[i][j] = dins.readInt();
					nikeName[i][j] = readString(dins, 10);
					bodyState[i][j] = dins.readByte();
					System.out.println(bodyNum[i][j]+" "+bodyPic[i][j]+" "+nikeName[i][j]+" "+bodyState[i][j]);
				}

			}

			/*
			 * 读取结束，写入对象
			 */
			MsgTeamList mtl = new MsgTeamList();
			mtl.setUserName(UserName);
			mtl.setPic(pic);
			mtl.setTotalLen(totalLen);
			mtl.setType(msgtype);
			mtl.setDest(dest);
			mtl.setSrc(src);
			mtl.setListCount(listCount);
			mtl.setListName(listName);
			mtl.setBodyCount(bodyCount);
			mtl.setBodyNum(bodyNum);
			mtl.setBodyPic(bodyPic);
			mtl.setNikeName(nikeName);
			mtl.setBodyState(bodyState);

			return mtl;

		}
		
		else if (msgtype == 0x04) { //如果是传送消息
			MsgChatText mct = new MsgChatText();
			String msgText = readString(dins, totalLen-13);
			mct.setTotalLen(totalLen);
			mct.setType(msgtype);
			mct.setDest(dest);
			mct.setSrc(src);
			mct.setMsgText(msgText);
			
			return mct;
		}
		
		else if (msgtype == 0x05){ //添加好友
			MsgAddFriend maf = new MsgAddFriend();
			int add_id = dins.readInt();
			String list_name = readString(dins, totalLen - 17);
			maf.setTotalLen(totalLen);
			maf.setType(msgtype);
			maf.setDest(dest);
			maf.setSrc(src);
			maf.setAdd_ID(add_id);
			maf.setList_name(list_name);
			return maf;
		}
		
		else if (msgtype == 0x55){ //添加好友回执
			MsgAddFriendResp mafr = new MsgAddFriendResp();
			byte state = dins.readByte();
			mafr.setTotalLen(totalLen);
			mafr.setType(msgtype);
			mafr.setDest(dest);
			mafr.setSrc(src);
			mafr.setState(state);
			return mafr;
		}
		
		return null;

	}
}
