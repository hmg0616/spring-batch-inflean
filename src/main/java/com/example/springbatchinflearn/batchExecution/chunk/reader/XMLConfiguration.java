package com.example.springbatchinflearn.batchExecution.chunk.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class XMLConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job XMLConfigurationJob() {
        return jobBuilderFactory.get("XMLConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(XMLConfigurationStep1())
                .build();
    }

    @Bean
    public Step XMLConfigurationStep1() {
        return stepBuilderFactory.get("XMLConfigurationStep1")
                .<Customer, Customer>chunk(3)
                .reader(XMLConfigurationItemReader())
                .writer(XMLConfigurationItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> XMLConfigurationItemReader() {
        return new StaxEventItemReaderBuilder<Customer>() // XML
                .name("staxXml")
                .resource(new ClassPathResource("/chunk/reader/customer3.xml"))
                .addFragmentRootElements("customer")
                .unmarshaller(XMLConfigurationItemUnmarshaller())
                .build();
    }

    @Bean
    public Unmarshaller XMLConfigurationItemUnmarshaller() {
        Map<String, Class<?>> alias = new HashMap<>();
        alias.put("customer", Customer.class);
        alias.put("id", Long.class);
        alias.put("name", String.class);
        alias.put("age", Integer.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(alias);

        return xStreamMarshaller;
    }

    @Bean
    public ItemWriter<Customer> XMLConfigurationItemWriter() {
        return items -> {
            for(Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }


}
