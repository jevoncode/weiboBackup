package com.jc.web.controller;

import com.jc.service.IndexService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;

import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class IndexControllerTest{
	
	private static final String RESPONSE_BODY = "Hello,my name is WeiboBackup Jc,I am a backup tool to backup your Weibo data";
	
	MockMvc mockMvc;
	
	@InjectMocks
	IndexController controller;
	
	@Mock
	IndexService indexService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		mockMvc = standaloneSetup(controller).build();
		when(indexService.showAboutMe()).thenReturn(RESPONSE_BODY);
	}
	
	@Test
	public void thatTextReturned() throws Exception{
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(content().string(RESPONSE_BODY));
	}
}