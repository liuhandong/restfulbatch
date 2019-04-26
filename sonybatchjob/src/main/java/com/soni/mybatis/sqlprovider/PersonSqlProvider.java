package com.soni.mybatis.sqlprovider;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import com.soni.entity.Person;
import com.soni.mybatis.base.BaseSqlProvider;

public class PersonSqlProvider extends BaseSqlProvider<Person> {
	private String TABLE_NAME = "person";
	
	public String batchInsert(Map<String, List<Person>> map) {
        List<Person> list = map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(TABLE_NAME);
        sb.append(" (name,age,nation,address)");
        sb.append(" values ");
        MessageFormat mf = new MessageFormat(
                "(#{list[{0}].name},#{list[{0}].age},#{list[{0}].nation},#{list[{0}].address})");
        for (int i = 0; i < list.size(); i++) {
            sb.append(mf.format(new Object[] { i }));
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }

        //insert into person (name,age,nation,address) values
        log.debug(sb.toString());
        return sb.toString();
    }

    /**         
     * 
     * @param param
     * @return
     */
    public String queryRecentTop(Map<String, Object> param) {

        SQL sql = new SQL().SELECT("*").FROM(TABLE_NAME);
        if (StringUtils.hasText((String) param.get("name"))) {
            sql.WHERE("name = #{name}");
        }

        if(param.get("age") != null){
            sql.WHERE("age = #{age}");
        }

        if(param.get("nation") != null){
            sql.WHERE("nation = #{nation}");
        }

        if(param.get("address") != null){
            sql.WHERE("address = #{address}");
        }

        /*
        SELECT *
                FROM person
        WHERE (name = #{name} AND age = #{age} AND nation = #{nation} AND address = #{address})  ORDER BY xxxx DESC limit 3
         */
        log.debug("生成SQL：" + sql.toString() + "  ORDER BY version_timestamp DESC limit " + (Integer)param.get("count"));
        return sql.toString() /*+ "  ORDER BY xxxx DESC limit " + (Integer)param.get("count")*/;
    }
}
