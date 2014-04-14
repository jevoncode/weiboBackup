package com.jc.persistence.service;

import com.jc.persistence.repository.Repository;
import com.jc.persistence.domain.JcConnection;
import com.jc.core.domain.JcUser;
import com.jc.core.domain.JcStatus;
import java.util.List;
import java.util.ArrayList; 

public class WeiboPersistenceMemoryService implements WeiboPersistenceService {
	
	public static final String TABLE_NAME = "Weibo";
	
	public Repository repository ;
	
	public WeiboPersistenceMemoryService(Repository repository){
		this.repository = repository;
	}
	
	
	@Override
	public int saveStatuses(List<JcStatus> statuses){
		JcStatus s = null;
		for(int i=0;i<statuses.size();i++){
			s = statuses.get(i);
			save(s);
		}
		return statuses.size();  
	}
	
	@Override
	public List<JcStatus> getAllTop(JcUser jcUser){
		List<JcStatus> statuses =null;
		JcConnection conn = repository.getConn();  
		List<Object> objects = conn.getAll(TABLE_NAME);
		if(objects!=null){
			statuses = new ArrayList<JcStatus>();
			for(Object o:objects){
				JcStatus js = (JcStatus)o;
				if(js.contains(jcUser))
					statuses.add(js);
			}
		}
		return statuses;
	}
	
	/**
	 * return id,which in database called ID,but in memory is called Index.
	 */
	@Override
	public int save(JcStatus s){
		JcConnection conn = repository.getConn(); 
		return conn.executeUpdate(TABLE_NAME,s); 
	} 
}