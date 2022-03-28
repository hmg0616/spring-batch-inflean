package com.example.springbatchinflearn.batchExecution.batchControl.retry;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration1 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryConfiguration1Job() throws Exception {
        return jobBuilderFactory.get("retryConfiguration1Job")
                .incrementer(new RunIdIncrementer())
                .start(retryConfiguration1Step())
                .build();
    }

    @Bean
    public Step retryConfiguration1Step() {
        return stepBuilderFactory.get("retryConfiguration1Step")
                .<String, String>chunk(5)
                .reader(retryConfiguration1Reader())
                .processor(retryConfiguration1Processor())
                .writer(items -> items.forEach(item -> System.out.println("ItemWriter : " + item)))
                .faultTolerant()
                .skip(RetryableException.class) // retry 2번까지 했는 데도 실패할 경우 에러를 던지게 되는데 그 에러를 skip할 수 있도록 처리도 가능
                .skipLimit(2)
                .retry(RetryableException.class)  // RetryableException 발생시 2번까지 retry
                .retryLimit(2)
                // RetryPolicy를 직접 구현도 가능
//                .retryPolicy(retryConfiguration1RetryPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> retryConfiguration1Processor() {
        return new RetryItemProcessor1();
    }

    @Bean
    public ItemReader<String> retryConfiguration1Reader() {
        List<String> items = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy retryConfiguration1RetryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);  // RetryableException 발생시 2번까지 retry
        return simpleRetryPolicy;
    }
}
