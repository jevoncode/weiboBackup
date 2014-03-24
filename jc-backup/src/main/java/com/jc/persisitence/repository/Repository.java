package com.jc.persistence.repository;

import com.jc.persistence.domain.JcConnection;

public interface Repository{
	
	public JcConnection getConn();
}