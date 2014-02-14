package com.jc.dao;

import java.sql.Connection;
import java.util.List;
import weibo4j.model.Status;
public class WeiboDAO{
	private Database database = null;
	private Connection conn = null;
	public WeiboDAO(){
		database = new Database();
		conn = database.getConnection();
	}
	public void saveStatuses(List<Status> statuses){

	}
}
