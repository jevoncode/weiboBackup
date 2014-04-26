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

public class Place {
	private HttpClient client = null;

	public Place() {
		this(150, 30000, 30000);
	}

	public Place(int maxConPerHost, int conTimeOutMs, int soTimeOutMs) {
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

	public String formattedAddress(double latitude, double longitude) throws HttpException, IOException, JSONException {
		String result = "";
		String url = "http://api.map.baidu.com/geocoder/v2/?ak=51cc1b314ffcf13cef06f57ea33ec11f";
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("&callback=renderReverse&location=");
		sb.append(latitude);
		sb.append(",");
		sb.append(longitude);
		sb.append("&output=json&pois=1");
		url = sb.toString();
		GetMethod method = new GetMethod(url);
		client.executeMethod(method);
		Header[] resHeader = method.getResponseHeaders();
		int responseCode = method.getStatusCode();
		if (responseCode == 200) {
			String content = method.getResponseBodyAsString();
			content = content.substring(content.indexOf("(") + 1, content.lastIndexOf(")"));
			JSONObject json = new JSONObject(content);
			result = json.getJSONObject("result").getString("formatted_address");
		}
		return result;
	}
}