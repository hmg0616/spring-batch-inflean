package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MultiThreadConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job multiThreadConfigurationJob() throws Exception {
        return jobBuilderFactory.get("multiThreadConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadConfigurationStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step multiThreadConfigurationStep() throws Exception {
        return stepBuilderFactory.get("multiThreadConfigurationStep")
                .<Customer2,Customer2>chunk(2)
                .reader(multiThreadConfigurationItemReader())
                .listener(new multiThreadConfigurationItemReadListener())
                .processor((ItemProcessor<? super Customer2, ? extends Customer2>) item -> item)
                .listener(new multiThreadConfigurationItemProcessListener())
                .writer(multiThreadConfigurationItemWriter())
                .listener(new multiThreadConfigurationItemWriteListener())
                .taskExecutor(multiThreadConfigurationTaskExecutor()) // 멀티스레드로 실행시키기
                .build();
    }

    @Bean
    public TaskExecutor multiThreadConfigurationTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // 기본 스레드 생성
        taskExecutor.setMaxPoolSize(8); // 최대 스레드 생성 가능 개수
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }

    @Bean
    public ItemReader<? extends Customer2> multiThreadConfigurationItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        // MultiThread 작업 시 Reader는 스레드 안전해야함. 
        // 스레드마다 중복된 데이터를 읽지 않음을 보장해야 하므로.
        // JdbcPagingItemReader, JpaPagingItemReader 는 thread-safe 함
        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jdbcPagingItemReader")
                .pageSize(2)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jdbcPagingQueryProvider3())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jdbcPagingQueryProvider3() throws Exception {

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
    public JdbcBatchItemWriter multiThreadConfigurationItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }

}
