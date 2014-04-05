package com.jc.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {PersistenceConfig.class,CoreConfig.class,WebConfig.class})
public class WebDomainIntegrationTest{

	private static final String RESPONSE_BODY = "Welcom to JC weibo backup app.";
	private MockMvc mockMvc;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Before
	public void setup(){
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void thatTextReturned() throws Exception{
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(content().string(RESPONSE_BODY));
	}
	
}