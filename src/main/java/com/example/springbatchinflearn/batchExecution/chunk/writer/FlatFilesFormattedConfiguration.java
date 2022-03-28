package com.example.springbatchinflearn.batchExecution.chunk.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFilesFormattedConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesFormattedJob() {
        return jobBuilderFactory.get("flatFilesFormattedJob")
                .start(flatFilesFormattedStep())
                .build();
    }

    @Bean
    public Step flatFilesFormattedStep() {
        return stepBuilderFactory.get("flatFilesFormattedStep")
                .<Customer, Customer>chunk(10)
                .reader(flatFilesFormattedItemReader())
                .writer(flatFilesFormattedItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFilesFormattedItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFilesFormattedItemWriter")
                .resource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\chunk\\writer\\customer2.txt"))
                .append(true) // 기존 파일이 있다면 거기에 내용 추가
                .formatted()
                .format("%-2d%-15s%-2d") // id를 위한 2칸, name을 위한 15칸, age를 위한 2칸
                .names(new String[]{"id","name","age"})
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> flatFilesFormattedItemReader() {

        List<Customer> customers = Arrays.asList(new Customer(1,"hong gil dong1",41),
                new Customer(2,"hong gil dong2",42),
                new Customer(3,"hong gil dong3",43));

        ListItemReader<Customer> reader = new ListItemReader<>(customers);
        return reader;
    }

}
