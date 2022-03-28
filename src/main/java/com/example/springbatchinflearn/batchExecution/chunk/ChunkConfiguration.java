package com.example.springbatchinflearn.batchExecution.chunk;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChunkConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job chunkConfigurationJob() {
        return jobBuilderFactory.get("chunkConfigurationJob")
                .start(chunkConfigurationStep1())
                .next(chunkConfigurationStep2())
                .build();
    }

    @Bean
    public Step chunkConfigurationStep1() {
        return stepBuilderFactory.get("chunkConfigurationStep1")
                .<String, String>chunk(2)
                .reader(new ListItemReader<>(Arrays.asList("item1","item2","item3","item4","item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        Thread.sleep(300);
                        System.out.println("item = " + item);
                        return "my" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(List<? extends String> items) throws Exception {
                        Thread.sleep(300);
//                        System.out.println("items = " + items);
                        items.forEach(item -> System.out.println(item));
                    }
                })
                .build();
    }


    @Bean
    public Step chunkConfigurationStep2() {
        return stepBuilderFactory.get("chunkConfigurationStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("chunkConfigurationStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
