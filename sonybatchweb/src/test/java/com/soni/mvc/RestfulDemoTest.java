package com.soni.mvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.soni.config.WebConfig;
/**
 * Junit4中的新断言 https://blog.csdn.net/smxjant/article/details/78206435
 * https://blog.csdn.net/Victor_Cindy1/article/details/52126161
 * @author handong.liu
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
@SpringBatchTest
public class RestfulDemoTest {
	@Autowired 
    private WebApplicationContext ctx;

    private MockMvc mockMvc;
	
	@Before 
	public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

	@Test
    public void testShowHome() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/hello/abcd"))
                .andExpect(status().isOk());
    }
}
