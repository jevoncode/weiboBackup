package com.jc.persistence.service;

import com.jc.persistence.repository.Repository;
import com.jc.persistence.domain.JcConnection;
import com.jc.core.domain.JcUser;

import java.util.Iterator;
import java.util.List;

public class JcUserPersistenceMemoryService implements JcUserPersistenceService {
	public static final String TABLE_NAME = "JcUser";

	public Repository repository;

	public JcUserPersistenceMemoryService(Repository repository) {
		this.repository = repository;
	}

	/**
	 * return id,which in database called ID,but in memory is called Index.
	 */
	@Override
	public int save(JcUser jcUser) {
		JcConnection conn = repository.getConn();
		// JcUser old = getUserByToken(jcUser.getAccessToken());
		// if(old!=null){
		// old.setBackupComment(jcUser.isBackupComment());
		// old.setBackupLarge(jcUser.isBackupLarge());
		// old.setBackupThumbnail(jcUser.isBackupThumbnail());
		// old.setCode(jcUser.getCode());
		// old.setCommentCount(jcUser.getCommentCount());
		// old.setIpAddress(jcUser.getIpAddress());
		// old.setSession(jcUser.getSession());
		// old.setVerificationCode();
		// List<Object> objects= conn.getAll(TABLE_NAME);
		// for(int i=0;i<objects.size();i++){
		// JcUser u = (JcUser)objects.get(i);
		// objects.set(i, u);
		// }
		// }

		return conn.executeUpdate(TABLE_NAME, jcUser);
	}

	@Override
	public JcUser getUserBySessionId(String sessionId) {
		JcConnection conn = repository.getConn();
		List<Object> objects = conn.getAll(TABLE_NAME);
		for (Object o : objects) {
			JcUser u = (JcUser) o;
			if (sessionId.equals(u.getSession()))
				return u;
		}
		return null;
	}

	@Override
	public JcUser getUserByToken(String token) {
		if (token == null || token.length() == 0)
			return null;
		JcConnection conn = repository.getConn();
		List<Object> objects = conn.getAll(TABLE_NAME);
		if (objects == null)
			return null;
		for (Object o : objects) {
			JcUser u = (JcUser) o;
			if (token.equals(u.getAccessToken()))
				return u;
		}
		return null;
	}

	@Override
	public void delete(JcUser old) {
		JcConnection conn = repository.getConn();
		List<Object> objects = conn.getAll(TABLE_NAME);
		for (int i = 0; i < objects.size(); i++) {
			JcUser u = (JcUser) objects.get(i);
			if (u.getAccessToken().equals(old.getAccessToken())) {
				objects.remove(i);
				break;
			}
		}

	}
}