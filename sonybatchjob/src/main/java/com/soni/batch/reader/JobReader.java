package com.soni.batch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.soni.entity.Person;


@Component
public class JobReader {
	
	@Value("${source.file.path}")
    private String filePath;
	
	@Bean
    public FlatFileItemReader<Person> csvFileReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("csvFileReader")
                .resource(new ClassPathResource(filePath))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

}
