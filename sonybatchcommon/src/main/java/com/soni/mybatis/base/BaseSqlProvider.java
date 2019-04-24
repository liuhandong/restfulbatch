package com.soni.mybatis.base;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseSqlProvider<T> {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSqlProvider.class);
	
	//private String tableName;
	/**
	 * (name,age,nation,address)
	 */
	//private String fieldList;
	
	/**
	 * #{name},#{age},#{nation},#{address}
	 * @return
	 */
	private String formatFieldList(T t) {
		return getFieldList();
	}
	
	/**
	 * 
	 * @return
	 */
	abstract String getTableName();
	/**
	 * 
	 * @return
	 */
	abstract String getFieldList();
	
	public String insert(T t) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(getTableName());
        sb.append(" ("+getFieldList()+")");
        sb.append(" values ");
        MessageFormat mf = new MessageFormat(
                "("+formatFieldList(t)+")");
        
        sb.append(mf.format(new Object[] { 0 }));
        
        log.debug(sb.toString());
        return sb.toString();
    }


	public String batchInsert(Map<String, List<T>> map) {
        List<T> list = map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(getTableName());
        sb.append(" (name,age,nation,address)");
        sb.append(" values ");
        MessageFormat mf = new MessageFormat(
                "(#'{'list[{0}].name},#'{'list[{0}].age},#'{'list[{0}].nation},#'{'list[{0}].address})");
        for (int i = 0; i < list.size(); i++) {
            sb.append(mf.format(new Object[] { i }));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        
        log.debug(sb.toString());
        return sb.toString();
    }
	

}
