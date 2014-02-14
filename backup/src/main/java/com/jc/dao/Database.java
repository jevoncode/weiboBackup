package com.jc.dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
public class Database{
	private static String url = "jdbc:mysql://127.0.0.1:3306/weibobackup";
	private static String className = "com.mysql.jdbc.Driver";
	private static String userName = "weibo";
	private static String password = "hwj2yjy";
	private Connection conn = null;

	static{
		try{
			Class.forName(className);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(){
		if(conn==null){
			try{
				conn = DriverManager.getConnection(url,userName,password);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		return conn;
	}

}
