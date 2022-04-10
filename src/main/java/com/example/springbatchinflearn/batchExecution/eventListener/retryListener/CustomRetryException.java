package com.example.springbatchinflearn.batchExecution.eventListener.retryListener;

public class CustomRetryException extends Exception {
    public CustomRetryException(String msg) {
        super(msg);
    }
}
