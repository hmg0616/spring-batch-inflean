package com.example.springbatchinflearn.batchExecution.multiThread;

import com.example.springbatchinflearn.batchExecution.chunk.reader.Customer2;
import org.springframework.batch.core.ItemProcessListener;

public class multiThreadConfigurationItemProcessListener implements ItemProcessListener<Customer2,Customer2> {

    @Override
    public void beforeProcess(Customer2 item) {

    }

    @Override
    public void afterProcess(Customer2 item, Customer2 result) {
        System.out.println("Thread : " + Thread.currentThread().getName() + ", process item : " + item.getId());

    }

    @Override
    public void onProcessError(Customer2 item, Exception e) {

    }
}
