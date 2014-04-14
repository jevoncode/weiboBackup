package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod; 
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
 
import com.jc.core.domain.JcUser;
import com.jc.core.service.WeiboService;

@Controller 
public class WeiboController{
	private static final Logger LOG = LoggerFactory.getLogger(WeiboController.class);
	
	@Autowired
	private JcUser jcUser;
	
	@Autowired
	private WeiboService weiboService;
	
	@RequestMapping(value="/backup",method = RequestMethod.GET) 
	@ResponseBody
	public int backup(){
		LOG.debug("backup weibo which onwer's sessionid:"+jcUser.getSession());
		LOG.debug("backup weibo which onwer's code:"+jcUser. getCode());
		int count = weiboService.obtainWeibo(jcUser);
		//model.addAttribute("count",count);
		return count; 
	} 
	
	@ModelAttribute("jcUser")
	private JcUser getJcUser(){
		return jcUser;
	}
}