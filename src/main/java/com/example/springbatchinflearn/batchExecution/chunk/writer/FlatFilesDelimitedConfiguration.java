package com.example.springbatchinflearn.batchExecution.chunk.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFilesDelimitedConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFilesDelimitedJob() {
        return jobBuilderFactory.get("flatFilesDelimitedJob")
                .start(flatFilesDelimitedStep())
                .build();
    }

    @Bean
    public Step flatFilesDelimitedStep() {
        return stepBuilderFactory.get("flatFilesDelimitedStep")
                .<Customer, Customer>chunk(10)
                .reader(flatFilesDelimitedItemReader())
                .writer(flatFilesDelimitedItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFilesDelimitedItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFilesDelimitedItemWriter")
                .resource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\chunk\\writer\\customer.txt"))
                .append(true) // 기존 파일이 있다면 거기에 내용 추가
//                .shouldDeleteIfEmpty(true) // 기존 파일이 존재할때 write할 데이터가 없으면 기존 파일 삭제
//                .shouldDeleteIfExists(true) // 기존 파일 이미 존재 시 삭제
                .delimited() // DelimitedLineAggregator 사용
                .delimiter("|")
                .names(new String[]{"id","name","age"})
                .build();
    }


    @Bean
    public ItemReader<? extends Customer> flatFilesDelimitedItemReader() {

        List<Customer> customers = Arrays.asList(new Customer(1,"hong gil dong1",41),
                new Customer(2,"hong gil dong2",42),
                new Customer(3,"hong gil dong3",43));

        ListItemReader<Customer> reader = new ListItemReader<>(customers);
        return reader;
    }

}
