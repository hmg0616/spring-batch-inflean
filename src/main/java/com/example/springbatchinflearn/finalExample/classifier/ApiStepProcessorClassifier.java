package com.example.springbatchinflearn.finalExample.classifier;

import com.example.springbatchinflearn.finalExample.domain.ApiRequestVO;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

public class ApiStepProcessorClassifier<C, T> implements Classifier<C,T> {

    private Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();

    // classifiable(read해온값) 값에 따라 일치하는 ItemProcessor 반환
    @Override
    public T classify(C classifiable) {
        return (T)processorMap.get(((ProductVO)classifiable).getType());
    }

    public void setProcessorMap(Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap) {
        this.processorMap = processorMap;
    }
}
