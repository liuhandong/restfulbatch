package com.soni.batch.apprun;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.soni.config.PersonJobConfig;
import com.soni.config.WebConfig;
import com.soni.repository.CustomizedRepository;

import junit.framework.TestCase;
/**
 * Junit4中的新断言 https://blog.csdn.net/smxjant/article/details/78206435
 * https://blog.csdn.net/Victor_Cindy1/article/details/52126161
 * @author handong.liu
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(/*initializers = CustomContextIntializer.class,*/classes = {WebConfig.class ,PersonJobConfig.class})
@WebAppConfiguration
public class AppTest extends TestCase {

	@Autowired
	private CustomizedRepository customizedRepository;
	
	@Autowired
    private JobLauncher jobLauncher;
	
	@Autowired
	PersonJobConfig appConfig;
	
	// MyBatis insert
	@Test
	public void testInsertSQL() throws Exception {
		/*
insert into person (name,age,nation,address) value('test01',20,'china','dalian');
insert into person (name,age,nation,address) value('test02',45,'china','dalian');
insert into person (name,age,nation,address) value('test03',23,'china','dalian');
insert into person (name,age,nation,address) value('test04',18,'china','dalian');
		 */
		String sql="insert into person (name,age,nation,address) values('test01',20,'china','dalian')," + 
				"('test02',45,'china','dalian')," + 
				"('test03',23,'china','dalian')," + 
				"('test04',18,'china','dalian')";
		customizedRepository.myBatisUpdateSQL(sql);
		List<LinkedHashMap<String, Object>> items = customizedRepository.myBatisSelectSQL("SELECT * FROM person");
		//assertEquals(items.size(), is(equalTo(4)));
		customizedRepository.myBatisUpdateSQL("delete from person");
		List<LinkedHashMap<String, Object>> delitems = customizedRepository.myBatisSelectSQL("SELECT * FROM person");
		assertEquals(delitems.size(),0);
		
	}
	

	// MyBatis Select
	@Test
	public void testSelectSQL() throws Exception {
		String sql="insert into person (name,age,nation,address) values('test01',20,'china','dalian')," + 
				"('test02',45,'china','dalian')," + 
				"('test03',23,'china','dalian')," + 
				"('test04',18,'china','dalian')";
		customizedRepository.myBatisUpdateSQL(sql);
		List<LinkedHashMap<String, Object>> items = customizedRepository.myBatisSelectSQL("SELECT * FROM person");
		for (LinkedHashMap<String, Object> hashMap : items) {
			for (String key : hashMap.keySet()) {
				System.out.println(key + ":" + hashMap.get(key));
			}
		}
		customizedRepository.myBatisUpdateSQL("delete from person");
	}
	
	
	//
	@Test
	public void testJob(){
		Random r=new Random();
		JobParameters jobParameters = new JobParametersBuilder()
                .addString("job1param",String.valueOf(r.nextInt()))
                .toJobParameters();
        try {
        	
			jobLauncher.run(appConfig.personJob(),jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			e.printStackTrace();
		} catch (JobRestartException e) {
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}
}
