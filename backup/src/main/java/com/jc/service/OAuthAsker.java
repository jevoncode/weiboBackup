package com.jc.service;

import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import weibo4j.http.AccessToken;
import com.jc.model.User;

public class OAuthAsker{
	public String assembleOAuthURL(User user) throws WeiboException{ 
		Oauth oauth = new Oauth();
		String oauthURL = oauth.authorize(user.getCode(),"",""); 
		return oauthURL;
	}	

    public AccessToken askForToken(User user) throws WeiboException{ 
		Oauth oauth = new Oauth();
		AccessToken accessToken = oauth.getAccessTokenByCode(user.getCode()); 
		return accessToken;
    }

}
