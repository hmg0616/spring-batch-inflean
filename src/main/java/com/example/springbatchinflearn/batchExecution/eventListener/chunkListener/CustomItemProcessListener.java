package com.example.springbatchinflearn.batchExecution.eventListener.chunkListener;

import org.springframework.batch.core.ItemProcessListener;

public class CustomItemProcessListener implements ItemProcessListener<Integer, String> {
    @Override
    public void beforeProcess(Integer o) {
        System.out.println(">> before Process");
    }

    @Override
    public void afterProcess(Integer o, String o2) {
        System.out.println(">> after Process");

    }

    @Override
    public void onProcessError(Integer o, Exception e) {
        System.out.println(">> on Process Error");

    }
}
