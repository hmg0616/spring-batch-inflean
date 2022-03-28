package com.example.springbatchinflearn.batchExecution.chunk.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ClassifierCompositeItemConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job classifierCompositeItemConfigurationJob() throws Exception {
        return jobBuilderFactory.get("classifierCompositeItemConfigurationJob")
                .incrementer(new RunIdIncrementer())
                .start(classifierCompositeItemConfigurationStep())
                .build();
    }

    @Bean
    public Step classifierCompositeItemConfigurationStep() throws Exception {
        return stepBuilderFactory.get("classifierCompositeItemConfigurationStep")
                .<ProcessorInfo, ProcessorInfo>chunk(10)
                .reader(new ItemReader<ProcessorInfo>() {
                    int i = 0;
                    @Override
                    public ProcessorInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        ProcessorInfo processorInfo = ProcessorInfo.builder().id(i).build();
                        return i > 3 ? null : processorInfo;
                    }
                })
                .processor(classifierCompositeItemConfigurationProcessor())
                .writer(items -> System.out.println(items))
                .build();
    }

    @Bean
    public ItemProcessor<? super ProcessorInfo,? extends ProcessorInfo> classifierCompositeItemConfigurationProcessor() {

        Map<Integer, ItemProcessor<ProcessorInfo,ProcessorInfo>> processorMap = new HashMap<>();
        processorMap.put(1, new CCICProcessor1());
        processorMap.put(2, new CCICProcessor2());
        processorMap.put(3, new CCICProcessor3());

        ProcessorClassifier<ProcessorInfo, ItemProcessor<?,? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();
        classifier.setProcessorMap(processorMap);

        return new ClassifierCompositeItemProcessorBuilder<ProcessorInfo, ProcessorInfo>()
                .classifier(classifier)
                .build();

    }

}
