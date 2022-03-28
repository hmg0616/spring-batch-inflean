package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private int chunkSize = 5;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorConfigurationJob() {
        return jobBuilderFactory.get("jdbcCursorConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(jdbcCursorConfigurationStep())
                .build();
    }

    @Bean
    public Step jdbcCursorConfigurationStep() {
        return stepBuilderFactory.get("jdbcCursorConfigurationStep")
                .<Customer2, Customer2>chunk(chunkSize)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer2> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer2>()
                .name("jdbcCursorItemReader")
                .fetchSize(chunkSize)
                .sql("select id, firstname, lastname, birthdate from customer where firstname like ? order by lastname, firstname")
                .beanRowMapper(Customer2.class)
                .queryArguments("R%")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Customer2> jdbcCursorItemWriter() {
        return items -> {
            for(Customer2 item : items) {
                System.out.println(item.toString());
            }
        };
    }
}
