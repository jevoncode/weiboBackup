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
public class WeiboDao extends DaoBase{
	private PreparedStatement pstmt =null;
	private static final Logger LOG = LoggerFactory.getLogger(WeiboDao.class);
	public WeiboDao(){
		super();
	}
	public void saveStatuses(List<Status> statuses) throws SQLException{
		for(Status s:statuses)
			saveStatus(s);
		conn.close();
	}

	public List<Status> getAll() {
		String sql = "SELECT created_at,"+
						"weibo_text,"+
						"in_reply_to_screen_name,"+
						"thumbnail_pic,"+
						"geo,"+
						"longitude,"+
						"reposts_count,"+
						"comments_count"+
						" FROM weibo ORDER BY created_at desc;";
		LOG.debug("get all weibo,sql:"+sql);
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
				statuses.add(s);
				//LOG.debug("get Weibo from database:"+s.getText());
			}
			rs.close();
			pstmt.close();
			conn.close();
		}catch(SQLException e){
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

	public Long saveStatus(Status s) throws SQLException{
		Long id = null;
		if(s.getRetweetedStatus()!=null){
			id = saveStatus(s.getRetweetedStatus());  
		}
		Source source = s.getSource();
		Visible visible = s.getVisible();
		Long sourceId = null;
		Long visibleId = null; 
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
					"created_time=?;";
			
		LOG.debug("begin save Weibo just swooped,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setTimestamp(1,new Timestamp(s.getCreatedAt().getTime()));
		pstmt.setString(2,s.getId());
		pstmt.setString(3,s.getMid());
		pstmt.setLong(4,s.getIdstr());
		pstmt.setString(5,s.getText());
		pstmt.setLong(6,sourceId==null?0:sourceId);
		pstmt.setString(7,s.isFavorited()==true?"Y":"N");
		pstmt.setString(8,s.isTruncated()==true?"Y":"N");
		pstmt.setLong(9,s.getInReplyToStatusId());
		pstmt.setLong(10,s.getInReplyToStatusId());
		pstmt.setString(11,s.getInReplyToScreenName());
		pstmt.setString(12,s.getThumbnailPic());
		pstmt.setString(13,s.getBmiddlePic());
		pstmt.setString(14,s.getOriginalPic());
		pstmt.setLong(15,id==null?0:id);  
		pstmt.setString(16,s.getGeo());
		pstmt.setDouble(17,s.getLongitude());
		pstmt.setDouble(18,s.getLatitude());
		pstmt.setInt(19,s.getRepostsCount());
		pstmt.setInt(20,s.getCommentsCount());
		pstmt.setString(21,s.getAnnotations());
		pstmt.setInt(22,s.getMlevel());
		pstmt.setLong(23,visibleId==null?0:visibleId);
		pstmt.setTimestamp(24,new Timestamp((new Date()).getTime()));				
		pstmt.executeUpdate();
		pstmt.close();
		
		sql = "select id from weibo w where w.weibo_id = ?;";		
		LOG.debug("select id of WEIBO,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,s.getId());
		ResultSet rs = 	pstmt.executeQuery();
		while(rs.next()){
			id = rs.getLong(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
	public Long saveSource(Source s) throws SQLException{
		Long id = null;
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
			id = rs.getLong(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
	public Long saveVisible(Visible v) throws SQLException{
		Long id = null;
		String sql = "insert into visible "+
					" set visible_type= ?,"+
					" list_id= ?;";
		LOG.debug("insert VISIBLE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1,v.getType());
		pstmt.setInt(2,v.getList_id());
		pstmt.executeUpdate();
		pstmt.close();
		
		sql = "select id from visible v where v.visible_type=?"+
				" and v.list_id=?;";
		LOG.debug("select id of VISIBLE,sql:"+sql);
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1,v.getType());
		pstmt.setInt(2,v.getList_id());
		ResultSet rs = 	pstmt.executeQuery();
		while(rs.next()){
			id = rs.getLong(1);
		}
		rs.close();
		pstmt.close();
		return id;
	}
}
