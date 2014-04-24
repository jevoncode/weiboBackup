package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jc.weibo4j.service.Oauth;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.weibo4j.http.AccessToken;
import com.jc.core.domain.JcUser;
import com.jc.persistence.service.JcUserPersistenceService;

public class AuthorizationServiceHandler implements AuthorizationService{

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServiceHandler.class);
	private Oauth oauth = new Oauth();
	private JcUserPersistenceService jcUserPersistenceService;
	
	public AuthorizationServiceHandler(JcUserPersistenceService jcUserPersistenceService){
		this.jcUserPersistenceService = jcUserPersistenceService;
	}
	
	@Override
	public String assembleOAuthURL(){ 
		String oauthURL = "";
		try{
			oauthURL = oauth.authorize("code","jc"); //code and state
		}catch(WeiboException e){
			LOG.error("occured a exception:"+e);
		}
		return oauthURL;
	}
	
	@Override
	public JcUser obtainAuthorization(JcUser jcUser){
		LOG.debug("Begin to get accessToken,jcUser's code="+jcUser.getCode()+
					" and sessionId="+jcUser.getSession());
		AccessToken accessToken ;
		try{
			accessToken = oauth.getAccessTokenByCode(jcUser.getCode()); 
		}catch(WeiboException e){
			LOG.error("occured a excepion when we were obtaining authorization:"+e);
			return jcUser;
		}
		jcUser.setAccessToken(accessToken.getAccessToken());
		
		jcUserPersistenceService.save(jcUser);
		return jcUser;
	}
	
}