package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tools.ConTool;

public class RecordModi {
	public List<RecordInfo> getRecordByUID(int uid1,int uid2) throws SQLException{
		ConTool contool=new ConTool();
        ResultSet rs = contool.query("SELECT * FROM history where uid1=" + uid1+" AND uid2="+uid2);
        ArrayList<RecordInfo> res = new ArrayList<>();
        while (rs.next()) {
            res.add(new RecordInfo(rs));
        }
        contool.release();
        return res;
	}
	
	public int inserthistry(int uid1,int uid2,String a) throws SQLException{
		ConTool contool=new ConTool();
		String sql = String.format("INSERT INTO history (uid1, uid2, text) VALUES (%d, %d, '%s')", uid1, uid2, a);
		System.out.println(sql);
		return contool.update(sql);
	}
	
	public static void main(String[] args) {
		RecordModi tmp = new RecordModi();
		try {
			tmp.inserthistry(1, 2, "你好");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<RecordInfo> a;
		try {
			a = tmp.getRecordByUID(1, 2);
			for(int i=0;i<a.size();i++) {
				System.out.println(a.get(i).text);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
