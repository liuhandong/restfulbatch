package com.soni.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.soni.entity.Person;

@RestController
public class DemoController {
	
	@Autowired
	JobLauncher jobLauncher;

    @Autowired
    Job personJob;
    
    @Scheduled(cron = "0 0/5 * * * ?")
    //@RequestMapping("/home")
    @RequestMapping(value="/hello/{player}",method= RequestMethod.GET)
    public void imp(@PathVariable String player) throws  Exception{

    	//JobLauncher jobLauncher = new SimpleJobLauncher();
    	JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(personJob,jobParameters);
    }
    
    @RequestMapping(value="/hello",method= RequestMethod.GET)
    public Map<String,String> sayHello(){
    	Map<String,String> map =new HashMap<>();
    	map.put("name","test");
    	map.put("age","21");
    	map.put("country","CH");
        return map;
    }
    
    @RequestMapping(value="/hello_valid",method= RequestMethod.POST)
    public Map<String,Object> sayHelloValid(@Valid @RequestBody Person person,BindingResult result){
    	//TODO result convert message for base class
    	
    	Map<String,Object> map =new HashMap<>();
    	map.put("name","test");
    	map.put("age","21");
    	map.put("country","CH");
    	
    	map.put("errors", result.getAllErrors());
        return map;
    }

}
