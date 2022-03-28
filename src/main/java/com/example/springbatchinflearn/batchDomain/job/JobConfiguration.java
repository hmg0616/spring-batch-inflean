package com.example.springbatchinflearn.batchDomain.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobConfigurationJob() {
        return jobBuilderFactory.get("jobConfigurationJob")
                .start(jobConfigurationStep1())
                .next(jobConfigurationStep2())
                .build();
    }

    @Bean
    public Step jobConfigurationStep1() {
        return stepBuilderFactory.get("jobConfigurationStep1") // Step 생성
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                        // 전달 받은 JobParameter 가져오기
                        // 1. stepContribution 사용 - JobParameter를 반환 받음
                        JobParameters jobParameters = stepContribution.getStepExecution().getJobExecution().getJobParameters();
                        JobParameter nameParam = jobParameters.getParameters().get("name");
                        JobParameter seqParam = jobParameters.getParameters().get("seq");
                        String dateParam = jobParameters.getString("date"); // 이렇게 값 가져올 수도 있음
                        String ageParam = jobParameters.getString("age");
                        jobParameters.getString("age");

                        // 2. chunkContext 사용 - 객체 자체를 반환 받음
                        Map<String,Object> jobParameters1 = chunkContext.getStepContext().getJobParameters();
                        String name = (String) jobParameters1.get("name");
                        Long seq = (Long) jobParameters1.get("seq");
                        Date date = (Date) jobParameters1.get("date");
                        Double age = (Double) jobParameters1.get("age");

                        System.out.println("Step1 was executed");
                        return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
                    }
                })
                .build();
    }

    @Bean
    public Step jobConfigurationStep2() {
        return stepBuilderFactory.get("jobConfigurationStep2") // Step 생성
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Step2 was executed");
                        return RepeatStatus.FINISHED; // tasklet은 기본적으로 무한 실행되므로 FINISHED를 리턴해줘야 실행 멈춤
                    }
                })
                .build();
    }
}
