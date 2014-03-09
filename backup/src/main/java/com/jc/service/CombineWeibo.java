package com.jc.service;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weibo4j.model.Status;
import com.jc.dao.WeiboDao;
import com.jc.util.DataUtil;

public class CombineWeibo{
	private static final Logger LOG = LoggerFactory.getLogger(CombineWeibo.class);

	public String getValue(Object obj,String name){
		String value = null;
		String[] args = name.split("\\.");
		int argLen = args.length;
		String objName = upperCaseFirst(args[0]);
		Class clazz = obj.getClass();
		int index = clazz.getName().indexOf(objName);
		LOG.debug("obeject'name index of className:"+index);
		LOG.debug("object is:"+objName+" and length of object'name:"+objName.length());
		LOG.debug("className:"+clazz.getName()+" className's length:"+clazz.getName().length());
		try{
			Method m = clazz.getMethod("get"+upperCaseFirst(args[argLen-1]));
			value = String.valueOf(m.invoke(obj));
			LOG.debug("getValue name:"+name+" and corresponding value:"+value);
		}catch(NoSuchMethodException e1){
			LOG.debug("No such method exception");
			e1.printStackTrace();
		}catch(IllegalAccessException e2){
			LOG.debug("Illegal access exception");
			e2.printStackTrace();
		}catch(IllegalArgumentException e3){
			LOG.debug("Illegal argument exception");
			e3.printStackTrace();
		}catch(InvocationTargetException e4){
			LOG.debug("Invocation target exception");
			e4.printStackTrace();
		}
		return value==null?"":value;
	}
	
	public String combineTemplate(URI fileUri,Status status) throws FileNotFoundException,IOException{
		String debugBeforeTemp = null;
		String debugAfterTemp = null;
		DataUtil du = new DataUtil();
		String temp = DataUtil.readFlatFile(new File(fileUri));
		int begin = temp.indexOf("${");
		int end = temp.indexOf("}",begin);
		while(begin!=-1){
			String name = temp.substring(begin+2,end);
			String value = "";
			if(name.split("\\.").length<3)
				value = getValue(status,name);
			else
				value = getValue(status.getRetweetedStatus(),name);
			name = name.replace(".","\\u002E");
			//LOG.debug("regex:"+"\\$\\{"+name+"\\}");
			value = escapeChar(value);
			debugBeforeTemp = temp;
			try{
				temp = temp.replaceFirst("\\u0024\\u007B"+name+"\\u007D",value);
				debugAfterTemp = temp;
			}catch(IllegalArgumentException e){
				//LOG.debug("temp which before replaced:"+debugBeforeTemp);
				//LOG.debug("replace value:"+value);
				//LOG.debug("temp which after replaced:"+debugAfterTemp);
				throw e;
			}
			begin = temp.indexOf("${");
			end = temp.indexOf("}",begin);
			LOG.debug("begin:"+begin);
		}
		return temp;
	}
		
	public String combineWeibo() throws FileNotFoundException,IOException,URISyntaxException{
		StringBuffer cs = new StringBuffer();
		WeiboDao weiboDao = new WeiboDao(); 
		List<Status> statuses = weiboDao.getAllTop();
		for(Status s:statuses){
			String resource = "/templates/status.html";
			if(s.getRetweetedStatus()!=null){
				resource = "/templates/retweeted.html";
			}
			URL url= this.getClass().getResource(resource);
			cs.append(combineTemplate(url.toURI(),s));
		}
		URL url = this.getClass().getResource("/templates/weibo.html");
		String weibo = DataUtil.readFlatFile(new File(url.toURI()));
		weibo = weibo.replaceAll("\\$\\{content\\}",cs.toString());
		//DataUtil.writeFlatFile(new File("weibo/index.html"),weibo);
		return weibo;
	}

	public String upperCaseFirst(String letters){
		return letters.substring(0,1).toUpperCase()+letters.substring(1);
	}

	public String escapeChar(String content){
		String result = null;
		//$ want to be replaced \$ ,it must use \\\\\\$ to replace it,because one backslash should use \\\ to escape, and  $ shloud be replaced \\$.
		result = content.replaceAll("\\u0024","");
		return result;
	}
}
