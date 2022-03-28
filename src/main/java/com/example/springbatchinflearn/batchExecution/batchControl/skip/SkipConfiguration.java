package com.example.springbatchinflearn.batchExecution.batchControl.skip;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SkipConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job skipConfigurationJob() throws Exception {
        return jobBuilderFactory.get("skipConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(skipConfigurationStep())
                .build();
    }

    @Bean
    public Step skipConfigurationStep() throws Exception {
        return stepBuilderFactory.get("skipConfigurationStep")
                .<String,String>chunk(5)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws SkippableException {
                        i++;
                        if(i == 3) {
                            throw new SkippableException("skip");
                        }
                        System.out.println("ItemReader : " + i);
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(skipItemProcessor())
                .writer(skipItemWriter())
                .faultTolerant()
                // API 사용 방식
                // 1. skip()
                .skip(SkippableException.class) // 내부적으로 LimitCheckingItemSkipPolicy.class 사용됨
                .skipLimit(3) // 2일때, 3일때, 4일때 결과 테스트

                // 2. noSkip()
//                .noSkip(NoSkippableException.class) // NoSkippableException 스킵 안함

                // SkipPolicy 설정 방식
                // 1. LimitCheckingItemSkipPolicy.class
//                .skipPolicy(limitCheckingItemSkipPolicy())
                // 2. AlwaysSkipItemSkipPolicy.class
//                .skipPolicy(new AlwaysSkipItemSkipPolicy()) // 무조건 스킵
                // 3. NeverSkipItemSkipPolicy.class
//                .skipPolicy(new NeverSkipItemSkipPolicy()) // 스킵 안함
                .build();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(SkippableException.class, true);

        LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(4, exceptionClass);
        return limitCheckingItemSkipPolicy;
    }

    @Bean
    public ItemWriter<? super String> skipItemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public ItemProcessor<? super String, String> skipItemProcessor() {
        return new SkipItemProcessor();
    }
}
