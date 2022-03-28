package com.example.springbatchinflearn.batchExecution.batchControl.retry;

public class RetryableException extends RuntimeException {

    public RetryableException() {
        super();
    }

    public RetryableException(String message) {
        super(message);
    }
}
