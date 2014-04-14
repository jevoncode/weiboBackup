package com.jc.persistence.service;
 
import java.util.List;
import com.jc.core.domain.JcUser;
import com.jc.core.domain.JcStatus;

public interface WeiboPersistenceService{

	public int saveStatuses(List<JcStatus> statuses); 
	public List<JcStatus> getAllTop(JcUser jcUser);
	public int save(JcStatus s); 
}