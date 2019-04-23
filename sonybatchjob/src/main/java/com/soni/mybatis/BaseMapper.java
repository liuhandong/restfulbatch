package com.soni.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

public interface BaseMapper {
	//https://docs.spring.io/spring-batch/4.1.x/reference/pdf/spring-batch-reference.pdf
	//http://www.mybatis.org/mybatis-3/zh/java-api.html
//	@SelectProvider(type = SQLProvider.class, method = "selectById")    
//    T selectById(T item);    
//    @SelectProvider(type = SQLProvider.class, method = "selectOne")    
//    T selectOne(T item);    
//    @SelectProvider(type = SQLProvider.class, method = "select")    
//    List<T> select(T item);    
//    @InsertProvider(type = SQLProvider.class, method = "insert")    
//    int insert(T item);    
//    @DeleteProvider(type = SQLProvider.class, method = "delete")    
//    int delete(T item);    
//    @DeleteProvider(type = SQLProvider.class, method = "deleteById")    
//    int deleteById(T item);    
//    @UpdateProvider(type = SQLProvider.class, method = "updateById")   
//    int updateById(T item);    
//    @UpdateProvider(type = SQLProvider.class, method =     "updateSelectiveById")    
//    int updateSelectiveById(T item);
}
