package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class PartitioningConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job partitioningConfigurationJob() throws Exception {
        return jobBuilderFactory.get("partitioningConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(partitioningMasterStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step partitioningMasterStep() throws Exception {
        return stepBuilderFactory.get("partitioningMasterStep")
                .partitioner(partitioningSlaveStep().getName(), partitioningConfigurationPartitioner()) // partitioner 설정
                .step(partitioningSlaveStep()) // slaveStep
                .gridSize(4) // 몇개의 파티션으로 나눌 것인지 (slaveStep 개수)
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 스레드풀 실행자 설정
                .build();
    }

    @Bean
    public Partitioner partitioningConfigurationPartitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
        partitioner.setDataSource(dataSource);
        partitioner.setTable("customer");
        partitioner.setColumn("id");

        return partitioner;
    }

    @Bean
    public Step partitioningSlaveStep() throws Exception {
        return stepBuilderFactory.get("partitioningSlaveStep")
                .<Customer2,Customer2> chunk(2)
                .reader(partitioningConfigurationItemReader(null, null))
                .writer(partitioningConfigurationItemWriter())
                .build();
    }

    @Bean
    @StepScope // 초기화시에는 프록시 객체 생성, 실행시점에 실제 빈 생성됨
    public ItemReader<? extends Customer2> partitioningConfigurationItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue, // executionContext는 partition(thread) 마다 따로따로 가짐
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) throws Exception {
        System.out.println("reading : " + minValue + " to " + maxValue);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jdbcPagingItemReader")
                .pageSize(2)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jdbcPagingQueryProvider4(minValue,maxValue))
//                .parameterValues(parameters)
                .build();

    }

//    @Bean
    public PagingQueryProvider jdbcPagingQueryProvider4(Long minValue, Long maxValue) throws Exception {

        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource);
        queryProviderFactoryBean.setSelectClause("id,firstname,lastname,birthdate");
        queryProviderFactoryBean.setFromClause("from customer");
//        queryProviderFactoryBean.setWhereClause("where firstname like :firstname");
        queryProviderFactoryBean.setWhereClause("where id >= " + minValue + " and id < " + maxValue);

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProviderFactoryBean.setSortKeys(sortKeys);
        return queryProviderFactoryBean.getObject();
    }

    @Bean
    @StepScope // writer도 병렬 처리하려면 스코프 설정해야함
    public JdbcBatchItemWriter partitioningConfigurationItemWriter() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }
}
