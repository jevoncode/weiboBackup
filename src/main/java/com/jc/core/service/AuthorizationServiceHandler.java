package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jc.weibo4j.service.Oauth;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.weibo4j.http.AccessToken;
import com.jc.core.domain.JcUser;
import com.jc.core.domain.UserTask;
import com.jc.persistence.service.JcUserPersistenceService;
import com.jc.persistence.service.UserTaskPersistenceService;

public class AuthorizationServiceHandler implements AuthorizationService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServiceHandler.class);
	private Oauth oauth = new Oauth();
	private JcUserPersistenceService jcUserPersistenceService;
	private UserTaskPersistenceService userTaskPersistenceService;

	public AuthorizationServiceHandler(JcUserPersistenceService jcUserPersistenceService, UserTaskPersistenceService userTaskPersistenceService) {
		this.jcUserPersistenceService = jcUserPersistenceService;
		this.userTaskPersistenceService = userTaskPersistenceService;
	}

	@Override
	public String assembleOAuthURL() {
		String oauthURL = "";
		try {
			oauthURL = oauth.authorize("code", "jc"); // code and state
		} catch (WeiboException e) {
			LOG.error("occured a exception:" + e);
		}
		return oauthURL;
	}

	@Override
	public JcUser obtainAuthorization(JcUser jcUser) {
		LOG.debug("Begin to get accessToken,jcUser's code=" + jcUser.getCode() + " and sessionId=" + jcUser.getSession());
		AccessToken accessToken;
		try {
			accessToken = oauth.getAccessTokenByCode(jcUser.getCode());
		} catch (WeiboException e) {
			LOG.error("occured a excepion when we were obtaining authorization:" + e);
			return jcUser;
		}
		String token = accessToken.getAccessToken();
		jcUser.setAccessToken(token);
		JcUser old = jcUserPersistenceService.getUserByToken(token);
		if (old != null) {
			LOG.debug("get jcUser from repository, old jcUser="+old.getAccessToken());
			UserTask userTask = userTaskPersistenceService.getUserTaskByKey(old.getAccessToken());
			if (userTask.getTask().isAlive()) {
				LOG.debug("old userTask is alive!");
				old.setSession(jcUser.getSession());
				jcUser = old;
			} else {
				LOG.debug("old userTask is dead!");
				jcUser.setZipPath(old.getZipPath());
				jcUser.setOutOfLimit(old.isOutOfLimit());
				LOG.debug("update old jcUser="+jcUser.getAccessToken());
				jcUserPersistenceService.delete(old);
				jcUserPersistenceService.save(jcUser);
			}
		} else{
			LOG.debug("didn't find old user,so save the new jcUser="+jcUser.getAccessToken());
			jcUserPersistenceService.save(jcUser);
		}
		return jcUser;
	}

}