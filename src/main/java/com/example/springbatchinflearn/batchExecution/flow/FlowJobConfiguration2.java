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
public class FlowJobConfiguration2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJobConfiguration2Job(){
        return jobBuilderFactory.get("flowJobConfiguration2Job")
                .start(flowJobConfiguration2FlowA())
                .next(flowJobConfiguration2Step3())
                .next(flowJobConfiguration2FlowB())
                .next(flowJobConfiguration2Step6())
                .end()
                .build();
    }

    @Bean
    public Flow flowJobConfiguration2FlowA() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobConfiguration2FlowA");
        flowBuilder.start(flowJobConfiguration2Step1())
                .next(flowJobConfiguration2Step2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow flowJobConfiguration2FlowB() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flowJobConfiguration2FlowB");
        flowBuilder.start(flowJobConfiguration2Step4())
                .next(flowJobConfiguration2Step5())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step flowJobConfiguration2Step1() {
        return stepBuilderFactory.get("flowJobConfiguration2Step1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step1 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfiguration2Step2() {
        return stepBuilderFactory.get("flowJobConfiguration2Step2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfiguration2Step3() {
        return stepBuilderFactory.get("flowJobConfiguration2Step3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step3 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfiguration2Step4() {
        return stepBuilderFactory.get("flowJobConfiguration2Step4")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step4 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfiguration2Step5() {
        return stepBuilderFactory.get("flowJobConfiguration2Step5")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step5 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfiguration2Step6() {
        return stepBuilderFactory.get("flowJobConfiguration2Step6")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfiguration2Step6 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
