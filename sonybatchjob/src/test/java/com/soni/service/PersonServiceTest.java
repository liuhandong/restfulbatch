package com.soni.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.soni.config.PersonJobConfig;
import com.soni.config.RootConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RootConfig.class,PersonJobConfig.class})
public class PersonServiceTest {
	
	@Autowired
	PersonService personService;
	
	@Test
	public void personServiceTest() {
		//personService.addSomeBatch();
		personService.addPerson();
	}

}
