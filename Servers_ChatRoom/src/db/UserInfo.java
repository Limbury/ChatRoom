package db;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserInfo {
	/*
	 * 用户基本信息
	 */

	private int UID;// 保存该用户的UID号
	private String nickName;// 保存该用户的昵称
	private int avatar;

	UserInfo(ResultSet userResult) throws SQLException {
	    UID = userResult.getInt("uid");
	    nickName = userResult.getString("nickname");
	    avatar = userResult.getInt("avatar");
    }

	/*
	 * 用户好友信息
	 */

	private byte collectionCount;// 保存有多少组好友
	private String ListName[];// 保存每个分组的名称
	private byte[] bodyCount;// 每组有多少个人
	private int bodyNum[][];// 每个好友的UID号
	private int bodypic[][];//好友头像
	private String bodyName[][];// 每个好友的昵称

	public byte getCollectionCount() {
		return collectionCount;
	}

	public void setCollectionCount(byte listCount) {
		this.collectionCount = listCount;
	}

	public String[] getListName() {
		return ListName;
	}

	public void setListName(String[] listName) {
		ListName = listName;
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

	public String[][] getBodyName() {
		return bodyName;
	}

	public void setBodyName(String[][] bodyName) {
		this.bodyName = bodyName;
	}

	public int getUID() {
		return UID;
	}

	public void setUID(int uid) {
		UID = uid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nick) {
		nickName = nick;
	}

	public boolean equals(UserInfo compare) {
		if (compare.getUID() == UID/* && compare.getPassWord().equals(passWord)*/) {
			return true;
		}
		return false;
	}
  
	public int[][] getBodypic() {
		return bodypic;
	}

	public void setBodypic(int bodypic[][]) {
		this.bodypic = bodypic;
	}

	public int getAvatar() {
		return avatar;
	}

	public void setAvatar(int avatar) {
		this.avatar = avatar;
	}
}
