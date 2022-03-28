package com.example.springbatchinflearn.batchExecution.chunk.reader;

public class ItemReaderAdapterService<T> {

    private int cnt = 0;

    public T read() {
        return (T)("item" + cnt++);
    }
}
