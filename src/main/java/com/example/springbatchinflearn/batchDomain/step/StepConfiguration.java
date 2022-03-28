package com.example.springbatchinflearn.batchDomain.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepConfigurationJob() {
        return jobBuilderFactory.get("stepConfigurationJob")
                .start(stepConfigurationStep1())
                .next(stepConfigurationStep2())
                .next(stepConfigurationStep3())
                .build();
    }

    @Bean
    public Step stepConfigurationStep1() {
        return stepBuilderFactory.get("stepConfigurationStep1") // Step 생성
                .tasklet(new CustomTasklet()) // TaskletStep
                .build();
    }

    @Bean
    public Step stepConfigurationStep2() {
        return stepBuilderFactory.get("stepConfigurationStep2") // Step 생성
                .tasklet(((stepContribution, chunkContext) -> { // TaskletStep
                    // stepContribution.getStepExecution().getJobExecution().getJobInstance().getJobName();
                    System.out.println("StepConfiguration >> Step2 was executed");
                    return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
                }))
                .build();
    }

    @Bean
    public Step stepConfigurationStep3() {
        return stepBuilderFactory.get("stepConfigurationStep3") // Step 생성
                .tasklet(((stepContribution, chunkContext) -> { // TaskletStep
                    System.out.println("StepConfiguration >> Step3 was executed");
                    return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
                }))
                .build();
    }
}
