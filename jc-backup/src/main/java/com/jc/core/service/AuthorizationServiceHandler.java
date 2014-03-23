package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LogFactory;
import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import weibo4j.http.AccessToken;
import com.jc.model.core.JcUser;
import com.jc.persistence.JcUserPersistenceService;

public class AuthorizationServiceHandler{

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServiceHandler.class);
	private Oauth oauth ;
	private JcUserPersistenceService jcUserPersistenceService;
	
	public String assembleOAuthURL(){ 
		String oauthURL = "";
		try{
			oauthURL = oauth.authorize("code","jc"); //code and state
		}catch(WeiboException e){
			LOG.error("occured a exception:"+e);
		}
		return oauthURL;
	}
	
	public JcUser obtainAuthorization(JcUser jcUser){
		LOG.debug("Begin to get accessToken,jcUser's code="+jcUser.getCode()+
					" and sessionId="+jcUser.getSession());
		AccessToken accessToken ;
		try{
			accessToken = oauth.getAccessTokenByCode(JcUser.getCode()); 
		}catch(WeiboException e){
			LOG.error("occured a excepion when we were obtaining authorization:"+e);
			return null;
		}
		jcUser.setAccessToken(accessToken.getAccessToken());
		jcUserPersistenceService.save(jcUser);
		return jcUser;
	}
	
}