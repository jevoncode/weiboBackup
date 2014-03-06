package com.jc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jc.model.User;
public class UserDao extends DaoBase{
	
	private PreparedStatement pstmt = null;
	private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);
	public UserDao(){
		super();
	}
	
	public void saveUser(User user){
		LOG.debug("jc Begin to save myuser,user_code="+user.getCode());
		try{
		String sql = "insert into myuser "+
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
		}catch(SQLException e){
			LOG.error("ocurrence error when save user,user_code:"+user.getCode());
			e.printStackTrace();
		}
	}
	public User getUserBySessionId(String sessionId) throws SQLException{
		LOG.debug("Begin getUserBySessionId");
		User user = null;
		String sql = "SELECT * FROM myuser u where u.user_session = ?;";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,sessionId);
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			user = new User();
			user.setId(rs.getLong(1));
			user.setSession(rs.getString(2));
			user.setCode(rs.getString(3));
			user.setAccessToken(rs.getString(4));
			user.setExpiresIn(rs.getInt(5));
			user.setUid(rs.getLong(6));
			user.setCreatedTime(new Date(rs.getTimestamp(7).getTime()));	
			LOG.debug("find a User:Id="+user.getId()+
						",Session="+user.getSession()+
						",Code="+user.getCode()+
						",AccessToken="+user.getAccessToken()+
						",ExpireIn="+user.getExpiresIn()+
						",Uid="+user.getUid()+
						",CreatedTime="+user.getCreatedTime());
		}
		rs.close();
		pstmt.close();
		conn.close();
		return user;
	}
}
