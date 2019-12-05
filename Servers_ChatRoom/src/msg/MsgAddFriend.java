package msg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MsgAddFriend extends MsgHead{
	private int add_ID;
	private String list_name;

	public int getAdd_ID() {
		return add_ID;
	}

	public void setAdd_ID(int add_ID) {
		this.add_ID = add_ID;
	}

	public String getList_name() {
		return list_name;
	}

	public void setList_name(String list_name) {
		this.list_name = list_name;
	}
	@Override
	public byte[] packMessage() throws IOException {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		packMessageHead(dous);
		dous.writeInt(getAdd_ID());
		writeString(dous,getTotalLen() -  17,getList_name());
		dous.flush();
		byte[] data = bous.toByteArray();
		return data;
	}

}
