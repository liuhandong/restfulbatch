package com.soni.repository;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.soni.entity.Person;

public interface CustomizedRepository {
	List<LinkedHashMap<String,Object>> myBatisSelectSQL(@Param("sql")String sql);
	 
    int myBatisUpdateSQL(@Param("sql")String sql);
    
    boolean addBatch(List<Person> persons);
    
    int insert(Person person);
}
