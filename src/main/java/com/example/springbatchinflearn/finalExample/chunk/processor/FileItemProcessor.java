package com.example.springbatchinflearn.finalExample.chunk.processor;

import com.example.springbatchinflearn.finalExample.domain.Product;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {
    @Override
    public Product process(ProductVO productVO) throws Exception {
        // productVO -> Product 변환
        ModelMapper modelMapper = new ModelMapper();
        Product product = modelMapper.map(productVO, Product.class);
        return product;
    }
}
