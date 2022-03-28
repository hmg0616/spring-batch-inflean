package com.example.springbatchinflearn.batchExecution.flow;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FlowStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowStepConfigurationJob(){
        return jobBuilderFactory.get("flowStepConfigurationJob")
                .start(flowStepConfigurationFlowStep())
                .next(flowStepConfigurationStep2())
                .build();
    }

    @Bean
    public Step flowStepConfigurationFlowStep(){
        return stepBuilderFactory.get("flowStepConfigurationFlowStep")
                .flow(flowStepConfigurationFlow()) // Step 안에 Flow를 품음
                .build();
    }

    @Bean
    public Flow flowStepConfigurationFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowStepConfigurationFlow");
        flowBuilder.start(flowStepConfigurationStep1())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step flowStepConfigurationStep1() {
        return stepBuilderFactory.get("flowStepConfigurationStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowStepConfigurationStep1 has executed");
                        throw new RuntimeException("flowStepConfigurationStep1 error");
//                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowStepConfigurationStep2() {
        return stepBuilderFactory.get("flowStepConfigurationStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowStepConfigurationStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
