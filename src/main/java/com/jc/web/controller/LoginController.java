package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import com.jc.core.service.AuthorizationService;
import com.jc.core.domain.JcUser;
import com.jc.util.StringUtil;

@Controller 
public class LoginController{
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private JcUser jcUser;
	 
	@RequestMapping(value="/login",method = RequestMethod.GET) 
	public String login(HttpServletRequest request){
		String ipAddress = request.getRemoteAddr();
		jcUser.setIpAddress(ipAddress);
		LOG.debug("logining from ip:"+jcUser.getIpAddress());
		return "redirect:"+authorizationService.assembleOAuthURL();
	} 
	
	@RequestMapping(value="/obtainoauth",method = RequestMethod.GET) 
	public String obtainOAuth(@RequestParam("state") String state,@RequestParam("code") String code,HttpSession session){
		if(jcUser.getAccessToken()!=null&&jcUser.getAccessToken().length()>0)
			return "/main";
		jcUser.setSession(session.getId());
		jcUser.setCreatedTime(new Date());
		jcUser.setCode(code);
		jcUser.setVerificationCode(StringUtil.generateVarificationCode());
		jcUser = authorizationService.obtainAuthorization(jcUser);
		LOG.debug("login in user's code:"+jcUser.getCode());
		LOG.debug("login in user's session:"+jcUser.getSession());
		LOG.debug("login in user's accessToken:"+jcUser.getAccessToken());
		LOG.debug("login in user's ip address:"+jcUser.getIpAddress());
		if(jcUser.getAccessToken()!=null&&jcUser.getAccessToken().length()>0)
			return "/main";
		else
			return "/loginerror";
	} 
	
	/**
	 * put the JcUser into the model for the view to be able to read from.
	 */
	@ModelAttribute("jcUser")
	private JcUser getJcUser(){
		return jcUser;
	}
}