package com.soni.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.soni.exception.DBException;

public class CommonUtil {
	private static Pattern linePattern = Pattern.compile("_(\\w)");
	/** 下划线转驼峰 */
	public static String lineToHump(String str) {
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
 
	private static Pattern humpPattern = Pattern.compile("[A-Z]");
	/** 驼峰转下划线,效率比上面高 */
	public static String humpToLine(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	private static Map<String,String> MYBATIS_TYPE_MAP = new HashMap<>();
	
	public static String getTypeMap(String type) throws DBException {
		String value = MYBATIS_TYPE_MAP.get(type);
		if(StringUtils.isEmpty(value)) {
			throw new DBException("type ["+type+"] parse not exist!");
		}
		return value;
	}
	
	static {
		MYBATIS_TYPE_MAP.put("long","jdbcType=BIGINT");
		MYBATIS_TYPE_MAP.put("java.lang.Long","jdbcType=BIGINT");
		//MYBATIS_TYPE_MAP.put("byte[]","jdbcType=BINARY");
		//MYBATIS_TYPE_MAP.put("boolean","jdbcType=BIT");
		MYBATIS_TYPE_MAP.put("java.lang.Boolean","jdbcType=BOOLEAN");
		MYBATIS_TYPE_MAP.put("Blob","jdbcType=BLOB");
		MYBATIS_TYPE_MAP.put("boolean","jdbcType=BOOLEAN");
		//MYBATIS_TYPE_MAP.put("java.lang.String","jdbcType=CHAR");
		MYBATIS_TYPE_MAP.put("Clob","jdbcType=CLOB");
		//mybatisTypeMap.put("","jdbcType=CURSOR");
		MYBATIS_TYPE_MAP.put("java.net.URL","jdbcType=DATALINK");
		MYBATIS_TYPE_MAP.put("java.sql.Date","jdbcType=DATE");
		//mybatisTypeMap.put("","jdbcType=DATETIMEOFFSET");
		MYBATIS_TYPE_MAP.put("java.math.BigDecimal","jdbcType=DECIMAL");
		MYBATIS_TYPE_MAP.put("","jdbcType=DISTINCT");
		MYBATIS_TYPE_MAP.put("double","jdbcType=DOUBLE");
		MYBATIS_TYPE_MAP.put("float","jdbcType=FLOAT");
		MYBATIS_TYPE_MAP.put("int","jdbcType=INTEGER");
		MYBATIS_TYPE_MAP.put("java.lang.Integer","jdbcType=INTEGER");
		//MYBATIS_TYPE_MAP.put("","jdbcType=JAVA_OBJECT");
		//MYBATIS_TYPE_MAP.put("","jdbcType=LONGNVARCHAR");
		//MYBATIS_TYPE_MAP.put("byte[]","jdbcType=LONGVARBINARY");
		//MYBATIS_TYPE_MAP.put("java.lang.String","jdbcType=LONGVARCHAR");
		//MYBATIS_TYPE_MAP.put("java.lang.String","jdbcType=NCHAR");
		//mybatisTypeMap.put("","jdbcType=NCLOB");
		//mybatisTypeMap.put("","jdbcType=NULL");
		MYBATIS_TYPE_MAP.put("java.math.BigDecimal","jdbcType=NUMERIC");
		//MYBATIS_TYPE_MAP.put("java.lang.String","jdbcType=NVARCHAR");
		//MYBATIS_TYPE_MAP.put("","jdbcType=OTHER");
		//MYBATIS_TYPE_MAP.put("float","jdbcType=REAL");
		//mybatisTypeMap.put("Ref","jdbcType=REF");
		//mybatisTypeMap.put("","jdbcType=ROWID");
		MYBATIS_TYPE_MAP.put("short","jdbcType=SMALLINT");
		//MYBATIS_TYPE_MAP.put("","jdbcType=SQLXML");
		MYBATIS_TYPE_MAP.put("Struct","jdbcType=STRUCT");
		MYBATIS_TYPE_MAP.put("java.sql.Time","jdbcType=TIME");
		MYBATIS_TYPE_MAP.put("java.sql.Timestamp","jdbcType=TIMESTAMP");
		MYBATIS_TYPE_MAP.put("byte","jdbcType=TINYINT");
		//mybatisTypeMap.put("","jdbcType=UNDEFINED");
		MYBATIS_TYPE_MAP.put("byte[]","jdbcType=VARBINARY");
		MYBATIS_TYPE_MAP.put("java.lang.String","jdbcType=VARCHAR");

	}
}
