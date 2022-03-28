package com.example.springbatchinflearn.batchExecution.chunk.processor;

import org.springframework.batch.item.ItemProcessor;

public class CompositeItemConfigurationProcessor implements ItemProcessor<String,String> {

    int cnt = 0;

    @Override
    public String process(String item) throws Exception {
        cnt++;
        return item + cnt;
    }
}
