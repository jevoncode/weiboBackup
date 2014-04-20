package com.jc.util; 
import java.util.Random;  
import java.util.Date;
import java.text.SimpleDateFormat;

public class StringUtil{

	public static boolean containsEmoji(String s){
		if("".equals(s.trim()))
			return false;
		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			if(isEmojiCharacter(c)){
				return true;
			}
		}
		return false;
	}


	public static boolean isEmojiCharacter(char c){
		return !((c==0x0)||
				(c==0x9)||
				(c==0xA)||
				(c==0xD)||
				((c>=0x20)&&(c<=0xD7FF))||
				((c>=0xE000)&&(c<=0xFFFD))||
				((c>=0x10000)&&(c<=0x10FFFF)));
	}	

	public static String filterEmoji(String s){
		if(!containsEmoji(s))
			return s;
		StringBuilder buf = new StringBuilder();

		for(int i=0;i<s.length();i++){
			char c = s.charAt(i);
			if(!isEmojiCharacter(c)){
				buf.append(c);
			}
		}
		if(buf.length() == s.length())
			return s;
		else
			return buf.toString();
	}
	public static String toLineByComma(String[] array){
		if(array==null||array.length==0)
			return "";
		StringBuffer sb = new StringBuffer();
		String delim = "";
		for(String s:array){
			sb.append(delim).append(s);
			delim = ",";
		}
		return sb.toString();
	}

	public static String upperCaseFirst(String letters){
		return letters.substring(0,1).toUpperCase()+letters.substring(1);
	}
	
	public static String escapeChar(String content){
		String result = null;
		//$ want to be replaced \$ ,it must use \\\\\\$ to replace it,because one backslash should use \\\ to escape, and  $ shloud be replaced \\$.
		result = content.replaceAll("\\u0024","");
		return result;
	}
	
	public static String generateVarificationCode(){
		Random r = new Random();  
		char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		int len = ch.length;
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<6;i++)
			sb.append(ch[r.nextInt(len)]);
		return sb.toString();
	}
	
	public static String formatDate(Date date,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
}
