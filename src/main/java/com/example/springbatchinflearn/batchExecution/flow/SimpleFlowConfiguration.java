package com.example.springbatchinflearn.batchExecution.flow;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SimpleFlowConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleFlowConfigurationJob(){
        return jobBuilderFactory.get("simpleFlowConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(simpleFlowConfigurationFlow1())
                    .on("COMPLETED")
                    .to(simpleFlowConfigurationFlow2())
                .from(simpleFlowConfigurationFlow1())
                    .on("FAILED")
                    .to(simpleFlowConfigurationFlow3())
                .end()
                .build();
    }

    @Bean
    public Flow simpleFlowConfigurationFlow1() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlowConfigurationFlow1");
        flowBuilder.start(simpleFlowConfigurationStep1())
                .next(simpleFlowConfigurationStep2())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlowConfigurationFlow2() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlowConfigurationFlow2");
        flowBuilder.start(simpleFlowConfigurationFlow3())
                .next(simpleFlowConfigurationStep5())
                .next(simpleFlowConfigurationStep6())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow simpleFlowConfigurationFlow3() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("simpleFlowConfigurationFlow3");
        flowBuilder.start(simpleFlowConfigurationStep3())
                .next(simpleFlowConfigurationStep4())
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Step simpleFlowConfigurationStep1() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep1 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleFlowConfigurationStep2() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep2 has executed");
                    throw new RuntimeException("simpleFlowConfigurationStep2 error");
//                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleFlowConfigurationStep3() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep3")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep3 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleFlowConfigurationStep4() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep4")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep4 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleFlowConfigurationStep5() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep5")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep5 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step simpleFlowConfigurationStep6() {
        return stepBuilderFactory.get("simpleFlowConfigurationStep6")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("simpleFlowConfigurationStep6 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
