package com.example.springbatchinflearn.batchExecution.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ItemReader_ItemProcessor_ItemWriter_Configuration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job iR_IP_IW_ConfigurationJob() {
        return jobBuilderFactory.get("iR_IP_IW_ConfigurationJob")
                .start(iR_IP_IW_ConfigurationStep1())
                .next(iR_IP_IW_ConfigurationStep2())
                .build();
    }

    @Bean
    public Step iR_IP_IW_ConfigurationStep1() {
        return stepBuilderFactory.get("iR_IP_IW_ConfigurationStep1")
                .<Customer, Customer>chunk(3)
                .reader(iR_IP_IW_itemReader())
                .processor(iR_IP_IW_itemProcessor())
                .writer(iR_IP_IW_itemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> iR_IP_IW_itemReader() {
        return new CustomItemReader(Arrays.asList(
                  new Customer("user1")
                , new Customer("user2")
                , new Customer("user3")));
    }

    @Bean
    public ItemProcessor<? super Customer,? extends Customer> iR_IP_IW_itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<? super Customer> iR_IP_IW_itemWriter() {
        return new CustomItemWriter();
    }

    @Bean
    public Step iR_IP_IW_ConfigurationStep2() {
        return stepBuilderFactory.get("iR_IP_IW_ConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("iR_IP_IW_ConfigurationStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
