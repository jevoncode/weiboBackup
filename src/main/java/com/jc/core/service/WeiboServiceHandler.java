package com.jc.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Date;
import java.util.UUID;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import com.jc.weibo4j.service.Timeline;
import com.jc.weibo4j.domain.Status;
import com.jc.weibo4j.domain.StatusWapper;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.core.domain.JcUser;
import com.jc.util.StringUtil;
import com.jc.util.DataUtil;
import com.jc.util.NetUtil;
import com.jc.util.FileUtils;
import com.jc.util.ReflectionUtil;
import com.jc.persistence.service.JcUserPersistenceService;
import com.jc.persistence.service.WeiboPersistenceService;

import javax.servlet.ServletContext;

public class WeiboServiceHandler implements WeiboService {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeiboServiceHandler.class);
	// private WeiboDao weiboDao = new WeiboDao();
	private static final int COUNT_PER_PAGE = 100;
	private WeiboPersistenceService weiboPersistenceService;
	private JcUserPersistenceService jcUserPersistenceService;
	private String path = ""; // which save weibo file including html,images

	public WeiboServiceHandler(WeiboPersistenceService weiboPersistenceService,
			JcUserPersistenceService jcUserPersistenceService) {
		this.weiboPersistenceService = weiboPersistenceService;
		this.jcUserPersistenceService = jcUserPersistenceService;
	}

	/**
	 * call Weibo api to obtain weibos return count weibos have been saved
	 */
	@Override
	public int obtainWeibo(JcUser jcUser) {
		int count = 0;
		int swoopCount = 0;
		int limitCount = 0;
		int total = 0;
		int totalPage = 0;
		int page = 2;
		StatusWapper statusWapper = null;
		Timeline tm = new Timeline();
		// get user's AccessToken by sessionId.
		jcUser = jcUserPersistenceService.getUserBySessionId(jcUser
				.getSession());
		if (jcUser == null)
			return 0;
		// begin to obtain weibo from weibo api.
		tm.client.setToken(jcUser.getAccessToken());
		try {
			statusWapper = tm.getUserTimeline(COUNT_PER_PAGE, 1, jcUser);
		} catch (WeiboException e) {
			LOG.error("occured a exception when obtaining weibo use weibo api"
					+ e);
		}
		LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
		count += weiboPersistenceService.saveStatuses(statusWapper
				.getStatuses());
		swoopCount += statusWapper.getStatuses().size();

		total = (int) (statusWapper.getTotalNumber()); // total weibos that user
														// have
		totalPage = total % 100 == 0 ? total / 100 : total / 100 + 1;

		// because weibo api has a limit that only 100 weibos can be obtained
		// each request.
		for (; page < totalPage; page++) {
			try {
				statusWapper = tm.getUserTimeline(COUNT_PER_PAGE, page, jcUser);
			} catch (WeiboException e) {
				LOG.error("occured a exception when obtaining weibo use weibo api"
						+ e);
			}
			LOG.debug(++limitCount + "st to invoke UserTimeline interface.");
			// weiboDao = new WeiboDao(); // TODO optimize
			count += weiboPersistenceService.saveStatuses(statusWapper
					.getStatuses());
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
		List<Status> statuses = weiboPersistenceService.getAllTop(jcUser);
		for (Status s : statuses) {

			// download pictures
			s = downloadImage(s);

			LOG.debug("compositing weibo,status is:" + s.getId());
			String resource = "/templates/status.html";
			if (s.getRetweetedStatus() != null) {
				resource = "/templates/retweeted.html";
				// download pictures
				if (s.getRetweetedStatus().getPicUrls() != null
						&& s.getRetweetedStatus().getPicUrls().length > 0)
					s.setRetweetedStatus(downloadImage(s.getRetweetedStatus()));
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
			LOG.error("not found file:/templates/weibo.html" + e);
		} catch (IOException e2) {
			LOG.error("occured exception read file '/templates/weibo.html' \n"
					+ e2);
		} catch (URISyntaxException e3) {
			LOG.error(e3.getMessage());
		}
		weibo = weibo.replaceAll("\\$\\{content\\}", cs.toString());
		return weibo;
	}

	public String formatWeibo(URI fileUri, Status status) {
		// String debugBeforeTemp = null;
		// String debugAfterTemp = null;
		String template = "";
		int begin;
		int end;

		try {
			template = DataUtil.readFlatFile(new File(fileUri));
		} catch (FileNotFoundException e) {
			LOG.debug("not found file:" + fileUri + "' \n" + e);
		} catch (IOException e2) {
			LOG.debug("occured exception read file '" + fileUri + "' \n" + e2);
		}

		begin = template.indexOf("${");
		end = template.indexOf("}", begin);
		while (begin != -1) {
			String name = template.substring(begin + 2, end);
			String value = "";
			try {
				LinkedList<String> names = new LinkedList<String>(Arrays.asList(name.split("\\.")));
				names.poll(); // remove string 'status' 
				value = ReflectionUtil.getValue(status, names);
			} catch (NoSuchMethodException e1) {
				LOG.error("occured a exception status'id=" + status.getId()
						+ ",:" + e1);
			} catch (IllegalAccessException e2) {
				LOG.error("occured a exception status'id=" + status.getId()
						+ ",:" + e2);
			} catch (IllegalArgumentException e3) {
				LOG.error("occured a exception status'id=" + status.getId()
						+ ",:" + e3);
			} catch (InvocationTargetException e4) {
				LOG.error("occured a exception status'id=" + status.getId()
						+ ",:" + e4);
			} catch (NullPointerException e5) {
				LOG.error("try to get value of '" + name
						+ "' occured a exception status'id=" + status.getId()
						+ ",:" + e5);
			}
			name = name.replace(".", "\\u002E");
			// LOG.debug("regex:"+"\\$\\{"+name+"\\}");
			value = StringUtil.escapeChar(value);
			// debugBeforeTemp = temp;
			try {
				template = template.replaceFirst("\\u0024\\u007B" + name
						+ "\\u007D", value);
				// debugAfterTemp = temp;
			} catch (IllegalArgumentException e) {
				// LOG.debug("temp which before replaced:"+debugBeforeTemp);
				// LOG.debug("replace value:"+value);
				// LOG.debug("temp which after replaced:"+debugAfterTemp);
				// throw e;
				LOG.error("occured exception when format weibo,status'id="
						+ status.getId() + ",:" + e);
				return "";
			}
			begin = template.indexOf("${");
			end = template.indexOf("}", begin);
		}
		return template;
	}

	@Override
	public JcUser packageZip(JcUser jcUser, ServletContext context) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
			String basePath = context.getRealPath("/WEB-INF/weibo_files");
			String subPath = sdf.format(new Date());
			path = basePath + subPath + UUID.randomUUID().toString(); // avoids
			// if not exists then create it
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String weibo = compositeWeibo(jcUser);
			// same
			// name
			// directory

			byte[] data = weibo.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(path, "index.html")));
			stream.write(data);
			stream.close();

			// copy style files into it
			URL tamplateUrl = this.getClass().getResource("/templates/style");
			try {
				FileUtils.copyFile(new File(tamplateUrl.toURI()), dir);
			} catch (URISyntaxException e3) {
				LOG.error(e3.getMessage());
			}
			// package files in zip
			String zipPath = path + ".zip";
			File zip = new File(zipPath);
			FileUtils.zipFile(dir, zip);
			jcUser.setZipPath(zipPath);
		} catch (FileNotFoundException e) {
			LOG.debug("not found file' \n" + e);
		} catch (IOException e2) {
			LOG.debug("dowload zip function occured errer: \n" + e2);
		}
		return jcUser;
	}

	@Override
	public FileInputStream downloadZip(JcUser jcUser) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(jcUser.getZipPath());
		} catch (FileNotFoundException e) {
			LOG.error("File " + jcUser.getZipPath() + " is not found." + e);
		}
		return in;
	}

	public Status downloadImage(Status s) {
		if(s.getPicUrls()==null||s.getPicUrls().length<1)
			return s;
		String[] imageUrls = new String[s.getPicUrls().length];
		int index = 0;
		for (String imageUrl : s.getPicUrls()) {
			try {
				// download thumbnail image
				byte[] thumbnailImage = NetUtil.readImage(imageUrl);
				File thumbnailDirectory = new File(path, "thumbnail");
				DataUtil.writeImage(
						thumbnailDirectory,
						imageUrl.substring(imageUrl.lastIndexOf("/"),
								imageUrl.length()), thumbnailImage);
				// download large image
				String largeImageUrl = imageUrl.replaceFirst("thumbnail",
						"large");
				byte[] largeImage = NetUtil.readImage(largeImageUrl);
				File largeDirectory = new File(path, "large");
				DataUtil.writeImage(largeDirectory,
						largeImageUrl.substring(largeImageUrl.lastIndexOf("/"),
								largeImageUrl.length()), largeImage);
				imageUrl = imageUrl.substring(
						imageUrl.lastIndexOf("thumbnail"), imageUrl.length());
				imageUrls[index++] = imageUrl;
				LOG.debug("saved image:" + imageUrl);
			} catch (IOException e) {
				LOG.error("occured exception when download images' \n" + e);
			}
		}
		s.setPicUrls(imageUrls);
		return s;
	}
}
