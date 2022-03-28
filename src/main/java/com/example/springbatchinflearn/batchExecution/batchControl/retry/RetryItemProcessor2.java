package com.example.springbatchinflearn.batchExecution.batchControl.retry;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

public class RetryItemProcessor2 implements ItemProcessor<String, Customer> {

    @Autowired
    @Qualifier("retryConfiguration2RetryTemplate")
    private RetryTemplate retryTemplate;

    private int cnt = 0;

    @Override
    public Customer process(String item) throws Exception {

        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        Customer customer = retryTemplate.execute(
            new RetryCallback<Customer, RuntimeException>() {
                @Override
                public Customer doWithRetry(RetryContext retryContext) throws RuntimeException {

                    if("1".equals(item) || "2".equals(item)) {
                        cnt++;
                        System.out.println("ItemProcessor : " + item);
                        throw new RetryableException("failed cnt : " + cnt);
                    }

                    return new Customer(item);
                }
            },
            new RecoveryCallback<Customer>() {
                @Override
                public Customer recover(RetryContext retryContext) throws Exception {
                    return new Customer(item); // 더이상 retry 불가할 경우 default값 반환
                }
            }
//            , new DefaultRetryState(item, rollbackClassifier)
        );

        return customer;
    }
}
