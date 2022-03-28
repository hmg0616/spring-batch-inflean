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
public class TransitionConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job transitionConfigurationJob(){
        // Step이 다 성공하는 경우 : step1 -> step3 -> step4
        // Step1 실패, 나머지 성공하는 경우 : step1 -> step2 -> step5
        return jobBuilderFactory.get("transitionConfigurationJob")
                .start(transitionConfigurationStep1()) // step1이
                    .on("FAILED") // 실패할 경우
                    .to(transitionConfigurationStep2())
                    .on("FAILED")
                    .stop() // step2가 FAILED면 Job의 상태는 STOPPED로 종료
                .from(transitionConfigurationStep1()) // step1이
                    .on("*") // 실패 외의 나머지 상태일 경우
                    .to(transitionConfigurationStep3())
                    .next(transitionConfigurationStep4())
                .from(transitionConfigurationStep2())
                    .on("*")
                    .to(transitionConfigurationStep5())
                    .end() // Job의 상태는 step5 성공여부 관련없이 COMPLETED로 종료
                .build();
    }

    @Bean
    public Step transitionConfigurationStep1() {
        return stepBuilderFactory.get("transitionConfigurationStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("transitionConfigurationStep1 has executed");
                        throw new RuntimeException("HI");
                        //return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step transitionConfigurationStep2() {
        return stepBuilderFactory.get("transitionConfigurationStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("transitionConfigurationStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step transitionConfigurationStep3() {
        return stepBuilderFactory.get("transitionConfigurationStep3")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("transitionConfigurationStep3 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step transitionConfigurationStep4() {
        return stepBuilderFactory.get("transitionConfigurationStep4")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("transitionConfigurationStep4 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step transitionConfigurationStep5() {
        return stepBuilderFactory.get("transitionConfigurationStep5")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("transitionConfigurationStep5 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }
}
