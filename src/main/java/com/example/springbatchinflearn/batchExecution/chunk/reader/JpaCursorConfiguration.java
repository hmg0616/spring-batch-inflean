package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JpaCursorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorConfigurationJob() {
        return jobBuilderFactory.get("jpaCursorConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jpaCursorConfigurationStep())
                .build();
    }

    @Bean
    public Step jpaCursorConfigurationStep() {
        return stepBuilderFactory.get("jpaCursorConfigurationStep")
                .<Customer2, Customer2>chunk(5)
                .reader(jpaCursorItemReader())
                .writer(jpaCursorItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer2> jpaCursorItemReader() {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JpaCursorItemReaderBuilder<Customer2>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c from customer c where firstname like :firstname")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemWriter<Customer2> jpaCursorItemWriter() {
        return items -> {
            for(Customer2 item : items) {
                System.out.println(item.toString());
            }
        };
    }
}
