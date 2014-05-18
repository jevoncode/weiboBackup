package com.jc.persistence.service;

import java.util.List;

import com.jc.core.domain.JcUser;
import com.jc.core.domain.UserTask;
import com.jc.persistence.domain.JcConnection;
import com.jc.persistence.repository.Repository;

public class UserTaskPersistenceMemoryService implements UserTaskPersistenceService {

	public static final String TABLE_NAME = "UserTask";

	private Repository repository;

	public UserTaskPersistenceMemoryService(Repository repository) {
		this.repository = repository;
	}

	@Override
	public void save(UserTask userTask) {
		JcConnection conn = repository.getConn();
		conn.executeUpdate(TABLE_NAME, userTask);
	}

	@Override
	public void delete(UserTask userTask) {
		JcConnection conn = repository.getConn();
		List<Object> objects = conn.getAll(TABLE_NAME);
		for (int i = 0; i < objects.size(); i++) {
			UserTask t = (UserTask) objects.get(i);
			if (t.getKey().equals(userTask.getKey())) {
				objects.remove(i);
				break;
			}
		}
	}

	@Override
	public UserTask getUserTaskByKey(String key) {
		if(key==null||key.length()==0)
			return null;

		JcConnection conn = repository.getConn();
		List<Object> objects = conn.getAll(TABLE_NAME);
		if (objects == null)
			return null;
		for (Object o : objects) {
			UserTask u = (UserTask) o;
			if (key.equals(u.getKey()))
				return u;
		}
		return null;
	}

}
