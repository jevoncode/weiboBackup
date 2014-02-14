package com.jc.dao;

import com.jc.model.User;
import org.junit.Assert;
import org.junit.Test;
import java.sql.SQLException;
import java.util.Date;
public class UserDaoTest{

	@Test
	public void saveUser(){
		final UserDao userDao = new UserDao();
		final int expectedInt = 1;
		int realInt = 0;
		try{
			User user = new User();
			user.setSession("testSession");
			user.setCode("testCode");
			user.setAccessToken("testAccesstoken");
			user.setExpiresIn(340);
			user.setUid(430);
			user.setCreatedTime(new Date());
			userDao.saveUser(user);
			realInt = 1;
		}catch(SQLException e){
			e.printStackTrace();	
		}finally{
			Assert.assertEquals("is it go right?",expectedInt,realInt);
		}
	}
}
