package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
public class JsonConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jsonConfigurationJob() {
        return jobBuilderFactory.get("jsonConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jsonConfigurationStep1())
                .build();
    }

    @Bean
    public Step jsonConfigurationStep1() {
        return stepBuilderFactory.get("jsonConfigurationStep1")
                .<Customer, Customer>chunk(3)
                .reader(jsonConfigurationItemReader())
                .writer(jsonConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jsonConfigurationItemReader() {
        return new JsonItemReaderBuilder<Customer>()
                .name("jsonReader")
                .resource(new ClassPathResource("/chunk/reader/customer4.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
                .build();
    }

    @Bean
    public ItemWriter<Customer> jsonConfigurationItemWriter() {
        return items -> {
            for(Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

}
