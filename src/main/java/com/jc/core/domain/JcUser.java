package com.jc.core.domain;

import java.util.Date;
import java.io.Serializable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="session",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class JcUser implements Serializable {
	private Long id;
	private String session;
	private String code;
	private String accessToken;
	private int expiresIn;
	private long uid;
	private Date createdTime;
	private String verificationCode;
	private String zipPath;
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
	public void setVerificationCode(String verificationCode){
		this.verificationCode = verificationCode;
	}
	public String getVerificationCode(){
		return verificationCode;
	}
	public void setZipPath(String zipPath){
		this.zipPath = zipPath;
	}
	public String getZipPath(){
		return zipPath;
	}
}
