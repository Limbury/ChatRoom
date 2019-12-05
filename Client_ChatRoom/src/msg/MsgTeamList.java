package msg;

public class MsgTeamList extends MsgHead {
	private String UserName;
	private int pic;
	private byte listCount;
	private String listName[];
	private byte bodyCount[];
	private int bodyNum[][];
	private int bodyPic[][];
	private String nikeName[][];
	private byte bodyState[][];
	
	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}
	public int getPic() {
		return pic;
	}

	public void setPic(int pic) {
		this.pic = pic;
	}
	public byte getListCount() {
		return listCount;
	}

	public void setListCount(byte listCount) {
		this.listCount = listCount;
	}

	public String[] getListName() {
		return listName;
	}

	public void setListName(String[] listName) {
		this.listName = listName;
	}

	public byte[] getBodyCount() {
		return bodyCount;
	}

	public void setBodyCount(byte[] bodyCount) {
		this.bodyCount = bodyCount;
	}

	public int[][] getBodyNum() {
		return bodyNum;
	}

	public void setBodyNum(int[][] bodyNum) {
		this.bodyNum = bodyNum;
	}

	public String[][] getNikeName() {
		return nikeName;
	}

	public void setNikeName(String[][] nikeName) {
		this.nikeName = nikeName;
	}

	public byte[][] getBodyState() {
		return bodyState;
	}

	public void setBodyState(byte[][] bodyState) {
		this.bodyState = bodyState;
	}

	public int[][] getBodyPic() {
		return bodyPic;
	}

	public void setBodyPic(int bodyPic[][]) {
		this.bodyPic = bodyPic;
	}
/*
	@Override
	public byte[] packMessage() throws IOException {
		ByteArrayOutputStream bous = new ByteArrayOutputStream();
		DataOutputStream dous = new DataOutputStream(bous);
		packMessageHead(dous);
		// 从mtl中获取信息
		String userName = getUserName();
		int pic = getPic();
		byte listCount = getListCount();
		String listName[] = getListName();
		byte bodyCount[] = getBodyCount();
		int bodyNum[][] = getBodyNum();
		int bodyPic[][] = getBodyPic();
		String nikeName[][] = getNikeName();
		byte bodyState[][] = getBodyState();

		// 开始写入流中
		writeString(dous, 10, userName);
		dous.writeInt(pic);
		dous.write(listCount);// 分组个数
		for (int i = 0; i < listCount; i++) {
			writeString(dous, 10, listName[i]);
			dous.write(bodyCount[i]);
			for (int j = 0; j < bodyCount[i]; j++) {// 每个组里面
				dous.writeInt(bodyNum[i][j]);
				dous.writeInt(bodyPic[i][j]);
				writeString(dous, 10, nikeName[i][j]);
				dous.write(bodyState[i][j]);
			}
		}
		dous.flush();
		byte[] data = bous.toByteArray();
		return data;
	}
*/
}
