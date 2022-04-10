package com.example.springbatchinflearn.batchExecution.eventListener.chunkListener;

import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class CustomItemWriteListener implements ItemWriteListener<String> {
    @Override
    public void beforeWrite(List<? extends String> list) {
        System.out.println(">> before Write");

    }

    @Override
    public void afterWrite(List<? extends String> list) {
        System.out.println(">> after Write");

    }

    @Override
    public void onWriteError(Exception e, List<? extends String> list) {
        System.out.println(">> on Write Error");

    }
}
