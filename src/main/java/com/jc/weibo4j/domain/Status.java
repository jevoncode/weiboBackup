package com.jc.weibo4j.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.IOException; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jc.weibo4j.http.Response;
import com.jc.weibo4j.json.JSONArray;
import com.jc.weibo4j.json.JSONException;
import com.jc.weibo4j.json.JSONObject;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.core.domain.JcUser;
import com.jc.util.StringUtil;
import com.jc.baidu4j.service.HttpUrl;

public class Status extends WeiboResponse implements java.io.Serializable {

	private static final long serialVersionUID = -8795691786466526420L;

	private User user = null; // 作者信息
	private JcUser jcUser = null; // user who uses jc's app
	private Date createdAt; // status创建时间
	private String id; // status id
	private String mid; // 微博MID
	private long idstr; // 保留字段，请勿使用
	private String text; // 微博内容
	private Source source; // 微博来源
	private boolean favorited; // 是否已收藏
	private boolean truncated;
	private long inReplyToStatusId; // 回复ID
	private long inReplyToUserId; // 回复人ID
	private String inReplyToScreenName; // 回复人昵称
	private String thumbnailPic; // 微博内容中的图片的缩略地址
	private String bmiddlePic; // 中型图片
	private String originalPic; // 原始图片
	private Status retweetedStatus = null; // 转发的博文，内容为status，如果不是转发，则没有此字段
	private String geo; // 地理信息，保存经纬度，没有时不返回此字段
	private double latitude = -1; // 纬度
	private double longitude = -1; // 经度
	private int repostsCount; // 转发数
	private int commentsCount; // 评论数
	private String annotations; // 元数据，没有时不返回此字段
	private int mlevel;
	private Visible visible;
	private String mediaList = "";
	private String[] picUrls;
	private List<Comment> comments;
	private String commentList = "";
	private String formatedAddress = "";
	private boolean deleted = false;

	public Status() {

	}

	public Status(Response res) throws WeiboException {
		super(res);
		JSONObject json = res.asJSONObject();
		constructJson(json);
	}

	private void constructJson(JSONObject json) throws WeiboException {
		try {
			createdAt = parseDate(json.getString("created_at"), "EEE MMM dd HH:mm:ss z yyyy");
			id = json.getString("id");
			mid = json.getString("mid");
			idstr = json.getLong("idstr");
			text = json.getString("text");
			text = refactor(text);
			if (!json.getString("source").isEmpty()) {
				source = new Source(json.getString("source"));
			}
			inReplyToStatusId = getLong("in_reply_to_status_id", json);
			inReplyToUserId = getLong("in_reply_to_user_id", json);
			inReplyToScreenName = json.getString("in_reply_toS_screenName");
			favorited = getBoolean("favorited", json);
			truncated = getBoolean("truncated", json);
			thumbnailPic = json.getString("thumbnail_pic");
			bmiddlePic = json.getString("bmiddle_pic");
			originalPic = json.getString("original_pic");
			repostsCount = json.getInt("reposts_count");
			commentsCount = json.getInt("comments_count");
			annotations = json.getString("annotations");
			if (!json.isNull("user"))
				user = new User(json.getJSONObject("user"));
			if (!json.isNull("retweeted_status")) {
				retweetedStatus = new Status(json.getJSONObject("retweeted_status"));
			}
			mlevel = json.getInt("mlevel");
			geo = json.getString("geo");
			if (geo != null && !"".equals(geo) && !"null".equals(geo)) {
				getGeoInfo(geo);
			}
			if (!json.isNull("visible")) {
				visible = new Visible(json.getJSONObject("visible"));
			}
			if (!json.isNull("pic_urls")) {
				JSONArray ja = json.getJSONArray("pic_urls");
				picUrls = new String[ja.length()];
				for (int i = 0; i < ja.length(); i++)
					picUrls[i] = ja.optJSONObject(i).getString("thumbnail_pic");
				setPicUrls(picUrls);
			}
		} catch (JSONException je) {
			throw new WeiboException(je.getMessage() + ":" + json.toString(), je);
		}
	}

	private void getGeoInfo(String geo) {
		StringBuffer value = new StringBuffer();
		for (char c : geo.toCharArray()) {
			if (c > 45 && c < 58) {
				value.append(c);
			}
			if (c == 44) {
				if (value.length() > 0) {
					latitude = Double.parseDouble(value.toString());
					value.delete(0, value.length());
				}
			}
		}
		longitude = Double.parseDouble(value.toString());
	}

	private String refactor(String text) {
		int index = text.indexOf("http://t.cn/");
		if (index != -1) {
			List<String> urls = new ArrayList<String>();
			Pattern pattern = Pattern.compile("http://t.cn/[a-zA-Z0-9]+");
			Matcher matcher = pattern.matcher(text);
			HttpUrl httpUrl = new HttpUrl();
			while (matcher.find()) {
				String url = matcher.group();
				String redirectedUrl = "";
				try {
					redirectedUrl = httpUrl.translate(url);
				} catch (IOException e) {
					System.out.println("occurred a error when translating short url" + e);
					System.out.println("try again:");
					try {
						redirectedUrl = httpUrl.translate(url);
					} catch (IOException e2) {
						System.out.println("occurred a error when translating short url" + e2);
					}
				}
				StringBuffer sb = new StringBuffer();
				sb.append("<a href=\"");
				sb.append(redirectedUrl);
				sb.append("\" target=\"_blank\">");
				sb.append(url);
				sb.append("</a>");
				text = text.replaceFirst(url, sb.toString());
			}

		}
		return text;
	}

	public Status(JSONObject json) throws WeiboException, JSONException {
		constructJson(json);
	}

	public Status(JSONObject json, JcUser jcUser) throws WeiboException, JSONException {
		setJcUser(jcUser);
		constructJson(json);
	}

	public Status(String str) throws WeiboException, JSONException {
		// StatusStream uses this constructor
		super();
		JSONObject json = new JSONObject(str);
		constructJson(json);
	}

	public void setPicUrls(String[] picUrls) {
		this.picUrls = picUrls;
		if (picUrls == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		if (picUrls.length > 1) { // GridView
			sb.append("<div class=\"lotspic_Media clearfix\" ><ul class=\"lotspic_list clearfix\">");
			for (int i = 0; i < picUrls.length; i++) {
				sb.append("<li><a href=\"");
				sb.append(picUrls[i].replaceFirst("thumbnail", "large"));
				sb.append("\" class=\"gallery_1\"><img class=\"bigcursor\" src=\"");
				sb.append(picUrls[i]);
				sb.append("\" /></a></li>");
				// sb.append("<li><a href=\"large/6e609eedgw1ee3pyod15wj20eg0ssaem.jpg"
				// class="gallery_1"><img
				// class=\"bigcursor\" src=\""+picUrls[i]+"\" /></a></li>");
			}
			sb.append("</ul></div>");
		} else if (picUrls.length > 0) {// only one picture
			sb.append("<div class=\"chePicMin S_bg2 bigcursor\">");
			sb.append("<a href=\"");
			sb.append(picUrls[0].replaceFirst("thumbnail", "large"));
			sb.append("\" class=\"gallery_1\"><img class=\"bigcursor\" src=\"");
			sb.append(picUrls[0]);
			sb.append("\" /></a></div>");
			// sb.append("<img class=\"bigcursor\" src=\""+picUrls[0]+ "\" />");
			// sb.append("</div>");
		}
		setMediaList(sb.toString());
	}

	public boolean contains(JcUser jcUser) {
		return this.jcUser.getAccessToken().equals(jcUser.getAccessToken());
	}

	public void setJcUser(JcUser jcUser) {
		this.jcUser = jcUser;
	}

	public JcUser getJcUser() {
		return jcUser;
	}

	public String[] getPicUrls() {
		return picUrls;
	}

	public void setMediaList(String mediaList) {
		this.mediaList = mediaList;
	}

	public String getMediaList() {
		return mediaList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getIdstr() {
		return idstr;
	}

	public void setIdstr(long idstr) {
		this.idstr = idstr;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public boolean isFavorited() {
		return favorited;
	}

	public void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	public String getThumbnailPic() {
		return thumbnailPic;
	}

	public void setThumbnailPic(String thumbnailPic) {
		this.thumbnailPic = thumbnailPic;
	}

	public String getBmiddlePic() {
		return bmiddlePic;
	}

	public void setBmiddlePic(String bmiddlePic) {
		this.bmiddlePic = bmiddlePic;
	}

	public String getOriginalPic() {
		return originalPic;
	}

	public void setOriginalPic(String originalPic) {
		this.originalPic = originalPic;
	}

	public Status getRetweetedStatus() {
		return retweetedStatus;
	}

	public void setRetweetedStatus(Status retweetedStatus) {
		this.retweetedStatus = retweetedStatus;
	}

	/**
	 * <span class="W_icol6 icon_locate"></span> ${status.geo}
	 * <a>经度：${status.longitude}，纬度：${status.latitude}</a>
	 */
	public String getGeo() {
		return geo;
	}

	public void setGeo(String geo) {
		this.geo = geo;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getRepostsCount() {
		return repostsCount;
	}

	public void setRepostsCount(int repostsCount) {
		this.repostsCount = repostsCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getAnnotations() {
		return annotations;
	}

	public void setAnnotations(String annotations) {
		this.annotations = annotations;
	}

	public int getMlevel() {
		return mlevel;
	}

	public void setMlevel(int mlevel) {
		this.mlevel = mlevel;
	}

	public Visible getVisible() {
		return visible;
	}

	public void setVisible(Visible visible) {
		this.visible = visible;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	/**
	 * 
	 * 
	 * <dl comment_id="3702663675734729" class="comment_list S_line1">
	 * <dt>
	 * <a href="/wjmyth"><img width="30" height="30" alt="阳光肥杰"
	 * src="http://tp3.sinaimg.cn/1810460762/50/5687593528/1"
	 * usercard="id=1810460762"></a></dt>
	 * <dd>
	 * <a href="/wjmyth">阳光肥杰</a> ：回复<a href="/n/haru%E5%B9%B4"
	 * usercard="name=haru年">@haru年</a>:其实XXL也挺帅的 <a href="/n/%E6%9C%88SHY"
	 * usercard="name=月SHY">@月SHY</a> (4月23日 20:29)</dd>
	 * </dl>
	 * 
	 */
	public void setComments(List<Comment> comments) {
		this.comments = comments;
		if (comments == null)
			return;
		StringBuffer sb = new StringBuffer();
		for (Comment c : comments) {
			sb.append("<dl comment_id=\"");
			sb.append(c.getIdstr());
			sb.append("\" class=\"comment_list S_line1\"><dt><a href=\"http://weibo.com/");
			sb.append(c.getUser().getProfileUrl());
			sb.append("\"  target=\"_blank\"><img width=\"30\" height=\"30\" alt=\"");
			sb.append(c.getUser().getScreenName());
			sb.append("\" src=\"");
			sb.append(c.getUser().getProfileImageURL());
			sb.append("\" usercard=\"");
			sb.append(c.getUser().getId());
			sb.append("\"/></a></dt><dd><a href=\"http://weibo.com/");
			sb.append(c.getUser().getProfileUrl());
			sb.append("\"  target=\"_blank\">");
			sb.append(c.getUser().getScreenName());
			sb.append("</a>");
			sb.append("：");
			sb.append(c.getText());
			sb.append("(");
			sb.append(StringUtil.formatDate(c.getCreatedAt(), "yyyy-MM-dd HH:mm"));
			sb.append(")");
			sb.append("</dd></dl>");
		}
		setCommentList(sb.toString());
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setCommentList(String commentList) {
		this.commentList = commentList;
	}

	public String getCommentList() {
		return commentList;
	}
	
	public void setFormatedAddress(String formatedAddress){
		if(formatedAddress==null||formatedAddress.length()==0)
			return;
		StringBuffer sb = new StringBuffer();
		sb.append("<span class=\"W_icol6 icon_locate\"></span>");
		sb.append(formatedAddress);
		sb.append(" (经度：");
		sb.append(longitude);
		sb.append("，纬度：");
		sb.append(latitude);
		sb.append(")");
		this.formatedAddress = sb.toString();
	}
	public String getFormatedAddress(){
		return formatedAddress;
	}
	
	public void setDeleted(boolean deleted){
		this.deleted = deleted;
	}
	public boolean isDeleted(){
		return deleted;
	}

	public static StatusWapper constructWapperStatus(Response res) throws WeiboException {
		JSONObject jsonStatus = res.asJSONObject(); // asJSONArray();
		JSONArray statuses = null;
		try {
			if (!jsonStatus.isNull("statuses")) {
				statuses = jsonStatus.getJSONArray("statuses");
			}
			if (!jsonStatus.isNull("reposts")) {
				statuses = jsonStatus.getJSONArray("reposts");
			}
			int size = statuses.length();
			List<Status> status = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				status.add(new Status(statuses.getJSONObject(i)));
			}
			long previousCursor = jsonStatus.getLong("previous_curosr");
			long nextCursor = jsonStatus.getLong("next_cursor");
			long totalNumber = jsonStatus.getLong("total_number");
			String hasvisible = jsonStatus.getString("hasvisible");
			return new StatusWapper(status, previousCursor, nextCursor, totalNumber, hasvisible);
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		}
	}

	public static StatusWapper constructWapperStatus(Response res, JcUser jcUser) throws WeiboException {
		JSONObject jsonStatus = res.asJSONObject(); // asJSONArray();
		JSONArray statuses = null;
		try {
			if (!jsonStatus.isNull("statuses")) {
				statuses = jsonStatus.getJSONArray("statuses");
			}
			if (!jsonStatus.isNull("reposts")) {
				statuses = jsonStatus.getJSONArray("reposts");
			}
			int size = statuses.length();
			List<Status> status = new ArrayList<Status>(size);
			for (int i = 0; i < size; i++) {
				status.add(new Status(statuses.getJSONObject(i), jcUser));
			}
			long previousCursor = jsonStatus.getLong("previous_curosr");
			long nextCursor = jsonStatus.getLong("next_cursor");
			long totalNumber = jsonStatus.getLong("total_number");
			String hasvisible = jsonStatus.getString("hasvisible");
			return new StatusWapper(status, previousCursor, nextCursor, totalNumber, hasvisible);
		} catch (JSONException jsone) {
			throw new WeiboException(jsone);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Status other = (Status) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Status [user=" + user + ", idstr=" + idstr + ", createdAt=" + createdAt + ", id=" + id + ", text=" + text + ", source="
				+ source + ", favorited=" + favorited + ", truncated=" + truncated + ", inReplyToStatusId=" + inReplyToStatusId
				+ ", inReplyToUserId=" + inReplyToUserId + ", inReplyToScreenName=" + inReplyToScreenName + ", thumbnailPic="
				+ thumbnailPic + ", bmiddlePic=" + bmiddlePic + ", originalPic=" + originalPic + ", retweetedStatus=" + retweetedStatus
				+ ", geo=" + geo + ", latitude=" + latitude + ", longitude=" + longitude + ", repostsCount=" + repostsCount
				+ ", commentsCount=" + commentsCount + ", mid=" + mid + ", annotations=" + annotations + ", mlevel=" + mlevel
				+ ", visible=" + visible + "]";
	}

}
