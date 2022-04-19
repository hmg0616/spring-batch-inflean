package com.example.springbatchinflearn.finalExample.classifier;

import com.example.springbatchinflearn.finalExample.domain.ApiRequestVO;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

import java.util.HashMap;
import java.util.Map;

public class ApiStepWriterClassifier<C, T> implements Classifier<C,T> {

    private Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();

    // classifiable(read->process해온값) 값에 따라 일치하는 ItemWriter 반환
    @Override
    public T classify(C classifiable) {
        return (T)writerMap.get(((ApiRequestVO)classifiable).getProductVO().getType());
    }

    public void setProcessorMap(Map<String, ItemWriter<ApiRequestVO>> writerMap) {
        this.writerMap = writerMap;
    }
}
