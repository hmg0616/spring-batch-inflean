package com.example.springbatchinflearn.finalExample.rowmapper;

import com.example.springbatchinflearn.finalExample.domain.ProductVO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ProductRowMapper implements RowMapper<ProductVO> {

    @Override
    public ProductVO mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return ProductVO.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .price(resultSet.getInt("price"))
                .type(resultSet.getString("type"))
                .build();
    }
}
