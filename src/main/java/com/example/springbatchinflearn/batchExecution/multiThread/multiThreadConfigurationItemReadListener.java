package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import org.springframework.batch.core.ItemReadListener;

public class multiThreadConfigurationItemReadListener implements ItemReadListener<Customer2> {
    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Customer2 item) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", read item : " + item.getId());
    }

    @Override
    public void onReadError(Exception e) {

    }
}
