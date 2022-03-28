package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ItemReaderAdapterConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job itemReaderAdapterJob() {
        return jobBuilderFactory.get("itemReaderAdapterJob")
                .incrementer(new RunIdIncrementer())
                .start(itemReaderAdapterStep())
                .build();
    }


    @Bean
    public Step itemReaderAdapterStep() {
        return stepBuilderFactory.get("itemReaderAdapterStep")
                .<String, String>chunk(10)
                .reader(itemReaderAdapterItemReader())
                .writer(itemReaderAdapterItemWriter())
                .build();
    }

    @Bean
    public ItemReader<String> itemReaderAdapterItemReader() {
        ItemReaderAdapter<String> readerAdapter = new ItemReaderAdapter<>();
        readerAdapter.setTargetObject(itemReaderAdapterService()); // 커스텀 서비스로 reader 역할 위임
        readerAdapter.setTargetMethod("read"); // 커스텀 서비스의 read() 메서드 호출
        return readerAdapter;
    }

    @Bean
    public Object itemReaderAdapterService() {
        return new ItemReaderAdapterService();
    }


    @Bean
    public ItemWriter<String> itemReaderAdapterItemWriter() {
        return items -> System.out.println(items);
    }
}
