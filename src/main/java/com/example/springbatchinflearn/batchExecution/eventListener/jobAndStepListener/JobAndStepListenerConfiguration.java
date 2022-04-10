package com.example.springbatchinflearn.batchExecution.eventListener.jobAndStepListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobAndStepListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final CustomStepExecutionListener customStepExecutionListener;

    @Bean
    public Job jobAndStepListenerConfigurationJob() throws Exception {
        return jobBuilderFactory.get("jobAndStepListenerConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jobAndStepListenerConfigurationStep1())
                .next(jobAndStepListenerConfigurationStep2())
                .listener(new CustomJobExecutionListener()) // 인터페이스 구현 방식
//                .listener(new CustomAnnotationJobExecutionListener()) // 어노테이션 방식
                .build();
    }

    @Bean
    public Step jobAndStepListenerConfigurationStep1() {
        return stepBuilderFactory.get("jobAndStepListenerConfigurationStep1")
                .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
                .listener(customStepExecutionListener) // Bean으로 가져오면 step1과 2가 같은 listener 객체를 봄. 그게 싫다면 new CustomStepExecutionListener() 로 받기
                .build();
    }

    @Bean
    public Step jobAndStepListenerConfigurationStep2() {
        return stepBuilderFactory.get("jobAndStepListenerConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED)
                .listener(customStepExecutionListener)
                .build();
    }
}
