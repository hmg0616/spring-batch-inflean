package com.example.springbatchinflearn.batchExecution.scope;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobScope_StepScope_Configuration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    // @Value 사용시 @JobScope 또는 @StepScope를 꼭 붙여야함
    // @JobScope는 step 선언문에 정의
    // @StepScope는 Tasklet이나 ItemReader, ItemWriter, ItemProcessor 선언문에 정의
    @Bean
    public Job scopeConfigurationJob() {
        return jobBuilderFactory.get("scopeConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(scopeConfigurationStep1(null))// 실행시점 이전까지는 null 로 세팅해둠
                .next(scopeConfigurationStep2())
                .listener(new CustomJobListener())
                .build();
    }

    @Bean
    @JobScope
    public Step scopeConfigurationStep1(@Value("#{jobParameters['message']}") String message){ // 실제 실행시점에 값 들어옴
        System.out.println("message = " + message);
        return stepBuilderFactory.get("scopeConfigurationStep1")
                .tasklet(scopeConfigurationTasklet1(null))
                .build();
    }

    @Bean
    public Step scopeConfigurationStep2(){
        return stepBuilderFactory.get("scopeConfigurationStep2")
                .tasklet(scopeConfigurationTasklet2(null))
                .listener(new CustomStepListener())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeConfigurationTasklet1(@Value("#{jobExecutionContext['name']}") String name){
        System.out.println("name = " + name);
        return (stepContribution, chunkContext) -> {
            System.out.println("scopeConfigurationTasklet1 has executed");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet scopeConfigurationTasklet2(@Value("#{stepExecutionContext['name2']}") String name2){
        System.out.println("name2 = " + name2);
        return (stepContribution, chunkContext) -> {
            System.out.println("scopeConfigurationTasklet2 has executed");
            return RepeatStatus.FINISHED;
        };
    }
}
