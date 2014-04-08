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

import com.jc.core.service.AuthorizationService;
import com.jc.core.domain.JcUser;

@Controller 
public class LoginController{
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private JcUser jcUser;
	 
	@RequestMapping(value="/login",method = RequestMethod.GET) 
	public String login(){
		LOG.debug("login");
		return "redirect:"+authorizationService.assembleOAuthURL();
	} 
	
	@RequestMapping(value="/obtainoauth",method = RequestMethod.GET) 
	public String obtainOAuth(@RequestParam("state") String state,@RequestParam("code") String code,HttpSession session){
		jcUser.setSession(session.getId());
		jcUser.setCreatedTime(new Date());
		jcUser.setCode(code);
		
		if(authorizationService.obtainAuthorization(jcUser))
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