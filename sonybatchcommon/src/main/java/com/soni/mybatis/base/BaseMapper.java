package com.soni.mybatis.base;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

public interface BaseMapper<T> {
	//https://docs.spring.io/spring-batch/4.1.x/reference/pdf/spring-batch-reference.pdf
	//http://www.mybatis.org/mybatis-3/zh/java-api.html	
	
	@InsertProvider(type = BaseSqlProvider.class, method = "batchInsert")    
	boolean batchInsert(List<T> items);
  
    @SelectProvider(type = BaseSqlProvider.class, method = "select")  
    T select(T item);    
    @InsertProvider(type = BaseSqlProvider.class, method = "insert")    
    @Options(useGeneratedKeys=true)
    int insert(T item);    
    @DeleteProvider(type = BaseSqlProvider.class, method = "delete")    
    int delete(T item);
    @UpdateProvider(type = BaseSqlProvider.class, method = "update")   
    int update(T item);
    
	//执行任意SELECT语句（利用LinkedHashMap接收返回的结果）
    @Select("${sql}")
    List<LinkedHashMap<String,Object>> plainSelectSQL(@Param("sql")String sql);
    // 执行任意INSERT、UPDATE、DELETE语句
    @Update("${sql}")
    int plainUpdateSQL(@Param("sql")String sql);
}
