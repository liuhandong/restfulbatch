package com.soni.mvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.soni.common.util.JsonUtil;
import com.soni.config.RootConfig;
import com.soni.config.WebConfig;
import com.soni.entity.Person;
/**
 * Junit4中的新断言 https://blog.csdn.net/smxjant/article/details/78206435
 * https://blog.csdn.net/Victor_Cindy1/article/details/52126161
 * @author handong.liu
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebConfig.class,RootConfig.class})
@WebAppConfiguration
public class RestfulDemoTest {
	@Autowired 
    private WebApplicationContext ctx;

    private MockMvc mockMvc;
	
	@Before 
	public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

	//@Test
    public void testShowHello() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/hello/abcd"))
                .andExpect(status().isOk());
    }
	
	@Test
    public void testShowHelloValid() throws Exception {
		Person person = new Person();
		person.setName("12345");
		person.setAge("tttt");
		String param = JsonUtil.convertObjectToJsonBytes(person);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/hello_valid")
                .contentType(JsonUtil.APPLICATION_JSON_UTF8)
                .content(param))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("$.errors", is("msgs.GWNCDT0100L-3006")))
        		.andExpect(content().contentType(JsonUtil.APPLICATION_JSON_UTF8));
    }
}
