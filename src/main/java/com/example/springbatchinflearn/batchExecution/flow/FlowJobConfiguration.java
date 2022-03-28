package com.example.springbatchinflearn.batchExecution.flow;

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
public class FlowJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flowJobConfigurationJob(){
        return jobBuilderFactory.get("flowJobConfigurationJob")
                .start(flowJobConfigurationStep1()) // step1이
                .on("COMPLETED") // 성공하면
                    .to(flowJobConfigurationStep3()) // Step3로 가라
                .from(flowJobConfigurationStep1()) // step1이
                .on("FAILED") // 실패하면
                    .to(flowJobConfigurationStep2()) // step2로 가라
                .end() // FlowBuilder 종료
                .build();
    }

    @Bean
    public Step flowJobConfigurationStep1() {
        return stepBuilderFactory.get("flowJobConfigurationStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfigurationStep1 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step flowJobConfigurationStep2() {
        return stepBuilderFactory.get("flowJobConfigurationStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfigurationStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
    @Bean
    public Step flowJobConfigurationStep3() {
        return stepBuilderFactory.get("flowJobConfigurationStep3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("flowJobConfigurationStep3 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
