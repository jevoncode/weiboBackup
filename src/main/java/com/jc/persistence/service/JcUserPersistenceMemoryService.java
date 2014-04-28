package com.jc.persistence.service;

import com.jc.persistence.repository.Repository;
import com.jc.persistence.domain.JcConnection;
import com.jc.core.domain.JcUser;

import java.util.List;

public class JcUserPersistenceMemoryService implements JcUserPersistenceService{
	public static final String TABLE_NAME = "JcUser";
	
	public Repository repository ;
	
	public JcUserPersistenceMemoryService(Repository repository){
		this.repository = repository;
	}
	
	/**
	 * return id,which in database called ID,but in memory is called Index.
	 */
	@Override
	public int save(JcUser jcUser){
		JcConnection conn = repository.getConn();
		return conn.executeUpdate(TABLE_NAME,jcUser);
	}
	
	@Override
	public JcUser getUserBySessionId(String sessionId){
		JcConnection conn = repository.getConn();
		List<Object> objects= conn.getAll(TABLE_NAME);
		for(Object o:objects){
			JcUser u = (JcUser)o;
			if(sessionId.equals(u. getSession()))
				return u;
		}
		return null;
	}
	
	@Override
	public JcUser getUserByToken(String token){
		JcConnection conn = repository.getConn();
		List<Object> objects= conn.getAll(TABLE_NAME);
		for(Object o:objects){
			JcUser u = (JcUser)o;
			if(token.equals(u. getAccessToken()))
				return u;
		}
		return null;
	}
}