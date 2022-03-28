package com.example.springbatchinflearn.batchExecution.chunk.processor;

import org.springframework.batch.item.ItemProcessor;

public class CCICProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {
    @Override
    public ProcessorInfo process(ProcessorInfo processorInfo) throws Exception {
        System.out.println("CCICProcessor3");
        return null;
    }
}
