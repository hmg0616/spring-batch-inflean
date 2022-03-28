package com.example.springbatchinflearn.batchExecution.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

// 제대로 실행 안되는 프로그램임.
// 그냥 개념 알려주려고 대충 짠 코드라서..
@Configuration
@RequiredArgsConstructor
public class StepBuilderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job stepBuilderJob1() {
        return jobBuilderFactory.get("stepBuilderJob1")
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderStep1())
                .next(stepBuilderStep2())
                .next(stepBuilderStep3())
                .build();
    }

    @Bean
    public Step stepBuilderStep1() {
        return stepBuilderFactory.get("stepBuilderStep1")
                .tasklet((stepContribution, chunkContext) -> { // TaskletStepBuilder 테스트
                    System.out.println("stepBuilderStep1 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step stepBuilderStep2() {
        return stepBuilderFactory.get("stepBuilderStep2")
                .<String, String>chunk(3) // SimpleStepBuilder 테스트
                .reader(new ItemReader<String>() {
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        return null;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String s) throws Exception {
                        return null;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> list) throws Exception {

                    }
                })
                .build();
    }

    @Bean
    public Step stepBuilderStep3() {
        return stepBuilderFactory.get("stepBuilderStep3") 
                .partitioner(stepBuilderStep1()) // PartitionStepBuilder 테스트
                .gridSize(2)
                .build();
    }

    @Bean
    public Step stepBuilderStep4() {
        return stepBuilderFactory.get("stepBuilderStep4")
                .job(stepBuilderJob2()) // JobStepBuilder 테스트
                .build();
    }

    @Bean
    public Step stepBuilderStep5() {
        return stepBuilderFactory.get("stepBuilderStep5")
                .flow(stepBuilderFlow()) // FlowStepBuilder 테스트
                .build();
    }

    @Bean
    public Job stepBuilderJob2() {
        return jobBuilderFactory.get("stepBuilderJob2")
                .start(stepBuilderStep1())
                .next(stepBuilderStep2())
                .next(stepBuilderStep3())
                .build();
    }

    @Bean
    public Flow stepBuilderFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("stepBuilderFlow");
        flowBuilder.start(stepBuilderStep2()).end();
        return flowBuilder.build();
    }
}
