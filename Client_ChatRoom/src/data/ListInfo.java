package data;

import data.UserInfo;

public class ListInfo extends UserInfo{
	private byte[][] bodyState;

	public byte[][] getBodyState() {
		return bodyState;
	}

	public void setBodyState(byte[][] bodyState) {
		this.bodyState = bodyState;
	}
}
