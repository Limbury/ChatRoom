package msg;


import db.Server_id;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
 * 服务器应答注册状态消息体
 */
public class MsgRegResp extends MsgHead {
	private byte state;

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public MsgRegResp() {}

	public MsgRegResp(int jkDest, byte state) {
		setTotalLen(14);
		setType((byte)0x11);
		setDest(jkDest);
		setSrc(Server_id.ServerID);
		setState(state);
	}
	@Override
	public byte[] packMessage() throws IOException {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		packMessageHead(dous);
		dous.write(getState());
		dous.flush();
		byte[] data = bous.toByteArray();
		return data;
	}

}
