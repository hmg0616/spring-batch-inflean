package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SynchronizedItemStreamReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job synchronizedItemStreamReaderJob() throws Exception {
        return jobBuilderFactory.get("synchronizedItemStreamReaderJob")
                .incrementer(new RunIdIncrementer())
                .start(synchronizedItemStreamReaderStep())
                .build();
    }

    @Bean
    public Step synchronizedItemStreamReaderStep() throws Exception {
        return stepBuilderFactory.get("synchronizedItemStreamReaderStep")
                .<Customer2,Customer2>chunk(2)
                .reader(synchronizedItemStreamReader_Reader())
                .listener(new ItemReadListener<Customer2>() {
                    @Override
                    public void beforeRead() {

                    }

                    @Override
                    public void afterRead(Customer2 item) {
                        System.out.println("Thread : " + Thread.currentThread().getName() + ", item.getId()" + item.getId());
                    }

                    @Override
                    public void onReadError(Exception e) {

                    }
                })
                .writer(synchronizedItemStreamReader_Writer())
                .taskExecutor(synchronizedItemStreamReaderTaskExecutor()) // 멀티스레드로 실행시키기
                .build();
    }

    @Bean
    public TaskExecutor synchronizedItemStreamReaderTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // 기본 스레드 생성
        taskExecutor.setMaxPoolSize(8); // 최대 스레드 생성 가능 개수
        taskExecutor.setThreadNamePrefix("async-thread");
        return taskExecutor;
    }

    @Bean
    public SynchronizedItemStreamReader<Customer2> synchronizedItemStreamReader_Reader() throws Exception {

        // MultiThread 작업 시 Reader는 스레드 안전해야함.
        // 스레드마다 중복된 데이터를 읽지 않음을 보장해야 하므로.
        // JdbcCursorItemReaderBuilder는 thread-safe하지 않음.
        JdbcCursorItemReader<Customer2> notSafetyReader =  new JdbcCursorItemReaderBuilder<Customer2>()
                .name("jdbcPagingItemReader")
                .fetchSize(2)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .sql("select id,firstname,lastname,birthdate from customer")
                .name("not-thread-safe-reader")
                .build();

        // thread-safe하게 변경
        return new SynchronizedItemStreamReaderBuilder<Customer2>()
                .delegate(notSafetyReader)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter synchronizedItemStreamReader_Writer() {
        return new JdbcBatchItemWriterBuilder<>()
                .dataSource(dataSource)
                .sql("insert into customer4 values (:id, :firstname, :lastname, :birthdate)")
                .beanMapped()
                .build();
    }
}
