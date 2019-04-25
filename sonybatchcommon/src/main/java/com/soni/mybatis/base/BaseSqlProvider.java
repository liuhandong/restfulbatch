package com.soni.mybatis.base;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soni.common.util.CommonUtil;
import com.soni.exception.DBException;
import com.soni.mybatis.annotations.KeyIgnore;
import com.soni.mybatis.annotations.PrimaryKey;

public class BaseSqlProvider<T> {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSqlProvider.class);
	
	protected final String VAR_LEFT = "#{";
	
	protected final String QUOTE_LEFT = "#'{'";
	
	protected final String VAR_RIGHT = "}";
	
	private String getRealTableName(String tableName){
		return CommonUtil.humpToLine(tableName).replaceAll(/*table fixed name*/"", "").substring(1);
	}
	
	public String insert(T t) throws DBException {		
		SQL sql = new SQL();		 
		Class<?> clazz = t.getClass(); 
		String tableName = clazz.getSimpleName();		
		String realTableName = getRealTableName(tableName);
		sql.INSERT_INTO(realTableName); 
		List<Field> fields = getFields(clazz,false);
		for (Field field : fields) { 
			field.setAccessible(true); 
			String column = field.getName(); 
			String type = field.getType().getName();
			log.debug("column:" + CommonUtil.humpToLine(column)); 
			sql.VALUES(CommonUtil.humpToLine(column), String.format(VAR_LEFT + column + "," + CommonUtil.getTypeMap(type) + VAR_RIGHT)); 
		} 
		return sql.toString();
    }

	
	private List<Field> getPrimarkKeyFields(Class<?> clazz) {
		 
		List<Field> primaryKeyField = new ArrayList<>();
		List<Field> fields = getFields(clazz,true);
		for (Field field : fields) {
			field.setAccessible(true);
			PrimaryKey key = field.getAnnotation(PrimaryKey.class);
			if (key != null) {
				primaryKeyField.add(field);
			}
 
		}
		return primaryKeyField;
	}
	
	private List<Field> getFields(Class<?> clazz,boolean includeKey) {
		 
		List<Field> fieldList = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			KeyIgnore key = field.getAnnotation(KeyIgnore.class);
			if (key == null||(includeKey&&key != null)) {
				fieldList.add(field);
			}
 
		}
		return fieldList;
	}
	
	public String batchInsert(Map<String, List<T>> map) {
        List<T> list = map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append("person");
        sb.append(" (name,age,nation,address)");
        sb.append(" values ");
        //getFields()
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
	
	
	public String delete(T bean) throws DBException {		 
		SQL sql = new SQL(); 
		Class<?> clazz = bean.getClass(); 
		String tableName = clazz.getSimpleName(); 
		String realTableName = getRealTableName(tableName);
		sql.DELETE_FROM(realTableName);
 
		List<Field> primaryKeyField = getPrimarkKeyFields(clazz);
 
		if (!primaryKeyField.isEmpty()) { 
			for (Field pkField : primaryKeyField) {
				pkField.setAccessible(true);
				sql.WHERE(pkField.getName() + "=" + String.format(VAR_LEFT + pkField.getName() + VAR_RIGHT));
			} 
		} else { 
			sql.WHERE(" 1= 2"); 
			throw new DBException("object not contain primaryKey annotation! Can't delete the object.");
		}
 
		return sql.toString();
	}
	
	public String select(T bean) throws DBException {
		 
		SQL sql = new SQL(); 
		Class<?> clazz = bean.getClass(); 
		String tableName = clazz.getSimpleName();
 
		String realTableName = getRealTableName(tableName);
		sql.SELECT("*").FROM(realTableName); 
		List<Field> primaryKeyField = getPrimarkKeyFields(clazz);
 
		if (!primaryKeyField.isEmpty()) { 
			for (Field pkField : primaryKeyField) {
				pkField.setAccessible(true);
				sql.WHERE(pkField.getName() + "=" + String.format(VAR_LEFT + pkField.getName() + VAR_RIGHT));				
			}
		} else { 
			sql.WHERE(" 1= 2"); 
			throw new DBException("object not contain primaryKey annotation! Can't select the object.");
		}
		log.debug("getSql:"+sql.toString());
		return sql.toString();
	}
 
	public String update(T bean) throws DBException {
 
		SQL sql = new SQL(); 
		Class<?> clazz = bean.getClass(); 
		String tableName = clazz.getSimpleName();
 		String realTableName = getRealTableName(tableName);
		sql.UPDATE(realTableName); 
		List<Field> fields = getFields(clazz,true);
		for (Field field : fields) {
 
			field.setAccessible(true); 
			String column = field.getName(); 
			KeyIgnore key = field.getAnnotation(KeyIgnore.class);
			if (key!=null) {
				continue;
			}
			String type = field.getType().getName();
			log.debug(CommonUtil.humpToLine(column)); 
			sql.SET(CommonUtil.humpToLine(column) + "=" + String.format(VAR_LEFT + column + "," + CommonUtil.getTypeMap(type) + VAR_RIGHT));
		}
 
		List<Field> primaryKeyField = getPrimarkKeyFields(clazz); 
		if (!primaryKeyField.isEmpty()) { 
			for (Field pkField : primaryKeyField) {
				pkField.setAccessible(true);
				sql.WHERE(pkField.getName() + "=" + String.format(VAR_LEFT + pkField.getName() + VAR_RIGHT));
			} 
		} else { 
			sql.WHERE(" 1= 2"); 
			throw new DBException("object not contain primaryKey annotation! Can't update the object.");
		}
		log.debug("updateSql:"+sql.toString());
		return sql.toString();
 
	}

}
