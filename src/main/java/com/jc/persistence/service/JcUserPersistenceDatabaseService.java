package com.jc.persistence.service;

import com.jc.core.domain.JcUser;

public class JcUserPersistenceDatabaseService implements JcUserPersistenceService{
	@Override
	public int save(JcUser user){
		//TODO
		return 0; 
	}
	@Override
	public JcUser getUserBySessionId(String sessionId) {
		//TODO
		return null;
	}
	
	@Override
	public JcUser getUserByToken(String token){ 
		return null;
	}
}