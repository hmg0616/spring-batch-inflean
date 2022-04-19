package com.example.springbatchinflearn.finalExample.controller;

import com.example.springbatchinflearn.finalExample.domain.ApiInfo;
import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

// 강의에서는 새로운 프로젝트로 서버를 구성했지만, 편의상 나는 같은 서버에 진행함.
@RestController
public class ApiController {

    @PostMapping("/product/1")
    public String product1(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream().map(item -> item.getProductVO()).collect(Collectors.toList());
        System.out.println("productVoList = " + productVOList);

        return "product1 was successfully processed";
    }

    @PostMapping("/product/2")
    public String product2(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream().map(item -> item.getProductVO()).collect(Collectors.toList());
        System.out.println("productVoList = " + productVOList);

        return "product2 was successfully processed";
    }

    @PostMapping("/product/3")
    public String product3(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> productVOList = apiInfo.getApiRequestList().stream().map(item -> item.getProductVO()).collect(Collectors.toList());
        System.out.println("productVoList = " + productVOList);

        return "product3 was successfully processed";
    }
}
