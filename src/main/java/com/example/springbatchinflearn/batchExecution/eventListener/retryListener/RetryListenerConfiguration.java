package com.example.springbatchinflearn.batchExecution.eventListener.retryListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RetryListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryListenerConfigurationJob() {
        return jobBuilderFactory.get("retryListenerConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(retryListenerConfigurationStep())
                .build();
    }


    @Bean
    public Step retryListenerConfigurationStep() {
        return stepBuilderFactory.get("retryListenerConfigurationStep")
                .<Integer,String>chunk(10)
                .reader(listItemReader2())
                .processor(new RetryListenerItemProcessor())
                .writer(new RetryListenerItemWriter())
                .faultTolerant()
                .retry(CustomRetryException.class)
                .retryLimit(2)
                .listener(new CustomRetryListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> listItemReader2() {
        List<Integer> list = Arrays.asList(1,2,3,4);
        return new ListItemReader<>(list);
    }
}
