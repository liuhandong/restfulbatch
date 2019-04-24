package com.soni.mybatis.base;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.soni.mybatis.annotations.KeyIgnore;

public class BaseSqlProvider<T> {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSqlProvider.class);
	
	private boolean sameTableMap = true;
	
	protected final String VAR_LEFT = "#{";
	
	protected final String VAR_RIGHT = "}";

	
	/**
	 * Efficiency low 
	 * such as #{name},#{age},#{nation},#{address}
	 * @return
	 */
	private String formatFieldList(T t) {
		StringBuilder sdr = new StringBuilder();
		if(sameTableMap) {
					
			Field[] fields = t.getClass().getFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(field.getAnnotationsByType(KeyIgnore.class).length==0) {
					sdr.append(VAR_LEFT+field.getName()+VAR_RIGHT);
		            if (i < fields.length - 1) {
		            	sdr.append(",");
		            }
				}else {
					//auto increase key remove	
				}
	        }
		}else {			
			sdr.append(getFieldList());
		}
		
		return sdr.toString();
	}
	
	/**
	 * Get table name
	 * sameTableMap be false to overwrite here method
	 * @return
	 */
	String getTableName() {
		Assert.isNull(getFieldList(),"Must overwrite getTableName() method.");
		return null;
	}
	
	/**
	 * table field name list
	 * sameTableMap be false to overwrite here method
	 * @return
	 */
	String getFieldList() {
		Assert.isNull(getFieldList(),"Must overwrite getFieldList() method.");
		return null;
	}
	/**
	 * 
	 * @param isStm
	 */
	void isSameTableMap(boolean isStm) {
		this.sameTableMap = isStm;
	}
	
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
