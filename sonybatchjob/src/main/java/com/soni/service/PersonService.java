package com.soni.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.soni.entity.Person;
import com.soni.exception.DBException;
import com.soni.mybatis.PersonMapper;

@Component
//@Transactional /*class transactional default Propagation.REQUIRED*/
public class PersonService {
	@Autowired
    PersonMapper personMapper;
	
	
	@Transactional(rollbackFor = DBException.class)
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
		boolean result = personMapper.addBatch(persons);
		return result;
	}
	//
	@Transactional(rollbackFor = Exception.class)
	public long addPerson() {
		Person person = new Person();
		person.setName("0name["+0+"]");
		person.setAge("26");
		person.setNation("china");
		person.setAddress("dl45");
		personMapper.insert(person);
		Person selectperson = new Person();
		selectperson.setId(person.getId());
		personMapper.select(selectperson);
		
		person.setName("update 888");
		personMapper.update(person);
		return person.getId();
	}

}
