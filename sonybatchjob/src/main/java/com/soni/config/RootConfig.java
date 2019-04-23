package com.soni.config;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan(basePackages = {"com.soni.config","com.soni.repository"})
@PropertySource(value={"classpath:application.properties"})
//,excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,value = EnableWebMvc.class)}
public class RootConfig {	
    
	@Value("${spring.datasource.url}")
    public String url;
	@Value("${spring.datasource.driver-class-name}")
    public String jdbcname;
	@Value("${spring.datasource.username}")
    public String user;
	@Value("${spring.datasource.password}")
    public String password;
    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;
	
    //数据源 spring 下的自带jdbc的链接数据源
    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcname);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean createSqlSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactoryBean = null;
        try {
            // 加载JNDI配置
            //Context context = new InitialContext();
            DataSource dataSource = (DataSource)dataSource();

            // 实例SessionFactory
            sqlSessionFactoryBean = new SqlSessionFactoryBean();
            // 配置数据源
            sqlSessionFactoryBean.setDataSource(dataSource);

            // 加载MyBatis配置文件
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            // 能加载多个，所以可以配置通配符(如：classpath*:mapper/**/*.xml)
            sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(mapperLocations));
            // 配置mybatis的config文件(我目前用不上)
            // sqlSessionFactoryBean.setConfigLocation("mybatis-config.xml");
        } catch (Exception e) {
        	e.printStackTrace();
            //logger.error("创建SqlSession连接工厂错误：{}", e);
        }
        return sqlSessionFactoryBean;
    }
 
}