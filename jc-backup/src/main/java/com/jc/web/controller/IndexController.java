package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jc.core.service.IndexService;

@Controller
@RequestMapping("/")
public class IndexController{
	
	private static final Logger LOG = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired
	private IndexService indexService;
	
	@RequestMapping(value="",method = RequestMethod.GET)
	@ResponseBody
	public String showMe(){
		LOG.debug("WeboBackup directly to ResonseBody");
		return indexService.showAboutMe();
	} 
	
}