package com.jc.model;

import java.util.Date;

public class JcUser{
	private Long id;
	private String session;
	private String code;
	private String accessToken;
	private int expiresIn;
	private long uid;
	private Date createdTime;
	public void setId(Long id){
		this.id = id;
	}
	public Long getId(){
		return this.id;
	}
	public void setSession(String session){
		this.session = session;
	}
	public String getSession(){
		return this.session;
	}
	public void setAccessToken(String accessToken){
		this.accessToken = accessToken;
	}
	public String getAccessToken(){
   		return this.accessToken;
	}
	public void setCode(String code){
		this.code = code;
	}
	public String getCode(){
		return this.code;
	}
	public void setCreatedTime(Date createdTime){
		this.createdTime = createdTime;
	}
	public Date getCreatedTime(){
		return this.createdTime;
	}
	public void setExpiresIn(int expiresIn){
		this.expiresIn = expiresIn;
	}
	public int getExpiresIn(){
		return this.expiresIn;
	}
	public void setUid(long uid){
		this.uid = uid;
	}
	public long getUid(){
		return this.uid;
	}
}
