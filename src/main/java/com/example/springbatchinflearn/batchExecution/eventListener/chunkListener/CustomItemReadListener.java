package com.example.springbatchinflearn.batchExecution.eventListener.chunkListener;

import org.springframework.batch.core.ItemReadListener;

public class CustomItemReadListener implements ItemReadListener {
    @Override
    public void beforeRead() {
        System.out.println(">> before Read");
    }

    @Override
    public void afterRead(Object o) {
        System.out.println(">> after Read");

    }

    @Override
    public void onReadError(Exception e) {
        System.out.println(">> on Read Error");

    }
}
