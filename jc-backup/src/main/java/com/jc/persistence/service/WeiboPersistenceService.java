package com.jc.persistence.service;

import weibo4j.model.Status;
import java.util.List;
import com.jc.core.domain.JcUser;

public interface WeiboPersistenceService{

	public int saveStatuses(List<Status> statuses); 
	public List<Status> getAllTop(JcUser jcUser);
	public int save(Status s); 
}