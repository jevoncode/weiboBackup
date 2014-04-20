package com.jc.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedList;

public class ReflectionUtil {

	public static String getValue(Object obj, LinkedList<String> names)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String value = "";
		Class clazz = obj.getClass();
		if(names.size()==1){ 
			Method m = clazz.getMethod("get"
					+ StringUtil.upperCaseFirst(names.poll()));
			Object oValue = m.invoke(obj);
			if (oValue instanceof Date) {
				Date d = (Date) oValue;
				value = StringUtil.formatDate(d, "yyyy-MM-dd HH:mm");
			} else
				value = String.valueOf(m.invoke(obj));
		}else{
			Method m = clazz.getMethod("get"
					+ StringUtil.upperCaseFirst(names.poll()));
			Object oValue = m.invoke(obj);
			value = getValue(oValue,names);
		}
		
		return value;
	}

}