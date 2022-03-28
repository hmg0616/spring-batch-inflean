package com.example.springbatchinflearn.batchExecution.chunk.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CompositeItemConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job compositeItemConfigurationJob() throws Exception {
        return jobBuilderFactory.get("compositeItemConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(compositeItemConfigurationStep())
                .build();
    }

    @Bean
    public Step compositeItemConfigurationStep() throws Exception {
        return stepBuilderFactory.get("compositeItemConfigurationStep")
                .<String, String>chunk(10)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item";
                    }
                })
                .processor(compositeItemConfigurationProcessor())
                .writer(items -> System.out.println(items))
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> compositeItemConfigurationProcessor() {
        List itemProcessorList = new ArrayList();
        itemProcessorList.add(new CompositeItemConfigurationProcessor());
        itemProcessorList.add(new CompositeItemConfigurationProcessor2());

        return new CompositeItemProcessorBuilder<>()
                .delegates(itemProcessorList)
                .build();
    }
}
