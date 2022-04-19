package com.example.springbatchinflearn.finalExample.chunk.processor;

import com.example.springbatchinflearn.finalExample.domain.ApiRequestVO;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class ApiItemProcessor3 implements ItemProcessor<ProductVO, ApiRequestVO> {

    @Override
    public ApiRequestVO process(ProductVO productVO) throws Exception {
        return ApiRequestVO.builder()
                .id(productVO.getId())
                .productVO(productVO)
                .build();
    }
}
