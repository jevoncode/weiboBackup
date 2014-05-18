package com.jc.persistence.service;

import com.jc.core.domain.JcUser;

public interface JcUserPersistenceService{
	
	public int save(JcUser jcUser);
	
	public JcUser getUserBySessionId(String sessionId) ;
	
	public JcUser getUserByToken(String token);

	public void delete(JcUser old);
}