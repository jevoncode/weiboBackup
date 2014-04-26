package com.jc.baidu4j.service;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import com.jc.weibo4j.json.JSONException;
import com.jc.weibo4j.json.JSONObject;

public class HttpUrl {
	private HttpClient client = null;

	public HttpUrl() {
		this(150, 30000, 30000);
	}

	public HttpUrl(int maxConPerHost, int conTimeOutMs, int soTimeOutMs) {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setDefaultMaxConnectionsPerHost(maxConPerHost);
		params.setConnectionTimeout(conTimeOutMs);
		params.setSoTimeout(soTimeOutMs);

		HttpClientParams clientParams = new HttpClientParams();
		// 忽略cookie 避免 Cookie rejected 警告
		clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		client = new HttpClient(clientParams, connectionManager);
	}

	public String translate(String url) throws IOException {
		GetMethod method = new GetMethod(url);
		method.setFollowRedirects(false);
		client.executeMethod(method);
		String result = method.getResponseHeader("location").getValue();
		return result;
	}
}