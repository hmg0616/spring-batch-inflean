package com.example.springbatchinflearn.batchExecution.batchControl;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RepeatTemplateConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job repeatTemplateConfigurationJob() throws Exception {
        return jobBuilderFactory.get("repeatTemplateConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(repeatTemplateConfigurationStep())
                .build();
    }

    @Bean
    public Step repeatTemplateConfigurationStep() {
        return stepBuilderFactory.get("repeatTemplateConfigurationStep")
                .<String,String>chunk(5)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {

                    RepeatTemplate repeatTemplate = new RepeatTemplate();

                    @Override
                    public String process(String item) throws Exception {

                        // 1. 3번 반복 후 종료
                         repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));

                         // 2. 3초 동안 반복 후 반복 종료
//                         repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000));

                        // 3. 복합 CompletionPolicy 만들기
                        // > 여러 CompletionPolicy 중 만족하는거 하나라도 있으면 반복 종료
                        // > (아래의 경우 3번 반복이 3초보다 빠르므로 3번 반복 후 끝남.)
//                        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
//                        CompletionPolicy[] completionPolicies = new CompletionPolicy[] {
//                                new SimpleCompletionPolicy(3),
//                                new TimeoutTerminationPolicy(3000)
//                        };
//                        compositeCompletionPolicy.setPolicies(completionPolicies);
//                        repeatTemplate.setCompletionPolicy(compositeCompletionPolicy);

                        // 4. 예외가 발생해도 3번까지는 반복 수행. 3번 초과 시 예외 던짐
                        // > SimpleLimitExceptionHandler의 경우 내부적으로 afterPropertiesSet()를 구현하는데
                        // > 빈 라이프 사이클 중에 afterPropertiesSet()를 호출하여 limit값을 설정해주기 때문에
                        // > 빈으로 등록해야 limit 설정이 가능하다.
                        // > 빈으로 등록하지 않으면 limit 값이 0임. 즉 예외 발생하면 바로 실패.
//                        repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler());

                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext repeatContext) throws Exception {
                                // 비즈니스 로직 구현
                                System.out.println("repeatTemplate is testing");
                                return RepeatStatus.CONTINUABLE;

                                // 4. SimpleLimitExceptionHandler 테스트 시 예외 강제 발생 테스트
//                                throw new RuntimeException("SimpleLimitExceptionHandler Test");
                            }
                        });

                        return item;
                    }
                })
                .writer(items -> System.out.println(items))
                .build();
    }

    @Bean
    public ExceptionHandler simpleLimitExceptionHandler() {
        return new SimpleLimitExceptionHandler(3);
    }
}
