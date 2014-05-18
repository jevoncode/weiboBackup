package com.jc.config;

import com.jc.core.service.*;
import com.jc.persistence.service.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig{
	
	@Bean
	public AuthorizationService authorizationService(JcUserPersistenceService jcUserPersistenceService,UserTaskPersistenceService userTaskPersistenceService){
		return new AuthorizationServiceHandler(jcUserPersistenceService,userTaskPersistenceService);
	}
	
	@Bean
	public WeiboService weiboService(WeiboPersistenceService weiboPersistenceService,JcUserPersistenceService jcUserPersistenceService,UserTaskPersistenceService userTaskPersistenceService){
		return new WeiboServiceHandler(weiboPersistenceService,jcUserPersistenceService,userTaskPersistenceService);
	}
	
	@Bean
	public IndexService indexService(){
		return new IndexServiceHandler();
	}
}