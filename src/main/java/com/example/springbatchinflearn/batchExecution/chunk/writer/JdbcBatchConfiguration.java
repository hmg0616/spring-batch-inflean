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
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JdbcBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job jdbcBatchConfigurationJob() throws Exception {
        return jobBuilderFactory.get("jdbcBatchConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jdbcBatchConfigurationStep())
                .build();
    }

    @Bean
    public Step jdbcBatchConfigurationStep() throws Exception {
        return stepBuilderFactory.get("jdbcBatchConfigurationStep")
                .<Customer2, Customer2>chunk(10)
                .reader(jdbcBatchConfigurationItemReader())
                .writer(jdbcBatchConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer2> jdbcBatchConfigurationItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }

    // reader는 이전에 JdbcPagingConfiguration.java 에서 구현했던거 이용
    // customer 테이블에서 firstname이 R로 시작하는 데이터 select
    @Bean
    public ItemReader<? extends Customer2> jdbcBatchConfigurationItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jdbcBatchConfigurationItemReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jdbcBatchConfigurationPagingQueryProvider())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jdbcBatchConfigurationPagingQueryProvider() throws Exception {

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
