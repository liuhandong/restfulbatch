package com.soni.batch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.soni.config.PersonJobConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PersonJobConfig.class})
@SpringBatchTest
public class JobExecuteTest {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;
	
	@Before
	public void clearMetadata() {
		jobRepositoryTestUtils.removeJobExecutions();
	}
	
	@Test
	public void testJob() throws Exception {
		JobParameters jobParameters =jobLauncherTestUtils.getUniqueJobParameters();
		JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
		Assert.assertEquals(ExitStatus.COMPLETED,jobExecution.getExitStatus());
	}

}
