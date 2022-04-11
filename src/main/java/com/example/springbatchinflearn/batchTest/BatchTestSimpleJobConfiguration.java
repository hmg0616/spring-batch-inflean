package com.example.springbatchinflearn.batchTest;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import com.example.springbatchinflearn.batchExecution.eventListener.chunkListener.CustomChunkListener;
import com.example.springbatchinflearn.batchExecution.eventListener.chunkListener.CustomItemProcessListener;
import com.example.springbatchinflearn.batchExecution.eventListener.chunkListener.CustomItemReadListener;
import com.example.springbatchinflearn.batchExecution.eventListener.chunkListener.CustomItemWriteListener;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BatchTestSimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job batchTestSimpleJob() throws Exception {
        return jobBuilderFactory.get("batchTestSimpleJob")
                .incrementer(new RunIdIncrementer())
                .start(batchTestSimpleStep())
                .build();
    }

    @Bean
    public Step batchTestSimpleStep() throws Exception {
        return stepBuilderFactory.get("batchTestSimpleStep")
                .<Customer2, Customer2>chunk(2)
                .reader(batchTestSimpleItemReader())
                .writer(batchTestSimpleItemWriter())
                .build();
    }


    @Bean
    public JdbcPagingItemReader<Customer2> batchTestSimpleItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jdbcPagingItemReader")
                .pageSize(2)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jdbcPagingQueryProvider5())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jdbcPagingQueryProvider5() throws Exception {

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

    @Bean
    public JdbcBatchItemWriter batchTestSimpleItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }
}
