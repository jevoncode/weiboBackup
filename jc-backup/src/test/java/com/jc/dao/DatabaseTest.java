package com.jc.dao;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest{
	@Test
	public void getConnection(){
		final Database database = new Database();
		final Boolean realFlag = database.getConnection()==null?false:true;
		final Boolean expectedFlag = true;
		Assert.assertEquals("get database connection",expectedFlag, realFlag);
	}
}
