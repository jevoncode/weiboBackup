package com.jc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import com.jc.model.User;
public class UserDao extends DaoBase{
	
	private PreparedStatement pstmt = null;
	public UserDao(){
		super();
	}
	
	public void saveUser(User user) throws SQLException{
		String sql = "insert into user "+
					"set user_session = ?,"+
					"user_code = ?,"+
					"access_token = ?,"+
					"expires_in = ?,"+
					"uid = ?,"+
					"created_time= ?;";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,user.getSession());
		pstmt.setString(2,user.getCode());
		pstmt.setString(3,user.getAccessToken());
		pstmt.setInt(4,user.getExpiresIn());
		pstmt.setLong(5,user.getUid());
		pstmt.setTimestamp(6,new Timestamp(user.getCreatedTime().getTime()));
		pstmt.executeUpdate();
		pstmt.close();
		conn.close();
	}
}
