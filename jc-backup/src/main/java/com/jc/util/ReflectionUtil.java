public com.jc.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class RelectionUtil{
	
	
	public static String getValue(Object obj,String name) throws NoSuchMethodException,IllegalAccessException,IllegalArgumentException,InvocationTargetException{
		String value = "";
		String[] args = name.split("\\.");
		int argLen = args.length;
		String objName = upperCaseFirst(args[0]);
		Class clazz = obj.getClass();
		int index = clazz.getName().indexOf(objName); 
		Method m = clazz.getMethod("get"+upperCaseFirst(args[argLen-1]));
		value = String.valueOf(m.invoke(obj)); 
		return value;
	}
	
}