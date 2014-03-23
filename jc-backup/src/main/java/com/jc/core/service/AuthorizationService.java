public com.jc.core.service;


import com.jc.model.core.JcUser;

public interface AuthorizationService{
	
	public String assembleOAuthURL();
	
	public JcUser obtainAuthorize();
}