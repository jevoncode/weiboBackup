package com.jc.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryRepository implements Repository {

	private static Map<String, List<Object>> data = new HashMap<String, List<Object>>();
	
	private JcConnection jcConnection ;
	
	public MemoryRepository(Map<String, List<Object>> data){
		this.data = data;
	}

	@Override
	public JcConnection getConn() {
		if(jcConnection==null){
			jcConnection = new JcConnection()
		}
		return jcConnection;
	}
	
	public static int add(String name,List<Obejct> table){
		List<Obejct> success = data.put(name,table);
		return success.size();
	}
}