package com.example.springbatchinflearn.batchExecution.chunk.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job itemWriterAdapterConfigurationJob() {
        return jobBuilderFactory.get("itemWriterAdapterConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(itemWriterAdapterConfigurationStep())
                .build();
    }

    @Bean
    public Step itemWriterAdapterConfigurationStep() {
        return stepBuilderFactory.get("itemWriterAdapterConfigurationStep")
                .<String,String>chunk(10)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i>10 ? null : "item" + i;
                    }
                })
                .writer(itemWriterAdapterConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super String> itemWriterAdapterConfigurationItemWriter() {
        ItemWriterAdapter<String> writerAdapter = new ItemWriterAdapter<>();
        writerAdapter.setTargetObject(itemWriterAdapterConfigurationService()); // 커스텀 서비스의 write 사용
        writerAdapter.setTargetMethod("write");
        return writerAdapter;
    }

    @Bean
    public Object itemWriterAdapterConfigurationService() {
        return new ItemWriterAdapterConfigurationService();
    }
}
