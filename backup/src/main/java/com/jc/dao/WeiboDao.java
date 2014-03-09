package com.jc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import weibo4j.model.Status;
import weibo4j.model.Source;
import weibo4j.model.Visible;
import weibo4j.model.User;
import com.jc.util.StringUtil;
public class WeiboDao extends DaoBase{
	public static int saveCount = 0;
	private PreparedStatement pstmt =null;
	private static final Logger LOG = LoggerFactory.getLogger(WeiboDao.class);
	public WeiboDao(){
		super();
	}
	public void saveStatuses(List<Status> statuses) {
		Status s = null;
		try{
			for(int i=0;i<statuses.size();i++){
				s = statuses.get(i);
				LOG.debug("jc before filter emoji text=:\""+s.getText()+"\"");
				LOG.debug("jc after filter emoji text=:\""+StringUtil.filterEmoji(s.getText())+"\"");
				int flag = saveStatus(s);
				if(flag==-1)
					throw new SQLException("jc -1 SQLException,it means save one weibo failure");
			}
			conn.close();
		}catch(SQLException e){
			if(s!=null)
				LOG.error("occurrence error when save weibo:"+s);
			else
				LOG.error("occurrence error when save weibo:null");
			e.printStackTrace();
		}finally{
			try{
			conn.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}

	public List<Status> getAllTop() {
		String sql = "SELECT created_at,"+
						"w.weibo_text,"+
						"w.in_reply_to_screen_name,"+
						"w.thumbnail_pic,"+
						"w.geo,"+
						"w.longitude,"+
						"w.reposts_count,"+
						"w.comments_count,"+
						"w.retweeted_weibo_id,"+
						"w.user_id"+
						" FROM weibo w"+
						" where not exists("+
						" 		select 1 from weibo w2 "+
						" 			where w.id = w2.retweeted_weibo_id) "+
						" ORDER BY w.created_at desc;";
		LOG.debug("get all top weibo,sql:"+sql);
		Status s = null;
		List<Status> statuses = new ArrayList<Status>();
		try{
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				s = new Status();
				s.setCreatedAt(new Date(rs.getTimestamp(1).getTime()));
				s.setText(rs.getString(2));
				s.setInReplyToScreenName(rs.getString(3));
				s.setThumbnailPic(rs.getString(4));
				s.setGeo(rs.getString(5));
				s.setLongitude(rs.getDouble(6));
				s.setRepostsCount(rs.getInt(7));
				s.setCommentsCount(rs.getInt(8));
				int retweetedId = rs.getInt(9);
				int userId = rs.getInt(10);
				if(retweetedId!=-1){
					try{
					String retweetedSql = "SELECT w.created_at,"+
						"w.weibo_text,"+
						"w.in_reply_to_screen_name,"+
						"w.thumbnail_pic,"+
						"w.geo,"+
						"w.longitude,"+
						"w.reposts_count,"+
						"w.comments_count "+
						" FROM weibo w"+
						" WHERE w.id =?;";
					LOG.debug("get retweeted WeiBo:"+retweetedSql);
					PreparedStatement retweetedPstmt = conn.prepareStatement(retweetedSql);
					retweetedPstmt.setInt(1,retweetedId);
					ResultSet rsR = retweetedPstmt.executeQuery();
					while(rsR.next()){
						Status retweeted = new Status();
						retweeted.setCreatedAt(new Date(rsR.getTimestamp(1).getTime()));
						retweeted.setText(rsR.getString(2));
						retweeted.setInReplyToScreenName(rsR.getString(3));
						retweeted.setThumbnailPic(rsR.getString(4));
						retweeted.setGeo(rsR.getString(5));
						retweeted.setLongitude(rsR.getDouble(6));
						retweeted.setRepostsCount(rsR.getInt(7));
						retweeted.setCommentsCount(rsR.getInt(8));
						s.setRetweetedStatus(retweeted);
					}
					rsR.close();
					retweetedPstmt.close();
					}catch(SQLException e){
						LOG.error("occurrence error when get a reweetedStatus,her id is :"+retweetedId);
						throw e;
					}
				}
				if(userId!=-1){
					try{
					String userSql = "SELECT u.origin_id,"+
							"u.screen_name,"+
							"u.name,"+
							"u.province,"+
							"u.city,"+
							"u.location,"+
							"u.description,"+
							"u.url,"+
							"u.profile_image_url,"+
							"u.domain,"+
							"u.gender,"+
							"u.followers_count,"+
							"u.friends_count,"+
							"u.statuses_count,"+
							"u.favourites_count,"+
							"u.created_at,"+
							"u.following,"+
							"u.verified,"+
							"u.verified_type"+
							" FROM user u"+
							" WHERE u.id=?;";
					LOG.debug("get weibo's owner,sql:"+userSql);
					PreparedStatement userPstmt = conn.prepareStatement(userSql);
					userPstmt.setInt(1,userId);
					ResultSet ur =  userPstmt.executeQuery();
					while(ur.next()){
						User user = new User();
						user.setId(ur.getString(1));
						user.setScreenName(ur.getString(2));
						user.setName(ur.getString(3));
						user.setProvince(ur.getInt(4));
						user.setCity(ur.getInt(5));
						user.setLocation(ur.getString(6));
						user.setDescription(ur.getString(7));
						user.setUrl(ur.getString(8));
						user.setProfileImageUrl(ur.getString(9));
						user.setUserDomain(ur.getString(10));
						user.setGender(ur.getString(11));
						user.setFollowersCount(ur.getInt(12));
						user.setFriendsCount(ur.getInt(13));
						user.setStatusesCount(ur.getInt(14));
						user.setFavouritesCount(ur.getInt(15));
						user.setCreatedAt(new Date(ur.getTimestamp(16).getTime()));
						user.setFollowing("Y".equals(ur.getString(17)));
						user.setVerified("Y".equals(ur.getString(18)));
						user.setVerifiedType(ur.getInt(19));
						s.setUser(user);
					}
					ur.close();
					userPstmt.close();
					}catch(SQLException e){
						LOG.error("occurrence a error when get a user,her id is"+userId);
						throw e;
					}
				}
				statuses.add(s);
				//LOG.debug("get Weibo from database:"+s.getText());
			}
			rs.close();
			pstmt.close();
			conn.close();
		}catch(SQLException e){
			LOG.error("occurrence a error when get a status:"+s);
			e.printStackTrace();
		}finally{
			try{
				pstmt.close();
				conn.close();
			}catch(SQLException e2){
				e2.printStackTrace();
			}
		}
		return statuses;
	}	

	public int saveStatus(Status s) throws SQLException{
		int id = -1;
		int userId =-1 ;
		if(s.getRetweetedStatus()!=null){
			LOG.debug("jc before filter emoji text=:\""+s.getRetweetedStatus().getText()+"\"");
			LOG.debug("jc after filter emoji text=:\""+StringUtil.filterEmoji(s.getRetweetedStatus().getText())+"\"");
			id = saveStatus(s.getRetweetedStatus()); 
		}
		if(s.getUser()!=null)
			userId = saveUser(s.getUser()); 
		Source source = s.getSource();
		Visible visible = s.getVisible();
		int sourceId = -1;
		int visibleId = -1; 
		if(source!=null)
			sourceId = saveSource(source); 
		if(visible!=null)
			visibleId = saveVisible(visible);
		String sql = "insert into weibo "+
					"set created_at =?,"+
					"weibo_id = ?,"+
					"mid = ?,"+
					"idstr = ?,"+
					"weibo_text =?,"+
					"weibo_source_id=?,"+
					"favorited =?,"+
					"truncated =?,"+
					"in_reply_to_status_id =?,"+
					"in_reply_to_user_id =?,"+
					"in_reply_to_screen_name =?,"+
					"thumbnail_pic =?,"+
					"bmiddle_pic =?,"+
					"orginal_pic =?,"+
					"retweeted_weibo_id=?,"+
					"geo =?,"+
					"longitude=?,"+
					"latitude=?,"+
					"reposts_count=?,"+
					"comments_count=?,"+
					"annotation=?,"+	
					"mlevel=?,"+
					"visible_id=?,"+
					"user_id =?,"+
					"created_time=?;";
			
		LOG.debug("begin save Weibo just swooped,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setTimestamp(1,new Timestamp(s.getCreatedAt().getTime()));
		pstmt.setString(2,s.getId());
		pstmt.setString(3,s.getMid());
		pstmt.setLong(4,s.getIdstr());
		pstmt.setString(5,StringUtil.filterEmoji(s.getText()));
		pstmt.setInt(6,sourceId);
		pstmt.setString(7,s.isFavorited()==true?"Y":"N");
		pstmt.setString(8,s.isTruncated()==true?"Y":"N");
		pstmt.setLong(9,s.getInReplyToStatusId());
		pstmt.setLong(10,s.getInReplyToStatusId());
		pstmt.setString(11,s.getInReplyToScreenName());
		pstmt.setString(12,s.getThumbnailPic());
		pstmt.setString(13,s.getBmiddlePic());
		pstmt.setString(14,s.getOriginalPic());
		pstmt.setInt(15,id);  
		pstmt.setString(16,s.getGeo());
		pstmt.setDouble(17,s.getLongitude());
		pstmt.setDouble(18,s.getLatitude());
		pstmt.setInt(19,s.getRepostsCount());
		pstmt.setInt(20,s.getCommentsCount());
		pstmt.setString(21,s.getAnnotations());
		pstmt.setInt(22,s.getMlevel());
		pstmt.setInt(23,visibleId);
		pstmt.setInt(24,userId);
		pstmt.setTimestamp(25,new Timestamp((new Date()).getTime()));				
		pstmt.executeUpdate();
		pstmt.close();
		saveCount++;	
		sql = "select id from weibo w where w.weibo_id = ?;";		
		LOG.debug("select id of WEIBO,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,s.getId());
		ResultSet rs = 	pstmt.executeQuery();
		while(rs.next()){
			id = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
	public int saveSource(Source s) throws SQLException{
		String sql = "insert into weibo_source "+
					"set url= ?,"+
					"relation_ship = ?,"+
					"weibo_source_name = ?;";
		LOG.debug("insert SOURCE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,s.getUrl());
		pstmt.setString(2,s.getRelationship());
		pstmt.setString(3,s.getName());
		pstmt.executeUpdate();
		pstmt.close();
		
		int id = -1;
		sql = "select id from weibo_source s where s.url=?"+
			" and s.relation_ship=?"+
			" and weibo_source_name=?;";
		LOG.debug("select id of SOURCE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,s.getUrl());
		pstmt.setString(2,s.getRelationship());
		pstmt.setString(3,s.getName());
		ResultSet rs = 	pstmt.executeQuery();
		while(rs.next()){
			id = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
	public int saveVisible(Visible v) throws SQLException{
		String sql = "insert into visible "+
					" set visible_type= ?,"+
					" list_id= ?;";
		LOG.debug("insert VISIBLE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1,v.getType());
		pstmt.setInt(2,v.getList_id());
		pstmt.executeUpdate();
		pstmt.close();
	
		int id = -1;	
		sql = "select id from visible v where v.visible_type=?"+
				" and v.list_id=?;";
		LOG.debug("select id of VISIBLE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1,v.getType());
		pstmt.setInt(2,v.getList_id());
		ResultSet rs = 	pstmt.executeQuery();
		while(rs.next()){
			id = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
	
	public int saveUser(User user) throws SQLException{
		String sql = "INSERT INTO user "+
					"set origin_id=?,"+
					"screen_name=?,"+
					"name=?,"+
					"province=?,"+
					"city=?,"+
					"location=?,"+
					"description=?,"+
					"url=?,"+
					"profile_image_url=?,"+
					"domain=?,"+
					"gender=?,"+
					"followers_count=?,"+
					"statuses_count=?,"+
					"favourites_count=?,"+
					"created_at=?,"+
					"following=?,"+
					"verified=?,"+
					"verified_type=?,"+
					"created_time=?;";
		LOG.debug("save weibo user,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,user.getId());
		pstmt.setString(2,user.getScreenName());
		pstmt.setString(3,user.getName());
		pstmt.setInt(4,user.getProvince());
		pstmt.setInt(5,user.getCity());
		pstmt.setString(6,user.getLocation());
		pstmt.setString(7,StringUtil.filterEmoji(user.getDescription()));
		pstmt.setString(8,user.getUrl());
		pstmt.setString(9,user.getProfileImageUrl());
		pstmt.setString(10,user.getUserDomain());
		pstmt.setString(11,user.getGender());
		pstmt.setInt(12,user.getFollowersCount());
		pstmt.setInt(13,user.getStatusesCount());
		pstmt.setInt(14,user.getFavouritesCount());
		pstmt.setTimestamp(15,new Timestamp(user.getCreatedAt().getTime()));
		pstmt.setString(16,user.isFollowing()==true?"Y":"N");
		pstmt.setString(17,user.isVerified()==true?"Y":"N");
		pstmt.setInt(18,user.getverifiedType());
		pstmt.setTimestamp(19,new Timestamp(new Date().getTime()));
		pstmt.executeUpdate();
		pstmt.close();

		int id = -1;
		sql = "SELECT id FROM user WHERE origin_id=?;";
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,user.getId());
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			id = rs.getInt(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
}
