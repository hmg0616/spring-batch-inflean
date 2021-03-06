package com.example.springbatchinflearn.batchExecution.chunk.writer;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JpaConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaConfigurationJob() throws Exception {
        return jobBuilderFactory.get("jpaConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jpaConfigurationStep())
                .build();
    }

    @Bean
    public Step jpaConfigurationStep() throws Exception {
        return stepBuilderFactory.get("jpaConfigurationStep")
                .<Customer2, Customer5>chunk(10)
                .reader(jpaConfigurationItemReader())
                .processor(jpaConfigurationItemProcessor())
                .writer(jpaConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<? super Customer2,? extends Customer5> jpaConfigurationItemProcessor() {
        return new ItemProcessor<Customer2, Customer5>() {
            @Override
            public Customer5 process(Customer2 customer2) throws Exception {
                ModelMapper modelMapper = new ModelMapper();
                // reader?????? ?????? Customer2 ????????? Customer5 ??????(?????????)??? ??????
                Customer5 customer5 = modelMapper.map(customer2, Customer5.class);
                return customer5;
            }
        };
    }

    @Bean
    public ItemWriter<? super Customer5> jpaConfigurationItemWriter() {
        return new JpaItemWriterBuilder<Customer5>()
                .usePersist(true) // true(?????????) : persist ??????, false : merge ??????
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // reader??? ????????? JdbcPagingConfiguration.java ?????? ??????????????? ??????
    // customer ??????????????? firstname??? R??? ???????????? ????????? select
    @Bean
    public ItemReader<? extends Customer2> jpaConfigurationItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("jpaConfigurationItemReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(jpaConfigurationPagingQueryProvider())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider jpaConfigurationPagingQueryProvider() throws Exception {

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
