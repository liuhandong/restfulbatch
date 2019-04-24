package com.soni.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.soni.mybatis.base.BaseSqlProvider;

public interface BaseMapper<T> {
	//https://docs.spring.io/spring-batch/4.1.x/reference/pdf/spring-batch-reference.pdf
	//http://www.mybatis.org/mybatis-3/zh/java-api.html
	
	
	@InsertProvider(type = BaseSqlProvider.class, method = "batchInsert")    
	boolean batchInsert(List<T> items);
	
//	@SelectProvider(type = BaseSqlProvider.class, method = "selectById")    
//    T selectById(T item);    
//	//    @SelectProvider(type = BaseSqlProvider.class, method = "selectOne")    
//	//    T selectOne(T item);    
//    @SelectProvider(type = BaseSqlProvider.class, method = "select")    
//    List<T> select(T item);    
    @InsertProvider(type = BaseSqlProvider.class, method = "insert")    
    int insert(T item);    
//    @DeleteProvider(type = BaseSqlProvider.class, method = "delete")    
//    int delete(T item);    
//	//    @DeleteProvider(type = BaseSqlProvider.class, method = "deleteById")    
//	//    int deleteById(T item);    
//    @UpdateProvider(type = BaseSqlProvider.class, method = "updateById")   
//    int updateById(T item);    
//	//    @UpdateProvider(type = BaseSqlProvider.class, method =     "updateSelectiveById")    
//	//    int updateSelectiveById(T item);
}
