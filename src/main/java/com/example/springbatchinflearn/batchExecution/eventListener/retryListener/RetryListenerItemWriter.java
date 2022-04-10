package com.example.springbatchinflearn.batchExecution.eventListener.retryListener;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class RetryListenerItemWriter implements ItemWriter<String> {

    int count = 0;

    @Override
    public void write(List<? extends String> list) throws Exception {
        for(String item : list) {
            System.out.println("RetryListenerItemWriter > " + item);
            if(count < 2) {
                if (count % 2 == 0) {
                    count++;
                } else if(count % 2 == 1) {
                    count++;
                    throw new CustomRetryException("failed");
                }
            }
            System.out.println("write : " + item);
        }
    }
}
