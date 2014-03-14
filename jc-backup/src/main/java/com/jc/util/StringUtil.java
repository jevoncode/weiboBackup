package com.jc.util;

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

}
