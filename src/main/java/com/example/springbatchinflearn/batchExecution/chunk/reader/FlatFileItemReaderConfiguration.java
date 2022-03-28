package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class FlatFileItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("flatFileItemReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFileItemReaderStep1())
                .next(flatFileItemReaderStep2())
                .build();
    }

    @Bean
    public Step flatFileItemReaderStep1() {
        return stepBuilderFactory.get("flatFileItemReaderStep1")
                .<String, String>chunk(5)
                .reader(flatFileItemReader2())
                .writer(new ItemWriter<Object>() {
                    @Override
                    public void write(List items) throws Exception {
                        System.out.println("items = " + items);
                    }
                })
                .build();

    }

    @Bean
    public ItemReader flatFileItemReader() {
        // DelimitedLineTokenizer 사용
        /*
        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new ClassPathResource("/chunk/reader/customer.csv"));

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenize(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());

        itemReader.setLineMapper(lineMapper);
        itemReader.setLinesToSkip(1);

        return itemReader;
        */

        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new ClassPathResource("/chunk/reader/customer.csv"))
//                .fieldSetMapper(new CustomerFieldSetMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>()) // 스프링배치가 기본으로 제공하는 FieldSetMapper
                .targetType(Customer.class) // BeanWrapperFieldSetMapper의 타켓 타입 지정.
                .linesToSkip(1) // 맨 위 라인 1줄 무시
                .delimited().delimiter(",") // 구분자 설정 (DelimitedLineTokenizer 사용)
                .names("name","age","year") // FieldSet에서 인덱스가 아닌 필드명으로 가져올수 있도록
                .build(); // LineMapper는 설정 따로 안하면 스프링배치가 기본으로 제공하는 DefaultLineMapper 사용
    }

    public FlatFileItemReader flatFileItemReader2() {
        // Fixedlengthtokenizer 사용
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile2")
                .resource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\chunk\\reader\\customer2.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength() // FixedLengthBuilder - Fixedlengthtokenizer 사용
//                .strict(false) // 토큰화 중 예외가 발생해도 에러가 발생하지 않도록 처리
                .addColumns(new Range(1,5))
                .addColumns(new Range(6,9))
                .addColumns(new Range(10,11))
                .names("name","year","age")
                .build();
    }

    @Bean
    public Step flatFileItemReaderStep2() {
        return stepBuilderFactory.get("flatFileItemReaderStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("flatFileItemReaderStep2 has executed");
                    return RepeatStatus.FINISHED;
                })
                .build();

    }
}
