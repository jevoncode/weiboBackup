package com.jc.core.domain;

public class UserTask {
	public String key;
	public Thread task;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Thread getTask() {
		return task;
	}

	public void setTask(Thread task) {
		this.task = task;
	}

}
