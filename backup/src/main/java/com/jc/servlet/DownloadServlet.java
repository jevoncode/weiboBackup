package com.jc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import com.jc.service.CombineWeibo;

public class DownloadServlet extends HttpServlet{

	public void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		this.doPost(req,resp);
	}
	public void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException{
		CombineWeibo combineWeibo = new CombineWeibo();
		String htmlFile = "nullValue";
		try{
			htmlFile =  combineWeibo.combineWeibo();
		}catch(URISyntaxException e){
			e.printStackTrace();
		}
		resp.setContentType("");
		resp.setHeader("Content-disposition","attachment;filename=index.html");
		OutputStream out = resp.getOutputStream();
		out.write(htmlFile.getBytes());
		out.flush();
	}
}
