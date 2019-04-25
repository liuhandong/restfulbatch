package com.soni.repository;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.soni.entity.Person;
import com.soni.mybatis.PersonMapper;
@Repository
public class CustomizedRepositoryImpl extends SqlSessionDaoSupport implements CustomizedRepository{
	@Autowired
    PersonMapper employeeMapper;
	
	@Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory);
    }
 
    @Override
    public List<LinkedHashMap<String, Object>> myBatisSelectSQL(String sql)
    {
        return employeeMapper.plainSelectSQL(sql);
    }
 
    @Override
    public int myBatisUpdateSQL(String sql)
    {
        return employeeMapper.plainUpdateSQL(sql);
    }

	@Override
	public boolean addBatch(List<Person> persons) {		
		return employeeMapper.addBatch(persons);
	}

	@Override
	public int insert(Person person) {		
		return employeeMapper.insert(person);
	}
}
