package com.jc.config;

import com.jc.core.service.*;
import com.jc.core.domain.JcUser;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class,CoreConfig.class})
public class CoreDomainIntergrationTest{
	
	private static final String oauthUrl = "https://api.weibo.com/oauth2/authorize?client_id=3087859449&redirect_uri=http://112.124.104.15/obtainoauth&response_type=code&state=jc";

	@Autowired
	//@InjectMocks
	AuthorizationService authorizationService;
	
	@Autowired
	WeiboService weiboService;
	
	@Before 
	public void setup(){
		
	}
	
	@Test
	public void thatOAuthUrlAssemble(){
		String url = authorizationService.assembleOAuthURL();
		assertEquals(oauthUrl,url);
	}
	
	//TODO to test but how can InjectMocks and Autowired use together
	/*
	@Test
	public void obtainAuthorizationAndSaveIt(){
		JcUser jcUser = new JcUSER(1,"SESSIONID123456","CODEasdf1234");
		authorizationService.obtainAuthorization(jcUser);
		
	}
	
	@Test
	public void obtainWeiboAndSaveThem(){
		weiboService.obtainWeibo();
	}
	*/
	//TODO it's difficult to test
	/*
	@Test
	public void thatCompositeWeiboReturned(){
		
	}
	*/
}