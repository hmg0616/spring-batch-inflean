package com.example.springbatchinflearn.batchExecution.chunk.writer;

public class ItemWriterAdapterConfigurationService<T> {

    public void write(T item) {
        System.out.println(item);
    }

}
