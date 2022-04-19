package com.example.springbatchinflearn.finalExample.partition;

import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import com.example.springbatchinflearn.finalExample.job.api.QueryGenerator;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import javax.management.Query;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ProductPartitioner implements Partitioner {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        // type distinct 해서 가져오기 - type 별로 파티션 나누기 위해
        ProductVO[] productArray = QueryGenerator.getProductList(dataSource);
        Map<String, ExecutionContext> result = new HashMap<>();

        int number = 0;
        // 이 테스트 케이스의 경우 gridSize가 productArray.length와 같음 (데이터에 type이 3개 뿐이라서)
        for(int i = 0; i < productArray.length; i++) {
            // 파티션 크기만큼 ExecutionContext 생성해서 저장 - 각 파티션에서 사용.
            ExecutionContext value = new ExecutionContext();
            result.put("partition" + number, value);
            value.put("product", productArray[i]);
            number++;
        }

        return result;
    }
}
