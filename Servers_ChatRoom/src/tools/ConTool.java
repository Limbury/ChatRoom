package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConTool {
	private Connection conn=null;
	private Statement st=null;
	private ResultSet rs=null;
	public Connection getConnection()
	{
		try {
			//加载驱动程序
			Class.forName("com.mysql.cj.jdbc.Driver");
			//获得数据库连接
			conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/db_service?allowPublicKeyRetrieval=true&serverTimezone=GMT&useUnicode=true&characterEncoding=UTF-8&useOldAliasMetadataBehaior=true",
					"root","lyj123456");
			if(conn!=null)
			{
				System.out.println("数据库连接成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 释放数据库资源
	 * @param args
	 */
	public void release()
	{
		try {
			if(rs!=null)
			{
				rs.close();
			}
			if(st!=null)
			{
				st.close();
			}
			if(conn!=null)
			{
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("数据库连接关闭");
	}
	
	public ResultSet query (String sql) throws SQLException {
		conn=this.getConnection();
		st=conn.createStatement();
		rs = st.executeQuery(sql);
        return rs;
    }
	/*
	 * 返回更新影响的条目数量
	 */
    public int update(String sql){
    	int res=0;
    	try {
    		conn=this.getConnection();
    		st=conn.createStatement();
            res = st.executeUpdate(sql);
    	}catch (SQLException e) {
    		e.printStackTrace();
    	}finally {
    		release();
    	}
        return res;
    }
    public int insertAndGet(String sql) throws SQLException {
    	conn=this.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            int id = rs.getInt(1);
            release();
            return id;
        }
        release();
        return -1;
    }
    
	public static void main(String[] args) {
		ConTool db= new ConTool();
		db.getConnection();
		db.release();
	}

}
