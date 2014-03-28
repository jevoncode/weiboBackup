package com.jc.config;
 
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.jc.persistence.service.*;
import com.jc.persistence.repository.*;
import com.jc.persistence.domain.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig{
	
	@Bean
	public JcUserPersistenceService jcUserPersistenceService(Repository repository){
		return new JcUserPersistenceMemoryService(repository);
	} 
	
	@Bean
	public WeiboPersistenceService WeiboPersistenceService(Repository repository){
		return new WeiboPersistenceMemoryService(repository);
	} 
	
	
	@Bean
	public Repository repository(){
		return new MemoryRepository();
	} 
	 
}