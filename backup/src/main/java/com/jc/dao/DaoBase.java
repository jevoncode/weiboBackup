package com.jc.dao;
import java.sql.Connection;
public class DaoBase{
	protected Database database = null;
	protected Connection conn = null;
	public DaoBase(){
		database = new Database();
		conn = database.getConnection();
	}
}	
