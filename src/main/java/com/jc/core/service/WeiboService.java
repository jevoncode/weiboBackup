package com.jc.core.service;

import com.jc.core.domain.JcUser;
import javax.servlet.ServletContext;
import java.io.FileInputStream;

public interface WeiboService{
	
	public int obtainWeibo(JcUser jcUser);
	
	public String compositeWeibo(JcUser jcUser) ;
	
	public FileInputStream downloadZip(JcUser jcUser);
	
	public JcUser packageZip(JcUser jcUser, ServletContext context);
}