package com.example.springbatchinflearn.finalExample.job.api;

import com.example.springbatchinflearn.batchExecution.chunk.processor.ProcessorClassifier;
import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import com.example.springbatchinflearn.finalExample.chunk.processor.ApiItemProcessor1;
import com.example.springbatchinflearn.finalExample.chunk.processor.ApiItemProcessor2;
import com.example.springbatchinflearn.finalExample.chunk.processor.ApiItemProcessor3;
import com.example.springbatchinflearn.finalExample.chunk.writer.ApiItemWriter1;
import com.example.springbatchinflearn.finalExample.chunk.writer.ApiItemWriter2;
import com.example.springbatchinflearn.finalExample.chunk.writer.ApiItemWriter3;
import com.example.springbatchinflearn.finalExample.classifier.ApiStepProcessorClassifier;
import com.example.springbatchinflearn.finalExample.classifier.ApiStepWriterClassifier;
import com.example.springbatchinflearn.finalExample.domain.ApiRequestVO;
import com.example.springbatchinflearn.finalExample.domain.Product;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import com.example.springbatchinflearn.finalExample.partition.ProductPartitioner;
import com.example.springbatchinflearn.finalExample.service.ApiService1;
import com.example.springbatchinflearn.finalExample.service.ApiService2;
import com.example.springbatchinflearn.finalExample.service.ApiService3;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final ApiService1 apiService1;
    private final ApiService2 apiService2;
    private final ApiService3 apiService3;

    private int chunkSize = 10;

    @Bean
    public Step apiMasterStep() throws Exception {
        return stepBuilderFactory.get("apiMasterStep")
                .partitioner(apiSlaveStep().getName(), productPartitioner())
                .step(apiSlaveStep())
                .gridSize(3) // 몇개의 파티션으로 나눌지 (스레드 수)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(6);
        taskExecutor.setThreadNamePrefix("api-thread-");
        return taskExecutor;
    }

    @Bean
    public Step apiSlaveStep() throws Exception {
        return stepBuilderFactory.get("apiSlaveStep")
                .<ProductVO, ProductVO>chunk(chunkSize)
                .reader(apiSlaveItemReader(null))
                .processor(apiSlaveItemProcessor())
                .writer(apiSlaveItemWriter())
                .build();
    }

    @Bean
    public ProductPartitioner productPartitioner() {
        ProductPartitioner productPartitioner = new ProductPartitioner();
        productPartitioner.setDataSource(dataSource);
        return productPartitioner;
    }

    @Bean
    @StepScope
    public ItemReader<ProductVO> apiSlaveItemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {

        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(chunkSize);
        reader.setRowMapper(new BeanPropertyRowMapper<>(ProductVO.class));

        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource);
        queryProviderFactoryBean.setSelectClause("id, name, price, type");
        queryProviderFactoryBean.setFromClause("from product");
        queryProviderFactoryBean.setWhereClause("where type = :type");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        queryProviderFactoryBean.setSortKeys(sortKeys);

//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("type", productVO.getType()); // 각 스레드(파티션) 마다 생성된 stepExecutionContext에서 가져온 객체 이용

        reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
        reader.setQueryProvider(queryProviderFactoryBean.getObject());
        reader.afterPropertiesSet();

        return reader;
    }

    @Bean
    public ItemProcessor apiSlaveItemProcessor() {
        // 읽어온 type 값에 따라 서로 다른 ItemProcessor를 반환하는 프로세서 (파티션마다 읽어온 type값 다름)
        ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> processor
                = new ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO>();

        ApiStepProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier
                = new ApiStepProcessorClassifier<>();
        Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();
        processorMap.put("1", new ApiItemProcessor1()); // type값에 따른 ItemProcessor 반환을 위해 (type값 : 1)
        processorMap.put("2", new ApiItemProcessor2()); // (type값 : 2)
        processorMap.put("3", new ApiItemProcessor3()); // (type값 : 3)
        classifier.setProcessorMap(processorMap);

        processor.setClassifier(classifier);

        return processor;
    }

    @Bean
    public ItemWriter apiSlaveItemWriter() {
        // 읽어온 type 값에 따라 서로 다른 ItemWriter를 반환하는 Writer (파티션마다 읽어온 type값 다름)
        ClassifierCompositeItemWriter<ApiRequestVO> processor
                = new ClassifierCompositeItemWriter<ApiRequestVO>();

        ApiStepWriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier
                = new ApiStepWriterClassifier<>();
        Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();
        writerMap.put("1", new ApiItemWriter1(apiService1)); // type값에 따른 ItemWriter 반환을 위해 (type값 : 1)
        writerMap.put("2", new ApiItemWriter2(apiService2)); // (type값 : 2)
        writerMap.put("3", new ApiItemWriter3(apiService3)); // (type값 : 3)
        classifier.setProcessorMap(writerMap);

        processor.setClassifier(classifier);

        return processor;
    }
}
