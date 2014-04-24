package com.jc.core.domain;

public class State {
	private int thumbnailCount;
	private int largeCount;
	private int commentCount;
	private int weiboCount;

	public void setThumbnailCount(int thumbnailCount) {
		this.thumbnailCount = thumbnailCount;
	}

	public int getThumbnailCount() {
		return thumbnailCount;
	}

	public void setLargeCount(int largeCount) {
		this.largeCount = largeCount;
	}

	public int getLargeCount() {
		return largeCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setWeiboCount(int weiboCount) {
		this.weiboCount = weiboCount;
	}

	public int getWeiboCount() {
		return weiboCount;
	}
}