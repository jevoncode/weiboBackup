package com.jc.service

import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import com.jc.model.User;

public class OAuthAsker{
	public String assembleOAuthURL(User user){ //TODO
		Oauth oauth = new Oauth();
		String oauthURL = oauth.authorize(user.getCode(),"",""); //TODO
		return oauthURL;
	}	

    public String askForToken(User user) throws WeiboException{ //TODO
		Oauth oauth = new Oauth();
		String accessToken = oauth.getAccessTokenByCode(user.getCode()); //TODO
		return accessToken;
    }

}
