package com.jc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import weibo4j.Timeline;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import com.jc.model.User; 
import com.jc.dao.WeiboDao; 
import com.jc.dao.UserDao;
public class WeiboManager{
	private static final Logger LOG = LoggerFactory.getLogger(WeiboManager.class);
	private WeiboDao weiboDao = new WeiboDao(); 
	private int countPerPage = 100;
	public boolean swoopWeibo(String sessionId) throws WeiboException{ 
		int swoopCount = 0;
		User user = null;
		int limitCount = 0;
		try{
			user = (new UserDao()).getUserBySessionId(sessionId);
		}catch(SQLException e){
			//LOG.error(e.printStackTrace());
			LOG.error(e.getMessage());
		}
		if(user == null)
			return false;
		Timeline tm = new Timeline();
		tm.client.setToken(user.getAccessToken()); 
		StatusWapper status = tm.getUserTimeline(countPerPage,1);
		LOG.debug(++limitCount+"st to invoke UserTimeline interface.");
		weiboDao.saveStatuses(status.getStatuses()); 
		swoopCount += status.getStatuses().size();
		int total = (int)(status.getTotalNumber());
		int totalPage = total%100 ==0? total/100:total/100+1;
		int page = 2;
		//for(;page<totalPage;page++){
		for(;page<totalPage;page++){
			status = tm.getUserTimeline(countPerPage,page);
			LOG.debug(++limitCount+"st to invoke UserTimeline interface.");
			weiboDao = new WeiboDao(); //TODO optimize
			weiboDao.saveStatuses(status.getStatuses()); 
			swoopCount += status.getStatuses().size();
		}
		LOG.debug("swoops "+swoopCount+" Weibos");
		LOG.debug("saves "+WeiboDao.saveCount+" Weibos");
		return true;
	}

}
