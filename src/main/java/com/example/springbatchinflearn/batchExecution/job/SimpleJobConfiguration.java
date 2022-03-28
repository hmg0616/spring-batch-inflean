package com.example.springbatchinflearn.batchExecution.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(simpleJobStep1())
                .next(simpleJobStep2())
                .next(simpleJobStep3())
//                .validator(new DefaultJobParametersValidator(
//                                new String[]{"name","date"} ,new String[]{"count"})) // requiredKeys, optionalKeys 설정
                .validator(new CustomJobParametersValidator()) // 사용자 정의 파라미터 검증
                .preventRestart() // Job이 실패했어도 재실행되는 것을 방지 (같은 파라미터에서의 재실행일때)
//                .incrementer(new RunIdIncrementer()) // 기본 제공 JobParametersIncrementer
                .incrementer(new CustomJobParametersIncrementer()) // 사용자 정의 JobParametersIncrementer
                .build();
    }

    @Bean
    public Step simpleJobStep1() {
        return stepBuilderFactory.get("simpleJobStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleJobStep1 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleJobStep2() {
        return stepBuilderFactory.get("simpleJobStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleJobStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleJobStep3() {
        return stepBuilderFactory.get("simpleJobStep3")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleJobStep3 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
