package com.example.springbatchinflearn.batchExecution.chunk.writer;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class XMLConfiguration2 {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job XMLConfiguration2Job() throws Exception {
        return jobBuilderFactory.get("XMLConfiguration2Job")
                .incrementer(new RunIdIncrementer())
                .start(XMLConfiguration2Step())
                .build();
    }

    @Bean
    public Step XMLConfiguration2Step() throws Exception {
        return stepBuilderFactory.get("XMLConfiguration2Step")
                .<Customer2, Customer2>chunk(10)
                .reader(XMLConfiguration2ItemReader())
                .writer(XMLConfiguration2ItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer2> XMLConfiguration2ItemWriter() {
        return new StaxEventItemWriterBuilder<Customer2>()
                .name("XMLConfiguration2ItemWriter")
                .marshaller(XMLConfiguration2ItemMarshaller())
                .resource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\chunk\\writer\\customer3.xml"))
                .rootTagName("customer")
                .build();

    }

    @Bean
    public Marshaller XMLConfiguration2ItemMarshaller() {
        Map<String, Class<?>> alias = new HashMap<>();
        alias.put("customer", Customer2.class);
        alias.put("id", Long.class);
        alias.put("firstname", String.class);
        alias.put("lastname", String.class);
        alias.put("birthdate", String.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(alias);

        return xStreamMarshaller;
    }

    // reader는 이전에 JdbcPagingConfiguration.java 에서 구현했던거 이용
    // customer 테이블에서 firstname이 R로 시작하는 데이터 select
    @Bean
    public ItemReader<? extends Customer2> XMLConfiguration2ItemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JdbcPagingItemReaderBuilder<Customer2>()
                .name("XMLConfiguration2ItemReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer2.class))
                .queryProvider(XMLConfiguration2PagingQueryProvider())
                .parameterValues(parameters)
                .build();

    }

    @Bean
    public PagingQueryProvider XMLConfiguration2PagingQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(dataSource);
        queryProviderFactoryBean.setSelectClause("id,firstname,lastname,birthdate");
        queryProviderFactoryBean.setFromClause("from customer");
        queryProviderFactoryBean.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProviderFactoryBean.setSortKeys(sortKeys);
        return queryProviderFactoryBean.getObject();
    }
}
