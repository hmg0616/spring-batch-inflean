package com.example.springbatchinflearn.batchExecution.flow;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobExecutionDeciderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobExecutionDeciderJob() {
        return jobBuilderFactory.get("jobExecutionDeciderJob")
                .incrementer(new RunIdIncrementer())
                .start(jobExecutionDeciderStep())
                .next(jobExecutionDeciderDecider())
                .from(jobExecutionDeciderDecider()).on("ODD").to(jobExecutionDeciderOddStep())
                .from(jobExecutionDeciderDecider()).on("EVEN").to(jobExecutionDeciderEvenStep())
                .end()
                .build();
    }

    @Bean
    public JobExecutionDecider jobExecutionDeciderDecider() {
        return new CustomJobExecutionDecider();
    }

    @Bean
    public Step jobExecutionDeciderStep() {
        return stepBuilderFactory.get("jobExecutionDeciderStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("jobExecutionDeciderStep has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step jobExecutionDeciderOddStep() {
        return stepBuilderFactory.get("jobExecutionDeciderOddStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("jobExecutionDeciderOddStep has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step jobExecutionDeciderEvenStep() {
        return stepBuilderFactory.get("jobExecutionDeciderEvenStep")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("jobExecutionDeciderEvenStep has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
