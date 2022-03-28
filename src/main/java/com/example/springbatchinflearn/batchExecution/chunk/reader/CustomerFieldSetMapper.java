package com.example.springbatchinflearn.batchExecution.chunk.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

// 스프링 배치에서 기본으로 존재하는 BeanWrapperFieldSetMapper 사용하여 대체 가능
// 예제에서는 직접 만들어봄
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {

        if(fieldSet == null) {
            return null;
        }

        Customer customer = new Customer();
//        customer.setName(fieldSet.readString(0));
//        customer.setAge(fieldSet.readInt(1));
//        customer.setYear(fieldSet.readString(2));
        
        // ItemReader 설정시 names 값 지정할 경우 FieldSet에서 인덱스가 아닌 필드명으로 가져올수 있음
        customer.setName(fieldSet.readString("name"));
        customer.setAge(fieldSet.readInt("age"));
        customer.setYear(fieldSet.readString("year"));

        return customer;
    }
}
