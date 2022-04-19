package com.example.springbatchinflearn.finalExample.job.api;

import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import com.example.springbatchinflearn.finalExample.rowmapper.ProductRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryGenerator {

    public static ProductVO[] getProductList(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<ProductVO> productVOList = jdbcTemplate.query("select type from product group by type", new ProductRowMapper() {
            // type 값만 필요해서 재 오버라이딩
            @Override
            public ProductVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                return ProductVO.builder()
                        .type(resultSet.getString("type"))
                        .build();
            }
        });
        return productVOList.toArray(new ProductVO[]{});
    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parameter, value); // 각 스레드(파티션) 마다 생성된 stepExecutionContext에서 가져온 객체 이용
        return parameters;
    }
}
