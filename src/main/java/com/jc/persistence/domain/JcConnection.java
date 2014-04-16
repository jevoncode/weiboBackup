package com.jc.persistence.domain;

import java.util.List;
import com.jc.persistence.repository.MemoryRepository;

public class JcConnection{
	
	 
	
	public int executeUpdate(String name,List<Object> table){
		return MemoryRepository.add(name,table);
	}
	
	public int executeUpdate(String name,Object record){
		return MemoryRepository.add(name,record);
	}
	
	public List<Object> getAll(String name){
		return MemoryRepository.getAll(name);
	}
}