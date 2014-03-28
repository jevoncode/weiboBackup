package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.jc.core.service.AuthorizationService;

@Controller
@RequestMapping("/login")
public class LoginController{
	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private AuthorizationService authorizationService;
	 
}