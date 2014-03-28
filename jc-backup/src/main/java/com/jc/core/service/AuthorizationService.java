package com.jc.core.service;


import com.jc.core.domain.JcUser;

public interface AuthorizationService{
	
	public String assembleOAuthURL();
	
	public JcUser obtainAuthorization(JcUser jcUser);
}