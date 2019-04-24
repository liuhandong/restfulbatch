package com.soni.mybatis;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

import com.soni.entity.Person;
import com.soni.mybatis.sqlprovider.PersonSqlProvider;
/**
 * http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/
 * @author handong.liu
 *
 */
@Mapper
public interface PersonMapper extends BaseMapper<Person> {
	
	@InsertProvider(type = PersonSqlProvider.class, method = "batchInsert")
    boolean addBatch(List<Person> persons);

    //可以解决SQL注入
    @SelectProvider(type = PersonSqlProvider.class,method = "queryRecentTop")
    @Results(value = {
    		 @Result(id = true, property = "id", column = "id", javaType = Long.class, jdbcType = JdbcType.BIGINT),
             @Result(id = false, property = "name", column = "name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
             @Result(id = false, property = "age", column = "age", javaType = String.class, jdbcType = JdbcType.VARCHAR),
             @Result(id = false, property = "nation", column = "nation", javaType = String.class, jdbcType = JdbcType.VARCHAR),
             @Result(id = false, property = "address", column = "address", javaType = String.class, jdbcType = JdbcType.LONGVARCHAR),})
    List<Person> queryTop(@Param("name") String name, @Param("age") String age, @Param("nation") String nation, @Param("address") String address,@Param("count") int count);


    @Select("select COUNT(*) from person where nation=#{nation}")
    int findSignature(@Param("nation") String nation);
	
	//执行任意SELECT语句（利用LinkedHashMap接收返回的结果）
    @Select("${sql}")
    List<LinkedHashMap<String,Object>> myBatisSelectSQL(@Param("sql")String sql);
    // 执行任意INSERT、UPDATE、DELETE语句
    @Update("${sql}")
    int myBatisUpdateSQL(@Param("sql")String sql);

}
