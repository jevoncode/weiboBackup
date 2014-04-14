package com.jc.core.domain;

import weibo4j.model.Status;

/**
 * extends weibo4j's status, let it contains JcUser so that can be recognised.
 * @author JevonCode
 *
 */
public class JcStatus extends Status{
	JcUser jcUser = null;
	
	public void setJcUser(JcUser jcUser){
		this.jcUser = jcUser;
	}
	
	public JcUser getJcUser(){
		return jcUser;
	}
	
	
	public boolean contains(JcUser jcUser){
		return this.jcUser.getVerificationCode().equals(jcUser.getVerificationCode());
	}
}

