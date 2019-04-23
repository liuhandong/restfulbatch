package com.soni.config;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.soni.entity.Person;
import com.soni.repository.CustomizedRepository;
import com.soni.validate.CsvBeanValidator;
import com.soni.validate.CsvItemProcessor;

@Configuration
@MapperScan(value = "com.soni.mybatis"/*,sqlSessionFactoryRef = "sqlSessionFactory"*/)
@EnableBatchProcessing
//@EnableTransactionManagement
@ComponentScan(basePackages = {"com.soni"/*,"com.soni.config","com.soni.repository"*/})
public class PersonJobConfig {
//https://innersource.accenture.com/projects/DMMIF
	//private static final Logger log = LoggerFactory.getLogger(PersonJobConfig.class);
	
	@Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

	
	@Autowired
	private CustomizedRepository customizedRepository;
    


    @Bean
    public Job personJob(/*, @Qualifier("step2") Step step2*/) {
        return jobs.get("myJob").start(step1())/*.next(step2)*/.build();
    }
    
	/**
     * ItemReader定义,用来读取数据
     * 1，使用FlatFileItemReader读取文件
     * 2，使用FlatFileItemReader的setResource方法设置csv文件的路径
     * 3，对此对cvs文件的数据和领域模型类做对应映射
     * @return
     * @throws Exception
     */
    @Bean
    public ItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("person.csv"));
        reader.setLineMapper(new DefaultLineMapper<Person>(){
            {
                setLineTokenizer(new DelimitedLineTokenizer(){
                    {
                        setNames(new String[]{"name","age","nation","address"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>(){{
                    setTargetType(Person.class);
                }});
            }
        });
        return reader;
    }
    
    /**
     * ItemProcessor定义，用来处理数据
     * @return
     */
    @Bean
    public ItemProcessor<Person,Person> processor(){
        //使用我们自定义的ItemProcessor的实现CsvItemProcessor
        CsvItemProcessor processor = new CsvItemProcessor();
        //为processor指定校验器为CsvBeanValidator()
        processor.setValidator(csvBeanValidator());
        return processor;
    }
    
    /**
     * ItemWriter定义，用来输出数据
     * spring能让容器中已有的Bean以参数的形式注入，Spring Boot已经为我们定义了dataSource
     * @param dataSource
     * @return
     */
    @Bean
    public ItemWriter<Person> writer( ){

    	ItemWriter<Person> writer = new ItemWriter<Person>() {
    		@Override
    	    public void write(List<? extends Person> list) throws Exception {
    			StringBuilder sqlb = new StringBuilder();
    			sqlb.append("insert into person (name,age,nation,address) values ");
    			int c=0;
    	        for(Person person: list) {
    	        	sqlb.append("('"+person.getName()+"','"+person.getAge()+"','"+person.getNation()+"','"+person.getAddress()+"')"+(c!=list.size()-1?",":""));
    	        	c++;
    	        	System.out.println("===="+c);
    	        }
    	        customizedRepository.myBatisUpdateSQL(sqlb.toString());
    	        customizedRepository.myBatisUpdateSQL("delete from person");
    	    }
    	};
        return writer;
    }

//    /**
//     * Job定义，我们要实际执行的任务，包含一个或多个Step
//     * @param jobBuilderFactory
//     * @param s1
//     * @return
//     */
//    @Bean
//    public Job importJob(Step s1){
//        return jobs.get("importJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(s1)//为Job指定Step
//                .end()
//                //.listener(csvJobListener())//绑定监听器csvJobListener
//                .build();
//    }

    /**
     *step步骤，包含ItemReader，ItemProcessor和ItemWriter
     * @param stepBuilderFactory
     * @param reader
     * @param writer
     * @param processor
     * @return
     */
    @Bean
    public Step step1(){
        return steps
                .get("step1")
                .<Person,Person>chunk(10)//批处理每次提交10条数据
                .reader(reader())//给step绑定reader
                .processor(processor())//给step绑定processor
                .writer(writer())//给step绑定writer
                .build();
    }


//    @Bean
//    public CsvJobListener csvJobListener(){
//        return new CsvJobListener();
//    }

    @Bean
    public Validator<Person> csvBeanValidator(){
        return new CsvBeanValidator<Person>();
    }


}
