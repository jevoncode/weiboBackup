package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.io.File;
import java.net.URISyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import weibo4j.Timeline;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import com.jc.core.domain.JcUser;
import com.jc.persistence.JcUserPersistenceService;
import com.jc.persistence.WeiboPersistenceService;

public class WeiboServiceHandler implements WeiboService {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeiboServiceHandler.class);
	// private WeiboDao weiboDao = new WeiboDao();
	private WeiboPersistenceService weiboPersistenceService;
	private JcUserPersistenceService jcUserPersistenceService;
	
	public WeiboServiceHandler(WeiboPersistenceService weiboPersistenceService,JcUserPersistenceService jcUserPersistenceService){
		this.weiboPersistenceService = weiboPersistenceService;
		this.jcUserPersistenceService = jcUserPersistenceService;
	}

	private int countPerPage = 100;

	public int obtainWeibo(String sessionId) {
		int count;
		int swoopCount = 0;
		User user = null;
		int limitCount = 0;
		int total;
		int totalPage;
		int page = 2;
		StatusWapper status;
		Timeline tm = new Timeline();
		// get user's AccessToken by sessionId.
		try {
			user = jcUserPersistenceService.getUserBySessionId(sessionId);
		} catch (SQLException e) {
			// LOG.error(e.printStackTrace());
			LOG.error("we did not save the user who sessionId=" + sessionId
					+ ",:" + e);
		}
		if (user == null)
			return false;
		// begin to obtain weibo from weibo api.
		tm.client.setToken(user.getAccessToken());
		try {
			status = tm.getUserTimeline(countPerPage, 1);
		} catch (WeiboException e) {
			LOG.error("occured a exception when obtaining weibo use weibo api"
					+ e);
		}
		LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
		count += weiboPersistenceService.saveStatuses(status.getStatuses());
		swoopCount += status.getStatuses().size();

		total = (int) (status.getTotalNumber()); // total weibos that user have
		totalPage = total % 100 == 0 ? total / 100 : total / 100 + 1;

		// because weibo api has a limit that only 100 weibos can be obtained
		// each request.
		for (; page < totalPage; page++) {
			try {
				status = tm.getUserTimeline(countPerPage, page);
			} catch (WeiboException e) {
				LOG.error("occured a exception when obtaining weibo use weibo api"
						+ e);
			}
			LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
			// weiboDao = new WeiboDao(); // TODO optimize
			weiboPersistenceService.saveStatuses(status.getStatuses());
			swoopCount += status.getStatuses().size();
		}
		LOG.debug("obtained " + swoopCount + " Weibos");
		LOG.debug("save " + count + " Weibos");
		return true;
	}

	public String compositeWeibo(JcUser jcUser) {
		StringBuffer cs = new StringBuffer();
		URL url = null;
		String weibo = "";
		List<Status> statuses = weiboPersistenceService.getAllTop(jcUser.getId);
		for (Status s : statuses) {
			String resource = "/templates/status.html";
			if (s.getRetweetedStatus() != null) {
				resource = "/templates/retweeted.html";
			}
			url = this.getClass().getResource(resource);
			try {
				cs.append(formatWeibo(url.toURI(), s));
			} catch (URISyntaxException e) {
				LOG.error(e);
			}
		}
		url = this.getClass().getResource("/templates/weibo.html");
		try {
			weibo = DataUtil.readFlatFile(new File(url.toURI()));
		} catch (FileNotFoundException e) {
			LOG.error("not found file:/templates/weibo.html");
		} catch (IOException e2) {
			LOG.error("occured exception read file '/templates/weibo.html' \n"
					+ e);
		} catch (URISyntaxException e3) {
			LOG.error(e3);
		}
		weibo = weibo.replaceAll("\\$\\{content\\}", cs.toString());
		return weibo;
	}

	public String formatWeibo(URI fileUri,Status status){
//		String debugBeforeTemp = null;
//		String debugAfterTemp = null;
		String template;
		int begin;
		int end;

		try{
			template = DataUtil.readFlatFile(new File(fileUri));
		}catch(FileNotFoundException e){
			LOG.debug("not found file:"+fileUri);
		}catch(IOException e2){
			LOG.debug("occured exception read file '"+fileUri+""' \n"+e);
		}
		
		begin = template.indexOf("${");
		end = template.indexOf("}",begin);
		while(begin!=-1){
			String name = template.substring(begin+2,end);
			String value = "";
			try{
			if(name.split("\\.").length<3)
				value = getValue(status,name);
			else
				value = getValue(status.getRetweetedStatus(),name);
			}catch(NoSuchMethodException e1){
				LOG.error("occured a exception status'id="+status.getId()+",:"+e1);
			}catch(IllegalAccessException e2){
				LOG.error("occured a exception status'id="+status.getId()+",:"+e2);
			}catch(IllegalArgumentException e3){
				LOG.error("occured a exception status'id="+status.getId()+",:"+e3);
			}catch(InvocationTargetException e4){
				LOG.error("occured a exception status'id="+status.getId()+",:"+e4);
			}
			name = name.replace(".","\\u002E");
			//LOG.debug("regex:"+"\\$\\{"+name+"\\}");
			value = StringUtil.escapeChar(value);
//			debugBeforeTemp = temp;
			try{
				template = template.replaceFirst("\\u0024\\u007B"+name+"\\u007D",value);
//				debugAfterTemp = temp;
			}catch(IllegalArgumentException e){
				//LOG.debug("temp which before replaced:"+debugBeforeTemp);
				//LOG.debug("replace value:"+value);
				//LOG.debug("temp which after replaced:"+debugAfterTemp);
//				throw e;
				LOG.error("occured exception when format weibo,status'id="+status.getId()+",:"+e);
				return "";
			}
			begin = template.indexOf("${");
			end = template.indexOf("}",begin);
		}
		return template;
	}
}
