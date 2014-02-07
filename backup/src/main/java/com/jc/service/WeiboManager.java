package com.jc.service;

import weibo4j.TimeLine;
import weibo4j.model.StatusWapper;
import com.jc.model.User; //TODO
import com.jc.dao.WeiboDao; //TODO
public class WeiboManager{
	private WeiboDAO weiboDAO = new WeiboDAO(); //TODO
	private int countPerPage = 100;
	public boolean swoopWeibo(User user) throws WeiboException{ //TODO
		Timeline tm = new Timeline();
		tm.client.setToken(user.getAccessToken()); //TODO
		StatusWapper status = tm.getUserTimeline(countPerPage,1);
		weiboDAO.saveStatuses(status.getStatuses()); //TODO
		int total = (int)(status.getTotalNumber());
		int totalPage = total%100 ==0? total/100:total/100+1;
		int page = 2;
		for(;page<totalPage;page++){
			status = tm.getUserTimeline(countPerPage,page);
			weiboDAO.saveStatuses(status.getStatuses()); //TODO
		}
		
	}

}
