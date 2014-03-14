package com.jc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import weibo4j.model.WeiboException;
import com.jc.service.WeiboManager;

public class BackupServlet extends HttpServlet{
	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		this.doPost(req,resp);
	}
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		String sessionId = req.getSession().getId();
		boolean isSave = false;

		try{
			isSave = (new WeiboManager()).swoopWeibo(sessionId);	
		}catch(WeiboException e){
			e.printStackTrace();
		}
		if(isSave)
			req.getRequestDispatcher("WEB-INF/jsp/success.jsp").forward(req,resp);
		else
			req.getRequestDispatcher("WEB-INF/jsp/success.jsp").forward(req,resp);
		
	}
}
