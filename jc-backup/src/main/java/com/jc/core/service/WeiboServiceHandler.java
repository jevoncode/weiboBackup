package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.net.URISyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import com.jc.core.domain.JcUser;
import com.jc.core.domain.JcStatus;
import com.jc.util.StringUtil;
import com.jc.util.DataUtil;
import com.jc.util.ReflectionUtil;
import com.jc.persistence.service.JcUserPersistenceService;
import com.jc.persistence.service.WeiboPersistenceService;

public class WeiboServiceHandler implements WeiboService {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeiboServiceHandler.class);
	// private WeiboDao weiboDao = new WeiboDao();
	private static final int COUNT_PER_PAGE = 100;
	private WeiboPersistenceService weiboPersistenceService;
	private JcUserPersistenceService jcUserPersistenceService;
	
	public WeiboServiceHandler(WeiboPersistenceService weiboPersistenceService,JcUserPersistenceService jcUserPersistenceService){
		this.weiboPersistenceService = weiboPersistenceService;
		this.jcUserPersistenceService = jcUserPersistenceService;
	}
	/**
	 * call Weibo api to obtain weibos
	 * return count weibos have been saved
	 */
	@Override
	public int obtainWeibo(JcUser jcUser) {
		int count = 0;
		int swoopCount = 0; 
		int limitCount = 0;
		int total = 0;
		int totalPage =0;
		int page = 2;
		StatusWapper statusWapper =null;
		List<JcStatus> jcStatuses =null; 
		Timeline tm = new Timeline();
		// get user's AccessToken by sessionId. 
		jcUser = jcUserPersistenceService.getUserBySessionId(jcUser.getSession()); 
		if (jcUser == null)
			return 0;
		// begin to obtain weibo from weibo api.
		tm.client.setToken(jcUser.getAccessToken());
		try {
			statusWapper = tm.getUserTimeline(COUNT_PER_PAGE, 1);
			jcStatuses = new ArrayList<JcStatus>();
			for(Status s:statusWapper.getStatuses()){
				JcStatus js = (JcStatus)s;
				js.setJcUser(jcUser);
				jcStatuses.add(js);
			}
		} catch (WeiboException e) {
			LOG.error("occured a exception when obtaining weibo use weibo api"
					+ e);
		}
		LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
//		count += weiboPersistenceService.saveStatuses(statusWapper.getStatuses());
		count += weiboPersistenceService.saveStatuses(jcStatuses);
		swoopCount += statusWapper.getStatuses().size();

		total = (int) (statusWapper.getTotalNumber()); // total weibos that user have
		totalPage = total % 100 == 0 ? total / 100 : total / 100 + 1;

		// because weibo api has a limit that only 100 weibos can be obtained
		// each request.
		for (; page < totalPage; page++) {
			try {
				statusWapper = tm.getUserTimeline(COUNT_PER_PAGE, page);
				jcStatuses = new ArrayList<JcStatus>();
				for(Status s:statusWapper.getStatuses()){
					JcStatus js = (JcStatus)s;
					js.setJcUser(jcUser);
					jcStatuses.add(js);
				}
			} catch (WeiboException e) {
				LOG.error("occured a exception when obtaining weibo use weibo api"
						+ e);
			}
			LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
			// weiboDao = new WeiboDao(); // TODO optimize
			weiboPersistenceService.saveStatuses(jcStatuses);
			swoopCount += statusWapper.getStatuses().size();
		}
		LOG.debug("obtained " + swoopCount + " Weibos");
		LOG.debug("save " + count + " Weibos");
		return count;
	}

	/**
	 * return HTML style weibo
	 */
	@Override
	public String compositeWeibo(JcUser jcUser) {
		StringBuffer cs = new StringBuffer();
		URL url = null;
		String weibo = "";
		List<JcStatus> statuses = weiboPersistenceService.getAllTop(jcUser);
		for (JcStatus s : statuses) {
			String resource = "/templates/status.html";
			if (s.getRetweetedStatus() != null) {
				resource = "/templates/retweeted.html";
			}
			url = this.getClass().getResource(resource);
			try {
				cs.append(formatWeibo(url.toURI(), s));
			} catch (URISyntaxException e) {
				LOG.error(e.getMessage());
			}
		}
		url = this.getClass().getResource("/templates/weibo.html");
		try {
			weibo = DataUtil.readFlatFile(new File(url.toURI()));
		} catch (FileNotFoundException e) {
			LOG.error("not found file:/templates/weibo.html"+ e);
		} catch (IOException e2) {
			LOG.error("occured exception read file '/templates/weibo.html' \n"
					+ e2);
		} catch (URISyntaxException e3) {
			LOG.error(e3.getMessage());
		}
		weibo = weibo.replaceAll("\\$\\{content\\}", cs.toString());
		return weibo;
	}

	public String formatWeibo(URI fileUri,JcStatus status){
//		String debugBeforeTemp = null;
//		String debugAfterTemp = null;
		String template = "";
		int begin;
		int end;

		try{
			template = DataUtil.readFlatFile(new File(fileUri));
		}catch(FileNotFoundException e){
			LOG.debug("not found file:"+fileUri+"' \n"+e);
		}catch(IOException e2){
			LOG.debug("occured exception read file '"+fileUri+"' \n"+e2);
		}
		
		begin = template.indexOf("${");
		end = template.indexOf("}",begin);
		while(begin!=-1){
			String name = template.substring(begin+2,end);
			String value = "";
			try{
			if(name.split("\\.").length<3)
				value = ReflectionUtil.getValue(status,name);
			else
				value = ReflectionUtil.getValue(status.getRetweetedStatus(),name);
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
