package com.example.springbatchinflearn.batchExecution.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TaskletStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskletStepJob() {
        return jobBuilderFactory.get("taskletStepJob")
                .incrementer(new RunIdIncrementer())
                .start(taskletStepStep1())
                .next(taskletStepStep2())
                .build();
    }

    @Bean
    public Step taskletStepStep1() {
        return stepBuilderFactory.get("taskletStepStep1")
                .tasklet(new Tasklet() { // 익명클래스 사용
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("taskletStepStep has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step taskletStepStep2() {
        return stepBuilderFactory.get("taskletStepStep2")
                .tasklet(new CustomTaslketStepTasklet()) // 클래스 구현
                .build();
    }
}
