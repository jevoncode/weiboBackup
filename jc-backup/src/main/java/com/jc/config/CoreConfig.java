package com.jc.config;

import com.jc.core.service.*;

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
	
	@Bean
	public IndexService indexService(){
		return new IndexServiceHandler();
	}
}