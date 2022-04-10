package com.example.springbatchinflearn.batchExecution.eventListener.skipListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SkipListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job skipListenerConfigurationJob() throws Exception {
        return jobBuilderFactory.get("skipListenerConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(skipListenerConfigurationStep())
                .build();
    }

    @Bean
    public Step skipListenerConfigurationStep() {
        return stepBuilderFactory.get("skipListenerConfigurationStep")
                .<Integer,String>chunk(10)
                .reader(listItemReader())
                .processor(new ItemProcessor<Integer, String>() {
                    @Override
                    public String process(Integer item) throws Exception {
                        if(item == 4) {
                            throw new CustomSkipException("process skipped");
                        }
                        return "item" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> items) throws Exception {
                        for (String item : items) {
                            if(item.equals("item5")) {
                                throw new CustomSkipException("write skipped");
                            }
                            System.out.println("write : " + item);
                        }
                    }
                })
                .faultTolerant()
                .skip(CustomSkipException.class)
                .skipLimit(3)
                .listener(new CustomSkipListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> listItemReader() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        return new LinkedListItemReader<>(list);
    }
}
