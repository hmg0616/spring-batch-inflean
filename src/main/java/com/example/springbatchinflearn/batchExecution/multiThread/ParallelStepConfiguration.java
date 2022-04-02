package com.example.springbatchinflearn.batchExecution.multiThread;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@RequiredArgsConstructor
@Configuration
public class ParallelStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job parallelStepConfigurationJob() {
        return jobBuilderFactory.get("parallelStepConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(parallelStepConfigurationFlow1())
                .split(parallelStepConfigurationTaskExecutor()).add(parallelStepConfigurationFlow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Flow parallelStepConfigurationFlow1() {

        TaskletStep step = stepBuilderFactory.get("parallelStepConfigurationStep1")
                .tasklet(parallelStepConfigurationTasklet())
                .build();

        return new FlowBuilder<Flow>("parallelStepConfigurationFlow1")
                .start(step)
                .build();
    }

    private Flow parallelStepConfigurationFlow2() {

        TaskletStep step2 = stepBuilderFactory.get("parallelStepConfigurationStep2")
                .tasklet(parallelStepConfigurationTasklet())
                .build();

        TaskletStep step3 = stepBuilderFactory.get("parallelStepConfigurationStep3")
                .tasklet(parallelStepConfigurationTasklet())
                .build();

        return new FlowBuilder<Flow>("parallelStepConfigurationFlow2")
                .start(step2)
                .next(step3)
                .build();
    }

    @Bean
    public Tasklet parallelStepConfigurationTasklet() {
        return new ParallelStepConfigurationTasklet();
    }

    @Bean
    public TaskExecutor parallelStepConfigurationTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2); // 기본 스레드 생성
        taskExecutor.setMaxPoolSize(4); // 최대 스레드 생성 가능 개수
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }

}
