package com.jc.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.jc.persistence.domain.JcConnection;

public class MemoryRepository implements Repository {

	private static Map<String, List<Object>> data = new HashMap<String, List<Object>>();
	
	private JcConnection jcConnection ;
	

	@Override
	public JcConnection getConn() {
		if(jcConnection==null){
			jcConnection = new JcConnection();
		}
		return jcConnection;
	}
	
	public static int add(String name,List<Object> table){
		List<Object> success = data.put(name,table);
		return success.size();
	}
	
	/**
	 * return id,which in database called ID,but in memory is called Index.
	 */
	public static int add(String name,Object record){
		List<Object> table = data.get(name);
		if(table==null){
			table = new ArrayList<Object>();
		}
		table.add(record);
		List<Object> success = data.put(name,table);
		return table.size()-1;
	}
	
	public static List<Object> getAll(String name){
		return data.get(name);
	}
}