package com.soni.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.soni.entity.Person;
import com.soni.repository.CustomizedRepository;

@Component
//@Transactional /*class transactional default Propagation.REQUIRED*/
public class PersonService {
	@Autowired
	CustomizedRepository customizedRepository;
	
	
	@Transactional(rollbackForClassName= {"com.soni.exception.DBException"})
	public boolean addSomeBatch() {
		List<Person> persons = new ArrayList<>();
		for(int i=0;i<20;i++) {
			Person person = new Person();
			person.setName("0name["+i+"]");
			person.setAge(""+i);
			person.setNation("china");
			person.setAddress("dl"+i);
			persons.add(person);
		}
		boolean result = customizedRepository.addBatch(persons);
		return result;
	}
	
	@Transactional(rollbackForClassName= {"com.soni.exception.DBException"})
	public int addPerson() {
		Person person = new Person();
		person.setName("0name["+0+"]");
		person.setAge("26");
		person.setNation("china");
		person.setAddress("dl45");
		
		return customizedRepository.insert(person);
	}

}
