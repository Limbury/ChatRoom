package msg;



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
}
