package com.jc.core.service;

import com.jc.core.domain.JcUser;

public interface WeiboService{
	
	public int obtainWeibo(JcUser jcUser);
	
	public String compositeWeibo(JcUser jcUser) ;
}