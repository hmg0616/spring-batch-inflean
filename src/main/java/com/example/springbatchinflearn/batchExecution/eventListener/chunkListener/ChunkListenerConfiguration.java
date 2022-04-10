package com.example.springbatchinflearn.batchExecution.eventListener.chunkListener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChunkListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkListenerConfigurationJob() throws Exception {
        return jobBuilderFactory.get("chunkListenerConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(chunkListenerConfigurationStep1())
                .build();
    }

    @Bean
    public Step chunkListenerConfigurationStep1() {
        return stepBuilderFactory.get("chunkListenerConfigurationStep1")
                .<Integer, String>chunk(10)
                .listener(new CustomChunkListener())
                .listener(new CustomItemReadListener())
                .listener(new CustomItemProcessListener())
                .listener(new CustomItemWriteListener())
                .reader(chunkListenerConfigurationReader())
                .processor((ItemProcessor) item -> {
                    // throw new RuntimeException();
                    return "item" + item;
                })
                .writer((ItemWriter<String>) items -> {
                    System.out.println("items = " + items);
                })
                .build();
    }

    @Bean
    public ItemReader<Integer> chunkListenerConfigurationReader() {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        return new ListItemReader<>(list);
    }
}
