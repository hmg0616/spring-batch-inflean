package com.example.springbatchinflearn.batchExecution.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ItemStreamConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemStreamConfigurationJob() {
        return jobBuilderFactory.get("itemStreamConfigurationJob")
                .start(itemStreamConfigurationStep1())
                .next(itemStreamConfigurationStep2())
                .build();
    }

    @Bean
    public Step itemStreamConfigurationStep1() {
        return stepBuilderFactory.get("itemStreamConfigurationStep1")
                .<String, String>chunk(5)
                .reader(itemStreamConfigurationItemReader())
                .writer(itemStreamConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super String> itemStreamConfigurationItemWriter() {
        return new CustomItemStreamWriter();
    }

    @Bean
    public CustomItemStreamReader itemStreamConfigurationItemReader() {
        List<String> items = new ArrayList<>(10);
        for(int i = 0; i <= 10; i++) {
            items.add(String.valueOf(i));
        }
        return new CustomItemStreamReader(items);
    }


    @Bean
    public Step itemStreamConfigurationStep2() {
        return stepBuilderFactory.get("itemStreamConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("itemStreamConfigurationStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
