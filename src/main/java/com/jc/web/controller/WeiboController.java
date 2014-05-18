package com.jc.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Date;

import com.jc.core.domain.JcUser;
import com.jc.core.domain.State;
import com.jc.core.domain.UserTask;
import com.jc.core.service.WeiboService;
import com.jc.persistence.service.JcUserPersistenceService;
import com.jc.persistence.service.UserTaskPersistenceService;

@Controller
public class WeiboController {
	private static final Logger LOG = LoggerFactory.getLogger(WeiboController.class);

	@Autowired
	private JcUser jcUser;

	@Autowired
	private WeiboService weiboService;
	
	@Autowired
	private JcUserPersistenceService jcUserPersistenceService;
	
	@Autowired
	private UserTaskPersistenceService userTaskPersistenceService;

	@RequestMapping(value = "/backup", method = RequestMethod.GET)
	@ResponseBody
	public State backup(String thumbnail, String large, String comment, HttpSession session) {
		State state = new State();
		UserTask userTask = userTaskPersistenceService.getUserTaskByKey(jcUser.getAccessToken());
		if ((userTask==null||!userTask.getTask().isAlive())&&!isOutOfLimit()) {
			LOG.debug("backup weibo, its  owner's sessionid:" + jcUser.getSession());
			LOG.debug("backup weibo, its  owner's code:" + jcUser.getCode());
			LOG.debug("backup weibo, its  owner's accessToken:" + jcUser.getAccessToken());
			LOG.debug("backup weibo, its  owner's ip address:" + jcUser.getIpAddress());
			jcUser.setBackupThumbnail("true".equals(thumbnail));
			jcUser.setBackupLarge("true".equals(large));
			jcUser.setBackupComment("true".equals(comment));
			jcUser = weiboService.obtainWeibo(jcUser, session.getServletContext());
			jcUser = weiboService.packageZip(jcUser);
			state.setInfo("it is backuping");
		} else if(jcUser.isOutOfLimit()==true){
			state.setInfo("out for limit!");
		}
		else{
			state.setInfo("it is deleting, forbidden backup!");
		}
		state.setWeiboCount(jcUser.getWeiboCount());
		state.setThumbnailCount(jcUser.getThumbnailCount());
		state.setLargeCount(jcUser.getLargeCount());
		state.setCommentCount(jcUser.getCommentCount());
		state.setFileSize(jcUser.getFileSize());
		return state;
	}

	private boolean isOutOfLimit() {
		if(jcUser.isOutOfLimit()==false)
			return false;
		else{
			Date now = new Date();
			long interval = (now.getTime() - jcUser.getCreatedTime().getTime())/(3600*1000);
			if(interval>1){
				jcUser.setOutOfLimit(false);
				jcUser.setCreatedTime(now);
				jcUserPersistenceService.delete(jcUser);
				jcUserPersistenceService.save(jcUser);
				return false;
			}
			else
				return true;
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	@ResponseBody
	public State delete() {
		LOG.debug("delete weibo, its  owner's sessionid:" + jcUser.getSession());
		LOG.debug("delete weibo, its  owner's code:" + jcUser.getCode());
		LOG.debug("delete weibo, its  owner's accessToken:" + jcUser.getAccessToken());
		LOG.debug("delete weibo, its  owner's ip address:" + jcUser.getIpAddress());
		jcUser = weiboService.deleteWeibo(jcUser);
		State state = new State();
		state.setDeleteCount(jcUser.getDeleteCount());
		return state;
	}

	@RequestMapping(value = "/downpage", method = RequestMethod.GET)
	public String downpage() {
		return "/down";
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(HttpServletResponse response) {
		FileInputStream in = null;
		OutputStream out = null;
		try {
			in = weiboService.downloadZip(jcUser);
			out = response.getOutputStream();
			response.setContentType("");
			response.setHeader("Content-disposition", "attachment;filename=weiboBackup.zip");
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