package com.example.springbatchinflearn.batchExecution.chunk.writer;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JsonConfiguration2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jsonConfiguration2Job() throws Exception {
        return jobBuilderFactory.get("jsonConfiguration2Job")
                .incrementer(new RunIdIncrementer())
                .start(jsonConfiguration2Step())
                .build();
    }

    @Bean
    public Step jsonConfiguration2Step() throws Exception {
        return stepBuilderFactory.get("jsonConfiguration2Step")
                .<Customer2, Customer2>chunk(10)
                .reader(jsonConfiguration2ItemReader())
                .writer(jsonConfiguration2ItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer2> jsonConfiguration2ItemWriter() {
        return new JsonFileItemWriterBuilder<Customer2>()
                .name("jsonConfiguration2ItemWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\chunk\\writer\\customer4.json"))
                .build();
    }

    // reader는 이전에 JdbcPagingConfiguration.java 에서 구현했던거 이용
    // customer 테이블에서 firstname이 R로 시작하는 데이터 select
    @Bean
    public ItemReader<? extends Customer2> jsonConfiguration2ItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jsonConfiguration2ItemReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jsonConfiguration2PagingQueryProvider())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jsonConfiguration2PagingQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource);
        queryProviderFactoryBean.setSelectClause("id,firstname,lastname,birthdate");
        queryProviderFactoryBean.setFromClause("from customer");
        queryProviderFactoryBean.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProviderFactoryBean.setSortKeys(sortKeys);
        return queryProviderFactoryBean.getObject();
    }
}
