package com.jc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import weibo4j.http.AccessToken;
import com.jc.model.User;
import com.jc.dao.UserDao;
import java.sql.SQLException;

public class OAuthAsker{
	
	private static final Logger LOG = LoggerFactory.getLogger(OAuthAsker.class);
	public String assembleOAuthURL() throws WeiboException{ 
		Oauth oauth = new Oauth();
		String oauthURL = oauth.authorize("code","jc"); //code and state 
		return oauthURL;
	}	

    public AccessToken askForToken(User user) throws WeiboException{ 
		Oauth oauth = new Oauth();
		AccessToken accessToken = oauth.getAccessTokenByCode(user.getCode()); 
		return accessToken;
    }
	public User userAuthorize(User user) throws WeiboException{
		LOG.debug("jc Begin to ask for token,the user'code is:"+user.getCode()+
					"and the session:"+user.getSession());
		AccessToken accessToken = askForToken(user);
		user.setAccessToken(accessToken.getAccessToken());
		(new UserDao()).saveUser(user);
		return user;
	}
}
