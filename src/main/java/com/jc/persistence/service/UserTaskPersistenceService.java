package com.jc.persistence.service;

import com.jc.core.domain.UserTask;

public interface UserTaskPersistenceService {
	public void save(UserTask userTask);
	public void delete(UserTask userTask);
	public UserTask getUserTaskByKey(String key);
}
