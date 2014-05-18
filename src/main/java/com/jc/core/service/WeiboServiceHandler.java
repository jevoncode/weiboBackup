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
import java.util.Map;
import java.util.HashMap;
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
import com.jc.weibo4j.service.Comments;
import com.jc.weibo4j.domain.Status;
import com.jc.weibo4j.domain.Comment;
import com.jc.weibo4j.domain.StatusWapper;
import com.jc.weibo4j.domain.CommentWapper;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.core.domain.JcUser;
import com.jc.core.domain.UserTask;
import com.jc.util.StringUtil;
import com.jc.util.DataUtil;
import com.jc.util.NetUtil;
import com.jc.util.FileUtils;
import com.jc.util.ReflectionUtil;
import com.jc.persistence.service.JcUserPersistenceService;
import com.jc.persistence.service.UserTaskPersistenceService;
import com.jc.persistence.service.WeiboPersistenceService;
import com.jc.baidu4j.service.Place;

import javax.servlet.ServletContext;

public class WeiboServiceHandler implements WeiboService {
	private static final Logger LOG = LoggerFactory.getLogger(WeiboServiceHandler.class);
	// private WeiboDao weiboDao = new WeiboDao();
	private static final int COUNT_PER_PAGE = 100;
	private WeiboPersistenceService weiboPersistenceService;
	private JcUserPersistenceService jcUserPersistenceService;
	private UserTaskPersistenceService userTaskPersistenceService;
	private String path = ""; // which save weibo file including html,images
	private JcUser jcUser;
	private int thumbnailCount = 0;
	private int largeCount = 0;
	private int commentCount = 0;
	private float zipFileSize; // Unit is 'M'
	private Comments c = new Comments(); // TODO to be optimized
	private Timeline tm = new Timeline(); // TODO to be optimized
	private int callCount = 0;
	private int limit = 150; // the limit of call weibo api in one hour;
	private boolean outOfLimit = false;
	private int destroyedIndex = 0;

	public WeiboServiceHandler(WeiboPersistenceService weiboPersistenceService, JcUserPersistenceService jcUserPersistenceService, UserTaskPersistenceService userTaskPersistenceService) {
		this.weiboPersistenceService = weiboPersistenceService;
		this.jcUserPersistenceService = jcUserPersistenceService;
		this.userTaskPersistenceService = userTaskPersistenceService;
	}

	/**
	 * call Weibo api to obtain weibos return count weibos have been saved
	 */
	@Override
	public JcUser obtainWeibo(JcUser jcUser, ServletContext context) {
		int count = 0;
		int swoopCount = 0;
		int totalNumber = 0;
		int totalPage = 0;
		StatusWapper statusWapper = null;

		// get user's AccessToken by sessionId.
		// this.jcUser =
		// jcUserPersistenceService.getUserBySessionId(jcUser.getSession()); it
		// should be handled by security validation
		if (jcUser.getAccessToken() == null || jcUser.getAccessToken().length() == 0)
			return jcUser;
		this.jcUser = jcUser;
		preparePath(context);

		tm.client.setToken(jcUser.getAccessToken());
		c.client.setToken(jcUser.getAccessToken());

		// begin to obtain weibo from weibo api.
		statusWapper = obtainWeibo(tm, jcUser, 1);
		count += weiboPersistenceService.saveStatuses(statusWapper.getStatuses());
		swoopCount += statusWapper.getStatuses().size();

		totalNumber = (int) (statusWapper.getTotalNumber()); // total weibos the
																// user have
		totalPage = totalNumber % 100 == 0 ? totalNumber / 100 : totalNumber / 100 + 1;
		// because weibo api has a limit that only 100 weibos can be obtained
		// each request.
		for (int page = 2; page < totalPage; page++) {
			statusWapper = obtainWeibo(tm, jcUser, page);
			// if (callCount >= limit || outOfLimit == true)
			if (jcUser.isOutOfLimit())
				break;
			count += weiboPersistenceService.saveStatuses(statusWapper.getStatuses());
			swoopCount += statusWapper.getStatuses().size();
		}
		LOG.debug("obtained " + swoopCount + " Weibos");
		LOG.debug("save " + count + " Weibos");
		this.jcUser.setWeiboCount(count);
		this.jcUser.setThumbnailCount(thumbnailCount);
		this.jcUser.setLargeCount(largeCount);
		this.jcUser.setCommentCount(commentCount);
		return this.jcUser;
	}

	public StatusWapper obtainWeibo(Timeline tm, JcUser jcUser, int page) {
		StatusWapper statusWapper = null;
		int i = 0;
		try {
			statusWapper = tm.getUserTimeline(COUNT_PER_PAGE, page, jcUser);
			LOG.debug(++callCount + "st call weibo api (now is userTimeline)");
		} catch (WeiboException e) {
			LOG.error("occured a exception when obtaining weibo use weibo api" + e);
			LOG.error(++callCount + "st call weibo api (now is comments) cause error");
			// outOfLimit = true;
			jcUser.setOutOfLimit(true);
			return null;
		}

		// download pictures and/or obtain comments
		List<Status> statuses = statusWapper.getStatuses();
		for (i = 0; i < statuses.size(); i++) {
			Status s = statuses.get(i);
			Place place = new Place();
			try {
				if (s.getLatitude() != -1 && s.getLongitude() != -1)
					s.setFormatedAddress(place.formattedAddress(s.getLatitude(), s.getLongitude()));
				else
					s.setFormatedAddress(null);
			} catch (Exception e) {
				LOG.error("occured a exception when format address " + e);
				LOG.error("try again");
				try {
					if (s.getLatitude() != -1 && s.getLongitude() != -1)
						s.setFormatedAddress(place.formattedAddress(s.getLatitude(), s.getLongitude()));
					else
						s.setFormatedAddress(null);
				} catch (Exception e2) {
					LOG.error("occured a exception when format address " + e2);
					s.setFormatedAddress(null);
				}
			}
			if (s.getCommentsCount() > 0)
				s.setComments(obtainComments(s));
			statuses.set(i, downloadImage(s));
			// if (callCount >= limit || outOfLimit == true)
			if (jcUser.isOutOfLimit())
				break;
		}
		LOG.debug("this time get statues count is" + statuses.size());
		int index = 0;
		// if (callCount >= limit || outOfLimit == true)
		if (jcUser.isOutOfLimit())
			index = i;
		else
			index = i + 1;
		int len = statuses.size();
		int times = len - index;
		for (int j = 0; j < times; j++) {
			Status s = statuses.remove(index);
			LOG.debug("removing id:" + s.getId() + ", creatAt:" + StringUtil.formatDate(s.getCreatedAt(), "yyyy-MM-dd HH:mm"));
		}
		LOG.debug("after remove get statues count is" + statuses.size());
		LOG.debug("after remove get statusWapper.statues count is" + statusWapper.getStatuses().size());
		return statusWapper;
	}

	public List<Comment> obtainComments(Status s) {
		CommentWapper commentWapper = null;
		if (!jcUser.isBackupComment())
			return null;
		try {
			commentWapper = c.getCommentById(s.getId());
			commentCount += commentWapper.getComments().size();
			LOG.debug(++callCount + "st call weibo api (now is comments)");
		} catch (WeiboException e) {
			LOG.error("occured a exception when obtaining comments status'id=" + s.getId() + ", Exception:" + e);
			LOG.error(++callCount + "st call weibo api (now is comments) cause error");
			// outOfLimit = true;
			jcUser.setOutOfLimit(true);
		}
		if (commentWapper != null)
			return commentWapper.getComments();
		else
			return null;
	}

	public void preparePath(ServletContext context) {
		SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
		String basePath = context.getRealPath("/WEB-INF/weibo_files");
		String subPath = sdf.format(new Date());
		path = basePath + subPath + UUID.randomUUID().toString(); // avoids same
																	// name
																	// directory
		// if not exists then create it
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// copy style files into it
		URL tamplateUrl = this.getClass().getResource("/templates/style");
		try {
			FileUtils.copyFile(new File(tamplateUrl.toURI()), dir);
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error("occurred a errer when copy style files " + e);
		}
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
			LOG.debug("compositing weibo,status is:" + s.getId());
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
			LOG.error("not found file:/templates/weibo.html" + e);
		} catch (IOException e2) {
			LOG.error("occurred exception read file '/templates/weibo.html' \n" + e2);
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
				LOG.error("occured a exception status'id=" + status.getId() + ",:" + e1);
			} catch (IllegalAccessException e2) {
				LOG.error("occured a exception status'id=" + status.getId() + ",:" + e2);
			} catch (IllegalArgumentException e3) {
				LOG.error("occured a exception status'id=" + status.getId() + ",:" + e3);
			} catch (InvocationTargetException e4) {
				LOG.error("occured a exception status'id=" + status.getId() + ",:" + e4);
			} catch (NullPointerException e5) {
				LOG.error("try to get value of '" + name + "' occured a exception status'id=" + status.getId() + ",:" + e5);
			}
			name = name.replace(".", "\\u002E");
			// LOG.debug("regex:"+"\\$\\{"+name+"\\}");
			value = StringUtil.escapeChar(value);
			// debugBeforeTemp = temp;
			try {
				template = template.replaceFirst("\\u0024\\u007B" + name + "\\u007D", value);
				// debugAfterTemp = temp;
			} catch (IllegalArgumentException e) {
				// LOG.debug("temp which before replaced:"+debugBeforeTemp);
				// LOG.debug("replace value:"+value);
				// LOG.debug("temp which after replaced:"+debugAfterTemp);
				// throw e;
				LOG.error("occured exception when format weibo,status'id=" + status.getId() + ",:" + e);
				return "";
			}
			begin = template.indexOf("${");
			end = template.indexOf("}", begin);
		}
		return template;
	}

	@Override
	public JcUser packageZip(JcUser jcUser) {
		try {
			String weibo = compositeWeibo(jcUser);
			byte[] data = weibo.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(path, "index.html")));
			stream.write(data);
			stream.close();

			// package files in zip
			String zipPath = path + ".zip";
			File zip = new File(zipPath);
			FileUtils.zipFile(new File(path), zip);
			jcUser.setZipPath(zipPath);
			float fileSize = zip.length();
			jcUser.setFileSize(fileSize / (1024 * 1024));
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
		if (s.getRetweetedStatus() != null) {
			s.setRetweetedStatus(downloadImage(s.getRetweetedStatus()));
		}
		if (s.getPicUrls() == null || s.getPicUrls().length == 0)
			return s;
		Map<Integer, String> failUrls = new HashMap<Integer, String>();
		// String[] imageUrls = new String[s.getPicUrls().length];
		String[] imageUrls = s.getPicUrls();
		// int index = 0;
		for (int i = 0; i < imageUrls.length; i++) {
			// for (String imageUrl : s.getPicUrls()) {
			try {
				String imageUrl = downloadImage(imageUrls[i]);
				imageUrls[i] = imageUrl;
			} catch (IOException e) {
				LOG.error("occured exception when download images:" + imageUrls[i] + e);
				LOG.error("keep it,then i will download it again.");
				failUrls.put(i, imageUrls[i]);
			}
		}
		if (failUrls.size() > 0) {
			for (int i : failUrls.keySet())
				try {
					LOG.debug("the second time to download image:" + failUrls.get(i));
					String imageUrl = downloadImage(failUrls.get(i));
					imageUrls[i] = imageUrl;
				} catch (IOException e) {
					LOG.error("occured exception when the second time download images:" + failUrls.get(i) + " \n" + e);
					imageUrls[i] = "images/fail.thumbnail.jpg";
				}
		}
		if (jcUser.isBackupThumbnail())
			s.setPicUrls(imageUrls);
		// else //without any picture
		// s.setPicUrls(null);
		return s;
	}

	public String downloadImage(String imageUrl) throws IOException {
		String result = "";
		if (jcUser.isBackupThumbnail()) {
			// download thumbnail image
			byte[] thumbnailImage = NetUtil.readImage(imageUrl);
			File thumbnailDirectory = new File(path, "thumbnail");
			DataUtil.writeImage(thumbnailDirectory, imageUrl.substring(imageUrl.lastIndexOf("/"), imageUrl.length()), thumbnailImage);
			LOG.debug("saved thumbnail image:" + imageUrl);
			result = imageUrl.substring(imageUrl.lastIndexOf("thumbnail"), imageUrl.length());
			thumbnailCount++;
		}
		if (jcUser.isBackupLarge()) {
			// download large image
			String largeImageUrl = imageUrl.replaceFirst("thumbnail", "large");
			byte[] largeImage = NetUtil.readImage(largeImageUrl);
			File largeDirectory = new File(path, "large");
			DataUtil.writeImage(largeDirectory, largeImageUrl.substring(largeImageUrl.lastIndexOf("/"), largeImageUrl.length()), largeImage);
			result = imageUrl.substring(imageUrl.lastIndexOf("thumbnail"), imageUrl.length());
			LOG.debug("saved large image:" + largeImageUrl);
			largeCount++;
		}
		return result;
	}

	@Override
	public JcUser deleteWeibo(JcUser jcUser) {
		this.jcUser = jcUser;
		UserTask userTask = userTaskPersistenceService.getUserTaskByKey(jcUser.getAccessToken());
		if (userTask == null || !userTask.getTask().isAlive()) {
			if (userTask == null)
				userTask = new UserTask();
			else
				userTaskPersistenceService.delete(userTask);
			tm.client.setToken(jcUser.getAccessToken());
			List<Status> statuses = weiboPersistenceService.getAllTop(jcUser);
			if (statuses.size() == 0) {
				jcUser.setDeleteCount(-1);
				return jcUser;
			}
			DeleteTask deleteTask = new DeleteTask(jcUser.getAccessToken());
			deleteTask.setStatues(statuses);
			deleteTask.start();
			// jcUser.setDeleteTask(deleteTask);
			userTask.setKey(jcUser.getAccessToken());
			userTask.setTask(deleteTask);
			userTaskPersistenceService.save(userTask);
		}
		jcUser.setDeleteCount(destroyedIndex + 1);
		jcUser.setOutOfLimit(outOfLimit);
		return this.jcUser;
	}

	class DeleteTask extends Thread {
		private List<Status> statuses = null;
		private String token;

		public DeleteTask(String name) {
			super(name);
			this.token = name;
		}

		public void setStatues(List<Status> statuses) {
			this.statuses = statuses;
		}

		public boolean containUndeleted(List<Status> statuses) {
			for (Status s : statuses) {
				if (!s.isDeleted())
					return true;
			}
			return false;
		}

		@Override
		public void run() {
			while (containUndeleted(statuses)) {
				LOG.debug("(Thread-" + Thread.currentThread().getName() + ")delete weibo");
				for (int i = 0; i < statuses.size(); i++) {
					Status s = statuses.get(i);
					if (s.isDeleted())
						continue;
					Status ds = null;
					try {
						if (!outOfLimit) {
							LOG.debug("deleting " + (destroyedIndex + 1) + "st  weibos, status.id=:" + s.getId());
							ds = tm.Destroy(s.getId());
							s.setDeleted(true);
							statuses.set(i, s); // in OOP(Object Oriented Programming), it will changes table(type of List) which in repository(type of Map)
							// weiboPersistenceService.save(statues); in procedure oriented programming, it should be save by PersistenceService;
							destroyedIndex++;
						} else {
							LOG.debug("not be deleted:status.id=" + s.getId());
						}

					} catch (WeiboException e) {
						String error = e.toString();
						if (error.indexOf("target weibo does not exist!") != -1)
							LOG.error("an error eccorred when delete weibo,status.id=" + s.getId() + " this weibo does not exist");
						else if (error.indexOf("User requests out of rate limit!") != -1) {
							LOG.error("an error(\"User requests out of rate limit!\") eccorred when delete weibo,status.id=" + s.getId() + "  " + e);
							outOfLimit = true;
//							ju.setOutOfLimit(true);
//							jcUserPersistenceService.delete(ju);
//							jcUserPersistenceService.save(ju);
							LOG.debug("follow ids are not be delete:");
						}
					}
				}
				 if (outOfLimit == true) {
//				if (!ju.isOutOfLimit()) {
					LOG.debug("take a break for a half hour,it has delete count=" + (destroyedIndex + 1));
					try {
						Thread.sleep(1800 * 1000);
						outOfLimit = false;
//						ju.setOutOfLimit(true);
//						jcUserPersistenceService.delete(ju);
//						jcUserPersistenceService.save(ju);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			weiboPersistenceService.deleteStatuses(statuses);
			LOG.debug("(Thread-" + Thread.currentThread().getName() + ")delete completed.");
			LOG.debug("(Thread-" + Thread.currentThread().getName() + ")delete completed.");
		}
	}

}
