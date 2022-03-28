package com.example.springbatchinflearn.batchExecution.chunk.writer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private long id;
    private String name;
    private int age;
}
