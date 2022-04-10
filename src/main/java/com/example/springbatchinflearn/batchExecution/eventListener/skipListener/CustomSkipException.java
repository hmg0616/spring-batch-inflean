package com.example.springbatchinflearn.batchExecution.eventListener.skipListener;

public class CustomSkipException extends Exception {
    public CustomSkipException() { super(); }
    public CustomSkipException(String message) {
        super(message);
    }
}
