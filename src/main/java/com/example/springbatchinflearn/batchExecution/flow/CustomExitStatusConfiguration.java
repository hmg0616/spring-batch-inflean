package com.example.springbatchinflearn.batchExecution.flow;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
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
public class CustomExitStatusConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job customExitStatusJob() {
        return jobBuilderFactory.get("customExitStatusJob")
                .start(customExitStatusStep1())
                    .on("FAILED")
                    .to(customExitStatusStep2())
                    .on("PASS")
                    .stop()
                .end()
                .build();
    }

    @Bean
    public Step customExitStatusStep1() {
        return stepBuilderFactory.get("customExitStatusStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("customExitStatusStep1 has executed");
                        stepContribution.getStepExecution().setExitStatus(ExitStatus.FAILED);
                        return RepeatStatus.FINISHED;
                    }
                })
                .build();
    }

    @Bean
    public Step customExitStatusStep2() {
        return stepBuilderFactory.get("customExitStatusStep2")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("customExitStatusStep2 has executed");
                        return RepeatStatus.FINISHED;
                    }
                })
                .listener(new PassCheckingListener()) // ExitStatus를 강제로 PASS로 바꾸는 Listener
               .build();
    }
}
