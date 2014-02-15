package com.jc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import com.jc.service.OAuthAsker;
import weibo4j.model.WeiboException;

public class LoginServlet extends HttpServlet{
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		OAuthAsker oAuthAsker = new OAuthAsker();
		String url = null;
		try{
			url = oAuthAsker.assembleOAuthURL();
		}catch(WeiboException e){
			e.printStackTrace();
		}
		resp.sendRedirect(url);
	}
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		this.doGet(req,resp);
	}
}
