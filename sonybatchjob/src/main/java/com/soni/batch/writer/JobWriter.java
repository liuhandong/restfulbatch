package com.soni.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soni.entity.Person;
import com.soni.repository.CustomizedRepository;

@Component
public class JobWriter implements ItemWriter<Person> {
	
	@Autowired
	private CustomizedRepository customizedRepository;

	@Override
    public void write(List<? extends Person> list) throws Exception {
		StringBuilder sqlb = new StringBuilder();
		sqlb.append("insert into person (name,age,nation,address) values ");
		int c=0;
        for(Person person: list) {
        	sqlb.append("('"+person.getName()+"','"+person.getAge()+"','"+person.getNation()+"','"+person.getAddress()+"')"+(c!=list.size()-1?",":""));
        	c++;
        }
        customizedRepository.myBatisUpdateSQL(sqlb.toString());
        customizedRepository.myBatisUpdateSQL("delete from person");
    }
}
