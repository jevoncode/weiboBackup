package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
 
 
import com.jc.core.domain.JcUser;

@Controller 
public class WeiboController{
	private static final Logger LOG = LoggerFactory.getLogger(WeiboController.class);
	
	@Autowired
	private JcUser jcUser;
	
	@RequestMapping(value="/backup",method = RequestMethod.GET) 
	public String backup(){
		LOG.debug("backup weibo which onwer's sessionid:"+jcUser.getSession());
		LOG.debug("backup weibo which onwer's code:"+jcUser. getCode());
		
		return "/down"; 
	} 
	
	@ModelAttribute("jcUser")
	private JcUser getJcUser(){
		return jcUser;
	}
}