package com.example.springbatchinflearn.finalExample.service;

import com.example.springbatchinflearn.finalExample.domain.ApiInfo;
import com.example.springbatchinflearn.finalExample.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService3 extends AbstractApiService {
    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {

        // 원래는 다른 서버에서 8083 포트로 호출 - 나는 편의상 같은 서버에서 진행
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8080/product/3", apiInfo, String.class);
        int statusCodeValue = responseEntity.getStatusCodeValue();
        ApiResponseVO apiResponseVO = ApiResponseVO.builder()
                .status(statusCodeValue)
                .msg(responseEntity.getBody())
                .build();

        return apiResponseVO;
    }
}
