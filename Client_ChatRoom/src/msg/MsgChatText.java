package msg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MsgChatText extends MsgHead {
	private String msgText;

	public String getMsgText() {
		return msgText;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	public MsgChatText() {}
	public MsgChatText(int from, int target, String text) {
		int totalLen = 13;
		byte msgType = 0x04;
		byte[] data = text.getBytes();

		totalLen += data.length;

		setTotalLen(totalLen);
		setType(msgType);
		setDest(target);
		setSrc(from);
		setMsgText(text);
	}
	@Override
	public byte[] packMessage() throws IOException {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		packMessageHead(dous);
		writeString(dous, getTotalLen() - 13, getMsgText());
		dous.flush();
		byte[] data = bous.toByteArray();
		return data;
	}

}
