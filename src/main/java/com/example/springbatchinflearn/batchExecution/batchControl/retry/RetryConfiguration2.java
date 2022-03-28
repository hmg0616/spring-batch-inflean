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
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job retryConfiguration2Job() throws Exception {
        return jobBuilderFactory.get("retryConfiguration2Job")
                .incrementer(new RunIdIncrementer())
                .start(retryConfiguration2Step())
                .build();
    }

    @Bean
    public Step retryConfiguration2Step() {
        return stepBuilderFactory.get("retryConfiguration2Step")
                .<String, Customer>chunk(5)
                .reader(retryConfiguration2Reader())
                .processor(retryConfiguration2Processor())
                .writer(items -> items.forEach(item -> System.out.println("ItemWriter : " + item)))
                .faultTolerant()
//                .skip(RetryableException.class) // retry 2번까지 했는 데도 실패할 경우 에러를 던지게 되는데 그 에러를 skip할 수 있도록 처리도 가능
//                .skipLimit(2)
//                .retry(RetryableException.class)  // RetryableException 발생시 2번까지 retry
//                .retryLimit(2)
                .build();
    }

    @Bean
    public ItemProcessor<? super String, Customer> retryConfiguration2Processor() {
        return new RetryItemProcessor2();
    }

    @Bean
    public ItemReader<String> retryConfiguration2Reader() {
        List<String> items = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
            System.out.println("ItemReader : " + i);
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryTemplate retryConfiguration2RetryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass); // RetryableException 발생시 2번까지 retry
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy); // retry 전 2초 대기

        return retryTemplate;

    }
}
