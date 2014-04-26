package com.jc.weibo4j.service;

import com.jc.weibo4j.domain.Comment;
import com.jc.weibo4j.domain.CommentWapper;
import com.jc.weibo4j.domain.PostParameter;
import com.jc.weibo4j.exception.WeiboException;
import com.jc.weibo4j.util.WeiboConfig;


public class Comments extends Weibo{
	
	private static final long serialVersionUID = 3321231200237418256L;

	/**
	 * 根据微博ID返回某条微博的评论列表
	 * 
	 * @param id
	 *            需要查询的微博ID
	 * @return list of Comment default 200(already it is the max count weibo api can support)
	 * @throws WeiboException
	 *             when Weibo service or network is unavailable
	 * @see <a
	 *      href="http://open.weibo.com/wiki/2/comments/show">comments/show</a>
	 * @since JDK 1.5
	 * @author JevonCode
	 */
	public CommentWapper getCommentById(String id) throws WeiboException {
		return Comment.constructWapperComments(client.get(
				WeiboConfig.getValue("baseURL") + "comments/show.json",
				new PostParameter[] { new PostParameter("id", id), new PostParameter("count", 200) }));
	}
}