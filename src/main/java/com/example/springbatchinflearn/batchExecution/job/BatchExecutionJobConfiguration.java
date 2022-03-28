package com.example.springbatchinflearn.batchExecution.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchExecutionJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // SimpleJobBuilder 테스트
//    @Bean
//    public Job batchExecution_SimpleJob() {
//        return jobBuilderFactory.get("batchExecution_SimpleJob") // JobBuilderFactory -> JobBuilder
//                .start(batchExecution_JobStep1()) // JobBuilder -> SimpleJobBuilder
//                .next(batchExecution_JobStep2())
//                .build(); // SimpleJob
//    }

    // FlowJobBuilder 테스트
    @Bean
    public Job batchExecution_FlowJob() {
        return jobBuilderFactory.get("batchExecution_FlowJob") // JobBuilderFactory -> JobBuilder
                .start(batchExecution_flow()) // JobBuilder -> FlowJobBuilder -> JobFlowBuilder
                .next(batchExecution_JobStep3())
                .end()
                .build(); // FlowJob
    }

    @Bean
    public Flow batchExecution_flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("batchExecution_flow");
        flowBuilder.start(batchExecution_JobStep1())
                .next(batchExecution_JobStep2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step batchExecution_JobStep1() {
        return stepBuilderFactory.get("batchExecution_JobStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("batchExecution_JobStep1 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step batchExecution_JobStep2() {
        return stepBuilderFactory.get("batchExecution_JobStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("batchExecution_JobStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step batchExecution_JobStep3() {
        return stepBuilderFactory.get("batchExecution_JobStep3")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("batchExecution_JobStep3 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
