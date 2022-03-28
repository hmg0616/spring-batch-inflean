package com.example.springbatchinflearn.batchExecution.batchControl.retry;

import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessor1 implements ItemProcessor<String, String> {

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {
        if("2".equals(item) || "3".equals(item)) {
            cnt++;
            System.out.println("ItemProcessor : " + item);
            throw new RetryableException("failed cnt : cnt");
        }
        return item;
    }
}
