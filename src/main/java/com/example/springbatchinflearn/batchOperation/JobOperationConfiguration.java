package com.example.springbatchinflearn.batchOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobOperationConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobRegistry jobRegistry;

    @Bean
    public Job jobOperationConfigurationJob() {
        return jobBuilderFactory.get("jobOperationConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jobOperationConfigurationStep1())
                .next(jobOperationConfigurationStep2())
                .build();
    }

    @Bean
    public Step jobOperationConfigurationStep1() {
        return stepBuilderFactory.get("jobOperationConfigurationStep1")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("jobOperationConfigurationStep1 was executed");
                    Thread.sleep(3000);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step jobOperationConfigurationStep2() {
        return stepBuilderFactory.get("jobOperationConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("jobOperationConfigurationStep2 was executed");
                    Thread.sleep(3000);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public BeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
