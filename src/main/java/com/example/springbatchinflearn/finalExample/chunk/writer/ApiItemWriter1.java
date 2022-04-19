package com.example.springbatchinflearn.finalExample.chunk.writer;

import com.example.springbatchinflearn.finalExample.domain.ApiRequestVO;
import com.example.springbatchinflearn.finalExample.domain.ApiResponseVO;
import com.example.springbatchinflearn.finalExample.service.AbstractApiService;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

public class ApiItemWriter1 extends FlatFileItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    public ApiItemWriter1(AbstractApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        // Api 호출
        ApiResponseVO apiResponseVO = apiService.service(items);
        System.out.println("responseVO = " + apiResponseVO);

        // api 호출 결과 파일로 저장하기
        items.forEach(item -> item.setApiResponseVO(apiResponseVO));

        super.setResource(new FileSystemResource("C:\\Users\\mungy\\IdeaProjects\\spring-batch-inflearn\\src\\main\\resources\\finalExample\\product1.txt"));
        super.open(new ExecutionContext());
        super.setLineAggregator(new DelimitedLineAggregator<>());
        super.setAppendAllowed(true);
        super.write(items);
    }
}
