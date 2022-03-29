package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AsyncConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job asyncConfigurationJob() throws Exception {
        return jobBuilderFactory.get("asyncConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(asyncConfiguration_AsyncStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    // 동기식 스텝
    @Bean
    public Step asyncConfiguration_Step() throws Exception {
        return stepBuilderFactory.get("asyncConfiguration_Step")
                .<Customer2,Customer2>chunk(100)
                .reader(asyncConfiguration_ItemReader())
                .processor(asyncConfiguration_ItemProcessor())
                .writer(asyncConfiguration_ItemWriter())
                .build();
    }

    // 비동기식 스텝
    @Bean
    public Step asyncConfiguration_AsyncStep() throws Exception {
        return stepBuilderFactory.get("asyncConfiguration_AsyncStep")
                .<Customer2,Customer2>chunk(100)
                .reader(asyncConfiguration_ItemReader())
                .processor(asyncConfiguration_AsyncItemProcessor())
                .writer(asyncConfiguration_AsyncItemWrtier())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer2> asyncConfiguration_ItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jdbcPagingItemReader")
                .pageSize(300)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jdbcPagingQueryProvider2())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jdbcPagingQueryProvider2() throws Exception {

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
    public ItemProcessor<Customer2,Customer2> asyncConfiguration_ItemProcessor() throws InterruptedException {

        return new ItemProcessor<Customer2, Customer2>() {
            @Override
            public Customer2 process(Customer2 item) throws Exception {

                Thread.sleep(300);

                return new Customer2(item.getId(), item.getFirstname().toUpperCase(),
                        item.getLastname().toUpperCase(), item.getBirthdate());
            }
        };
    }

    @Bean
    public JdbcBatchItemWriter asyncConfiguration_ItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }

    @Bean
    public AsyncItemProcessor asyncConfiguration_AsyncItemProcessor() throws InterruptedException {
        AsyncItemProcessor<Customer2, Customer2> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(asyncConfiguration_ItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        asyncItemProcessor.afterPropertiesSet(); // bean으로 안 만들 경우 필요

        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter asyncConfiguration_AsyncItemWrtier() {
        AsyncItemWriter<Customer2> asyncItemWriter = new AsyncItemWriter<>();
        asyncItemWriter.setDelegate(asyncConfiguration_ItemWriter());
        return asyncItemWriter;
    }
}
