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
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;

import com.jc.core.domain.JcUser;
import com.jc.core.service.WeiboService;

@Controller
public class WeiboController {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeiboController.class);

	@Autowired
	private JcUser jcUser;

	@Autowired
	private WeiboService weiboService;

	@RequestMapping(value = "/backup", method = RequestMethod.GET)
	@ResponseBody
	public String backup() {
		LOG.debug("backup weibo which onwer's sessionid:" + jcUser.getSession());
		LOG.debug("backup weibo which onwer's code:" + jcUser.getCode());
		int count = weiboService.obtainWeibo(jcUser);
		// model.addAttribute("count",count);
		return String.valueOf(count);
	}

	@RequestMapping(value = "/downpage", method = RequestMethod.GET)
	public String downpage() {
		return "/down";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(HttpSession session, HttpServletResponse response) {
		if (jcUser.getZipPath() == null || jcUser.getZipPath().length() == 0)
			jcUser = weiboService.packageZip(jcUser,
					session.getServletContext());
		FileInputStream in = null;
		OutputStream out = null;
		try {
			in = weiboService.downloadZip(jcUser);
			out = response.getOutputStream();
			response.setContentType("");
			response.setHeader("Content-disposition",
					"attachment;filename=weiboBackup.zip");
			byte[] buffer = new byte[4096];
			int len = -1;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			LOG.error("occurred a error when download zip file:" + e);
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e2) {
				LOG.error("occurred a error when close zip iostream" + e2);
			}
		}
	}

	@ModelAttribute("jcUser")
	private JcUser getJcUser() {
		return jcUser;
	}
}