package com.jc.persistence.domain;



public class JcConnection implements Map{
	
	 
	
	public int executeUpdate(String name,List<Obejct> table){
		return MemoryRepository.add(name,table);
	}
}