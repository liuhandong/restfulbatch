package com.soni.batch.apprun;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeansException;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

//import com.soni.config.AppConfig;



//@SpringBootApplication
@ComponentScan("com.soni.config,com.soni.batch.writer,com.soni.batch.reader,com.soni.repository,com.soni.controller")
public class Application /*implements CommandLineRunner*/
{
	
    public static void main( String[] args )
    {
//    	ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
//    	
//    	JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
//    	JobParameters jobParameters = new JobParametersBuilder()
//                .addDate("date", new Date())
//                .toJobParameters();
//        try {
//			jobLauncher.run(ctx.getBean("personJob", Job.class), jobParameters);
//		} catch (BeansException e) {
//			e.printStackTrace();
//		} catch (JobExecutionAlreadyRunningException e) {
//			e.printStackTrace();
//		} catch (JobRestartException e) {
//			e.printStackTrace();
//		} catch (JobInstanceAlreadyCompleteException e) {
//			e.printStackTrace();
//		} catch (JobParametersInvalidException e) {
//			e.printStackTrace();
//		}
    }
/*
	@Override
	public void run(String... args) throws Exception {
		System.out.println("####################################################");
//		System.out.println(personMapper.myBatisSelectSQL("select * from person"));        

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(AppConfig.class);
        ctx.refresh();
        JobLauncher  jobLauncher = ctx.getBean(JobLauncher.class);
        Job job = (Job)ctx.getBean("myJob");
        try {
            jobLauncher.run(job, new JobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.close();
		System.out.println("####################################################");
	}
*/
}
