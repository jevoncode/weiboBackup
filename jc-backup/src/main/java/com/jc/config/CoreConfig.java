package com.jc.config;

import com.jc.core.service.AuthorizationService;
import com.jc.core.service.AuthorizationServiceHandler;
import com.jc.core.service.WeiboService;
import com.jc.core.service.WeiboServiceHandler;

import com.jc.persistence.service.*;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig{
	
	@Bean
	public AuthorizationService authorizationService(JcUserPersistenceService jcUserPersistenceService){
		return new AuthorizationServiceHandler(jcUserPersistenceService);
	}
	
	@Bean
	public WeiboService weiboService(WeiboPersistenceService weiboPersistenceService,JcUserPersistenceService jcUserPersistenceService){
		return new WeiboServiceHandler(weiboPersistenceService,jcUserPersistenceService);
	}
}