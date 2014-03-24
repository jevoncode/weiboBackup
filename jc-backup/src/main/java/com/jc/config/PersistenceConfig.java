package com.jc.config;
 
import com.jc.persistence.service.*;
import com.jc.persistence.repository.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public Corefig{
	
	@Bean
	public JcUserPersistenceService jcUserPersistenceService(Repository repository){
		return new JcUserPersistenceMemoryService(repository);
	} 
}