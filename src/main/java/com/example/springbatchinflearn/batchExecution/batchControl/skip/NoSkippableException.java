package com.example.springbatchinflearn.batchExecution.batchControl.skip;

public class NoSkippableException extends Exception {
    public NoSkippableException(String s) {
        super(s);
    }
}
