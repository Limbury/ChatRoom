package db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RecordInfo {
	RecordInfo(ResultSet rs) throws SQLException {
        uid1 = rs.getInt("uid1");
        uid2 = rs.getInt("uid2");
        text = rs.getString("text");
    }
	public int getUid1() {
		return uid1;
	}
	public void setUid1(int uid1) {
		this.uid1 = uid1;
	}
	public int getUid2() {
		return uid2;
	}
	public void setUid2(int uid2) {
		this.uid2 = uid2;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	int uid1;
	int uid2;
	String text;
	
	

}
