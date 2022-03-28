package com.example.springbatchinflearn.batchExecution.step;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TaskletStep2Configuration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job taskletStep2Job() {
        return jobBuilderFactory.get("taskletStep2Job")
                .start(taskletStep2Step1())
                .next(taskletStep2Step2())
                .build();
    }

    @Bean
    public Step taskletStep2Step1() {
        return stepBuilderFactory.get("taskletStep2Step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("taskletStep2Step1 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .allowStartIfComplete(true) // Job 재실행 시 Step의 이전 성공 여부와 상관없이 항상 step을 실행함
                .build();
    }

    @Bean
    public Step taskletStep2Step2() {
        return stepBuilderFactory.get("taskletStep2Step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        throw new RuntimeException("taskletStep2Step2 error");
                    }
                })
                .startLimit(2) // Job 재실행 시 이 Step은 최대 2번까지만 실행이 가능함
                .build();
    }
}
