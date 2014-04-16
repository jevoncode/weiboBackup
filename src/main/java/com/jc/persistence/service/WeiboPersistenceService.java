package com.jc.persistence.service;
 
import java.util.List;
import com.jc.core.domain.JcUser;
import com.jc.weibo4j.domain.Status;

public interface WeiboPersistenceService{

	public int saveStatuses(List<Status> statuses); 
	public List<Status> getAllTop(JcUser jcUser);
	public int save(Status s); 
}