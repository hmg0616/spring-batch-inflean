package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class JpaPagingConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingConfigurationJob() {
        return jobBuilderFactory.get("jpaPagingConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jpaPagingConfigurationStep())
                .build();
    }


    @Bean
    public Step jpaPagingConfigurationStep() {
        return stepBuilderFactory.get("jpaPagingConfigurationStep")
                .<Customer3, Customer3>chunk(10)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer3> jpaPagingItemReader() {
        return new JpaPagingItemReaderBuilder<Customer3>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(10)
                .queryString("select c from Customer3 c join fetch c.address") // n+1 문제 방지를 위해 패치조인
                .build();
    }

    @Bean
    public ItemWriter<Customer3> jpaPagingItemWriter() {
        return items -> {
            for(Customer3 customer : items) {
                System.out.println(customer.getAddress().getLocation());
            }
        };
    }
}
