package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class multiThreadConfigurationItemWriteListener implements ItemWriteListener<Customer2> {

    @Override
    public void beforeWrite(List<? extends Customer2> items) {

    }

    @Override
    public void afterWrite(List<? extends Customer2> items) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", writes items : " + items.size());
    }

    @Override
    public void onWriteError(Exception e, List<? extends Customer2> items) {

    }
}
