package com.jc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Date;
import com.jc.service.OAuthAsker;
import com.jc.model.User;
import weibo4j.model.WeiboException;


public class ObtainOAuth extends HttpServlet{

	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		OAuthAsker oAuthAsker = new OAuthAsker();
		HttpSession session = req.getSession();
		String code = req.getParameter("code");
		User user = new User();
		user.setSession(session.getId());
		user.setCreatedTime(new Date());
		user.setCode(code);
		try{
			oAuthAsker.userAuthorize(user);	
		}catch(WeiboException e){
			e.printStackTrace();
		}
		req.getRequestDispatcher("WEB-INF/jsp/main.jsp").forward(req,resp);
	}
}
