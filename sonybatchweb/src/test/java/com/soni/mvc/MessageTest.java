package com.soni.mvc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.soni.config.RootConfig;
import com.soni.config.WebConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebConfig.class,RootConfig.class})
@WebAppConfiguration
public class MessageTest {
	
	@Autowired
	public MessageSource messageSource;
	
	@Test
	public void messageGetTest() {
		Assert.assertEquals(messageSource.getMessage("msgs.GWNCDT0092L-3011", new String[] {}, null), "必須項目チェック");
	}

}
