package com.example.springbatchinflearn.batchExecution.chunk.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

public class ProcessorClassifier<C,T> implements Classifier<C,T> {

    private Map<Integer, ItemProcessor<ProcessorInfo,ProcessorInfo>> processorMap = new HashMap<>();

    // classifiable(read해온값) 값에 따라 일치하는 ItemProcessor 반환
    @Override
    public T classify(C classifiable) {
        return (T)processorMap.get(((ProcessorInfo)classifiable).getId());
    }

    public void setProcessorMap(Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap) {
        this.processorMap = processorMap;
    }
}
